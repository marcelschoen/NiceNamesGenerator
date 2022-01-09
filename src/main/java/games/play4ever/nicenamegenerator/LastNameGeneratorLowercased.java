package games.play4ever.nicenamegenerator;

import org.bukkit.Bukkit;

/**
 * This "generator" does not really generate a value, but return the one that
 * was generated with the last invocation - all lowercased. To use it, the placeholder must have
 * the suffix "-lastnameLowercased", e.g. "%nicenames_uname-lastnameLowercased%". The use-case for this
 * is a situation where a unique name must be generated first with a console command,
 * and then that value should be used in several follow-up commands, e.g. to set the
 * name of a WorldGuard region, or a Citizen NPC.
 */
public class LastNameGeneratorLowercased implements NameGenerator {

    @Override
    public String compose(int syllables) {
        String lastName = NamesHandler.lastGeneratedName.get();
        if(lastName != null) {
            return lastName.toLowerCase();
        }
        Bukkit.getLogger().warning("Failed to re-use last generated name, as there is none in the current thread.");
        return "";
    }
}
