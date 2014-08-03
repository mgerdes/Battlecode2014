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
	
	static boolean pathFound;
	
	public static void run (RobotController rcin) {		
		try {	
			pathFound = false;
			
			rc = rcin;
			init();
			while(true) {
				if (rc.getType() == RobotType.HQ){
					runHQ();
				} else if (rc.getType() == RobotType.SOLDIER) {
					runSoldier();
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
		//printCoarsenedMap();
	}
	
	public static void runSoldier() throws GameActionException {
		if (rc.isActive()) {
			MapLocation loc = rc.getLocation();
			
			int xPoint = coarsenedMap[loc.x][loc.y][0];
			int yPoint = coarsenedMap[loc.x][loc.y][1];
			
			Direction movingDirection = Direction.values()[rc.readBroadcast(locToInt(new MapLocation(xPoint, yPoint)))];
			
			if (rc.canMove(movingDirection)) {
				rc.move(movingDirection);
			} else {
				//movingDirection = Direction.values()[(int)(Math.random() * 8)];
				//if (rc.canMove(movingDirection)) {
				//	rc.move(movingDirection);
				//}
			}
		}
	}
	
	public static void runHQ() throws GameActionException {
		if (!pathFound) {
			printCoarsenedMap();
			bfs(rc.senseEnemyHQLocation());
			pathFound = true;
			//printPath();
		}
		if (rc.isActive() && rc.canMove(Direction.NORTH)) {
			rc.spawn(Direction.NORTH);
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
				} else if (inside && map[x][y] != 2) {
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
		Queue q = new Queue(10000);
		int i = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (coarsenedMapColors[x][y] == -1) {
					fillCoarsenedMap(x, y, i, q);
					i++;
				}
			}
		}
	}
	
	public static void fillCoarsenedMap(int xStart, int yStart, int i, Queue q) {
		int x, y;
		
		int xTotal = 0, yTotal = 0, number = 0;
				
		for (x = xStart; x < width; x++) {
			if (map[x][yStart] == 2) {
				break;
			}
			for (y = yStart; y < height; y++) {
				if (map[xStart][y] == 2) {
					break;
				}
						
				if (map[x][y] == 2) {
					coarsenedMap[x][y][0] = -1;
					coarsenedMap[x][y][1] = -1;
					coarsenedMapColors[x][y] = -1;
				} else {
					q.enqueue(x);
					q.enqueue(y);
					
					number++;
					xTotal += x;
					yTotal += y;
					
					coarsenedMapColors[x][y] = i;
				}
				if (coarsenedMapLeftRight[xStart][y] == 1) {
					break;
				}
				if ((y + 1 < height) && (coarsenedMapLeftRight[xStart][y + 1] == 1)) {
					break;
				}
			}
			if (coarsenedMapUpDown[x][yStart] == 1) {
				break;
			}
			if ((x + 1 < width) && (coarsenedMapUpDown[x + 1][yStart] == 1)) {
				break;
			}
		}
		
		if (number != 0) {	
			int xAverage = xTotal / number;
			int yAverage = yTotal / number;
			
			while (!q.isEmpty()) {
				int newX = q.dequeue();
				int newY = q.dequeue();
				
				coarsenedMap[newX][newY][0] = xAverage;
				coarsenedMap[newX][newY][1] = yAverage;
			}
		}
	}

	public static void printCoarsenedMap() {		
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
		/*
		
		System.out.println();
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (map[x][y] == 2) {
					System.out.print('#');
				} else if (coarsenedMapLeftRight[x][y] == 1) {
					System.out.print('#');
				} else {
					System.out.print('0');
				}
			}
			System.out.println();
		}
		
		System.out.println();
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (map[x][y] == 2) {
					System.out.print('#');
				} else if (coarsenedMapUpDown[x][y] == 1) {
					System.out.print('#');
				} else {
					System.out.print('0');
				}
			}
			System.out.println();
		}
		*/
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
	
	public static void bfs(MapLocation location) throws GameActionException {
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
			
			MapLocation currentLocation = new MapLocation(currentX, currentY);
			
			for (int i = 0; i < 4; i++) {
				neighbor = getNeighbor(currentX, currentY, xOffsets[i], yOffsets[i]);
				
				int nextX = neighbor[0];
				int nextY = neighbor[1];
				
				if(nextX != -1) {
					if (!visited[nextX][nextY]) {
						q.enqueue(neighbor[0]);
						q.enqueue(neighbor[1]);
						
						MapLocation nextLocation = new MapLocation(nextX, nextY);
						
						rc.broadcast(locToInt(nextLocation), nextLocation.directionTo(currentLocation).ordinal());
						
						visited[neighbor[0]][neighbor[1]] = true;
					}
				}
			}
		}
	}
	
	public static int locToInt(MapLocation location) throws GameActionException {
		return location.x * 100 + location.y;
	}

	public static MapLocation intToLoc(int location) throws GameActionException {
		return new MapLocation(location / 100, location % 100);
	}
	
	public static void printPath() throws GameActionException {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < width; y++) {
				int xPoint = coarsenedMap[x][y][0];
				int yPoint = coarsenedMap[x][y][0];
				System.out.print(rc.readBroadcast(locToInt(new MapLocation(xPoint ,yPoint))));
				//System.out.print("[" + coarsenedMap[x][y][0] + "," + coarsenedMap[x][y][1] + "], ");
			}
			System.out.println();
		}
	}
	
	public static int[] getNeighbor(int xStart, int yStart, int xDirection, int yDirection) {
		int x = xStart, y = yStart;
		int initialNodeColor = coarsenedMapColors[x][y];
		while (x < width && x > -1 && y < height && y > -1 && map[x][y] != 2) {
			if (coarsenedMapColors[x][y] != initialNodeColor) {
				return new int[]{coarsenedMap[x][y][0], coarsenedMap[x][y][1]};
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
	
	void clear() {
		head = tail = 0;
	}
	
	boolean isEmpty() {
		return tail == head;
	}
}
