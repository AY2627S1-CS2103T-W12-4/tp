package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.applicant.Applicant;

/**
 * A UI component that displays information of a {@code Applicant}.
 */
public class ApplicantCard extends UiPart<Region> {

    private static final String FXML = "ApplicantListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Applicant applicant;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label yearsOfExperience;
    @FXML
    private Label source;
    @FXML
    private Label interviewNotes;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code ApplicantCard} with the given {@code Applicant} and index to display.
     */
    public ApplicantCard(Applicant applicant, int displayedIndex) {
        super(FXML);
        this.applicant = applicant;
        id.setText(displayedIndex + ". ");
        name.setText(applicant.getName().fullName);
        phone.setText(applicant.getPhone().value);
        address.setText(applicant.getAddress().value);
        email.setText(applicant.getEmail().value);
        setOptionalLabel(yearsOfExperience, "Experience: ", applicant.getYearsOfExperience().value, " years");
        setOptionalLabel(source, "Source: ", applicant.getSource().value, "");
        setOptionalLabel(interviewNotes, "Notes: ", applicant.getInterviewNotes().value, "");
        applicant.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
    }

    /**
     * Shows {@code prefix + value + suffix} in {@code label}, or hides the label if {@code value} is empty.
     */
    private static void setOptionalLabel(Label label, String prefix, String value, String suffix) {
        boolean hasValue = !value.isEmpty();
        label.setText(hasValue ? prefix + value + suffix : "");
        label.setVisible(hasValue);
        label.setManaged(hasValue);
    }
}
