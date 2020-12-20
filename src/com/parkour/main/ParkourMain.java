package com.parkour.main;

import java.nio.file.Path;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.parkour.commands.ParkourCommands;
import com.parkour.save.SaveLoadInteface;


public class ParkourMain extends JavaPlugin {

	public static Path folder;
	public static ParkourMain parkourMain;
	public SaveLoadInteface saves;
	public static Plugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		parkourMain = this;
		folder = this.getDataFolder().toPath();
		
		saves = new SaveLoadInteface(this);
		saves.loaddata();
		
		getCommand("parkour").setExecutor(new ParkourCommands());
		getServer().getPluginManager().registerEvents(new ParkourManager(), this);
		
		this.getLogger().log(Level.INFO, "Enabled 0.0.1");
	}
	
	@Override
	public void onDisable() {
		
		
		
	}
	
}
