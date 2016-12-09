import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * This class implements the logic for a Hangman game. The answer is chosen
 * from the list of words given of a given length. It assumes that the list
 * given is non-empty. To use this API, first, instantiate it with the
 * appropriate information (non-empty list of words, length, and max number of
 * incorrect guesses). Then pass guesses to the record method which will update
 * the number of incorrect guesses left (accessed via guessesLeft()). The
 * current pattern to display (accessed via pattern()) is updated after each
 * call to record.
 *
 */

/**
 * @author Brian Bell
 *
 */
public class Hangman implements HangmanManager {

    /** The words of given length from the dictionary that fit the pattern. */
    private Set<String> words;

    /** The number of incorrect-guess left. */
    private int guessesLeft;

    /** The guesses already made. */
    private SortedSet<Character> guesses;

    /** The pattern to display. */
    private StringBuilder pattern;

    /**
     * Sets up a hangman game. It chooses a word from the list given with a
     * length that matches the length given.
     *
     * @param dictionary
     *            a list of non-empty strings containing only lower-case
     *            charactesrs
     * @param length
     *            the length of the word to choose
     * @param max
     *            the max number of missed guesses allowed
     * @throws IllegalArgumentException
     *             if the length is less than 1
     */
    Hangman(final List<String> dictionary, final int length, final int max)
            throws IllegalArgumentException {
        setInitialPattern(length);
        loadDictionaryAndFindWordsOfDesiredLength(dictionary, length);
        guessesLeft = max;
        guesses = new TreeSet<>();
    }

    /**
     * Creates the set of words from the dictionary.
     *
     * @param dictionary
     *            of list of non-empty strings containing lower-case letters
     * @param length
     *            the desired word length
     * @throws IllegalArgumentException
     *             if the desired length is less than 1
     */
    private void loadDictionaryAndFindWordsOfDesiredLength(
            final List<String> dictionary, final int length)
            throws IllegalArgumentException {
        assert dictionary != null; // the list cannot be null
        if (length < 1) {
            throw new IllegalArgumentException(
                    "Cannot have length less than 1");
        }
        words = new TreeSet<>();
        words.addAll(dictionary.stream().filter(s -> s.length() == length)
                .collect(Collectors.toSet()));
    }

    /**
     * Creates the initial pattern based on length.
     *
     * @param length
     *            the length of the word
     */
    private void setInitialPattern(final int length) {
        String[] strArray = new String[length];
        pattern = new StringBuilder(Arrays.stream(strArray).map(s -> "-")
                .collect(Collectors.joining(" ")));
    }

    /**
     * Returns all words of the set length that match the pattern.
     *
     * @see HangmanManager#words()
     */
    @Override
    public Set<String> words() {
        return new TreeSet<>(words);
    }

    /**
     *
     * @see HangmanManager#guessesLeft()
     */
    @Override
    public int guessesLeft() {
        return guessesLeft;
    }

    /**
     *
     * @see HangmanManager#guesses()
     */
    @Override
    public SortedSet<Character> guesses() {
        return guesses;
    }

    /**
     *
     * @see HangmanManager#pattern()
     */
    @Override
    public String pattern() {
        return pattern.toString();
    }

    /**
     *
     * @see HangmanManager#record(char)
     */
    @Override
    public int record(final char guess) throws IllegalArgumentException {
        assert ('a' <= guess) && (guess <= 'z'); // Guess is lower-case letter.

        if (guesses.contains(guess)) {
            throw new IllegalArgumentException(
                    guess + " has already been guessed");
        }
        guesses.add(guess);

        words = findLargestSet(guess);

        // Find number of changes made.
        String firstWord = words.iterator().next();

        if (firstWord.indexOf(guess) == -1) {
            guessesLeft--;
            return 0;
        }

        return (int) firstWord.chars().filter(c -> c == guess).count();
    }

    /**
     * Finds the largest set of words based on the last guess and sets the new
     * pattern.
     *
     * @param guess
     *            the letter guessed
     * @return the largest set of words
     */
    private Set<String> findLargestSet(final char guess) {
        // Maps patterns to sets of words
        TreeMap<String, Set<String>> allWords = new TreeMap<>();
        String wordPattern;

        // Set up map
        for (String word : words) {
            wordPattern = findNewPattern(word, guess);
            if (allWords.containsKey(wordPattern)) {
                allWords.get(wordPattern).add(word);
            } else {
                TreeSet<String> newSet = new TreeSet<>();
                newSet.add(word);
                allWords.put(wordPattern, newSet);
            }
        }
        // Find largest map entry
        Set<String> largestSet = new TreeSet<>();
        String largestSetPattern = "";
        for (Map.Entry<String, Set<String>> entry : allWords.entrySet()) {
            if (entry.getValue().size() > largestSet.size()) {
                largestSet = entry.getValue();
                largestSetPattern = entry.getKey();
            }
        }
        pattern = new StringBuilder(largestSetPattern);
        return largestSet;
    }

    /**
     * Finds the pattern that would be associated with a word after the guess.
     *
     * @param word
     *            the word to find a new pattern for
     * @param guess
     *
     *            the last guess
     * @return the pattern for this word
     */
    private String findNewPattern(final String word, final char guess) {
        StringBuilder newPattern = new StringBuilder(pattern);

        if (word.indexOf(guess) != -1) {
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == guess) {

                    // 2 * i maps 0,1,2,3... to 0,2,4,6...
                    newPattern.setCharAt(2 * i, guess);
                }
            }
        }
        return newPattern.toString();
    }
}
