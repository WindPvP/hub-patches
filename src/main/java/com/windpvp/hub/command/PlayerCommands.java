package com.windpvp.hub.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.windpvp.hub.HubPatches;

public class PlayerCommands implements CommandExecutor {

    private final HubPatches plugin;

    public PlayerCommands(HubPatches plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use warp commands!");
            return true;
        }

        Player player = (Player) sender;
        String cmdName = command.getName().toLowerCase();

        if (plugin.getWarpLocations().containsKey(cmdName)) {
            Location destination = plugin.getWarpLocations().get(cmdName);
            
            if (destination == null) {
                player.sendMessage(ChatColor.RED + "An admin hasn't set up the destination for /" + cmdName + " yet!");
                return true;
            }

            player.teleport(destination);
            player.sendMessage(ChatColor.GOLD + "Teleported to " + command.getName() + "!");
            return true;
        }

        return false;
    }
}