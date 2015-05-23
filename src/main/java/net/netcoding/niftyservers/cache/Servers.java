package net.netcoding.niftyservers.cache;

import java.util.concurrent.ConcurrentHashMap;

import net.netcoding.niftyservers.converters.ServerInfoConverter;

import org.bukkit.plugin.java.JavaPlugin;

public class Servers extends net.netcoding.niftycore.yaml.Config {

	private ConcurrentHashMap<String, ServerInfo> serverList = new ConcurrentHashMap<>();

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