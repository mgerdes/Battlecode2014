package pathFind;

import battlecode.common.*;

import java.util.*;

public class RobotPlayer {
	
	public static RobotController rc;
	public static int[][] map;
	public static int width;
	public static int height;
	public static Direction[] directions = Direction.values();
	
	public static final int HQ_PATH_CHANNEL[] = {60000, 60001, 60002};
		
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
		shoot();
		if (isHQPathFound(1)) {
			moveWhereHQWants(1);
		} else if (isHQPathFound(0)) {
			moveWhereHQWants(0);
		} else {
			dumbMoveTo(rc.senseEnemyHQLocation());
		}
	}

	public static void runHQ() throws GameActionException {
		spawn();
		shoot();
		
		if (map == null) {
			createMap();
//			printMap();
			setHQPathNotFound(0);
			setHQPathNotFound(1);
			setHQPathNotFound(2);
		}

		if (!isHQPathFound(0)) {
			findHQPathTo(rc.senseEnemyHQLocation(), 0);
//			printDirectionalField();
			setHQPathFound(0);
		}

		//if (!isHQPathFound(1)) {
			//findHQPathTo(new MapLocation(width / 2, height / 2), 1);
			//setHQPathFound(1);
		//}

//		if (!isHQPathFound(2)) {
//			findHQPathTo(optimalCowLocation(), 2);
//			setHQPathFound(2);
//		}
	}
	
	public static MapLocation optimalCowLocation() throws GameActionException {
		int optimalX = 0;
		int optimalY = 0;
		double mostCows = rc.senseCowsAtLocation(new MapLocation(optimalX, optimalY));
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double cowCount = rc.senseCowsAtLocation(new MapLocation(x ,y));
				if (cowCount > mostCows) {
					optimalX = x;
					optimalY = y;
					mostCows = cowCount;
				}
			}
		}
		
		return new MapLocation(optimalX, optimalY);
	}
	
	public static void shoot() throws GameActionException {
		Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, RobotType.SOLDIER.attackRadiusMaxSquared, rc.getTeam().opponent());
		if (enemies.length > 0) {
			if (rc.isActive()) {
				RobotInfo robotToAttack = rc.senseRobotInfo(enemies[(int)(Math.random() * enemies.length)]);
				if (robotToAttack.type != RobotType.HQ) {
					MapLocation attackLocation = robotToAttack.location;
					if (rc.canAttackSquare(attackLocation)) {
						rc.attackSquare(attackLocation);
					} 
				}
			}
		}
	}
	
	public static void dumbMoveTo(MapLocation location) throws GameActionException {
		Direction movingDirection = rc.getLocation().directionTo(location);
		if (move(movingDirection));
		else {
				moveTowards(location);
		}
	}
	
	public static void moveTowards(MapLocation location) throws GameActionException {
		Direction direction1 = rc.getLocation().directionTo(location).rotateLeft();
		Direction direction2 = rc.getLocation().directionTo(location).rotateRight();
		Direction direction3 = direction2.rotateRight();
		Direction direction4 = direction3.rotateRight();
		
		if (move(direction1));
		else if (move(direction2));
		else if (move(direction3));
		else if (move(direction4));
		else moveRandomly();
	}
	
	public static boolean move(Direction direction) throws GameActionException {
		if (rc.isActive() && rc.canMove(direction)) {
			rc.move(direction);
			return true;
		} else {
			return false;
		}
	}

	public static void moveRandomly() throws GameActionException {
		Direction movingDirection = directions[(int)(Math.random() * 8)];
		move(movingDirection);
	}
	
	public static void moveWhereHQWants(int pathNum) throws GameActionException {
		Direction movingDirection = directions[rc.readBroadcast(getBroadcastPathChannelNum(rc.getLocation(), pathNum))];
		
		if (move(movingDirection));
		else {
			moveRandomly();
		}
	}
	
	public static void spawn() throws GameActionException {
		if (rc.isActive() && rc.canMove(Direction.NORTH)){
			rc.spawn(Direction.NORTH);
		}
	}

	public static void createMap() throws GameActionException {
		map = new int[width][height];
		
//		for (int x = 0; x < width; x++) {
//			for (int y = 0; y < height; y++) {
//				int tile = rc.senseTerrainTile(new MapLocation(x, y)).ordinal(); 
//				map[x][y] = tile;
//			}
//		}
		
		int startX = 0;
		int startY = height - 1;
		
		for (int y = startY; y > -1; y--, startX++) {
			for (int x = startX; x < width; x++) {
				int tile = rc.senseTerrainTile(new MapLocation(x, y)).ordinal();
				map[x][y] = tile;
				map[Math.abs(x - width) - 1][Math.abs(y - height) - 1] = tile;
			}
		}

	}

	public static void setHQPathNotFound(int pathNum) throws GameActionException {
		rc.broadcast(HQ_PATH_CHANNEL[pathNum], -1);
	}

	public static void setHQPathFound(int pathNum) throws GameActionException {
		rc.broadcast(HQ_PATH_CHANNEL[pathNum], 1);
	}

	public static boolean isHQPathFound(int pathNum) throws GameActionException {
		return rc.readBroadcast(HQ_PATH_CHANNEL[pathNum]) == 1;
	} 

	public static int locToInt(MapLocation location) throws GameActionException {
		return location.x * 100 + location.y;
	}

	public static MapLocation intToLoc(int location) throws GameActionException {
		return new MapLocation(location / 100, location % 100);
	}
	
	public static int getBroadcastPathChannelNum(MapLocation location, int pathNum) throws GameActionException {
		return pathNum * 10000 + locToInt(location);
	}

	public static void findHQPathTo(MapLocation destination, int pathNum) throws GameActionException {
		int startX = destination.x;
		int startY = destination.y;

		int xOffsets[] = {0, 1,  0, -1, -1, -1,  1,  1};
		int yOffsets[] = {1, 0, -1,  0, -1,  1, -1, -1};

		boolean visited[][] = new boolean[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				visited[x][y] = false;
			}
		}
		
		Queue q = new Queue(2 * width * height);
		
		q.enqueue(startX);
		q.enqueue(startY);
		q.enqueue(0);

		while (!q.isEmpty()) {
			int currentX = q.dequeue();
			int currentY = q.dequeue();
			int timeLeft = q.dequeue();
			
			MapLocation currentLocation = new MapLocation(currentX, currentY);

			if (timeLeft == 0) {
				for (int i = 0; i < 8; i++) {
					spawn();
					
					int nextX = currentX + xOffsets[i];
					int nextY = currentY + yOffsets[i];

					if (nextX > -1 && nextY > -1 && nextX < width && nextY < height) {
						if (!visited[nextX][nextY] && map[nextX][nextY] != 2) {
							q.enqueue(nextX);
							q.enqueue(nextY);
							q.enqueue((map[nextX][nextY] + 1) % 2);
							
							visited[nextX][nextY] = true;
							
							MapLocation nextLocation = new MapLocation(nextX, nextY);
							
							rc.broadcast(getBroadcastPathChannelNum(nextLocation, pathNum), nextLocation.directionTo(currentLocation).ordinal());
						}
					}
				}
			} else {
				q.enqueue(currentX);
				q.enqueue(currentY);
				q.enqueue(timeLeft - 1);
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
