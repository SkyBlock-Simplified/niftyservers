package net.netcoding.nifty.servers.cache;

import net.netcoding.nifty.common.Nifty;
import net.netcoding.nifty.common.api.plugin.MinecraftPlugin;
import net.netcoding.nifty.common.minecraft.inventory.item.ItemStack;
import net.netcoding.nifty.common.yaml.BukkitConfig;
import net.netcoding.nifty.core.yaml.annotations.Comment;
import net.netcoding.nifty.core.yaml.annotations.Path;
import net.netcoding.nifty.core.yaml.exceptions.InvalidConfigurationException;
import net.netcoding.nifty.servers.NiftyServers;

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
	private ItemStack itemOpener = Nifty.getItemDatabase().get("0");

	public Config(MinecraftPlugin plugin) {
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