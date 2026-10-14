package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a person's name, phone and emergency phone match the supplied find criteria.
 */
public class PersonMatchesFindCriteriaPredicate implements Predicate<Person> {
    private final List<String> nameKeywords;
    private final List<Phone> phones;
    private final List<Phone> emergencyPhones;

    /**
     * Creates a predicate that matches any value in each supplied field.
     */
    public PersonMatchesFindCriteriaPredicate(List<String> nameKeywords, List<Phone> phones) {
        this(nameKeywords, phones, List.of());
    }

    /**
     * Creates a predicate requiring every supplied field to match at least one value.
     */
    public PersonMatchesFindCriteriaPredicate(List<String> nameKeywords, List<Phone> phones,
            List<Phone> emergencyPhones) {
        this.emergencyPhones = List.copyOf(emergencyPhones);
        this.nameKeywords = List.copyOf(nameKeywords);
        this.phones = List.copyOf(phones);
    }

    @Override
    public boolean test(Person person) {
        boolean matchesName = nameKeywords.isEmpty() || nameKeywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword));
        boolean matchesPhone = phones.isEmpty() || phones.contains(person.getPhone());
        boolean matchesEmergencyPhone = emergencyPhones.isEmpty()
                || person.getEmergencyPhone().filter(emergencyPhones::contains).isPresent();
        return matchesName && matchesPhone && matchesEmergencyPhone;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof PersonMatchesFindCriteriaPredicate otherPredicate)) {
            return false;
        }
        return nameKeywords.equals(otherPredicate.nameKeywords) && phones.equals(otherPredicate.phones)
                && emergencyPhones.equals(otherPredicate.emergencyPhones);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("nameKeywords", nameKeywords).add("phones", phones)
                .add("emergencyPhones", emergencyPhones).toString();
    }
}
