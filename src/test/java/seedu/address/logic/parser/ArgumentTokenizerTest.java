package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FTP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class ArgumentTokenizerTest {

    private final Prefix unknownPrefix = new Prefix("--u");
    private final Prefix pSlash = new Prefix("p/");
    private final Prefix dashT = new Prefix("-t");
    private final Prefix hatQ = new Prefix("^Q");

    @Test
    public void tokenizeWithQuotedNotes_prefixesInsideQuotes_areLiteral() throws Exception {
        for (String separator : List.of(" ", "\t", "\n", "\u2003")) {
            ArgumentMultimap arguments = ArgumentTokenizer.tokenizeWithQuotedNotes(
                    " 1 notes/\"Bring camera f/2 notes/test q/test\"" + separator + "f/200",
                    PREFIX_NOTES, PREFIX_FTP);
            assertPreamblePresent(arguments, "1");
            assertArgumentPresent(arguments, PREFIX_NOTES, "Bring camera f/2 notes/test q/test");
            assertArgumentPresent(arguments, PREFIX_FTP, "200");
        }
        ArgumentMultimap arguments = ArgumentTokenizer.tokenizeWithQuotedNotes(
                " f/200 notes/\"  camera  f/2  \\\\path  \"   ", PREFIX_NOTES, PREFIX_FTP);
        assertArgumentPresent(arguments, PREFIX_NOTES, "  camera  f/2  \\\\path  ");
        assertArgumentPresent(arguments, PREFIX_FTP, "200");
    }

    @Test
    public void tokenizeWithQuotedNotes_duplicatesOutsideQuotes_remainDuplicates() throws Exception {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenizeWithQuotedNotes(
                " f/180 notes/\"f/2 notes/literal\" f/200 notes/second", PREFIX_NOTES, PREFIX_FTP);
        assertArgumentPresent(arguments, PREFIX_FTP, "180", "200");
        assertArgumentPresent(arguments, PREFIX_NOTES, "f/2 notes/literal", "second");
        assertThrows(ParseException.class, () -> arguments.verifyNoDuplicatePrefixesFor(PREFIX_FTP));
        assertThrows(ParseException.class, () -> arguments.verifyNoDuplicatePrefixesFor(PREFIX_NOTES));
    }

    @Test
    public void tokenizeWithQuotedNotes_malformedQuotes_rejected() {
        for (String notes : List.of("\"camera f/2", "\"\"", "\"   \"", "\"\u2003\"", "\"\u00a0\"", "\"\u202f\"",
                "\"\u3000\"", "\"camera\"tail",
                "\"camera\" tail f/200", "\"camera\"f/200", "\"camera \"lens\"\"",
                "\"camera\" \"extra\"", "\"camera\" q/test", "\"camera \\\"lens\"")) {
            ParseException exception = assertThrows(ParseException.class, () ->
                    ArgumentTokenizer.tokenizeWithQuotedNotes(" notes/" + notes, PREFIX_NOTES, PREFIX_FTP), notes);
            assertEquals(ArgumentTokenizer.MESSAGE_INVALID_QUOTED_NOTES, exception.getMessage());
        }
    }

    @Test
    public void tokenizeWithQuotedNotes_unquotedAndOtherFields_keepExistingSemantics() throws Exception {
        for (String input : List.of(" notes/Bring water f/200", " f/200 notes/Bring water",
                " notes/Bring camera with aperture f/2", " notes/Bring \"camera\" f/200",
                " notes/ \"camera\" f/200", " f/\"200 notes/Bring water")) {
            ArgumentMultimap expected = ArgumentTokenizer.tokenize(input, PREFIX_NOTES, PREFIX_FTP);
            ArgumentMultimap actual = ArgumentTokenizer.tokenizeWithQuotedNotes(input, PREFIX_NOTES, PREFIX_FTP);
            assertEquals(expected.getAllValues(PREFIX_NOTES), actual.getAllValues(PREFIX_NOTES));
            assertEquals(expected.getAllValues(PREFIX_FTP), actual.getAllValues(PREFIX_FTP));
        }
        ArgumentMultimap ordinary = ArgumentTokenizer.tokenize(" notes/\"camera f/2\"", PREFIX_NOTES, PREFIX_FTP);
        assertArgumentPresent(ordinary, PREFIX_NOTES, "\"camera");
        assertArgumentPresent(ordinary, PREFIX_FTP, "2\"");
    }

    @Test
    public void tokenize_whitespaceSeparators_recognisesPrefixesWithoutSplittingValues() {
        for (String separator : java.util.List.of(" ", "  ", "\t", "\n", "\r\n")) {
            ArgumentMultimap arguments = ArgumentTokenizer.tokenize(
                    separator + "p/first" + separator + "-tsecond", pSlash, dashT);
            assertPreambleEmpty(arguments);
            assertArgumentPresent(arguments, pSlash, "first");
            assertArgumentPresent(arguments, dashT, "second");
        }
        assertPreamblePresent(ArgumentTokenizer.tokenize(" untouched ", new Prefix("")), "untouched");
        ArgumentMultimap embedded = ArgumentTokenizer.tokenize(" p/first-tsecond", pSlash, dashT);
        assertArgumentPresent(embedded, pSlash, "first-tsecond");
        assertArgumentAbsent(embedded, dashT);
    }

    @Test
    public void tokenize_emptyArgsString_noValues() {
        String argsString = "  ";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash);

        assertPreambleEmpty(argMultimap);
        assertArgumentAbsent(argMultimap, pSlash);
    }

    private void assertPreamblePresent(ArgumentMultimap argMultimap, String expectedPreamble) {
        assertEquals(expectedPreamble, argMultimap.getPreamble());
    }

    private void assertPreambleEmpty(ArgumentMultimap argMultimap) {
        assertTrue(argMultimap.getPreamble().isEmpty());
    }

    /**
     * Asserts all the arguments in {@code argMultimap} with {@code prefix} match the {@code expectedValues}
     * and only the last value is returned upon calling {@code ArgumentMultimap#getValue(Prefix)}.
     */
    private void assertArgumentPresent(ArgumentMultimap argMultimap, Prefix prefix, String... expectedValues) {

        // Verify the last value is returned
        assertEquals(expectedValues[expectedValues.length - 1], argMultimap.getValue(prefix).get());

        // Verify the number of values returned is as expected
        assertEquals(expectedValues.length, argMultimap.getAllValues(prefix).size());

        // Verify all values returned are as expected and in order
        for (int i = 0; i < expectedValues.length; i++) {
            assertEquals(expectedValues[i], argMultimap.getAllValues(prefix).get(i));
        }
    }

    private void assertArgumentAbsent(ArgumentMultimap argMultimap, Prefix prefix) {
        assertFalse(argMultimap.getValue(prefix).isPresent());
    }

    @Test
    public void tokenize_noPrefixes_allTakenAsPreamble() {
        String argsString = "  some random string /t tag with leading and trailing spaces ";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString);

        // Same string expected as preamble, but leading/trailing spaces should be trimmed
        assertPreamblePresent(argMultimap, argsString.trim());

    }

    @Test
    public void tokenize_oneArgument() {
        // Preamble present
        String argsString = "  Some preamble string p/ Argument value ";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash);
        assertPreamblePresent(argMultimap, "Some preamble string");
        assertArgumentPresent(argMultimap, pSlash, "Argument value");

        // No preamble
        argsString = " p/   Argument value ";
        argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash);
        assertPreambleEmpty(argMultimap);
        assertArgumentPresent(argMultimap, pSlash, "Argument value");

    }

    @Test
    public void tokenize_multipleArguments() {
        // Only two arguments are present
        String argsString = "SomePreambleString -t dashT-Value p/pSlash value";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "SomePreambleString");
        assertArgumentPresent(argMultimap, pSlash, "pSlash value");
        assertArgumentPresent(argMultimap, dashT, "dashT-Value");
        assertArgumentAbsent(argMultimap, hatQ);

        // All three arguments are present
        argsString = "Different Preamble String ^Q111 -t dashT-Value p/pSlash value";
        argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "Different Preamble String");
        assertArgumentPresent(argMultimap, pSlash, "pSlash value");
        assertArgumentPresent(argMultimap, dashT, "dashT-Value");
        assertArgumentPresent(argMultimap, hatQ, "111");

        /* Also covers: Reusing of the tokenizer multiple times */

        // Reuse tokenizer on an empty string to ensure ArgumentMultimap is correctly reset
        // (i.e. no stale values from the previous tokenizing remain)
        argsString = "";
        argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreambleEmpty(argMultimap);
        assertArgumentAbsent(argMultimap, pSlash);

        /* Also covers: testing for prefixes not specified as a prefix */

        // Prefixes not previously given to the tokenizer should not return any values
        argsString = unknownPrefix + "some value";
        argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertArgumentAbsent(argMultimap, unknownPrefix);
        assertPreamblePresent(argMultimap, argsString); // Unknown prefix is taken as part of preamble
    }

    @Test
    public void tokenize_multipleArgumentsWithRepeats() {
        // Two arguments repeated, some have empty values
        String argsString = "SomePreambleString -t dashT-Value ^Q ^Q -t another dashT value p/ pSlash value -t";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "SomePreambleString");
        assertArgumentPresent(argMultimap, pSlash, "pSlash value");
        assertArgumentPresent(argMultimap, dashT, "dashT-Value", "another dashT value", "");
        assertArgumentPresent(argMultimap, hatQ, "", "");
    }

    @Test
    public void tokenize_multipleArgumentsJoined() {
        String argsString = "SomePreambleStringp/ pSlash joined-tjoined -t not joined^Qjoined";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "SomePreambleStringp/ pSlash joined-tjoined");
        assertArgumentAbsent(argMultimap, pSlash);
        assertArgumentPresent(argMultimap, dashT, "not joined^Qjoined");
        assertArgumentAbsent(argMultimap, hatQ);
    }

    @Test
    public void equalsMethod() {
        Prefix aaa = new Prefix("aaa");

        assertEquals(aaa, aaa);
        assertEquals(aaa, new Prefix("aaa"));

        assertNotEquals(aaa, "aaa");
        assertNotEquals(aaa, new Prefix("aab"));
    }

}
