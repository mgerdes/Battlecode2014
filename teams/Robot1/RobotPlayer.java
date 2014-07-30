package Robot1;

import battlecode.common.*;

import java.util.*;

public class RobotPlayer {
	
	private static RobotController rc;
	private static MapLocation rallyPoint;
	
	private static Random rand = new Random();
	
	private static int position = -1;
	private static int[] offsets = {-10, 0, -9, 1, -8, 2, -7, 3, -6, 4, -5, 5, -4, 6, -3, 7, -2, 8, -1, 9, 0, 10};
	
	public static void run(RobotController myRC) {
		
		rc = myRC;
		rallyPoint = findRallyPoint();
		rand.setSeed(rc.getRobot().getID());
				
		while (true) {	
			try {
				if (rc.getType() == RobotType.SOLDIER) {
					// Soldier code
					/*	
					int dist = rc.getLocation().distanceSquaredTo(rallyPoint);
					if (dist > 0 && rc.isActive()) {					
						Direction dir = rc.getLocation().directionTo(rallyPoint);
						int[] directionOffsets = {0,1,-1,2,-2};
						
						Direction lookingAtCurrently = dir;
						lookAround: for (int d : directionOffsets) {
							lookingAtCurrently = Direction.values()[(dir.ordinal() + d + 8) % 8];
							if (rc.canMove(lookingAtCurrently)) {
								break lookAround;
							}
						}
						
						if (rc.canMove(dir)) {
							rc.move(dir);
						}
					}
					*/
					if (shoot()); 
					else 
						moveDefense();
					
				} else {
					// Head quarters code
					hqCode();
				}

				rc.yield();
			} catch (Exception e) {
				System.out.println("Exception: ):");
				e.printStackTrace();
			}
		}
		
	} 
	
	public static void moveDefense() throws GameActionException {
		MapLocation hqloc = rc.senseHQLocation();
		
		if (position == -1) {
			position = (int)(rand.nextDouble() * 10);
		}
		
		int destX = rc.senseHQLocation().x + offsets[position * 2];
		int destY = rc.senseHQLocation().y + offsets[(position * 2) + 1];
		
		MapLocation positionTo = new MapLocation(destX, destY);
		
		move(positionTo);
	}
	
	public static boolean shoot() throws GameActionException {
		if (rc.isActive()) {
			Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class, rc.getType().attackRadiusMaxSquared + 10, rc.getTeam().opponent());
			if (enemyRobots.length > 0) {
				Robot anEnemy = enemyRobots[(int)(rand.nextDouble() * enemyRobots.length)];
				RobotInfo anEnemyInfo = rc.senseRobotInfo(anEnemy);
				if (anEnemyInfo.location.distanceSquaredTo(rc.getLocation()) < rc.getType().attackRadiusMaxSquared) {
					rc.attackSquare(anEnemyInfo.location);
					return true;
				} else {
					move(anEnemyInfo.location);
					return true;
				}
			}
		}
		return false;
	}
	
	public static void move(MapLocation location) throws GameActionException {
		if (!rc.isActive()) {
			return;
		}
		
		Direction movingDirection = rc.getLocation().directionTo(location);
		if (rc.canMove(movingDirection)) {
			rc.move(movingDirection);
		} else {
			moveNear(location);
		}		
	}
	
	public static void moveNear(MapLocation position) throws GameActionException {
		if (!rc.isActive()) {
			return;
		}
		
		Direction movingDirection =  rc.getLocation().directionTo(position);
		
		if (rc.canMove(movingDirection.rotateLeft())) {
			rc.move(movingDirection.rotateLeft());
			return;
		}
		if (rc.canMove(movingDirection.rotateRight())) {
			rc.move(movingDirection.rotateRight());
			return;
		}		
	}
	
	private static MapLocation findRallyPoint() {
		MapLocation enemyLoc = rc.senseEnemyHQLocation();
		MapLocation ourLoc= rc.senseHQLocation();
		
		int x = (enemyLoc.x + 3 * ourLoc.x) / 4; 
		int y = (enemyLoc.y + 3 * ourLoc.y) / 4; 
		
		MapLocation rallyPoint = new MapLocation(x,y);
		return rallyPoint;
	}

	public static void hqCode() throws GameActionException {
		if (rc.isActive()) {
			// Spawn a soldier.
			Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
			if (rc.canMove(dir) && rc.senseRobotCount() < GameConstants.MAX_ROBOTS) {
				rc.spawn(dir);
			}
			
		}
	}

}
