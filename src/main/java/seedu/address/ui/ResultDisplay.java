package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import seedu.address.logic.Messages;

/**
 * A UI component that displays the result of a command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";
    private static final double NORMAL_HEIGHT = 100;
    private static final double CONFIRMATION_HEIGHT = 200;

    @FXML
    private Label confirmationHeading;

    @FXML
    private Label confirmationInstructions;

    @FXML
    private TextArea resultDisplay;

    public ResultDisplay() {
        super(FXML);
    }

    /**
     * Displays feedback, keeping overlap instructions above the scrollable details for separate styling.
     */
    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        String instructionLine = "\n" + Messages.MESSAGE_OVERLAP_INSTRUCTIONS + "\n";
        int instructionIndex = feedbackToUser.indexOf(instructionLine);
        boolean isConfirmation = instructionIndex >= 0;
        getRoot().setPrefHeight(isConfirmation ? CONFIRMATION_HEIGHT : NORMAL_HEIGHT);
        confirmationHeading.setVisible(isConfirmation);
        confirmationHeading.setManaged(isConfirmation);
        confirmationInstructions.setVisible(isConfirmation);
        confirmationInstructions.setManaged(isConfirmation);
        resultDisplay.setWrapText(isConfirmation);
        if (isConfirmation) {
            confirmationHeading.setText(feedbackToUser.substring(0, instructionIndex));
            confirmationInstructions.setText(Messages.MESSAGE_OVERLAP_INSTRUCTIONS);
            resultDisplay.setText(feedbackToUser.substring(instructionIndex + instructionLine.length()));
        } else {
            confirmationHeading.setText("");
            confirmationInstructions.setText("");
            resultDisplay.setText(feedbackToUser);
        }
        resultDisplay.positionCaret(0);
    }

}
