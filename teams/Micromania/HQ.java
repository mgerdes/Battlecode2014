package Micromania;

import java.util.*;

import battlecode.common.*;

public class HQ {

	public static RobotController rc = RobotPlayer.rc;
	
	public static int lastRoundIUpdatedEnemyPastrLocations = 0;

	public static void init() throws GameActionException {
		for (int i = 0; i < 4; i++) {
			rc.broadcast(Channels.HQ_PATH_LOCATION_CHANNEL[i], -1);
		}
		createPathToFriendlyPastr();
	}
	
	public static void run() throws GameActionException {
		spawn();
		shoot();
		
		if (lastRoundIUpdatedEnemyPastrLocations + 20 < Clock.getRoundNum()) {
			createPathToEnemyPastrs();
			lastRoundIUpdatedEnemyPastrLocations = Clock.getRoundNum();
		}
	}
	
	public static void createPathToFriendlyPastr() throws GameActionException {
		MapLocation optimalCowLocation = Pastr.optimalCowLocation();
		PathFind.createPathTo(optimalCowLocation, PathFind.FRIENDLY_PASTR_PATH_NUM_1);
	}
	
	public static void createPathToEnemyPastrs() throws GameActionException {
		Pastr.updateEnemyPastrLocations();
				
		int pathNum = PathFind.freeEnemyPastrPathNum();
								
		if (pathNum == -1) {
			return;
		} else {
			for (MapLocation pastrLocation : Pastr.enemyPastrLocations) {
				if (PathFind.isPathCreatedTo(pastrLocation) == -1) {
					System.out.println("Finding path on path num " + pathNum);
					PathFind.createPathTo(pastrLocation, pathNum);
					return;
				}
			}	
		}
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
			Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, RobotType.SOLDIER.attackRadiusMaxSquared, rc.getTeam().opponent());
		
			if (enemies.length > 0) {
				RobotInfo robotToAttack = rc.senseRobotInfo(enemies[(int)(Math.random() * enemies.length)]);
				MapLocation attackLocation = robotToAttack.location;
				if (rc.canAttackSquare(attackLocation)) {
					rc.attackSquare(attackLocation);
				} 
			}
		}
	}


}