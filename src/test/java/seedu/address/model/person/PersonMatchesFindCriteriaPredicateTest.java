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
        // EP: emergency-phone criteria match the emergency field and combine with other fields using AND.
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
        // This verifies that emergency-phone criteria participate in predicate equality.
        PersonMatchesFindCriteriaPredicate predicate = new PersonMatchesFindCriteriaPredicate(
                List.of(), List.of(), List.of(new Phone("+65 99999999")));
        assertFalse(predicate.equals(new PersonMatchesFindCriteriaPredicate(List.of(), List.of())));
        assertTrue(predicate.equals(new PersonMatchesFindCriteriaPredicate(
                List.of(), List.of(), List.of(new Phone("+65 99999999")))));
    }

    @Test
    public void test_nameKeywords_matchCompleteWordsIgnoringCase() {
        // EP: complete words match case-insensitively, while partial or incorrectly ordered words do not.
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of("carol", "aLIce"), List.of()).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of("Ali"), List.of()).test(alice));
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of("alice bob"), List.of()).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of("bob alice"), List.of()).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of("alice tan"), List.of()).test(alice));
    }

    @Test
    public void test_nameKeywords_preserveUnicodeCaseInsensitiveMatching() {
        // Regression: equalsIgnoreCase matches Turkish capital I with dot as the old find behavior did.
        Person person = new PersonBuilder().withName("İsmail Yilmaz").build();
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of("ismail"), List.of()).test(person));
    }

    @Test
    public void test_phoneNumbers_matchAnyExactNumber() {
        // EP: phone criteria use exact matching with OR semantics across repeated values.
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of(),
                List.of(new Phone("+65 12345678"), new Phone("+65 96320842"))).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of(),
                List.of(new Phone("+65 963"))).test(alice));
    }

    @Test
    public void test_nameAndPhone_bothMustMatch() {
        // EP: different criteria use AND semantics, so one mismatch rejects the person.
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of("bob"),
                List.of(new Phone("+65 96320842"))).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of("bob"),
                List.of(new Phone("+65 12345678"))).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of("carol"),
                List.of(new Phone("+65 96320842"))).test(alice));
    }

    @Test
    public void test_location_matchesCaseInsensitivelyAndAnyIsWildcard() {
        // EP: locations support case-insensitive words, the reserved any value, and invalid partial words.
        Person punggolCyclist = new PersonBuilder(alice).withLocations("Punggol").build();
        Person unrestrictedCyclist = new PersonBuilder(alice).withLocations("any").build();
        Person cyclistAtAnyPark = new PersonBuilder(alice).withLocations("Any Park").build();
        PersonMatchesFindCriteriaPredicate predicate = predicateWithLocation("pUnGgOl");
        assertTrue(predicate.test(punggolCyclist));
        assertTrue(predicate.test(unrestrictedCyclist));
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), List.of(), List.of(),
                Set.of(new Location("any"), new Location("Punggol")), null).test(unrestrictedCyclist));
        assertTrue(predicateWithLocation("ANy").test(unrestrictedCyclist));
        assertFalse(predicateWithLocation("ANy").test(cyclistAtAnyPark));
        assertFalse(predicateWithLocation("any").test(punggolCyclist));
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
        // EP: experience must match exactly, while FTP must meet the minimum threshold.
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
        // Boundary value: cyclist FTP 300 W equals the requested minimum of 300 W.
        Person cyclist = new PersonBuilder(alice).withFtp("300").build();

        assertTrue(predicateWithFtp("300").test(cyclist));
    }

    @Test
    public void test_locationExperienceAndMinimumFtp_requireAllFieldsToMatch() {
        // EP: each supplied field is independently necessary for a match.
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
        // EP: a person matches only when every supplied find criterion matches.
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
        // This verifies that cycling criteria participate in predicate equality.
        PersonMatchesFindCriteriaPredicate locationPredicate = new PersonMatchesFindCriteriaPredicate(
                List.of(), List.of(), List.of(), List.of(), Set.of(new Location("Punggol")), null);
        PersonMatchesFindCriteriaPredicate ftpPredicate = new PersonMatchesFindCriteriaPredicate(
                List.of(), List.of(), List.of(), List.of(), Set.of(), new Ftp("250"));
        assertFalse(locationPredicate.equals(ftpPredicate));
    }

    private PersonMatchesFindCriteriaPredicate predicateWithLocation(String location) {
        return new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), List.of(), List.of(),
                Set.of(new Location(location)), null);
    }

    private PersonMatchesFindCriteriaPredicate predicateWithFtp(String ftp) {
        return new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), List.of(), List.of(),
                Set.of(), new Ftp(ftp));
    }
}
