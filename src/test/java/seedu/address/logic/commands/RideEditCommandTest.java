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
import seedu.address.testutil.RideBuilder;

public class RideEditCommandTest {

    private Model model;
    private Ride rideToEdit;
    private Ride otherRide;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        rideToEdit = model.addRide(new RideBuilder().withNotes("Bring lights")
                .withRoster("+65 91234567", "+65 92345678").build());
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
    public void execute_editName_updatesOnlyName() throws Exception {
        EditRideDescriptor descriptor = descriptorWithName("Night Ride");
        Ride expectedRide = new RideBuilder(rideToEdit).withName("Night Ride").build();

        assertEditSuccess(descriptor, expectedRide);
    }

    @Test
    public void execute_editFrom_updatesOnlyStart() throws Exception {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setFrom(new RideDateTime("18-10-2026 0600"));
        Ride expectedRide = new RideBuilder(rideToEdit).withFrom("18-10-2026 0600").build();

        assertEditSuccess(descriptor, expectedRide);
    }

    @Test
    public void execute_editTo_updatesOnlyEnd() throws Exception {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setTo(new RideDateTime("18-10-2026 1000"));
        Ride expectedRide = new RideBuilder(rideToEdit).withTo("18-10-2026 1000").build();

        assertEditSuccess(descriptor, expectedRide);
    }

    @Test
    public void execute_editLocation_updatesOnlyLocation() throws Exception {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setLocation(new Location("Punggol"));
        Ride expectedRide = new RideBuilder(rideToEdit).withLocation("Punggol").build();

        assertEditSuccess(descriptor, expectedRide);
    }

    @Test
    public void execute_editDistance_updatesOnlyDistance() throws Exception {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setDistance(new Distance("50.5"));
        Ride expectedRide = new RideBuilder(rideToEdit).withDistance("50.5").build();

        assertEditSuccess(descriptor, expectedRide);
    }

    @Test
    public void execute_editMinimumPower_updatesOnlyMinimumPower() throws Exception {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setMinimumPower(new MinimumPower("250"));
        Ride expectedRide = new RideBuilder(rideToEdit).withMinimumPower("250").build();

        assertEditSuccess(descriptor, expectedRide);
    }

    @Test
    public void execute_editMaxGroupSize_updatesOnlyMaxGroupSize() throws Exception {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setMaxGroupSize(new GroupSize("20"));
        Ride expectedRide = new RideBuilder(rideToEdit).withMaxGroupSize("20").build();

        assertEditSuccess(descriptor, expectedRide);
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
                .withRoster("+65 91234567", "+65 92345678").build();

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
        descriptor.setMinimumPower(new MinimumPower("220"));
        descriptor.setMaxGroupSize(new GroupSize("18"));
        descriptor.setNotes(Optional.of(new Notes("Bring two bottles")));
        Ride expectedRide = new RideBuilder(rideToEdit).withName("Coastal Century")
                .withFrom("19-10-2026 0600").withTo("19-10-2026 1100").withLocation("Changi")
                .withDistance("100").withMinimumPower("220").withMaxGroupSize("18")
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
    public void execute_bothTimesCreateInvalidPeriod_throwsCommandExceptionAndLeavesModelUnchanged() {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setFrom(new RideDateTime("20-10-2026 1000"));
        descriptor.setTo(new RideDateTime("20-10-2026 0900"));

        assertCommandFailure(new RideEditCommand(rideToEdit.getId(), descriptor), model,
                Ride.MESSAGE_INVALID_PERIOD);
    }

    @Test
    public void execute_onlyFromCreatesInvalidPeriod_throwsCommandExceptionAndLeavesModelUnchanged() {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setFrom(new RideDateTime("18-10-2026 1000"));

        assertCommandFailure(new RideEditCommand(rideToEdit.getId(), descriptor), model,
                Ride.MESSAGE_INVALID_PERIOD);
    }

    @Test
    public void execute_onlyToCreatesInvalidPeriod_throwsCommandExceptionAndLeavesModelUnchanged() {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setTo(new RideDateTime("18-10-2026 0600"));

        assertCommandFailure(new RideEditCommand(rideToEdit.getId(), descriptor), model,
                Ride.MESSAGE_INVALID_PERIOD);
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
        assertEquals(rideToEdit.getId(), expectedRide.getId());
        assertEquals(rideToEdit.getRoster(), expectedRide.getRoster());
        assertEquals(3, model.getAddressBook().getNextRideId());
    }

    private static EditRideDescriptor descriptorWithName(String name) {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setName(new Name(name));
        return descriptor;
    }
}
