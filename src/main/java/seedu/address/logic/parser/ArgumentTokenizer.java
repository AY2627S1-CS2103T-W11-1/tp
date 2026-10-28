package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ride.Notes;

/**
 * Tokenizes arguments string of the form: {@code preamble <prefix>value <prefix>value ...}<br>
 *     e.g. {@code some preamble text l/ 11.00 l/12.00 k/ m/ July}  where prefixes are {@code l/ k/ m/}.<br>
 * 1. An argument's value can be an empty string e.g. the value of {@code k/} in the above example.<br>
 * 2. Leading and trailing whitespaces of an argument value will be discarded.<br>
 * 3. An argument may be repeated and all its values will be accumulated e.g. the value of {@code l/}
 *    in the above example.<br>
 */
public class ArgumentTokenizer {

    public static final String MESSAGE_INVALID_QUOTED_NOTES =
            "Invalid quoted ride notes. Use notes/\"non-blank notes\" followed only by whitespace "
            + "and another recognised parameter, or the end of the command. "
            + "Double quotes inside quoted notes and escape sequences are not supported.";

    /**
     * Tokenizes ride add/edit arguments, allowing an optional double-quoted notes value.
     * Only an opening quote immediately after notes/ enables quoting. The first following quote closes
     * the value; backslashes are literal. Prefixes inside that value are ignored, and delimiters are removed.
     * All other fields and unquoted values retain the behavior of {@link #tokenize(String, Prefix...)}.
     *
     * @throws ParseException if quoted notes are blank, unclosed, or followed by text before the next prefix.
     */
    public static ArgumentMultimap tokenizeWithQuotedNotes(String argsString, Prefix... prefixes)
            throws ParseException {
        List<PrefixPosition> positions = findAllPrefixPositions(argsString, prefixes);
        positions.sort(Comparator.comparingInt(PrefixPosition::getStartPosition));
        List<PrefixPosition> outsidePositions = new ArrayList<>();
        int quotedEnd = -1;
        for (int i = 0; i < positions.size(); i++) {
            PrefixPosition position = positions.get(i);
            if (position.getStartPosition() < quotedEnd) {
                continue;
            }
            outsidePositions.add(position);
            if (hasOpeningNotesQuote(argsString, position)) {
                quotedEnd = findValidatedClosingQuote(argsString, positions, i);
            }
        }
        return extractArguments(argsString, outsidePositions, true);
    }

    /**
     * Returns whether a notes prefix is immediately followed by an opening quote.
     */
    private static boolean hasOpeningNotesQuote(String argsString, PrefixPosition position) {
        int valueStart = position.getStartPosition() + position.getPrefix().getPrefix().length();
        return position.getPrefix().equals(PREFIX_NOTES) && argsString.startsWith("\"", valueStart);
    }

    /**
     * Finds the closing quote, rejecting blank notes and trailing text before the next prefix.
     * Prefix-like text inside the quotes is part of the notes.
     */
    private static int findValidatedClosingQuote(String argsString, List<PrefixPosition> positions, int notesIndex)
            throws ParseException {
        PrefixPosition position = positions.get(notesIndex);
        int valueStart = position.getStartPosition() + position.getPrefix().getPrefix().length();
        int quotedEnd = argsString.indexOf('"', valueStart + 1);
        if (quotedEnd == -1 || Notes.isBlank(argsString.substring(valueStart + 1, quotedEnd))) {
            throw new ParseException(MESSAGE_INVALID_QUOTED_NOTES);
        }
        int nextIndex = notesIndex + 1;
        while (nextIndex < positions.size() && positions.get(nextIndex).getStartPosition() < quotedEnd) {
            nextIndex++;
        }
        int nextStart = nextIndex < positions.size()
                ? positions.get(nextIndex).getStartPosition() : argsString.length();
        if (!Notes.isBlank(argsString.substring(quotedEnd + 1, nextStart))) {
            throw new ParseException(MESSAGE_INVALID_QUOTED_NOTES);
        }
        return quotedEnd;
    }

    /**
     * Tokenizes an arguments string and returns an {@code ArgumentMultimap} object that maps prefixes to their
     * respective argument values. Only the given prefixes will be recognized in the arguments string.
     *
     * @param argsString Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixes   Prefixes to tokenize the arguments string with
     * @return           ArgumentMultimap object that maps prefixes to their arguments
     */
    public static ArgumentMultimap tokenize(String argsString, Prefix... prefixes) {
        List<PrefixPosition> positions = findAllPrefixPositions(argsString, prefixes);
        return extractArguments(argsString, positions, false);
    }

    /**
     * Finds all zero-based prefix positions in the given arguments string.
     *
     * @param argsString Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixes   Prefixes to find in the arguments string
     * @return           List of zero-based prefix positions in the given arguments string
     */
    private static List<PrefixPosition> findAllPrefixPositions(String argsString, Prefix... prefixes) {
        return Arrays.stream(prefixes)
                .flatMap(prefix -> findPrefixPositions(argsString, prefix).stream())
                .collect(Collectors.toList());
    }

    /**
     * @see #findAllPrefixPositions(String, Prefix...)
     */
    private static List<PrefixPosition> findPrefixPositions(String argsString, Prefix prefix) {
        List<PrefixPosition> positions = new ArrayList<>();

        int prefixPosition = findPrefixPosition(argsString, prefix.getPrefix(), 0);
        while (prefixPosition != -1) {
            PrefixPosition extendedPrefix = new PrefixPosition(prefix, prefixPosition);
            positions.add(extendedPrefix);
            prefixPosition = findPrefixPosition(argsString, prefix.getPrefix(), prefixPosition + 1);
        }

        return positions;
    }

    /**
     * Returns the index of the first occurrence of {@code prefix} in
     * {@code argsString} starting from index {@code fromIndex}. An occurrence
     * is valid if there is a whitespace before {@code prefix}. Returns -1 if no
     * such occurrence can be found.
     *
     * E.g if {@code argsString} = "e/hip/900", {@code prefix} = "p/" and
     * {@code fromIndex} = 0, this method returns -1 as there are no valid
     * occurrences of "p/" with whitespace before it. However, if
     * {@code argsString} = "e/hi p/900", {@code prefix} = "p/" and
     * {@code fromIndex} = 0, this method returns 5.
     */
    private static int findPrefixPosition(String argsString, String prefix, int fromIndex) {
        if (prefix.isEmpty()) {
            return -1;
        }
        int prefixIndex = argsString.indexOf(prefix, fromIndex);
        while (prefixIndex != -1) {
            if (prefixIndex > 0 && Character.isWhitespace(argsString.charAt(prefixIndex - 1))) {
                return prefixIndex;
            }
            prefixIndex = argsString.indexOf(prefix, prefixIndex + 1);
        }
        return -1;
    }

    /**
     * Extracts prefixes and their argument values, and returns an {@code ArgumentMultimap} object that maps the
     * extracted prefixes to their respective arguments. Prefixes are extracted based on their zero-based positions in
     * {@code argsString}.
     *
     * @param argsString      Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixPositions Zero-based positions of all prefixes in {@code argsString}
     * @param hasQuotedNotes  Whether the positions have been validated and filtered for quoted ride notes.
     * @return                ArgumentMultimap object that maps prefixes to their arguments
     */
    private static ArgumentMultimap extractArguments(String argsString, List<PrefixPosition> prefixPositions,
            boolean hasQuotedNotes) {

        // Sort by start position
        prefixPositions.sort(Comparator.comparingInt(PrefixPosition::getStartPosition));

        // Insert a PrefixPosition to represent the preamble
        PrefixPosition preambleMarker = new PrefixPosition(new Prefix(""), 0);
        prefixPositions.addFirst(preambleMarker);

        // Add a dummy PrefixPosition to represent the end of the string
        PrefixPosition endPositionMarker = new PrefixPosition(new Prefix(""), argsString.length());
        prefixPositions.add(endPositionMarker);

        // Map prefixes to their argument values (if any)
        ArgumentMultimap argMultimap = new ArgumentMultimap();
        for (int i = 0; i < prefixPositions.size() - 1; i++) {
            // Extract and store prefixes and their arguments
            Prefix argPrefix = prefixPositions.get(i).getPrefix();
            String argValue = extractArgumentValue(argsString, prefixPositions.get(i),
                    prefixPositions.get(i + 1), hasQuotedNotes);
            argMultimap.put(argPrefix, argValue);
        }

        return argMultimap;
    }

    /**
     * Returns the trimmed value of the argument in the arguments string specified by {@code currentPrefixPosition}.
     * The end position of the value is determined by {@code nextPrefixPosition}.
     * Trimming uses the same {@link Character#isWhitespace(int)} definition as prefix recognition.
     * Validated quoted notes have their delimiters removed and retain their inner whitespace.
     */
    private static String extractArgumentValue(String argsString,
            PrefixPosition currentPrefixPosition, PrefixPosition nextPrefixPosition, boolean hasQuotedNotes) {
        Prefix prefix = currentPrefixPosition.getPrefix();

        int valueStartPos = currentPrefixPosition.getStartPosition() + prefix.getPrefix().length();
        if (hasQuotedNotes && hasOpeningNotesQuote(argsString, currentPrefixPosition)) {
            int closingQuote = argsString.indexOf('"', valueStartPos + 1);
            return argsString.substring(valueStartPos + 1, closingQuote);
        }
        String value = argsString.substring(valueStartPos, nextPrefixPosition.getStartPosition());

        return value.strip();
    }

    /**
     * Represents a prefix's position in an arguments string.
     */
    private static class PrefixPosition {
        private final int startPosition;
        private final Prefix prefix;

        PrefixPosition(Prefix prefix, int startPosition) {
            this.prefix = prefix;
            this.startPosition = startPosition;
        }

        int getStartPosition() {
            return startPosition;
        }

        Prefix getPrefix() {
            return prefix;
        }
    }

}
