package com.parkour.player;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.parkour.main.ParkourManager;
import com.parkour.map.Command;
import com.parkour.map.CommandTrigger;
import com.parkour.map.ParkourMap;
import com.parkour.map.SavePoint;

import net.md_5.bungee.api.ChatColor;

public class ParkourPlayer {

	private final Player player;
	private final Location joinedFrom;
	
	private Set<SavePoint> oldPoints = new HashSet<>();
	
	private SavePoint point = null;
	private final ParkourMap map;
	
	public ParkourPlayer(Player p, ParkourMap map, Location joinedFrom) {
		
		this.joinedFrom = joinedFrom;
		this.player = p;
		this.map = map;
		
	}

	public SavePoint getPoint() {
		return point;
	}

	public void setPoint(SavePoint point) {
		if(point == this.point || oldPoints.contains(point)) return;
		this.point = point;
		oldPoints.add(point);
		for(Command c : point.getCommands()) {
			c.execute(new CommandTrigger(getPlayer(), point));
		}
		getPlayer().sendMessage(ChatColor.GREEN + "You have reached a check point.");
	}
	
	public void endReached() {
		for(Command c : map.getCommands()) {
			c.execute(new CommandTrigger(getPlayer(), new SavePoint(map.getEnd(), 2, map.getId())));
		}
		getPlayer().sendMessage(ChatColor.GREEN + "You have reached the end.");
		ParkourManager.removePlayerData(this.getPlayer());
	}

	public Player getPlayer() {
		return player;
	}

	public ParkourMap getMap() {
		return map;
	}
	
	public boolean InDeathZone() {
		if(map.getDeathLvl() < 0) return false;
		return player.getLocation().getY() < map.getDeathLvl();
	}
	
	public boolean hasSavePoint() {
		return this.point != null;
	}
	
	public void returnToLastSavePoint() {
		tpPlayer(this.point.getPoint());
	}
	
	public void backToSpawn() {
		tpPlayer(map.getStart());
	}
	
	public void returnFromWhenceTheeCamest() {
		tpPlayer(joinedFrom);
	}
	
	public void tpPlayer(Location to) {
		Location l = player.getLocation();
		float pitch = l.getPitch();
		float yaw = l.getYaw();
		to.setPitch(pitch);
		to.setYaw(yaw);
		player.teleport(to);
		player.setVelocity(new Vector(0,0,0));
		player.setFallDistance(0);
		player.setFireTicks(0);
	}
	
	public Location getRespawnLocation() {
		if(this.point == null) {
			return this.map.getStart();
		}
		return this.point.getPoint();
	}
	
}
