package testing;

import battlecode.common.*;

import java.util.*;

public class RobotPlayer {
	
	static RobotController rc;
	static Direction allDirections[] = Direction.values();
	static Random rand = new Random();
	
	public static void run (RobotController rcin) {
		rc = rcin;
		rand.setSeed(rc.getRobot().getID());
		
		try {
			rc.broadcast(1, 1);
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		
		while (true) {
			try {
				if (rc.getType() == RobotType.HQ){
					runHQ();		
				} else if (rc.getType() == RobotType.SOLDIER){
					runSoldier();
				}
				rc.yield();
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void runSoldier() throws GameActionException {
		if (shoot());
		else {
			if (rc.senseRobotCount() > 0) {
				MapLocation averagePosition = getAveragePosition();
				if (Clock.getRoundNum() < 500) {
					move(mldivide(mladd(rc.senseEnemyHQLocation(), rc.senseHQLocation()), 2));
				} else if (Clock.getRoundNum() < 506) {
					if ((int)(rand.nextDouble() * 8) == 2) {
						move(rc.senseEnemyHQLocation());
					}
				} else {
					move(rc.senseEnemyHQLocation());
				}
			} 
		}
		
	}
	
	public static MapLocation getAveragePosition() throws GameActionException {
		int editingChannel = Clock.getRoundNum() % 2;
		int usingChannel = (Clock.getRoundNum() + 1) % 2;
		
		int runningCount = rc.readBroadcast(editingChannel) + 1;
		rc.broadcast(editingChannel, runningCount);
		
		MapLocation runningVectorTotal = mladd(intToLoc(rc.readBroadcast(editingChannel + 2)), rc.getLocation());
		rc.broadcast(editingChannel + 2, locToInt(runningVectorTotal));
				
		if (rc.readBroadcast(usingChannel) > 0) {
			MapLocation averagePosition = mldivide(intToLoc(rc.readBroadcast(usingChannel + 2)), rc.readBroadcast(usingChannel));
			rc.setIndicatorString(0, "" + locToInt(averagePosition));
			return averagePosition;
		}	
		return null;
	}
	
	public static MapLocation mladd(MapLocation m1, MapLocation m2) {
		return new MapLocation(m1.x + m2.x, m1.y + m2.y);
	}
	
	public static MapLocation mldivide(MapLocation bigM, int divisor) {
		return new MapLocation(bigM.x / divisor, bigM.y / divisor);
	}
	
	public static int locToInt(MapLocation loc) {
		return loc.x * 100 + loc.y;		
	}
	
	public static MapLocation intToLoc(int loc) {
		return new MapLocation(loc / 100, loc % 100);
	}
	
	public static boolean shoot() throws GameActionException {
		if (rc.isActive()) {
			Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class, 10000, rc.getTeam().opponent());
			if (enemyRobots.length > 0) {
				Robot anEnemy = enemyRobots[(int)(rand.nextDouble() * enemyRobots.length)];
				RobotInfo anEnemyInfo = rc.senseRobotInfo(anEnemy);
				if (anEnemyInfo.location.distanceSquaredTo(rc.getLocation()) < rc.getType().attackRadiusMaxSquared) {
					rc.attackSquare(anEnemyInfo.location);
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean buildPasture() throws GameActionException {
		if (rc.isActive()) {
			if (rand.nextDouble()<0.001 && rc.sensePastrLocations(rc.getTeam()).length < 5) {
				rc.construct(RobotType.PASTR);
				return true;
			}
		}
		return false;
	}
	
	public static void move(MapLocation position) throws GameActionException {
		if (!rc.isActive()) {
			return;
		}
		if (rand.nextDouble() < 1) {
			Direction movingDirection = rc.getLocation().directionTo(position);
			if (rc.canMove(movingDirection)) {
				rc.move(movingDirection);
			} else {
				moveNear(position);
			}
		} else {
			Direction movingDirection = allDirections[(int)(rand.nextDouble() * 8)];
			if (rc.canMove(movingDirection)) {
				rc.move(movingDirection);
			}
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
	
	public static void runHQ() throws GameActionException {
		Direction spawnDir = Direction.NORTH;
		
		if (rc.isActive() && rc.canMove(spawnDir) && rc.senseRobotCount() < GameConstants.MAX_ROBOTS) {
			rc.spawn(spawnDir);
		}
		
		int editingChannel = (Clock.getRoundNum()%2);

		rc.broadcast(editingChannel, 0);
		rc.broadcast(editingChannel + 2, 0);
	}
	
}
