package seedu.address.ui;

/**
 * Helpers for the text shown in labels.
 */
final class DisplayText {

    private static final char NO_BREAK_SPACE = ' ';

    private DisplayText() {} // prevents instantiation

    /**
     * Returns {@code text} with every space replaced by a no-break space, so that a label which wraps its text
     * never splits the value in the middle, for example the phone number {@code +65 91234567} or {@code 45 km}.
     */
    static String keepTogether(String text) {
        return text.replace(' ', NO_BREAK_SPACE);
    }
}
