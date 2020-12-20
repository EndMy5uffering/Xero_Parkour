package com.parkour.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;
import com.parkour.main.ParkourMain;
import com.parkour.main.ParkourManager;
import com.parkour.main.ParkourPerms;
import com.parkour.map.CommandUser;
import com.parkour.map.ParkourMap;
import com.parkour.map.SavePoint;
import com.parkour.player.ParkourPlayer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ParkourCommands implements TabExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String line, String[] args) {
		if(!(sender instanceof Player)) return true;
		Player p = (Player) sender;
		if(args.length < 1) {
			p.sendMessage(ChatColor.RED + "Missing arguments");
			p.sendMessage("/parkour <create | remove | join | leave | set | tp>");
			return true;
		}
		
		switch(args[0].toLowerCase()) {
		case "create":
			if(!hasPerm(p, ParkourPerms.create)) return true;
			if(args.length < 2) {
				p.sendMessage(ChatColor.RED + "Missing arguments");
				p.sendMessage("/parkour create <map|savepoint>");
				return true;
			}
			
			switch(args[1].toLowerCase()) {
			case "map":
				if(args.length < 5) {
					p.sendMessage(ChatColor.RED + "Missing arguments");
					p.sendMessage("/parkour create map <radius> <deathlvl> <name>");
					return true;
				}
				int r = 0;
				int deathLvl = 0;
				try {
					r = Integer.valueOf(args[2]);
					deathLvl = Integer.valueOf(args[3]);
				}catch(NumberFormatException e) {
					p.sendMessage(ChatColor.RED + "Number formating Exception!");
					p.sendMessage("/parkour create map <radius> <deathlvl> <name>");
					return true;
				}
				String name = args[4];
				if(name.length() > 16) {
					p.sendMessage(ChatColor.RED + "Name can not be longer then 16 characters!");
					return true;
				}
				CreateMap.create(p.getLocation(), r, deathLvl, name);
				p.sendMessage(ChatColor.GREEN + "New Parkour map created!");
				break;
			case "savepoint":
				if(args.length < 3) {
					p.sendMessage(ChatColor.RED + "Missing arguments");
					p.sendMessage("/parkour create savepoint <radius>");
					return true;
				}
				if(ParkourManager.getMap(p.getLocation()) == null) {
					p.sendMessage(ChatColor.RED + "You are not inside a Parkour map");
					p.sendMessage(ChatColor.RED + "To create a SavePoint you have to be inside a parkour map");
					return true;
				}
				int r2 = Integer.valueOf(args[2]);
				CreateSavePoint.create(ParkourManager.getMap(p.getLocation()), p.getLocation(), r2);
				p.sendMessage(ChatColor.GREEN + "New savepoint created!");
				break;
			default:
				p.sendMessage("/parkour create <map|savepoint> <radius> <deathlvl>");
				return true;
			}
			return true;
		case "remove":
			if(!hasPerm(p, ParkourPerms.remove)) return true;
			ParkourMap currentMap = null;
			SavePoint savePoint = null;
			if(args.length == 1) {
				currentMap = ParkourManager.getMap(p.getLocation());
				if(currentMap == null) {
					p.sendMessage(ChatColor.RED + "You are currently not inside a parkour map!");
					return true;
				}
				
				if(currentMap != null) {
					savePoint = currentMap.getSavePoint(p.getLocation());
				}
				
				if(savePoint != null) {
					ParkourManager.removeSavePoint(savePoint);
					p.sendMessage(ChatColor.GREEN + "The savepoint you were standing in was removed.");
					return true;
				}
				
				ParkourManager.removeMap(currentMap);
				p.sendMessage(ChatColor.GREEN + "The Parkourmap [" + ChatColor.GOLD + currentMap.getName() + ChatColor.GREEN + "] was removed.");
				return true;
				
			}else if(args.length < 3) {
				p.sendMessage(ChatColor.RED + "Missing arguments");
				p.sendMessage("/parkour remove <map|savepoint> <map|index>");
				return true;
			}
			
			switch(args[1].toLowerCase()) {
			case "map":
				
				currentMap = ParkourManager.getMapByName(args[2]);
				
				if(currentMap == null) {
					p.sendMessage(ChatColor.RED + "There is no parkour map with the name [" + args[2] + "]");
					return true;
				}else {
					ParkourManager.removeMap(currentMap);
					p.sendMessage(ChatColor.GREEN + "The Parkourmap [" + ChatColor.GOLD + currentMap.getName() + ChatColor.GREEN + "] was removed.");
				}
				
				return true;
			case "savepoint":
				currentMap = ParkourManager.getMap(p.getLocation());
				if(currentMap == null) {
					p.sendMessage(ChatColor.RED + "You are currently not inside a parkour map!");
					return true;
				}
				int index = 0;
				try {
					index = Integer.valueOf(args[2]);
				}catch(Exception e) {
					p.sendMessage(ChatColor.RED + "Save point index was not a number!");
					return true;
				}
				
				if(index > currentMap.getSaves().size()-1 || index < 0) {
					p.sendMessage(ChatColor.RED + "Save point index was out of range!");
					return true;
				}
				
				savePoint = currentMap.getSaves().get(index);
				ParkourManager.removeSavePoint(savePoint);
				p.sendMessage(ChatColor.GREEN + "Save point was removed.");
				return true;
				default:
					p.sendMessage(ChatColor.RED + "There is no such command!");
					return true;
			}
		case "set":
			if(!hasPerm(p, ParkourPerms.set)) return true;
			if(args.length < 2) {
				p.sendMessage(ChatColor.RED + "Missing arguments");
				p.sendMessage("/parkour set <start|end>");
				return true;
			}
			
			ParkourMap map = ParkourManager.getMap(p.getLocation());
			if(map == null) {
				p.sendMessage(ChatColor.RED + "To set a start or end point you have to stand inside a map!");
				return true;
			}
			
			switch(args[1].toLowerCase()) {
			
			case "radius":
				if(args.length < 3) {
					p.sendMessage(ChatColor.RED + "Missing arguments");
					p.sendMessage("/parkour set radius <radius>");
					return true;
				}
				
				SavePoint point = map.getSavePoint(p.getLocation());
				
				if(point != null) {
					int r = 0;
					try {
						r = Integer.valueOf(args[2]);
					} catch (Exception e) {
						p.sendMessage(ChatColor.RED + "Casting Exception");
						p.sendMessage(ChatColor.RED + "Change in savepoint");
						return true;
					}
					point.setRadius(r);
					ParkourMain.parkourMain.saves.UpdateRadiusSavePoint(point);
					p.sendMessage(ChatColor.GREEN + "Radius of "+ChatColor.GOLD+"[SavePoint]"+ChatColor.GREEN+" was changed to: "+ChatColor.GOLD+r);
				
					return true;
				}
				int r = 0;
				try {
					r = Integer.valueOf(args[2]);
				} catch (Exception e) {
					p.sendMessage(ChatColor.RED + "Casting Exception");
					p.sendMessage(ChatColor.RED + "Change in map");
					return true;
				}
				map.setRadius(r);
				ParkourMain.parkourMain.saves.UpdateRadiusMap(map);
				p.sendMessage(ChatColor.GREEN + "Radius of "+ChatColor.GOLD+"[Map]"+ChatColor.GREEN+" was changed to: "+ChatColor.GOLD+r);
							
				return true;
			case "start":
				UpdatePointsOfMap.updateStart(map, p.getLocation());
				p.sendMessage(ChatColor.GOLD + "New start for [" + ChatColor.BLUE + map.getName() + ChatColor.GOLD + "] was set");
				return true;
			case "end":	
				p.sendMessage(ChatColor.GOLD + "New end for [" + ChatColor.BLUE + map.getName() + ChatColor.GOLD + "] was set");
				UpdatePointsOfMap.updateEnd(map, p.getLocation());
				return true;
				default:
					p.sendMessage("/parkour set <start|end>");
					return true;
										
			}
		case "join":
			if(!hasPerm(p, ParkourPerms.join)) return true;
			if(args.length < 2) {
				p.sendMessage(ChatColor.RED + "Missing arguments");
				p.sendMessage("/parkour join <map>");
				return true;
			}
			if(ParkourManager.hasPlayerParkourData(p)) {
				p.sendMessage(ChatColor.RED + "You can not join more then one parkour at one time!");
				return true;
			}

			ParkourMap joinedMap = ParkourManager.getMapByName(args[1]);
			
			if(joinedMap == null) {
				p.sendMessage(ChatColor.RED + "There is no map named [" + ChatColor.GOLD + args[1] + ChatColor.RED +"]");
				return true;
			}
			ParkourPlayer parkourPlayer = new ParkourPlayer(p, joinedMap, p.getLocation());
			ParkourManager.addPlayerData(parkourPlayer);
			parkourPlayer.backToSpawn();
			p.sendMessage(ChatColor.GREEN + "You have joined the parkour [" + ChatColor.GOLD + joinedMap.getName() + ChatColor.GREEN + "] have fun.");
			p.sendMessage(ChatColor.YELLOW + "If you want to leave the parkour use:");
			textCommand(p, "[/parkour leave]", "/parkour leave", ChatColor.GOLD);
			return true;
		case "leave":
			if(!ParkourManager.hasPlayerParkourData(p)) {
				p.sendMessage(ChatColor.RED + "You are currently not registerd at a parkour!");
				return true;
			}
			
			ParkourPlayer playerData = ParkourManager.getPlayerData(p);
			playerData.returnFromWhenceTheeCamest();
			ParkourManager.removePlayerData(p);
			p.sendMessage(ChatColor.GREEN + "You are no longer taking part in the parkour.");
			
			return true;
			
		case "back":
			if(!ParkourManager.hasPlayerParkourData(p)) {
				p.sendMessage(ChatColor.RED + "You are currently not registerd at a parkour!");
				return true;
			}
			ParkourPlayer playerData2 = ParkourManager.getPlayerData(p);
			playerData2.returnToLastSavePoint();
			return true;
		case "list":
			if(!hasPerm(p, ParkourPerms.list)) return true;
			ParkourMap map2 = ParkourManager.getMap(p.getLocation());
			if(map2 == null) {
				p.sendMessage(ChatColor.GOLD + "List of all Maps:");
				
				for(ParkourMap m : ParkourManager.maps) {
					String c = "/parkour tp " + m.getName();
					textCommand(p, "[Map: " + m.getName() + "]", c, ChatColor.GOLD);
				}
				return true;
			}else {
				p.sendMessage(ChatColor.GOLD + "List of all SavePoints in map [" + map2.getName() + "]:");
				String tpstart = "/tp " + map2.getStart().getX() + " " + map2.getStart().getY() + " " + map2.getStart().getZ();
				textCommand(p, "[Map: " + map2.getName() + ", Point: Start]", tpstart, ChatColor.GREEN);
				String tpend = "/tp " + map2.getEnd().getX() + " " + map2.getEnd().getY() + " " + map2.getEnd().getZ();
				textCommand(p, "[Map: " + map2.getName() + ", Point: End]", tpend, ChatColor.RED);
				for(int i = 0; i < map2.getSaves().size(); i++) {
					String c = "/parkour tp " + map2.getName() + " " + i;
					textCommand(p, "[Map: " + map2.getName() + ", Point: " + i + "]", c, ChatColor.GOLD);
				}
				return true;
			}
			
		case "tp":
			if(!hasPerm(p, ParkourPerms.tp)) return true;
			if(args.length < 2) {
				p.sendMessage(ChatColor.RED + "Missing arguments");
				p.sendMessage("/parkour tp <map>");
				p.sendMessage("/parkour tp <map> <index>");
				p.sendMessage("/parkour tp <player> <x> <y> <z>");
				return true;
			}
			if(args.length == 3) {
				ParkourMap tpsptarget = ParkourManager.getMapByName(args[1]);
				SavePoint point = tpsptarget.getSaves().get(Integer.valueOf(args[2]));
				if(point == null) {
					p.sendMessage(ChatColor.RED + "This savepoint dose not exist!");
					return true;
				}
				Location loc = point.getPoint();
				p.teleport(loc);
				return true;
			}
			if(args.length == 2) {
				ParkourMap tptarget = ParkourManager.getMapByName(args[1]);
				p.teleport(tptarget.getStart());
				return true;
			}
			if(args.length == 5) {
				double x = 0;
				double y = 0; 
				double z = 0;
				String player = args[1];
				try {
					x = Double.valueOf(args[2]);
					y = Double.valueOf(args[3]);
					z = Double.valueOf(args[4]);
				} catch (Exception e) {
					p.sendMessage(e.toString());
					return true;
				}
				
				Player tpPlayer = ParkourMain.plugin.getServer().getPlayer(player);
				
				if(tpPlayer == null) {
					return true;
				}
				
				World w = p.getLocation().getWorld();
				Location tpto = new Location(w, x, y, z);
				tpPlayer(tpto, tpPlayer);
				return true;
			}
			p.sendMessage("/parkour tp <map>");
			p.sendMessage("/parkour tp <map> <index>");
			p.sendMessage("/parkour tp <player> <x> <y> <z>");
			return true;
		case "commands":
			if(!hasPerm(p, ParkourPerms.commands)) return true;
			if(args.length < 2) {
				p.sendMessage(ChatColor.RED + "Missing arguments");
				p.sendMessage("/parkour commands <add | remove | list>");
				return true;
			}
			CommandUser commanduser;
			ParkourMap current = ParkourManager.getMap(p.getLocation());
			if(current == null) {
				p.sendMessage(ChatColor.RED + "You are currently not inside a parkour map!");
				return true;
			}
			
			if(current.getSavePoint(p.getLocation()) != null) {
				commanduser = current.getSavePoint(p.getLocation());
			}else {
				commanduser = current;
			}
			
			switch(args[1].toLowerCase()) {
			case "add":
				if(args.length < 4) {
					p.sendMessage(ChatColor.RED + "Missing arguments");
					p.sendMessage("/parkour commands add <sender> [command]");
					return true;
				}
				String myCommandSender;
				
				if(args[2].toLowerCase().equals("player")) {
					myCommandSender = "player";
				}else if(args[2].toLowerCase().equals("console")) {
					myCommandSender = "console";
				}else {
					p.sendMessage(ChatColor.RED + "There is no sender: " + args[2]);
					return true;
				}
				
				String addedCommand = "";
				for(int i = 3; i < args.length; i++) {
					addedCommand += args[i] + " ";
				}
				addedCommand = addedCommand.substring(0, addedCommand.length()-1);
				try {
					com.parkour.map.Command mycommand = new com.parkour.map.Command(myCommandSender, addedCommand);
					commanduser.addCommand(mycommand);
					p.sendMessage(ChatColor.GREEN + "Command set: " + ChatColor.GOLD + addedCommand);
				}catch(UnsupportedOperationException exception) {
					p.sendMessage(ChatColor.RED + "Player command can not be executed by the console!");
					return true;
				}
				return true;
			case "remove":
				if(args.length < 2) {
					p.sendMessage(ChatColor.RED + "Missing arguments");
					p.sendMessage("/parkour remove <all|index>");
					return true;
				}
				
				switch(args[2].toLowerCase()) {
				case "all":
					
					commanduser.clearCommands();
					p.sendMessage(ChatColor.GREEN + "All commands where removed!");
					return true;
				case "index":
					if(args.length < 4) {
						p.sendMessage(ChatColor.RED + "Missing arguments");
						p.sendMessage("/parkour remove index <index>");
						return true;
					}
					
					try {
						int commandIndex = Integer.valueOf(args[3]);
						if(commandIndex < 0 || commandIndex > commanduser.getCommands().size()) {
							p.sendMessage(ChatColor.RED + "Index was out of range!");
							return true;
						}
						commanduser.removeCommand(commandIndex);
						p.sendMessage(ChatColor.GREEN + "Command was removed!");
						return true;
					}catch(NumberFormatException exception) {
						p.sendMessage(ChatColor.RED + "Index was not a number or out of range!");
						return true;
					}
					
					default:
						p.sendMessage(ChatColor.RED + "There is no such command!");
						return true;
				
				}
			case "list":
				p.sendMessage(ChatColor.GOLD + "#---Commands---#");
				List<com.parkour.map.Command> commandsList = commanduser.getCommands();
				for(int i = 0; i < commandsList.size(); i++) {
					commandsList.get(i).sendCommandText(p, i);
				}
				return true;
				default:
					p.sendMessage(ChatColor.RED + "There is no such command!");
					return true;
			}
		case "permissions":
			permissions(p);
			return true;
		default:
			p.sendMessage(ChatColor.RED + "There is no such command!");
			p.sendMessage("/parkour <create | remove | join | leave | set | tp>");
			return true;
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String line, String[] args) {
		if(!(sender instanceof Player)) return null;
		
		if(args.length == 1) {
			return Lists.newArrayList("create", "list", "commands", "remove" , "join", "leave", "tp", "back");
		}
		
		if(args.length == 2) {
			String arg0 = args[0].toLowerCase();
			List<String> arrayOut;
			switch(arg0) {
			case "create":
			case "remove":
				return Lists.newArrayList("map", "savepoint");
			case "set":
				return Lists.newArrayList("start", "end", "radius");
			case "commands":
				return Lists.newArrayList("add", "remove", "list");
			case "join":
				arrayOut = ParkourManager.maps.stream().map(x -> {
					return x.getName();
				}).sorted().collect(Collectors.toList());
				return arrayOut;
			case "tp":
				arrayOut = ParkourManager.maps.stream().map(x -> {
					return x.getName();
				}).sorted().collect(Collectors.toList());
				
				ParkourMain.plugin.getServer().getOnlinePlayers().stream().forEach(x -> {
					arrayOut.add(x.getName());
				});
				
				return arrayOut;
			default:
				return null;
			}
		}
		
		if(args.length == 3) {
			String arg0 = args[0].toLowerCase();
			List<String> arrayOut;
			switch(arg0) {
			case "create":
				return null;
			case "remove":
				return null;
			case "set":
				return null;
			case "join":
				return null;
			case "commands":
				switch(args[1].toLowerCase()) {
				case "add":
					return Lists.newArrayList("player", "console");
				case "remove":
					return Lists.newArrayList("all", "index");
				case "list":
					return null;
					default:
						return null;
				}
			case "tp":
				ParkourMap mapout = ParkourManager.getMapByName(args[1]);
				if(mapout == null) {
					return null;
				}
				arrayOut = new ArrayList<>();
				for(int i = 0; i < mapout.getSaves().size(); i++) {
					arrayOut.add("" + i);
				}
				return arrayOut;
			default:
				return null;
			}
		}
		
		if(args.length > 3) {
			String arg0 = args[0].toLowerCase();
			List<String> arrayOut;
			switch(arg0) {
			case "commands":
				switch(args[1].toLowerCase()) {
				case "add":
					return Lists.newArrayList("{PLAYER}");
				case "remove":
					return null;
				case "list":
					return null;
					default:
						return null;
				}
			default:
				return null;
			}
		}
		
		return null;
	}
	
	private void textCommand(Player p, String msg, String command, ChatColor c) {
		TextComponent message = new TextComponent(msg);
		message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		message.setColor(c);
		p.spigot().sendMessage(message);
	}

	private boolean hasPerm(Player p, String perm) {
		boolean out = p.hasPermission(perm);
		if(!out) p.sendMessage(ChatColor.RED + "You don't have permissin to use this command!");
		return out;
	}
	
	private void permissions(Player p) {
		p.sendMessage(ChatColor.GOLD + "---------------" + ChatColor.RED + "{Parkour Permissions}" + ChatColor.GOLD + "---------------");
		p.sendMessage(ChatColor.YELLOW + ParkourPerms.create);
		p.sendMessage(ChatColor.YELLOW + ParkourPerms.remove);
		p.sendMessage(ChatColor.YELLOW + ParkourPerms.signs);
		p.sendMessage(ChatColor.YELLOW + ParkourPerms.elytra);
		p.sendMessage(ChatColor.YELLOW + ParkourPerms.commands);
		p.sendMessage(ChatColor.YELLOW + ParkourPerms.set);
		p.sendMessage(ChatColor.YELLOW + ParkourPerms.list);
		p.sendMessage(ChatColor.YELLOW + ParkourPerms.tp);
		p.sendMessage(ChatColor.YELLOW + ParkourPerms.join);
		p.sendMessage(ChatColor.YELLOW + ParkourPerms.leave);
		p.sendMessage(ChatColor.GOLD + "=================================================");
	}
	
	public void tpPlayer(Location to, Player player) {
		Location l = player.getLocation();
		float pitch = l.getPitch();
		float yaw = l.getYaw();
		to.setPitch(pitch);
		to.setYaw(yaw);
		player.teleport(to);
		player.setVelocity(new Vector(0,0,0));
		player.setFallDistance(0);
	}
	
}
