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
import net.netcoding.niftybukkit.minecraft.BukkitHelper;
import net.netcoding.niftybukkit.mojang.MojangProfile;
import net.netcoding.niftybukkit.mojang.exceptions.ProfileNotFoundException;
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

public class ServerInventory extends BukkitHelper implements FakeInventoryListener {

	public ServerInventory(JavaPlugin plugin) {
		super(plugin);
	}

	public static List<String> getServerNames(CommandSender sender) {
		List<String> matched = new ArrayList<>();

		for (String serverName : NiftyBukkit.getBungeeHelper().getServerNames()) {
			ServerInfo serverInfo = Cache.Servers.getServer(serverName);
			if (serverInfo.isHidden()) continue;
			if (!Cache.Config.showOfflineServers() && !NiftyBukkit.getBungeeHelper().getServer(serverName).isOnline()) continue;

			if (serverInfo.isRestricted()) {
				if (!NiftyBukkit.getPlugin().hasPermissions(sender, "server", "restricted", serverName))
					continue;
			}

			if (NiftyBukkit.getPlugin().hasPermissions(sender, "server", serverName))
				matched.add(serverName);
		}

		return matched;
	}

	public static void processChest(MojangProfile profile) {
		FakeInventoryInstance inventory = Cache.Inventory.newInstance(profile);

		for (String serverName : getServerNames(profile.getOfflinePlayer().getPlayer())) {
			ServerInfo serverInfo = Cache.Servers.getServer(serverName);
			ItemStack item = serverInfo.getItem();

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
			String serverList = StringUtil.implode(StringUtil.format("{0}, {1}", ChatColor.GRAY, ChatColor.RED), serverNames);
			NiftyBukkit.getPlugin().getLog().message(sender, "You are on: {{0}}\nYou can connect to: {{1}}", NiftyBukkit.getBungeeHelper().getServerName(), serverList);
		} else
			NiftyBukkit.getPlugin().getLog().error(sender, "You cannot list the servers!");
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = event.getProfile().getOfflinePlayer().getPlayer();
		ItemStack currentItem = event.getClickedItem();
		MojangProfile profile;

		try {
			profile = NiftyBukkit.getMojangRepository().searchByPlayer(player);
		} catch (ProfileNotFoundException pnfe) {
			this.getLog().error(player, "Unable to locate the profile of {{0}}!", player.getName());
			return;
		}

		for (String serverName : Cache.Servers.getServerList().keySet()) {
			ServerInfo serverInfo = Cache.Servers.getServer(serverName);
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