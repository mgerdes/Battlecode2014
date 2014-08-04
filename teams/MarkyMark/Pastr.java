package MarkyMark;

import java.util.*;

import battlecode.common.*;

public class Pastr {
	
	static RobotController rc = RobotPlayer.rc;
	static int COW_BOX_WIDTH = 5;
	static double cowLocations[][] = rc.senseCowGrowth();
	
	public static void run() throws GameActionException {
		if (rc.isActive()) {
			if (rc.getHealth() < 50) {
				pastrCountSubtract();
			}
		}
	}
	
	public static void buildPastr() throws GameActionException {
		if (rc.isActive()) {
			rc.construct(RobotType.PASTR);
			pastrCountAdd();
		}
	}
	
	public static int cowBoxCount(int startX, int startY) throws GameActionException {
		int x, y, cowCount = 0;
		for (x = startX; x < startX + COW_BOX_WIDTH; x++) {
			for (y = startY; y < startY + COW_BOX_WIDTH; y++) {
				if (x < Movement.width && y < Movement.height) { 
					cowCount += cowLocations[x][y];
				}
			}
		}
		return cowCount;
	}
	
	public static MapLocation optimalCowLocation() throws GameActionException {
		int x, y, maxCows = 0;
		MapLocation optimalCowLocation = new MapLocation(0, 0);
		
		for (x = 0; x < Movement.width; x += COW_BOX_WIDTH) {
			for (y = 0; y < Movement.height; y += COW_BOX_WIDTH) {				
				MapLocation currentLoc = new MapLocation(x ,y);
				if (currentLoc.distanceSquaredTo(rc.senseEnemyHQLocation()) < currentLoc.distanceSquaredTo(rc.senseHQLocation())) {
					continue;
				}
				int count = cowBoxCount(x,y);
				if (count > maxCows) {
					maxCows = count;
					optimalCowLocation = currentLoc;
				}
			}
		}
		
		int optiX = optimalCowLocation.x + 2;
		int optiY = optimalCowLocation.y + 2;
		
		int offsets[] = {0, -1, 1, -2, 2};
		
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				int xAdd = offsets[i];
				int yAdd = offsets[j];
				if (Movement.map[optiX + xAdd][optiY + yAdd] != 2 && cowLocations[optiX + xAdd][optiY + yAdd] > 0) {
					return new MapLocation(optiX + xAdd, optiY + yAdd);
				}
			}
		}
				
		return optimalCowLocation.add(2, 2);
	}
	
	public static int pastrCount() throws GameActionException {
		return rc.readBroadcast(61004);
	}
	
	public static void pastrCountAdd() throws GameActionException {
		rc.broadcast(61004, pastrCount() + 1);
	}
	
	public static void pastrCountSubtract() throws GameActionException {
		rc.broadcast(61004, pastrCount() - 1);
	}
	
	public static int friendlyPastrCount2() throws GameActionException {	
		MapLocation pastrLocations[] = rc.sensePastrLocations(rc.getTeam());
		return pastrLocations.length;
	}	
	
	public static int enemyPastrCount() throws GameActionException {
		MapLocation pastrLocations[] = rc.sensePastrLocations(rc.getTeam().opponent());
		return pastrLocations.length;
	}
	
	public static MapLocation friendlyPastrLocation() throws GameActionException {
		MapLocation pastrLocations[] = rc.sensePastrLocations(rc.getTeam());
		if (pastrLocations.length > 0)
			return pastrLocations[0];
		
		return new MapLocation(-1, -1);
	}
	
	public static MapLocation enemyPastrLocation() throws GameActionException {
		MapLocation pastrLocations[] = rc.sensePastrLocations(rc.getTeam().opponent());
		if (pastrLocations.length > 0)
			return pastrLocations[0];
		
		return new MapLocation(-1, -1);	
	}
		
	public static int distanceSquaredToFriendlyPastr() throws GameActionException {
		MapLocation pastrLocations[] = rc.sensePastrLocations(rc.getTeam());
		if (pastrLocations.length > 0)
			return rc.getLocation().distanceSquaredTo(pastrLocations[0]);
		
		return -1;
	}
}
