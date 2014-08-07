package Micromania;

import java.util.*;

import battlecode.common.*;

public class RobotPlayer {

	public static RobotController rc;
	public static RobotType type;

	public static void run(RobotController rcin) {
		try {
			rc = rcin;
			type = rc.getType();
			init();

			while(true) {			
				if (type == RobotType.SOLDIER) {
					Soldier.run();
				} else if (type == RobotType.HQ) {
					HQ.run();
				} else if (type == RobotType.NOISETOWER) {

				} else if (type == RobotType.PASTR) {

				}

				rc.yield();
			}

		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
	
	public static void init() throws GameActionException {
		MapData.createMap();
		if (type == RobotType.HQ){
			HQ.init();
		}
	}

}

