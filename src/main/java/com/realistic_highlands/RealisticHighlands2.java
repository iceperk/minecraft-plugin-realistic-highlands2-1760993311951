package com.realistic_highlands;

import com.realistic_highlands.commands.HighlandsGenerateCommand;
import com.realistic_highlands.commands.HighlandsInfoCommand;
import com.realistic_highlands.commands.HighlandsReloadCommand;
import com.realistic_highlands.world.RealisticHighlandsGenerator;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class RealisticHighlands2 extends JavaPlugin {

    private static RealisticHighlands2 instance;

    public static RealisticHighlands2 getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        getLogger().log(Level.INFO, "RealisticHighlands2 has been enabled!");

        // Save default config if not exists
        saveDefaultConfig();

        // Register commands
        getCommand("highlandsreload").setExecutor(new HighlandsReloadCommand(this));
        getCommand("highlandsinfo").setExecutor(new HighlandsInfoCommand(this));
        getCommand("highlandsgenerate").setExecutor(new HighlandsGenerateCommand(this));

        // Register the world generator. 
        // Note: For existing worlds, you need to set the generator in server.properties or world.yml manually.
        // Example for server.properties: level-type=FLAT, generator-settings={"custom_generator":"RealisticHighlands2"}
        // More robust approach for creating a new world with this generator:
        // WorldCreator w = new WorldCreator("highlands_world");
        // w.generator(new RealisticHighlandsGenerator(this.getName()));
        // Bukkit.createWorld(w);

        getLogger().log(Level.INFO, "World generator 'RealisticHighlandsGenerator' registered for plugin '" + this.getName() + "'.");
        getLogger().log(Level.INFO, "To create a world with this generator, set 'generator-settings' in server.properties to '{\"custom_generator\":\"RealisticHighlands2\"}' for a new world, or create a world using a plugin like Multiverse with the generator 'RealisticHighlands2'.");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "RealisticHighlands2 has been disabled!");
    }

    @Override
    public RealisticHighlandsGenerator getDefaultWorldGenerator(String worldName, String id) {
        if (id != null && id.equalsIgnoreCase(this.getName())) {
            getLogger().log(Level.INFO, "Providing RealisticHighlandsGenerator for world: " + worldName);
            return new RealisticHighlandsGenerator(id);
        }
        getLogger().log(Level.INFO, "Not providing custom generator for world: " + worldName + " with id: " + id + ", falling back to default.");
        return null; // Fallback to default Minecraft generator
    }
}