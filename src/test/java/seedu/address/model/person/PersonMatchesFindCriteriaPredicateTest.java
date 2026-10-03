package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonMatchesFindCriteriaPredicateTest {

    private final Person alice = new PersonBuilder().withName("Alice Bob").withPhone("96320842").build();

    @Test
    public void test_nameKeywords_matchAnyFullWordIgnoringCase() {
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of("carol", "aLIce"), List.of()).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of("Ali"), List.of()).test(alice));
    }

    @Test
    public void test_phoneNumbers_matchAnyExactNumber() {
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of(),
                List.of(new Phone("12345678"), new Phone("96320842"))).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of(),
                List.of(new Phone("963"))).test(alice));
    }

    @Test
    public void test_nameAndPhone_bothMustMatch() {
        assertTrue(new PersonMatchesFindCriteriaPredicate(List.of("bob"),
                List.of(new Phone("96320842"))).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of("bob"),
                List.of(new Phone("12345678"))).test(alice));
        assertFalse(new PersonMatchesFindCriteriaPredicate(List.of("carol"),
                List.of(new Phone("96320842"))).test(alice));
    }
}
