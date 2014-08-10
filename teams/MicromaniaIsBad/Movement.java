package MicromaniaIsBad;

import java.util.*;
import battlecode.common.*;

public class Movement {
	
	static RobotController rc = RobotPlayer.rc;
	
	public static int SNEAK = 1;
	public static int RUN = 0;
	
	public static void move(Direction movingDirection, int type) throws GameActionException {
		if (tryToMove(movingDirection, type));
		else {
			moveRandomly(type);
		}
	}
	
	public static void move(MapLocation movingLocation, int type) throws GameActionException {
		move(rc.getLocation().directionTo(movingLocation), type);
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
		Direction movingDirection = MapData.directions[(int)(Math.random() * 8)];
		tryToMove(movingDirection, type);
	}
	
	public static void moveOnPath(int pathNum, int type) throws GameActionException {
		MapLocation myLocation = rc.getLocation();
		if (PathFind.isPathFound(myLocation, pathNum)) {
			move(MapData.directions[PathFind.getDirectionForPath(myLocation, pathNum)], type);
		} else {
			move(MapData.intToLoc(rc.readBroadcast(Channels.PATH_LOCATION_CHANNEL[pathNum])), type);
		}
	}
	
}
