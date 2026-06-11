package com.windpvp.hub;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.windpvp.hub.command.AdminCommands;
import com.windpvp.hub.command.PlayerCommands;
import com.windpvp.hub.listeners.HubListeners;
import com.windpvp.hub.particle.PersistentParticle;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HubPatches extends JavaPlugin {

    private final Map<String, Location> warpLocations = new HashMap<>();
    private final Map<String, PersistentParticle> persistentParticles = new LinkedHashMap<>();
    private Location hubSpawnLocation;
    private BukkitTask particleTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadAllLocations();
        loadParticles();
        startParticleLoop();

        // Register Admin Commands
        AdminCommands adminCommands = new AdminCommands(this);
        getCommand("sethubspawn").setExecutor(adminCommands);
        getCommand("setaliaswarp").setExecutor(adminCommands);
        getCommand("addparticle").setExecutor(adminCommands);
        getCommand("listparticles").setExecutor(adminCommands);
        getCommand("removeparticle").setExecutor(adminCommands);
        getCommand("clearparticles").setExecutor(adminCommands);

        // Register Player Commands
        PlayerCommands playerCommands = new PlayerCommands(this);
        getCommand("spawn").setExecutor(playerCommands);
        getCommand("arcade").setExecutor(playerCommands);
        getCommand("lobby").setExecutor(playerCommands);

        // Register Listeners
        getServer().getPluginManager().registerEvents(new HubListeners(this), this);
    }

    @Override
    public void onDisable() {
        if (particleTask != null) {
            particleTask.cancel();
        }
    }

    public Location getHubSpawnLocation() {
        return hubSpawnLocation;
    }

    public void setHubSpawnLocation(Location loc) {
        this.hubSpawnLocation = loc;
    }

    public Map<String, Location> getWarpLocations() {
        return warpLocations;
    }

    public Map<String, PersistentParticle> getPersistentParticles() {
        return persistentParticles;
    }

    private void loadAllLocations() {
        hubSpawnLocation = loadLocationFromConfig("hub");

        String[] registeredAliases = {"spawn", "arcade", "lobby"};
        for (String alias : registeredAliases) {
            Location loc = loadLocationFromConfig("aliases." + alias);
            if (loc != null) {
                warpLocations.put(alias, loc);
            }
        }
    }

    public void loadParticles() {
        persistentParticles.clear();
        if (!getConfig().contains("particles")) return;

        ConfigurationSection section = getConfig().getConfigurationSection("particles");
        for (String key : section.getKeys(false)) {
            String path = "particles." + key;
            Location loc = loadLocationFromConfig(path);
            String typeStr = getConfig().getString(path + ".type");

            if (loc != null && typeStr != null) {
                try {
                    Effect effect = Effect.valueOf(typeStr.toUpperCase());
                    persistentParticles.put(key, new PersistentParticle(key, loc, effect));
                } catch (IllegalArgumentException e) {
                    getLogger().warning("Invalid particle type '" + typeStr + "' found in config!");
                }
            }
        }
    }

    private void startParticleLoop() {
        particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (PersistentParticle p : persistentParticles.values()) {
                    Location loc = p.getLocation();
                    if (loc != null && loc.getWorld() != null) {
                        loc.getWorld().playEffect(loc, p.getType(), 0);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 10L);
    }

    public void saveLocationToConfig(String path, Location loc) {
        getConfig().set(path + ".world", loc.getWorld().getName());
        getConfig().set(path + ".x", loc.getX());
        getConfig().set(path + ".y", loc.getY());
        getConfig().set(path + ".z", loc.getZ());
        getConfig().set(path + ".yaw", (double) loc.getYaw());
        getConfig().set(path + ".pitch", (double) loc.getPitch());
        saveConfig();
    }

    public Location loadLocationFromConfig(String path) {
        if (!getConfig().contains(path + ".world")) {
            return null;
        }

        String worldName = getConfig().getString(path + ".world");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            getLogger().warning("The world '" + worldName + "' specified for path '" + path + "' does not exist!");
            return null;
        }

        double x = getConfig().getDouble(path + ".x");
        double y = getConfig().getDouble(path + ".y");
        double z = getConfig().getDouble(path + ".z");
        float yaw = (float) getConfig().getDouble(path + ".yaw");
        float pitch = (float) getConfig().getDouble(path + ".pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }
}