package MicromaniaIsBadButIsNowGoodMaybe;

public class Channels {
	
	// 0 - 10000 is reserved for path to friendly pastr.
	// 10000 - 20000 is reserved for path 2 to friendly pastr.
	// 20000 - 30000 is reserved for path to enemy pastr.
	
	// 30000 - 40000 is reserved for nothing.
	// 40000 - 50000 is reserved for nothing.
	
	// 50000 - 50100 is reserved for enemy soldier infos.
	public static final int ENEMY_LOCATION_CHANNEL = 50000;
	
	// these channels store whether path 1,2,3,4 have been found.
	public static final int PATH_FOUND_CHANNEL[] = {60000, 60001, 60002, 60003};
	// these channels store the locations path 1,2,3,4 are going to.
	public static final int PATH_LOCATION_CHANNEL[] = {60004, 60005, 60006, 60007};
	
	// these channels store whether friendly pastrs and towers are build. Are updated when pastr/tower is build and then 
	// every 50 rounds.
	static final int IS_PASTR_BUILT_CHANNEL = 61000;
	static final int IS_TOWER_BUILT_CHANNEL = 61001;
	
	static final int ENEMY_PASTR_COUNT = 61002;
	
	static final int ENEMY_TO_ATTACK_LOCATION_CHANNEL = 62000;

}
