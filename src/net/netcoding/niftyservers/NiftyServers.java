package net.netcoding.niftyservers;

import java.util.HashMap;
import java.util.HashSet;

import net.netcoding.niftyservers.Listeners.BungeeListener;
import net.netcoding.niftyservers.Listeners.CommandListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class NiftyServers extends JavaPlugin {
	private final BungeeListener MessageListener = new BungeeListener(this);
	private final HashSet<String> ServerChestOpen = new HashSet<>();
	private CommandListener CmdListener;
	public static HashMap<Player, String> PlayerArg = new HashMap<>();

	public void onEnable() {
		this.saveDefaultConfig();
		this.CmdListener = new CommandListener(this);
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", MessageListener);
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	}

	public void onDisable() {
		this.CmdListener = null;
		Bukkit.getMessenger().unregisterIncomingPluginChannel(this, "BungeeCord");
		Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
	}

	public void chestFakeClose(String name) {
		synchronized (this.ServerChestOpen) {
			this.ServerChestOpen.remove(name);
		}
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String commandName = cmd.getName();
		Player player;
		if (commandName.equalsIgnoreCase("screload")) {
			if ((sender instanceof Player)) {
				player = (Player) sender;
				if (player.hasPermission("serverchest.reload")) {
					player.sendMessage(ChatColor.AQUA + "ServerChest config reloaded");
					this.reloadConfig();
				} else {
					player.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
				} 
			} else {
				ConsoleCommandSender console = this.getServer().getConsoleSender();
				console.sendMessage(ChatColor.AQUA + "ServerChest config reloaded");
				this.reloadConfig();
			}
			return true;
		}
		return false;
	}
	public boolean chestFakeInUse(String name) {
		synchronized (this.ServerChestOpen) {
			return this.ServerChestOpen.contains(name);
		}
	}

	public void chestFakeOpen(String name) {
		synchronized (this.ServerChestOpen) {
			this.ServerChestOpen.add(name);
		}
	}
	public static String implode(String separator, String... data) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length - 1; i++) {
			if (!data[i].matches(" *")) {
				sb.append(data[i]);
				sb.append(separator);
			}
		}
		sb.append(data[data.length - 1]);
		return sb.toString();
	}
}