package com.realistic_highlands.commands;

import com.realistic_highlands.RealisticHighlands2;
import com.realistic_highlands.world.RealisticHighlandsGenerator;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HighlandsInfoCommand implements CommandExecutor {

    private final RealisticHighlands2 plugin;

    public HighlandsInfoCommand(RealisticHighlands2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendPlainMessage("§b--- RealisticHighlands2 Info ---");
        sender.sendPlainMessage("§aVersion: §e" + plugin.getDescription().getVersion());
        sender.sendPlainMessage("§aAuthor: §e" + plugin.getDescription().getAuthors().get(0));
        sender.sendPlainMessage("§aDescription: §e" + plugin.getDescription().getDescription());
        sender.sendPlainMessage("§aConfig Reloaded: §e" + (plugin.getConfig().getBoolean("generator.reload_test_value", false) ? "Yes" : "No (Default config loaded)"));

        if (sender instanceof Player) {
            Player player = (Player) sender;
            World world = player.getWorld();
            sender.sendPlainMessage("§aYour current world: §e" + world.getName());
            if (world.getGenerator() instanceof RealisticHighlandsGenerator) {
                sender.sendPlainMessage("§aCurrent World Generator: §eRealisticHighlandsGenerator (active)");
                sender.sendPlainMessage("§aBiome Scale: §e" + plugin.getConfig().getDouble("generator.biomeScale", 1.0));
                sender.sendPlainMessage("§aHeight Scale: §e" + plugin.getConfig().getDouble("generator.heightScale", 200.0));
                sender.sendPlainMessage("§aMountain Frequency: §e" + plugin.getConfig().getDouble("generator.mountainFrequency", 0.005));
                sender.sendPlainMessage("§aRiver Frequency: §e" + plugin.getConfig().getDouble("generator.riverFrequency", 0.01));
                sender.sendPlainMessage("§aSea Level: §e" + plugin.getConfig().getInt("generator.seaLevel", 63));
            } else {
                sender.sendPlainMessage("§cCurrent World Generator: §e" + (world.getGenerator() != null ? world.getGenerator().getClass().getSimpleName() : "Default Minecraft Generator"));
                sender.sendPlainMessage("§cRealisticHighlandsGenerator is not active in this world.");
                sender.sendPlainMessage("§cTo use it, create a new world with 'generator-settings='{\"custom_generator\":\"RealisticHighlands2\"}' or via Multiverse.");
            }
        } else {
            sender.sendPlainMessage("§eRun this command as a player for world-specific info.");
        }
        sender.sendPlainMessage("§b---------------------------");
        return true;
    }
}
