package net.netcoding.niftyservers.Managers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.netcoding.niftyservers.NiftyServers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ServerChestInterface implements Listener {

    private final NiftyServers plugin;
    private final Configuration config;
    private final HashMap<String, String> ServerList = new HashMap<>();

    public ServerChestInterface(NiftyServers plugin, String servers, Player player) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        String[] BungeeServers = servers.split(", ");
        Integer Slot = 0;
        int TotalSlots = (int) (Math.ceil(BungeeServers.length / 9.0) * 9.0);

        Inventory inventory = Bukkit.createInventory(player, TotalSlots, config.getString("window-name"));

        for (String key : BungeeServers) {
            String defaultname = key;
            String defaultitem = "2:0";
            Boolean restricted;
            String defaultdesc = "";
            if (TotalSlots - Slot == 9 && Boolean.valueOf(config.getString("center-icons"))) {
                Slot += (TotalSlots - BungeeServers.length) / 2;
            }
            try {
                restricted = Boolean.valueOf(node(key, "restricted"));
            } catch (Exception e) {
                restricted = false;
            }
            try {
                defaultname = cc(node(key, "name"));
            } catch (Exception e) {
            }
            try {
                defaultdesc = cc(node(key, "desc"));
            } catch (Exception e) {
            }
            try {
                defaultitem = node(key, "item");
            } catch (Exception e) {
            }

            if (restricted && !player.hasPermission("serverchest.use.restricted")) {
                continue;
            }
            inventory.setItem(Slot, SlotItem(defaultitem, defaultname, defaultdesc));
            ServerList.put(key, defaultname);
            Slot++;
        }
        player.openInventory(inventory);
        this.plugin.chestFakeOpen(player.getName());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClose(InventoryCloseEvent event) {
        if (this.plugin.chestFakeInUse(event.getPlayer().getName())) {
            this.plugin.chestFakeClose(event.getPlayer().getName());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (this.plugin.chestFakeInUse(event.getWhoClicked().getName())) {
            event.setCancelled(true);
            if (!event.getSlotType().equals(SlotType.OUTSIDE)) {
                if (event.getCurrentItem().getItemMeta() != null) {
                    for (Map.Entry<String, String> e : ServerList.entrySet()) {
                        if (e.getValue().equalsIgnoreCase(event.getCurrentItem().getItemMeta().getDisplayName())) {
                            if (player.hasPermission("serverchest.server.*") || player.hasPermission("serverchest.server." + e.getKey())) {
                                ByteArrayOutputStream b = new ByteArrayOutputStream();
                                DataOutputStream out = new DataOutputStream(b);
                                try {
                                    out.writeUTF("Connect");
                                    out.writeUTF(e.getKey());

                                } catch (IOException err) {}
                                player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
                            } else {
                                player.sendMessage(ChatColor.DARK_RED + "You do not have access to that server.");
                                event.getView().close();
                            }
                        }
                    }
                }
            }
        }
    }

    private String cc(String s) {
        return s.replaceAll("(?i)&([a-fk-or0-9])", "\u00A7$1");
    }

    private String node(String key, String type) {
        return (config.getConfigurationSection("servers." + key).get(type).toString());
    }

    @SuppressWarnings("deprecation")
	private ItemStack SlotItem(String id, String name, String desc) {
        List<String> DescArray = new ArrayList<>();
        DescArray.add(desc);
        ItemStack exStack = new ItemStack(Material.getMaterial(Integer.valueOf(id.split(":")[0])));
        try {
            exStack.setDurability(Short.valueOf(id.split(":")[1]));
        } catch (Exception e) {
        }
        ItemMeta exMeta = exStack.getItemMeta();
        exMeta.setDisplayName(name);
        exMeta.setLore(DescArray);
        exStack.setItemMeta(exMeta);
        return (exStack);
    }
}