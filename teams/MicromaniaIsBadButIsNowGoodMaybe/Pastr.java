package MicromaniaIsBadButIsNowGoodMaybe;

import java.util.*;
import battlecode.common.*;

public class Pastr {
	
	static RobotController rc = RobotPlayer.rc;
	static int COW_BOX_WIDTH = 5;
	static double cowLocations[][] = rc.senseCowGrowth();
		
	public static void run() throws GameActionException {
		if (rc.isActive()) {
		}
	}
	
	public static void buildPastr() throws GameActionException {
		if (rc.isActive()) {
			rc.construct(RobotType.PASTR);
			setPastrBuilt();
		}
	}
	
	public static int cowBoxCount(int startX, int startY) throws GameActionException {
		int x, y, cowCount = 0;
		for (x = startX; x < startX + COW_BOX_WIDTH; x++) {
			for (y = startY; y < startY + COW_BOX_WIDTH; y++) {
				if (x < MapData.WIDTH && y < MapData.HEIGHT) { 
					cowCount += cowLocations[x][y];
				}
			}
		}
		return cowCount;
	}
	
	public static MapLocation optimalCowLocation() throws GameActionException {
		int x, y, maxCows = 0;
		MapLocation optimalCowLocation = new MapLocation(0, 0);
		
		for (x = 0; x < MapData.WIDTH; x += COW_BOX_WIDTH) {
			for (y = 0; y < MapData.HEIGHT; y += COW_BOX_WIDTH) {				
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
				if (MapData.map[optiX + xAdd][optiY + yAdd] != 2 && cowLocations[optiX + xAdd][optiY + yAdd] > 0) {
					return new MapLocation(optiX + xAdd, optiY + yAdd);
				}
			}
		}
				
		return optimalCowLocation.add(2, 2);
	}
	
	
	public static boolean isConstructing() throws GameActionException {
		Robot robots[] = rc.senseNearbyGameObjects(Robot.class, 10000, rc.getTeam());
		
		for (Robot robot : robots) {
			if (rc.canSenseObject(robot)) {
				RobotInfo robotInfo = rc.senseRobotInfo(robot);
				if(robotInfo.isConstructing && robotInfo.constructingType == RobotType.PASTR) return true;
			}
		}
		
		return false;
	}
	
	public static void setPastrBuilt() throws GameActionException {
		rc.broadcast(Channels.IS_PASTR_BUILT_CHANNEL, 1);
	}
	
	public static boolean isPastrBuiltFull() throws GameActionException {
		if (isConstructing()) return true;
		
		MapLocation[] pastrs = rc.sensePastrLocations(rc.getTeam());
		return pastrs.length > 0;
	}
	
	public static boolean isPastrBuilt() throws GameActionException {
		return rc.readBroadcast(Channels.IS_PASTR_BUILT_CHANNEL) == 1;
	}
	
	public static int enemyPastrCount() throws GameActionException {
		return rc.readBroadcast(Channels.ENEMY_PASTR_COUNT);
	}
	
	public static int enemyPastrCountFull() throws GameActionException {
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
