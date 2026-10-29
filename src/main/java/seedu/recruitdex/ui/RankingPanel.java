package seedu.recruitdex.ui;

import static seedu.recruitdex.commons.util.CollectionUtil.requireAllNonNull;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.applicant.ApplicantId;
import seedu.recruitdex.model.ranking.RankingEntry;

/**
 * Panel displaying the engine's complete ranking with current applicant names resolved by ID.
 * Groups unassessed applicants separately, with command indices from the current applicant search results.
 * Observable updates are supplied on the JavaFX application thread.
 */
public class RankingPanel extends UiPart<Region> {

    private static final String FXML = "RankingPanel.fxml";

    private final ObservableList<RankingEntry> rankingEntries;
    private final ObservableList<Applicant> applicants;
    private final Map<ApplicantId, Applicant> applicantsById = new HashMap<>();

    @FXML
    private Label subtitle;
    @FXML
    private ListView<RankingEntry> rankingListView;
    @FXML
    private ListView<RankingEntry> unassessedListView;
    @FXML
    private VBox unassessedSection;

    /**
     * Creates a leaderboard observing {@code rankingEntries} and the complete applicant registry view.
     * Applicant edits refresh names; search changes refresh command indices without filtering the ranking.
     *
     * @throws NullPointerException if any list is null.
     */
    public RankingPanel(ObservableList<RankingEntry> rankingEntries, ObservableList<Applicant> applicants,
                        ObservableList<Applicant> displayedApplicants) {
        super(FXML);
        requireAllNonNull(rankingEntries, applicants, displayedApplicants);
        this.rankingEntries = rankingEntries;
        this.applicants = applicants;
        rebuildApplicantIndex();
        FilteredList<RankingEntry> assessedEntries = new FilteredList<>(rankingEntries, RankingEntry::isAssessed);
        FilteredList<RankingEntry> unassessedEntries = new FilteredList<>(rankingEntries, entry -> !entry.isAssessed());
        rankingListView.setCellFactory(listView -> new RankingCell(applicantsById, displayedApplicants));
        rankingListView.setItems(assessedEntries);
        unassessedListView.setCellFactory(listView -> new RankingCell(applicantsById, displayedApplicants));
        unassessedListView.setItems(unassessedEntries);
        rankingListView.visibleProperty().bind(Bindings.isNotEmpty(assessedEntries)
                .or(Bindings.isEmpty(rankingEntries)));
        rankingListView.managedProperty().bind(rankingListView.visibleProperty());
        unassessedSection.visibleProperty().bind(Bindings.isNotEmpty(unassessedEntries));
        unassessedSection.managedProperty().bind(unassessedSection.visibleProperty());
        rankingEntries.addListener((ListChangeListener<RankingEntry>) change -> updateSubtitle());
        applicants.addListener((ListChangeListener<Applicant>) change -> {
            rebuildApplicantIndex();
            rankingListView.refresh();
            unassessedListView.refresh();
        });
        displayedApplicants.addListener((ListChangeListener<Applicant>) change -> {
            rankingListView.refresh();
            unassessedListView.refresh();
        });
        updateSubtitle();
    }

    private void rebuildApplicantIndex() {
        applicantsById.clear();
        for (Applicant applicant : applicants) {
            applicantsById.put(applicant.getId(), applicant);
        }
    }

    private void updateSubtitle() {
        int count = rankingEntries.size();
        subtitle.setText(count + (count == 1 ? " applicant" : " applicants") + " in comparison list");
    }

    /**
     * Shows an entry's shared ranking position, current name, score, and comparison count.
     * Reuses its controls while clearing content and styling from previous items.
     */
    private static class RankingCell extends ListCell<RankingEntry> {
        private final Map<ApplicantId, Applicant> applicantsById;
        private final ObservableList<Applicant> displayedApplicants;
        private final Label applicantIndexLabel = new Label();
        private final Label positionLabel = new Label();
        private final Label nameLabel = new Label();
        private final Label metadataLabel = new Label();
        private final Tooltip nameTooltip = new Tooltip();
        private final HBox row;

        private RankingCell(Map<ApplicantId, Applicant> applicantsById,
                            ObservableList<Applicant> displayedApplicants) {
            this.applicantsById = applicantsById;
            this.displayedApplicants = displayedApplicants;
            // Let the viewport constrain rows instead of using long names to determine the list width.
            setPrefWidth(0);
            positionLabel.getStyleClass().add("ranking-position");
            positionLabel.setMinWidth(Region.USE_PREF_SIZE);
            nameLabel.getStyleClass().add("ranking-name");
            nameLabel.setMinWidth(0);
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            nameLabel.setTooltip(nameTooltip);
            metadataLabel.getStyleClass().add("ranking-metadata");
            metadataLabel.setWrapText(true);
            metadataLabel.setTooltip(new Tooltip("Relative ranking score; higher values rank earlier."));

            applicantIndexLabel.getStyleClass().add("ranking-metadata");
            applicantIndexLabel.setWrapText(true);
            VBox details = new VBox(3, nameLabel, applicantIndexLabel, metadataLabel);
            details.setMinWidth(0);
            HBox.setHgrow(details, Priority.ALWAYS);
            row = new HBox(8, positionLabel, details);
            row.setMinWidth(0);
            row.setMaxWidth(Double.MAX_VALUE);
            row.getStyleClass().add("ranking-row");
        }

        @Override
        protected void updateItem(RankingEntry entry, boolean empty) {
            super.updateItem(entry, empty);
            render();
        }

        private void render() {
            setText(null);
            row.getStyleClass().remove("ranking-row-first");
            RankingEntry entry = getItem();
            if (isEmpty() || entry == null) {
                positionLabel.setText(null);
                nameLabel.setText(null);
                metadataLabel.setText(null);
                applicantIndexLabel.setText(null);
                nameTooltip.setText(null);
                setGraphic(null);
                return;
            }

            RankingEntryDisplay display = RankingEntryDisplay.create(entry, applicantsById, displayedApplicants);
            positionLabel.setText(display.positionText());
            nameLabel.setText(display.nameText());
            metadataLabel.setText(display.metadataText());
            applicantIndexLabel.setText(display.applicantIndexText());
            nameTooltip.setText(display.nameText());
            if (display.isFirstPosition()) {
                row.getStyleClass().add("ranking-row-first");
            }
            setGraphic(row);
        }
    }
}
