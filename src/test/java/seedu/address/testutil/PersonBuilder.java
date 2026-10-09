package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.ExperienceLevel;
import seedu.address.model.person.Ftp;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";
    public static final String DEFAULT_EXPERIENCE_LEVEL = "Beginner";

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private ExperienceLevel experienceLevel;
    private Ftp ftp;
    private Set<Location> locations;
    private Set<Tag> tags;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        experienceLevel = new ExperienceLevel(DEFAULT_EXPERIENCE_LEVEL);
        ftp = new Ftp("1");
        locations = Set.of(new Location("any"));
        tags = new HashSet<>();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        email = personToCopy.getEmail();
        address = personToCopy.getAddress();
        experienceLevel = personToCopy.getExperienceLevel();
        ftp = personToCopy.getFtp();
        locations = new HashSet<>(personToCopy.getLocations());
        tags = new HashSet<>(personToCopy.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Person} that we are building.
     */
    public PersonBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Sets the {@code ExperienceLevel} of the {@code Person} that we are building.
     */
    public PersonBuilder withExperienceLevel(String experienceLevel) {
        this.experienceLevel = new ExperienceLevel(experienceLevel);
        return this;
    }

    /** Sets the FTP of the {@code Person} that we are building. */
    public PersonBuilder withFtp(String ftp) {
        this.ftp = new Ftp(ftp);
        return this;
    }

    /** Sets the preferred cycling locations of the {@code Person} that we are building. */
    public PersonBuilder withLocations(String... locations) {
        this.locations = new HashSet<>();
        for (String location : locations) {
            this.locations.add(new Location(location));
        }
        return this;
    }

    public Person build() {
        return new Person(name, phone, email, ftp, address, experienceLevel, locations, tags);
    }

}
