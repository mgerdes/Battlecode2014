package Micromania;

import java.util.*;

import battlecode.common.*;

public class PathFind {
	
	static RobotController rc = RobotPlayer.rc;	

	public static final int FRIENDLY_PASTR_PATH_NUM_1 = 0;
	public static final int FRIENDLY_PASTR_PATH_NUM_2 = 1;
	public static final int ENEMY_PASTR_PATH_NUM_1 = 2;
	public static final int ENEMY_PASTR_PATH_NUM_2 = 3;
	
	public static final MapLocation FRIENDLY_HQ_LOCATION = rc.senseHQLocation();
	public static final MapLocation ENEMY_HQ_LOCATION = rc.senseEnemyHQLocation();
		
	// Does a bfs to create a path to a destination.
	// Bfs is started at the destination then progresses outward, broadcasting to a channel that represents 
	// a x,y coordinate the direction to travel to reach the destination.
	// What's broadcasted is a 5 digit number, the first digit is the direction, second 2 are the x coordinate of the
	// destination and the fourth and fifth are the y coordinate of the destination.
	// Destination coordinate is broadcasted so robots know if this is the correct data.
	public static void createPathTo(MapLocation destination, int pathNum) throws GameActionException {
		int destinationInt = MapData.locToInt(destination);
		rc.broadcast(Channels.HQ_PATH_LOCATION_CHANNEL[pathNum], destinationInt);
						
		int startX = destination.x;
		int startY = destination.y;

		int xOffsets[] = {0, 1,  0, -1, -1, -1,  1, 1};
		int yOffsets[] = {1, 0, -1,  0, -1,  1, -1, 1};

		boolean visited[][] = new boolean[MapData.WIDTH][MapData.HEIGHT];

		Queue q = new Queue(3 * MapData.WIDTH * MapData.HEIGHT);
		
		q.enqueue(startX);
		q.enqueue(startY);
		q.enqueue(0);

		while (!q.isEmpty()) {
			int currentX = q.dequeue();
			int currentY = q.dequeue();
			int timeLeft = q.dequeue();
			
			MapLocation currentLocation = new MapLocation(currentX, currentY);

			if (timeLeft == 0) {
				for (int i = 0; i < 8; i++) {
					if (i == 0) {
						HQ.spawn();
					}
					if (i == 4) {
						HQ.shoot();
					}
					
					int nextX = currentX + xOffsets[i];
					int nextY = currentY + yOffsets[i];

					if (nextX > -1 && nextY > -1 && nextX < MapData.WIDTH && nextY < MapData.HEIGHT) {
						if (!visited[nextX][nextY] && MapData.map[nextX][nextY] != 2) {
							q.enqueue(nextX);
							q.enqueue(nextY);
							q.enqueue((MapData.map[nextX][nextY] + 1) % 2);
								
							visited[nextX][nextY] = true;
							
							MapLocation nextLocation = new MapLocation(nextX, nextY);
							
							// This will evaluate to a 5 digit number, DXXYY, D is direction, XX and YY are x,y coordinates.
							int whatToBroadcast = nextLocation.directionTo(currentLocation).ordinal() * 10000 + destinationInt;
							rc.broadcast(getBroadcastChannelNum(nextLocation, pathNum), whatToBroadcast);
						}
					}
				}
			} else {
				q.enqueue(currentX);
				q.enqueue(currentY);
				q.enqueue(timeLeft - 1);
			}
		}
	}
	
	public static int getDestinationInt(int number) throws GameActionException {
		return number % 10000;
	}
	
	// This returns a path num that is free for a path to an enemy pastr.
	// Will search through each enemy pastr location and check if this location has a path toward it.
	// If it does one of the two booleans will be set to false, if both are set to false then there are no
	// paths free, a -1 will be returned if this is the case.
	public static int freeEnemyPastrPathNum() throws GameActionException {
		boolean pathNumOneFree = true;
		boolean pathNumTwoFree = true;
		
		for (MapLocation pastrLocation : Pastr.enemyPastrLocations) {
			if (MapData.locToInt(pastrLocation) == rc.readBroadcast(Channels.HQ_PATH_LOCATION_CHANNEL[ENEMY_PASTR_PATH_NUM_1])) {
				pathNumOneFree = false;
			} else if (MapData.locToInt(pastrLocation) == rc.readBroadcast(Channels.HQ_PATH_LOCATION_CHANNEL[ENEMY_PASTR_PATH_NUM_2])) {
				pathNumTwoFree = false;
			}
		}
		
		if (pathNumOneFree) {
			return ENEMY_PASTR_PATH_NUM_1;
		} else if (pathNumTwoFree) {
			return ENEMY_PASTR_PATH_NUM_2;
		} else {
			return -1; // there is no free path num.
		}
	}
	
	// this returns whether or not a path has been created to a certain location.
	// It returns the path num if a path is created, or a -1 if no path is created.
	public static int isPathCreatedTo(MapLocation location) throws GameActionException {
		int locationInt = MapData.locToInt(location);
		for (int i = 0; i < 4; i++) {
			if (locationInt == rc.readBroadcast(Channels.HQ_PATH_LOCATION_CHANNEL[i])) {
				return i;
			}
		}
		return -1;
	}
	
	public static boolean doesPathGoToEnemyPastr(int pathNum) throws GameActionException {
		Pastr.updateEnemyPastrLocations();
		
		for (MapLocation pastrLocation : Pastr.enemyPastrLocations) {
			if (MapData.locToInt(pastrLocation) == rc.readBroadcast(Channels.HQ_PATH_LOCATION_CHANNEL[pathNum])) {
				return true;
			}
		}
		
		return false;
	}
	
	// this returns the pathnum to an enemy pastr.
	// it returns a -1 if there isn't one.
	public static int enemyPastrPathNum() throws GameActionException {
		Pastr.updateEnemyPastrLocations();
		
		for (MapLocation pastrLocation : Pastr.enemyPastrLocations) {
			int pathNum = isPathCreatedTo(pastrLocation);
			if (pathNum != -1) {
				return pathNum;
			}
		}
		
		return -1;
	}
	
	public static int distanceSquaredToPathLocation(int pathNum) throws GameActionException {
		return rc.getLocation().distanceSquaredTo(MapData.intToLoc(rc.readBroadcast(Channels.HQ_PATH_LOCATION_CHANNEL[pathNum])));
	}
			
	public static int getBroadcastChannelNum(MapLocation location, int pathNum) throws GameActionException {
		return pathNum * 10000 + MapData.locToInt(location);
	}
	
	public static void printDirectionalField() throws GameActionException {
		for (int x = 0; x < MapData.WIDTH; x++) {
			for (int y = 0; y < MapData.HEIGHT; y++) {
				System.out.print(rc.readBroadcast(MapData.locToInt(new MapLocation(x,y))));
			}
			System.out.println();
		}
	}
	
}
