package com.windpvp.hub.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import com.windpvp.hub.HubPatches;

public class HubListeners implements Listener {

    private final HubPatches plugin;

    public HubListeners(HubPatches plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
        if (plugin.getHubSpawnLocation() != null) {
            event.setSpawnLocation(plugin.getHubSpawnLocation());
        }
        
        Player player = event.getPlayer();
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) { // Safety check in case they instantly disconnected
                if (player.hasPermission("hubpatches.fly")) {
                    player.setAllowFlight(true);
                    player.setFlying(true);
                } else {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                }
            }
        }, 1L); // 1 tick delay
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
		if (plugin.getHubSpawnLocation() != null) {
			if (event.getTo().getWorld().equals(plugin.getHubSpawnLocation().getWorld())) {
				if (event.getTo().distanceSquared(plugin.getHubSpawnLocation()) > (Math.pow(164.274, 2))) {
					event.getPlayer().teleport(plugin.getHubSpawnLocation());
					event.getPlayer().sendMessage("§cCome back!!");
				}
			}
		}
	}
}