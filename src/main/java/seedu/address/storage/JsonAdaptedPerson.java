package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Email;
import seedu.address.model.person.ExperienceLevel;
import seedu.address.model.person.Ftp;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Jackson-friendly version of {@link Person}.
 * Retired tags are ignored on loading and omitted from subsequent saves.
 */
@JsonIgnoreProperties("tags")
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Cyclist's %s field is missing.";

    private final String name;
    private final String phone;
    private final String emergencyPhone;
    private final String email;
    private final String ftp;
    private final String experienceLevel;
    private final List<String> locations = new ArrayList<>();

    /**
     * Constructs an adapted person without an emergency number for direct Java callers.
     * Only the full constructor is annotated for JSON deserialization to avoid ambiguous creator properties.
     */
    public JsonAdaptedPerson(String name, String phone, String email, String ftp,
            String experienceLevel, List<String> locations) {
        this(name, phone, email, ftp, experienceLevel, locations, null);
    }

    /**
     * Constructs an adapted person, accepting a missing emergency number in legacy data.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("ftp") String ftp,
            @JsonProperty("experienceLevel") String experienceLevel,
            @JsonProperty("locations") List<String> locations,
            @JsonProperty("emergencyPhone") String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.ftp = ftp;
        this.experienceLevel = experienceLevel;
        if (locations != null) {
            this.locations.addAll(locations);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        emergencyPhone = source.getEmergencyPhone().map(number -> number.value).orElse(null);
        email = source.getEmail().value;
        ftp = source.getFtp().value;
        experienceLevel = source.getExperienceLevel().value;
        locations.addAll(source.getLocations().stream()
                .map(location -> location.value)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        Name modelName = toModelName();
        Phone modelPhone = toModelPhone();
        Optional<Phone> modelEmergencyPhone = toModelEmergencyPhone();
        Email modelEmail = toModelEmail();
        Ftp modelFtp = toModelFtp();
        ExperienceLevel modelExperienceLevel = toModelExperienceLevel();
        Set<Location> modelLocations = toModelLocations();
        return new Person(modelName, modelPhone, modelEmail, modelFtp, modelExperienceLevel,
                modelLocations, modelEmergencyPhone);
    }

    /**
     * Converts and validates the required name.
     */
    private Name toModelName() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(name);
    }

    /**
     * Converts and validates the required phone number.
     */
    private Phone toModelPhone() throws IllegalValueException {
        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(phone);
    }

    /**
     * Converts the optional emergency number, preserving its absence in legacy records.
     */
    private Optional<Phone> toModelEmergencyPhone() throws IllegalValueException {
        if (emergencyPhone != null && !Phone.isValidPhone(emergencyPhone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        return Optional.ofNullable(emergencyPhone).map(Phone::new);
    }

    /**
     * Converts and validates the required email address.
     */
    private Email toModelEmail() throws IllegalValueException {
        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(email);
    }

    /**
     * Converts FTP, defaulting to 1 W for legacy records.
     */
    private Ftp toModelFtp() throws IllegalValueException {
        String ftpValue = ftp == null ? "1" : ftp;
        if (!Ftp.isValidFtp(ftpValue)) {
            throw new IllegalValueException(Ftp.MESSAGE_CONSTRAINTS);
        }
        return new Ftp(ftpValue);
    }

    /**
     * Converts and validates the required experience level.
     */
    private ExperienceLevel toModelExperienceLevel() throws IllegalValueException {
        if (experienceLevel == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, ExperienceLevel.class.getSimpleName()));
        }
        if (!ExperienceLevel.isValidExperienceLevel(experienceLevel)) {
            throw new IllegalValueException(ExperienceLevel.MESSAGE_CONSTRAINTS);
        }
        return new ExperienceLevel(experienceLevel);
    }

    /**
     * Converts locations, defaulting to unrestricted locations for legacy records.
     */
    private Set<Location> toModelLocations() throws IllegalValueException {
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
        return modelLocations;
    }
}
