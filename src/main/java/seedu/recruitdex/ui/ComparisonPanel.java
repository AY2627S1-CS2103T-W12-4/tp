package seedu.recruitdex.ui;

import java.util.List;
import java.util.Optional;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.comparison.ComparisonPair;

/**
 * Panel showing the two applicants of the current comparison pair side by side, labelled A and B.
 * The applicants are looked up by ID, so edits to their records appear without choosing a new pair.
 */
public class ComparisonPanel extends UiPart<ScrollPane> {

    private static final String FXML = "ComparisonPanel.fxml";

    private final ObservableValue<Optional<ComparisonPair>> currentPair;
    private final ObservableList<Applicant> applicants;
    private ComparisonPair displayedPair;

    @FXML
    private StackPane slotA;
    @FXML
    private StackPane slotB;

    /**
     * Creates a {@code ComparisonPanel} that shows the applicants of {@code currentPair}, looked up in
     * {@code applicants}. It refreshes when the pair changes or when any applicant record changes.
     */
    public ComparisonPanel(ObservableValue<Optional<ComparisonPair>> currentPair,
            ObservableList<Applicant> applicants) {
        super(FXML);
        this.currentPair = currentPair;
        this.applicants = applicants;
        // Invalidation also reports replacement pairs with the same applicant IDs and sides.
        currentPair.addListener(observable -> refresh());
        applicants.addListener((ListChangeListener<Applicant>) change -> refresh());
        refresh();
    }

    private void refresh() {
        Optional<ComparisonPair> pair = currentPair.getValue();
        List<Optional<Applicant>> sides = ComparisonPairResolver.resolve(pair, applicants);
        show(slotA, "A", sides.get(0));
        show(slotB, "B", sides.get(1));
        // Repeated selections have a new pair instance; refreshing its details keeps the existing instance.
        if (pair.orElse(null) != displayedPair) {
            getRoot().setVvalue(0);
        }
        displayedPair = pair.orElse(null);
    }

    private void show(StackPane slot, String slotLabel, Optional<Applicant> applicant) {
        ComparisonCard card = applicant.isPresent()
                ? new ComparisonCard(slotLabel, applicant.get())
                : new ComparisonCard(slotLabel);
        slot.getChildren().setAll(card.getRoot());
    }
}
