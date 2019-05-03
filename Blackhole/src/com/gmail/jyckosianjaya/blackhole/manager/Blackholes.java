package com.gmail.jyckosianjaya.blackhole.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;



import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.inventivetalent.glow.GlowAPI;

import com.gmail.jyckosianjaya.blackhole.Blackhole;
import com.gmail.jyckosianjaya.blackhole.utils.Utility;
import com.gmail.jyckosianjaya.blackhole.utils.XMaterial;
import com.gmail.jyckosianjaya.blackhole.utils.XSound;


public class Blackholes {
	private String type = "";
	private int duration = 0;
	private int sing_radius = 0;
	private double sing_dmg = 0;
	private float sing_pullpwr = 2f;
	private String name = "Blackhole";
	private int grvt_pull_radius = 0;
	private float grvt_pull_power = 0.1f;
	private double evpr_dmg = 0;
	private int evpr_radius = 50;
	private float evpr_push_power = 20f;
	private ArrayList<Block> block_pull = new ArrayList<Block>();
	private Location loc;
	private Location dummyloc;
	private ArmorStand dummy;
	private UUID owneruuid;
	private double currentdummyhead = 0;
	private Boolean infinite = false;
	public String getName() {
		return this.name;
	}
	public void pullBlock() {
		if (block_pull.isEmpty()) {
			return;
		}
		Random rand = new Random();
		int index = rand.nextInt(block_pull.size());
		Block b = block_pull.get(index);
		block_pull.remove(index);
		switch (b.getType().toString()) {
		case "CHEST":
		case "FURNACE":
		case "TRAPPED_CHEST":
		case "LEGACY_TRAPPED_CHEST":
		case "DROPPER":
		case "LEGACY_DROPPER":
		case "SHULKER_BOX":
		case "LEGACY_SHULKER_BOX":
		case "ENDER_CHEST":
		case "LEGACY_CHEST":
		case "AIR":
		case "LEGACY_AIR":
		case "LEGACY_FURNACE":
		case "LEGACY_ENDER_CHEST":
			return;
		default:
			break;
		}
		Blackhole.getInstance().getManager().addTask(new BHTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				FallingBlock falling = (FallingBlock) b.getWorld().spawnFallingBlock(b.getLocation(), b.getBlockData());
				falling.setCustomName("PullX" + 20);
				falling.setGravity(false);
				falling.setInvulnerable(true);
				falling.setDropItem(false);
				falling.setCustomNameVisible(false);	
				b.setType(air);
			}
			});
	}
	Material air = Material.matchMaterial("AIR");
	public void addBlockToPull(Block b) {
		block_pull.add(b);
	}
	public String getID() {
		return this.type;
	}
	public UUID getOwnerUUID() {
		return this.owneruuid;
	}
	protected Blackholes(UUID owner, Location locz, TemplateBlackhole temp, int health) {
		this.type = temp.getID();
		this.owneruuid = owner;
		this.duration = health;
		this.sing_radius = temp.getSingularityRadius();
		this.sing_dmg = temp.getSingularityDamage();
		this.sing_pullpwr = temp.getSingularityPullPower();
		this.grvt_pull_radius = temp.getGravitationalPullRadius();
		this.grvt_pull_power = temp.getGravitationalPullPower();
		this.evpr_dmg = temp.getEvaporationDamage();
		this.evpr_radius = temp.getEvaporationRadius();
		this.evpr_push_power = temp.getEvaporationPushPower();
		this.loc = locz.clone();
		if (temp.getDuration() == -1) {
			this.infinite =  true;
		}
		loc = loc.add(0.0, 2.0, 0.0);
		dummyloc = locz.clone().add(0.0, 0.5, 0.0);
		dummy = (ArmorStand) loc.getWorld().spawnEntity(dummyloc, EntityType.ARMOR_STAND);
		dummy.setVisible(false);
		dummy.setMarker(true);
		dummy.setCustomName("blackhole");
		dummy.setCustomNameVisible(false);
		dummy.setGravity(false);
		if (Blackhole.packetlistenerenabled) {
		GlowAPI.setGlowing(dummy, GlowAPI.Color.BLACK, Bukkit.getOnlinePlayers());}
		ItemStack coal = new ItemStack(XMaterial.COAL_BLOCK.parseItem());
		dummy.setHelmet(coal);
		if (Blackhole.getInstance().getStorage().isBlockPulled()) {

		block_pull = new ArrayList<Block>(Utility.getBlocks(dummy.getLocation().getBlock(), sing_radius));
		}
	}
	public void silentKill() {
		dummy.remove();
	}
	protected Blackholes(UUID owner, Location locz, TemplateBlackhole temp) {
		this.type = temp.getID();
		this.owneruuid = owner;
		this.duration = temp.getDuration();
		this.sing_radius = temp.getSingularityRadius();
		this.sing_dmg = temp.getSingularityDamage();
		this.sing_pullpwr = temp.getSingularityPullPower();
		this.grvt_pull_radius = temp.getGravitationalPullRadius();
		this.grvt_pull_power = temp.getGravitationalPullPower();
		this.evpr_dmg = temp.getEvaporationDamage();
		this.evpr_radius = temp.getEvaporationRadius();
		this.evpr_push_power = temp.getEvaporationPushPower();
		this.loc = locz.clone();
		if (temp.getDuration() == -1) {
			this.infinite =  true;
		}
		loc = loc.add(0.0, 2.0, 0.0);
		dummyloc = locz.clone().add(0.0, 0.5, 0.0);
		dummy = (ArmorStand) loc.getWorld().spawnEntity(dummyloc, EntityType.ARMOR_STAND);
		dummy.setVisible(false);
		dummy.setMarker(true);
		dummy.setCustomName("blackhole");
		dummy.setCustomNameVisible(false);
		dummy.setGravity(false);
		if (Blackhole.packetlistenerenabled) {
		GlowAPI.setGlowing(dummy, GlowAPI.Color.BLACK, Bukkit.getOnlinePlayers());}
		ItemStack coal = new ItemStack(XMaterial.COAL_BLOCK.parseItem());
		dummy.setHelmet(coal);
		if (Blackhole.getInstance().getStorage().isBlockPulled()) {

		block_pull = new ArrayList<Block>(Utility.getBlocks(dummy.getLocation().getBlock(), sing_radius));
		}
	}
	public Boolean isInfinite() {
		return this.infinite;
	}
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		return this.dummy.getNearbyEntities(x, y, z);
	}
	public boolean isOwner(UUID uuid) {
		return uuid == owneruuid;
	}
	public boolean isOwner(Player p) {
		return p.getUniqueId() == owneruuid;
	}
/*	public void createNewDragon() {
		if (ed != null) {
			ed.remove();
		}
		ed = (EnderDragon) loc.getWorld().spawnEntity(loc, EntityType.ENDER_DRAGON);
		ed.setCustomName("Blackhole");
		ed.setGravity(false);
		ed.setSilent(true);
		this.resetEnderDragonDelay();
		new BukkitRunnable() {
			@Override
			public void run() {
				//hideDragon();
				ed.damage(3500.0);
			}
		}.runTaskLater(Blackhole.getInstance(), 1L);
	}*/
	public Location getLocation() {
		return this.loc;
	}
	public int getDuration() {
		return duration;
	}
	public void spinDummy() {
		this.currentdummyhead = currentdummyhead + 0.1;
		dummy.setHeadPose(new EulerAngle(currentdummyhead, currentdummyhead * -1, 0));
		if (this.currentdummyhead > 360) {
			this.currentdummyhead = 0;
		}
	}
	public void reduceDuration() {
		duration--;
	}
	public void destroy() {
		Utility.PlaySoundAt(loc.getWorld(), loc, XSound.EXPLODE.bukkitSound(), 4.0F, 0.2F);
		Utility.PlaySoundAt(loc.getWorld(), loc, XSound.EXPLODE.bukkitSound(), 6.0F, 0F);

		Utility.PlaySoundAt(loc.getWorld(), loc, XSound.EXPLODE.bukkitSound(), 0.4F, 2F);

		Utility.PlaySoundAt(loc.getWorld(), loc, XSound.EXPLODE.bukkitSound(), 1F, 1F);
		Utility.PlaySoundAt(loc.getWorld(), loc, XSound.EXPLODE.bukkitSound(), 5.0F, 0F);
		Utility.PlaySoundAt(loc.getWorld(), loc, XSound.ENDERDRAGON_DEATH.bukkitSound(), 5.0F, 0F);

		Utility.PlaySoundAt(loc.getWorld(), loc, XSound.EXPLODE.bukkitSound(), 3.0F, 0.4F);
		World w = loc.getWorld();
		w.spawnParticle(Particle.EXPLOSION_HUGE, loc, 40);
		dummy.remove();
	}
	public int getSingularityRadius() {
		 return this.sing_radius;
	}
	public double getSingularityDamage() {
		return this.sing_dmg;
	}
	public float getSingularityPullPower() {
		 return this.sing_pullpwr;
	}
	public int getGravitationalPullRadius() {
		return this.grvt_pull_radius;
	}
	public float getGravitationalPullPower() {
		return this.grvt_pull_power;
	}
	public double getEvaporationDamage() {
		return this.evpr_dmg;
	}
	public int getEvaporationRadius() {
		return this.evpr_radius;
	}
	public float getEvaporationPushPower() {
		return this.evpr_push_power;
	}
}
