package games.play4ever.nicenamegenerator;

import org.bukkit.Bukkit;

/**
 * This "generator" does not really generate a value, but return the one that
 * was generated with the last invocation. To use it, the placeholder must have
 * the suffix "-lastname", e.g. "%nicenames_uname-lastname%". The use-case for this
 * is a situation where a unique name must be generated first with a console command,
 * and then that value should be used in several follow-up commands, e.g. to set the
 * name of a WorldGuard region, or a Citizen NPC.
 */
public class LastNameGenerator implements NameGenerator {

    @Override
    public String compose(int syllables) {
        String lastName = NamesHandler.lastGeneratedName.get();
        if(lastName != null) {
            return lastName;
        }
        Bukkit.getLogger().warning("Failed to re-use last generated name, as there is none in the current thread.");
        return "";
    }
}
