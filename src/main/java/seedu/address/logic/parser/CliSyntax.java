package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_PHONE = new Prefix("p/");
    public static final Prefix PREFIX_EMERGENCY_PHONE = new Prefix("sos/");
    public static final Prefix PREFIX_EMAIL = new Prefix("e/");
    public static final Prefix PREFIX_FTP = new Prefix("f/");
    public static final Prefix PREFIX_LOCATION = new Prefix("l/");
    public static final Prefix PREFIX_EXPERIENCE_LEVEL = new Prefix("x/");

    /* Ride prefix definitions. Rides also use PREFIX_NAME, PREFIX_LOCATION and PREFIX_FTP (minimum power). */
    public static final Prefix PREFIX_FROM = new Prefix("from/");
    public static final Prefix PREFIX_TO = new Prefix("to/");
    public static final Prefix PREFIX_DISTANCE = new Prefix("d/");
    public static final Prefix PREFIX_GROUP_SIZE = new Prefix("s/");
    public static final Prefix PREFIX_NOTES = new Prefix("notes/");

}
