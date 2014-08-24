package MicromaniaIsBadButIsNowGoodMaybe;

import java.util.*;
import battlecode.common.*;

public class HQ {
	
	static RobotController rc = RobotPlayer.rc;
	static int everySoOftenLastHappened = -1;
	
	public static void run() throws GameActionException {
		doYourThing();
		
		// find a path to the optimal cow location.
		if (!PathFind.isPathFound(PathFind.FRIENDLY_PASTR_PATH_NUM_1)) {
			PathFind.createPathTo(Pastr.optimalCowLocation(), PathFind.FRIENDLY_PASTR_PATH_NUM_1);
			PathFind.setPathFound(PathFind.FRIENDLY_PASTR_PATH_NUM_1);
		}
		
		// finds the path to an enemy pastr if there is one.
		if (Pastr.enemyPastrCount() > 0) {
			MapLocation enemyPastrLocation = Pastr.enemyPastrLocation();
			// checks to see if a path already goes to this pastr. 
			// If there is do nothing, if the path goes to an already destroyed pastr then make a new path.

			if (MapData.locToInt(enemyPastrLocation) != rc.readBroadcast(Channels.PATH_LOCATION_CHANNEL[PathFind.ENEMY_PASTR_PATH_NUM_1])) {
				// Make a new path because current path goes to an already destroyed pastr.
				// rc.setIndicatorString(0, "Changed path to " + MapData.locToInt(enemyPastrLocation));
				PathFind.createPathTo(enemyPastrLocation, PathFind.ENEMY_PASTR_PATH_NUM_1);
			}		
		}
		
	}
		
	public static void doYourThing() throws GameActionException {
		shoot();
		spawn();

		// execute stuff every 20 rounds.
		int currentRoundNum = Clock.getRoundNum();
		if (currentRoundNum > everySoOftenLastHappened + 20) {
			everySoOften();
			everySoOftenLastHappened = currentRoundNum;
		}
	}
	
	// this will be executed every 20 rounds.
	// it recalculates the tower, pastr counts.
	public static void everySoOften() throws GameActionException {			
		rc.broadcast(Channels.IS_TOWER_BUILT_CHANNEL, NoiseTower.isTowerBuiltFull() ? 1 : 0);
		rc.broadcast(Channels.IS_PASTR_BUILT_CHANNEL, Pastr.isPastrBuiltFull() ? 1 : 0);
		rc.broadcast(Channels.ENEMY_PASTR_COUNT, Pastr.enemyPastrCountFull());
	}
		
	public static void spawn() throws GameActionException {
		if (rc.isActive() && rc.senseRobotCount() < GameConstants.MAX_ROBOTS) {
			for (Direction directionToSpawn : MapData.directions){
				if (rc.canMove(directionToSpawn)) {
					rc.spawn(directionToSpawn);
					return;
				}
			}
		}
	}
	
	public static void shoot() throws GameActionException {
		if (rc.isActive()) {
			Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, RobotType.HQ.attackRadiusMaxSquared, rc.getTeam().opponent());
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
	
}
