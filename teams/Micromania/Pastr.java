package Micromania;

import java.util.*;
import battlecode.common.*;

public class Pastr {
	
	public static RobotController rc = RobotPlayer.rc;
	
	public static final double cowGrowth[][] = rc.senseCowGrowth();
	public static MapLocation enemyPastrLocations[];

	public static void updateEnemyPastrLocations() {
		enemyPastrLocations = rc.sensePastrLocations(rc.getTeam().opponent());
	}
	
	public static int cowBoxCount(int startX, int startY) throws GameActionException {
		int x, y, totalCowGrowth = 0;
		for (x = startX; x < startX + 5; x++) {
			for (y = startY; y < startY + 5; y++) {
				if (x < MapData.WIDTH && y < MapData.HEIGHT) { 
					totalCowGrowth += cowGrowth[x][y];
				}
			}
		}
		return totalCowGrowth;
	}
	
	public static MapLocation optimalCowLocation() throws GameActionException {
		int x, y, maxCowGrowth = 0;
		MapLocation optimalCowLocation = new MapLocation(0, 0);
		
		for (x = 0; x < MapData.WIDTH; x += 5) {
			for (y = 0; y < MapData.HEIGHT; y += 5) {				
				MapLocation currentLoc = new MapLocation(x ,y);
				if (currentLoc.distanceSquaredTo(rc.senseEnemyHQLocation()) < currentLoc.distanceSquaredTo(rc.senseHQLocation())) {
					continue;
				}
				int count = cowBoxCount(x,y);
				if (count > maxCowGrowth) {
					maxCowGrowth = count;
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
				if (MapData.map[optiX + xAdd][optiY + yAdd] != 2 && cowGrowth[optiX + xAdd][optiY + yAdd] > 0) {
					return new MapLocation(optiX + xAdd, optiY + yAdd);
				}
			}
		}
				
		return optimalCowLocation.add(2, 2);
	}

}
