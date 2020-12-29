package com.parkour.map;

import org.bukkit.entity.Player;

public class CommandTrigger {

	private Player player;
	private SavePoint point;
	
	public CommandTrigger(Player p, SavePoint point) {
		this.player = p;
		this.point = point;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public SavePoint getPoint() {
		return point;
	}
}
