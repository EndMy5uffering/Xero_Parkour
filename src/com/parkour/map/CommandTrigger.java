package com.parkour.map;

import org.bukkit.entity.Player;

public class CommandTrigger {

	private Player player;
	
	public CommandTrigger(Player p) {
		this.player = p;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
}
