package com.gmail.jyckosianjaya.blackhole.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.jyckosianjaya.blackhole.Blackhole;
import com.gmail.jyckosianjaya.blackhole.manager.Blackholes;
import com.gmail.jyckosianjaya.blackhole.utils.Utility;
import com.gmail.jyckosianjaya.blackhole.utils.nbt.NBTItem;

public class BHListener implements Listener {
	private Blackhole m;
	public BHListener(Blackhole m) {
		this.m = m;
	}
	@EventHandler
	public void onThrow(PlayerInteractEvent e) {
		ItemStack item = e.getItem();
		if (item == null) return;
		switch (e.getAction()) {
		case RIGHT_CLICK_BLOCK:
			break;
		default:
			return;
		}
		NBTItem nbt = new NBTItem(item);
		if (!nbt.hasKey("blackhole")) return;
		e.setCancelled(true);
	}
}
