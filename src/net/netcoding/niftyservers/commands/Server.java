package net.netcoding.niftyservers.commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.netcoding.niftybukkit.NiftyBukkit;
import net.netcoding.niftybukkit.inventory.FakeInventoryInstance;
import net.netcoding.niftybukkit.minecraft.BukkitTabCommand;
import net.netcoding.niftybukkit.util.ListUtil;
import net.netcoding.niftybukkit.util.StringUtil;
import net.netcoding.niftyservers.cache.Cache;
import net.netcoding.niftyservers.cache.ServerInfo;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Server extends BukkitTabCommand {

	public Server(JavaPlugin plugin) {
		super(plugin, "server");
		this.setMinimumArgsLength(0);
		this.setCheckPerms(false);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		List<String> serverNames = this.getServerNames(sender);

		if (ListUtil.isEmpty(args))
			return serverNames;
		else {
			List<String> exactNames = new ArrayList<>();
			List<String> matchedNames = new ArrayList<>();

			for (String serverName : serverNames) {
				String checkName = serverName.toLowerCase();

				if (checkName.equalsIgnoreCase(args[0]))
					exactNames.add(serverName);
				else if (checkName.startsWith(args[0].toLowerCase()))
					matchedNames.add(serverName);
			}

			exactNames.addAll(matchedNames);
			return exactNames;
		}
	}

	private List<String> getServerNames(CommandSender sender) {
		List<String> matched = new ArrayList<>();

		for (String serverName : NiftyBukkit.getBungeeHelper().getServerNames()) {
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
	public void onCommand(CommandSender sender, String alias, String[] args) throws SQLException, Exception {
		if (!NiftyBukkit.getBungeeHelper().isOnline()) {
			this.getLog().error(sender, "This command requires BungeeCord to function!");
			return;
		}

		List<String> serverNames = this.getServerNames(sender);
		String action = isConsole(sender) ? "list" : (ListUtil.isEmpty(args) ? "chest" : args[0]);

		if (action.matches("^list|chest$")) {
			if (this.hasPermissions(sender, true, action)) {
				if (action.equalsIgnoreCase("chest")) {
					if (this.hasPermissions(sender, action)) {
						FakeInventoryInstance inventory = Cache.Inventory.newInstance((Player)sender);

						for (String serverName : serverNames) {
							ServerInfo serverInfo = Cache.Servers.getServer(serverName);
							ItemStack item = serverInfo.getItem();

							ItemMeta meta = item.getItemMeta();
							if (StringUtil.isEmpty(meta.getDisplayName())) meta.setDisplayName(serverName);
							item.setItemMeta(meta);

							inventory.add(item);
						}

						inventory.open();
						return;
					} else
						this.getLog().error(sender, "You are not allowed to view the server selection screen!");
				}

				String serverList = StringUtil.implode(StringUtil.format("{0}, {1}", ChatColor.GRAY, ChatColor.RED), serverNames);
				this.getLog().message(sender, "Current Server: {{0}}\nYou can connect to: {{1}}", NiftyBukkit.getBungeeHelper().getServerName(), serverList);
			}
		} else {
			if (args.length == 1) {
				String serverName = action.replace("\"", "");
				ServerInfo serverInfo = Cache.Servers.getServer(serverName);

				if (serverInfo != null) {
					if (this.hasPermissions(sender, "server", serverName))
						NiftyBukkit.getBungeeHelper().connect((Player)sender, serverName);
				} else
					this.getLog().error(sender, "{{0}} is not a valid server!", serverName);
			} else
				this.showUsage(sender);
		}
	}

}