package MarkyMark;

import java.util.*;

import battlecode.common.*;
import MarkyMark.*;

public class HQ {
	
	static RobotController rc = RobotPlayer.rc;
	
	public static void run() throws GameActionException {
		doYourThing();
		
		if (!PathFind.isHQPathFound(PathFind.FRIENDLY_PASTR_PATH_NUM)) {
			PathFind.findHQLocationTo(Pastr.optimalCowLocation(), PathFind.FRIENDLY_PASTR_PATH_NUM);
			PathFind.setHQPathFound(PathFind.FRIENDLY_PASTR_PATH_NUM);
		}
		
		if (Pastr.enemyPastrCount() > 0) {
			MapLocation enemyPastrLocation = Pastr.enemyPastrLocation();
			if (Movement.locToInt(enemyPastrLocation) != rc.readBroadcast(PathFind.HQ_PATH_LOCATION_CHANNEL[PathFind.ENEMY_PASTR_PATH_NUM])) {
				PathFind.findHQLocationTo(enemyPastrLocation, PathFind.ENEMY_PASTR_PATH_NUM);
				PathFind.setHQPathFound(PathFind.ENEMY_PASTR_PATH_NUM);
			}
		}
		
	}
	
	public static void doYourThing() throws GameActionException {
		spawn();
		shoot();
	}
	
	public static void spawn() throws GameActionException {
		if (rc.isActive() && rc.senseRobotCount() < GameConstants.MAX_ROBOTS) {
			if (rc.canMove(Direction.NORTH)) {
				rc.spawn(Direction.NORTH);
			} else if (rc.canMove(Direction.NORTH_EAST)){
				rc.spawn(Direction.NORTH_EAST);
			} else if (rc.canMove(Direction.EAST)){
				rc.spawn(Direction.EAST);
			} else if (rc.canMove(Direction.SOUTH_EAST)){
				rc.spawn(Direction.SOUTH_EAST);
			} else if (rc.canMove(Direction.SOUTH)){
				rc.spawn(Direction.SOUTH);
			} else if (rc.canMove(Direction.SOUTH_WEST)){
				rc.spawn(Direction.SOUTH_WEST);
			} else if (rc.canMove(Direction.WEST)){
				rc.spawn(Direction.WEST);
			} else if (rc.canMove(Direction.NORTH_WEST)){
				rc.spawn(Direction.NORTH_WEST);
			}
		}
	}
	
	public static void shoot() throws GameActionException {
		Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, RobotType.HQ.attackRadiusMaxSquared, rc.getTeam().opponent());		
		if (enemies.length > 0) {
			RobotInfo robotToAttack = rc.senseRobotInfo(enemies[(int)(Math.random() * enemies.length)]);
			MapLocation attackLocation = robotToAttack.location;
			if (rc.isActive() && rc.canAttackSquare(attackLocation)) {
				rc.attackSquare(attackLocation);
			} 
		}
	}
	
}
