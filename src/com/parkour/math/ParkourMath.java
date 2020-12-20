package com.parkour.math;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class ParkourMath {
	
	public static Vector getVectorBetween(Vector l1, Vector l2) {
		return new Vector(l1.getX() - l2.getX(),
				l1.getY() - l2.getY(),
				l1.getZ() - l2.getZ());
	}
	
	public static Vector getVectorBetween(Location l1, Location l2) {
		return new Vector(l1.getX() - l2.getX(),
				l1.getY() - l2.getY(),
				l1.getZ() - l2.getZ());
	}
	
	public static double getVectorLength(Vector v) {
		return Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ());
	}

	public static double distance(Location l1, Location l2) {
		return getVectorLength(getVectorBetween(l1, l2));
	}

	public static double distancexz(Location l1, Location l2) {
		Vector v = getVectorBetween(l1, l2);
		return Math.sqrt((v.getX()*v.getX()) + (v.getZ()*v.getZ()));
	}
	
}
