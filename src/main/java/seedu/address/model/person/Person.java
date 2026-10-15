package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    /** Absent only for records created before emergency contacts were supported. */
    private final Optional<Phone> emergencyPhone;
    private final ExperienceLevel experienceLevel;
    private final Ftp ftp;
    private final Set<Location> locations = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Ftp ftp, ExperienceLevel experienceLevel,
            Set<Location> locations) {
        this(name, phone, email, ftp, experienceLevel, locations, Optional.empty());
    }

    /**
     * Constructs a person with an optional emergency contact for legacy record compatibility.
     */
    public Person(Name name, Phone phone, Email email, Ftp ftp, ExperienceLevel experienceLevel,
            Set<Location> locations, Optional<Phone> emergencyPhone) {
        requireAllNonNull(name, phone, email, ftp, experienceLevel, locations, emergencyPhone);
        this.emergencyPhone = emergencyPhone;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.ftp = ftp;
        this.experienceLevel = experienceLevel;
        this.locations.addAll(locations);
    }

    /**
     * Constructs a person with default FTP and unrestricted cycling locations.
     */
    public Person(Name name, Phone phone, Email email, ExperienceLevel experienceLevel) {
        this(name, phone, email, new Ftp("1"), experienceLevel, Set.of(new Location("any")));
    }

    /**
     * Constructs a person with unrestricted cycling locations.
     */
    public Person(Name name, Phone phone, Email email, Ftp ftp, ExperienceLevel experienceLevel) {
        this(name, phone, email, ftp, experienceLevel, Set.of(new Location("any")));
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Optional<Phone> getEmergencyPhone() {
        return emergencyPhone;
    }

    public Email getEmail() {
        return email;
    }

    public Ftp getFtp() {
        return ftp;
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public Set<Location> getLocations() {
        return Collections.unmodifiableSet(locations);
    }

    /**
     * Returns true if both persons have the same phone number or email address, ignoring email case.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && (otherPerson.getPhone().equals(getPhone())
                || otherPerson.getEmail().value.equalsIgnoreCase(getEmail().value));
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && emergencyPhone.equals(otherPerson.emergencyPhone)
                && email.equals(otherPerson.email)
                && ftp.equals(otherPerson.ftp)
                && experienceLevel.equals(otherPerson.experienceLevel)
                && locations.equals(otherPerson.locations);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, emergencyPhone, email, ftp, experienceLevel, locations);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("emergencyPhone", emergencyPhone.orElse(null))
                .add("email", email)
                .add("ftp", ftp)
                .add("experienceLevel", experienceLevel)
                .add("locations", locations)
                .toString();
    }

}
