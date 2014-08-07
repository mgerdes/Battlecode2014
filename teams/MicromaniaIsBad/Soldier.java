// 1. move to optimal pastrLocation.
// 2. wait until round 500 to build a pastr.
// 		2a. if while waiting enemy builds a pastr then send everyone but two to kill it. Other two create a pastr.
// 3. build a pastr after round 500.
// 4. defend pastr.
// 		4a. what to do if enemy build pastr? Maybe make decision basted on milk count.

package MicromaniaIsBad;

import java.util.*;

import battlecode.common.*;

public class Soldier {
	
	static RobotController rc = RobotPlayer.rc;
	
	public static void run() throws GameActionException {
		doYourThing();
		
		rc.setIndicatorString(0, "" + rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()));
		
		if (rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()) < RobotType.HQ.attackRadiusMaxSquared) {
			rc.setIndicatorString(1, "WE ARE IN THE SHIT");
			Movement.move(rc.getLocation().directionTo(rc.senseEnemyHQLocation()).opposite(), Movement.RUN);
		}
		
		if (Pastr.enemyPastrCount() > 0) {
			if (PathFind.distanceSquaredToPathLocation(PathFind.ENEMY_PASTR_PATH_NUM) < 100) {
				if (inGoodSituation()) {
					Movement.moveOnPath(PathFind.ENEMY_PASTR_PATH_NUM, Movement.RUN);
				}
			} else {
				Movement.moveOnPath(PathFind.ENEMY_PASTR_PATH_NUM, Movement.RUN);
			}
		} else {
			Movement.moveOnPath(PathFind.FRIENDLY_PASTR_PATH_NUM_1, Movement.RUN);
		}
	}
	
	public static void doYourThing() throws GameActionException {
		shoot();
	}
	
	// Checks to see if a robot is in a good situation.
	// Is true if the amount of allies is greater than the amount of foes.
	public static boolean inGoodSituation() throws GameActionException {
		Robot robots[] = rc.senseNearbyGameObjects(Robot.class, 50, rc.getTeam());
		int friendlyCount = 0;
		
		for (Robot robot : robots) {
			if (rc.senseRobotInfo(robot).type == RobotType.SOLDIER) {
				friendlyCount++;
			}
		}
		
		if (friendlyCount < 5) {
			return false;
		}
		
		return true;
	}
	
	public static void shoot() throws GameActionException {
		Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, RobotType.HQ.attackRadiusMaxSquared, rc.getTeam().opponent());		
		if (enemies.length > 0) {
			RobotInfo robotToAttack = rc.senseRobotInfo(enemies[(int)(Math.random() * enemies.length)]);
			MapLocation attackLocation = robotToAttack.location;
			if (rc.isActive() && robotToAttack.type != RobotType.HQ && rc.canAttackSquare(attackLocation)) {
				rc.attackSquare(attackLocation);
			} 
		}
	}

}
