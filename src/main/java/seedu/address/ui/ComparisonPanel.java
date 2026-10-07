package seedu.address.ui;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import seedu.address.model.applicant.Applicant;

/**
 * Panel showing two applicants side by side, labelled A and B, for the recruiter to compare.
 * For now the two applicants are the first two in the displayed list.
 */
public class ComparisonPanel extends UiPart<Region> {

    private static final String FXML = "ComparisonPanel.fxml";

    private final ObservableList<Applicant> applicants;

    @FXML
    private StackPane slotA;
    @FXML
    private StackPane slotB;

    /**
     * Creates a {@code ComparisonPanel} that shows the first two applicants of {@code applicants}.
     */
    public ComparisonPanel(ObservableList<Applicant> applicants) {
        super(FXML);
        this.applicants = applicants;
        applicants.addListener((ListChangeListener<Applicant>) change -> refresh());
        refresh();
    }

    private void refresh() {
        show(slotA, "A", 0);
        show(slotB, "B", 1);
    }

    private void show(StackPane slot, String slotLabel, int index) {
        ComparisonCard card = index < applicants.size()
                ? new ComparisonCard(slotLabel, applicants.get(index))
                : new ComparisonCard(slotLabel);
        slot.getChildren().setAll(card.getRoot());
    }
}
