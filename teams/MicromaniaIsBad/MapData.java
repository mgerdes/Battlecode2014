package MicromaniaIsBad;

import java.util.*;
import battlecode.common.*;

public class MapData {
	
	private static RobotController rc = RobotPlayer.rc;

	public static Direction directions[] = Direction.values();

	public static final int WIDTH = rc.getMapWidth();
	public static final int HEIGHT = rc.getMapHeight();
	
	public static int map[][] = new int[WIDTH][HEIGHT];
	
	// creates a map where a 2 is a wall, 1 is a road, 0 is normal.
	public static void createMap() {
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				map[x][y] = rc.senseTerrainTile(new MapLocation(x,y)).ordinal();
			}
		}
	}

	// returns a four digit number, with the first two digits being the x location, second 2 the y location.
	public static int locToInt(MapLocation location) {
		return location.x * 100 + location.y;
	}

	public static MapLocation intToLoc(int location) {
		return new MapLocation(location / 100, location % 100);
	}

}
