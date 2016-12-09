import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * Class HangmanGUI includes a static main method that reads a dictionary of
 * words to be used during the game, then plays a game with the user via the
 * console.
 *
 * @author Dr. Jody Paul
 * @version 1.1
 */
public final class HangmanGUI {
    /** Location (filename) of dictionary file. */
    public static final String DICTIONARY_FILE = "dictionary.txt";
    /** Location (filename) of icon image file. */
    public static final String ICON_IMAGE_FILE = "iconimage.gif";
    /** The icon to use for JOptionPane dialogs. */
    private static ImageIcon icon;

    /** Whether or not to show the number of word choices. */
    public static final boolean SHOW_COUNT = false;

    /** Default word length. */
    private static final int WORD_LENGTH = 5;
    /** Default number of wrong guesses. */
    private static final int WRONG_GUESSES = 7;

    /**
     * Ensure no visible constructor for this utility class.
     */
    private HangmanGUI() {
    }

    /**
     * Driver to run the hangman game.
     *
     * @param args
     *            ignored
     * @throws FileNotFoundException
     *             if dictionary file not available
     */
    public static void main(final String[] args) throws FileNotFoundException {
        int length = WORD_LENGTH; // The number of characters in guessed word.
        int max = WRONG_GUESSES; // The maximum number of wrong guesses allowed.
        String prompt = ""; // Variable used to construct user prompts.

        // Set up the icon for JOptionPane use.
        icon = new ImageIcon(ICON_IMAGE_FILE);
        UIManager.put(icon, "OptionPane.informationIcon");

        // Open the dictionary file and read dictionary into a List.
        Scanner input = new Scanner(new File(DICTIONARY_FILE));
        List<String> dictionary = new ArrayList<String>();
        while (input.hasNext()) {
            dictionary.add(input.next().toLowerCase());
        }
        length = Integer.parseInt(
                askUser("Welcome to the Hangman word-guessing game.\n\n"
                        + "What length word do you want to use?"));

        max = Integer.parseInt(askUser("How many wrong answers allowed?"));

        // Set up a HangMgr and start the game.
        List<String> dictionary2 = Collections.unmodifiableList(dictionary);
        HangmanManager hanager = new Hangman(dictionary2, length, max);
        if (hanager.words().isEmpty()) {
            tellUser("No words of that length in the dictionary.");
        } else {
            playGame(hanager);
            showResults(hanager);
            while (confirmUser("Would you like to play again?")) {
                hanager = new Hangman(dictionary2, length, max);
                playGame(hanager);
                showResults(hanager);
            }
        }
    }

    /**
     * Plays one game with the user.
     *
     * @param hanager
     *            the hangman manager
     */
    private static void playGame(final HangmanManager hanager) {
        String prompt = "";
        String response = "";
        while (hanager.guessesLeft() > 0 && hanager.pattern().contains("-")) {
            prompt += "guesses left: " + hanager.guessesLeft();
            if (SHOW_COUNT) {
                prompt += "\nwords   : " + hanager.words().size();
            }
            prompt += "\nguessed : " + hanager.guesses();
            prompt += "\ncurrent : " + hanager.pattern();
            prompt += "\n\nYour guess? ";
            response = "";
            while (response.length() < 1) {
                response = askUser(prompt);
            }
            char ch = response.toLowerCase().charAt(0);
            if (hanager.guesses().contains(ch)) {
                tellUser("You already guessed '" + ch + "' ");
                prompt = "";
            } else {
                int count = hanager.record(ch);
                if (count == 0) {
                    prompt = "Sorry, there are no " + ch + "'s\n\n";
                } else if (count == 1) {
                    prompt = "Yes, there is one " + ch + "\n\n";
                } else {
                    prompt = "Yes, there are " + count + " " + ch + "'s\n\n";
                }
            }
        }
    }

    /**
     * Prompt the user to enter a response.
     *
     * @param prompt
     *            the prompt to provide to the user
     * @return string entered by user
     */
    private static String askUser(final String prompt) {
        String response = (String) JOptionPane.showInputDialog(null, prompt,
                "Hangman Game", JOptionPane.QUESTION_MESSAGE, icon, null, null);
        if (null == response) {
            System.exit(-1);
        }
        return response;
    }

    /**
     * Prompt the user to respond with YES or NO.
     *
     * @param prompt
     *            the prompt to provide to the user
     * @return true if user selects YES, false otherwise
     */
    private static boolean confirmUser(final String prompt) {
        return (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                prompt, "Hangman Game", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, icon));
    }

    /**
     * Provide information to the user.
     *
     * @param info
     *            the information to provide to the user
     */
    private static void tellUser(final String info) {
        JOptionPane.showMessageDialog(null, info, "Hangman Message",
                JOptionPane.INFORMATION_MESSAGE, icon);
    }

    /**
     * Report the results of the game, including showing the answer if
     * appropriate.
     *
     * @param hanager
     *            the hangman play manager
     */
    private static void showResults(final HangmanManager hanager) {
        // Retrieve and display goal (the first word in the list).
        String answer = "";
        answer += "guessed : " + hanager.guesses();
        answer += "\n\ncurrent : " + hanager.pattern();
        answer += "\n\nAnswer = ";
        answer += hanager.words().iterator().next();
        // Indicate win or lose.
        if (hanager.guessesLeft() > 0) {
            answer += "\n\nYou beat me!";
        } else {
            answer += "\n\nSorry, you lose.";
        }
        tellUser(answer);
    }
}
