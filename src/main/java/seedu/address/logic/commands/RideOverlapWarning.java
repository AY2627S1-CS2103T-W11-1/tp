package seedu.address.logic.commands;

import java.util.List;

import seedu.address.model.person.Phone;
import seedu.address.model.ride.Ride;

/**
 * Describes overlapping commitments using all stored rides, including rides hidden by filters.
 */
final class RideOverlapWarning {
    private RideOverlapWarning() {}

    /**
     * Returns one line per affected cyclist and conflicting ride, excluding the candidate's own ID.
     */
    static String describe(Ride candidate, List<Phone> cyclists, List<Ride> rides) {
        StringBuilder details = new StringBuilder();
        for (Ride other : rides) {
            if (other.getId() == candidate.getId() || !candidate.overlaps(other)) {
                continue;
            }
            for (Phone cyclist : cyclists) {
                if (other.getRoster().contains(cyclist)) {
                    details.append("\nCyclist ").append(cyclist).append(": ride ").append(other.getId())
                            .append(" (").append(other.getName()).append("): ")
                            .append(other.getFrom()).append(" to ").append(other.getTo());
                }
            }
        }
        if (details.isEmpty()) {
            return "";
        }
        return "Proposed ride " + candidate.getId() + " (" + candidate.getName() + "): "
                + candidate.getFrom() + " to " + candidate.getTo() + details;
    }
}
