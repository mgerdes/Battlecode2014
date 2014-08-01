package coarsenMap;

import java.util.*;

import battlecode.common.*;

public class RobotPlayer {

	static RobotController rc;
	static int coarsenedMap[][];
	static int map[][];
	static int newMap[][][];
	static int newMap2[][];
	static int width;
	static int height;

	public static void run (RobotController rcin) {
		rc = rcin;
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		map = new int[width][height];
		coarsenedMap = new int[width][height];
		newMap = new int[width][height][2];
		newMap2 = new int[width][height];
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				newMap[x][y][0] = -1;
				newMap[x][y][1] = -1;
				newMap2[x][y] = -1;
				newMap2[x][y] = -1;
			}
		}

		try {
			createMap();
			coarsenMap();
			//printCoarsenedMap();
			createNewMap();
			printNewMap();
			printMap();
			
			while (true) {
				if (rc.getType() == RobotType.HQ) {
					
				}
				rc.yield();
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
	
	public static void coarsenMap() throws GameActionException {
		boolean inside;
		inside = false;
		for (int y = 0; y < height; y++) {
			for (int  x = 0; x < width; x++) {
				if (!inside) {
					if (map[x][y] == 2) {
						inside = true;
						goUpAndDown(x, y);
					}
				} else if (map[x][y] != 2) {
					inside = false;
					goUpAndDown(x - 1, y);
				}
			}
			inside = false;
		}
		inside = false;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (!inside) {
					if (map[x][y] == 2) {
						inside = true;
						goLeftAndRight(x,y);
					}
				} else if (map[x][y] != 2) {
					inside = false;
					goLeftAndRight(x, y - 1);
				}
			}
			inside = false;
		}
	}

	public static void createNewMap() {
		int i = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (map[x][y] != 2 && newMap[x][y][0] == -1) {
					fillNewMap(x,y, i);
					i++;
				}
			}
		}
	}

	public static void fillNewMap(int x, int y, int i) {
		int x1 = x, y1 = y;
		while (x1 < width) {
			while (y1 < height) {
				newMap[x1][y1][0] = x;
				newMap[x1][y1][1] = y;
				
				newMap2[x1][y1] = i;
				newMap2[x1][y1] = i;
				
				if (map[x1][y1] == 2) {
					newMap2[x1][y1] = -1;
					newMap2[x1][y1] = -1;
				}
				
				if (coarsenedMap[x][y1] == 1) {
					break;
				}
				
				y1++;
			}
			y1 = y;
			
			if(coarsenedMap[x1][y] == 1) {
				break;
			}
			
			x1++;
		}
	}

	public static void printCoarsenedMap() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (coarsenedMap[x][y] == 1) System.out.print('-');
				else if (map[x][y] == 2) System.out.print(1);
				else System.out.print(0);
			}
			System.out.println();
		}
	}


	public static void goLeftAndRight(int x1, int y) {
		int x;
		x = x1 - 1;
		while (x > -1 && map[x][y] != 2) {
			coarsenedMap[x][y] = 1;
			x--;
		}
		x = x1 + 1;
		while (x < width && map[x][y] != 2) {
			coarsenedMap[x][y] = 1;
			x++;
		}
	}

	public static void goUpAndDown(int x, int y1) {
		int y;
		y = y1 - 1;	
		while (y > -1 && map[x][y] != 2) {
			coarsenedMap[x][y] = 1;
			y--;
		}
		y = y1 + 1;
		while (y < height && map[x][y] != 2) {
			coarsenedMap[x][y] = 1;
			y++;
		}
	}

	public static void createMap() throws GameActionException {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int tile = rc.senseTerrainTile(new MapLocation(x, y)).ordinal(); 
				map[x][y] = tile;
			}
		}
	}
	
	public static void printMap() throws GameActionException {
		for (int y = 0; y < height; y++) {
			System.out.print("!");
			for (int x = 0; x < width; x++) {
				System.out.print(map[x][y]);
				if (x != width - 1) {
					System.out.print(",");
				}
			}
			System.out.print("!");
			System.out.println();
		}
	}

	public static void printNewMap() throws GameActionException {
		for (int y = 0; y < height; y++) {
			System.out.print("!");
			for (int x = 0; x < width; x++) {
				System.out.print(newMap2[x][y]);
				if (x != width - 1) {
					System.out.print(",");
				}			
			}
			System.out.print("!");
			System.out.println();
		}
	}

}
