package seedu.address.model.person;

import java.util.Objects;

/**
 * Represents a person's Functional Threshold Power in watts.
 */
public class Ftp {
    public static final String MESSAGE_CONSTRAINTS = "FTP should be a whole number between 1 and 600 watts.";
    private static final int MIN_FTP = 1;
    private static final int MAX_FTP = 600;
    public final String value;

    /**
     * Constructs an {@code Ftp} with the given value.
     */
    public Ftp(String value) {
        if (!isValidFtp(value)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        this.value = value;
    }

    /**
     * Returns true if the given string is a valid FTP value.
     */
    public static boolean isValidFtp(String test) {
        try {
            int ftp = Integer.parseInt(test);
            return ftp >= MIN_FTP && ftp <= MAX_FTP;
        } catch (NumberFormatException | NullPointerException exception) {
            return false;
        }
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Ftp otherFtp && value.equals(otherFtp.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
