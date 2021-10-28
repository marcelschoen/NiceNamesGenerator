package games.play4ever.nicenamegenerator;

import games.play4ever.integration.NiceNamesGeneratorExpansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            new NiceNamesGeneratorExpansion(this).register();
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

    /**
     * Helper method to return the major version that the server is running.
     *
     * This is needed because in 1.17, NMS is no longer versioned.
     *
     * @return the major version of Minecraft the server is running
     */
    public static int minecraftVersion() {
        try {
            final Matcher matcher = Pattern.compile("\\(MC: (\\d)\\.(\\d+)\\.?(\\d+?)?\\)").matcher(Bukkit.getVersion());
            if (matcher.find()) {
                return Integer.parseInt(matcher.toMatchResult().group(2), 10);
            } else {
                throw new IllegalArgumentException(String.format("No match found in '%s'", Bukkit.getVersion()));
            }
        } catch (final IllegalArgumentException ex) {
            throw new RuntimeException("Failed to determine Minecraft version", ex);
        }
    }
}
