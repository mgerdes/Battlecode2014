package pathFind;

import battlecode.common.*;

import java.util.*;

public class RobotPlayer {
	
	static RobotController rc;
	static Direction allDirections[] = Direction.values();
	static int map[][];
	static boolean foundPath;
	static int path[][];
	
	public static void run (RobotController rcin) {
		rc = rcin;
		
		while (true) {
			try {
				if (rc.getType() == RobotType.SOLDIER) {
					
					if (rc.isActive()) {
						
						if (shoot());
						else {
							if (isHQPathFound()) {
								if (rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()) < 50) {
									if (rc.senseNearbyGameObjects(Robot.class, 100, rc.getTeam()).length > 4) {
										if (moveWhereHQWants());
										else {
											moveRandomly();
										}
									}
								} else {
									if (moveWhereHQWants());
									else {
										moveRandomly();
									}	
								}
							} else {
								moveRandomly();							
							}
						}
					}
				} else if (rc.getType() == RobotType.HQ){
					if (rc.isActive() && rc.canMove(Direction.NORTH) && rc.senseRobotCount() < GameConstants.MAX_ROBOTS){
						rc.spawn(Direction.NORTH);
					}
					
					if (map == null) {
						createMap();
						setHQPathToNotFound();
					}
					
					if (rc.readBroadcast(10000) == -1) {
						bfs(rc.senseEnemyHQLocation());
						setHQPathToFound();
					}
				}
			} catch (GameActionException e) {
				e.printStackTrace();
			}
			rc.yield();
		}
	}
	
	public static void move(MapLocation location) throws GameActionException {
		if (!rc.isActive()) {
			return;
		}
		
		Direction movingDirection = rc.getLocation().directionTo(location);
		if (rc.canMove(movingDirection)) {
			rc.move(movingDirection);
		}	
	}

	public static boolean shoot() throws GameActionException {
		if (rc.isActive()) {
			Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class, rc.getType().attackRadiusMaxSquared + 10, rc.getTeam().opponent());
			if (enemyRobots.length > 0) {
				Robot anEnemy = enemyRobots[rc.getRobot().getID() % enemyRobots.length];
				RobotInfo anEnemyInfo = rc.senseRobotInfo(anEnemy);
				if (anEnemyInfo.location.distanceSquaredTo(rc.getLocation()) < rc.getType().attackRadiusMaxSquared) {
					rc.attackSquare(anEnemyInfo.location);
					return true;
				} else {
					move(anEnemyInfo.location);
					return true;
				}
			}
		}
		return false;
	}

	
	public static void bugMove(MapLocation goal) throws GameActionException {
		createMap();
		if (rc.isActive()) {
			Direction directionToGoal = rc.getLocation().directionTo(goal);
			if (rc.canMove(directionToGoal)) {
				// move in direction of goal.
				rc.move(directionToGoal);
			} else {
				// follow obstacle
				MapLocation myLocation = rc.getLocation();
				
				int north = map[myLocation.x][myLocation.y - 1];
				int south = map[myLocation.x][myLocation.y - 1];
				int west = map[myLocation.x - 1][myLocation.y];
				int east = map[myLocation.x + 1][myLocation.y];
				
				
			}
		}
	}
	
	public static void setSoldierPathToNotFound() throws GameActionException {
		foundPath = false;
	}
	
	public static void setSoldierPathToFound() throws GameActionException {
		foundPath = true;
	}
	
	public static boolean isSoldierPathFound() throws GameActionException {
		return foundPath;
	}
	
	public static void setHQPathToNotFound() throws GameActionException {
		rc.broadcast(10000, -1);
	}
	
	public static void setHQPathToFound() throws GameActionException {
		rc.broadcast(10000, 1);
	}
	
	public static boolean isHQPathFound() throws GameActionException {
		if (rc.readBroadcast(10000) == -1) {
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean moveWhereHQWants() throws GameActionException {
		Direction movingDirection = allDirections[rc.readBroadcast(locToInt(rc.getLocation()))];
		if (rc.canMove(movingDirection)) {
			rc.move(movingDirection);
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean moveWhereSoldierWants() throws GameActionException {
		Direction movingDirection = allDirections[path[rc.getLocation().x][rc.getLocation().y]];
		if (rc.canMove(movingDirection)) {
			rc.move(movingDirection);
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean moveRandomly() throws GameActionException {
		Direction randomDirection = allDirections[(int)(Math.random() * 8)];
		if (rc.canMove(randomDirection)) {
			rc.move(randomDirection);
			return true;
		} else {
			return false;
		}
	}
	
	public static void createMap() {
		if (map == null) {
			int height = rc.getMapHeight();
			int width = rc.getMapWidth();
			
			map = new int[width][height];
			
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					map[x][y] = rc.senseTerrainTile(new MapLocation(x,y)).ordinal(); // 2 can't pass, 1 is a road, 0 can pass
				}
			}
		}
	}
	
	public static int locToInt(int x, int y) {
		return x * 100 + y;
	}
	
	public static int locToInt(MapLocation location) {
		return location.x * 100 + location.y;
	}
	
	public static MapLocation intToLoc (int location) {
		return new MapLocation(location / 100, location % 100);
	}
	
	public static void bfsTo(MapLocation startLocation, MapLocation endLocation) throws GameActionException {
		int width = rc.getMapWidth();
		int height = rc.getMapHeight();

		if (map == null) {
			createMap();
		}
		if (path == null) {
			path = new int[width][height];
		}
				
		int startX = startLocation.x;
		int startY = startLocation.y;
		int endX = endLocation.x;
		int endY = endLocation.y;
		
		int xOffsets[] = {-1, 0, 1, 0, -1, 1, 1, -1};
		int yOffsets[] = {0, -1, 0, 1, 1, -1, 1, -1};
		
		LinkedList<Integer> q = new LinkedList<Integer>();
		boolean visited[][] = new boolean[width][height];
		int parent[][][] = new int[width][height][2];
		
		for (int i = 0; i < visited.length; i++) {
			for (int j = 0; j < visited[0].length; j++) {
				visited[i][j] = false;
			}
		}
		
		q.add(startX);
		q.add(startY);
		visited[startX][startY] = true;
		
		while(!q.isEmpty()) {
			int currentX = q.poll();
			int currentY = q.poll();
			
			if (currentX == endX && currentY == endY) {
				break;
			}
			
			for (int i = 0; i < 8; i++) {
				int nextX = currentX + xOffsets[i];
				int nextY = currentY + yOffsets[i];
				
				if (nextX > -1 && nextX < width && nextY > -1 && nextY < width) {
					if (map[nextX][nextY] != 2) {
						if (!visited[nextX][nextY]) {
							visited[nextX][nextY] = true;
							
							q.add(nextX);
							q.add(nextY);
																					
							parent[nextX][nextY][0] = currentX;
							parent[nextX][nextY][1] = currentY;
						}
					}
				}
			}
		}
		
		int currentX = endX;
		int currentY = endY;
		
		do {
			MapLocation currentLocation = new MapLocation(currentX, currentY);
			
			int parentX = parent[currentX][currentY][0];
			int parentY = parent[currentX][currentY][1];
			MapLocation parentLocation = new MapLocation(parentX, parentY);
			
			path[parentX][parentY] = parentLocation.directionTo(currentLocation).ordinal();
			
			currentX = parentX;
			currentY = parentY;
		} while (currentX != startX && currentY != startY);
	}
	
	public static void bfs(MapLocation location) throws GameActionException {
		int width = rc.getMapWidth();
		int height = rc.getMapHeight();
		
		int startX = location.x;
		int startY = location.y;
		
		int xOffsets[] = {-1, 0, 1, 0, -1, 1, 1, -1};
		int yOffsets[] = {0, -1, 0, 1, 1, -1, 1, -1};
		
		LinkedList<Integer> q = new LinkedList<Integer>();
		boolean visited[][] = new boolean[width][height];
		
		for (int i = 0; i < visited.length; i++) {
			for (int j = 0; j < visited[0].length; j++) {
				visited[i][j] = false;
			}
		}
		
		q.add(startX);
		q.add(startY);
		q.add(0);
		visited[startX][startY] = true;
		
		while (!q.isEmpty()) {
			int currentX = q.poll();
			int currentY = q.poll();
			int time = q.poll();
			
			MapLocation currentLocation = new MapLocation(currentX, currentY);
			
			if (time == 0) {
				for (int i = 0; i < 8; i++) {
					int nextX = currentX + xOffsets[i];
					int nextY = currentY + yOffsets[i];
					
					if (nextX > -1 && nextX < width && nextY > -1 && nextY < height) {
						if (map[nextX][nextY] != 2) {
							if (!visited[nextX][nextY]) {
								visited[nextX][nextY] = true;
								
								q.add(nextX);
								q.add(nextY);
								
								if (map[nextX][nextY] == 1) {
									q.add(0);
								} else {
									q.add(1);
								}
															
								MapLocation nextLocation = new MapLocation(nextX, nextY);
															
								rc.broadcast(locToInt(nextLocation), nextLocation.directionTo(currentLocation).ordinal());
							}
						}
					}
				}
			} else {
				q.add(currentX);
				q.add(currentY);
				q.add(time - 1);
			}
		}		
	}

}
