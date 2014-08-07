package Micromania;

import java.util.*;

import battlecode.common.*;

public class Soldier {

	public static RobotController rc = RobotPlayer.rc;

	public static void run() throws GameActionException {
		Pastr.updateEnemyPastrLocations();
		if (Pastr.enemyPastrLocations.length > 0) {
			Movement.moveToEnemyPastrLocation();
		} else {
			Movement.moveOnPathNum(PathFind.FRIENDLY_PASTR_PATH_NUM_1, 0);
		}

	}
	
	public static void shoot() throws GameActionException {
		if (rc.isActive()) {
			Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, RobotType.SOLDIER.attackRadiusMaxSquared, rc.getTeam().opponent());
		
			if (enemies.length > 0) {
				RobotInfo robotToAttack = rc.senseRobotInfo(enemies[(int)(Math.random() * enemies.length)]);
				if (robotToAttack.type != RobotType.HQ) { 
					MapLocation attackLocation = robotToAttack.location;
					if (rc.canAttackSquare(attackLocation)) {
						rc.attackSquare(attackLocation);
					} 
				}
			}
		}
	}

}
