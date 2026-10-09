package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.ExperienceLevel;
import seedu.address.model.person.Ftp;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String ftp;
    private final String address;
    private final String experienceLevel;
    private final List<String> locations = new ArrayList<>();
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    public JsonAdaptedPerson(String name, String phone, String email, String address,
            String experienceLevel, List<JsonAdaptedTag> tags) {
        this(name, phone, email, "1", address, experienceLevel, null, tags);
    }

    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("ftp") String ftp,
            @JsonProperty("address") String address, @JsonProperty("experienceLevel") String experienceLevel,
            @JsonProperty("locations") List<String> locations, @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.ftp = ftp;
        this.address = address;
        this.experienceLevel = experienceLevel;
        if (locations != null) {
            this.locations.addAll(locations);
        }
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        ftp = source.getFtp().value;
        address = source.getAddress().value;
        experienceLevel = source.getExperienceLevel().value;
        locations.addAll(source.getLocations().stream()
                .map(location -> location.value)
                .collect(Collectors.toList()));
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        String ftpValue = ftp == null ? "1" : ftp;
        if (!Ftp.isValidFtp(ftpValue)) {
            throw new IllegalValueException(Ftp.MESSAGE_CONSTRAINTS);
        }
        final Ftp modelFtp = new Ftp(ftpValue);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        if (experienceLevel == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, ExperienceLevel.class.getSimpleName()));
        }
        if (!ExperienceLevel.isValidExperienceLevel(experienceLevel)) {
            throw new IllegalValueException(ExperienceLevel.MESSAGE_CONSTRAINTS);
        }
        final ExperienceLevel modelExperienceLevel = new ExperienceLevel(experienceLevel);

        final Set<Location> modelLocations = new HashSet<>();
        for (String location : locations) {
            if (!Location.isValidLocation(location)) {
                throw new IllegalValueException(Location.MESSAGE_CONSTRAINTS);
            }
            modelLocations.add(new Location(location));
        }
        if (modelLocations.isEmpty()) {
            modelLocations.add(new Location("any"));
        }

        final Set<Tag> modelTags = new HashSet<>(personTags);
        return new Person(modelName, modelPhone, modelEmail, modelFtp, modelAddress, modelExperienceLevel,
                modelLocations, modelTags);
    }

}
