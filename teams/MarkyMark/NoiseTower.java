package MarkyMark;

import java.util.*;

import battlecode.common.*;
import MarkyMark.*;

public class NoiseTower {
	
	static RobotController rc = RobotPlayer.rc;
	
	static int time = 5;
	
	static int directionIndex = 0;
	static int distance = 20;
	
	static int xOffset[] = {-1, -1, -1,  0,  1, 1, 1, 0};
	static int yOffset[] = { 1,  0, -1, -1, -1, 0, 1, 1};
	
	static MapLocation location = rc.getLocation();
	static int x = location.x;
	static int y = location.y;
	
	static int currentRoundNum = Clock.getRoundNum();
		
	static double cowLocations[][] = rc.senseCowGrowth();
	
	static final int IS_TOWER_BUILT_CHANNEL = 61001;
	
	public static void run() throws GameActionException {
		if (rc.isActive()) {
			if ((Clock.getRoundNum() - 1) > currentRoundNum) {
				distance -= 2;
				
				if (distance < 3) {					
					directionIndex++;
					if (directionIndex == 8) {
						directionIndex = 0;
					}
					
					distance = 18;
				}
				
				currentRoundNum = Clock.getRoundNum();				
			}
			
			attack(new MapLocation(x + (distance * xOffset[directionIndex]), y + (distance * yOffset[directionIndex])));
		}
	}
	
	public static int startingDistance(int directionIndex) {
		int startingDistance = 0;
		while (startingDistance < 20 
				&& startingDistance * xOffset[directionIndex] < Movement.width 
				&& startingDistance * yOffset[directionIndex] < Movement.height) {
			startingDistance += 2;
		}
		return startingDistance;
	}
	
	public static void buildTower() throws GameActionException {
		if (rc.isActive()) {
			rc.construct(RobotType.NOISETOWER);
			setTowerBuilt();
		}
	}
	
	public static void attack(MapLocation location) throws GameActionException {
		if (rc.isActive() && rc.canAttackSquare(location)) {
			rc.attackSquare(location);
		}
	}
	
	public static boolean isConstructing() throws GameActionException {
		Robot robots[] = rc.senseNearbyGameObjects(Robot.class, 10000, rc.getTeam());
		
		for (Robot robot : robots) {
			if (rc.canSenseObject(robot)) {
				RobotInfo robotInfo = rc.senseRobotInfo(robot);
				if(robotInfo.isConstructing && robotInfo.constructingType == RobotType.NOISETOWER) return true;
			}
		}
		
		return false;
	}
	
	public static void setTowerBuilt() throws GameActionException {
		rc.broadcast(IS_TOWER_BUILT_CHANNEL, 1);;
	}
	
	public static boolean isTowerBuiltFull() throws GameActionException {
		if (isConstructing()) return true;
		
		Robot robots[] = rc.senseNearbyGameObjects(Robot.class, 10000, rc.getTeam());
		int count = 0;
		
		for (Robot robot : robots) {
			if (rc.canSenseObject(robot)) {
				if (rc.senseRobotInfo(robot).type == RobotType.NOISETOWER){
					count++;
				}
			}
		}
		
		return count > 0;
	}
	
	public static boolean isTowerBuilt() throws GameActionException {
		return rc.readBroadcast(IS_TOWER_BUILT_CHANNEL) == 1;
	}
}
