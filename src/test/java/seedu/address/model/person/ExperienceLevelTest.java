package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class ExperienceLevelTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ExperienceLevel(null));
    }

    @Test
    public void constructor_invalidExperienceLevel_throwsIllegalArgumentException() {
        String invalidExperienceLevel = "Expert";
        assertThrows(IllegalArgumentException.class, () -> new ExperienceLevel(invalidExperienceLevel));
    }

    @Test
    public void constructor_validExperienceLevelDifferentCase_storesCanonicalForm() {
        assertEquals("Beginner", new ExperienceLevel("beginner").value);
        assertEquals("Intermediate", new ExperienceLevel("INTERMEDIATE").value);
        assertEquals("Advanced", new ExperienceLevel("aDvAnCeD").value);
    }

    @Test
    public void isValidExperienceLevel() {
        // null experience level
        assertThrows(NullPointerException.class, () -> ExperienceLevel.isValidExperienceLevel(null));

        // invalid experience levels
        assertFalse(ExperienceLevel.isValidExperienceLevel("")); // empty string
        assertFalse(ExperienceLevel.isValidExperienceLevel(" ")); // spaces only
        assertFalse(ExperienceLevel.isValidExperienceLevel("Expert")); // not one of the three valid levels
        assertFalse(ExperienceLevel.isValidExperienceLevel("Beginner ")); // trailing whitespace not trimmed here

        // valid experience levels
        assertTrue(ExperienceLevel.isValidExperienceLevel("Beginner"));
        assertTrue(ExperienceLevel.isValidExperienceLevel("Intermediate"));
        assertTrue(ExperienceLevel.isValidExperienceLevel("Advanced"));
        assertTrue(ExperienceLevel.isValidExperienceLevel("advanced")); // case-insensitive
    }

    @Test
    public void equals() {
        ExperienceLevel experienceLevel = new ExperienceLevel("Beginner");

        // same values -> returns true
        assertTrue(experienceLevel.equals(new ExperienceLevel("Beginner")));

        // same object -> returns true
        assertTrue(experienceLevel.equals(experienceLevel));

        // null -> returns false
        assertFalse(experienceLevel.equals(null));

        // different types -> returns false
        assertFalse(experienceLevel.equals(5.0f));

        // different values -> returns false
        assertFalse(experienceLevel.equals(new ExperienceLevel("Advanced")));
    }
}
