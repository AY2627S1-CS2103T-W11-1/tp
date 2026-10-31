package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * Helper functions for handling strings.
 */
public class StringUtil {

    /** Matches the characters that separate words: whitespace, hyphens and apostrophes. */
    private static final String WORD_SEPARATORS = "[\\s'-]+";

    /**
     * Returns whether all words in {@code searchText} occur in order in {@code text}, ignoring case.
     * Words are separated by whitespace, hyphens and apostrophes, so {@code "Wei-Ling"} matches
     * {@code "Tan Wei-Ling's Ride"} and {@code "Ling"} matches {@code "Wei-Ling"}, but a partial
     * word such as {@code "Lin"} does not match. Intervening words in {@code text} are allowed.
     *
     * @param text the text to search.
     * @param searchText the ordered words to find.
     * @return true if every search word occurs in order. False if {@code searchText} has no words.
     */
    public static boolean containsWordsInOrderIgnoreCase(String text, String searchText) {
        requireNonNull(text);
        requireNonNull(searchText);

        String[] textWords = splitIntoWords(text);
        String[] searchWords = splitIntoWords(searchText);
        if (searchWords.length == 0) {
            return false;
        }
        int textIndex = 0;
        for (String searchWord : searchWords) {
            while (textIndex < textWords.length && !textWords[textIndex].equalsIgnoreCase(searchWord)) {
                textIndex++;
            }
            if (textIndex == textWords.length) {
                return false;
            }
            textIndex++;
        }
        return true;
    }

    /**
     * Splits {@code text} into its non-empty words, using {@code WORD_SEPARATORS} as delimiters.
     */
    private static String[] splitIntoWords(String text) {
        return Arrays.stream(text.split(WORD_SEPARATORS))
                .filter(word -> !word.isEmpty())
                .toArray(String[]::new);
    }

    /**
     * Returns true if the {@code sentence} contains the {@code word}.
     *   Ignores case, but a full word match is required.
     *   <br>examples:<pre>
     *       containsWordIgnoreCase("ABc def", "abc") == true
     *       containsWordIgnoreCase("ABc def", "DEF") == true
     *       containsWordIgnoreCase("ABc def", "AB") == false //not a full word match
     *       </pre>
     * @param sentence cannot be null
     * @param word cannot be null, cannot be empty, must be a single word
     */
    public static boolean containsWordIgnoreCase(String sentence, String word) {
        requireNonNull(sentence);
        requireNonNull(word);

        String preppedWord = word.trim();
        checkArgument(!preppedWord.isEmpty(), "Word parameter cannot be empty");
        checkArgument(preppedWord.split("\\s+").length == 1, "Word parameter should be a single word");

        String preppedSentence = sentence;
        String[] wordsInPreppedSentence = preppedSentence.split("\\s+");

        return Arrays.stream(wordsInPreppedSentence)
                .anyMatch(preppedWord::equalsIgnoreCase);
    }

    /**
     * Returns a detailed message of {@code t}, including the stack trace.
     */
    public static String getDetails(Throwable t) {
        requireNonNull(t);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return t.getMessage() + "\n" + sw.toString();
    }

    /**
     * Returns true if {@code s} represents a non-zero unsigned integer
     * e.g. 1, 2, 3, ..., {@code Integer.MAX_VALUE} <br>
     * Will return false for any other non-null string input
     * e.g. empty string, "-1", "0", "+1", and " 2 " (untrimmed), "3 0" (contains whitespace), "1 a" (contains letters)
     * @throws NullPointerException if {@code s} is null.
     */
    public static boolean isNonZeroUnsignedInteger(String s) {
        requireNonNull(s);

        try {
            int value = Integer.parseInt(s);
            return value > 0 && !s.startsWith("+"); // "+1" is successfully parsed by Integer#parseInt(String)
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
