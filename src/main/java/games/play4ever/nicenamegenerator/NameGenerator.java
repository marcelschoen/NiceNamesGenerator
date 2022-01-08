package games.play4ever.nicenamegenerator;

/**
 * Interfaces for different implementations of name generators.
 *
 * @author Marcel Schoen
 */
public interface NameGenerator {

    /**
     * Composes a name string with the given number of syllables.
     *
     * @param syllables Then number of syllables.
     * @return The generated name.
     */
    String compose(int syllables);
}
