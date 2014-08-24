package MicromaniaIsBadButIsNowGoodMaybe;

import java.util.*;
import battlecode.common.*;

public class Movement {
	
	static RobotController rc = RobotPlayer.rc;
	
	public static int SNEAK = 1;
	public static int RUN = 0;

	public static boolean inBug = false;
	public static Direction bugWallDir = null;
	public static Direction bugMovingDir = null;
	public static MapLocation bugDestination = null;
	public static double bugStartDist = 0;

	public static void move(Direction movingDirection, int type) throws GameActionException {
		if (rc.isActive()) {
			if (tryToMove(movingDirection, type));
			else if (tryToMove(movingDirection.rotateLeft(), type));
			else if (tryToMove(movingDirection.rotateRight(), type));
			else if (tryToMove(movingDirection.rotateLeft().rotateLeft(), type));
			else if (tryToMove(movingDirection.rotateRight().rotateRight(), type));
			else {
				moveRandomly(type);
			}
		}
	}

	public static void move(MapLocation movingLocation, int type) throws GameActionException {

		if (rc.isActive()) {

			if (inBug && bugMovingDir == rc.getLocation().directionTo(movingLocation) && rc.getLocation().distanceSquaredTo(movingLocation) < bugStartDist) {
				inBug = false;
			}

			if (inBug && movingLocation.x != bugDestination.x && movingLocation.y != bugDestination.y) {
				inBug = false;
			}

			if (inBug) {

				if (rc.canMove(bugWallDir)) {
					Direction temp = bugMovingDir;
					bugMovingDir = bugWallDir;
					bugWallDir = temp.opposite();

					tryToMove(bugMovingDir, type);
				} else {
					if(tryToMove(bugMovingDir, type));
					else {
						Direction temp = bugMovingDir;
						bugMovingDir = bugMovingDir.rotateLeft().rotateLeft();
						bugWallDir = temp;
						
						tryToMove(bugMovingDir, type);
					}
				}

			} else {

				Direction movingDirection = rc.getLocation().directionTo(movingLocation);

				if (tryToMove(movingDirection, type));
				else {
					inBug = true;
					bugDestination = movingLocation;
					bugStartDist = rc.getLocation().distanceSquaredTo(movingLocation);
					
					if (!movingDirection.isDiagonal()) {
						bugWallDir = movingDirection;
						bugMovingDir = movingDirection.rotateLeft().rotateLeft();
					} else if (!rc.canMove(movingDirection.rotateLeft()) && !rc.canMove(movingDirection.rotateRight())) {
						bugWallDir = movingDirection.rotateRight();
						bugMovingDir = bugWallDir.rotateRight().rotateRight();
					} else {
						bugWallDir = movingDirection.rotateRight();
						bugMovingDir = bugWallDir.rotateRight().rotateRight();
					}
				}

			}

		}

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
