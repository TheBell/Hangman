import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

public class HangmanTest {

    public String TEST_DICTIONARY = "test_dictionary.txt";

    /**
     * Used to read a file containing words to use in a Hangman game. It assumes
     * that the file input is valid.
     *
     * @param fileName
     *            the name of the file to use
     * @param length
     *            the length of word to choose
     * @param max
     *            the max number of missed guesses allowed
     * @return a Hangman game based on the input
     * @throws FileNotFoundException
     *             if the file is not found
     */
    public Hangman testGame(final String fileName, final int length,
            final int max) throws FileNotFoundException {
        Scanner input = new Scanner(new File(fileName));
        List<String> dictionary = new ArrayList<>();
        while (input.hasNext()) {
            dictionary.add(input.next().toLowerCase());
        }
        return new Hangman(dictionary, length, max);
    }

    /**
     * Tests the word method and ensures that the set is built correctly.
     *
     * @throws FileNotFoundException
     *             if the file is not found
     */
    @Test
    public void testWords() throws FileNotFoundException {
        Hangman testGame = testGame(TEST_DICTIONARY, 4, 5);
        assertEquals(5, testGame.words().size());

        testGame = testGame(TEST_DICTIONARY, 5, 5);
        assertEquals(1, testGame.words().size());
    }

    /**
     * Tests to make sure that guessesLeft works correctly.
     *
     * @throws FileNotFoundException
     *             if the file is not found
     */
    @Test
    public void testGuessesLeft() throws FileNotFoundException {
        Hangman testGame = testGame(TEST_DICTIONARY, 5, 5); // Word has to
                                                            // be "berry"
        assertEquals(5, testGame.guessesLeft());

        testGame.record('z'); // no z in berry
        assertEquals(4, testGame.guessesLeft());

        testGame.record('b');
        assertEquals(4, testGame.guessesLeft());
    }

    @Test
    public void testPattern() throws FileNotFoundException {
        Hangman testGame = testGame(TEST_DICTIONARY, 5, 5); // Word has to be
                                                            // "berry"
        assertEquals("- - - - -", testGame.pattern());

        testGame.record('r');
        assertEquals("- - r r -", testGame.pattern());
    }

    @Test
    public void testRecord() throws FileNotFoundException {
        Hangman testGame = testGame(TEST_DICTIONARY, 5, 5); // Word has to be
                                                            // "berry"
        assertEquals(2, testGame.record('r'));
    }
}
