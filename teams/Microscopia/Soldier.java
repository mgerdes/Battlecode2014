// 1. move to optimal pastrLocation.
// 2. wait until round 500 to build a pastr.
// 		2a. if while waiting enemy builds a pastr then send everyone but two to kill it. Other two create a pastr.
// 3. build a pastr after round 500.
// 4. defend pastr.
// 		4a. what to do if enemy build pastr? Maybe make decision basted on milk count.

package Microscopia;

import java.util.*;

import battlecode.common.*;
import MarkyMark.*;

public class Soldier {
	
	static RobotController rc = RobotPlayer.rc;
	
	public static void run() throws GameActionException {
		doYourThing();
		
		int startingByteCodes = Clock.getBytecodeNum();
		inGoodSituation();
		rc.setIndicatorString(0, "" + (Clock.getBytecodeNum() - startingByteCodes));
						
		if (Pastr.enemyPastrCount() > 0) {
			Movement.moveToEnemyPastrLocation(Movement.RUN);
		} else {
			Movement.moveToFriendlyPastrLocation(Movement.RUN);
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
			RobotInfo robotToAttack = rc.senseRobotInfo(enemies[0]);
			MapLocation attackLocation = robotToAttack.location;
			if (rc.isActive() && robotToAttack.type != RobotType.HQ && rc.canAttackSquare(attackLocation)) {
				rc.attackSquare(attackLocation);
			} 
		}
	}

}
