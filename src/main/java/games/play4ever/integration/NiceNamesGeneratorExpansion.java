package games.play4ever.integration;

import games.play4ever.nicenamegenerator.NiceNamesGenerator;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Hooks into Placeholder API (PAPI) to allow to get a generated
 * name simply by using a placeholder.
 *
 * @author Marcel Schoen
 */
public class NiceNamesGeneratorExpansion extends PlaceholderExpansion {

    private final NiceNamesGenerator plugin;

    /**
     * Creates the expansion instance.
     *
     * @param plugin The name generator handler.
     */
    public NiceNamesGeneratorExpansion(NiceNamesGenerator plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "Marcel Schoen";
    }

    @Override
    public String getIdentifier() {
        return "nicenames";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public List<String> getPlaceholders() {
        Bukkit.getLogger().info("((NiceNamesGenerator)) getPlaceholders()");
        return Arrays.asList(new String[] { "name", "uname-*" } );
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        Bukkit.getLogger().info("((NiceNamesGenerator)) onRequest() / params: " + params);
        if(params.startsWith("name") || params.startsWith("uname")) {
            return plugin.getNamesHandler().replacePapiNamePlaceholder(params);
        }
        return null; // Placeholder is unknown by the Expansion
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        Bukkit.getLogger().info("((NiceNamesGenerator)) onPlaceHolderRequest() / params: " + params);
        if(params.startsWith("name") || params.startsWith("uname")) {
            return plugin.getNamesHandler().replacePapiNamePlaceholder(params);
        }
        return null; // Placeholder is unknown by the Expansion
    }
}
