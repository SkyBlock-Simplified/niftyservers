package net.netcoding.niftyservers.cache;

import java.util.HashSet;
import java.util.Set;

import net.netcoding.niftybukkit.yaml.Config;
import net.netcoding.niftyservers.converters.ServerInfoConverter;

import org.bukkit.plugin.java.JavaPlugin;

public class Servers extends Config {

	//private Map<String, ServerInfo> serverList = new HashMap<>();

	private Set<ServerInfo> serverList = new HashSet<>();

	public Servers(JavaPlugin plugin) {
		super(plugin, "servers");
		this.addConverter(ServerInfoConverter.class);
	}

	public void addServer(String serverName) {
		for (ServerInfo serverInfo : this.serverList) {
			if (serverInfo.getServerName().equalsIgnoreCase(serverName))
				return;
		}

		this.serverList.add(new ServerInfo(serverName));
		//if (!this.serverList.containsKey(serverName))
		//	this.serverList.put(serverName, new ServerInfo());
	}

	public ServerInfo getServer(String serverName) {
		for (ServerInfo serverInfo : this.serverList) {
			if (serverInfo.getServerName().equalsIgnoreCase(serverName))
				return serverInfo;
		}

		return null;
		//return this.serverList.get(serverName);
	}

}