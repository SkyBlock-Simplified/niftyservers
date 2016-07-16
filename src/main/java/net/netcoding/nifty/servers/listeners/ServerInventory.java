package net.netcoding.nifty.servers.listeners;

import net.netcoding.nifty.common.Nifty;
import net.netcoding.nifty.common.api.inventory.FakeInventoryInstance;
import net.netcoding.nifty.common.api.inventory.FakeInventoryListener;
import net.netcoding.nifty.common.api.inventory.events.FakeInventoryClickEvent;
import net.netcoding.nifty.common.api.inventory.events.FakeInventoryCloseEvent;
import net.netcoding.nifty.common.api.inventory.events.FakeInventoryOpenEvent;
import net.netcoding.nifty.common.api.inventory.events.FakeItemInteractEvent;
import net.netcoding.nifty.common.api.plugin.MinecraftListener;
import net.netcoding.nifty.common.api.plugin.MinecraftPlugin;
import net.netcoding.nifty.common.api.plugin.messaging.BungeeServer;
import net.netcoding.nifty.common.minecraft.command.CommandSource;
import net.netcoding.nifty.common.minecraft.entity.living.human.Player;
import net.netcoding.nifty.common.minecraft.inventory.item.ItemStack;
import net.netcoding.nifty.common.minecraft.inventory.item.meta.ItemMeta;
import net.netcoding.nifty.common.mojang.MinecraftMojangProfile;
import net.netcoding.nifty.core.api.color.ChatColor;
import net.netcoding.nifty.core.util.ListUtil;
import net.netcoding.nifty.core.util.StringUtil;
import net.netcoding.nifty.servers.NiftyServers;
import net.netcoding.nifty.servers.cache.ServerInfo;

import java.util.ArrayList;
import java.util.List;

public class ServerInventory extends MinecraftListener implements FakeInventoryListener {

	public ServerInventory(MinecraftPlugin plugin) {
		super(plugin);
	}

	public static List<String> getServerNames(CommandSource source) {
		List<String> matched = new ArrayList<>();

		for (String serverName : Nifty.getBungeeHelper().getServerNames()) {
			ServerInfo serverInfo = NiftyServers.getServersConfig().getServer(serverName);
			if (serverInfo.isHidden()) continue;
			if (!NiftyServers.getPluginConfig().showOfflineServers() && !Nifty.getBungeeHelper().getServer(serverName).isOnline()) continue;

			if (serverInfo.isRestricted()) {
				if (!NiftyServers.getPlugin(NiftyServers.class).hasPermissions(source, "restricted", "server", serverName))
					continue;
			}

			if (NiftyServers.getPlugin(NiftyServers.class).hasPermissions(source, "server", serverName))
				matched.add(serverName);
		}

		return matched;
	}

	public static void processChest(MinecraftMojangProfile profile) {
		FakeInventoryInstance inventory = NiftyServers.getFakeInventory().newInstance(profile);

		for (String serverName : getServerNames(profile.getOfflinePlayer().getPlayer())) {
			BungeeServer server = Nifty.getBungeeHelper().getServer(serverName);
			ServerInfo serverInfo = NiftyServers.getServersConfig().getServer(serverName);
			ItemStack item = serverInfo.getItem();
			if (server.isCurrentServer()) item.addGlow();

			if (StringUtil.isEmpty(serverInfo.getDisplayName())) {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(serverName);
				item.setItemMeta(meta);
			}

			inventory.add(item);
		}

		inventory.open();
	}

	public static void showList(CommandSource source) {
		List<String> serverNames = getServerNames(source);

		if (ListUtil.notEmpty(serverNames)) {
			String current = isConsole(source) ? "Current Server" : "You are on";
			String players = isConsole(source) ? "Players" : "You";
			String serverList = StringUtil.implode(StringUtil.format("{0}, {1}", ChatColor.GRAY, ChatColor.RED), serverNames);
			Nifty.getPlugin().getLog().message(source, "{0}: {{1}}\n{2} can connect to: {{3}}", current, Nifty.getBungeeHelper().getServerName(), players, serverList);
		} else
			Nifty.getPlugin().getLog().error(source, "You cannot list the servers!");
	}

	@Override
	public void onInventoryClick(FakeInventoryClickEvent event) {
		MinecraftMojangProfile profile = event.getProfile();
		Player player = profile.getOfflinePlayer().getPlayer();
		ItemStack currentItem = event.getClickedItem();

		for (String serverName : NiftyServers.getServersConfig().getServerList().keySet()) {
			ServerInfo serverInfo = NiftyServers.getServersConfig().getServer(serverName);
			String name = StringUtil.isEmpty(serverInfo.getDisplayName()) ? serverName : serverInfo.getDisplayName();

			if (name.equals(currentItem.getItemMeta().getDisplayName())) {
				if (Nifty.getBungeeHelper().getServer(serverName).isOnline()) {
					player.closeInventory();
					Nifty.getBungeeHelper().connect(profile, serverName);
				} else
					this.getLog().error(player, "Unable to connect, {{0}} is offline!", serverName);

				break;
			}
		}

		event.setCancelled(true);
	}

	@Override
	public void onInventoryClose(FakeInventoryCloseEvent event) { }

	@Override
	public void onInventoryOpen(FakeInventoryOpenEvent event) { }

	@Override
	public void onItemInteract(FakeItemInteractEvent event) {
		if (this.hasPermissions(event.getProfile(), "chest"))
			processChest(event.getProfile());
		else
			showList(event.getProfile().getOfflinePlayer().getPlayer());
	}

}