// 1. move to optimal pastrLocation.
// 2. wait until round 500 to build a pastr.
// 		2a. if while waiting enemy builds a pastr then send everyone but two to kill it. Other two create a pastr.
// 3. build a pastr after round 500.
// 4. defend pastr.
// 		4a. what to do if enemy build pastr? Maybe make decision basted on milk count.


package MarkyMark;

import java.util.*;

import battlecode.common.*;
import MarkyMark.*;

public class Soldier {
	
	static RobotController rc = RobotPlayer.rc;
	
	public static void run() throws GameActionException {
		doYourThing();
		
		int startingByteCodes = Clock.getBytecodeNum();
		
		Pastr.isPastrBuilt();
		NoiseTower.isTowerBuilt();
		
		rc.setIndicatorString(0, "" + (Clock.getBytecodeNum() - startingByteCodes));
						
		if (Pastr.enemyPastrCount() > 0) {
			Movement.moveToEnemyPastrLocation(Movement.RUN);
		} else {
			if (Clock.getRoundNum() < 300) {
				Movement.moveToFriendlyPastrLocation(Movement.RUN);
				// destroy and build own pastr if enemy builds one.
			} else {			
				if (!Pastr.isPastrBuilt() && PathFind.distanceSquaredToPathLocation(PathFind.FRIENDLY_PASTR_PATH_NUM) < 2 && inGoodSituation()) {
					Pastr.buildPastr();
				} else if (!NoiseTower.isTowerBuilt() && PathFind.distanceSquaredToPathLocation(PathFind.FRIENDLY_PASTR_PATH_NUM) < 2 && inGoodSituation()) {
					NoiseTower.buildTower();
				} else {
					Movement.moveToFriendlyPastrLocation(Movement.SNEAK);
				}
			}
		}
	}
	
	public static void doYourThing() throws GameActionException {
		shoot();
	}
	
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
				if (Pastr.enemyPastrCount() > 0) {
					if (robotToAttack.type == RobotType.PASTR) {
						rc.attackSquare(attackLocation);
					}
				} else {
					rc.attackSquare(attackLocation);
				}
			} 
		}
	}

}
