package MarkyMark;

import java.util.*;
import battlecode.common.*;

public class RobotPlayer {
	
	public static RobotController rc;
	
	public static void run (RobotController rcin) {
		rc = rcin;
		
		try {
			init();
			while (true) {
				RobotType type = rc.getType();
				if (type == RobotType.SOLDIER){
					Soldier.run();
				} else if (type == RobotType.HQ){
					HQ.run();
				} else if (type == RobotType.NOISETOWER) {
					NoiseTower.run();
				} else if (type == RobotType.PASTR){
					Pastr.run();
				}
				rc.yield();
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
	
	public static void init() throws GameActionException {
		Movement.createMap();
		if (rc.getType() == RobotType.HQ) {
			PathFind.setHQPathNotFound(0);
			PathFind.setHQPathNotFound(1);
			PathFind.setHQPathNotFound(2);
			PathFind.setHQPathNotFound(3);
		}
	}

}