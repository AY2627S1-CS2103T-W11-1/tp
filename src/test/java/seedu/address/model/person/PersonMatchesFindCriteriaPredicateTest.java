package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonMatchesFindCriteriaPredicateTest {

    private final Person alice = new PersonBuilder().withName("Alice Bob").withPhone("+65 96320842").build();

    @Test
    public void test_emergencyPhone_matchesOwnFieldAndAllCriteria() {
        Person cyclist = new PersonBuilder(alice).withEmergencyPhone("+65 99999999").build();
        List<Phone> emergencyPhones = List.of(new Phone("+65 88888888"), new Phone("+65 99999999"));
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), emergencyPhones).test(cyclist));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), emergencyPhones).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of(), emergencyPhones, List.of()).test(cyclist));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of(), List.of(),
                List.of(alice.getPhone())).test(cyclist));
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of("alice"), List.of(alice.getPhone()),
                emergencyPhones).test(cyclist));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of("carol"), List.of(alice.getPhone()),
                emergencyPhones).test(cyclist));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of("alice"),
                List.of(new Phone("+65 88888888")), emergencyPhones).test(cyclist));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of(), List.of(),
                List.of(new Phone("+65 999"))).test(cyclist));
    }

    @Test
    public void equals_differentEmergencyCriteria_returnsFalse() {
        PersonMatchesFindCriteriaPredicate predicate = new PersonMatchesFindCriteriaPredicate(
                List.of(), List.of(), List.of(new Phone("+65 99999999")));
        assertFalse(predicate.equals(new PersonMatchesFindCriteriaPredicate(List.of(), List.of())));
        assertTrue(predicate.equals(new PersonMatchesFindCriteriaPredicate(
                List.of(), List.of(), List.of(new Phone("+65 99999999")))));
    }

    @Test
    public void test_nameKeywords_matchCompleteWordsIgnoringCase() {
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of("carol", "aLIce"), List.of()).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of("Ali"), List.of()).test(alice));
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of("alice bob"), List.of()).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of("bob alice"), List.of()).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of("alice tan"), List.of()).test(alice));
    }

    @Test
    public void test_phoneNumbers_matchAnyExactNumber() {
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of(),
                List.of(new Phone("+65 12345678"), new Phone("+65 96320842"))).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of(),
                List.of(new Phone("+65 963"))).test(alice));
    }

    @Test
    public void test_nameAndPhone_bothMustMatch() {
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of("bob"),
                List.of(new Phone("+65 96320842"))).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of("bob"),
                List.of(new Phone("+65 12345678"))).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of("carol"),
                List.of(new Phone("+65 96320842"))).test(alice));
    }

    @Test
    public void test_location_matchesCaseInsensitivelyAndAnyIsWildcard() {
        Person punggolCyclist = new PersonBuilder(alice).withLocations("Punggol").build();
        Person unrestrictedCyclist = new PersonBuilder(alice).withLocations("any").build();
        Set<Location> punggol = Set.of(new Location("pUnGgOl"));

        PersonMatchesFindCriteriaPredicate predicate = new PersonMatchesFindCriteriaPredicate(
                List.of(), List.of(), List.of(), List.of(), punggol, null);
        assertTrue(predicate.test(punggolCyclist));
        assertTrue(predicate.test(unrestrictedCyclist));
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), List.of(), List.of(),
                Set.of(new Location("any"), new Location("Punggol")), null).test(unrestrictedCyclist));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), List.of(), List.of(),
                Set.of(new Location("any")), null).test(punggolCyclist));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), List.of(), List.of(),
                Set.of(new Location("Tampines")), null).test(punggolCyclist));
        Person cyclistAtEastCoast = new PersonBuilder(alice).withLocations("East Coast Park").build();
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), List.of(), List.of(),
                Set.of(new Location("coast")), null).test(cyclistAtEastCoast));
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), List.of(), List.of(),
                Set.of(new Location("east coast")), null).test(cyclistAtEastCoast));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), List.of(), List.of(),
                Set.of(new Location("coas")), null).test(cyclistAtEastCoast));
    }

    @Test
    public void test_experienceAndMinimumFtp_matchExpectedValues() {
        Person cyclist = new PersonBuilder(alice).withExperienceLevel("Intermediate").withFtp("300").build();

        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), List.of(),
                List.of(new ExperienceLevel("intermediate")), Set.of(), new Ftp("250")).test(cyclist));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), List.of(),
                List.of(new ExperienceLevel("Beginner")), Set.of(), new Ftp("250")).test(cyclist));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), List.of(),
                List.of(new ExperienceLevel("Intermediate")), Set.of(), new Ftp("301")).test(cyclist));
    }

    @Test
    public void test_minimumFtp_matchesExactBoundary() {
        Person cyclist = new PersonBuilder(alice).withFtp("300").build();

        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), List.of(), List.of(), Set.of(),
                new Ftp("300")).test(cyclist));
    }

    @Test
    public void test_locationExperienceAndMinimumFtp_requireAllFieldsToMatch() {
        Person cyclist = new PersonBuilder(alice).withLocations("Punggol")
                .withExperienceLevel("Intermediate").withFtp("300").build();
        Person wrongLocation = new PersonBuilder(cyclist).withLocations("Tampines").build();
        Person wrongExperience = new PersonBuilder(cyclist).withExperienceLevel("Beginner").build();
        Person insufficientFtp = new PersonBuilder(cyclist).withFtp("299").build();
        PersonMatchesFindCriteriaPredicate predicate = new PersonMatchesFindCriteriaPredicate(List.of(), List.of(),
                List.of(), List.of(new ExperienceLevel("Intermediate")), Set.of(new Location("Punggol")),
                new Ftp("300"));

        assertTrue(predicate.test(cyclist));
        assertFalse(predicate.test(wrongLocation));
        assertFalse(predicate.test(wrongExperience));
        assertFalse(predicate.test(insufficientFtp));
    }

    @Test
    public void test_allFindCriteria_requireEveryFieldToMatch() {
        Person cyclist = new PersonBuilder(alice).withEmergencyPhone("+65 99999999")
                .withLocations("Tampines").withExperienceLevel("Intermediate").withFtp("300").build();
        Person wrongEmergencyPhone = new PersonBuilder(cyclist).withEmergencyPhone("+65 88888888").build();
        PersonMatchesFindCriteriaPredicate predicate = new PersonMatchesFindCriteriaPredicate(
                List.of("alice"), List.of(cyclist.getPhone()), List.of(cyclist.getEmergencyPhone().get()),
                List.of(new ExperienceLevel("Intermediate")), Set.of(new Location("Tampines")), new Ftp("300"));

        assertTrue(predicate.test(cyclist));
        assertFalse(predicate.test(wrongEmergencyPhone));
    }

    @Test
    public void equals_differentCyclingCriteria_returnsFalse() {
        PersonMatchesFindCriteriaPredicate locationPredicate = new PersonMatchesFindCriteriaPredicate(
                List.of(), List.of(), List.of(), List.of(), Set.of(new Location("Punggol")), null);
        PersonMatchesFindCriteriaPredicate ftpPredicate = new PersonMatchesFindCriteriaPredicate(
                List.of(), List.of(), List.of(), List.of(), Set.of(), new Ftp("250"));
        assertFalse(locationPredicate.equals(ftpPredicate));
    }
}
