package com.parkour.commands;

import com.parkour.main.ParkourManager;
import com.parkour.map.ParkourMap;

public class RemoveMap {

	public static void remove(ParkourMap map) {
		ParkourManager.removeMap(map);
	}
	
}
