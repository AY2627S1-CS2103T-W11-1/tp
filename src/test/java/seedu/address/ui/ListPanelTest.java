package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxTestUtil.runOnJavaFxThread;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import seedu.address.model.person.Person;
import seedu.address.model.ride.Ride;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;

/**
 * Checks live list bindings and recycled card cells without opening desktop windows.
 */
public class ListPanelTest {
    @Test
    public void rideList_emptyAndUpdatedList_usesPlaceholderAndLiveItems() throws Exception {
        runOnJavaFxThread(() -> {
            var rides = FXCollections.<Ride>observableArrayList();
            RideListPanel panel = new RideListPanel(rides);
            ListView<?> view = (ListView<?>) panel.getRoot().lookup("#rideListView");
            assertSame(rides, view.getItems());
            Label placeholder = assertInstanceOf(Label.class, view.getPlaceholder());
            assertEquals(RideListPanel.NO_RIDES_MESSAGE, placeholder.getText());
            assertTrue(placeholder.isWrapText());
            Ride ride = new RideBuilder().withId(1).build();
            rides.add(ride);
            assertEquals(List.of(ride), view.getItems());
            rides.clear();
            assertTrue(view.getItems().isEmpty());
        });
    }

    @Test
    public void rideCell_reusedForChangedOrEmptyRide_replacesAndClearsCard() throws Exception {
        runOnJavaFxThread(() -> {
            RideListPanel panel = new RideListPanel(FXCollections.observableArrayList());
            ListView<?> view = (ListView<?>) panel.getRoot().lookup("#rideListView");
            var cell = assertInstanceOf(RideListPanel.RideListViewCell.class, view.getCellFactory().call(null));
            cell.updateItem(new RideBuilder().withId(1).withName("Ride A").build(), false);
            assertEquals("#1 Ride A", ((Label) cell.getGraphic().lookup("#title")).getText());
            cell.updateItem(new RideBuilder().withId(2).withName("Ride B").build(), false);
            assertEquals("#2 Ride B", ((Label) cell.getGraphic().lookup("#title")).getText());
            cell.updateItem(null, false);
            assertNull(cell.getGraphic());
            assertNull(cell.getText());
            cell.updateItem(new RideBuilder().build(), true);
            assertNull(cell.getGraphic());
        });
    }

    @Test
    public void personList_liveItemsAndRecycledCells_displayAndClearCyclist() throws Exception {
        runOnJavaFxThread(() -> {
            var persons = FXCollections.<Person>observableArrayList();
            PersonListPanel panel = new PersonListPanel(persons);
            ListView<?> view = (ListView<?>) panel.getRoot().lookup("#personListView");
            assertSame(persons, view.getItems());
            Person person = new PersonBuilder().withName("Overlap Tester").build();
            persons.add(person);
            assertEquals(List.of(person), view.getItems());
            var cell = assertInstanceOf(PersonListPanel.PersonListViewCell.class, view.getCellFactory().call(null));
            cell.updateItem(person, false);
            assertNotNull(cell.getGraphic());
            assertEquals("Overlap Tester", ((Label) cell.getGraphic().lookup("#name")).getText());
            cell.updateItem(null, false);
            assertNull(cell.getGraphic());
            assertNull(cell.getText());
            cell.updateItem(person, true);
            assertNull(cell.getGraphic());
            persons.clear();
            assertTrue(view.getItems().isEmpty());
        });
    }
}
