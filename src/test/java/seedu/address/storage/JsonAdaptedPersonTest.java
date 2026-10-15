package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector.MixInResolver;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.person.Email;
import seedu.address.model.person.ExperienceLevel;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_EXPERIENCE_LEVEL = "Expert";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_EXPERIENCE_LEVEL = BENSON.getExperienceLevel().toString();

    @Test
    public void fromJson_constructorDiscoveryOrder_preservesCurrentAndLegacyRecords() throws Exception {
        Person emergencyContactCyclist = new PersonBuilder(BENSON).withEmergencyPhone("+65 99999999").build();
        for (boolean isReversed : List.of(false, true)) {
            ObjectMapper mapper = new ObjectMapper()
                    .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                    .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            // JVMs may return constructors in either order. Exercise both without relying on the host JVM.
            mapper.setConfig(mapper.getDeserializationConfig().with(new BasicClassIntrospector() {
                @Override
                public BasicBeanDescription forDeserialization(DeserializationConfig config, JavaType type,
                        MixInResolver resolver) {
                    BasicBeanDescription description = super.forDeserialization(config, type, resolver);
                    if (type.getRawClass() == JsonAdaptedPerson.class) {
                        List<AnnotatedConstructor> constructors = description.getClassInfo().getConstructors();
                        constructors.sort(Comparator.comparingInt(AnnotatedConstructor::getParameterCount));
                        if (isReversed) {
                            Collections.reverse(constructors);
                        }
                    }
                    return description;
                }
            }));
            for (Person cyclist : List.of(BENSON, emergencyContactCyclist)) {
                String json = JsonUtil.toJsonString(new JsonAdaptedPerson(cyclist));
                assertEquals(cyclist, mapper.readValue(json, JsonAdaptedPerson.class).toModelType());
            }
            String legacyJson = "{\"name\":\"Legacy Rider\",\"phone\":\"+65 91234567\","
                    + "\"email\":\"legacy@example.com\",\"experienceLevel\":\"Beginner\"}";
            assertEquals(Optional.empty(), mapper.readValue(legacyJson, JsonAdaptedPerson.class)
                    .toModelType().getEmergencyPhone());
        }
    }

    @Test
    public void emergencyPhone_jsonRoundTripAndLegacyCompatibility() throws Exception {
        Person cyclist = new PersonBuilder(BENSON)
                .withEmergencyPhone("+65 99999999").build();
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(cyclist));
        assertEquals(cyclist, JsonUtil.fromJsonString(json, JsonAdaptedPerson.class).toModelType());
        for (String field : List.of("", "\"emergencyPhone\":null,")) {
            String legacyJson = "{" + field + "\"name\":\"Benson Meier\",\"phone\":\"+65 98765432\","
                    + "\"email\":\"benson@example.com\",\"experienceLevel\":\"Beginner\"}";
            Person legacy = JsonUtil.fromJsonString(legacyJson,
                    JsonAdaptedPerson.class).toModelType();
            assertEquals(Optional.empty(), legacy.getEmergencyPhone());
            assertEquals(legacy, JsonUtil.fromJsonString(JsonUtil.toJsonString(new JsonAdaptedPerson(legacy)),
                    JsonAdaptedPerson.class).toModelType());
        }
    }

    @Test
    public void emergencyPhone_invalidStoredValues_rejected() {
        for (String value : List.of("", "99999999", "+65 99", "+65 999-99999", "+65  99999999")) {
            JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, "1",
                    VALID_EXPERIENCE_LEVEL, List.of("any"), value);
            assertThrows(IllegalValueException.class, Phone.MESSAGE_CONSTRAINTS, person::toModelType);
        }
    }

    @Test
    public void fromJson_legacyTags_ignoresRetiredField() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(BENSON));
        for (String tags : List.of("[\"friends\",\"owesMoney\"]", "[]", "null",
                "[\"invalid tag!\",null]", "[{\"tagName\":\"friends\"}]")) {
            String legacyJson = "{\"tags\":" + tags + "," + json.substring(1);
            JsonAdaptedPerson adaptedPerson = JsonUtil.fromJsonString(legacyJson, JsonAdaptedPerson.class);
            assertEquals(BENSON, adaptedPerson.toModelType());
            assertFalse(JsonUtil.toJsonString(adaptedPerson).contains("\"tags\""));
        }
    }

    @Test
    public void jsonRoundTrip_withoutRetiredFields_preservesPerson() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(BENSON));
        assertFalse(json.contains("\"address\""));
        assertFalse(json.contains("\"tags\""));
        assertEquals(BENSON, JsonUtil.fromJsonString(json, JsonAdaptedPerson.class).toModelType());
    }

    @Test
    public void fromJson_legacyAddress_ignoresRetiredField() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(BENSON));
        for (String address : List.of("\"10 Test Road\"", "\"\"", "null")) {
            String legacyJson = "{\"address\":" + address + "," + json.substring(1);
            JsonAdaptedPerson adaptedPerson = JsonUtil.fromJsonString(legacyJson, JsonAdaptedPerson.class);
            assertEquals(BENSON, adaptedPerson.toModelType());
            assertFalse(JsonUtil.toJsonString(adaptedPerson).contains("\"address\""));
        }
    }

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(INVALID_NAME, VALID_PHONE, VALID_EMAIL, "1",
                VALID_EXPERIENCE_LEVEL, List.of("any"));
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_PHONE, VALID_EMAIL, "1",
                VALID_EXPERIENCE_LEVEL, List.of("any"));
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, INVALID_PHONE, VALID_EMAIL, "1",
                VALID_EXPERIENCE_LEVEL, List.of("any"));
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, null, VALID_EMAIL, "1",
                VALID_EXPERIENCE_LEVEL, List.of("any"));
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, INVALID_EMAIL, "1",
                VALID_EXPERIENCE_LEVEL, List.of("any"));
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null, "1",
                VALID_EXPERIENCE_LEVEL, List.of("any"));
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidExperienceLevel_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, "1",
                INVALID_EXPERIENCE_LEVEL, List.of("any"));
        String expectedMessage = ExperienceLevel.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullExperienceLevel_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, "1",
                null, List.of("any"));
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, ExperienceLevel.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

}
