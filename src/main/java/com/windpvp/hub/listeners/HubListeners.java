package com.windpvp.hub.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
    }
}