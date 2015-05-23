package net.netcoding.niftyservers.listeners;

import java.util.ArrayList;
import java.util.List;

import net.netcoding.niftybukkit.NiftyBukkit;
import net.netcoding.niftybukkit.inventory.FakeInventoryInstance;
import net.netcoding.niftybukkit.inventory.FakeInventoryListener;
import net.netcoding.niftybukkit.inventory.events.InventoryClickEvent;
import net.netcoding.niftybukkit.inventory.events.InventoryCloseEvent;
import net.netcoding.niftybukkit.inventory.events.InventoryItemInteractEvent;
import net.netcoding.niftybukkit.inventory.events.InventoryOpenEvent;
import net.netcoding.niftybukkit.inventory.items.ItemData;
import net.netcoding.niftybukkit.minecraft.BukkitHelper;
import net.netcoding.niftybukkit.minecraft.messages.BungeeServer;
import net.netcoding.niftybukkit.mojang.BukkitMojangProfile;
import net.netcoding.niftycore.mojang.MojangProfile;
import net.netcoding.niftycore.util.ListUtil;
import net.netcoding.niftycore.util.StringUtil;
import net.netcoding.niftyservers.NiftyServers;
import net.netcoding.niftyservers.cache.ServerInfo;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerInventory extends BukkitHelper implements FakeInventoryListener {

	public ServerInventory(JavaPlugin plugin) {
		super(plugin);
	}

	public static List<String> getServerNames(CommandSender sender) {
		List<String> matched = new ArrayList<>();

		for (String serverName : NiftyBukkit.getBungeeHelper().getServerNames()) {
			ServerInfo serverInfo = NiftyServers.getServersConfig().getServer(serverName);
			if (serverInfo.isHidden()) continue;
			if (!NiftyServers.getPluginConfig().showOfflineServers() && !NiftyBukkit.getBungeeHelper().getServer(serverName).isOnline()) continue;

			if (serverInfo.isRestricted()) {
				if (!NiftyBukkit.getPlugin().hasPermissions(sender, "restricted", "server", serverName))
					continue;
			}

			if (NiftyServers.getPlugin(NiftyServers.class).hasPermissions(sender, "server", serverName))
				matched.add(serverName);
		}

		return matched;
	}

	public static void processChest(BukkitMojangProfile profile) {
		FakeInventoryInstance inventory = NiftyServers.getFakeInventory().newInstance(profile);

		for (String serverName : getServerNames(profile.getOfflinePlayer().getPlayer())) {
			BungeeServer server = NiftyBukkit.getBungeeHelper().getServer(serverName);
			ServerInfo serverInfo = NiftyServers.getServersConfig().getServer(serverName);
			ItemStack item = serverInfo.getItem();
			if (server.isCurrentServer()) item = ItemData.addGlow(item);

			if (StringUtil.isEmpty(serverInfo.getDisplayName())) {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(serverName);
				item.setItemMeta(meta);
			}

			inventory.add(item);
		}

		inventory.open();
	}

	public static void showList(CommandSender sender) {
		List<String> serverNames = getServerNames(sender);

		if (ListUtil.notEmpty(serverNames)) {
			String current = isConsole(sender) ? "Current Server" : "You are on";
			String players = isConsole(sender) ? "Players" : "You";
			String serverList = StringUtil.implode(StringUtil.format("{0}, {1}", ChatColor.GRAY, ChatColor.RED), serverNames);
			NiftyBukkit.getPlugin().getLog().message(sender, "{0}: {{1}}\n{2} can connect to: {{3}}", current, NiftyBukkit.getBungeeHelper().getServerName(), players, serverList);
		} else
			NiftyBukkit.getPlugin().getLog().error(sender, "You cannot list the servers!");
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		MojangProfile profile = event.getProfile();
		Player player = ((BukkitMojangProfile)profile).getOfflinePlayer().getPlayer();
		ItemStack currentItem = event.getClickedItem();

		for (String serverName : NiftyServers.getServersConfig().getServerList().keySet()) {
			ServerInfo serverInfo = NiftyServers.getServersConfig().getServer(serverName);
			String name = StringUtil.isEmpty(serverInfo.getDisplayName()) ? serverName : serverInfo.getDisplayName();

			if (name.equals(currentItem.getItemMeta().getDisplayName())) {
				if (NiftyBukkit.getBungeeHelper().getServer(serverName).isOnline()) {
					player.closeInventory();
					NiftyBukkit.getBungeeHelper().connect(profile, serverName);
				} else
					this.getLog().error(player, "Unable to connect, {{0}} is offline!", serverName);

				break;
			}
		}

		event.setCancelled(true);
	}

	@Override
	public void onInventoryClose(InventoryCloseEvent event) { }

	@Override
	public void onInventoryOpen(InventoryOpenEvent event) { }

	@Override
	public void onInventoryItemInteract(InventoryItemInteractEvent event) {
		if (this.hasPermissions(event.getProfile(), "chest"))
			processChest(event.getProfile());
		else
			showList(event.getProfile().getOfflinePlayer().getPlayer());
	}

}