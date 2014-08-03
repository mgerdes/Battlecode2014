package MarkyMark;

import java.util.*;

import battlecode.common.*;
import MarkyMark.*;

public class NoiseTower {
	
	static RobotController rc = RobotPlayer.rc;
	
	static int time = 5;
	static MapLocation attackLoc;
	static MapLocation nextAttackLoc;
	
	public static void run() throws GameActionException {
		if (rc.isActive()) {		
			
			if (rc.getHealth() < 50) {
				NoiseTower.towerCountSubtract();
			}
			
			if (attackLoc == null) {
				attackLoc = optimalCowAttackLocation();
				//attackLoc = new MapLocation(25, 18);
			}
			if (time == 2) {
				nextAttackLoc = optimalCowAttackLocation();
			}
			if (time == 0) {
				attackLoc = nextAttackLoc;
				time = 5;
			}
			time--;
			attack(attackLoc);
			rc.setIndicatorString(0, attackLoc.x + ", " + attackLoc.y);
		}
	}
	
	public static void buildTower() throws GameActionException {
		if (rc.isActive()) {
			rc.construct(RobotType.NOISETOWER);
			towerCountAdd();
		}
	}
	
	public static void attack(MapLocation location) throws GameActionException {
		if (rc.isActive() && rc.canAttackSquare(location)) {
			rc.attackSquare(location);
		}
	}
	
	public static MapLocation optimalCowAttackLocation() throws GameActionException {
		MapLocation startLoc = rc.getLocation();
		int startX = startLoc.x;
		int startY = startLoc.y;
		
		MapLocation maxCowLocation = new MapLocation(0, 0);
		int maxCowCount = 0;
				
		int dist = 10;
		
		for (int x = startX - dist; x < startX + dist; x++) {
			for (int y = startY - dist; y < startY + dist; y++) {
				
				MapLocation curLoc = new MapLocation(x,y);
				
				if (rc.canSenseSquare(curLoc)) {
					int numOfCowsAtCurLoc = (int)rc.senseCowsAtLocation(curLoc);
					if (numOfCowsAtCurLoc > maxCowCount) {
						if (Math.abs(x - startX) > 2 && Math.abs(y - startY) > 2) {
							maxCowCount = numOfCowsAtCurLoc;
							maxCowLocation = curLoc;
						}
						if (attackLoc != null) {
							attack(attackLoc);
						}
					}
				}
				
			}
		}
		
		return maxCowLocation.add(1, 1);
	}
	
	public static int towerCount() throws GameActionException {
		return rc.readBroadcast(61005);
	}
	
	public static void towerCountAdd() throws GameActionException {
		rc.broadcast(61005, towerCount() + 1);
	}
	
	public static void towerCountSubtract() throws GameActionException {
		rc.broadcast(61005, towerCount() - 1);
	}
}
