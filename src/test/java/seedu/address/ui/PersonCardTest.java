package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    @Test
    public void formatLocations_multipleLocations_displaysEveryLocation() {
        Person person = new PersonBuilder()
                .withLocations("Marina Bay", "East Coast Park")
                .build();

        assertEquals("East Coast Park, Marina Bay", PersonCard.formatLocations(person));
    }

    @Test
    public void formatLocations_anyLocation_humanizesValue() {
        Person person = new PersonBuilder().withLocations("any").build();

        assertEquals("Any", PersonCard.formatLocations(person));
    }

    @Test
    public void formatLocations_mixedCaseLocation_preservesStoredCase() {
        Person person = new PersonBuilder().withLocations("eAsT Coast Park").build();

        assertEquals("eAsT Coast Park", PersonCard.formatLocations(person));
    }
}
