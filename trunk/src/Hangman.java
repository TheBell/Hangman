import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
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

    /** The words of given length from the dictionary. */
    private Set<String> words;

    /** The number of incorrect-guess left. */
    private int guessesLeft;

    /** The guesses already made. */
    private SortedSet<Character> guesses;

    /** The pattern to display. */
    private StringBuilder pattern;

    /** The answer chosen from the set of possible words. */
    private String answer;

    /**
     * A set containing only the answer. Returned to HangmanConsole and
     * HangmanConsole so they display the correct answer.
     */
    private Set<String> answerSet;

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
        loadDictionaryAndFindWordsOfDesiredLength(dictionary, length);
        setInitialPattern(length);
        guessesLeft = max;
        guesses = new TreeSet<>();
        if (words.size() >= 1) {
            chooseAnswer();
        }
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
        words = new TreeSet<String>();
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
     * Chooses the answer from the word-list randomly. Reduces the effective
     * word list to just that one answer.
     */
    private void chooseAnswer() {
        assert words.size() > 0;

        Random rand = new Random();
        int randInt = rand.nextInt(words.size());
        Iterator<String> it = words.iterator();
        for (int i = 0; i <= randInt; i++) {
            answer = it.next();
        }

        answerSet = new TreeSet<>();
        answerSet.add(answer);
    }

    /**
     * Returns a set containing only the answer.
     *
     * @see HangmanManager#words()
     */
    @Override
    public Set<String> words() {
        if (answer != null) {
            return new TreeSet<>(answerSet);
        }
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
        if (guessesLeft() < 1 || answer == null) {
            throw new IllegalStateException("No guesses left");
        }
        if (guesses.contains(guess)) {
            throw new IllegalArgumentException(
                    guess + " has already been guessed");
        }
        guesses.add(guess);
        int totalFound = 0;

        if (answer.indexOf(guess) >= 0) {
            for (int i = 0; i < answer.length(); i++) {
                if (answer.charAt(i) == guess) {
                    totalFound++;
                    // This pattern maps the series 0,1,2,3 to 0,2,4,6 because
                    // of the spaces in the pattern
                    pattern.setCharAt((i * 2), guess);
                }
            }
        } else {
            guessesLeft--;
        }
        return totalFound;
    }

}
