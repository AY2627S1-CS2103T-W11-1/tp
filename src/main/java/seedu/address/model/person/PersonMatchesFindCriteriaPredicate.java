package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a person's name and phone match the supplied find criteria.
 */
public class PersonMatchesFindCriteriaPredicate implements Predicate<Person> {
    private final List<String> nameKeywords;
    private final List<Phone> phones;

    /**
     * Creates a predicate that matches any value in each supplied field.
     */
    public PersonMatchesFindCriteriaPredicate(List<String> nameKeywords, List<Phone> phones) {
        this.nameKeywords = List.copyOf(nameKeywords);
        this.phones = List.copyOf(phones);
    }

    @Override
    public boolean test(Person person) {
        boolean matchesName = nameKeywords.isEmpty() || nameKeywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword));
        boolean matchesPhone = phones.isEmpty() || phones.contains(person.getPhone());
        return matchesName && matchesPhone;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof PersonMatchesFindCriteriaPredicate otherPredicate)) {
            return false;
        }
        return nameKeywords.equals(otherPredicate.nameKeywords) && phones.equals(otherPredicate.phones);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("nameKeywords", nameKeywords).add("phones", phones).toString();
    }
}
