package net.netcoding.nifty.servers.commands;

import net.netcoding.nifty.common.Nifty;
import net.netcoding.nifty.common.api.plugin.Command;
import net.netcoding.nifty.common.api.plugin.MinecraftListener;
import net.netcoding.nifty.common.api.plugin.MinecraftPlugin;
import net.netcoding.nifty.common.minecraft.command.CommandSource;
import net.netcoding.nifty.common.minecraft.entity.living.human.Player;
import net.netcoding.nifty.common.mojang.MinecraftMojangProfile;
import net.netcoding.nifty.core.mojang.exceptions.ProfileNotFoundException;
import net.netcoding.nifty.core.util.ListUtil;
import net.netcoding.nifty.servers.NiftyServers;
import net.netcoding.nifty.servers.cache.ServerInfo;
import net.netcoding.nifty.servers.listeners.ServerInventory;

import java.util.ArrayList;
import java.util.List;

public class Server extends MinecraftListener {

	public Server(MinecraftPlugin plugin) {
		super(plugin);
	}

	@Command(name = "servers",
			minimumArgs = 0,
			checkPerms = false,
			bungeeOnly = true
	)
	public void onCommand(CommandSource source, String alias, String[] args) throws Exception {
		String action = isConsole(source) ? "list" : (ListUtil.isEmpty(args) ? "chest" : args[0]);
		MinecraftMojangProfile profile = null;

		if (isPlayer(source)) {
			try {
				profile = Nifty.getMojangRepository().searchByPlayer((Player)source);
			} catch (ProfileNotFoundException pnfe) {
				this.getLog().error(source, "Unable to locate the profile of {{0}}!", source.getName());
				return;
			}
		}

		if (action.matches("^list|chest$")) {
			if (action.equalsIgnoreCase("chest")) {
				if (this.hasPermissions(source, action)) {
					ServerInventory.processChest(profile);
					return;
				}
			}

			ServerInventory.showList(source);
		} else {
			if (args.length == 1) {
				String serverName = action.replace("\"", "");
				ServerInfo serverInfo = NiftyServers.getServersConfig().getServer(serverName);

				if (serverInfo != null) {
					if (this.hasPermissions(source, "server", serverName))
						Nifty.getBungeeHelper().connect(profile, serverName);
				} else
					this.getLog().error(source, "{{0}} is not a valid server!", serverName);
			} else
				this.showUsage(source);
		}
	}

	@Command.TabComplete(index = 0, name = "servers")
	public List<String> onTabComplete(CommandSource source, String alias, String[] args) {
		List<String> serverNames = ServerInventory.getServerNames(source);

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