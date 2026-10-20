package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a person's fields match the supplied find criteria.
 */
public class PersonMatchesFindCriteriaPredicate implements Predicate<Person> {
    private final List<String> nameKeywords;
    private final List<Phone> phones;
    private final List<Phone> emergencyPhones;
    private final List<ExperienceLevel> experienceLevels;
    private final Set<Location> locations;
    private final Ftp minimumFtp;

    /**
     * Creates a predicate that matches any value in each supplied field.
     */
    public PersonMatchesFindCriteriaPredicate(List<String> nameKeywords, List<Phone> phones) {
        this(nameKeywords, phones, List.of());
    }

    /**
     * Creates a predicate requiring every supplied field to match at least one value.
     */
    public PersonMatchesFindCriteriaPredicate(List<String> nameKeywords, List<Phone> phones,
            List<Phone> emergencyPhones) {
        this(nameKeywords, phones, emergencyPhones, List.of(), Set.of(), null);
    }

    /** Creates a predicate requiring every supplied field to match at least one value. */
    public PersonMatchesFindCriteriaPredicate(List<String> nameKeywords, List<Phone> phones,
            List<Phone> emergencyPhones, List<ExperienceLevel> experienceLevels, Set<Location> locations,
            Ftp minimumFtp) {
        this.emergencyPhones = List.copyOf(emergencyPhones);
        this.nameKeywords = List.copyOf(nameKeywords);
        this.phones = List.copyOf(phones);
        this.experienceLevels = List.copyOf(experienceLevels);
        this.locations = Set.copyOf(locations);
        this.minimumFtp = minimumFtp;
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        assert person.getName() != null : "Person name must be present when matching criteria.";
        boolean matchesName = nameKeywords.isEmpty() || nameKeywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword));
        boolean matchesPhone = phones.isEmpty() || phones.contains(person.getPhone());
        boolean matchesEmergencyPhone = emergencyPhones.isEmpty()
                || person.getEmergencyPhone().filter(emergencyPhones::contains).isPresent();
        boolean matchesExperience = experienceLevels.isEmpty()
                || experienceLevels.contains(person.getExperienceLevel());
        boolean matchesLocation = locations.isEmpty()
                || person.getLocations().stream().anyMatch(location -> location.value.equalsIgnoreCase("any"))
                || person.getLocations().stream().anyMatch(locations::contains);
        boolean matchesFtp = minimumFtp == null
                || Integer.parseInt(person.getFtp().value) >= Integer.parseInt(minimumFtp.value);
        return matchesName && matchesPhone && matchesEmergencyPhone && matchesExperience
                && matchesLocation && matchesFtp;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof PersonMatchesFindCriteriaPredicate otherPredicate)) {
            return false;
        }
        return nameKeywords.equals(otherPredicate.nameKeywords) && phones.equals(otherPredicate.phones)
                && emergencyPhones.equals(otherPredicate.emergencyPhones)
                && experienceLevels.equals(otherPredicate.experienceLevels)
                && locations.equals(otherPredicate.locations)
                && java.util.Objects.equals(minimumFtp, otherPredicate.minimumFtp);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("nameKeywords", nameKeywords).add("phones", phones)
                .add("emergencyPhones", emergencyPhones).add("experienceLevels", experienceLevels)
                .add("locations", locations).add("minimumFtp", minimumFtp).toString();
    }
}
