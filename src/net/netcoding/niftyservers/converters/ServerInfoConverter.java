package net.netcoding.niftyservers.converters;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import net.netcoding.niftybukkit.yaml.ConfigSection;
import net.netcoding.niftybukkit.yaml.InternalConverter;
import net.netcoding.niftybukkit.yaml.converters.Converter;
import net.netcoding.niftyservers.cache.ServerInfo;

import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unchecked")
public class ServerInfoConverter extends net.netcoding.niftybukkit.yaml.converters.Converter {

	public ServerInfoConverter(InternalConverter converter) {
		super(converter);
	}

	@Override
	public Object fromConfig(Class<?> type, Object obj, ParameterizedType genericType) throws Exception {
		Map<String, Object> map = (Map<String, Object>)(obj instanceof Map ? obj : ((ConfigSection)obj).getRawMap());
		if (map.get("server") == null) return null;
		ServerInfo info = new ServerInfo((String)map.get("server"));
		info.setRestricted((boolean)map.get("restricted"));
		info.setHidden((boolean)map.get("hidden"));
		Converter itemStackConverter = this.getConverter(org.bukkit.inventory.ItemStack.class);
		info.setItem((ItemStack)itemStackConverter.fromConfig(org.bukkit.inventory.ItemStack.class, map.get("item"), null));
		return info;
	}

	@Override
	public Object toConfig(Class<?> type, Object obj, ParameterizedType genericType) throws Exception {
		ServerInfo info = (ServerInfo)obj;
		Map<String, Object> saveMap = new HashMap<>();
		saveMap.put("restricted", info.isRestricted());
		saveMap.put("hidden", info.isHidden());
		Converter itemStackConverter = this.getConverter(org.bukkit.inventory.ItemStack.class);
		saveMap.put("item", itemStackConverter.toConfig(org.bukkit.inventory.ItemStack.class, info.getItem(), null));
		return saveMap;
	}

	@Override
	public boolean supports(Class<?> type) {
		return ServerInfo.class.isAssignableFrom(type);
	}

}