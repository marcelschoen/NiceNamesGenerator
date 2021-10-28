package games.play4ever.nicenamegenerator;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Handles configuration of name generator data.
 *
 * @author Marcel Schoen
 */
public class NamesHandler {

    private static Random random = new Random();
    private Map<String, NameGenerator> nameGeneratorMap = new HashMap<>();
    private File namesFolder = null;

    /**
     * Processes the name generator configuration
     *
     * @param dataFolder The base plugin data folder.
     */
    public NamesHandler(File dataFolder) {
        this.namesFolder = new File(dataFolder, "existing");
        File namesDirectory = dataFolder;
        if(namesDirectory.exists()) {
            if(!this.namesFolder.exists()) {
                this.namesFolder.mkdirs();
            }
            String[] nameFileEntries = namesDirectory.list(new WildcardFileFilter("*.txt"));
            for(String nameFileEntry : nameFileEntries) {
                File nameFile = new File(namesDirectory, nameFileEntry);
                try {
                    NameGenerator nameGenerator = new NameGenerator(nameFile);
                    String alias = nameFileEntry.substring(0, nameFileEntry.indexOf("."));
                    nameGeneratorMap.put(alias, nameGenerator);
                } catch(IOException e) {
                    Bukkit.getLogger().warning("Failed to process language data file: " + nameFileEntry + ", Reason: " + e.toString());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Replaces the given PAPI placeholder with a randomly generated name string.
     *
     * @param papiPlaceholder The PAPI placeholder, e.g. "name-all:5"
     * @return The randomly generated name.
     */
    public String replacePapiNamePlaceholder(String papiPlaceholder) {
        String name = "";
        String type = "all";
        if(papiPlaceholder.contains("-")) {
            type = papiPlaceholder.substring(papiPlaceholder.indexOf("-") + 1);
        }
        Bukkit.getLogger().info("PAPI placeholder: " + papiPlaceholder);
        Bukkit.getLogger().info("Type: " + type);
        String testType = type.contains(":") ? type.substring(0, type.indexOf(":")) : type;
        if(nameGeneratorMap.get(testType) == null) {
            Bukkit.getLogger().warning("Invalid type: " + type + ", using 'all' instead!");
            type = "all";
        }
        if(papiPlaceholder.startsWith("uname")) {
            name = generateName(type, true);
        } else {
            name = generateName(type, false);
        }
        return name;
    }

    private String generateName(String nameValue, boolean mustBeUnique) {
        Bukkit.getLogger().info("Generate name: " + nameValue + ", unique: " + mustBeUnique);
        String alias = nameValue;
        if(alias.contains(":")) {
            alias = alias.substring(0, alias.indexOf(":"));
        }
        int minNumberOfSyllables = 2;
        int maxNumberOfSyllables = 0;
        if(nameValue.contains(":")) {
            String numberArgument = nameValue.substring(nameValue.indexOf(":") + 1);
            try {
                if (numberArgument.contains("-")) {
                    minNumberOfSyllables = Integer.parseInt(numberArgument.substring(0, numberArgument.indexOf("-")));
                    maxNumberOfSyllables = Integer.parseInt(numberArgument.substring(numberArgument.indexOf("-") + 1));
                } else {
                    minNumberOfSyllables = Integer.parseInt(numberArgument);
                }
            } catch(NumberFormatException e) {
                Bukkit.getLogger().warning("Failed to determine number of syllables from number argument '"
                        + numberArgument + "', reason: " + e);
            }
        }
        int numberOfSyllables = minNumberOfSyllables;
        if(maxNumberOfSyllables > minNumberOfSyllables) {
            numberOfSyllables = random.nextInt(maxNumberOfSyllables - minNumberOfSyllables) + minNumberOfSyllables;
        }

        String generatedName = getName(alias, numberOfSyllables);
        if(mustBeUnique) {
            int ct = 0;
            boolean alreadyExists = new File(namesFolder, generatedName + ".txt").exists();
            while(alreadyExists && ct++ < 100) {
                generatedName = getName(alias, numberOfSyllables);
                if(ct == 25 || ct == 50 || ct == 75) {
                    numberOfSyllables++;
                }
                alreadyExists = new File(namesFolder, generatedName + ".txt").exists();
            }
            try {
                new File(namesFolder, generatedName + ".txt").createNewFile();
            } catch(IOException e) {
                Bukkit.getLogger().warning("Failed to create name file to ensure unique names, for name: " + generatedName + ", Reason: " + e.toString());
                e.printStackTrace();
            }
        }

        return generatedName;
    }

    private String getName(String alias, int numberOfSyllables) {
        if(nameGeneratorMap.containsKey(alias)) {
            return nameGeneratorMap.get(alias).compose(numberOfSyllables);
        }
        Bukkit.getLogger().warning("ALIAS NOT FOUND: " + alias);
        return "undefined";
    }

    /**
     * Cleans up the NPC data.
     */
    public void cleanUp() {
        nameGeneratorMap.clear();
    }
}
