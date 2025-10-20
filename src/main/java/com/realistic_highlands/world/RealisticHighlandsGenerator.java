package com.realistic_highlands.world;

import com.realistic_highlands.RealisticHighlands2;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.PerlinNoiseGenerator;

import java.util.Random;

public class RealisticHighlandsGenerator extends ChunkGenerator {

    private final RealisticHighlands2 plugin;
    private PerlinNoiseGenerator heightNoise;
    private PerlinNoiseGenerator mountainNoise;
    private PerlinNoiseGenerator riverNoise;
    private PerlinNoiseGenerator biomeNoise;

    private double biomeScale;
    private double heightScale;
    private double mountainFrequency;
    private double riverFrequency;
    private int seaLevel;

    public RealisticHighlandsGenerator(String id) {
        this.plugin = RealisticHighlands2.getInstance(); // Access the plugin instance
        reloadConfigParameters();
    }

    private void reloadConfigParameters() {
        this.biomeScale = plugin.getConfig().getDouble("generator.biomeScale", 1.0);
        this.heightScale = plugin.getConfig().getDouble("generator.heightScale", 200.0);
        this.mountainFrequency = plugin.getConfig().getDouble("generator.mountainFrequency", 0.005);
        this.riverFrequency = plugin.getConfig().getDouble("generator.riverFrequency", 0.01);
        this.seaLevel = plugin.getConfig().getInt("generator.seaLevel", 63);

        // Initialize noise generators with a consistent seed
        long seed = plugin.getConfig().getLong("generator.seed", 0L);
        if (seed == 0L) {
            seed = new Random().nextLong(); // Generate a random seed if none is set
            plugin.getConfig().set("generator.seed", seed); // Save it to config
            plugin.saveConfig();
        }

        this.heightNoise = new PerlinNoiseGenerator(seed);
        this.mountainNoise = new PerlinNoiseGenerator(seed + 1);
        this.riverNoise = new PerlinNoiseGenerator(seed + 2);
        this.biomeNoise = new PerlinNoiseGenerator(seed + 3);

        plugin.getLogger().info("RealisticHighlandsGenerator parameters reloaded with seed: " + seed);
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.ChunkData chunkData) {
        // Iterate through each block in the chunk
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int blockX = chunkX * 16 + x;
                int blockZ = chunkZ * 16 + z;

                // Get biome factor for smoother transitions (0 to 1)
                double biomeFactor = (biomeNoise.noise(blockX * 0.001 * biomeScale, blockZ * 0.001 * biomeScale) + 1) / 2.0;

                // Base terrain height (rolling hills)
                double baseHeight = heightNoise.noise(blockX * 0.008, blockZ * 0.008) * 30.0 + 60.0;

                // Mountain formation
                double mountainStrength = Math.abs(mountainNoise.noise(blockX * mountainFrequency, blockZ * mountainFrequency, 0.5, 4)) * heightScale;

                // River/valley formation
                double riverDepth = Math.pow(riverNoise.noise(blockX * riverFrequency, blockZ * riverFrequency, 0.5, 2), 2) * 50.0; // Squared for sharper valleys
                if (riverDepth < 0) riverDepth = 0; // Ensure it only lowers terrain

                // Combine for final height
                double finalHeight = baseHeight + (mountainStrength * biomeFactor) - riverDepth;

                int y = (int) finalHeight;

                // Place terrain blocks
                for (int currentY = worldInfo.getMinHeight(); currentY < y; currentY++) {
                    if (currentY < y - 5) {
                        chunkData.setBlock(x, currentY, z, Material.STONE);
                    } else {
                        chunkData.setBlock(x, currentY, z, Material.DIRT);
                    }
                }
                chunkData.setBlock(x, y, z, Material.GRASS_BLOCK);

                // Place water for lakes and rivers if below sea level
                if (y < seaLevel) {
                    for (int currentY = y + 1; currentY <= seaLevel; currentY++) {
                        chunkData.setBlock(x, currentY, z, Material.WATER);
                    }
                }
            }
        }
    }

    // You might want to override getDefaultPopulators for natural structures like trees, ores etc.
    // Or handle them within generateSurface or through separate populator functions.
}
