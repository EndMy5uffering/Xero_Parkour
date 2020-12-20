package com.parkour.save;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.parkour.main.ParkourMain;
import com.parkour.main.ParkourManager;
import com.parkour.map.Command;
import com.parkour.map.ParkourMap;
import com.parkour.map.SavePoint;


public class SaveLoadInteface {

	private String SQLiteurl = "";
	private static DatabaseInfo dbinfo;
	private Plugin plugin;
	public SaveLoadInteface(Plugin plugin) {
		this.plugin = plugin;
		checkFile();
		SQLiteurl = "jdbc:sqlite:" + plugin.getDataFolder() + File.separator + "Parkour.sqlite";
		dbinfo = DatabaseAccess.getDatabaseInfo(SQLiteurl, null, null);
		createNewDatabase();
	}
	
	private void checkFile() {
		File gatesqlitefile = new File(plugin.getDataFolder(), File.separator + "Parkour.sqlite");
		String SQLiteurl = "jdbc:sqlite:" + plugin.getDataFolder() + File.separator + "Parkour.sqlite";
		if(!plugin.getDataFolder().exists()) {
			try {
				Files.createDirectory(Paths.get(plugin.getDataFolder().getPath()));
			} catch (IOException e) {
				System.out.println("Could not create data folder!");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
				return;
			}
		}
		
		if (!gatesqlitefile.exists()) {
			try {
				gatesqlitefile.createNewFile();
			} catch (IOException e) {
				System.out.println("Could not create new save file!");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
				return;
			}
		}
		try {
			DriverManager.getConnection(SQLiteurl);
		} catch (SQLException e) {
			e.printStackTrace();
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		}
	}
	
	public void createNewDatabase() {
		String sql = "CREATE TABLE IF NOT EXISTS parkour ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " uuid VARCHAR(36),"
				+ " world VARCHAR(36),"
				+ " locx DOUBLE,"
				+ " locy DOUBLE,"
				+ " locz DOUBLE,"
				+ " startx DOUBLE,"
				+ " starty DOUBLE,"
				+ " startz DOUBLE,"
				+ " endx DOUBLE,"
				+ " endy DOUBLE,"
				+ " endz DOUBLE,"
				+ " radius INTEGER,"
				+ " dlvl INTEGER,"
				+ " name VARCHAR(16)"
				+ " ); ";
		
		String pointTable = "CREATE TABLE IF NOT EXISTS parkourpoints ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " uuid VARCHAR(36),"
				+ " pointid VARCHAR(36),"
				+ " world VARCHAR(36),"
				+ " locx DOUBLE,"
				+ " locy DOUBLE,"
				+ " locz DOUBLE,"
				+ " radius INTEGER,"
				+ " params VARCHAR(36)"
				+ " ); ";
		
		String commandsTable = "CREATE TABLE IF NOT EXISTS parkourcommands ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " uuid VARCHAR(36),"
				+ " pointid VARCHAR(36),"
				+ " command VARCHAR(256),"
				+ " exec VARCHAR(8)"
				+ " ); ";
		
		execute(sql);
		execute(pointTable);
		execute(commandsTable);
	}
	
	public void addCommand(String uuid, String pointid, String command, String executor) {
		String sql = "INSERT INTO parkourcommands(uuid, pointid, command, exec) VALUES("
				+ "'" + uuid + "',"
				+ "'" + pointid + "',"
				+ "'" + command + "',"
				+ "'" + executor + "'"
				+ ");";
		execute(sql);
	}
	
	public void removeCommand(String uuid, String pointid, String command) {
		String sql = "DELETE FROM parkourcommands WHERE "
				+ "uuid='" + uuid + "' AND "
				+ "pointid='" + pointid + "' AND "
				+ "command='" + command + "';";
		execute(sql);
	}
	
	public List<com.parkour.map.Command> getCommandList(String uuid, String pointid){
		String sql = "SELECT * FROM parkourcommands WHERE "
				+ "uuid='" + uuid + "' AND"
				+ " pointid='" + pointid + "';";
		
		List<com.parkour.map.Command> out = new ArrayList<>();
		
		try {
			ResultSet rs = DatabaseAccess.getData(dbinfo, sql);
			
			while(rs.next()) {
				out.add(new Command(rs.getString("exec"), rs.getString("command")));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return out;
		
	}
	
	public Set<SavePoint> getSavePoints(String uuid){
		String sql = "SELECT * FROM parkourpoints WHERE "
				+ "uuid='" + uuid + "';";
		ResultSet rs = null;
		Set<SavePoint> out = new HashSet<>();
		try {
			rs = DatabaseAccess.getData(dbinfo, sql);
			while(rs.next()) {
				double x = rs.getDouble(5);
				double y = rs.getDouble(6);
				double z = rs.getDouble(7);
				World w = ParkourMain.plugin.getServer().getWorld(rs.getString(4));
				Location l = new Location(w, x, y, z);
				int r = rs.getInt(8);
				String pointid = rs.getString(3);
				String params = rs.getString(9);
				SavePoint p = new SavePoint(l, r, uuid, pointid);
				
				for(com.parkour.map.Command c : getCommandList(uuid, pointid)) {
					p.addCommandNoSave(c);
				}
				
				p.setParmas(params);
				out.add(p);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
		
	}
	
	public void addPoint(SavePoint p) {
		String sql = "INSERT INTO parkourpoints(uuid, pointid, world, locx, locy, locz, radius, params) VALUES('" +
				p.getMapID() + "','" +
				p.getPointID() + "','" +
				p.getPoint().getWorld().getName() + "','" +
				p.getPoint().getX() + "','" + 
				p.getPoint().getY() + "','" +
				p.getPoint().getZ() + "','" +
				p.getRadius() + "','" +
				p.getParmas() + "'"
				+ ")";
		execute(sql);
	}
	
	public void removePoint(SavePoint p) {
		String sql = "DELETE FROM parkourpoints WHERE pointid='" + p.getPointID() + "';";
		execute(sql);
	}
	
	public void addMap(ParkourMap map) {
		String sql = "INSERT INTO parkour(uuid, world, locx, locy, locz, startx, starty, startz, endx, endy, endz, radius, dlvl, name) VALUES('" +
				map.getId() + "','" +
				map.getCenter().getWorld().getName() + "','" +
				map.getCenter().getX() + "','" + 
				map.getCenter().getY() + "','" +
				map.getCenter().getZ() + "','" +
				map.getStart().getX() + "','" +
				map.getStart().getY() + "','" +
				map.getStart().getZ() + "','" +
				map.getEnd().getX() + "','" +
				map.getEnd().getY() + "','" +
				map.getEnd().getZ() + "','" +
				map.getRadius() + "','" +
				map.getDeathLvl() + "','" +
				map.getName() + "'"
				+ ")";
		execute(sql);
	}
	
	public void removeMap(ParkourMap map) {
		String sql = "DELETE FROM parkour WHERE uuid='" + map.getId() + "';";
		execute(sql);
	}
	
	public void UpdateRadiusMap(ParkourMap map) {
		String sql = "UPDATE parkour SET " +
				"radius='" + map.getRadius() + "'"
				+ " WHERE uuid='" + map.getId() + "';";
		execute(sql);
	}
	
	public void UpdateRadiusSavePoint(SavePoint map) {
		String sql = "UPDATE parkourpoints SET " +
				"radius='" + map.getRadius() + "'"
				+ " WHERE pointid='" + map.getPointID() + "';";
		execute(sql);
	}
	
	public void UpdateEndOfMap(ParkourMap map) {
		String sql = "UPDATE parkour SET " +
				"endx='" + map.getEnd().getX() + "',"
				+ "endy='" + map.getEnd().getY() + "',"
				+ "endz='" + map.getEnd().getZ() + "'"
				+ " WHERE uuid='" + map.getId() + "';";
		execute(sql);
	}
	
	public void UpdateStartOfMap(ParkourMap map) {
		String sql = "UPDATE parkour SET " +
				"endx='" + map.getEnd().getX() + "',"
				+ "endy='" + map.getEnd().getY() + "',"
				+ "endz='" + map.getEnd().getZ() + "'"
				+ " WHERE uuid='" + map.getId() + "';";
		execute(sql);
	}
	
	public void loaddata() {
		
		String querry = "SELECT * FROM parkour;";
		
		try {
			ResultSet rs = DatabaseAccess.getData(dbinfo, querry);
						
			while(rs.next()) {
				String id;
				World w;
				Location center;
				Location start;
				Location end;
				int r;
				int deathlvl;
				String name;
				
				id = rs.getString(2);
				w = ParkourMain.plugin.getServer().getWorld(rs.getString(3));
				center = new Location(w, rs.getDouble(4), rs.getDouble(5), rs.getDouble(6));
				start = new Location(w, rs.getDouble(7), rs.getDouble(8), rs.getDouble(9));
				end = new Location(w, rs.getDouble(10), rs.getDouble(11), rs.getDouble(12));
				r = rs.getInt(13);
				deathlvl = rs.getInt(14);
				name = rs.getString(15);
				
				ParkourMap map = new ParkourMap(center, start, end, name, r, deathlvl, id);
				for(com.parkour.map.Command c : getCommandList(id, "null")) {
					map.addCommandNoSave(c);
				}
				ParkourManager.addMapNoSave(map);
				ParkourManager.addSavePoints(getSavePoints(id));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private void execute(String querry) {
		try {
			DatabaseAccess.executeQuerry(dbinfo, querry);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
