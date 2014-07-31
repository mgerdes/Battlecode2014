package pathFind;

import battlecode.common.*;

import java.util.*;

public class RobotPlayer {
	
	public static RobotController rc;
	public static int[][] map;
	public static int width;
	public static int height;
	public static Direction[] directions = Direction.values();
	
	public static void run (RobotController rcin) {
		rc = rcin;
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		
		try {
			while (true) {			
				if (rc.getType() == RobotType.SOLDIER) {
					runSoldier();
				} else if (rc.getType() == RobotType.HQ) {
					runHQ();
				}
				rc.yield();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void runSoldier() throws GameActionException {
		if (isHQPathFound()) {
			moveWhereHQWants();
		} else {
			moveRandomly();
		}
	}

	public static void runHQ() throws GameActionException {
		spawn();
		
		if (map == null) {
			createMap();
			printMap();
			setHQPathNotFound();
		}

		if (!isHQPathFound()) {
			findHQPathTo(rc.senseEnemyHQLocation());
			printDirectionalField();
			setHQPathFound();
		} 
	}
	
	public static void moveRandomly() throws GameActionException {
		Direction movingDirection = directions[(int)(Math.random() * 8)];
		if (rc.isActive()) {
			if (rc.canMove(movingDirection)) {
				rc.move(movingDirection);
			}
		}
	}
	
	public static void moveWhereHQWants() throws GameActionException {
		Direction movingDirection = directions[rc.readBroadcast(locToInt(rc.getLocation()))];
		if (rc.isActive()) {
			if (rc.canMove(movingDirection)) {
				rc.move(movingDirection);
			} else {
				//moveRandomly();
			}
		}
	}
	
	public static void spawn() throws GameActionException {
		if (rc.isActive() && rc.canMove(Direction.NORTH)){
			rc.spawn(Direction.NORTH);
		}
	}

	public static void createMap() throws GameActionException {
		map = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				map[x][y] = rc.senseTerrainTile(new MapLocation(x, y)).ordinal();
			}
		}
	}

	public static void setHQPathNotFound() throws GameActionException {
		rc.broadcast(10000, -1);
	}

	public static void setHQPathFound() throws GameActionException {
		rc.broadcast(10000, 1);
	}

	public static boolean isHQPathFound() throws GameActionException {
		return rc.readBroadcast(10000) == 1;
	} 

	public static int locToInt(MapLocation location) throws GameActionException {
		return location.x * 100 + location.y;
	}

	public static MapLocation intToLoc(int location) throws GameActionException {
		return new MapLocation(location / 100, location % 100);
	}

	public static void findHQPathTo(MapLocation destination) throws GameActionException {
		int startX = destination.x;
		int startY = destination.y;

		int xOffsets[] = {-1, -1, -1,  0,  1, 1, 1, 0};
		int yOffsets[] = { 1,  0, -1, -1, -1, 0, 1, 1};

		boolean visited[][] = new boolean[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				visited[x][y] = false;
			}
		}
		
		LinkedList<Integer> q = new LinkedList<Integer>();

		q.add(startX);
		q.add(startY);
		q.add(0);

		while (!q.isEmpty()) {
			int currentX = q.poll();
			int currentY = q.poll();
			int timeLeft = q.poll();
			
			MapLocation currentLocation = new MapLocation(currentX, currentY);

			if (timeLeft == 0) {
				for (int i = 0; i < 8; i++) {
					int nextX = currentX + xOffsets[i];
					int nextY = currentY + yOffsets[i];

					if (nextX > -1 && nextY > -1 && nextX < width && nextY < height) {
						if (!visited[nextX][nextY] && map[nextX][nextY] != 2) {
							q.add(nextX);
							q.add(nextY);
							q.add((map[nextX][nextY] + 1) % 2);
							
							visited[nextX][nextY] = true;
							
							MapLocation nextLocation = new MapLocation(nextX, nextY);
							
							rc.broadcast(locToInt(nextLocation), nextLocation.directionTo(currentLocation).ordinal());
						}
					}
				}
			} else {
				q.add(currentX);
				q.add(currentY);
				q.add(timeLeft - 1);
			}
		}
	}
	
	public static void printDirectionalField() throws GameActionException {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				System.out.print(directionToChar(rc.readBroadcast(locToInt(new MapLocation(x,y)))));
			}
			System.out.println();
		}
	}
	
	public static void printMap() throws GameActionException {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				System.out.print(map[x][y]);
			}
			System.out.println();
		}
	}
	
	public static char directionToChar(int dir) {
		
		switch (dir) {
			case 0:
				return '>';
			case 1:
				return '?';
			case 2: 
				return '^';
			case 3:
				return '/';
			case 4:
				return '\\';
			case 5:
				return '?';
			case 6:
				return 'v';
			case 7:
				return '\\';
			case 8:
				return '/';
			case 9:
				return '<';
		}
		
		return '?';
		
	}
}
