package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMERGENCY_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FTP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Email;
import seedu.address.model.person.ExperienceLevel;
import seedu.address.model.person.Ftp;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.ride.Ride;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by their phone number in the displayed person list. "
            + "Existing values will be overwritten by the input values. "
            + "The resulting phone number must not belong to another person.\n"
            + "Parameters: PHONE "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMERGENCY_PHONE + "EMERGENCY_PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_FTP + "FTP_LEVEL] "
            + "[" + PREFIX_EXPERIENCE_LEVEL + "EXPERIENCE_LEVEL] "
            + "[" + PREFIX_LOCATION + "LOCATION]...\n"
            + "Example: " + COMMAND_WORD + " +65 91234567 "
            + PREFIX_PHONE + "+65 98765432 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "Another cyclist already has this phone number or email.";
    public static final String MESSAGE_DUPLICATE_PHONE = "This phone number already belongs to another cyclist. "
            + "Choose a unique phone number.";
    public static final String MESSAGE_AMBIGUOUS_PHONE = Messages.MESSAGE_AMBIGUOUS_PHONE;
    public static final String MESSAGE_PERSON_NOT_FOUND = Messages.MESSAGE_CYCLIST_NOT_FOUND;
    public static final String MESSAGE_FTP_BELOW_RIDE_MINIMUM =
            "Cannot edit cyclist: FTP %1$d W is below the minimum %2$d W for ride %3$d (%4$s).";

    private final Phone targetPhone;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param targetPhone phone number of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Phone targetPhone, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(targetPhone);
        requireNonNull(editPersonDescriptor);

        this.targetPhone = targetPhone;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        List<Person> matchingPersons = lastShownList.stream()
                .filter(person -> person.getPhone().equals(targetPhone))
                .toList();

        if (matchingPersons.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_PERSON_NOT_FOUND, targetPhone));
        }

        if (matchingPersons.size() > 1) {
            throw new CommandException(MESSAGE_AMBIGUOUS_PHONE);
        }

        Person personToEdit = matchingPersons.get(0);
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);
        if (editPersonDescriptor.getFtp().isPresent()) {
            validateRideEligibility(model, personToEdit, editedPerson);
        }

        try {
            model.setPerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException e) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Rejects an FTP edit that would violate any existing registration before changing stored data.
     * Uses the original phone number so a simultaneous phone edit cannot bypass validation.
     */
    private static void validateRideEligibility(Model model, Person originalPerson, Person editedPerson)
            throws CommandException {
        int updatedFtp = Integer.parseInt(editedPerson.getFtp().value);
        for (Ride ride : model.getAddressBook().getRideList()) {
            if (ride.getRoster().contains(originalPerson.getPhone())
                    && updatedFtp < Integer.parseInt(ride.getMinimumPower().value)) {
                throw new CommandException(String.format(MESSAGE_FTP_BELOW_RIDE_MINIMUM, updatedFtp,
                        Integer.parseInt(ride.getMinimumPower().value), ride.getId(), ride.getName().fullName));
            }
        }
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Optional<Phone> updatedEmergencyPhone = editPersonDescriptor.getEmergencyPhone().isPresent()
                ? editPersonDescriptor.getEmergencyPhone() : personToEdit.getEmergencyPhone();
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Ftp updatedFtp = editPersonDescriptor.getFtp().orElse(personToEdit.getFtp());
        ExperienceLevel updatedExperienceLevel = editPersonDescriptor.getExperienceLevel()
                .orElse(personToEdit.getExperienceLevel());
        Set<Location> updatedLocations = editPersonDescriptor.getLocations().orElse(personToEdit.getLocations());

        return new Person(updatedName, updatedPhone, updatedEmail, updatedFtp,
                updatedExperienceLevel, updatedLocations, updatedEmergencyPhone);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return targetPhone.equals(otherEditCommand.targetPhone)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetPhone", targetPhone)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Phone emergencyPhone;
        private Email email;
        private Ftp ftp;
        private ExperienceLevel experienceLevel;
        private Set<Location> locations;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * Defensive copies of {@code locations} are used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmergencyPhone(toCopy.emergencyPhone);
            setEmail(toCopy.email);
            setFtp(toCopy.ftp);
            setExperienceLevel(toCopy.experienceLevel);
            setLocations(toCopy.locations);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, emergencyPhone, email, ftp, experienceLevel, locations);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmergencyPhone(Phone emergencyPhone) {
            this.emergencyPhone = emergencyPhone;
        }

        public Optional<Phone> getEmergencyPhone() {
            return Optional.ofNullable(emergencyPhone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setFtp(Ftp ftp) {
            this.ftp = ftp;
        }

        public Optional<Ftp> getFtp() {
            return Optional.ofNullable(ftp);
        }

        public void setExperienceLevel(ExperienceLevel experienceLevel) {
            this.experienceLevel = experienceLevel;
        }

        public Optional<ExperienceLevel> getExperienceLevel() {
            return Optional.ofNullable(experienceLevel);
        }

        /**
         * Sets {@code locations} to this object's {@code locations}.
         * A defensive copy of {@code locations} is used internally.
         */
        public void setLocations(Set<Location> locations) {
            this.locations = (locations != null) ? new HashSet<>(locations) : null;
        }

        /**
         * Returns an unmodifiable location set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code locations} is null.
         */
        public Optional<Set<Location>> getLocations() {
            return (locations != null) ? Optional.of(Collections.unmodifiableSet(locations)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor otherEditPersonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(emergencyPhone, otherEditPersonDescriptor.emergencyPhone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(ftp, otherEditPersonDescriptor.ftp)
                    && Objects.equals(experienceLevel, otherEditPersonDescriptor.experienceLevel)
                    && Objects.equals(locations, otherEditPersonDescriptor.locations);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("emergencyPhone", emergencyPhone)
                    .add("email", email)
                    .add("ftp", ftp)
                    .add("experienceLevel", experienceLevel)
                    .add("locations", locations)
                    .toString();
        }
    }
}
