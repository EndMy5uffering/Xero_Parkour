package com.parkour.commands;

import org.bukkit.Location;

import com.parkour.main.ParkourManager;
import com.parkour.map.ParkourMap;

public class CreateMap {

	public static void create(Location center, int radius, int deathLvl, String name) {
		ParkourMap map = new ParkourMap(center, name, radius, deathLvl);
		ParkourManager.addMap(map);
	}
	
}
