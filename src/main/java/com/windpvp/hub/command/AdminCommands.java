package com.windpvp.hub.command;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.windpvp.hub.HubPatches;
import com.windpvp.hub.particle.PersistentParticle;

public class AdminCommands implements CommandExecutor {

    private final HubPatches plugin;

    public AdminCommands(HubPatches plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute admin commands!");
            return true;
        }

        Player player = (Player) sender;
        String cmdName = command.getName().toLowerCase();

        if (!player.hasPermission("hubpatches.admin")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
            return true;
        }

        if (cmdName.equalsIgnoreCase("sethubspawn")) {
            Location loc = player.getLocation();
            plugin.saveLocationToConfig("hub", loc);
            plugin.setHubSpawnLocation(loc);

            player.sendMessage(ChatColor.GREEN + "Hub first-join spawn location successfully set!");
            return true;
        }

        if (cmdName.equalsIgnoreCase("setaliaswarp")) {
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "Usage: /setaliaswarp <spawn|arcade|lobby>");
                return true;
            }

            String alias = args[0].toLowerCase();
            
            if (!alias.equals("spawn") && !alias.equals("arcade") && !alias.equals("lobby")) {
                player.sendMessage(ChatColor.RED + "That alias isn't registered in the plugin.yml!");
                return true;
            }

            Location loc = player.getLocation();
            plugin.saveLocationToConfig("aliases." + alias, loc);
            plugin.getWarpLocations().put(alias, loc);

            player.sendMessage(ChatColor.GREEN + "Alias '" + alias + "' destination has been saved successfully!");
            return true;
        }

        if (cmdName.equalsIgnoreCase("addparticle")) {
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "Usage: /addparticle <type>");
                return true;
            }

            String effectInput = args[0].toUpperCase();
            Effect effect;
            try {
                effect = Effect.valueOf(effectInput);
                if (effect.getType() != Effect.Type.PARTICLE) {
                    player.sendMessage(ChatColor.RED + "That effect type is not a particle!");
                    return true;
                }
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + "Invalid particle type!");
                return true;
            }

            int nextId = 1;
            if (plugin.getConfig().contains("particles")) {
                for (String key : plugin.getConfig().getConfigurationSection("particles").getKeys(false)) {
                    try {
                        int currentKey = Integer.parseInt(key);
                        if (currentKey >= nextId) {
                            nextId = currentKey + 1;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }

            String particleID = String.valueOf(nextId);
            String path = "particles." + particleID;

            Location loc = player.getLocation();
            plugin.saveLocationToConfig(path, loc);
            plugin.getConfig().set(path + ".type", effect.name());
            plugin.saveConfig();

            plugin.getPersistentParticles().put(particleID, new PersistentParticle(particleID, loc, effect));
            player.sendMessage(ChatColor.GREEN + "Spawned persistent particle with ID: " + ChatColor.YELLOW + particleID);
            return true;
        }

        if (cmdName.equalsIgnoreCase("listparticles")) {
            if (plugin.getPersistentParticles().isEmpty()) {
                player.sendMessage(ChatColor.RED + "There are no active persistent particles.");
                return true;
            }

            player.sendMessage(ChatColor.GOLD + "=== Persistent Particles List ===");
            for (PersistentParticle p : plugin.getPersistentParticles().values()) {
                Location l = p.getLocation();
                player.sendMessage(ChatColor.YELLOW + "ID: " + ChatColor.WHITE + p.getId() + 
                        ChatColor.YELLOW + " | Type: " + ChatColor.WHITE + p.getType().name() + 
                        ChatColor.GRAY + " (" + l.getWorld().getName() + ", " + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ() + ")");
            }
            return true;
        }

        if (cmdName.equalsIgnoreCase("removeparticle")) {
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "Usage: /removeparticle <id>");
                return true;
            }

            String targetId = args[0];

            if (!plugin.getPersistentParticles().containsKey(targetId)) {
                player.sendMessage(ChatColor.RED + "No particle found with ID '" + targetId + "'. Use /listparticles.");
                return true;
            }

            plugin.getPersistentParticles().remove(targetId);
            plugin.getConfig().set("particles." + targetId, null);
            plugin.saveConfig();

            player.sendMessage(ChatColor.GREEN + "Particle ID " + targetId + " has been deleted permanently.");
            return true;
        }

        if (cmdName.equalsIgnoreCase("clearparticles")) {
            plugin.getConfig().set("particles", null);
            plugin.saveConfig();
            plugin.getPersistentParticles().clear();
            player.sendMessage(ChatColor.GREEN + "All persistent particles have been cleared from the hub!");
            return true;
        }

        return false;
    }
}