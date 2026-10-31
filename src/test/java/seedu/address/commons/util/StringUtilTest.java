package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

public class StringUtilTest {

    //---------------- Tests for isNonZeroUnsignedInteger --------------------------------------

    @Test
    public void isNonZeroUnsignedInteger() {

        // EP: empty strings
        assertFalse(StringUtil.isNonZeroUnsignedInteger("")); // Boundary value: 0 input characters.
        assertFalse(StringUtil.isNonZeroUnsignedInteger("  "));

        // EP: not a number
        assertFalse(StringUtil.isNonZeroUnsignedInteger("a"));
        assertFalse(StringUtil.isNonZeroUnsignedInteger("aaa"));

        // EP: zero
        assertFalse(StringUtil.isNonZeroUnsignedInteger("0"));

        // EP: zero as prefix
        assertTrue(StringUtil.isNonZeroUnsignedInteger("01"));

        // EP: signed numbers
        assertFalse(StringUtil.isNonZeroUnsignedInteger("-1"));
        assertFalse(StringUtil.isNonZeroUnsignedInteger("+1"));

        // EP: numbers with white space
        assertFalse(StringUtil.isNonZeroUnsignedInteger(" 10 ")); // Leading/trailing spaces
        assertFalse(StringUtil.isNonZeroUnsignedInteger("1 0")); // Spaces in the middle

        // EP: negative text; int arithmetic wraps to Integer.MIN_VALUE before Long.toString is called.
        // This assertion does not exercise an unsigned number above Integer.MAX_VALUE.
        assertFalse(StringUtil.isNonZeroUnsignedInteger(Long.toString(Integer.MAX_VALUE + 1)));

        // EP: valid numbers, should return true
        assertTrue(StringUtil.isNonZeroUnsignedInteger("1")); // Boundary value: smallest valid integer.
        assertTrue(StringUtil.isNonZeroUnsignedInteger("10"));
    }


    //---------------- Tests for containsWordIgnoreCase --------------------------------------

    /*
     * Invalid equivalence partitions for word: null, empty, multiple words
     * Invalid equivalence partitions for sentence: null
     * The four test cases below test one invalid input at a time.
     */

    @Test
    public void containsWordIgnoreCase_nullWord_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StringUtil.containsWordIgnoreCase("typical sentence", null));
    }

    @Test
    public void containsWordIgnoreCase_emptyWord_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, "Word parameter cannot be empty", ()
            -> StringUtil.containsWordIgnoreCase("typical sentence", "  "));
    }

    @Test
    public void containsWordIgnoreCase_multipleWords_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, "Word parameter should be a single word", ()
            -> StringUtil.containsWordIgnoreCase("typical sentence", "aaa BBB"));
    }

    @Test
    public void containsWordIgnoreCase_nullSentence_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StringUtil.containsWordIgnoreCase(null, "abc"));
    }

    /*
     * Valid equivalence partitions for word:
     *   - any word
     *   - word containing symbols/numbers
     *   - word with leading/trailing spaces
     *
     * Valid equivalence partitions for sentence:
     *   - empty string
     *   - one word
     *   - multiple words
     *   - sentence with extra spaces
     *
     * Possible scenarios returning true:
     *   - matches first word in sentence
     *   - last word in sentence
     *   - middle word in sentence
     *   - matches multiple words
     *
     * Possible scenarios returning false:
     *   - query word matches part of a sentence word
     *   - sentence word matches part of the query word
     *
     * The test method below tries to verify all above with a reasonably low number of test cases.
     */

    @Test
    public void containsWordIgnoreCase_validInputs_correctResult() {

        // Empty sentence
        assertFalse(StringUtil.containsWordIgnoreCase("", "abc")); // Boundary value: sentence length 0.
        assertFalse(StringUtil.containsWordIgnoreCase("    ", "123"));

        // Matches a partial word only
        assertFalse(StringUtil.containsWordIgnoreCase("aaa bbb ccc", "bb")); // Sentence word bigger than query word
        assertFalse(StringUtil.containsWordIgnoreCase("aaa bbb ccc", "bbbb")); // Query word bigger than sentence word

        // Matches word in the sentence, different upper/lower case letters
        // EP: whole-word match in the middle of the sentence.
        assertTrue(StringUtil.containsWordIgnoreCase("aaa bBb ccc", "Bbb"));
        // EP: whole-word match at the end of the sentence.
        assertTrue(StringUtil.containsWordIgnoreCase("aaa bBb ccc@1", "CCc@1"));
        assertTrue(StringUtil.containsWordIgnoreCase("  AAA   bBb   ccc  ", "aaa")); // Sentence has extra spaces
        // Boundary value: sentence contains exactly 1 word.
        assertTrue(StringUtil.containsWordIgnoreCase("Aaa", "aaa"));
        assertTrue(StringUtil.containsWordIgnoreCase("aaa bbb ccc", "  ccc  ")); // Leading/trailing spaces

        // Matches multiple words in sentence
        assertTrue(StringUtil.containsWordIgnoreCase("AAA bBb ccc  bbb", "bbB"));
    }

    //---------------- Tests for containsWordsInOrderIgnoreCase -----------------------------

    @Test
    public void containsWordsInOrderIgnoreCase_nullInput_throwsNullPointerException() {
        // EP: null text and null search text are invalid inputs.
        assertThrows(NullPointerException.class, ()
                -> StringUtil.containsWordsInOrderIgnoreCase(null, "abc"));
        assertThrows(NullPointerException.class, ()
                -> StringUtil.containsWordsInOrderIgnoreCase("abc", null));
    }

    @Test
    public void containsWordsInOrderIgnoreCase_validInputs_correctResult() {
        // EP: one complete word matches regardless of case.
        assertTrue(StringUtil.containsWordsInOrderIgnoreCase("Sunday Sunrise Ride", "sunday"));
        // EP: Unicode case-insensitive matching preserves existing find behaviour.
        assertTrue(StringUtil.containsWordsInOrderIgnoreCase("İsmail Yilmaz", "ismail"));
        // EP: complete words may be separated by intervening words.
        assertTrue(StringUtil.containsWordsInOrderIgnoreCase("Sunday Sunrise Ride", "Sunday Ride"));
        // EP: location-style matching also preserves word order.
        assertTrue(StringUtil.containsWordsInOrderIgnoreCase("East Coast Park", "east park"));
        // EP: reversed word order does not match.
        assertFalse(StringUtil.containsWordsInOrderIgnoreCase("Sunday Sunrise Ride", "ride sunday"));
        // EP: a partial word fragment does not match.
        assertFalse(StringUtil.containsWordsInOrderIgnoreCase("Sunday Sunrise Ride", "Rise"));
        // Boundary value: whitespace-only search text does not match.
        assertFalse(StringUtil.containsWordsInOrderIgnoreCase("Sunday Sunrise Ride", "  "));
    }

    @Test
    public void containsWordsInOrderIgnoreCase_hyphensAndApostrophes_actAsWordSeparators() {
        // EP: a hyphenated or apostrophised search term matches the same spelling in the text.
        assertTrue(StringUtil.containsWordsInOrderIgnoreCase("Tan Wei-Ling", "wei-ling"));
        assertTrue(StringUtil.containsWordsInOrderIgnoreCase("John O'Connor", "O'Connor"));
        // EP: each part of a hyphenated or apostrophised word is a complete word on its own.
        assertTrue(StringUtil.containsWordsInOrderIgnoreCase("Tan Wei-Ling", "Ling"));
        assertTrue(StringUtil.containsWordsInOrderIgnoreCase("John O'Connor", "Connor"));
        assertTrue(StringUtil.containsWordsInOrderIgnoreCase("Punggol-Seletar Loop", "Seletar Loop"));
        // EP: a search term without punctuation matches the words before a possessive apostrophe.
        assertTrue(StringUtil.containsWordsInOrderIgnoreCase("Mother's Day Ride", "Mother"));
        // EP: a search term may stop short of the text's trailing punctuation.
        assertTrue(StringUtil.containsWordsInOrderIgnoreCase("Wei-Ling's Ride", "Wei-Ling"));
        // EP: several separators in the text and the search term can be skipped over in order.
        assertTrue(StringUtil.containsWordsInOrderIgnoreCase("Coast-to-Coast", "Coast Coast"));
        // EP: word order still matters across separators.
        assertFalse(StringUtil.containsWordsInOrderIgnoreCase("Tan Wei-Ling", "Ling Wei"));
        // EP: a partial word or a word with its separator removed does not match.
        assertFalse(StringUtil.containsWordsInOrderIgnoreCase("Tan Wei-Ling", "Lin"));
        assertFalse(StringUtil.containsWordsInOrderIgnoreCase("Tan Wei-Ling", "WeiLing"));
    }

    @Test
    public void containsWordsInOrderIgnoreCase_separatorsOnlySearchText_returnsFalse() {
        // Boundary values: search text with no words must not match everything.
        for (String searchText : new String[] {"", "-", "'", " - ", "-'-"}) {
            assertFalse(StringUtil.containsWordsInOrderIgnoreCase("Tan Wei-Ling", searchText), searchText);
        }
        // Boundary values: leading and trailing separators do not create empty words.
        assertTrue(StringUtil.containsWordsInOrderIgnoreCase("-Tan Wei-", "Tan Wei"));
        assertTrue(StringUtil.containsWordsInOrderIgnoreCase("Tan Wei-Ling", "-Ling-"));
    }

    //---------------- Tests for getDetails --------------------------------------

    /*
     * Equivalence Partitions: null, valid throwable object
     */

    @Test
    public void getDetails_exceptionGiven() {
        assertTrue(StringUtil.getDetails(new FileNotFoundException("file not found"))
            .contains("java.io.FileNotFoundException: file not found"));
    }

    @Test
    public void getDetails_nullGiven_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StringUtil.getDetails(null));
    }

}
