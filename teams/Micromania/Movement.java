package Micromania;

import java.util.*;

import battlecode.common.*;

public class Movement {
	
	public static RobotController rc = RobotPlayer.rc;
	
	public static final int RUN = 0;
	public static final int SNEAK = 1;
	
	public static int enemyPastrPathCurrentlyFollowing = -1;
	
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
	
	public static void moveOnPathNum(int pathNum, int type) throws GameActionException {
		int pathData = rc.readBroadcast(PathFind.getBroadcastChannelNum(rc.getLocation(), pathNum));
		
		int direction = pathData / 10000;
		int destinationIntMaybe = (pathData % 10000);
		
		int pathDestinationIntForSure = rc.readBroadcast(Channels.HQ_PATH_LOCATION_CHANNEL[pathNum]);
		rc.setIndicatorString(0, "For sure = " + pathDestinationIntForSure);
		rc.setIndicatorString(1, "Maybe = " + destinationIntMaybe);
		
		if (pathDestinationIntForSure == destinationIntMaybe) {
			move(MapData.directions[direction], type);
		} else {
			move(MapData.intToLoc(pathDestinationIntForSure), 0);
		}
	}
	
	public static void moveToEnemyPastrLocation() throws GameActionException {
		if (enemyPastrPathCurrentlyFollowing == -1) {
			enemyPastrPathCurrentlyFollowing = PathFind.enemyPastrPathNum();
		}
				
		if (enemyPastrPathCurrentlyFollowing == -1) {
			moveRandomly(0);
			return;
		}
		
		rc.setIndicatorString(2, "Path num = " + enemyPastrPathCurrentlyFollowing);
		
		//rc.setIndicatorString(2, "Path = " + enemyPastrPathCurrentlyFollowing);
				
		
		
		//if (PathFind.isPathCreatedTo(MapData.intToLoc(rc.readBroadcast(Channels.HQ_PATH_LOCATION_CHANNEL[enemyPastrPathCurrentlyFollowing]))) != -1) {
		if (PathFind.doesPathGoToEnemyPastr(enemyPastrPathCurrentlyFollowing)) {
			moveOnPathNum(enemyPastrPathCurrentlyFollowing, 0);
		} else {
			enemyPastrPathCurrentlyFollowing = PathFind.enemyPastrPathNum();
		}
	}

}
