package net.netcoding.niftyservers.commands;

import java.util.ArrayList;
import java.util.List;

import net.netcoding.niftybukkit.NiftyBukkit;
import net.netcoding.niftybukkit.minecraft.BukkitCommand;
import net.netcoding.niftybukkit.mojang.MojangProfile;
import net.netcoding.niftybukkit.mojang.exceptions.ProfileNotFoundException;
import net.netcoding.niftybukkit.util.ListUtil;
import net.netcoding.niftyservers.NiftyServers;
import net.netcoding.niftyservers.cache.ServerInfo;
import net.netcoding.niftyservers.listeners.ServerInventory;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Server extends BukkitCommand {

	public Server(JavaPlugin plugin) {
		super(plugin, "servers");
		this.setMinimumArgsLength(0);
		this.setCheckPerms(false);
		this.setBungeeOnly();
	}

	@Override
	public void onCommand(CommandSender sender, String alias, String[] args) throws Exception {
		String action = isConsole(sender) ? "list" : (ListUtil.isEmpty(args) ? "chest" : args[0]);
		MojangProfile profile = null;

		if (isPlayer(sender)) {
			try {
				profile = NiftyBukkit.getMojangRepository().searchByPlayer((Player)sender);
			} catch (ProfileNotFoundException pnfe) {
				this.getLog().error(sender, "Unable to locate the profile of {{0}}!", sender.getName());
				return;
			}
		}

		if (action.matches("^list|chest$")) {
			if (action.equalsIgnoreCase("chest")) {
				if (this.hasPermissions(sender, action)) {
					ServerInventory.processChest(profile);
					return;
				}
			}

			ServerInventory.showList(sender);
		} else {
			if (args.length == 1) {
				String serverName = action.replace("\"", "");
				ServerInfo serverInfo = NiftyServers.getServersConfig().getServer(serverName);

				if (serverInfo != null) {
					if (this.hasPermissions(sender, "server", serverName))
						NiftyBukkit.getBungeeHelper().connect(profile, serverName);
				} else
					this.getLog().error(sender, "{{0}} is not a valid server!", serverName);
			} else
				this.showUsage(sender);
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		List<String> serverNames = ServerInventory.getServerNames(sender);

		if (ListUtil.isEmpty(args))
			return serverNames;

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