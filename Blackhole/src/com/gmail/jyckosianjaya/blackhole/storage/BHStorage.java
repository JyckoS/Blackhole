package com.gmail.jyckosianjaya.blackhole.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.jyckosianjaya.blackhole.Blackhole;
import com.gmail.jyckosianjaya.blackhole.manager.TemplateBlackhole;
import com.gmail.jyckosianjaya.blackhole.utils.Utility;
import com.gmail.jyckosianjaya.blackhole.utils.nbt.NBTItem;

public class BHStorage {
	private Blackhole m;
	private int def_duration = 30;
	private HashMap<String, TemplateBlackhole> templates = new HashMap<String, TemplateBlackhole>();
	private double def_singul_dmg = 2;
	private int def_singul_rad = 10;
	private ArrayList<Material> blacklisted_mat = new ArrayList<Material>();
	private Float def_singul_pullpwr = 2f;
	private ArrayList<EntityType> ignored_entity = new ArrayList<EntityType>();
	private int def_grav_rad = 15;
	private Float def_grav_pullpwr = 0.1f;
	private ItemStack blackhole_item = null;
	private double def_evpr_dmg = 100;
	private int def_evpr_rad = 50;
	private Float  def_evpr_pushpwr = 20f;
	
	public BHStorage(Blackhole m) {
		this.m = m;
		this.m.getConfig().options().copyDefaults(true);
		this.m.saveConfig();
		File f = new File(m.getDataFolder(), "blackholes.yml");
		if (!f.exists()) {
			this.m.saveResource("blackholes.yml", false);
		}
		this.reloadConfig();
	}
	public List<String> getTemplates() {
		return new ArrayList<String>(templates.keySet());
	}
	public boolean isBlockPulled() {
		return this.pull_blocks;
	}
	public void reloadConfig() {
		this.templates.clear();
		this.m.reloadConfig();
		YamlConfiguration blackholez = YamlConfiguration.loadConfiguration(new File(m.getDataFolder(), "blackholes.yml"));
		FileConfiguration config = m.getConfig();
		this.pull_blocks = config.getBoolean("pull_blocks");
		this.blacklisted_mat.clear();
		ConfigurationSection item = config.getConfigurationSection("deadstar_item");
		Material mat = null;
		try {
			mat = Material.valueOf(item.getString("material"));
		} catch (Exception e) {
			this.m.Warn("&cMaterial: &f" + item.getString("material") + " &cfor the dead star doesn't exist.");
			mat = Material.COAL_BLOCK;
		}
		for (String str : config.getStringList("unaffected_blocks")) {
			Material matz = null;
			try {
				matz = Material.matchMaterial(str);
			} catch (Exception e) {
				this.m.Warn("&cMaterial: &f" + str + " &cfor the unaffected blocks doesn't exist.");
				continue;
			}
			if (matz != null) {
				this.blacklisted_mat.add(matz);
			}
		}
		blackhole_item = new ItemStack(mat);
		ItemMeta meta = blackhole_item.getItemMeta();
		meta.setDisplayName(Utility.TransColor(item.getString("name")));
		meta.setLore(Utility.TransColor(item.getStringList("lore")));
		blackhole_item.setItemMeta(meta);
			ignored_entity.clear();
			ConfigurationSection blackholes = blackholez.getConfigurationSection("blackholes");
			for (String str : config.getStringList("unaffected_entities")) {
				EntityType ne = null;
				try {
					ne = EntityType.valueOf(str);
				} catch (Exception e) {
					this.m.Warn("&7EntityType &f" + str + " &7doesn't exist in our universe, ignoring.");
					continue;
				}
				ignored_entity.add(ne);
			}
			int loaded = 0;
			for (String str : blackholes.getKeys(false)) {
				ConfigurationSection bhpt = blackholes.getConfigurationSection(str);
				int duration = bhpt.getInt("duration");
				double sing_dmg = bhpt.getDouble("singularity_damage");
				int sing_rad = bhpt.getInt("singularity_radius");
				float sing_pullpwr = Float.valueOf(bhpt.getDouble("singularity_pull_power") + "");
				float grav_pullpwr = Float.valueOf(bhpt.getDouble("gravitational_pull_power") + "");
				int grav_rad = bhpt.getInt("gravitational_pull_radius");
				
				double evpr_dmg = bhpt.getDouble("evaporation_damage");
				float evpr_pushpwr = Float.valueOf(bhpt.getDouble("evaporation_push_power") + "");
				int evpr_rad = bhpt.getInt("evaporation_radius");
				TemplateBlackhole template = new TemplateBlackhole(duration, sing_rad, sing_dmg, sing_pullpwr, grav_rad, grav_pullpwr,
						evpr_dmg, evpr_rad, evpr_pushpwr, str, str);
				this.templates.put(str.toLowerCase(), template);
				loaded++;
			}
			Utility.sendConsole(Blackhole.prefix + "&7Loaded &f" + loaded + " &7blackholes.");
			/*
			 * int duration, int sing_rad, double sing_dmg, float sing_power, 
			 * int grvt_rad, float grv_power, double evpr_dmg, int evpr_rad, float evpr_power
			 */
	}
	public boolean isIgnored(EntityType en) {
		return ignored_entity.contains(en);
	}
	public boolean containsBlackhole(String name) {
		return templates.containsKey(name.toLowerCase());
	}
	public TemplateBlackhole getBlackhole(String name) {
		return templates.get(name.toLowerCase());
	}
	public boolean isBlacklisted(Material mat) {
		return this.blacklisted_mat.contains(mat);
	}
	public ItemStack getBlackholeItem(String ownername, TemplateBlackhole temp, int amount) {
		ItemStack item = this.blackhole_item.clone();
		ItemMeta meta = item.getItemMeta();
		String name = meta.getDisplayName();
		name = this.processPlaceholder(name, ownername, temp);
		ArrayList<String> lore = new ArrayList<String>();
		for (String st : meta.getLore())  {
			st = this.processPlaceholder(st, ownername, temp);
			lore.add(st);
		}
		meta.setDisplayName(Utility.TransColor(name));
		meta.setLore(Utility.TransColor(lore));
		item.setItemMeta(meta);
		item.setAmount(amount);
		NBTItem nbt = new NBTItem(item);
		nbt.setObject("blackhole", temp);
		return nbt.getItem();
	}
	public String processPlaceholder(String str, String ownername, TemplateBlackhole temp) {
		str = str.replaceAll("%p", ownername);
		str = str.replaceAll("%n", temp.getName());
		str = str.replaceAll("%d", temp.getDuration() + "");
		str = str.replaceAll("%sd", "" +temp.getSingularityDamage());
		str = str.replaceAll("%sr", "" +temp.getSingularityRadius());
		str = str.replaceAll("%sp", "" +temp.getSingularityPullPower());
		str = str.replaceAll("%gp", "" +temp.getGravitationalPullPower());

		str = str.replaceAll("%gr", "" +temp.getGravitationalPullRadius());
		str = str.replaceAll("%ed", "" +temp.getEvaporationDamage());
		str = str.replaceAll("%er", "" +temp.getEvaporationRadius());
		str = str.replaceAll("%ep", "" +temp.getEvaporationPushPower());


		return str;
	}
	public ArrayList<String> getBlackholes() {
		ArrayList<String> bholes = new ArrayList<String>();
		for (TemplateBlackhole bh : templates.values()) {
			bholes.add(bh.getName());
		}
		return bholes;
	}
	private boolean pull_blocks = true;
	private TemplateBlackhole defaultbh;
	public TemplateBlackhole getDefault() {
		return this.defaultbh;
	}
}
