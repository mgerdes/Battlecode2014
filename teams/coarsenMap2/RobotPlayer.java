package coarsenMap2;

import java.util.*;
import battlecode.common.*;

public class RobotPlayer {

	static RobotController rc;
	static int map[][];
	
	static int coarsenedMap[][][];
	
	static int coarsenedMapColors[][];
	static int coarsenedMapUpDown[][];
	static int coarsenedMapLeftRight[][];
	
	static int width;
	static int height;
	
	public static void run (RobotController rcin) {		
		try {	
			rc = rcin;
			init();
			while(true) {
				if (rc.getType() == RobotType.HQ){
					runHQ();
				}
				rc.yield();
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
	
	public static void init() throws GameActionException {
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		map = new int[width][height];
		coarsenedMap = new int[width][height][2];
		coarsenedMapColors = new int[width][height];
		coarsenedMapUpDown = new int[width][height];
		coarsenedMapLeftRight = new int[width][height];
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				coarsenedMap[x][y][0] = -1;
				coarsenedMap[x][y][1] = -1;
				coarsenedMapColors[x][y] = -1;
			}
		}
		
		createMap();
		//printMap();
		createCoarsenedMap();
		coarsenMap();
		printCoarsenedMap();
	}
	
	public static void runHQ() throws GameActionException {
		
	}
	
	public static void createMap() throws GameActionException {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int tile = rc.senseTerrainTile(new MapLocation(x, y)).ordinal(); 
				map[x][y] = tile;
			}
		}
	}
	
	public static void createCoarsenedMap() throws GameActionException {
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
	
	public static void goUpAndDown(int x, int y1) {
		int y;
		y = y1 - 1;	
		while (y > -1 && map[x][y] != 2) {
			coarsenedMapUpDown[x][y] = 1;
			y--;
		}
		y = y1 + 1;
		while (y < height && map[x][y] != 2) {
			coarsenedMapUpDown[x][y] = 1;
			y++;
		}
	}
	
	public static void goLeftAndRight(int x1, int y) {
		int x;
		x = x1 - 1;
		while (x > -1 && map[x][y] != 2) {
			coarsenedMapLeftRight[x][y] = 1;
			x--;
		}
		x = x1 + 1;
		while (x < width && map[x][y] != 2) {
			coarsenedMapLeftRight[x][y] = 1;
			x++;
		}
	}
	
	public static void coarsenMap() {
		int i = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (coarsenedMapColors[x][y] == -1) {
					fillCoarsenedMap(x, y, i);
					i++;
				}
			}
		}
	}
	
	public static void fillCoarsenedMap(int xStart, int yStart, int i) {
		int x, y;
		
		for (x = xStart; x < width; x++) {
			
			/*
			if (coarsenedMapUpDown[x][yStart] == 1 || map[x][yStart] == 2) {
				break;
			}
			*/
			
			for (y = yStart; y < height; y++) {
				
				/*
				if (coarsenedMapLeftRight[xStart][y] == 1 || map[xStart][y] == 2) {
					break;
				}
				*/
				
				coarsenedMapColors[x][y] = i;
				coarsenedMap[x][y][0] = xStart;
				coarsenedMap[x][y][1] = yStart;
				
				if (coarsenedMapLeftRight[xStart][y] == 1 || map[xStart][y] == 2) {
					break;
				}
				
			}
			
			if (coarsenedMapUpDown[x][yStart] == 1 || map[x][yStart] == 2) {
				break;
			}
			
		}
	}

	public static void printCoarsenedMap() {
		/*
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (coarsenedMapUpDown[x][y] == 1) System.out.print('|');
				else if (map[x][y] == 2) System.out.print(1);
				else System.out.print(0);
			}
			System.out.println();
		}
		
		System.out.println();
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (coarsenedMapLeftRight[x][y] == 1) System.out.print('-');
				else if (map[x][y] == 2) System.out.print(1);
				else System.out.print(0);
			}
			System.out.println();
		}
		*/
		
		
		System.out.println();
		
		for (int y = 0; y < height; y++) {
			System.out.print("!");
			for (int x = 0; x < width; x++) {
				if (map[x][y] == 2) {
					System.out.print(-1);
				} else {
					System.out.print(coarsenedMapColors[x][y]);
				}
				if (x != width - 1) {
					System.out.print(",");
				}
			}
			System.out.print("!");
			System.out.println();
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
	
	public static void bfs(MapLocation location) {
		int startX = location.x;
		int startY = location.y;
		
		int neighbor[] = new int[2];
		
		int xOffsets[] = {0,1,0,-1};
		int yOffsets[] = {-1,0,1,0};
		
		boolean visited[][] = new boolean[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				visited[x][y] = false;
			}
		}
		
		Queue q = new Queue(2 * width * height);
		
		q.enqueue(startX);
		q.enqueue(startY);
		
		while (!q.isEmpty()) {
			int currentX = q.dequeue();
			int currentY = q.dequeue();
			
			for (int i = 0; i < 4; i++) {
				neighbor = getNeighbor(currentX, currentY, xOffsets[i], yOffsets[i]);
				if(neighbor[0] != -1) {
					q.enqueue(neighbor[0]);
					q.enqueue(neighbor[1]);
				}
			}
		}
	}
	
	public static int[] getNeighbor(int xStart, int yStart, int xDirection, int yDirection) {
		int x = xStart, y = yStart;
		int initialNodeColor = coarsenedMapColors[x][y];
		while (x < width && x > -1 && y < height && y < height && map[x][y] != 2) {
			if (coarsenedMapColors[x][y] != initialNodeColor) {
				return new int[]{x, y};
			}
			
			x += xDirection;
			y += yDirection;
		}
		return new int[]{-1, 1};
	}
	
}

class Queue {
	int tail, head, maxLength;
	int q[];
	
	Queue(int maxLength) {
		tail = 0;
		head = 0;
		this.maxLength = maxLength;
		q = new int[maxLength];
	}
	
	void enqueue(int x) {
		/*
		if (tail >= maxLength) {
		}
		*/
		q[tail] = x;
		if (tail == maxLength - 1) {
			tail = 0;
		} else {
			tail = tail + 1;
		}
	}
	
	int dequeue() {
		/*
		if (tail == head) {
		}
		*/
		int x = q[head];
		if (head == maxLength - 1) {
			head = 0;
		} else {
			head = head + 1;
		}
		return x;
	}
	
	boolean isEmpty() {
		return tail == head;
	}
}
