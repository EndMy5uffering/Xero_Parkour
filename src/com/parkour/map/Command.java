package com.parkour.map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.parkour.main.ParkourMain;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Command {

	private final String command;
	private final String executor;
	
	public Command(String executor, String command) throws UnsupportedOperationException{
		if(executor.equals("player") && command.contains("{PLAYER}")) throw new UnsupportedOperationException("Sender can not be console for a player command");
		this.executor = executor;
		this.command = command.replace("/", "");
	}
	
	public Command(String command) throws UnsupportedOperationException{
		if(command.contains("{PLAYER}")) throw new UnsupportedOperationException("Sender can not be console for a player command");
		this.command = command.replace("/", "");
		this.executor = "console";
	}
	
	public void execute(CommandTrigger t) {
		ParkourMain.plugin.getServer().dispatchCommand(commandSender(t), doReplacements(t));
	}
	
	private CommandSender commandSender(CommandTrigger t) {
		if(executor.equals("player")) {
			return t.getPlayer();
		}else if(executor.equals("console")) {
			return ParkourMain.plugin.getServer().getConsoleSender();
		}else {
			return ParkourMain.plugin.getServer().getConsoleSender();
		}
	}

	public String getCommand() {
		return command;
	}
	
	private String doReplacements(CommandTrigger t) {
		return command.replace("{PLAYER}", t.getPlayer().getName());
	}
	
	public void sendCommandText(Player p, int index) {
		TextComponent cmd = new TextComponent("-" + command + "-");
		TextComponent execute = new TextComponent("[Execute]");
		execute.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/"+command));
		execute.setColor(ChatColor.GREEN);
		TextComponent remove = new TextComponent("[Remove]");
		remove.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/parkour commands remove index " + index));
		remove.setColor(ChatColor.RED);
		TextComponent space = new TextComponent(" ");
		p.spigot().sendMessage(cmd, space, execute, space, remove);
	}

	public String getExecutor() {
		return executor;
	}
	
}
