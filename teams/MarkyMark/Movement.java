package MarkyMark;

import java.util.*;

import battlecode.common.*;
import MarkyMark.*;

public class Movement {
	
	static RobotController rc = RobotPlayer.rc;
	
	public static int[][] map;
	public static int width;
	public static int height;
	public static Direction[] directions = Direction.values();
	public static int SNEAK = 1;
	public static int RUN = 0;
	
	public static void move(Direction movingDirection, int type) throws GameActionException {
		if (tryToMove(movingDirection, type));
		else {
			moveRandomly(type);
		}
	}
	
	public static void move(MapLocation movingLocation) throws GameActionException {
		move(rc.getLocation().directionTo(movingLocation), 0);
	}
	
	public static boolean tryToMove(Direction movingDirection, int type) throws GameActionException {
		if (rc.isActive() && rc.canMove(movingDirection)) {
			if (type == 1) {
				rc.sneak(movingDirection);
			} else {
				rc.move(movingDirection);
			}
			return true;
		} else {
			return false;
		}
	}
	
	public static void moveRandomly(int type) throws GameActionException {
		Direction movingDirection = directions[(int)(Math.random() * 8)];
		tryToMove(movingDirection, type);
	}
	
	public static void moveWhereHQWants(int pathNum, int type) throws GameActionException {
		Direction movingDirection = directions[rc.readBroadcast(PathFind.getBroadcastChannelNum(rc.getLocation(), pathNum)) - 1];
		move(movingDirection, type);
	}
	
	public static void moveToFriendlyPastrLocation(int type) throws GameActionException {
		if (PathFind.isHQPathFound(rc.getLocation(), PathFind.FRIENDLY_PASTR_PATH_NUM)) {
			Movement.moveWhereHQWants(PathFind.FRIENDLY_PASTR_PATH_NUM, type);
		} else {
			Direction movingDirection = rc.getLocation().directionTo(Movement.intToLoc(rc.readBroadcast(PathFind.HQ_PATH_LOCATION_CHANNEL[PathFind.FRIENDLY_PASTR_PATH_NUM])));
			Movement.move(movingDirection, type);
		}
	}
	
	public static void moveToEnemyPastrLocation(int type) throws GameActionException {
		if (PathFind.isHQPathFound(rc.getLocation(), PathFind.ENEMY_PASTR_PATH_NUM)) {
			Movement.moveWhereHQWants(PathFind.ENEMY_PASTR_PATH_NUM, type);
		} else {
			Direction movingDirection = rc.getLocation().directionTo(Movement.intToLoc(rc.readBroadcast(PathFind.HQ_PATH_LOCATION_CHANNEL[PathFind.ENEMY_PASTR_PATH_NUM])));
			Movement.move(movingDirection, type);
		}
	}
		
	public static void createMap() {
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		map = new int[width][height];
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int tile = rc.senseTerrainTile(new MapLocation(x, y)).ordinal(); 
				map[x][y] = tile;
			}
		}
	}

	public static int locToInt(MapLocation location) {
		return location.x * 100 + location.y;
	}
	
	public static MapLocation intToLoc(int location) {
		return new MapLocation(location / 100, location % 100);
	}
	
}
