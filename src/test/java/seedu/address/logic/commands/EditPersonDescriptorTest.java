package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EXPERIENCE_LEVEL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.ExperienceLevel;
import seedu.address.model.person.Ftp;
import seedu.address.model.person.Location;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditPersonDescriptorTest {

    @Test
    public void newCyclingFields_gettersSettersAndEditState() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        assertFalse(descriptor.isAnyFieldEdited());
        assertTrue(descriptor.getFtp().isEmpty());
        assertTrue(descriptor.getExperienceLevel().isEmpty());
        assertTrue(descriptor.getLocations().isEmpty());

        Ftp ftp = new Ftp("300");
        descriptor.setFtp(ftp);
        assertTrue(descriptor.isAnyFieldEdited());
        assertEquals(ftp, descriptor.getFtp().orElseThrow());

        descriptor = new EditPersonDescriptor();
        ExperienceLevel experienceLevel = new ExperienceLevel("Intermediate");
        descriptor.setExperienceLevel(experienceLevel);
        assertTrue(descriptor.isAnyFieldEdited());
        assertEquals(experienceLevel, descriptor.getExperienceLevel().orElseThrow());

        descriptor = new EditPersonDescriptor();
        Set<Location> locations = Set.of(new Location("East Coast Park"));
        descriptor.setLocations(locations);
        assertTrue(descriptor.isAnyFieldEdited());
        assertEquals(locations, descriptor.getLocations().orElseThrow());
    }

    @Test
    public void setLocations_externalSetModified_locationsUnchanged() {
        Set<Location> externalLocations = new HashSet<>();
        externalLocations.add(new Location("East Coast Park"));
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setLocations(externalLocations);

        externalLocations.add(new Location("Marina Bay"));

        assertEquals(Set.of(new Location("East Coast Park")), descriptor.getLocations().orElseThrow());
    }

    @Test
    public void getLocations_modifySet_throwsUnsupportedOperationException() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withLocations("East Coast Park").build();

        Set<Location> locations = descriptor.getLocations().orElseThrow();
        assertThrows(UnsupportedOperationException.class, () -> locations.add(new Location("Marina Bay")));
    }

    @Test
    public void copyConstructor_sourceLocationsReplaced_copyUnchanged() {
        EditPersonDescriptor source = new EditPersonDescriptorBuilder()
                .withLocations("East Coast Park").build();
        EditPersonDescriptor copy = new EditPersonDescriptor(source);

        source.setLocations(Set.of(new Location("Marina Bay")));

        assertEquals(Set.of(new Location("East Coast Park")), copy.getLocations().orElseThrow());
    }

    @Test
    public void equals() {
        // same values -> returns true
        EditPersonDescriptor descriptorWithSameValues = new EditPersonDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // null -> returns false
        assertFalse(DESC_AMY.equals(null));

        // different types -> returns false
        assertFalse(DESC_AMY.equals(5));

        // different values -> returns false
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // different name -> returns false
        EditPersonDescriptor editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withName(VALID_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different phone -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withPhone(VALID_PHONE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different email -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different address -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different FTP -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withFtp("600").build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different experience level -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY)
                .withExperienceLevel(VALID_EXPERIENCE_LEVEL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different locations -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withLocations("East Coast Park").build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different tags -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(DESC_AMY.equals(editedAmy));
    }

    @Test
    public void toStringMethod() {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        String expected = EditPersonDescriptor.class.getCanonicalName() + "{name="
                + editPersonDescriptor.getName().orElse(null) + ", phone="
                + editPersonDescriptor.getPhone().orElse(null) + ", email="
                + editPersonDescriptor.getEmail().orElse(null) + ", ftp="
                + editPersonDescriptor.getFtp().orElse(null) + ", address="
                + editPersonDescriptor.getAddress().orElse(null) + ", experienceLevel="
                + editPersonDescriptor.getExperienceLevel().orElse(null) + ", locations="
                + editPersonDescriptor.getLocations().orElse(null) + ", tags="
                + editPersonDescriptor.getTags().orElse(null) + "}";
        assertEquals(expected, editPersonDescriptor.toString());
    }
}
