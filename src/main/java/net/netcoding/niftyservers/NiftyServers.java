package net.netcoding.niftyservers;

import net.netcoding.niftybukkit.inventory.FakeInventory;
import net.netcoding.niftybukkit.minecraft.BukkitPlugin;
import net.netcoding.niftyservers.cache.Config;
import net.netcoding.niftyservers.cache.Servers;
import net.netcoding.niftyservers.commands.Server;
import net.netcoding.niftyservers.listeners.ServerInventory;
import net.netcoding.niftyservers.listeners.ServerLoaded;

public class NiftyServers extends BukkitPlugin {

	private static transient Config pluginConfig;
	private static transient Servers serversConfig;
	private static transient FakeInventory fakeInventory;

	@Override
	public void onEnable() {
		this.getLog().console("Loading Configs");
		try {
			(pluginConfig = new Config(this)).init();
			pluginConfig.startWatcher();
			(serversConfig = new Servers(this)).init();
			serversConfig.startWatcher();
		} catch (Exception ex) {
			this.getLog().console("Unable to monitor plugin config files! Changes will require a restart!", ex);
		}

		this.getLog().console("Registering Commands");
		new Server(this);

		this.getLog().console("Registering Listeners");
		new ServerLoaded(this);
		fakeInventory = new FakeInventory(this, new ServerInventory(this));
		getFakeInventory().setAutoCancelled();
		getFakeInventory().setAutoCenter(getPluginConfig().isItemsCentered());
		getFakeInventory().setTitle(getPluginConfig().getTitle());
		getFakeInventory().setItemOpener(getPluginConfig().getItemOpener());
	}

	@Override
	public void onDisable() {
		getFakeInventory().destroy();
	}

	public final static FakeInventory getFakeInventory() {
		return fakeInventory;
	}

	public final static Config getPluginConfig() {
		return pluginConfig;
	}

	public final static Servers getServersConfig() {
		return serversConfig;
	}

}