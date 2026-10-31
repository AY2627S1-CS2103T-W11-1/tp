package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // EP: blank names and names containing unsupported punctuation.
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains non-alphanumeric characters

        // EP: letter-only, digit-only, and mixed names, including spaces and mixed case.
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
    }

    @Test
    public void isValidName_unicodeLettersDigitsAndMarks_returnsTrue() {
        // EP: precomposed/decomposed accents, non-Latin scripts, Unicode digits, and supplementary letters.
        // Spaces within or after a valid name do not create an invalid leading character.
        for (String name : new String[] {"José Tan", "Jose\u0301 Tan", "Zoë", "李明", "அருண்", "محمد",
            "Rider ٢", "𐐀 Rider", "José  Tan", "José Tan "}) {
            assertTrue(Name.isValidName(name), name);
            assertEquals(name, new Name(name).fullName);
        }
    }

    @Test
    public void isValidName_internalHyphensAndApostrophes_preservesSpelling() {
        // EP: single and mixed separators, accents, non-Latin letters, Unicode digits, and supplementary letters.
        // Multiple separators are valid when each joins letter/digit sequences; preserve the exact spelling.
        for (String name : new String[] {"Tan Wei-Ling", "John O'Connor", "Anne-Marie O'Neill", "D'Arcy-Smith",
            "Jose\u0301-Tan", "O'Jose\u0301", "李-明", "Rider٢-3", "𐐀-Rider", "A-B-C", "A'B'C"}) {
            assertTrue(Name.isValidName(name), name);
            assertEquals(name, new Name(name).fullName);
        }
    }

    @Test
    public void isValidName_misplacedHyphensAndApostrophes_returnsFalse() {
        // EP: punctuation-only names, leading/trailing separators, consecutive separators, and adjacent spaces.
        // A combining mark after a separator is detached because it has no preceding letter or digit.
        for (String name : new String[] {"-", "'", "-Tan", "'Tan", "Tan-", "Tan'", "Tan--Wei", "O''Connor",
            "Tan-'Wei", "Tan'-Wei", "Tan -Wei", "Tan- Wei", "John 'Connor", "John' Connor",
            "Jose-\u0301Tan", "Jose'\u0301Tan"}) {
            assertFalse(Name.isValidName(name), name);
            assertThrows(IllegalArgumentException.class, () -> new Name(name));
        }
    }

    @Test
    public void isValidName_invalidCharactersOrDetachedMarks_returnsFalse() {
        // EP: leading spaces, detached combining marks, punctuation, control whitespace, and emoji.
        for (String name : new String[] {" José", "\u0301Jose", "\u0301", "Jose \u0301Tan",
            "José_Tan", "José\tTan", "José\nTan", "Rider 🚲"}) {
            assertFalse(Name.isValidName(name), name);
            assertThrows(IllegalArgumentException.class, () -> new Name(name));
        }
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
