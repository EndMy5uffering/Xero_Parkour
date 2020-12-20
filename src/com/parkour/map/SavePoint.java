package com.parkour.map;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

import com.parkour.main.ParkourMain;
import com.parkour.math.ParkourMath;

public class SavePoint implements CommandUser{

	private final Location point;
	private int radius;
	private final String mapID;
	private final String pointID;
	private String parmas;
	private List<Command> commands = new ArrayList<>();
	
	public SavePoint(Location point, int radius, String mapID) {
		this.point = point;
		this.radius = radius;
		this.mapID = mapID;
		this.pointID = UUID.randomUUID().toString();
	}
	
	public SavePoint(Location point, int radius, String mapID, String pointID) {
		this.point = point;
		this.radius = radius;
		this.mapID = mapID;
		this.pointID = pointID;
	}
	
	public boolean playerInSavePoint(Location loc) {
		return ParkourMath.distance(loc, point) <= radius;
	}

	public Location getPoint() {
		return point;
	}

	public int getRadius() {
		return radius;
	}

	public String getMapID() {
		return mapID;
	}

	public String getPointID() {
		return pointID;
	}
	
	public void addCommand(Command cmd) {
		this.commands.add(cmd);
		ParkourMain.parkourMain.saves.addCommand(this.mapID, this.pointID, cmd.getCommand(), cmd.getExecutor());
	}
	
	public void addCommandNoSave(Command cmd) {
		this.commands.add(cmd);
	}
	
	public void clearCommands() {
		for(Command c : commands) {
			ParkourMain.parkourMain.saves.removeCommand(this.mapID, this.pointID, c.getCommand());
		}
		this.commands.clear();
	}
	
	public Command getCommand(int i) {
		return commands.get(i);
	}
	
	public Command getCommand(String command) {
		for(Command c : commands) {
			if(c.getCommand().equals(command)) return c;
		}
		return null;
	}
	
	public void removeCommand(Command cmd) {
		ParkourMain.parkourMain.saves.removeCommand(this.mapID, this.pointID, cmd.getCommand());
		this.commands.remove(cmd);
	}
	
	public void removeCommand(int i) {
		ParkourMain.parkourMain.saves.removeCommand(this.mapID, this.pointID, this.commands.get(i).getCommand());
		this.commands.remove(i);
	}

	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}

	public String getParmas() {
		return parmas;
	}

	public void setParmas(String parmas) {
		this.parmas = parmas;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}
	
}
