package net.netcoding.niftyservers.cache;

import net.netcoding.niftybukkit.yaml.BukkitConfig;
import net.netcoding.niftyservers.converters.ServerInfoConverter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ConcurrentHashMap;

public class Servers extends BukkitConfig {

	private final ConcurrentHashMap<String, ServerInfo> serverList = new ConcurrentHashMap<>();

	public Servers(JavaPlugin plugin) {
		super(plugin.getDataFolder(), "servers");
		this.addCustomConverter(ServerInfoConverter.class);
	}

	public void addServer(String serverName) {
		this.getServerList().put(serverName, new ServerInfo());
	}

	public ServerInfo getServer(String serverName) {
		return this.getServerList().get(serverName);
	}

	public ConcurrentHashMap<String, ServerInfo> getServerList() {
		return this.serverList;
	}

}