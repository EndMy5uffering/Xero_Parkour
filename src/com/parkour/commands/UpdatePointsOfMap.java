package com.parkour.commands;

import org.bukkit.Location;

import com.parkour.main.ParkourMain;
import com.parkour.map.ParkourMap;

public class UpdatePointsOfMap {

	public static void updateStart(ParkourMap map, Location start) {
		map.setStart(start);
		ParkourMain.parkourMain.saves.UpdateStartOfMap(map);
	}
	
	public static void updateEnd(ParkourMap map, Location end) {
		map.setEnd(end);
		ParkourMain.parkourMain.saves.UpdateEndOfMap(map);
	}
	
}
