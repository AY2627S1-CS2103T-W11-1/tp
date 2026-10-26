package seedu.address.ui;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";
    private static final Duration CLOSING_SCREEN_DURATION = Duration.seconds(1.5);

    /** The narrowest a Help menu item may be made, so that it stays readable in a very narrow window. */
    private static final double HELP_MENU_MIN_LABEL_WIDTH = 200;
    /** Space kept free at the right of the window: the menu's padding and shadow plus a small gap. */
    private static final double HELP_MENU_RIGHT_MARGIN = 48;

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;
    private Path dataFilePath;

    // Independent Ui parts residing in this Ui container
    private PersonListPanel personListPanel;
    private RideListPanel rideListPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;
    private int helpMenuCommandItemCount;

    @FXML
    private MenuBar menuBar;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private Menu helpMenu;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane personListPanelPlaceholder;

    @FXML
    private StackPane rideListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage}, {@code Logic},
     * and the data file path to show in the status bar.
     */
    public MainWindow(Stage primaryStage, Logic logic, Path dataFilePath) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.dataFilePath = dataFilePath;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in a later version of the SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField and
         * ResultDisplay contains a TextArea, some accelerators (e.g., F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add the following event filter to capture such key events and open
         * the help window purposely so as to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        personListPanel = new PersonListPanel(logic.getFilteredPersonList());
        personListPanelPlaceholder.getChildren().add(personListPanel.getRoot());

        rideListPanel = new RideListPanel(logic.getFilteredRideList(), logic.getRideList());
        rideListPanelPlaceholder.getChildren().add(rideListPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(dataFilePath);
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        addCommandsToHelpMenu(commandBox);
    }

    /**
     * Adds one item per command to the top of the Help menu, followed by a separator, so that the
     * list of commands appears as soon as the menu is opened. Clicking an item places that
     * command's starter text in {@code commandBox}; the existing "Full help..." item stays at the bottom.
     * The items are built again each time the menu opens, see {@link #refreshHelpMenuCommands(CommandBox, double)}.
     */
    private void addCommandsToHelpMenu(CommandBox commandBox) {
        refreshHelpMenuCommands(commandBox, Double.MAX_VALUE);
        // The window can be resized at any time, so fit the items to its current width whenever the menu opens.
        helpMenu.setOnShowing(event -> refreshHelpMenuCommands(commandBox, currentHelpMenuLabelWidth()));
    }

    /**
     * Replaces the command items at the top of the Help menu with new ones that are at most {@code labelWidth}
     * wide. A long command format therefore wraps onto further lines instead of making the menu wider than the
     * window. The items are created again, instead of resized, because a label that has already been shown keeps
     * its old text layout when its width changes and would cut its text off with "...".
     */
    private void refreshHelpMenuCommands(CommandBox commandBox, double labelWidth) {
        helpMenu.getItems().remove(0, helpMenuCommandItemCount);

        List<MenuItem> commandItems = new ArrayList<>();
        for (CommandTemplates.Template template : CommandTemplates.ALL) {
            CustomMenuItem commandItem = new CustomMenuItem(helpMenuLabel(template, labelWidth));
            commandItem.setOnAction(event -> commandBox.setCommandText(template.insertText()));
            commandItems.add(commandItem);
        }
        commandItems.add(new SeparatorMenuItem());

        helpMenu.getItems().addAll(0, commandItems);
        helpMenuCommandItemCount = commandItems.size();
    }

    /**
     * Returns the width that a Help menu item may take now: the room between the Help menu and the right edge of
     * the window. A wider window gives a wider menu, and a narrower window a narrower one.
     */
    private double currentHelpMenuLabelWidth() {
        double menuLeft = menuBar.lookupAll(".menu").stream()
                .filter(node -> node instanceof MenuButton button && helpMenu.getText().equals(button.getText()))
                .findFirst()
                .map(this::leftEdgeInScene)
                .orElse(0.0);
        return helpMenuLabelWidth(menuBar.getScene().getWidth(), menuLeft);
    }

    /**
     * Returns the label of a Help menu item showing the format of {@code template}. The label wraps, unlike plain
     * menu text, so it can be made at most {@code maxWidth} wide to fit the window.
     */
    static Label helpMenuLabel(CommandTemplates.Template template, double maxWidth) {
        Label label = new BoundedLabel(CommandTemplates.keepOptionalGroupsTogether(template.display()));
        label.setWrapText(true);
        label.setMaxWidth(maxWidth);
        return label;
    }

    /**
     * Returns the distance from the left edge of the window to the left edge of {@code node}.
     */
    private double leftEdgeInScene(Node node) {
        return node.localToScene(node.getBoundsInLocal()).getMinX();
    }

    /**
     * Returns the width that a Help menu item may take, given the {@code windowWidth} and the distance
     * {@code menuLeft} from the left edge of the window to the Help menu. Never less than a readable minimum.
     */
    static double helpMenuLabelWidth(double windowWidth, double menuLeft) {
        return Math.max(HELP_MENU_MIN_LABEL_WIDTH, windowWidth - menuLeft - HELP_MENU_RIGHT_MARGIN);
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        // Show the closing screen before hiding this window: the app exits when the last window closes.
        ArtworkScreen.closing().showFor(CLOSING_SCREEN_DURATION, () -> { });
        primaryStage.hide();
    }

    public PersonListPanel getPersonListPanel() {
        return personListPanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }

    /**
     * A label whose preferred width is its text width, but never more than its maximum width. A menu sizes its
     * items by their preferred width and ignores their maximum width, so without this a long label would make
     * the menu wider than the window instead of wrapping.
     */
    private static class BoundedLabel extends Label {
        BoundedLabel(String text) {
            super(text);
        }

        @Override
        protected double computePrefWidth(double height) {
            return Math.min(super.computePrefWidth(height), getMaxWidth());
        }

        /**
         * A menu asks for the height without giving the width, so use the width this label will get. Otherwise the
         * label is only one line tall and cuts off wrapped text.
         */
        @Override
        protected double computePrefHeight(double width) {
            return super.computePrefHeight(width < 0 ? computePrefWidth(-1) : width);
        }
    }
}
