package games.play4ever.nicenamegenerator;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class NiceNamesGeneratorTest {

    Server mockServer = mock(Server.class);

    @Test
    public void testGenerator() throws Exception {

        Logger logger = Logger.getLogger(NiceNamesGeneratorTest.class.getName());

        when(mockServer.getLogger()).thenReturn(logger);
        Bukkit.setServer(mockServer);

        File dataFolder = new File("src/test/resources");
        NamesHandler handler = new NamesHandler(dataFolder);

        List<String> lastCreatedName = new ArrayList<>();
        String text = "";
        text += "\n" + handler.replaceNamePlaceholders("His name was <uniqueName-all:3-4>, the Dragon Slayer!", lastCreatedName);
        text += "\n" + handler.replaceNamePlaceholders("<lastName> was feared in all the land!", lastCreatedName);

        System.out.println(text);
    }
}
