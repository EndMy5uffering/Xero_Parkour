package com.parkour.commands;

import org.bukkit.Location;

import com.parkour.main.ParkourManager;
import com.parkour.map.ParkourMap;
import com.parkour.map.SavePoint;

public class CreateSavePoint {

	public static void create(ParkourMap map, Location loc, int radius) {
		SavePoint point = new SavePoint(loc, radius, map.getId());
		ParkourManager.addSavePoint(point);
	}
	
}
