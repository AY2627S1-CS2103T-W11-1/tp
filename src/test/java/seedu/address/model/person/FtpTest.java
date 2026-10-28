package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class FtpTest {
    @Test
    public void constructor_invalidValue_throwsIllegalArgumentException() {
        String[] invalidValues = {null, "", " ", "0", "-1", "601", "300.5", "abc", "2147483648"};
        for (String value : invalidValues) {
            assertFalse(Ftp.isValidFtp(value));
            assertThrows(IllegalArgumentException.class, Ftp.MESSAGE_CONSTRAINTS, () -> new Ftp(value));
        }
    }

    @Test
    public void constructor_validBoundaries_preservesValue() {
        for (String value : new String[] {"1", "300", "600"}) {
            assertTrue(Ftp.isValidFtp(value));
            assertEquals(value, new Ftp(value).toString());
        }
    }

    @Test
    public void equalsAndHashCode() {
        Ftp ftp = new Ftp("300");
        assertTrue(ftp.equals(ftp));
        assertTrue(ftp.equals(new Ftp("300")));
        assertEquals(ftp.hashCode(), new Ftp("300").hashCode());
        assertFalse(ftp.equals(new Ftp("301")));
        assertFalse(ftp.equals(null));
        assertFalse(ftp.equals("300"));
    }
}
