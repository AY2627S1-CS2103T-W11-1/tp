package seedu.address.model.ride;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Collections;

import org.junit.jupiter.api.Test;

public class NotesTest {

    private static final String TEN_WORDS = "one two three four five six seven eight nine ten";

    @Test
    public void isBlank_usesTheSameWhitespaceDefinitionAsValidation() {
        assertThrows(NullPointerException.class, () -> Notes.isBlank(null));
        assertTrue(Notes.isBlank(""));
        assertTrue(Notes.isBlank(" \t\r\n\u00a0\u2003\u202f\u3000"));
        assertFalse(Notes.isBlank("\u00a0Bring\u2003"));
        assertFalse(Notes.isBlank("\u200b"));
    }

    @Test
    public void constructor_unicodeSeparators_normalizesAndEnforcesWordLimit() {
        for (String separator : new String[] {" ", "\t", "\r\n", "\u00a0", "\u2003", "\u202f", "\u3000"}) {
            assertFalse(Notes.isValidNotes(separator));
            assertThrows(IllegalArgumentException.class, () -> new Notes(separator));
            for (int words = 1; words <= Notes.MAX_WORDS; words++) {
                String expected = String.join(" ", Collections.nCopies(words, "word"));
                String input = separator + expected.replace(" ", separator + separator) + separator;
                assertEquals(expected, new Notes(input).value);
            }
            String eleven = (TEN_WORDS + " eleven").replace(" ", separator);
            assertFalse(Notes.isValidNotes(eleven));
            assertThrows(IllegalArgumentException.class, () -> new Notes(eleven));
        }
    }

    @Test
    public void constructor_mixedSeparators_preservesUnicodeCasePunctuationAndPrefixes() {
        assertEquals("Bring CAF\u00c9, \u6c34! f/2 \ud83d\udeb2\u200b", new Notes(
                "\u3000Bring\u00a0\tCAF\u00c9,\u2003\u6c34!\r\nf/2\u202f\ud83d\udeb2\u200b\u3000").value);
        assertFalse(Notes.isValidNotes(" \t\r\n\u00a0\u2003\u202f\u3000"));
        assertFalse(Notes.isValidNotes("one\ttwo\nthree\u00a0four\u2003five\u202fsix\u3000seven "
                + "eight\u2003\u00a0nine ten eleven"));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Notes(null));
    }

    @Test
    public void constructor_invalidNotes_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Notes(" "));
    }

    @Test
    public void isValidNotes() {
        // null notes
        assertThrows(NullPointerException.class, () -> Notes.isValidNotes(null));

        // invalid notes
        assertFalse(Notes.isValidNotes("")); // empty string
        assertFalse(Notes.isValidNotes("   ")); // spaces only
        assertFalse(Notes.isValidNotes(TEN_WORDS + " eleven")); // 11 words
        assertFalse(Notes.isValidNotes(TEN_WORDS + "   eleven twelve")); // more than 10 words with extra spaces

        // valid notes
        assertTrue(Notes.isValidNotes("Bring lights")); // two words
        assertTrue(Notes.isValidNotes("Bring")); // one word
        assertTrue(Notes.isValidNotes(TEN_WORDS)); // exactly 10 words
        assertTrue(Notes.isValidNotes("  " + TEN_WORDS + "  ")); // surrounding spaces do not count
        assertTrue(Notes.isValidNotes("Bring lights, a spare tube & snacks!")); // punctuation is allowed
        assertTrue(Notes.isValidNotes("one  two   three")); // repeated spaces are one separator
    }

    @Test
    public void constructor_repeatedSpaces_areCollapsed() {
        assertEquals("Bring lights and snacks", new Notes("  Bring   lights  and snacks ").value);
    }

    @Test
    public void equals() {
        Notes notes = new Notes("Bring lights");

        // same values -> returns true
        assertTrue(notes.equals(new Notes("Bring lights")));

        // same words with different spacing -> returns true
        assertTrue(notes.equals(new Notes("Bring   lights")));

        // same object -> returns true
        assertTrue(notes.equals(notes));

        // null -> returns false
        assertFalse(notes.equals(null));

        // different types -> returns false
        assertFalse(notes.equals(5.0f));

        // different values -> returns false
        assertFalse(notes.equals(new Notes("Bring snacks")));
    }

    @Test
    public void hashCode_equalValues_haveEqualHashCodes() {
        assertEquals(new Notes("Bring lights").hashCode(), new Notes("Bring   lights").hashCode());
    }

    @Test
    public void toString_returnsValue() {
        assertEquals("Bring lights", new Notes("Bring lights").toString());
    }
}
