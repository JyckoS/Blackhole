package com.gmail.jyckosianjaya.blackhole.manager;

import java.util.ArrayList;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.gmail.jyckosianjaya.blackhole.Blackhole;
import com.gmail.jyckosianjaya.blackhole.storage.BHStorage;
import com.gmail.jyckosianjaya.blackhole.utils.Utility;
import com.gmail.jyckosianjaya.blackhole.utils.XSound;
import com.gmail.jyckosianjaya.blackhole.utils.nbt.NBTItem;


public class BHManager {
	private Blackhole m;
	private ArrayList<Blackholes> blackholes = new ArrayList<Blackholes>();
	private ArrayList<BHTask> tasks = new ArrayList<BHTask>();
	public void addTask(BHTask task) {
		tasks.add(task);
	}
	public BHManager(Blackhole m) {
		this.m = m;
		new BukkitRunnable() {
			@Override
			public void run() {
				for (World w : Bukkit.getWorlds()) {
					for (Entity es : w.getEntities()) {
						if (es.getType() != EntityType.DROPPED_ITEM) continue;
						Item item = (Item) es;
						if (!item.isOnGround()) continue;
						ItemStack it = item.getItemStack();
						Location loc = item.getLocation();
						if (it == null) continue;
						NBTItem nbt = new NBTItem(it);
						if (!nbt.hasKey("blackhole")) continue;
						if (it.getAmount() > 1) {
							it.setAmount(it.getAmount() - 1);
							item.setItemStack(it);

						}
						else {
							item.remove();
						}
						TemplateBlackhole temp = nbt.getObject("blackhole", TemplateBlackhole.class);
						addTask(new BHTask() {
						@Override
						public void run() {
							m.getManager().createBlackhole(loc, UUID.randomUUID(), temp);

						}
							
						});

						w.spawnParticle(Particle.CRIT_MAGIC, item.getLocation(), 100);

					}
				}
				for (Blackholes bh : new ArrayList<Blackholes>(blackholes)) {
					
					bh.spinDummy();
					Location loc = bh.getLocation();
					World w = loc.getWorld();
					for (Entity en : bh.getNearbyEntities(bh.getSingularityRadius() + bh.getGravitationalPullRadius(), bh.getSingularityRadius() + bh.getGravitationalPullRadius(), bh.getSingularityRadius() + bh.getGravitationalPullRadius())) {
						switch (en.getType()) {
						case ENDER_DRAGON:
						case ARMOR_STAND:
							continue;
						default:
							break;
						}
						if (m.getStorage().isIgnored(en.getType())) continue;
						if (bh.isOwner(en.getUniqueId())) {
							continue;
						}
						if (en instanceof Damageable) {

							Location toLocation = en.getLocation();
							//en.teleportAsync(loc);
							Vector direction = (loc.toVector().subtract(toLocation.toVector())).normalize();
							en.setVelocity(direction.multiply(bh.getGravitationalPullPower()));
							w.spawnParticle(Particle.EXPLOSION_NORMAL, en.getLocation(), 1);

						}
					}
					if (m.getStorage().isBlockPulled()) {
					bh.pullBlock();
					}
					for (Entity en : bh.getNearbyEntities(bh.getSingularityRadius(), bh.getSingularityRadius(), bh.getSingularityRadius())) {
						switch (en.getType()) {
						case ENDER_DRAGON:
						case ARMOR_STAND:
							continue;
						default:
							break;
						}
						if (m.getStorage().isIgnored(en.getType())) continue;

						if (bh.isOwner(en.getUniqueId())) {
							continue;
						}
						if (en instanceof Damageable || en instanceof FallingBlock) {
							en.setFallDistance(0F);
							Location toLocation = en.getLocation();
							//en.teleportAsync(loc);
							Vector direction = (loc.toVector().subtract(toLocation.toVector())).normalize();
							en.setVelocity(direction.multiply(bh.getSingularityPullPower()));
							w.spawnParticle(Particle.LAVA, en.getLocation(), 1);
							w.spawnParticle(Particle.EXPLOSION_NORMAL, en.getLocation(), 1);

						}
					}
					w.spawnParticle(Particle.SMOKE_LARGE, loc, bh.getSingularityRadius());
					w.spawnParticle(Particle.SMOKE_NORMAL, loc, bh.getGravitationalPullRadius());
					Float soundradius = Float.valueOf(bh.getGravitationalPullRadius() / 3);
					Utility.PlaySoundAt(w, loc, XSound.MINECART_INSIDE.bukkitSound(), soundradius, 0.0F);
					Utility.PlaySoundAt(w, loc, XSound.MINECART_INSIDE.bukkitSound(), soundradius, 0.1F);

				}
				
			}
		}.runTaskTimerAsynchronously(m, 3L, 3L);
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Blackholes bh : new ArrayList<Blackholes>(blackholes)) {
					if (bh.getDuration() <= 0) {
						bh.destroy();
						for (Entity en : bh.getNearbyEntities(bh.getEvaporationRadius(), bh.getEvaporationRadius(), bh.getEvaporationRadius())) {
							switch (en.getType()) {
							case ENDER_DRAGON:
							case ARMOR_STAND:
								continue;
							default:
								break;
							}
							if (m.getStorage().isIgnored(en.getType())) continue;
							if (bh.isOwner(en.getUniqueId())) {
								continue;
							}
							if (en instanceof FallingBlock) {
								FallingBlock fal = (FallingBlock) en;
								fal.setDropItem(true);
								fal.setGravity(true);
								Vector direction = (en.getLocation().toVector().subtract(bh.getLocation().toVector())).normalize();
								en.setVelocity(direction.multiply(bh.getEvaporationPushPower() / 4));
								continue;
							}
							if (en instanceof Damageable) {
								//en.teleportAsync(bh.getLocation());
								Damageable dmg = (Damageable) en;
								addTask(new BHTask() {
									@Override
									public void run() {
								dmg.damage(bh.getEvaporationDamage());
								}});								
								if (en instanceof Player) {
									Player p = (Player) en;
									addTask(new BHTask() {
										@Override
										public void run() {
									p.addPotionEffect(blindness);
									p.addPotionEffect(nausea);
									}
									});
								}
								Vector direction = (en.getLocation().toVector().subtract(bh.getLocation().toVector())).normalize();
								en.setVelocity(direction.multiply(bh.getEvaporationPushPower()));
							}
						}
						blackholes.remove(bh);
					}
					bh.reduceDuration();
					World w = bh.getLocation().getWorld();
					Location loc = bh.getLocation();
					
					for (Entity en : bh.getNearbyEntities(bh.getSingularityRadius(), bh.getSingularityRadius(), bh.getSingularityRadius())) {
						switch (en.getType()) {
						case ENDER_DRAGON:
						case ARMOR_STAND:
							continue;
						default:
							break;
						}
						if (m.getStorage().isIgnored(en.getType())) continue;
						if (en instanceof FallingBlock) {
							FallingBlock fal = (FallingBlock) en;
							String name = fal.getCustomName();
							if (name == null) {
								fal.setGravity(true);
								fal.setInvulnerable(false);
								continue;
							}
							if (!name.startsWith("PullX")) return;
							int amount = Integer.parseInt(name.replaceAll("PullX", ""));
							if (amount <= 0) {
								if (fal.getBlockData().getMaterial().toString().contains("AIR")) {
									fal.remove();
									continue;
								}
								ItemStack item = new ItemStack(fal.getBlockData().getMaterial());
								item.setAmount(1);
								addTask(new BHTask() {
									@Override
									public void run() {
								fal.getWorld().dropItemNaturally(fal.getLocation(), item);}});
								fal.remove();
							}
							else {
								fal.setCustomName("PullX" + (amount - 1));
							}
							continue;
						}
						if (en instanceof Damageable) {
							if (bh.isOwner(en.getUniqueId())) {
								continue;
							}
							Location toLocation = en.getLocation();
							//en.teleportAsync(loc);
							Damageable dmg = (Damageable) en;
							addTask(new BHTask() {
								@Override
								public void run() {
							dmg.damage(bh.getSingularityDamage());
							}});
							if (en instanceof Player) {
								addTask(new BHTask() {
									@Override
									public void run() {
								((Player) en).addPotionEffect(blindness);
								((Player) en).addPotionEffect(nausea);}
								});
							}
						}
					}
					Utility.PlaySoundAt(w, loc, XSound.ENDERDRAGON_WINGS.bukkitSound(), 5.0F, 0.0F);
					Utility.PlaySoundAt(w, loc, XSound.ENDERDRAGON_WINGS.bukkitSound(), 5.0F, 0.2F);
					Utility.PlaySoundAt(w, loc, XSound.MINECART_INSIDE.bukkitSound(), 5.0F, 0.0F);
					w.spawnParticle(Particle.SMOKE_LARGE, loc, bh.getSingularityRadius() * 4);
					w.spawnParticle(Particle.SMOKE_NORMAL, loc, bh.getGravitationalPullRadius() * 4);

				}
			}
		}.runTaskTimerAsynchronously(m, 20L, 20L);
		new BukkitRunnable() {
			@Override
			public void run() {
				for (BHTask task : new ArrayList<BHTask>(tasks)) {
					if (task == null) continue;
					task.run();

					tasks.remove(task);
				}
			}
		}.runTaskTimer(m, 1L, 1L);
	}
	private PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 120, 1);
	private PotionEffect nausea = new PotionEffect(PotionEffectType.CONFUSION, 120, 1);
	public void killAll() {
		for (Blackholes bh : new ArrayList<Blackholes>(this.blackholes)) {
			bh.destroy();}	
		this.blackholes.clear();}
	public void createBlackhole(Location loc, UUID owner, TemplateBlackhole template) {
		BHStorage strg = this.m.getStorage();
		this.blackholes.add(new Blackholes(owner, loc, template));
	}
	/*
	 * 	protected Blackholes(UUID owner, Location locz, int duration, int sing_rad, double sing_dmg,
	 *  float sing_power, int grvt_rad,
	 *  float grv_power, double evpr_dmg, int evpr_rad, float evpr_power) {

	 */
}
