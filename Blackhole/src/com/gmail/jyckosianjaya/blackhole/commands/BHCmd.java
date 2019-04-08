package com.gmail.jyckosianjaya.blackhole.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.jyckosianjaya.blackhole.Blackhole;
import com.gmail.jyckosianjaya.blackhole.manager.TemplateBlackhole;
import com.gmail.jyckosianjaya.blackhole.utils.Utility;

public class BHCmd implements TabExecutor {
	private Blackhole m;
	private ArrayList<String> help = new ArrayList<String>();
	public BHCmd(Blackhole m) {
		this.m = m;
		help.add("give");help.add("summon");help.add("list");help.add("reload");help.add("killall");
	}
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		if (!arg0.hasPermission("blackhole.admin")) return true;
		redo(arg0, arg3);
		return true;
	}
	private void redo(CommandSender snd, String[] args) {
		Player p = null;
		boolean isplayer = false;
		if (snd instanceof Player) {
			isplayer = true;
			p = (Player) snd;
		}
		if (args.length == 0) {
			Utility.sendMsg(snd, "&8&lBlackhole");
			Utility.sendMsg(snd, "&5 - &b/blackhole summon &f<name>");
			Utility.sendMsg(snd, "&5 - &b/blackhole list");
			Utility.sendMsg(snd, "&5 - &b/blackhole give &f<player> <name> [amount]");
			Utility.sendMsg(snd, "&5 - &b/blackhole killall");

			Utility.sendMsg(snd, "&5 - &b/blackhole reload");
			return;
		}
		switch (args[0].toLowerCase()) {
		case "reload":
		{
			this.m.getStorage().reloadConfig();
			Utility.sendMsg(snd, "&7Config reloaded.");
			return;
		}
		case "killall":
		{
			this.m.getManager().killAll();
			Utility.sendMsg(snd, "&cBlackhole: &7Evaporated every blackhole.");
			return;
		}
		case "give":
		{
			if (args.length < 3) {
				Utility.sendMsg(snd, "&b/blackhole give &f<player> <name> [amount]");
				return;
			}
			Player tar = Bukkit.getPlayer(args[1]);
			if (tar == null) {
				Utility.sendMsg(snd, "&7Player is offline.");
				return;
			}
			String name = args[2].toLowerCase();
			if (!this.m.getStorage().containsBlackhole(name))  {
				Utility.sendMsg(snd, "&c&lBlack hole doesn't exist.");
				return;
			}
			TemplateBlackhole bh = this.m.getStorage().getBlackhole(name);
			if (bh == null) {
				Utility.sendMsg(snd, "&c&lBlack hole doesn't exist.");
				return;
			}
			int amountz = 1;
			if (args.length > 3) {
				try {
					amountz = Integer.parseInt(args[3]);
				} catch (NumberFormatException e) {
					Utility.sendMsg(snd, "&cNot an integer!");
					return;
				}
			}
			ItemStack item = this.m.getStorage().getBlackholeItem(tar.getName(), bh, amountz);
			tar.getInventory().addItem(item);
			return;
		}
		case "summon":
		{
			if (args.length < 2) {
				Utility.sendMsg(snd, "&b/blackhole summon &f<NAME>");
				return;
			}
			if (!isplayer) return;
			String name = args[1].toLowerCase();
			if (!this.m.getStorage().containsBlackhole(name))  {
				Utility.sendMsg(snd, "&c&lBlack hole doesn't exist.");
				return;
			}
			TemplateBlackhole bh = this.m.getStorage().getBlackhole(name);
			if (bh == null) {
				Utility.sendMsg(snd, "&c&lBlack hole doesn't exist.");
				return;
			}
			this.m.getManager().createBlackhole(p.getLocation(), p.getUniqueId(), bh);
			Utility.sendMsg(snd, "&7Summoned the Blackhole &f" + bh.getName() + "&7.");
			return;
		}
		case "list":
		{
			String bh = "&7 ";
			for (String str : this.m.getStorage().getBlackholes()) {
				bh = bh + str + "&b&l,&7 ";
			}
			Utility.sendMsg(snd, bh);
			return;
		}
		default:
			Utility.sendMsg(snd, "&8&lBlackhole");
			Utility.sendMsg(snd, "&5 - &b/blackhole summon &f<name>");
			Utility.sendMsg(snd, "&5 - &b/blackhole list");
			Utility.sendMsg(snd, "&5 - &b/blackhole killall");

			Utility.sendMsg(snd, "&5 - &b/blackhole give &f<player> <name> [amount]");

			Utility.sendMsg(snd, "&5 - &b/blackhole reload");
			return;		}

	}
	private List<String> redoTab(CommandSender snd, String[] args) {
		Player p = null;
		boolean isplayer = false;
		if (snd instanceof Player) {
			isplayer = true;
			p = (Player) snd;
		}
		if (!isplayer) return null;
		if (args.length == 0) {
			return this.help;
		}
		switch (args[0].toLowerCase()) {
		case "reload":
		{
			return null;
		}
		case "killall":
		{
			return null;
		}
		case "give":
		{
			if (args.length == 2) {
				ArrayList<String> name = new ArrayList<String>();
				for (Player pa : Bukkit.getOnlinePlayers()) {
					name.add(pa.getName());
				}
				return name;
			}
			if (args.length == 3) {
			return this.m.getStorage().getTemplates();
			}
			if (args.length > 3) {
				Utility.sendMsg(p, "&b&LTIP: &7You can input any integer/number there as an item amount.");
				return null;
			}
		}
		case "summon":
		{
			
			return this.m.getStorage().getBlackholes();
		}
		case "list":
		{
			return this.m.getStorage().getBlackholes();
		}
		default:
			return this.help;
		}
	}
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if (!arg0.hasPermission("blackhole.admin")) return null;
		return redoTab(arg0, arg3);
	}

}
