package com.windpvp.hub;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class HubPatches extends JavaPlugin implements CommandExecutor, Listener {

    private Location hubSpawnLocation;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        loadHubSpawn();

        getCommand("sethubspawn").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command!");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("sethubspawn")) {
            Location loc = player.getLocation();
            
            getConfig().set("hub.world", loc.getWorld().getName());
            getConfig().set("hub.x", loc.getX());
            getConfig().set("hub.y", loc.getY());
            getConfig().set("hub.z", loc.getZ());
            getConfig().set("hub.yaw", (double) loc.getYaw());
            getConfig().set("hub.pitch", (double) loc.getPitch());
            saveConfig();

            hubSpawnLocation = loc;

            player.sendMessage("§aHub spawn location successfully set and saved!");
            return true;
        }

        return false;
    }

    @EventHandler
    public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
        if (hubSpawnLocation != null) {
            event.setSpawnLocation(hubSpawnLocation);
        }
    }

    private void loadHubSpawn() {
        if (!getConfig().contains("hub.world")) {
            return; // No spawn set yet
        }

        String worldName = getConfig().getString("hub.world");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            getLogger().warning("The world '" + worldName + "' specified in config.yml does not exist!");
            return;
        }

        double x = getConfig().getDouble("hub.x");
        double y = getConfig().getDouble("hub.y");
        double z = getConfig().getDouble("hub.z");
        float yaw = (float) getConfig().getDouble("hub.yaw");
        float pitch = (float) getConfig().getDouble("hub.pitch");

        hubSpawnLocation = new Location(world, x, y, z, yaw, pitch);
    }
}