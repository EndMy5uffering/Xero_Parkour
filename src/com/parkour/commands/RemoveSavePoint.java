package com.parkour.commands;

import org.bukkit.Location;

import com.parkour.main.ParkourManager;
import com.parkour.map.ParkourMap;
import com.parkour.map.SavePoint;

public class RemoveSavePoint {

	public static void remove(ParkourMap map, Location loc) {
		SavePoint point = map.getSavePoint(loc);
		ParkourManager.removeSavePoint(point);
	}
	
}
