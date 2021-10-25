package games.play4ever.nicenamegenerator;

import org.bukkit.plugin.java.JavaPlugin;

public final class NiceNamesGenerator extends JavaPlugin {

    private NamesHandler namesHandler = null;

    @Override
    public void onEnable() {
        // Plugin startup logic

        namesHandler = new NamesHandler(getDataFolder());
    }



    @Override
    public void onDisable() {
        namesHandler = null;
    }
}
