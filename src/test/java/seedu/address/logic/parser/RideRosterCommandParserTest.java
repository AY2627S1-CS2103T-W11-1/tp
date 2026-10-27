package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.parser.exceptions.ParseException;

public class RideRosterCommandParserTest {

    @Test
    public void parse_invalidId_logsOperationWithoutRawInput() {
        List<RideRosterCommandParser> parsers = List.of(new RideRosterCommandParser(true),
                new RideRosterCommandParser(false));
        Logger logger = LogsCenter.getLogger(RideRosterCommandParser.class);
        Level originalLevel = logger.getLevel();
        boolean originallyUsesParentHandlers = logger.getUseParentHandlers();
        List<String> messages = new ArrayList<>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                assertEquals(Level.FINE, record.getLevel());
                messages.add(record.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };
        logger.addHandler(handler);
        logger.setLevel(Level.FINE);
        logger.setUseParentHandlers(false);
        try {
            for (RideRosterCommandParser parser : parsers) {
                ParseException exception = assertThrows(ParseException.class, () ->
                        parser.parse("02147483648 +65 91234567"));
                assertEquals("Ride ID should be a positive whole number no larger than 2147483647.",
                        exception.getMessage());
            }
            assertEquals(List.of("Rejected invalid ride ID for roster addition.",
                    "Rejected invalid ride ID for roster removal."), messages);
        } finally {
            logger.removeHandler(handler);
            logger.setLevel(originalLevel);
            logger.setUseParentHandlers(originallyUsesParentHandlers);
        }
    }
}
