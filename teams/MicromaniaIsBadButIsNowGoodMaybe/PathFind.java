package MicromaniaIsBadButIsNowGoodMaybe;

import java.util.*;
import battlecode.common.*;

public class PathFind {
	
	static RobotController rc = RobotPlayer.rc;	
	public static final int FRIENDLY_PASTR_PATH_NUM_1 = 0;
	public static final int FRIENDLY_PASTR_PATH_NUM_2 = 1;
	public static final int ENEMY_PASTR_PATH_NUM_1 = 2;
	public static final int ENEMY_PASTR_PATH_NUM_2 = 3;
	//public static final int RALLY_PATH_NUM = 3;
	
	public static final MapLocation ENEMY_HQ_LOCATION = rc.senseEnemyHQLocation();
	public static final MapLocation FRIENDLY_HQ_LOCATION = rc.senseHQLocation();

	// Does a bfs to create a path to a destination.
	// Bfs is started at the destination then progresses outward, broadcasting to a channel that represents 
	// a x,y coordinate the direction to travel to reach the destination.
	// What's broadcasted is a 5 digit number, the first digit is the direction, second 2 are the x coordinate of the
	// destination and the fourth and fifth are the y coordinate of the destination.
	// Destination coordinate is broadcasted so robots know if this is the correct data.
	public static void createPathTo(MapLocation destination, int pathNum) throws GameActionException {
		int destinationInt = MapData.locToInt(destination);
		rc.broadcast(Channels.PATH_LOCATION_CHANNEL[pathNum], destinationInt);
				
		int startX = destination.x;
		int startY = destination.y;

		int xOffsets[] = {0, 1,  0, -1, -1, -1,  1, 1};
		int yOffsets[] = {1, 0, -1,  0, -1,  1, -1, 1};

		boolean visited[][] = new boolean[MapData.WIDTH][MapData.HEIGHT];
		for (int x = 0; x < MapData.WIDTH; x++) {
			for (int y = 0; y < MapData.HEIGHT; y++) {
				visited[x][y] = false;
			}
		}
		
		Queue q = new Queue(2 * MapData.WIDTH * MapData.HEIGHT);
		
		q.enqueue(startX);
		q.enqueue(startY);
		q.enqueue(0);

		while (!q.isEmpty()) {
			if (rc.getType() == RobotType.HQ && rc.isActive()) {
				HQ.doYourThing();
			}
			int currentX = q.dequeue();
			int currentY = q.dequeue();
			int timeLeft = q.dequeue();
			
			MapLocation currentLocation = new MapLocation(currentX, currentY);

			if (timeLeft == 0) {
				for (int i = 0; i < 8; i++) {
					
					if (i == 0 || i == 4) {
						if (pathNum == ENEMY_PASTR_PATH_NUM_1 || pathNum == ENEMY_PASTR_PATH_NUM_2) {
							// check if path is still relevant. I.e. if the enemy pastr is still there.
							if(!isPathRelevant(destinationInt)) {
								return;
							}
						}
					}
					
					int nextX = currentX + xOffsets[i];
					int nextY = currentY + yOffsets[i];

					if (nextX > -1 && nextY > -1 && nextX < MapData.WIDTH && nextY < MapData.HEIGHT) {
						if (!visited[nextX][nextY] && MapData.map[nextX][nextY] != 2) {
							q.enqueue(nextX);
							q.enqueue(nextY);
							//q.enqueue((MapData.map[nextX][nextY] + 1) % 2);
							q.enqueue(0);
								
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

	public static MapLocation pathLocation(int pathNum) throws GameActionException {
		return MapData.intToLoc(rc.readBroadcast(Channels.PATH_LOCATION_CHANNEL[pathNum]));
	}
	
	public static int distanceSquaredToPathLocation(int pathNum) throws GameActionException {
		return rc.getLocation().distanceSquaredTo(pathLocation(pathNum));
	}
	
	// broadcasts a 1 to the appropriate channel, indicating the path is found.
	public static void setPathFound(int pathNum) throws GameActionException {
		rc.broadcast(Channels.PATH_FOUND_CHANNEL[pathNum], 1);
	}
	
	// broadcasts a -1 to the appropriate channel, indicting the path is not found.
	public static void setPathNotFound(int pathNum) throws GameActionException {
		rc.broadcast(Channels.PATH_FOUND_CHANNEL[pathNum], -1);
	}
	
	// returns if a path has been created.
	public static boolean isPathFound(int pathNum) throws GameActionException {
		return rc.readBroadcast(Channels.PATH_FOUND_CHANNEL[pathNum]) == 1;
	} 
	
	// returns if a path has been created up to a certain location.
	public static boolean isPathFound(MapLocation location, int pathNum) throws GameActionException {
		// this is the path data that is for this location, it may be old though.
		int locationPathData = rc.readBroadcast(getBroadcastChannelNum(location, pathNum));
		// this is the destination the path on this location thinks its going to, it may be old though.
		int locationDestination = locationPathData % 10000;
		// this is the destination the path is going to, this is never old.
		int pathDestination = rc.readBroadcast(Channels.PATH_LOCATION_CHANNEL[pathNum]);
		
		// return if the maybe old data is equal to the never old data. If it is then the path has been found up to this point.
		return locationDestination == pathDestination;
	} 	
	
	// this returns the direction the robot should go in for a given location.
	public static int getDirectionForPath(MapLocation location, int pathNum) throws GameActionException {
		int pathData = rc.readBroadcast(getBroadcastChannelNum(location, pathNum));
		return pathData / 10000;
	}
	
	public static boolean isPathRelevant(int locationData) {
		MapLocation pastrLocations[] = rc.sensePastrLocations(rc.getTeam().opponent());
		if (pastrLocations.length > 0) {
			return locationData == MapData.locToInt(pastrLocations[0]);
		} else {
			return false;
		}
	}
			
	public static int getBroadcastChannelNum(MapLocation location, int pathNum) throws GameActionException {
		return pathNum * 10000 + MapData.locToInt(location);
	}
	
	public static void initPathFindData() throws GameActionException {
		PathFind.setPathNotFound(0);
		PathFind.setPathNotFound(1);
		PathFind.setPathNotFound(2);
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
