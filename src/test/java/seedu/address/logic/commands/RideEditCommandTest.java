package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.RideEditCommand.EditRideDescriptor;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.ride.Distance;
import seedu.address.model.ride.GroupSize;
import seedu.address.model.ride.MinimumPower;
import seedu.address.model.ride.Notes;
import seedu.address.model.ride.Ride;
import seedu.address.model.ride.RideDateTime;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;

public class RideEditCommandTest {

    private static final String FIRST_PHONE = "+65 91234567";
    private static final String SECOND_PHONE = "+65 92345678";

    private Model model;
    private Ride rideToEdit;
    private Ride otherRide;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.addPerson(new PersonBuilder().withPhone(FIRST_PHONE).withFtp("200").build());
        model.addPerson(new PersonBuilder().withPhone(SECOND_PHONE)
                .withEmail("other@example.com").withFtp("250").build());
        rideToEdit = model.addRide(new RideBuilder().withNotes("Bring lights")
                .withRoster(FIRST_PHONE, SECOND_PHONE).build());
        otherRide = model.addRide(new RideBuilder().withName("Evening Spin").build());
    }

    @Test
    public void constructor_nullDescriptor_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RideEditCommand(1, null));
    }

    @Test
    public void constructor_nonPositiveId_throwsIllegalArgumentException() {
        EditRideDescriptor descriptor = descriptorWithName("Night Ride");

        assertThrows(IllegalArgumentException.class, () -> new RideEditCommand(0, descriptor));
        assertThrows(IllegalArgumentException.class, () -> new RideEditCommand(-1, descriptor));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        RideEditCommand command = new RideEditCommand(1, descriptorWithName("Night Ride"));

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_eachNonRosterField_updatesOnlySuppliedField() throws Exception {
        EditRideDescriptor descriptor = descriptorWithName("Night Ride");
        Ride expectedRide = new RideBuilder(rideToEdit).withName("Night Ride").build();
        assertEditSuccess(descriptor, expectedRide);

        descriptor = new EditRideDescriptor();
        descriptor.setFrom(new RideDateTime("18-10-2026 0600"));
        expectedRide = new RideBuilder(rideToEdit).withFrom("18-10-2026 0600").build();
        assertEditSuccess(descriptor, expectedRide);

        descriptor = new EditRideDescriptor();
        descriptor.setTo(new RideDateTime("18-10-2026 1000"));
        expectedRide = new RideBuilder(rideToEdit).withTo("18-10-2026 1000").build();
        assertEditSuccess(descriptor, expectedRide);

        descriptor = new EditRideDescriptor();
        descriptor.setLocation(new Location("Punggol"));
        expectedRide = new RideBuilder(rideToEdit).withLocation("Punggol").build();
        assertEditSuccess(descriptor, expectedRide);

        descriptor = new EditRideDescriptor();
        descriptor.setDistance(new Distance("50.5"));
        expectedRide = new RideBuilder(rideToEdit).withDistance("50.5").build();
        assertEditSuccess(descriptor, expectedRide);
    }

    @Test
    public void execute_minimumPowerAtLowestFtpThenLowered_updatesRide() throws Exception {
        for (String minimumPower : List.of("200", "150")) {
            EditRideDescriptor descriptor = new EditRideDescriptor();
            descriptor.setMinimumPower(new MinimumPower(minimumPower));
            Ride expectedRide = new RideBuilder(rideToEdit).withMinimumPower(minimumPower).build();
            assertEditSuccess(descriptor, expectedRide);
        }
    }

    @Test
    public void execute_groupSizeAtRosterCountThenIncreased_updatesRide() throws Exception {
        for (String groupSize : List.of("2", "20")) {
            EditRideDescriptor descriptor = new EditRideDescriptor();
            descriptor.setMaxGroupSize(new GroupSize(groupSize));
            Ride expectedRide = new RideBuilder(rideToEdit).withMaxGroupSize(groupSize).build();
            assertEditSuccess(descriptor, expectedRide);
        }
    }

    @Test
    public void execute_rosterConstraintsWithEmptyRoster_updatesRide() throws Exception {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setMinimumPower(new MinimumPower("600"));
        descriptor.setMaxGroupSize(new GroupSize("1"));
        Ride expectedEmptyRosterRide = new RideBuilder(otherRide).withMinimumPower("600")
                .withMaxGroupSize("1").build();

        new RideEditCommand(otherRide.getId(), descriptor).execute(model);

        assertEquals(List.of(rideToEdit, expectedEmptyRosterRide), model.getAddressBook().getRideList());
    }

    @Test
    public void execute_invalidRosterConstraints_rejectsAtomicallyWithGroupSizeFirst() {
        EditRideDescriptor descriptor = descriptorWithName("Changed Name");
        descriptor.setMinimumPower(new MinimumPower("201"));
        descriptor.setMaxGroupSize(new GroupSize("1"));

        assertCommandFailure(new RideEditCommand(rideToEdit.getId(), descriptor), model,
                RideEditCommand.MESSAGE_GROUP_SIZE_TOO_SMALL);

        descriptor.setMaxGroupSize(new GroupSize("2"));
        assertCommandFailure(new RideEditCommand(rideToEdit.getId(), descriptor), model,
                RideEditCommand.MESSAGE_MINIMUM_POWER_TOO_HIGH);
    }

    @Test
    public void execute_unresolvedRosterReference_allowsUnrelatedEditButRejectsFtpEdit() throws Exception {
        Model unresolvedModel = new ModelManager();
        Ride unresolvedRide = unresolvedModel.addRide(new RideBuilder().withRoster(FIRST_PHONE).build());

        new RideEditCommand(unresolvedRide.getId(), descriptorWithName("Renamed Ride")).execute(unresolvedModel);
        Ride renamedRide = new RideBuilder(unresolvedRide).withName("Renamed Ride").build();
        assertEquals(List.of(renamedRide), unresolvedModel.getAddressBook().getRideList());

        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setMinimumPower(new MinimumPower("200"));
        assertCommandFailure(new RideEditCommand(unresolvedRide.getId(), descriptor), unresolvedModel,
                RideRosterCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_replaceNotes_updatesOnlyNotes() throws Exception {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setNotes(Optional.of(new Notes("Meet at carpark")));
        Ride expectedRide = new RideBuilder(rideToEdit).withNotes("Meet at carpark").build();

        assertEditSuccess(descriptor, expectedRide);
    }

    @Test
    public void execute_clearNotes_removesNotes() throws Exception {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setNotes(Optional.empty());
        Ride expectedRide = new RideBuilder().withId(rideToEdit.getId())
                .withRoster(FIRST_PHONE, SECOND_PHONE).build();

        assertEditSuccess(descriptor, expectedRide);
    }

    @Test
    public void execute_clearAbsentNotes_succeeds() throws Exception {
        Ride rideWithoutNotes = otherRide;
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setNotes(Optional.empty());
        RideEditCommand command = new RideEditCommand(rideWithoutNotes.getId(), descriptor);

        command.execute(model);

        assertEquals(rideWithoutNotes, model.getAddressBook().getRideList().get(1));
    }

    @Test
    public void execute_multipleFields_updatesSuppliedFieldsAndPreservesOthers() throws Exception {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setName(new Name("Coastal Century"));
        descriptor.setFrom(new RideDateTime("19-10-2026 0600"));
        descriptor.setTo(new RideDateTime("19-10-2026 1100"));
        descriptor.setLocation(new Location("Changi"));
        descriptor.setDistance(new Distance("100"));
        descriptor.setMinimumPower(new MinimumPower("200"));
        descriptor.setMaxGroupSize(new GroupSize("18"));
        descriptor.setNotes(Optional.of(new Notes("Bring two bottles")));
        Ride expectedRide = new RideBuilder(rideToEdit).withName("Coastal Century")
                .withFrom("19-10-2026 0600").withTo("19-10-2026 1100").withLocation("Changi")
                .withDistance("100").withMinimumPower("200").withMaxGroupSize("18")
                .withNotes("Bring two bottles").build();

        assertEditSuccess(descriptor, expectedRide);
    }

    @Test
    public void execute_editToEqualFrom_acceptsEqualPeriod() throws Exception {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setTo(rideToEdit.getFrom());
        Ride expectedRide = new RideBuilder(rideToEdit).withTo(rideToEdit.getFrom().toString()).build();

        assertEditSuccess(descriptor, expectedRide);
    }

    @Test
    public void execute_unknownId_throwsCommandExceptionAndLeavesModelUnchanged() {
        RideEditCommand command = new RideEditCommand(999, descriptorWithName("Night Ride"));

        assertCommandFailure(command, model, String.format(RideEditCommand.MESSAGE_RIDE_NOT_FOUND, 999));
    }

    @Test
    public void execute_invalidPeriods_throwsCommandExceptionAndLeavesModelUnchanged() {
        EditRideDescriptor bothTimes = new EditRideDescriptor();
        bothTimes.setFrom(new RideDateTime("20-10-2026 1000"));
        bothTimes.setTo(new RideDateTime("20-10-2026 0900"));
        EditRideDescriptor onlyFrom = new EditRideDescriptor();
        onlyFrom.setFrom(new RideDateTime("18-10-2026 1000"));
        EditRideDescriptor onlyTo = new EditRideDescriptor();
        onlyTo.setTo(new RideDateTime("18-10-2026 0600"));

        for (EditRideDescriptor descriptor : List.of(bothTimes, onlyFrom, onlyTo)) {
            assertCommandFailure(new RideEditCommand(rideToEdit.getId(), descriptor), model,
                    Ride.MESSAGE_INVALID_PERIOD);
        }
    }

    @Test
    public void equals() {
        RideEditCommand command = new RideEditCommand(1, descriptorWithName("Night Ride"));
        RideEditCommand sameCommand = new RideEditCommand(1, descriptorWithName("Night Ride"));
        RideEditCommand differentIdCommand = new RideEditCommand(2, descriptorWithName("Night Ride"));
        RideEditCommand differentDescriptorCommand = new RideEditCommand(1, descriptorWithName("Morning Ride"));
        EditRideDescriptor clearNotesDescriptor = new EditRideDescriptor();
        clearNotesDescriptor.setNotes(Optional.empty());
        RideEditCommand clearNotesCommand = new RideEditCommand(1, clearNotesDescriptor);
        RideEditCommand noFieldsCommand = new RideEditCommand(1, new EditRideDescriptor());

        assertTrue(command.equals(command));
        assertTrue(command.equals(sameCommand));
        assertFalse(command.equals(differentIdCommand));
        assertFalse(command.equals(differentDescriptorCommand));
        assertFalse(command.equals(clearNotesCommand));
        assertFalse(clearNotesCommand.equals(noFieldsCommand));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
    }

    @Test
    public void editRideDescriptor_setNullNotes_throwsNullPointerException() {
        EditRideDescriptor descriptor = new EditRideDescriptor();

        assertThrows(NullPointerException.class, () -> descriptor.setNotes(null));
    }

    @Test
    public void constructor_descriptorChangedAfterConstruction_commandUsesDefensiveCopy() throws Exception {
        EditRideDescriptor descriptor = descriptorWithName("Night Ride");
        RideEditCommand command = new RideEditCommand(rideToEdit.getId(), descriptor);
        descriptor.setName(new Name("Unexpected Name"));

        command.execute(model);

        assertEquals("Night Ride", model.getAddressBook().getRideList().get(0).getName().fullName);
    }

    private void assertEditSuccess(EditRideDescriptor descriptor, Ride expectedRide) throws Exception {
        RideEditCommand command = new RideEditCommand(rideToEdit.getId(), descriptor);

        CommandResult result = command.execute(model);

        assertEquals(String.format(RideEditCommand.MESSAGE_SUCCESS, Messages.formatRide(expectedRide)),
                result.getFeedbackToUser());
        assertEquals(List.of(expectedRide, otherRide), model.getAddressBook().getRideList());
        assertEquals(3, model.getAddressBook().getNextRideId());
        rideToEdit = expectedRide;
    }

    private static EditRideDescriptor descriptorWithName(String name) {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setName(new Name(name));
        return descriptor;
    }
}
