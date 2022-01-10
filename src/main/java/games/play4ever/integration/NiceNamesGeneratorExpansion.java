package games.play4ever.integration;

import games.play4ever.nicenamegenerator.NamesHandler;
import games.play4ever.nicenamegenerator.NiceNamesGenerator;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hooks into Placeholder API (PAPI) to allow to get a generated
 * name simply by using a placeholder.
 *
 * @author Marcel Schoen
 */
public class NiceNamesGeneratorExpansion extends PlaceholderExpansion {

    private final NiceNamesGenerator plugin;
    private final String prefix;

    /**
     * Creates the expansion instance.
     *
     * @param plugin The name generator handler.
     */
    public NiceNamesGeneratorExpansion(NiceNamesGenerator plugin, String prefix) {
        this.prefix = prefix;
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "Marcel Schoen";
    }

    @Override
    public String getIdentifier() {
        return prefix;
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
        return Arrays.stream(NamesHandler.NAMETYPE.values()).map(v -> v.name()).collect(Collectors.toList());
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        for(NamesHandler.NAMETYPE type : NamesHandler.NAMETYPE.values()) {
            if(params.startsWith(type.name())) {
                return plugin.getNamesHandler().replacePapiNamePlaceholder(params);
            }
        }
        return null; // Placeholder is unknown by the Expansion
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        for(NamesHandler.NAMETYPE type : NamesHandler.NAMETYPE.values()) {
            if(params.startsWith(type.name())) {
                return plugin.getNamesHandler().replacePapiNamePlaceholder(params);
            }
        }
        return null; // Placeholder is unknown by the Expansion
    }
}
