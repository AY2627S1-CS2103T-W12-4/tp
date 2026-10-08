package seedu.address.ui;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import seedu.address.model.applicant.Applicant;

/**
 * Panel listing the applicants in ranking order, highest first.
 * For now the order is simply the order of the displayed list.
 */
public class RankingPanel extends UiPart<Region> {

    private static final String FXML = "RankingPanel.fxml";

    private final ObservableList<Applicant> applicants;

    @FXML
    private Label subtitle;
    @FXML
    private ListView<Applicant> rankingListView;

    /**
     * Creates a {@code RankingPanel} listing {@code applicants} in order.
     */
    public RankingPanel(ObservableList<Applicant> applicants) {
        super(FXML);
        this.applicants = applicants;
        rankingListView.setItems(applicants);
        rankingListView.setCellFactory(listView -> new RankingCell());
        applicants.addListener((ListChangeListener<Applicant>) change -> updateSubtitle());
        updateSubtitle();
    }

    private void updateSubtitle() {
        int count = applicants.size();
        subtitle.setText(count + (count == 1 ? " applicant" : " applicants"));
    }

    /**
     * Custom {@code ListCell} that shows an applicant's position and name.
     */
    private static class RankingCell extends ListCell<Applicant> {
        @Override
        public void updateIndex(int index) {
            super.updateIndex(index);
            render();
        }

        @Override
        protected void updateItem(Applicant applicant, boolean empty) {
            super.updateItem(applicant, empty);
            render();
        }

        private void render() {
            Applicant applicant = getItem();
            if (isEmpty() || applicant == null) {
                setGraphic(null);
                return;
            }
            Label position = new Label((getIndex() + 1) + ".");
            position.getStyleClass().add("ranking-position");
            position.setMinWidth(Region.USE_PREF_SIZE);

            Label name = new Label(applicant.getName().fullName);
            name.getStyleClass().add("ranking-name");
            HBox.setHgrow(name, Priority.ALWAYS);

            HBox row = new HBox(position, name);
            row.getStyleClass().add("ranking-row");
            if (getIndex() == 0) {
                row.getStyleClass().add("ranking-row-first");
            }
            setGraphic(row);
        }
    }
}
