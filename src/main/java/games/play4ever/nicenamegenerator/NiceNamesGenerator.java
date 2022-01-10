package games.play4ever.nicenamegenerator;

import games.play4ever.integration.NiceNamesGeneratorExpansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * The NiceNamesGenerator plugin.
 *
 * Credits to the original author(s):
 *
 * https://github.com/folkengine/java-random-name-generator
 * http://forum.codecall.net/topic/49665-java-random-name-generator/
 *
 * @author Marcel Schoen
 */
public final class NiceNamesGenerator extends JavaPlugin {

    private NamesHandler namesHandler = null;

    private final String[] configFiles = new String[] {
            "all.txt", "elven.txt", "fantasy.txt", "goblin.txt", "roman.txt"
    };

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Small check to make sure that PlaceholderAPI is installed
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getLogger().info("((NiceNamesGenerator)) Registering PAPI expansion...");
            new NiceNamesGeneratorExpansion(this, "nicenames").register();
            new NiceNamesGeneratorExpansion(this, "nnms").register();
        } else {
            Bukkit.getLogger().info("((NiceNamesGenerator)) PlaceholderAPI not found.");
        }

        File targetDirectory = getDataFolder();
        if(!targetDirectory.exists() || targetDirectory.list() == null || targetDirectory.list().length == 0) {
            if(!targetDirectory.exists()) {
                targetDirectory.mkdirs();
            }
            getLogger().info("((NiceNamesGenerator)) Loading the plugin for the first time. Preparing configuration...");
            for(String filename : configFiles) {
                File targetFile = new File(getDataFolder(), filename);
                if(!targetFile.exists()) {
                    getLogger().info("((NiceNamesGenerator)) Creating syllables configuration file: " + filename);
                    saveResource(filename, false);
                }
            }
        }

        // Initialize name handler with config files
        namesHandler = new NamesHandler(getDataFolder());
    }

    /**
     * @return The name generator handler.
     */
    public NamesHandler getNamesHandler() {
        return namesHandler;
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("((NiceNamesGenerator)) Disabled...");
        namesHandler = null;
    }
}
