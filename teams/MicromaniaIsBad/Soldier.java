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
		
		MapLocation enemyHQLocation = rc.senseEnemyHQLocation();
		MapLocation myLocation = rc.getLocation();
		
		// Avoid the enemy pastr.
		if (myLocation.distanceSquaredTo(enemyHQLocation) < RobotType.HQ.attackRadiusMaxSquared) {
			Movement.move(myLocation.directionTo(enemyHQLocation).opposite(), Movement.RUN);
		}
		
		// if the enemy has built a pastr than go to it, otherwise go to the friendly pastr location.
		if (Pastr.enemyPastrCount() > 0) {
			Movement.moveOnPath(PathFind.ENEMY_PASTR_PATH_NUM_1, Movement.RUN);
		} else {
			Movement.moveOnPath(PathFind.FRIENDLY_PASTR_PATH_NUM_1, Movement.RUN);
		}
	}
	
	public static void doYourThing() throws GameActionException {
		shoot();
	}
	
	public static void shoot() throws GameActionException {
		Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, RobotType.SOLDIER.attackRadiusMaxSquared, rc.getTeam().opponent());		
		if (enemies.length > 0) {
			RobotInfo robotToAttack = rc.senseRobotInfo(enemies[0]);
			
			if (robotToAttack.type != RobotType.HQ) {
			
				MapLocation attackLocation = robotToAttack.location;
				
				if (rc.isActive() && rc.canAttackSquare(attackLocation)) {
					rc.attackSquare(attackLocation);
				} 
				
			}
			
		}
	}

}
