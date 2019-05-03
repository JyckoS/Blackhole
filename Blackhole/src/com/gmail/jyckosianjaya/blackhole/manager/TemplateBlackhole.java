package com.gmail.jyckosianjaya.blackhole.manager;

import java.util.UUID;

import org.bukkit.Location;

public class TemplateBlackhole {
	private int duration = 0;
	private int sing_radius = 0;
	private double sing_dmg = 0;
	private float sing_pullpwr = 2f;
	private int grvt_pull_radius = 0;
	private float grvt_pull_power = 0.1f;
	private double evpr_dmg = 0;
	private int evpr_radius = 50;
	private float evpr_push_power = 20f;
	private String id = "";
	private String name = "Blackholes";
	public TemplateBlackhole(int duration, int sing_rad, double sing_dmg, float sing_power, int grvt_rad, float grv_power, double evpr_dmg, int evpr_rad, float evpr_power, String name, String id) {
		this.duration = duration;
		this.sing_radius = sing_rad;
		this.sing_dmg = sing_dmg;
		this.sing_pullpwr = sing_power;
		this.grvt_pull_radius = grvt_rad;
		this.grvt_pull_power = grv_power;
		this.evpr_dmg = evpr_dmg;
		this.evpr_radius = evpr_rad;
		this.evpr_push_power = evpr_power;
		this.name = name;
		this.id = id;
	}
	public String getID() {
		return this.id;
	}
	public String getName() {
		return this.name;
	}
	public int getDuration() {
		 return this.duration;
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
