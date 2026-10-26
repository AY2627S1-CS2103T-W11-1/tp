
package seedu.address.model.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Email;
import seedu.address.model.person.ExperienceLevel;
import seedu.address.model.person.Ftp;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.ride.Distance;
import seedu.address.model.ride.GroupSize;
import seedu.address.model.ride.MinimumPower;
import seedu.address.model.ride.Notes;
import seedu.address.model.ride.Ride;
import seedu.address.model.ride.RideDateTime;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {

    /**
     * Returns the sample cyclists shown in the User Guide's starting screen.
     */
    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Tan"), new Phone("+65 91234561"),
                    new Email("alex@example.com"), new Ftp("280"),
                    new ExperienceLevel("Advanced"), getLocationSet("East Coast Park", "Marina Bay"),
                    Optional.of(new Phone("+65 90000001"))),

            new Person(new Name("Bernice Lee"), new Phone("+65 91234562"),
                    new Email("bernice@example.com"), new Ftp("220"),
                    new ExperienceLevel("Intermediate"), getLocationSet("Punggol"),
                    Optional.of(new Phone("+65 90000002"))),

            new Person(new Name("Chloe Lim"), new Phone("+65 91234563"),
                    new Email("chloe@example.com"), new Ftp("180"),
                    new ExperienceLevel("Beginner"), getLocationSet("any"),
                    Optional.of(new Phone("+65 90000003")))
        };
    }

    /**
     * Returns a fresh sample book with three cyclists and two rides with empty rosters.
     * Existing saved books are loaded separately and do not receive this sample data.
     */
    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        sampleAb.addRide(new Ride(new Name("Sunday Sunrise Ride"),
                new RideDateTime("25-10-2026 0630"), new RideDateTime("25-10-2026 0930"),
                new Location("East Coast Park"), new Distance("45"), new MinimumPower("180"),
                new GroupSize("3"), Optional.of(new Notes("Bring lights and a spare tube"))));
        sampleAb.addRide(new Ride(new Name("Evening Spin"),
                new RideDateTime("27-10-2026 1800"), new RideDateTime("27-10-2026 1930"),
                new Location("Punggol"), new Distance("30.5"), new MinimumPower("150"),
                new GroupSize("8"), Optional.empty()));
        return sampleAb;
    }

    /**
     * Returns a location set containing the list of strings given.
     */
    public static Set<Location> getLocationSet(String... strings) {
        return Arrays.stream(strings)
                .map(Location::new)
                .collect(Collectors.toSet());
    }

}
