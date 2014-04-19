package net.netcoding.niftyservers.listeners;

import net.netcoding.niftybukkit.NiftyBukkit;
import net.netcoding.niftybukkit.inventory.FakeInventoryListener;
import net.netcoding.niftybukkit.minecraft.BukkitHelper;
import net.netcoding.niftybukkit.util.RegexUtil;
import net.netcoding.niftyservers.cache.ServerInfo;
import net.netcoding.niftyservers.cache.Cache;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerInventory extends BukkitHelper implements FakeInventoryListener {

	public ServerInventory(JavaPlugin plugin) {
		super(plugin);
	}

	@Override
	public void onInventoryClose(InventoryCloseEvent event) { }

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player)event.getWhoClicked();
		ItemStack currentItem = event.getClick().isShiftClick() ? event.getCursor() : event.getCurrentItem();
		String itemName = RegexUtil.strip(currentItem.getItemMeta().getDisplayName(), RegexUtil.VANILLA_PATTERN);

		for (String serverName : NiftyBukkit.getBungeeHelper().getServerNames()) {
			ServerInfo serverInfo = Cache.Servers.getServer(serverName);

			if (serverInfo.getDisplayName(true).equals(itemName)) {
				player.closeInventory();
				NiftyBukkit.getBungeeHelper().connect(player, serverName);
				break;
			}
		}
	}

	@Override
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (!this.hasPermissions((Player)event.getPlayer(), "server"))
			event.setCancelled(true);
	}

}