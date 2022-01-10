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

    public enum NAMETYPE {
        name,
        uname,
        uniquename,
        lastname,
        lname
    }

    private static Random random = new Random();
    private Map<String, NameGenerator> nameGeneratorMap = new HashMap<>();
    private File namesFolder = null;
    static ThreadLocal<String> lastGeneratedName = new ThreadLocal<>();

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
                    NewNameGenerator nameGenerator = new NewNameGenerator(nameFile);
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
        papiPlaceholder = papiPlaceholder.toLowerCase();
        String name = "";
        String type = "all";
        if(papiPlaceholder.contains("-")) {
            type = papiPlaceholder.substring(papiPlaceholder.indexOf("-") + 1);
        }
        String testType = type.contains(":") ? type.substring(0, type.indexOf(":")) : type;
        if(nameGeneratorMap.get(testType) == null) {
            Bukkit.getLogger().warning("Invalid type: " + type + ", using 'all' instead!");
            type = "all";
        }

        if(papiPlaceholder.startsWith(NAMETYPE.lname.name())
                || papiPlaceholder.startsWith(NAMETYPE.lastname.name())) {
            String lastName = NamesHandler.lastGeneratedName.get();
            // If the placeholder is something like "lastnameLow" or "lastnameLowercase"
            // or "lnameLow" etc., it shall be lowercased.
            if(papiPlaceholder.contains("low")) {
                lastName = lastName.toLowerCase();
            }
            return lastName;
        }

        if(papiPlaceholder.startsWith(NAMETYPE.uname.name())
                || papiPlaceholder.startsWith(NAMETYPE.uniquename.name())) {
            name = generateName(type, true);
        } else {
            name = generateName(type, false);
        }
        return name;
    }

    private String generateName(String nameValue, boolean mustBeUnique) {
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
            String name = nameGeneratorMap.get(alias).compose(numberOfSyllables);
            if(!alias.contains("lastname")) {
                // Store generated name in thread-local variable to re-use in in following commands etc.
                lastGeneratedName.set(name);
            }
            return name;
        }
        Bukkit.getLogger().warning("ALIAS NOT FOUND: " + alias);
        return "undefined";
    }
}
