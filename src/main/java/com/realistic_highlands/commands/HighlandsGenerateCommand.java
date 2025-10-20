package com.realistic_highlands.commands;

import com.realistic_highlands.RealisticHighlands2;
import com.realistic_highlands.world.RealisticHighlandsGenerator;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class HighlandsGenerateCommand implements CommandExecutor {

    private final RealisticHighlands2 plugin;

    public HighlandsGenerateCommand(RealisticHighlands2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendPlainMessage("§cThis command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;
        World world = player.getWorld();

        if (!(world.getGenerator() instanceof RealisticHighlandsGenerator)) {
            player.sendPlainMessage("§eThis command works best in a world created with the RealisticHighlandsGenerator.");
            player.sendPlainMessage("§eCurrent world generator: " + (world.getGenerator() != null ? world.getGenerator().getClass().getSimpleName() : "Default"));
            //return true; // Allow testing even if not our generator, but warn
        }

        if (args.length != 2) {
            player.sendPlainMessage("§cUsage: /" + label + " <chunkX> <chunkZ>");
            return true;
        }

        try {
            int chunkX = Integer.parseInt(args[0]);
            int chunkZ = Integer.parseInt(args[1]);

            long startTime = System.currentTimeMillis();
            player.sendPlainMessage("§aGenerating chunk at " + chunkX + ", " + chunkZ + " in world " + world.getName() + "...");

            // Request chunk generation asynchronously to avoid blocking the main thread
            world.getChunkAtAsync(chunkX, chunkZ, true).thenAccept(chunk -> {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                player.sendPlainMessage("§aChunk generated successfully! (Took " + duration + "ms)");
                player.sendPlainMessage("§eTeleporting to generated chunk roughly...");
                // Teleport player near the center of the chunk
                player.teleport(new org.bukkit.Location(world, chunkX * 16 + 8, plugin.getConfig().getInt("generator.seaLevel", 63), chunkZ * 16 + 8));
            }).exceptionally(e -> {
                player.sendPlainMessage("§cFailed to generate chunk: " + e.getMessage());
                plugin.getLogger().log(Level.SEVERE, "Error generating chunk: ", e);
                return null;
            });

        } catch (NumberFormatException e) {
            player.sendPlainMessage("§cInvalid chunk coordinates. Please enter numbers.");
        } catch (Exception e) {
            player.sendPlainMessage("§cAn unexpected error occurred: " + e.getMessage());
            plugin.getLogger().log(Level.SEVERE, "Error in /highlandsgenerate command: ", e);
        }

        return true;
    }
}
