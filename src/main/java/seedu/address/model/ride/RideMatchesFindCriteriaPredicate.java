package seedu.address.model.ride;

import java.util.Optional;
import java.util.function.Predicate;

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

    /** Creates a predicate using the supplied optional ride criteria. */
    public RideMatchesFindCriteriaPredicate(Optional<Name> name, Optional<RideDateTime> from,
            Optional<RideDateTime> to, Optional<Location> location, Optional<Distance> distance,
            Optional<MinimumPower> minimumPower, Optional<GroupSize> maxGroupSize, Optional<Notes> notes) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.location = location;
        this.distance = distance;
        this.minimumPower = minimumPower;
        this.maxGroupSize = maxGroupSize;
        this.notes = notes;
    }

    @Override
    public boolean test(Ride ride) {
        boolean matchesFrom = from.isEmpty() || !ride.getFrom().isBefore(from.get());
        boolean matchesTo = to.isEmpty() || !to.get().isBefore(ride.getTo());
        return matchesFrom && matchesTo
                && name.map(value -> ride.getName().fullName.equalsIgnoreCase(value.fullName)).orElse(true)
                && location.map(value -> ride.getLocation().equals(value)).orElse(true)
                && distance.map(value -> ride.getDistance().equals(value)).orElse(true)
                && minimumPower.map(value -> Integer.parseInt(ride.getMinimumPower().value)
                        >= Integer.parseInt(value.value)).orElse(true)
                && maxGroupSize.map(value -> ride.getMaxGroupSize().equals(value)).orElse(true)
                && notes.map(value -> ride.getNotes().map(value::equals).orElse(false)).orElse(true);
    }
}
