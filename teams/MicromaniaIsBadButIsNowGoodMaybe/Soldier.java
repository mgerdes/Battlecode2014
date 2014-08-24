// 1. move to optimal pastrLocation.
// 2. wait until round 500 to build a pastr.
// 		2a. if while waiting enemy builds a pastr then send everyone but two to kill it. Other two create a pastr.
// 3. build a pastr after round 500.
// 4. defend pastr.
// 		4a. what to do if enemy build pastr? Maybe make decision basted on milk count.
package MicromaniaIsBadButIsNowGoodMaybe;

import java.util.*;
import battlecode.common.*;

public class Soldier {
	
	static RobotController rc = RobotPlayer.rc;

	static boolean uploadedLocation;
	static boolean inGoodSituation;
	static boolean inSuicideMode;
	static boolean defenseMode;

	static Robot enemies[];
	static Robot friends[];

	static MapLocation enemyHQLocation;
	static MapLocation friendlyHQLocation;
	static MapLocation myLocation;
	static MapLocation enemyToAttackLocation;
	static MapLocation attackLocation;

	static Team goodTeam;
	static Team badTeam;

	static int currentRound;

	// have robot upload its location if it sees an enemy
	public static void uploadLocation(MapLocation location) throws GameActionException {
		uploadedLocation = true;			
		int uploadData = currentRound * 10000 + MapData.locToInt(location);
		rc.broadcast(Channels.ENEMY_LOCATION_CHANNEL, uploadData);		
	}

	// Download the location of a friendly soldier who have seen an enemy
	public static void downloadLocation() throws GameActionException {
		int data = rc.readBroadcast(Channels.ENEMY_LOCATION_CHANNEL);
		int uploadRound = data / 10000;
		enemyToAttackLocation = MapData.intToLoc(data % 10000);
	}

	// Returns true if the last round a location was uploaded is within 10 rounds of the current round. 
	public static boolean isLocationUploaded() throws GameActionException {
		int data = rc.readBroadcast(Channels.ENEMY_LOCATION_CHANNEL);
		int uploadRound = data / 10000;

		if (currentRound - 5 > uploadRound)
			return false;
		else 
			return true;
	}
	
	// run before every round.
	public static void initRound() throws GameActionException {
		uploadedLocation = isLocationUploaded();
		if (uploadedLocation) downloadLocation();
		enemies = rc.senseNearbyGameObjects(Robot.class, RobotType.SOLDIER.sensorRadiusSquared, badTeam);		
		friends = rc.senseNearbyGameObjects(Robot.class, RobotType.SOLDIER.sensorRadiusSquared, goodTeam);

		myLocation = rc.getLocation();
		currentRound = Clock.getRoundNum();

		inGoodSituation = inGoodSituation();

		attackLocation = null;
	}

	// run when soldier first spawn
	public static void init() throws GameActionException {
		goodTeam = rc.getTeam();
		badTeam = goodTeam.opponent();
		enemyHQLocation = rc.senseEnemyHQLocation();

		inSuicideMode = false;
		defenseMode = false;
	}

	public static void run() throws GameActionException {	
		initRound();
		
		// avoid the enemy hq
		if (myLocation.distanceSquaredTo(enemyHQLocation) < RobotType.HQ.attackRadiusMaxSquared + 10) {
			Movement.move(myLocation.directionTo(enemyHQLocation).opposite(), Movement.RUN);
		}

		doYourThing();

		// tactics are messy :(.
		if (Pastr.enemyPastrCount() > 0) {
			if (!defenseMode)
				Movement.moveOnPath(PathFind.ENEMY_PASTR_PATH_NUM_1, Movement.RUN);
		} else {
			if (Clock.getRoundNum() < 300) {
				Movement.moveOnPath(PathFind.FRIENDLY_PASTR_PATH_NUM_1, Movement.RUN);
			} else {	
				defenseMode = true;
				int distanceSquaredToPastr = PathFind.distanceSquaredToPathLocation(PathFind.FRIENDLY_PASTR_PATH_NUM_1);
				
				if (!Pastr.isPastrBuilt() && distanceSquaredToPastr == 0 && inGoodSituation) {
					Pastr.buildPastr();
				} else if (!NoiseTower.isTowerBuilt() && distanceSquaredToPastr == 1 && inGoodSituation) {
					NoiseTower.buildTower();
				} else {
					Movement.moveOnPath(PathFind.FRIENDLY_PASTR_PATH_NUM_1, Movement.SNEAK);
				}
			}
		}

	}
	
	// in good situation if the friendly count is greater than the enemy count.
	public static boolean inGoodSituation() throws GameActionException {
		return friends.length + 2 > enemies.length;
	}
	
	public static void doYourThing() throws GameActionException {
		whatDoIDo();
	}
	
	public static void whatDoIDo() throws GameActionException {
		tryToUploadLocation();
		if (uploadedLocation) {
			if (rc.getHealth() > 95 && rc.getRobot().getID() % 50 == 0) {
				inSuicideMode = true;
			} 
			if (inSuicideMode) {
				cyanideSuicide(enemyToAttackLocation);
			}
			if (rc.getHealth() > 30) {
				shoot();
				if (defenseMode && PathFind.distanceSquaredToPathLocation(PathFind.FRIENDLY_PASTR_PATH_NUM_1) < 100) { 
					Movement.move(enemyToAttackLocation, Movement.RUN);
				} else if (!defenseMode) {
					Movement.move(enemyToAttackLocation, Movement.RUN);
				}

			} else {
				Movement.move(myLocation.directionTo(enemyToAttackLocation).opposite(), Movement.RUN);
			}
		}
	}

	public static void cyanideSuicide(MapLocation location) throws GameActionException {
		if (myLocation.distanceSquaredTo(location) > 2) {
			Movement.move(location, Movement.RUN);
		} else {
			rc.selfDestruct();	 
		}
	}


	public static void tryToUploadLocation() throws GameActionException {
		for (Robot enemy : enemies) {
			RobotInfo enemyInfo = rc.senseRobotInfo(enemy);
			if (enemyInfo.type != RobotType.HQ) {
				MapLocation enemyLocation = enemyInfo.location;

				if (!uploadedLocation) uploadLocation(enemyLocation);

				if(rc.canAttackSquare(enemyLocation)) {
					attackLocation = enemyLocation;
				}
			}
		}
	}

	public static boolean enemyAt(MapLocation location) throws GameActionException {
		for (Robot enemy : enemies) {
			RobotInfo enemyInfo = rc.senseRobotInfo(enemy);
			if (enemyInfo.location.x == location.x && enemyInfo.location.y == location.y) return true;
		}
		return false;
	}
	
	public static void shoot() throws GameActionException {
		if (enemyToAttackLocation != null && rc.isActive() && rc.canAttackSquare(enemyToAttackLocation) && enemyAt(enemyToAttackLocation)) {
			rc.setIndicatorString(0, "attacking dude at " + enemyToAttackLocation.x + ", " + enemyToAttackLocation.y);
			rc.attackSquare(enemyToAttackLocation);
		} else if (attackLocation != null && rc.isActive() && rc.canAttackSquare(attackLocation)) {
			rc.attackSquare(attackLocation);
			rc.setIndicatorString(0, "attacking dude at " + attackLocation.x + ", " + attackLocation.y);
		}
	}
	
}
