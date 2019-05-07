package com.gmail.jyckosianjaya.blackhole;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.jyckosianjaya.blackhole.commands.BHCmd;
import com.gmail.jyckosianjaya.blackhole.listener.BHListener;
import com.gmail.jyckosianjaya.blackhole.manager.BHManager;
import com.gmail.jyckosianjaya.blackhole.storage.BHStorage;
import com.gmail.jyckosianjaya.blackhole.utils.Utility;

public class Blackhole extends JavaPlugin {
	private static Blackhole instance;
	public static Blackhole getInstance() { return instance; }
	
	@Override
	public void onEnable() {
		instance = this;
		BHCmd cmd = new BHCmd(this);
		this.getCommand("blackhole").setTabCompleter(cmd);
		this.getCommand("blackhole").setExecutor(cmd);
		this.getServer().getPluginManager().registerEvents(new BHListener(this), this);
		storage = new BHStorage(this);
	//	storage.reloadConfig();
		manager = new BHManager(this);
		if (Bukkit.getPluginManager().isPluginEnabled("PacketListenerApi") && Bukkit.getPluginManager().isPluginEnabled("GlowAPI")) {
			packetlistenerenabled = true;
			Utility.sendConsole(prefix + "Detected PacketListenerAPI & GlowAPI. &bGlow hook enabled!");
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				manager.loadAll();
			}
		}.runTaskLater(this, 2L);
	}
	public void Warn(String msg) {
		getLogger().warning(ChatColor.stripColor(Utility.TransColor(msg)));
	}
	@Override
	public void onDisable() {

		this.manager.saveAll();
		this.manager.silentKillAll();
		this.manager.cleanAll();


	}
	public BHManager getManager() {
		return this.manager;
	}
	private BHManager manager;
	private BHStorage storage;
	public static boolean packetlistenerenabled = false;
	public BHStorage getStorage() {
		return this.storage;
	}
	public static String prefix = "&b[Blackhole] &7";
}
