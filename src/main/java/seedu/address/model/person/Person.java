package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;
    private final Ftp ftp;

    // Data fields
    private final Address address;
    private final ExperienceLevel experienceLevel;
    private final Set<Location> locations = new HashSet<>();
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Ftp ftp, Address address, ExperienceLevel experienceLevel,
            Set<Location> locations, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, ftp, address, experienceLevel, locations, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.ftp = ftp;
        this.address = address;
        this.experienceLevel = experienceLevel;
        this.locations.addAll(locations);
        this.tags.addAll(tags);
    }

    public Person(Name name, Phone phone, Email email, Address address, ExperienceLevel experienceLevel,
            Set<Tag> tags) {
        this(name, phone, email, new Ftp("1"), address, experienceLevel, Set.of(new Location("any")), tags);
    }

    public Person(Name name, Phone phone, Email email, Ftp ftp, Address address, ExperienceLevel experienceLevel,
            Set<Tag> tags) {
        this(name, phone, email, ftp, address, experienceLevel, Set.of(new Location("any")), tags);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Ftp getFtp() {
        return ftp;
    }

    public Address getAddress() {
        return address;
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public Set<Location> getLocations() {
        return Collections.unmodifiableSet(locations);
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName());
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
                && email.equals(otherPerson.email)
                && ftp.equals(otherPerson.ftp)
                && address.equals(otherPerson.address)
                && experienceLevel.equals(otherPerson.experienceLevel)
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, ftp, address, experienceLevel, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("ftp", ftp)
                .add("address", address)
                .add("experienceLevel", experienceLevel)
                .add("locations", locations)
                .add("tags", tags)
                .toString();
    }

}
