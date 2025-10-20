package com.realistic_highlands.commands;

import com.realistic_highlands.RealisticHighlands2;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class HighlandsReloadCommand implements CommandExecutor {

    private final RealisticHighlands2 plugin;

    public HighlandsReloadCommand(RealisticHighlands2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        plugin.reloadConfig();
        // Optionally, update the generator parameters if it was already created
        // (though for world generators, reloading usually requires a server restart or new world)
        plugin.getLogger().log(Level.INFO, "RealisticHighlands2 configuration reloaded.");
        sender.sendPlainMessage("§aRealisticHighlands2 configuration reloaded successfully.");
        return true;
    }
}
