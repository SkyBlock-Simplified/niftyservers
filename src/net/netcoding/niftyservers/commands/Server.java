package net.netcoding.niftyservers.commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.netcoding.niftybukkit.inventory.FakeInventoryInstance;
import net.netcoding.niftybukkit.minecraft.BukkitTabCommand;
import net.netcoding.niftybukkit.util.StringUtil;
import net.netcoding.niftyservers.cache.ServerInfo;
import net.netcoding.niftyservers.cache.Cache;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Server extends BukkitTabCommand {

	public Server(JavaPlugin plugin) {
		super(plugin);
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return this.getServerNames(sender);
	}

	private List<String> getServerNames(CommandSender sender) {
		List<String> matched = new ArrayList<>();

		for (String serverName : Cache.BungeeHelper.getServerNames()) {
			ServerInfo serverInfo = Cache.Servers.getServer(serverName);
			if (serverInfo.isHidden()) continue;

			if (serverInfo.isRestricted()) {
				if (!this.hasPermissions(sender, "restricted", serverName))
					continue;
			}

			if (this.hasPermissions(sender, "server", serverName))
				matched.add(serverName);
		}

		return matched;
	}

	@Override
	public void command(CommandSender sender, String alias, String[] args) throws SQLException, Exception {
		List<String> serverNames = this.getServerNames(sender);
		String action = "";

		if (StringUtil.isEmpty(args) || (action = args[0]).matches("^list|chest$")) {
			if (this.hasPermissions(sender, action)) {
				if (action.equalsIgnoreCase("chest")) {
					FakeInventoryInstance inventory = Cache.Inventory.newInstance((Player)sender);

					for (String serverName : serverNames)
						inventory.add(Material.GRASS, (short)0, 1, serverName);

					inventory.open();
				} else {
					String serverList = StringUtil.implode(String.format("%s, %s", ChatColor.GRAY, ChatColor.RED), serverNames);
					this.getLog().message(sender, "You are currently connected to {%1$s}\nServers you can currently connect to: {%1$s}", Cache.BungeeHelper.getServerName(), serverList);
				}
			}
		} else {
			if (isConsole(sender)) {
				this.getLog().error(sender, "The console cannot teleport to servers!");
				return;
			}

			if (args.length == 1) {
				String serverName = args[0];
				ServerInfo serverInfo = Cache.Servers.getServer(serverName);

				if (serverInfo != null) {
					if (this.hasPermissions(sender, "server", serverName))
						Cache.BungeeHelper.connect((Player)sender, serverName);
				}
			} else
				this.showUsage(sender);
		}
	}

}