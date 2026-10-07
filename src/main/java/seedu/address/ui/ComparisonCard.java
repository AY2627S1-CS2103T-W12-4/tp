package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.applicant.Applicant;

/**
 * A UI component that displays the details of one applicant in the comparison view, marked with a slot label
 * such as "A" or "B".
 */
public class ComparisonCard extends UiPart<Region> {

    private static final String FXML = "ComparisonCard.fxml";
    private static final double KEY_LABEL_WIDTH = 80;

    @FXML
    private Label badge;
    @FXML
    private Label name;
    @FXML
    private FlowPane skills;
    @FXML
    private Label contact;
    @FXML
    private VBox details;

    /**
     * Creates a {@code ComparisonCard} showing {@code applicant} in the slot named {@code slotLabel}.
     */
    public ComparisonCard(String slotLabel, Applicant applicant) {
        super(FXML);
        styleForSlot(slotLabel);
        name.setText(applicant.getName().fullName);
        contact.setText(applicant.getPhone().value + "  |  " + applicant.getEmail().value);
        applicant.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> skills.getChildren().add(createTagLabel(tag.tagName)));

        addDetail("Address", applicant.getAddress().value);
        addDetail("Experience", formatYears(applicant.getYearsOfExperience().value));
        addDetail("Source", applicant.getSource().value);
        addDetail("Interview notes", applicant.getInterviewNotes().value);
    }

    /**
     * Creates a {@code ComparisonCard} for the slot named {@code slotLabel} when there is no applicant to show.
     */
    public ComparisonCard(String slotLabel) {
        super(FXML);
        styleForSlot(slotLabel);
        name.setText("No applicant yet");
        contact.setText("Add applicants with the add command.");
    }

    private void styleForSlot(String slotLabel) {
        badge.setText(slotLabel);
        getRoot().getStyleClass().add("comparison-card-" + slotLabel.toLowerCase());
    }

    private static Label createTagLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("comparison-tag");
        return label;
    }

    private static String formatYears(String years) {
        if (years.isEmpty()) {
            return "";
        }
        return years.equals("1") ? "1 year" : years + " years";
    }

    /**
     * Adds a "key: value" row to the card, unless {@code value} is empty.
     */
    private void addDetail(String key, String value) {
        if (value.isEmpty()) {
            return;
        }
        Label keyLabel = new Label(key);
        keyLabel.getStyleClass().add("comparison-key");
        keyLabel.setMinWidth(KEY_LABEL_WIDTH);
        keyLabel.setPrefWidth(KEY_LABEL_WIDTH);

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("comparison-value");
        valueLabel.setWrapText(true);

        details.getChildren().add(new HBox(8, keyLabel, valueLabel));
    }
}
