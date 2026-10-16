package seedu.address.model.ride;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NotesTest {

    private static final String TEN_WORDS = "one two three four five six seven eight nine ten";

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
