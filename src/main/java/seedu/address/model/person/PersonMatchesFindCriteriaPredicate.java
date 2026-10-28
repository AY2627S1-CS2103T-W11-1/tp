package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

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

    /**
     * Creates a predicate requiring a match in every supplied field and FTP at least {@code minimumFtp}.
     * Empty collections and a null minimum FTP impose no restriction on their respective fields.
     */
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
        boolean matchesPhone = phones.isEmpty() || phones.contains(person.getPhone());
        boolean matchesEmergencyPhone = emergencyPhones.isEmpty()
                || person.getEmergencyPhone().filter(emergencyPhones::contains).isPresent();
        boolean matchesExperience = experienceLevels.isEmpty()
                || experienceLevels.contains(person.getExperienceLevel());
        return matchesName(person) && matchesPhone && matchesEmergencyPhone && matchesExperience
                && matchesLocations(person) && matchesMinimumFtp(person);
    }

    /**
     * Returns whether the name contains any requested sequence of whole words, ignoring case.
     */
    private boolean matchesName(Person person) {
        return nameKeywords.isEmpty() || nameKeywords.stream()
                .anyMatch(keyword -> containsWordsInOrder(person.getName().fullName, keyword));
    }

    /**
     * Returns whether any preferred location matches any requested location.
     */
    private boolean matchesLocations(Person person) {
        return locations.isEmpty() || person.getLocations().stream()
                .anyMatch(personLocation -> locations.stream()
                        .anyMatch(searchLocation -> matchesLocation(personLocation, searchLocation)));
    }

    /**
     * Returns whether the cyclist meets the optional minimum FTP.
     */
    private boolean matchesMinimumFtp(Person person) {
        return minimumFtp == null
                || Integer.parseInt(person.getFtp().value) >= Integer.parseInt(minimumFtp.value);
    }

    /**
     * Returns whether a stored location matches a requested location criterion.
     */
    private boolean matchesLocation(Location personLocation, Location searchLocation) {
        if (searchLocation.value.equalsIgnoreCase("any")) {
            return personLocation.value.equalsIgnoreCase("any");
        }
        return personLocation.value.equalsIgnoreCase("any")
                || containsWordsInOrder(personLocation.value, searchLocation.value);
    }

    /**
     * Returns whether all search words occur in order, ignoring case and allowing intervening words.
     */
    private boolean containsWordsInOrder(String text, String searchText) {
        String[] textWords = text.split("\\s+");
        String[] searchWords = searchText.trim().split("\\s+");
        int textIndex = 0;
        for (String searchWord : searchWords) {
            while (textIndex < textWords.length && !textWords[textIndex].equalsIgnoreCase(searchWord)) {
                textIndex++;
            }
            if (textIndex == textWords.length) {
                return false;
            }
            textIndex++;
        }
        return true;
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
                && Objects.equals(minimumFtp, otherPredicate.minimumFtp);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nameKeywords", nameKeywords)
                .add("phones", phones)
                .add("emergencyPhones", emergencyPhones)
                .add("experienceLevels", experienceLevels)
                .add("locations", locations)
                .add("minimumFtp", minimumFtp)
                .toString();
    }
}
