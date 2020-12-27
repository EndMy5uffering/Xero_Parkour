package com.parkour.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.parkour.map.ParkourMap;
import com.parkour.map.SavePoint;
import com.parkour.math.ParkourMath;
import com.parkour.player.ParkourPlayer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ParkourManager implements Listener{

	public static Map<Player, ParkourPlayer> players = new HashMap<>();
	public static Set<ParkourMap> maps = new HashSet<>();
	public static Map<String, ParkourMap> nameMap = new HashMap<>();
	
	public static ParkourMap getMapByUUID(String uuid) {
		for(ParkourMap m : maps) {
			if(m.getId().equals(uuid)) return m;
		}
		return null;
	}
	
	public static ParkourMap getMapByName(String Name) {
		return nameMap.get(Name);
	}
	
	public static ParkourMap getMap(Location point) {
		for(ParkourMap m : maps) {
			double dist = ParkourMath.distancexz(m.getCenter(), point);
			if(dist <= m.getRadius()) return m;
		}
		return null;
	}
	
	public static void addMap(ParkourMap map) {
		maps.add(map);
		nameMap.put(map.getName(), map);
		ParkourMain.parkourMain.saves.addMap(map);
	}
	
	public static void addMapNoSave(ParkourMap map) {
		maps.add(map);
		nameMap.put(map.getName(), map);
	}
	
	public static void removeMap(ParkourMap map) {
		for(int i = 0; i < map.getSaves().size(); i++) {
			removeSavePoint(map.getSaves().get(i));
		}
		ParkourMain.parkourMain.saves.removeMap(map);
		maps.remove(map);
		nameMap.remove(map.getName());
	}
	
	public static void addSavePoint(SavePoint p) {
		ParkourMap map = getMapByUUID(p.getMapID());
		if(map != null) {
			map.addSavePoint(p);
			ParkourMain.parkourMain.saves.addPoint(p);
		}
	}
	
	public static void addSavePoints(Set<SavePoint> points) {
		for(SavePoint point : points) {
			ParkourMap map = getMapByUUID(point.getMapID());
			if(map != null) {
				map.addSavePoint(point);
			}
		}
	}
	
	public static void removeSavePoint(SavePoint p) {
		ParkourMap map = getMapByUUID(p.getMapID());
		if(map != null) {
			map.getSaves().remove(p);
			ParkourMain.parkourMain.saves.removePoint(p);
			for(int i = 0; i < p.getCommands().size(); i++) {
				ParkourMain.parkourMain.saves.removeCommand(p.getMapID(), p.getPointID(), p.getCommand(i).getCommand());
			}
		}
	}
	
	public static boolean hasPlayerParkourData(Player player) {
		return players.get(player) != null;
	}
	
	public static ParkourPlayer getPlayerData(Player player) {
		return players.get(player);
	}
	
	public static void addPlayerData(ParkourPlayer player) {
		if(hasPlayerParkourData(player.getPlayer())) {
			return;
		}
		players.put(player.getPlayer(), player);
	}
	
	public static void removePlayerData(Player player) {
		players.remove(player);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(!hasPlayerParkourData(e.getPlayer())) return;
		ParkourPlayer player = players.get(e.getPlayer());
		
		if(player.getPlayer().isGliding() && !player.getPlayer().hasPermission(ParkourPerms.elytra)) player.getPlayer().setGliding(false);
		
		if(!player.getMap().playerInMap(player)) {
			player.getPlayer().sendMessage(ChatColor.RED + "If you want to leave the parkour use:");
			textCommand(player.getPlayer(), "[/parkour leave]", "/parkour leave", ChatColor.GOLD);
			if(player.hasSavePoint()) {
				player.returnToLastSavePoint();
				return;
			}else {
				player.backToSpawn();
				return;
			}
		}
		if(player.getMap().endReached(player)) {
			player.endReached();
		}
		player.getMap().checkSavePointIntersect(player);
		if(player.InDeathZone()) {
			if(player.hasSavePoint()) {
				player.returnToLastSavePoint();
			}else {
				player.backToSpawn();
			}
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if(!hasPlayerParkourData(e.getEntity())) return;
		ParkourPlayer player = players.get(e.getEntity());
		
		e.setKeepInventory(true);
		e.setDroppedExp(0);
		e.setKeepLevel(true);
		e.setDeathMessage(ChatColor.GOLD + "[" + e.getEntity().getName() + "]" + ChatColor.DARK_AQUA + " died on parkour:" + ChatColor.YELLOW + " [" + player.getMap().getName() + "]");
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		if(!hasPlayerParkourData(e.getPlayer())) return;
		ParkourPlayer player = players.get(e.getPlayer());
		
		e.setRespawnLocation(player.getRespawnLocation());
	}
	
	@EventHandler
	public void onSignDone(SignChangeEvent e) {
		if(!e.getPlayer().hasPermission(ParkourPerms.signs)) return;
		String l0 = e.getLine(0);
		String l1 = e.getLine(1);
		String l2 = e.getLine(2);
		
		if(l0 == null || l1 == null) return;
		l2 = l2 == null ? "null" : l2;
		if(l0.equalsIgnoreCase("parkour")) {
			if(l1.equalsIgnoreCase("join")) {
				if(!e.getPlayer().hasPermission(ParkourPerms.signs)) {
					e.getPlayer().sendMessage("You are not allowed to create pakrour signs");
					return;
				}
				ParkourMap map = getMapByName(l2);
				if(map != null) {
					e.setLine(0, ChatColor.GOLD + "[Join Parkour]");
					e.setLine(1, ChatColor.BLUE + map.getName());
					e.setLine(2, "");
					e.setLine(3, "");
				}else {
					e.getPlayer().sendMessage(ChatColor.RED + "Map dose not extist: " + l2);
					return;
				}
			}else if(l1.equalsIgnoreCase("leave")){
				if(!e.getPlayer().hasPermission(ParkourPerms.signs)) {
					e.getPlayer().sendMessage("You are not allowed to create pakrour signs");
					return;
				}
				e.setLine(0, ChatColor.RED + "#-------------#");
				e.setLine(1, ChatColor.GOLD + "[Leave map]");
				e.setLine(2, ChatColor.RED + "#-------------#");
				e.setLine(3, "");
			}
		} 
		
	}
	
	@EventHandler
	public void InteractEvent(PlayerInteractEvent e) {
		if(e.getClickedBlock() == null) return;
		if(e.getClickedBlock().getType().toString().toLowerCase().contains("sign")) {
			
			Sign s = (Sign) e.getClickedBlock().getState();
			String l0 = s.getLine(0);
			String l1 = s.getLine(1);
			if(l0 == null || l1 == null) return;
			if(l0.equals(ChatColor.GOLD + "[Join Parkour]") && !l1.equals("")) {
				l1 = l1.replace(ChatColor.BLUE.toString(), "");
				ParkourMap map = getMapByName(l1);
				if(map != null) {
					if(!e.getPlayer().hasPermission(ParkourPerms.join)) {
						e.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to join a parkour!");
						return;
					}
					if(ParkourManager.hasPlayerParkourData(e.getPlayer())) {
						e.getPlayer().sendMessage(ChatColor.RED + "You can not join more then one parkour at one time!");
						return;
					}
					ParkourPlayer parkourPlayer = new ParkourPlayer(e.getPlayer(), map, e.getPlayer().getLocation());
					ParkourManager.addPlayerData(parkourPlayer);
					parkourPlayer.backToSpawn();
					e.getPlayer().sendMessage(ChatColor.GREEN + "You have joined the parkour [" + ChatColor.GOLD + map.getName() + ChatColor.GREEN + "] have fun.");
					e.getPlayer().sendMessage(ChatColor.YELLOW + "If you want to leave the parkour use:");
					textCommand(e.getPlayer(), "[/parkour leave]", "/parkour leave", ChatColor.GOLD);
				}else {
					e.getPlayer().sendMessage(ChatColor.RED + "This map [" + l1 + "] dose not exist!");
				}
			}else if(l0.equals(ChatColor.RED + "#-------------#") && l1.equals(ChatColor.GOLD + "[Leave map]")) {
				if(!ParkourManager.hasPlayerParkourData(e.getPlayer())) {
					e.getPlayer().sendMessage(ChatColor.RED + "You are currently not registerd at a parkour!");
					return;
				}
				
				ParkourPlayer playerData = ParkourManager.getPlayerData(e.getPlayer());
				playerData.returnFromWhenceTheeCamest();
				ParkourManager.removePlayerData(e.getPlayer());
				e.getPlayer().sendMessage(ChatColor.GREEN + "You are no longer taking part in the parkour.");
				return;
			}
			
		}
	}
	
	@EventHandler
	public void OnPlayerLeave(PlayerQuitEvent e) {
		players.remove(e.getPlayer().getPlayer());
	}
	
	private void textCommand(Player p, String msg, String command, ChatColor c) {
		TextComponent message = new TextComponent(msg);
		message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		message.setColor(c);
		p.spigot().sendMessage(message);
	}
	
}
