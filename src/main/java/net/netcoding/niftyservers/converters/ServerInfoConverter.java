package net.netcoding.niftyservers.converters;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import net.netcoding.niftycore.yaml.ConfigSection;
import net.netcoding.niftycore.yaml.InternalConverter;
import net.netcoding.niftycore.yaml.converters.Converter;
import net.netcoding.niftyservers.cache.ServerInfo;

import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unchecked")
public class ServerInfoConverter extends Converter {

	public ServerInfoConverter(InternalConverter converter) {
		super(converter);
	}

	@Override
	public Object fromConfig(Class<?> type, Object obj, ParameterizedType genericType) throws Exception {
		Map<String, Object> map = (Map<String, Object>)(obj instanceof Map ? (Map<String, Object>)obj : ((ConfigSection)obj).getRawMap());
		ServerInfo info = new ServerInfo();
		info.setRestricted((boolean)map.get("restricted"));
		info.setHidden((boolean)map.get("hidden"));
		info.setItem((ItemStack)this.getConverter(org.bukkit.inventory.ItemStack.class).fromConfig(org.bukkit.inventory.ItemStack.class, map.get("item"), null));
		return info;
	}

	@Override
	public Object toConfig(Class<?> type, Object obj, ParameterizedType genericType) throws Exception {
		ServerInfo info = (ServerInfo)obj;
		Map<String, Object> saveMap = new HashMap<>();
		saveMap.put("restricted", info.isRestricted());
		saveMap.put("hidden", info.isHidden());
		saveMap.put("item", this.getConverter(org.bukkit.inventory.ItemStack.class).toConfig(org.bukkit.inventory.ItemStack.class, info.getItem(), null));
		return saveMap;
	}

	@Override
	public boolean supports(Class<?> type) {
		return ServerInfo.class.isAssignableFrom(type);
	}

}