package net.netcoding.niftyservers.cache;

import net.netcoding.niftybukkit.NiftyBukkit;
import net.netcoding.niftybukkit.yaml.BukkitConfig;
import net.netcoding.niftycore.yaml.annotations.Comment;
import net.netcoding.niftycore.yaml.annotations.Path;
import net.netcoding.niftycore.yaml.exceptions.InvalidConfigurationException;
import net.netcoding.niftyservers.NiftyServers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Config extends BukkitConfig {

	@Comment("Inventory Title")
	private String title = "Server Selector";

	@Path("center-items")
	@Comment("Center Inventory Items")
	private boolean centerItems = true;

	@Path("show-offline")
	@Comment("Show Offline Servers")
	private boolean showOffline = false;

	@Path("item-opener")
	@Comment("Adds item to your inventory to open the chest of servers")
	private ItemStack itemOpener = NiftyBukkit.getItemDatabase().get("0");

	public Config(JavaPlugin plugin) {
		super(plugin.getDataFolder(), "config");
	}

	public ItemStack getItemOpener() {
		return this.itemOpener;
	}

	public String getTitle() {
		return this.title;
	}

	public boolean isItemsCentered() {
		return this.centerItems;
	}

	@Override
	public void reload() throws InvalidConfigurationException {
		super.reload();

		if (NiftyServers.getFakeInventory() != null) {
			NiftyServers.getFakeInventory().closeAll();
			NiftyServers.getFakeInventory().setAutoCenter(this.isItemsCentered());
			NiftyServers.getFakeInventory().setTitle(this.getTitle());
			NiftyServers.getFakeInventory().setItemOpener(this.getItemOpener());
		}
	}

	public boolean showOfflineServers() {
		return this.showOffline;
	}

}