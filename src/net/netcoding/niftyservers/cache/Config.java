package net.netcoding.niftyservers.cache;

import net.netcoding.niftybukkit.NiftyBukkit;
import net.netcoding.niftybukkit.yaml.annotations.Comment;
import net.netcoding.niftybukkit.yaml.annotations.Path;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Config extends net.netcoding.niftybukkit.yaml.Config {

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
		super(plugin, "config");
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

	public boolean showOfflineServers() {
		return this.showOffline;
	}

}