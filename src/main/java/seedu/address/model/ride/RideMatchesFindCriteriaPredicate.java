package seedu.address.model.ride;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import seedu.address.commons.util.StringUtil;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;

/** Tests whether a ride matches the supplied ride-find criteria. */
public class RideMatchesFindCriteriaPredicate implements Predicate<Ride> {
    private final Optional<Name> name;
    private final Optional<RideDateTime> from;
    private final Optional<RideDateTime> to;
    private final Optional<Location> location;
    private final Optional<Distance> distance;
    private final Optional<MinimumPower> minimumPower;
    private final Optional<GroupSize> maxGroupSize;
    private final Optional<Notes> notes;

    /** Creates a predicate that matches every ride. */
    public RideMatchesFindCriteriaPredicate() {
        this(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty());
    }

    /** Creates a predicate using the supplied optional ride criteria. */
    public RideMatchesFindCriteriaPredicate(Optional<Name> name, Optional<RideDateTime> from,
            Optional<RideDateTime> to, Optional<Location> location, Optional<Distance> distance,
            Optional<MinimumPower> minimumPower, Optional<GroupSize> maxGroupSize, Optional<Notes> notes) {
        this.name = requireNonNull(name);
        this.from = requireNonNull(from);
        this.to = requireNonNull(to);
        this.location = requireNonNull(location);
        this.distance = requireNonNull(distance);
        this.minimumPower = requireNonNull(minimumPower);
        this.maxGroupSize = requireNonNull(maxGroupSize);
        this.notes = requireNonNull(notes);
    }

    @Override
    public boolean test(Ride ride) {
        requireNonNull(ride);
        assert ride.getName() != null : "Ride name must be present when matching criteria.";
        return matchesFrom(ride) && matchesTo(ride) && matchesName(ride) && matchesLocation(ride)
                && matchesDistance(ride) && matchesMinimumPower(ride) && matchesGroupSize(ride)
                && matchesNotes(ride);
    }

    private boolean matchesFrom(Ride ride) {
        return from.isEmpty() || !ride.getFrom().isBefore(from.get());
    }

    private boolean matchesTo(Ride ride) {
        return to.isEmpty() || !to.get().isBefore(ride.getTo());
    }

    private boolean matchesName(Ride ride) {
        return name.map(value -> ride.getName().fullName.equalsIgnoreCase(value.fullName)).orElse(true);
    }

    private boolean matchesLocation(Ride ride) {
        return location.map(value -> StringUtil.containsWordsInOrderIgnoreCase(ride.getLocation().value,
                value.value)).orElse(true);
    }

    private boolean matchesDistance(Ride ride) {
        return distance.map(value -> ride.getDistance().equals(value)).orElse(true);
    }

    private boolean matchesMinimumPower(Ride ride) {
        return minimumPower.map(value -> Integer.parseInt(ride.getMinimumPower().value)
                >= Integer.parseInt(value.value)).orElse(true);
    }

    private boolean matchesGroupSize(Ride ride) {
        return maxGroupSize.map(value -> ride.getMaxGroupSize().equals(value)).orElse(true);
    }

    private boolean matchesNotes(Ride ride) {
        return notes.map(value -> ride.getNotes().map(rideNotes ->
                containsWholeWordsIgnoreCase(rideNotes.value, value.value)).orElse(false)).orElse(true);
    }

    /** Returns whether the text contains the supplied value as a complete word or phrase. */
    private static boolean containsWholeWordsIgnoreCase(String text, String value) {
        String lowerCaseText = text.toLowerCase(Locale.ROOT);
        String lowerCaseValue = value.toLowerCase(Locale.ROOT);
        String pattern = "(?<![\\p{L}\\p{N}])" + Pattern.quote(lowerCaseValue)
                + "(?![\\p{L}\\p{N}])";
        return Pattern.compile(pattern).matcher(lowerCaseText).find();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RideMatchesFindCriteriaPredicate otherPredicate)) {
            return false;
        }
        return name.equals(otherPredicate.name)
                && from.equals(otherPredicate.from)
                && to.equals(otherPredicate.to)
                && location.equals(otherPredicate.location)
                && distance.equals(otherPredicate.distance)
                && minimumPower.equals(otherPredicate.minimumPower)
                && maxGroupSize.equals(otherPredicate.maxGroupSize)
                && notes.equals(otherPredicate.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, from, to, location, distance, minimumPower, maxGroupSize, notes);
    }
}
