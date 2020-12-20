package com.parkour.map;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

import com.parkour.main.ParkourMain;
import com.parkour.math.ParkourMath;
import com.parkour.player.ParkourPlayer;

public class ParkourMap implements CommandUser{

	private Location center;
	private Location start;
	private Location end;
	private final int endRadius = 3;
	private int radius;
	private List<SavePoint> saves = new ArrayList<>();
	private List<Command> commands = new ArrayList<>();
	private final String id;
	private final int deathLvl;
	private final String name;
	
	public ParkourMap(Location center, String name, int radius, int deathLvl) {
		this.center = center;
		this.start = center;
		this.end = center;
		this.radius = radius;
		this.id = UUID.randomUUID().toString();
		this.deathLvl = deathLvl;
		this.name = name;
	}
	
	public ParkourMap(Location center, Location start, Location end, String name, int radius, int deathLvl, String uuid) {
		this.center = center;
		this.start = start;
		this.end = end;
		this.radius = radius;
		this.id = uuid;
		this.deathLvl = deathLvl;
		this.name = name;
	}
	
	public void addSavePoint(SavePoint point) {
		saves.add(point);
	}
	
	public void removeSavePoint(SavePoint point) {
		saves.remove(point);
	}
	
	public SavePoint getSavePoint(Location point) {
		for(SavePoint p : saves) {
			double dist = ParkourMath.distance(point, p.getPoint());
			if(dist <= p.getRadius()) return p;
		}
		return null;
	}
	
	public void checkSavePointIntersect(ParkourPlayer p) {
		for(SavePoint point : saves) {
			if(point.playerInSavePoint(p.getPlayer().getLocation())) p.setPoint(point);
		}
	}
	
	public boolean endReached(ParkourPlayer p) {
		double dist = ParkourMath.distance(end, p.getPlayer().getLocation());
		return dist <= endRadius;
	}
	
	public boolean playerInMap(ParkourPlayer p) {
		if(p.getPlayer().getLocation().getY() < 0) return true;
		return ParkourMath.distancexz(p.getPlayer().getLocation(), center) <= radius;
	}
	
	public void addCommand(Command cmd) {
		this.commands.add(cmd);
		ParkourMain.parkourMain.saves.addCommand(id, "null", cmd.getCommand(), cmd.getExecutor());
	}
	
	public void addCommandNoSave(Command cmd) {
		this.commands.add(cmd);
	}
	
	public void clearCommands() {
		for(Command c : commands) {
			ParkourMain.parkourMain.saves.removeCommand(id, "null", c.getCommand());
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
		this.commands.remove(cmd);
		ParkourMain.parkourMain.saves.removeCommand(id, "null", cmd.getCommand());
	}
	
	public void removeCommand(int i) {
		ParkourMain.parkourMain.saves.removeCommand(id, "null", this.commands.get(i).getCommand());
		this.commands.remove(i);
	}

	public List<SavePoint> getSaves() {
		return saves;
	}

	public void setSaves(List<SavePoint> saves) {
		this.saves = saves;
	}

	public Location getCenter() {
		return center;
	}

	public int getRadius() {
		return radius;
	}
	
	public String getId() {
		return id;
	}

	public int getDeathLvl() {
		return deathLvl;
	}

	public Location getStart() {
		return start;
	}

	public Location getEnd() {
		return end;
	}

	public void setCenter(Location center) {
		this.center = center;
	}

	public void setStart(Location start) {
		this.start = start;
	}

	public void setEnd(Location end) {
		this.end = end;
	}

	public String getName() {
		return name;
	}

	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

}
