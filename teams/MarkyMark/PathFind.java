package MarkyMark;

import java.util.*;

import MarkyMark.*;
import battlecode.common.*;

public class PathFind {
	
	static RobotController rc = RobotPlayer.rc;
	public static final int HQ_PATH_CHANNEL[] = {60000, 60001, 60002, 60003};
	public static final int HQ_PATH_LOCATION_CHANNEL[] = {60004, 60005, 60006, 60007};
	
	public static final int FRIENDLY_PASTR_PATH_NUM = 0;
	public static final int ENEMY_PASTR_PATH_NUM = 1;
	public static final int ENEMY_PASTR_ID_CHANNEL = 60008;
	
	public static final MapLocation ENEMY_HQ_LOCATION = rc.senseEnemyHQLocation();
	public static final MapLocation FRIENDLY_HQ_LOCATION = rc.senseHQLocation();
	
	public static int ENEMY_HQ_LOCATION_X = ENEMY_HQ_LOCATION.x;
	public static int ENEMY_HQ_LOCATION_Y = ENEMY_HQ_LOCATION.y;
	public static int FRIENDLY_HQ_LOCATION_X = FRIENDLY_HQ_LOCATION.x;
	public static int FRIENDLY_HQ_LOCATION_Y = FRIENDLY_HQ_LOCATION.y;
	
	public static void findHQLocationTo(MapLocation location, int pathNum) throws GameActionException {
		rc.broadcast(HQ_PATH_LOCATION_CHANNEL[pathNum], Movement.locToInt(location));
		
		int startX = location.x;
		int startY = location.y;

		int xOffsets[] = {0, 1,  0, -1, -1, -1,  1, 1};
		int yOffsets[] = {1, 0, -1,  0, -1,  1, -1, 1};

		boolean visited[][] = new boolean[Movement.width][Movement.height];
		for (int x = 0; x < Movement.width; x++) {
			for (int y = 0; y < Movement.height; y++) {
				visited[x][y] = false;
			}
		}
		
		Queue q = new Queue(2 * Movement.width * Movement.height);
		
		q.enqueue(startX);
		q.enqueue(startY);
		q.enqueue(0);

		while (!q.isEmpty()) {
			if (rc.isActive()) {
				HQ.doYourThing();
			}
			
			int currentX = q.dequeue();
			int currentY = q.dequeue();
			int timeLeft = q.dequeue();
			
			MapLocation currentLocation = new MapLocation(currentX, currentY);

			if (timeLeft == 0) {
				for (int i = 0; i < 8; i++) {
					int nextX = currentX + xOffsets[i];
					int nextY = currentY + yOffsets[i];

					if (nextX > -1 && nextY > -1 && nextX < Movement.width && nextY < Movement.height) {
						if (!visited[nextX][nextY] && Movement.map[nextX][nextY] != 2) {
							q.enqueue(nextX);
							q.enqueue(nextY);
							q.enqueue((Movement.map[nextX][nextY] + 1) % 2);
								
							visited[nextX][nextY] = true;
							
							MapLocation nextLocation = new MapLocation(nextX, nextY);
							rc.broadcast(getBroadcastChannelNum(nextLocation, pathNum), nextLocation.directionTo(currentLocation).ordinal() + 1);
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
	
	public static int distanceSquaredToPathLocation(int pathNum) throws GameActionException {
		return rc.getLocation().distanceSquaredTo(Movement.intToLoc(rc.readBroadcast(PathFind.HQ_PATH_LOCATION_CHANNEL[PathFind.FRIENDLY_PASTR_PATH_NUM])));
	}
		
	public static void setHQPathNotFound(int pathNum) throws GameActionException {
		rc.broadcast(HQ_PATH_CHANNEL[pathNum], -1);
	}

	public static void setHQPathFound(int pathNum) throws GameActionException {
		rc.broadcast(HQ_PATH_CHANNEL[pathNum], 1);
	}

	public static boolean isHQPathFound(int pathNum) throws GameActionException {
		return rc.readBroadcast(HQ_PATH_CHANNEL[pathNum]) == 1;
	} 
	
	public static boolean isHQPathFound(MapLocation location, int pathNum) throws GameActionException {
		return rc.readBroadcast(getBroadcastChannelNum(location, pathNum)) != 0;
	} 
	
	public static int getBroadcastChannelNum(MapLocation location, int pathNum) throws GameActionException {
		return pathNum * 10000 + Movement.locToInt(location);
	}
	
	public static void printDirectionalField() throws GameActionException {
		for (int x = 0; x < Movement.width; x++) {
			for (int y = 0; y < Movement.height; y++) {
				System.out.print(rc.readBroadcast(Movement.locToInt(new MapLocation(x,y))));
			}
			System.out.println();
		}
	}
	
}
