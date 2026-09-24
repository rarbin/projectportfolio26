package controllers.GUI.javaFX;

import controllers.API.ElectionSystemAPI;
import controllers.GUI.javaFX.ElectionApp.MapViewOpener;
import controllers.managers.CandidateManager;
import controllers.managers.ElectionManager;
import controllers.managers.PoliticianManager;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Candidate;
import models.Election;
import models.ElectionType;
import models.Politician;
import utils.CustomList.HashList;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.ResourceBundle;

public class ElectionMenuController implements Initializable {
    @FXML
    private Button resetButton, electionMapButton, exitButton, saveButton, loadButton;
    @FXML
    private Button mapViewButton;
    @FXML
    private Button overallStatsBtn, politicianStatsBtn, electionStatsBtn, candidateStatsBtn;
    @FXML
    private Button partyStatsBtn, yearStatsBtn, locationStatsBtn, fullReportBtn;
    @FXML
    private Button addPoliticianButton, updatePoliticianButton, deletePoliticianButton, clearPoliticianFormButton;
    @FXML
    private Button addElectionButton, updateElectionButton, deleteElectionButton, clearElectionFormButton;
    @FXML
    private Button addCandidateButton, updateCandidateButton, deleteCandidateButton, clearCandidateFormButton;
    @FXML
    private Button addImageButton, removeImageButton;

    // Tables and Columns
    @FXML
    private TableView<Politician> politicianTableView;
    @FXML
    private TableColumn<Politician, String> politicianAgeColumn;
    @FXML
    private TableColumn<Politician, String> politicianNameColumn, politicianPartyColumn;
    @FXML
    private TableColumn<Politician, String> politicianCountyColumn, politicianElectionsColumn, politicianImageColumn;

    @FXML
    private TableView<Election> electionTableView;
    @FXML
    private TableColumn<Election, Integer> electionYearColumn, electionSeatsColumn, electionCandidatesColumn;
    @FXML
    private TableColumn<Election, String> electionTypeColumn, electionLocationColumn, electionDateColumn;
    @FXML
    private TableColumn<Election, String> electionWinnerColumn;

    @FXML
    private TableView<Candidate> candidateTableView;
    @FXML
    private TableColumn<Candidate, Integer> candidateVotesColumn;
    @FXML
    private TableColumn<Candidate, String> candidateNameColumn, candidatePartyColumn;
    @FXML
    private TableColumn<Candidate, String> candidateElectionColumn, candidateWinnerColumn;

    // Form Fields
    @FXML
    private TextField politicianNameField, politicianPartyField, politicianCountyField;
    @FXML
    private TextField politicianDOBField, politicianImageUrlField;
    @FXML
    private TextField electionLocationField, electionSeatsField;
    @FXML
    private TextField electionYearField, electionMonthField, electionDayField;
    @FXML
    private ComboBox<ElectionType> electionTypeComboBox;

    // Search Fields
    @FXML
    private TextField searchPoliticianName, searchPoliticianParty, searchPoliticianCounty;
    @FXML
    private Button searchPoliticiansBtn, showAllPoliticiansBtn;
    @FXML
    private TextArea politicianSearchResults;

    @FXML
    private ComboBox<ElectionType> searchElectionType;
    @FXML
    private TextField searchElectionYear, searchElectionLocation;
    @FXML
    private Button searchElectionsBtn, showAllElectionsBtn;
    @FXML
    private TextArea electionSearchResults;

    @FXML
    private ComboBox<ElectionType> searchCandidateElectionType;
    @FXML
    private TextField searchCandidateParty, searchCandidateYear;
    @FXML
    private Button searchCandidatesBtn, showAllCandidatesBtn;
    @FXML
    private TextArea candidateSearchResults;

    // Sorting Components
    @FXML
    private ComboBox<String> sortPoliticianBy, sortElectionBy, sortCandidateBy;
    @FXML
    private RadioButton politicianAsc, politicianDesc, electionAsc, electionDesc, candidateAsc, candidateDesc;
    @FXML
    private Button sortPoliticiansBtn, sortElectionsBtn, sortCandidatesBtn;

    // Candidate Form
    @FXML
    private TextField candidateVotesField;
    @FXML
    private ComboBox<String> candidatePoliticianComboBox, candidateElectionComboBox, candidatePartyComboBox;
    @FXML
    private CheckBox candidateWinnerCheckBox;

    // Image Preview
    @FXML
    private AnchorPane imagePreviewPane;
    @FXML
    private ImageView politicianImageView;
    @FXML
    private Label imageNameLabel;

    // Additional Buttons
    @FXML
    private Button topVotesBtn, topWinsBtn;

    // Tabs
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab politiciansTab, electionsTab, candidatesTab;

    // Sorting Results Table
    @FXML
    private TableView<SortResultItem> sortingResultsTableView;
    @FXML
    private TableColumn<SortResultItem, String> sortResultTypeColumn, sortResultNameColumn;
    @FXML
    private TableColumn<SortResultItem, String> sortResultDetailsColumn, sortResultWinnerColumn;
    @FXML
    private Label sortingResultsLabel;

    private ElectionSystemAPI electionSystemAPI;
    private PoliticianManager politicianManager;
    private ElectionManager electionManager;
    private CandidateManager candidateManager;
    private File selectedPhotoFile;
    private String photoUrl = "";
    private final HashList<Politician> politicianData = new HashList<>();
    private final HashList<Election> electionData = new HashList<>();
    private final HashList<Candidate> candidateData = new HashList<>();
    private ElectionApp.MapViewOpener mapViewOpener;

    /**
     * INITIALIZE
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("[ElectionMenuController] initialize() called");

        try {
            initializeImagePreview();
            initializeFormComponents();
            setupButtonActions();
            setupTables();
            setupTableSelectionListeners();
            initializeSearchSortComponents();
            setupSortingResultsTable();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            showAlert("Controller initialization failed: " + e.getMessage());
        }
    }

    private void initializeImagePreview() {
        try {
            if (politicianImageView != null) {
                politicianImageView.setFitWidth(140);
                politicianImageView.setFitHeight(130);
                politicianImageView.setPreserveRatio(true);
                politicianImageView.setSmooth(true);
                politicianImageView.setImage(loadPlaceholderImage(140, 130));
                politicianImageView.setVisible(true);
            }
            if (imageNameLabel != null) {
                imageNameLabel.setText("Default profile image");
            }

            if (imagePreviewPane != null) {
                imagePreviewPane.setVisible(false);
            }
        } catch (Exception e) {
            System.err.println("  - ERROR in initializeImagePreview: " + e.getMessage());
        }
    }

    private void initializeFormComponents() {
        try {
            if (electionTypeComboBox != null) {
                electionTypeComboBox.getItems().addAll(ElectionType.values());
                electionTypeComboBox.setValue(ElectionType.GENERAL);
            }
            setTextField(electionYearField, String.valueOf(LocalDate.now().getYear()));
            setTextField(electionMonthField, "1");
            setTextField(electionDayField, "1");

            if (politicianDOBField != null) {
                LocalDate defaultDob = LocalDate.now().minusYears(30);
                setTextField(politicianDOBField, defaultDob.toString());
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void initializeSearchSortComponents() {
        try {
            initializeComboBox(sortPoliticianBy, "name", "party", "county");
            initializeComboBox(sortElectionBy, "date", "type", "year", "location");
            initializeComboBox(sortCandidateBy, "name", "party", "votes");

            initializeElectionTypeComboBox(searchElectionType);
            initializeElectionTypeComboBox(searchCandidateElectionType);

            System.out.println("  - Search/sort comboboxes initialized");
        } catch (Exception e) {
            System.err.println("  - ERROR in initializeSearchSortComponents: " + e.getMessage());
        }
    }

    private void initializeComboBox(ComboBox<String> combo, String defaultValue, String... items) {
        if (combo != null) {
            try {
                combo.getItems().addAll(items);
                combo.setValue(defaultValue);
            } catch (Exception e) {
                System.err.println("  - ERROR initializing combo box: " + e.getMessage());
            }
        }
    }

    private void initializeElectionTypeComboBox(ComboBox<ElectionType> combo) {
        if (combo != null) {
            try {
                combo.getItems().clear();
                combo.getItems().add(null);
                combo.getItems().addAll(ElectionType.values());
                combo.setValue(null);
            } catch (Exception e) {
                System.err.println("  - ERROR initializing election type combo: " + e.getMessage());
            }
        }
    }

    /**
     * SETUP
     */
    private void setupSortingResultsTable() {
        if (sortingResultsTableView != null) {
            try {
                setCellFactory(sortResultTypeColumn, "type");
                setCellFactory(sortResultNameColumn, "name");
                setCellFactory(sortResultDetailsColumn, "details");
                setCellFactory(sortResultWinnerColumn, "winnerName");

                sortResultWinnerColumn.setCellFactory(column -> new TableCell<SortResultItem, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            if (item != null && !item.isEmpty() && !item.equals("No Winner")) {
                                setStyle("-fx-background-color: #90EE90; -fx-font-weight: bold;");
                            } else {
                                setStyle("");
                            }
                        }
                    }
                });
            } catch (Exception e) {
                System.err.println("  - ERROR setting up sorting results table: " + e.getMessage());
            }
        }
    }

    private void setupButtonActions() {
        assignButton(resetButton, e -> handleReset());
        assignButton(electionMapButton, e -> handleElectionMap());
        assignButton(exitButton, e -> handleExit());
        assignButton(saveButton, e -> handleSave());
        assignButton(loadButton, e -> handleLoad());

        assignButton(addPoliticianButton, e -> handleAddPolitician());
        assignButton(updatePoliticianButton, e -> handleUpdatePolitician());
        assignButton(deletePoliticianButton, e -> handleDeletePolitician());
        assignButton(clearPoliticianFormButton, e -> clearPoliticianForm());

        assignButton(addElectionButton, e -> handleAddElection());
        assignButton(updateElectionButton, e -> handleUpdateElection());
        assignButton(deleteElectionButton, e -> handleDeleteElection());
        assignButton(clearElectionFormButton, e -> clearElectionForm());

        assignButton(addCandidateButton, e -> handleAddCandidate());
        assignButton(updateCandidateButton, e -> handleUpdateCandidate());
        assignButton(deleteCandidateButton, e -> handleDeleteCandidate());
        assignButton(clearCandidateFormButton, e -> clearCandidateForm());

        assignButton(addImageButton, e -> handleAddImage());
        assignButton(removeImageButton, e -> handleRemoveImage());

        assignButton(overallStatsBtn, e -> showStatsDialog("Overall System Statistics", getOverallStatistics()));
        assignButton(politicianStatsBtn, e -> showStatsDialog("Politician Statistics", getPoliticianStatistics()));
        assignButton(electionStatsBtn, e -> showStatsDialog("Election Statistics", getElectionStatistics()));
        assignButton(candidateStatsBtn, e -> showStatsDialog("Candidate Statistics", getCandidateStatistics()));
        assignButton(partyStatsBtn, e -> showStatsDialog("Party Statistics", getPartyStatistics()));
        assignButton(yearStatsBtn, e -> showStatsDialog("Year Statistics", getYearStatistics()));
        assignButton(locationStatsBtn, e -> showStatsDialog("Location Statistics", getLocationStatistics()));
        assignButton(fullReportBtn, e -> showStatsDialog("Full System Report", getFullReport()));

        assignButton(topVotesBtn, e -> showTopPerformersByVotes());
        assignButton(topWinsBtn, e -> showTopPerformersByWins());

        assignButton(searchPoliticiansBtn, e -> handleSearchPoliticians());
        assignButton(showAllPoliticiansBtn, e -> handleShowAllPoliticians());
        assignButton(searchElectionsBtn, e -> handleSearchElections());
        assignButton(showAllElectionsBtn, e -> handleShowAllElections());
        assignButton(searchCandidatesBtn, e -> handleSearchCandidates());
        assignButton(showAllCandidatesBtn, e -> handleShowAllCandidates());

        assignButton(sortPoliticiansBtn, e -> handleSortPoliticians());
        assignButton(sortElectionsBtn, e -> handleSortElections());
        assignButton(sortCandidatesBtn, e -> handleSortCandidates());
    }

    private void assignButton(Button button, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        if (button != null) {
            try {
                button.setOnAction(handler);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void setupTables() {
        try {
            setupPoliticianTable();
            setupElectionTable();
            setupCandidateTable();
            System.out.println("  - All tables set up");
        } catch (Exception e) {
            System.err.println("  - ERROR setting up tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private HashList<Election> getElectionsForPoliticianSimple(Politician politician) {
        HashList<Election> elections = new HashList<>();

        if (politician == null || electionSystemAPI == null) return elections;

        try {
            HashList<Candidate> allCandidates = electionSystemAPI.getAllCandidates();
            if (allCandidates == null) return elections;

            java.util.HashSet<Integer> electionIds = new java.util.HashSet<>();

            for (int i = 0; i < allCandidates.size(); i++) {
                Candidate c = allCandidates.getByIndex(i);
                if (c != null && c.getPolitician() != null &&
                        c.getPolitician().getId() == politician.getId() &&
                        c.getElection() != null) {

                    Election e = c.getElection();
                    if (!electionIds.contains(e.getId())) {
                        electionIds.add(e.getId());
                        try {
                            elections.add(e.hashCode(), e);
                        } catch (Exception ex) {
                            try {
                                elections.addAtLast(e);
                            } catch (Exception ex2) {
                                HashList<Election> newList = new HashList<>();
                                for (int j = 0; j < elections.size(); j++) {
                                    Election existing = elections.getByIndex(j);
                                    if (existing != null) {
                                        try {
                                            newList.add(existing.hashCode(), existing);
                                        } catch (Exception ex3) {
                                            try {
                                                newList.addAtLast(existing);
                                            } catch (Exception ignore) {
                                            }
                                        }
                                    }
                                }
                                try {
                                    newList.add(e.hashCode(), e);
                                } catch (Exception ex3) {
                                    try {
                                        newList.addAtLast(e);
                                    } catch (Exception ignore) {
                                    }
                                }
                                elections = newList;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("ERROR in getElectionsForPoliticianSimple: " + e.getMessage());
        }

        return elections;
    }

    private int getElectionCountForPolitician(Politician politician) {
        if (politician == null || electionSystemAPI == null) return 0;

        int count = 0;

        try {
            if (politician.getElectionsParticipated() != null) {
                count = politician.getElectionsParticipated().size();
                if (count > 0) {
                    return count;
                }
            }
            HashList<Candidate> allCandidates = electionSystemAPI.getAllCandidates();
            if (allCandidates != null) {
                for (int i = 0; i < allCandidates.size(); i++) {
                    Candidate c = allCandidates.getByIndex(i);
                    if (c != null && c.getPolitician() != null &&
                            c.getPolitician().getId() == politician.getId()) {
                        count++;
                    }
                }
            }

            if (count == 0 && electionManager != null) {
                HashList<Election> allElections = electionSystemAPI.getAllElections();
                for (int i = 0; i < allElections.size(); i++) {
                    Election e = allElections.getByIndex(i);
                    if (e != null && e.getCandidates() != null) {
                        HashList<Candidate> candidates = e.getCandidates();
                        for (int j = 0; j < candidates.size(); j++) {
                            Candidate c = candidates.getByIndex(j);
                            if (c != null && c.getPolitician() != null &&
                                    c.getPolitician().getId() == politician.getId()) {
                                count++;
                                break;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("ERROR in getElectionCountForPolitician: " + e.getMessage());
        }

        return count;
    }


    private void setupPoliticianTable() {
        System.out.println("[ElectionMenuController] Setting up politician table...");
        if (politicianTableView != null) {
            try {
                politicianNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                politicianPartyColumn.setCellValueFactory(new PropertyValueFactory<>("currentParty"));
                politicianCountyColumn.setCellValueFactory(new PropertyValueFactory<>("homeCounty"));
                if (politicianAgeColumn != null) {
                    politicianAgeColumn.setText("Date of Birth");

                    politicianAgeColumn.setCellValueFactory(cellData -> {
                        Politician p = cellData.getValue();
                        if (p != null && p.getDateOfBirth() != null) {
                            return new SimpleStringProperty(p.getDateOfBirth().toString());
                        }
                        return new SimpleStringProperty("");
                    });

                    politicianAgeColumn.setCellFactory(column -> new TableCell<Politician, String>() {
                        @Override
                        protected void updateItem(String dobString, boolean empty) {
                            super.updateItem(dobString, empty);
                            if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                                setText(null);
                                setTooltip(null);
                            } else {
                                setText(dobString);
                            }
                        }
                    });
                    politicianAgeColumn.setPrefWidth(100);
                }

                if (politicianElectionsColumn != null) {
                    politicianElectionsColumn.setCellValueFactory(cellData -> {
                        Politician politician = cellData.getValue();
                        int count = getElectionCountForPolitician(politician);


                        return new SimpleStringProperty(String.valueOf(count));
                    });

                    politicianElectionsColumn.setCellFactory(column -> new TableCell<Politician, String>() {
                        @Override
                        protected void updateItem(String count, boolean empty) {
                            super.updateItem(count, empty);
                            if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                                setText(null);
                                setTooltip(null);
                            } else {
                                setText(count);
                                Politician politician = getTableRow().getItem();


                                HashList<Election> politicianElections = getElectionsForPoliticianSimple(politician);
                            }
                        }
                    });
                }


                if (politicianImageColumn != null) {
                    politicianImageColumn.setCellValueFactory(cellData ->
                            new SimpleStringProperty(cellData.getValue().getImageUrl()));

                    politicianImageColumn.setCellFactory(column -> new TableCell<Politician, String>() {
                        private final ImageView imageView = createImageView(60, 60);

                        @Override
                        protected void updateItem(String imageUrl, boolean empty) {
                            super.updateItem(imageUrl, empty);
                            if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                Politician politician = getTableRow().getItem();
                                String actualImageUrl = politician.getImageUrl();
                                imageView.setImage(loadPlaceholderImage(60, 60));

                                if (actualImageUrl != null && !actualImageUrl.isEmpty()) {
                                    loadImageAsync(imageView, actualImageUrl, 60, 60);
                                }
                                setGraphic(imageView);
                                setText(null);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void updatePoliticianElectionCounts() {
        if (electionSystemAPI != null) {
            HashList<Politician> politicians = electionSystemAPI.getAllPoliticians();
            HashList<Candidate> candidates = electionSystemAPI.getAllCandidates();

            for (int i = 0; i < politicians.size(); i++) {
                Politician politician = politicians.getByIndex(i);
                if (politician != null) {
                    politician.setElectionsParticipated(new HashList<>());

                    for (int j = 0; j < candidates.size(); j++) {
                        Candidate candidate = candidates.getByIndex(j);
                        if (candidate != null && candidate.getPolitician() != null &&
                                candidate.getPolitician().getId() == politician.getId()) {
                            politician.addElectionParticipation(candidate);
                        }
                    }
                }
            }
            Platform.runLater(() -> {
                if (politicianTableView != null) {
                    politicianTableView.refresh();
                }
            });
        }
    }

    private ImageView createImageView(int width, int height) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        return imageView;
    }

    private void setupElectionTable() {
        if (electionTableView != null) {
            try {
                electionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
                electionLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
                electionYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

                if (electionDateColumn != null) {
                    electionDateColumn.setCellValueFactory(cellData -> {
                        Election e = cellData.getValue();
                        if (e != null && e.getDate() != null) {
                            return new SimpleStringProperty(e.getDate().toString());
                        }
                        return new SimpleStringProperty("");
                    });
                }

                if (electionSeatsColumn != null) {
                    electionSeatsColumn.setCellValueFactory(new PropertyValueFactory<>("seatsAvailable"));
                }

                if (electionCandidatesColumn != null) {
                    electionCandidatesColumn.setCellValueFactory(cellData -> {
                        Election e = cellData.getValue();
                        if (e != null && e.getCandidates() != null) {
                            return new ReadOnlyIntegerWrapper(e.getCandidates().size()).asObject();
                        }
                        return new ReadOnlyIntegerWrapper(0).asObject();
                    });
                }

                if (electionWinnerColumn != null) {
                    electionWinnerColumn.setCellValueFactory(cellData -> {
                        Election e = cellData.getValue();
                        return new SimpleStringProperty(getWinnerNameFromElection(e));
                    });

                    electionWinnerColumn.setCellFactory(column -> new TableCell<Election, String>() {
                        @Override
                        protected void updateItem(String winnerName, boolean empty) {
                            super.updateItem(winnerName, empty);

                            if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                                setText(null);
                                setStyle("");
                                setGraphic(null);
                            } else {
                                setText(winnerName);
                                Election election = getTableRow().getItem();

                                boolean hasWinners = false;
                                if (election != null && election.getCandidates() != null) {
                                    HashList<Candidate> candidates = election.getCandidates();
                                    for (int i = 0; i < candidates.size(); i++) {
                                        Candidate c = candidates.getByIndex(i);
                                        if (c != null && c.isWinner()) {
                                            hasWinners = true;
                                            break;
                                        }
                                    }
                                }
                                if (hasWinners && winnerName != null && !winnerName.equals("No Winner") && !winnerName.isEmpty()) {
                                    setStyle("-fx-background-color: #91c291; -fx-font-weight: bold; -fx-text-fill: #437e46;");
                                } else {
                                    setStyle("");
                                }
                            }
                        }
                    });
                }

                electionTableView.setRowFactory(tv -> new TableRow<Election>() {
                    @Override
                    protected void updateItem(Election election, boolean empty) {
                        super.updateItem(election, empty);

                        if (empty || election == null) {
                            setStyle("");
                        } else {
                            boolean hasWinners = false;
                            if (election.getCandidates() != null) {
                                HashList<Candidate> candidates = election.getCandidates();
                                for (int i = 0; i < candidates.size(); i++) {
                                    Candidate c = candidates.getByIndex(i);
                                    if (c != null && c.isWinner()) {
                                        hasWinners = true;
                                        break;
                                    }
                                }
                            }

                            if (hasWinners) {
                                setStyle("-fx-background-color: #a3dca8;");
                            } else {
                                setStyle("");
                            }
                        }
                    }
                });

                if (electionTableView != null) {
                    electionTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                        if (newSelection != null) {
                            if (newSelection.getCandidates() != null) {
                                System.out.println("  Candidates (" + newSelection.getCandidates().size() + "):");
                                HashList<Candidate> candidates = newSelection.getCandidates();
                                for (int i = 0; i < candidates.size(); i++) {
                                    Candidate c = candidates.getByIndex(i);
                                    if (c != null) {
                                        System.out.println("    - " +
                                                (c.getPolitician() != null ? c.getPolitician().getName() : "Unknown") +
                                                " | Votes: " + c.getVotesReceived() +
                                                " | Winner: " + c.isWinner());
                                    }
                                }
                            }
                        }
                    });
                }

                if (electionTypeColumn != null) electionTypeColumn.setPrefWidth(100);
                if (electionLocationColumn != null) electionLocationColumn.setPrefWidth(150);
                if (electionYearColumn != null) electionYearColumn.setPrefWidth(80);
                if (electionDateColumn != null) electionDateColumn.setPrefWidth(100);
                if (electionSeatsColumn != null) electionSeatsColumn.setPrefWidth(80);
                if (electionCandidatesColumn != null) electionCandidatesColumn.setPrefWidth(100);
                if (electionWinnerColumn != null) electionWinnerColumn.setPrefWidth(200);


            } catch (Exception e) {
                System.err.println("  - ERROR setting up election table: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private void setupCandidateTable() {
        System.out.println("[ElectionMenuController] Setting up candidate table...");

        if (candidateTableView != null) {
            try {
                candidateTableView.getColumns().clear();

                candidateNameColumn = new TableColumn<>("Politician");
                candidateNameColumn.setCellValueFactory(cellData -> {
                    Candidate c = cellData.getValue();
                    if (c != null && c.getPolitician() != null) {
                        return new SimpleStringProperty(c.getPolitician().getName());
                    }
                    return new SimpleStringProperty("");
                });
                candidateNameColumn.setPrefWidth(150);

                candidateElectionColumn = new TableColumn<>("Election");
                candidateElectionColumn.setCellValueFactory(cellData -> {
                    Candidate c = cellData.getValue();
                    if (c != null && c.getElection() != null) {
                        Election e = c.getElection();
                        return new SimpleStringProperty(e.getType() + " - " + e.getLocation() + " (" + e.getYear() + ")");
                    }
                    return new SimpleStringProperty("");
                });
                candidateElectionColumn.setPrefWidth(200);

                candidatePartyColumn = new TableColumn<>("Party");
                candidatePartyColumn.setCellValueFactory(cellData -> {
                    Candidate c = cellData.getValue();
                    if (c != null) {
                        return new SimpleStringProperty(c.getPartyAffiliation());
                    }
                    return new SimpleStringProperty("");
                });
                candidatePartyColumn.setPrefWidth(120);

                candidateVotesColumn = new TableColumn<>("Votes");
                candidateVotesColumn.setCellValueFactory(cellData -> {
                    Candidate c = cellData.getValue();
                    if (c != null) {
                        return new SimpleIntegerProperty(c.getVotesReceived()).asObject();
                    }
                    return new SimpleIntegerProperty(0).asObject();
                });
                candidateVotesColumn.setPrefWidth(80);

                candidateWinnerColumn = new TableColumn<>("Winner");
                candidateWinnerColumn.setCellValueFactory(cellData -> {
                    Candidate c = cellData.getValue();
                    if (c != null) {
                        return new SimpleStringProperty(c.isWinner() ? "Yes" : "No");
                    }
                    return new SimpleStringProperty("No");
                });
                candidateWinnerColumn.setPrefWidth(60);

                candidateWinnerColumn.setCellFactory(column -> new TableCell<Candidate, String>() {
                    @Override
                    protected void updateItem(String isWinner, boolean empty) {
                        super.updateItem(isWinner, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(isWinner);
                            Candidate candidate = getTableRow().getItem();
                            if (candidate != null && candidate.isWinner()) {
                                setStyle("-fx-background-color: #90EE90; -fx-font-weight: bold;");
                            } else {
                                setStyle("");
                            }
                        }
                    }
                });

                candidateTableView.getColumns().addAll(
                        candidateNameColumn,
                        candidateElectionColumn,
                        candidatePartyColumn,
                        candidateVotesColumn,
                        candidateWinnerColumn
                );

                candidateTableView.setRowFactory(tv -> new TableRow<Candidate>() {
                    @Override
                    protected void updateItem(Candidate candidate, boolean empty) {
                        super.updateItem(candidate, empty);
                        if (empty || candidate == null) {
                            setStyle("");
                        } else if (candidate.isWinner()) {
                            setStyle("-fx-background-color: #829a84;");
                        } else {
                            setStyle("");
                        }
                    }
                });

                System.out.println("Candidate table configured with " + candidateTableView.getColumns().size() + " columns");

            } catch (Exception e) {
                System.err.println("ERROR setting up candidate table: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("WARNING: candidateTableView is null!");
        }
    }

    private void setCellFactory(TableColumn column, String property) {
        if (column != null) {
            try {
                column.setCellValueFactory(new PropertyValueFactory<>(property));
            } catch (Exception e) {
                System.err.println("  - ERROR setting cell factory for " + property + ": " + e.getMessage());
            }
        }
    }

    private void setupTableSelectionListeners() {
        System.out.println("[ElectionMenuController] Setting up table selection listeners...");
        try {
            setupSelectionListener(politicianTableView, this::loadPoliticianIntoForm, politiciansTab);
            setupSelectionListener(electionTableView, this::loadElectionIntoForm, electionsTab);
            setupSelectionListener(candidateTableView, this::loadCandidateIntoForm, candidatesTab);
            System.out.println("  - Selection listeners configured");
        } catch (Exception e) {
            System.err.println("  - ERROR setting up selection listeners: " + e.getMessage());
        }
    }


    private void loadPoliticianIntoForm(Politician politician) {
        if (politician == null) return;

        try {
            System.out.println("[ElectionMenuController] Loading politician into form: " + politician.getName());

            setTextField(politicianNameField, politician.getName());
            setTextField(politicianPartyField, politician.getCurrentParty());
            setTextField(politicianCountyField, politician.getHomeCounty());


            if (politician.getDateOfBirth() != null) {
                setTextField(politicianDOBField, politician.getDateOfBirth().toString());
            } else {
                setTextField(politicianDOBField, "");
            }

            String imageUrl = politician.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                setTextField(politicianImageUrlField, imageUrl);
                photoUrl = imageUrl;
            } else {
                clearTextField(politicianImageUrlField);
                photoUrl = "";
            }


            if (politicianImageView != null) {
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    loadImageAsync(politicianImageView, imageUrl, 140, 130);

                    String filename = getFilenameFromUrl(imageUrl);
                    if (imageNameLabel != null) {
                        imageNameLabel.setText(filename);
                    }
                } else {
                    politicianImageView.setImage(loadPlaceholderImage(140, 130));
                    if (imageNameLabel != null) {
                        imageNameLabel.setText("No image available");
                    }
                }

                politicianImageView.setVisible(true);
                if (imagePreviewPane != null) {
                    imagePreviewPane.setVisible(false);
                }
            }

            System.out.println("  - Politician form loaded successfully");

        } catch (Exception e) {
            System.err.println("ERROR loading politician into form: " + e.getMessage());
            e.printStackTrace();
            showAlert("Failed to load politician data into form.");
        }
    }

    private <T> void setupSelectionListener(TableView<T> tableView, java.util.function.Consumer<T> loader, Tab tab) {
        if (tableView != null) {
            tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    try {
                        loader.accept(newSelection);
                        if (mainTabPane != null && tab != null) mainTabPane.getSelectionModel().select(tab);
                    } catch (Exception e) {
                        System.err.println("  - ERROR in selection listener: " + e.getMessage());
                    }
                }
            });
        }
    }


    public void setMapViewOpener(MapViewOpener mapViewOpener) {
        this.mapViewOpener = mapViewOpener;
        System.out.println("[ElectionMenuController] MapViewOpener set: " + (mapViewOpener != null ? "OK" : "NULL"));
    }

    private void refreshTableData() {
        System.out.println("[ElectionMenuController] refreshTableData() called");

        try {
            if (electionSystemAPI != null) {
                HashList<Politician> politicians = electionSystemAPI.getAllPoliticians();
                HashList<Election> elections = electionSystemAPI.getAllElections();
                HashList<Candidate> candidates = electionSystemAPI.getAllCandidates();

                Platform.runLater(() -> {
                    try {
                        if (politicianTableView != null) {
                            politicianTableView.getItems().clear();

                            for (int i = 0; i < politicians.size(); i++) {
                                Politician p = politicians.getByIndex(i);
                                if (p != null) {
                                    politicianTableView.getItems().add(p);
                                }
                            }
                            politicianTableView.refresh();
                        }

                        if (electionTableView != null) {
                            electionTableView.getItems().clear();
                            System.out.println("Loading " + elections.size() + " elections into table");

                            for (int i = 0; i < elections.size(); i++) {
                                Election e = elections.getByIndex(i);
                                if (e != null) {
                                    electionTableView.getItems().add(e);
                                }
                            }
                            electionTableView.refresh();
                            System.out.println("Election table now has: " + electionTableView.getItems().size() + " items");
                        }

                        if (candidateTableView != null) {
                            candidateTableView.getItems().clear();
                            System.out.println("Loading " + candidates.size() + " candidates into table");

                            for (int i = 0; i < candidates.size(); i++) {
                                Candidate c = candidates.getByIndex(i);
                                if (c != null) {
                                    candidateTableView.getItems().add(c);
                                }
                            }
                            candidateTableView.refresh();
                            System.out.println("Candidate table now has: " + candidateTableView.getItems().size() + " items");
                        }
                        loadComboBoxDataSimple();

                    } catch (Exception e) {
                        System.err.println("ERROR updating tables on UI thread: " + e.getMessage());
                        e.printStackTrace();
                        showAlert("Failed to update tables: " + e.getMessage());
                    }
                });

            }

        } catch (Exception e) {
            System.err.println("ERROR in refreshTableData: " + e.getMessage());
            e.printStackTrace();

            Platform.runLater(() -> {
                showAlert("Failed to load data: " + e.getMessage());
            });
        }
    }

    public void setSystemComponents(ElectionSystemAPI api, PoliticianManager pm, ElectionManager em, CandidateManager cm) {
        this.electionSystemAPI = api;
        this.politicianManager = pm;
        this.electionManager = em;
        this.candidateManager = cm;

        if (candidateManager == null) {
            if (api != null) {
                this.candidateManager = new CandidateManager(api);
            }
        }

        Platform.runLater(() -> {
            if (politicianTableView != null) {
                politicianTableView.setPlaceholder(new Label("Loading politicians..."));
            }
            if (electionTableView != null) {
                electionTableView.setPlaceholder(new Label("Loading elections..."));
            }
            if (candidateTableView != null) {
                candidateTableView.setPlaceholder(new Label("Loading candidates..."));
            }
        });

        Platform.runLater(() -> {
            try {
                refreshTableData();
            } catch (Exception e) {
                System.err.println("ERROR in deferred table refresh: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void loadComboBoxDataSimple() {
        if (candidatePoliticianComboBox != null && electionSystemAPI != null) {
            try {
                candidatePoliticianComboBox.getItems().clear();
                candidatePoliticianComboBox.getItems().add("Select a politician...");

                HashList<Politician> politicians = electionSystemAPI.getAllPoliticians();

                int limit = Math.min(200, politicians.size());
                for (int i = 0; i < limit; i++) {
                    Politician p = politicians.getByIndex(i);
                    if (p != null) {
                        candidatePoliticianComboBox.getItems().add(p.getName());
                    }

                    if (i % 50 == 0 && i > 0) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }

                candidatePoliticianComboBox.setValue("Select a politician...");
            } catch (Exception e) {
                System.err.println("  - ERROR loading politician combo box: " + e.getMessage());
            }
        }

        if (candidateElectionComboBox != null && electionSystemAPI != null) {
            try {
                candidateElectionComboBox.getItems().clear();
                candidateElectionComboBox.getItems().add("Select an election...");

                HashList<Election> elections = electionSystemAPI.getAllElections();
                int limit = Math.min(200, elections.size());
                for (int i = 0; i < limit; i++) {
                    Election e = elections.getByIndex(i);
                    if (e != null) {
                        candidateElectionComboBox.getItems().add(
                                e.getType() + " - " + e.getLocation() + " (" + e.getYear() + ")"
                        );
                    }

                    if (i % 50 == 0 && i > 0) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e1) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }

                candidateElectionComboBox.setValue("Select an election...");

            } catch (Exception e) {
                System.err.println("  - ERROR loading election combo box: " + e.getMessage());
            }
        }

        if (candidatePartyComboBox != null && electionSystemAPI != null) {
            try {
                candidatePartyComboBox.getItems().clear();
                candidatePartyComboBox.getItems().add("Select a party...");

                HashList<Politician> politicians = electionSystemAPI.getAllPoliticians();
                System.out.println("  - Loading parties from " + politicians.size() + " politicians");
                HashList<String> uniqueParties = new HashList<>();
                int limit = Math.min(300, politicians.size());

                for (int i = 0; i < limit; i++) {
                    Politician p = politicians.getByIndex(i);
                    if (p != null) {
                        String party = p.getCurrentParty();
                        if (party != null && !party.isEmpty() && !uniqueParties.contains(party)) {
                            uniqueParties.addAtLast(party);
                            candidatePartyComboBox.getItems().add(party);
                        }
                    }

                    if (i % 50 == 0 && i > 0) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }

                candidatePartyComboBox.setValue("Select a party...");
                System.out.println("  - Party combo box loaded with " + uniqueParties.size() + " unique parties");

            } catch (Exception e) {
                System.err.println("  - ERROR loading party combo box: " + e.getMessage());
            }
        }

        System.out.println("  - All combo boxes loaded");
    }


    /**
     * HANDLERS
     */
    private void handleSearchPoliticians() {
        if (politicianManager == null) return;
        try {
            HashList<Politician> results = politicianManager.searchPoliticians(
                    getTrimmedText(searchPoliticianName),
                    getTrimmedText(searchPoliticianParty),
                    getTrimmedText(searchPoliticianCounty)
            );
            displayResults(politicianSearchResults, results, "politician(s)", this::formatPoliticianResult);
        } catch (Exception e) {
            System.err.println("ERROR in handleSearchPoliticians: " + e.getMessage());
            showAlert("Search failed: " + e.getMessage());
        }
    }

    private void handleShowAllPoliticians() {
        if (politicianManager == null) return;
        try {
            clearSearchFields(searchPoliticianName, searchPoliticianParty, searchPoliticianCounty);
            setTextArea(politicianSearchResults, politicianManager.listAllPoliticians());
        } catch (Exception e) {
            System.err.println("ERROR in handleShowAllPoliticians: " + e.getMessage());
            showAlert("Failed to show all politicians: " + e.getMessage());
        }
    }

    private void handleSearchElections() {
        if (electionManager == null) return;

        try {
            ElectionType type = searchElectionType.getValue();
            Integer year = parseInteger(searchElectionYear.getText());
            String location = getTrimmedText(searchElectionLocation);

            HashList<Election> results = electionManager.searchElections(type, year, location);
            displayResults(electionSearchResults, results, "election(s)", this::formatElectionResult);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            showAlert("Search failed: " + e.getMessage());
        }
    }

    private void handleShowAllElections() {
        if (electionManager == null) return;
        try {
            clearSearchFields(searchElectionYear, searchElectionLocation);
            if (searchElectionType != null) searchElectionType.setValue(null);
            setTextArea(electionSearchResults, electionManager.listAllElections());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            showAlert("Failed to show all elections: " + e.getMessage());
        }
    }

    private void handleSearchCandidates() {

        if (candidateManager == null) {
            if (electionSystemAPI != null) {
                candidateManager = new CandidateManager(electionSystemAPI);
            }
            if (candidateManager == null) {
                return;
            }
        }

        try {
            String party = getTrimmedText(searchCandidateParty);
            ElectionType electionType = searchCandidateElectionType.getValue();
            Integer year = parseInteger(searchCandidateYear.getText());
            HashList<Candidate> results = new HashList<>();

            if ((party == null || party.isEmpty()) &&
                    electionType == null &&
                    year == null) {
                results = electionSystemAPI.getAllCandidates();
                displayResults(candidateSearchResults, results, "candidate(s)", this::formatCandidateResult);
                return;
            }

            results = electionSystemAPI.getAllCandidates();
            if (results.isEmpty()) {
                displayResults(candidateSearchResults, results, "candidate(s)", this::formatCandidateResult);
                return;
            }

            if (party != null && !party.isEmpty()) {
                HashList<Candidate> filtered = new HashList<>();
                for (int i = 0; i < results.size(); i++) {
                    Candidate c = results.getByIndex(i);
                    if (c != null && c.getPartyAffiliation() != null &&
                            c.getPartyAffiliation().toLowerCase().contains(party.toLowerCase())) {
                        filtered.add(c.hashCode(), c);
                    }
                }
                results = filtered;
            }


            if (electionType != null) {
                HashList<Candidate> filtered = new HashList<>();
                for (int i = 0; i < results.size(); i++) {
                    Candidate c = results.getByIndex(i);
                    if (c != null && c.getElection() != null &&
                            c.getElection().getType() == electionType) {
                        filtered.add(c.hashCode(), c);
                    }
                }
                results = filtered;
                if (results.isEmpty()) {
                    String electionTypeStr = electionType.toString();
                    HashList<Candidate> typeResults = candidateManager.searchCandidatesByElectionType(electionTypeStr);
                    if (!typeResults.isEmpty()) {
                        results = typeResults;
                    }
                }
            }

            if (year != null && !results.isEmpty()) {
                HashList<Candidate> filtered = new HashList<>();
                for (int i = 0; i < results.size(); i++) {
                    Candidate c = results.getByIndex(i);
                    if (c != null && c.getElection() != null && c.getElection().getYear() == year) {
                        filtered.add(c.hashCode(), c);
                    }
                }
                results = filtered;
            }

            if (results.isEmpty() && electionType != null &&
                    (party == null || party.isEmpty()) && year == null) {
                results = searchCandidatesByElectionTypeDirect(electionType);
            }

            displayResults(candidateSearchResults, results, "candidate(s)", this::formatCandidateResult);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            showAlert("Search failed: " + e.getMessage());
        }
    }

    private HashList<Candidate> searchCandidatesByElectionTypeDirect(ElectionType electionType) {
        HashList<Candidate> results = new HashList<>();

        if (electionSystemAPI == null || electionType == null) {
            return results;
        }

        try {
            HashList<Election> elections = electionSystemAPI.searchElectionsByType(electionType);
            HashList<Candidate> allCandidates = electionSystemAPI.getAllCandidates();

            for (int i = 0; i < allCandidates.size(); i++) {
                Candidate c = allCandidates.getByIndex(i);
                if (c != null && c.getElection() != null) {
                    for (int j = 0; j < elections.size(); j++) {
                        Election e = elections.getByIndex(j);
                        if (e != null && e.getId() == c.getElection().getId()) {
                            results.add(c.hashCode(), c);
                            break;
                        }
                    }
                }
            }


        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    private void handleShowAllCandidates() {
        if (candidateManager == null) {
            showAlert("Candidate manager not initialized.");
            return;
        }

        try {
            clearSearchFields(searchCandidateParty, searchCandidateYear);
            if (searchCandidateElectionType != null) {
                searchCandidateElectionType.setValue(null);
            }

            String allCandidatesText = candidateManager.listAllCandidates();
            if (candidateSearchResults != null) {
                candidateSearchResults.setText(allCandidatesText);
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            showAlert("Failed to show all candidates: " + e.getMessage());
        }
    }

    private void handleSortPoliticians() {
        if (sortPoliticianBy == null) return;
        try {
            handleSort("Politicians", sortPoliticianBy.getValue(), politicianAsc.isSelected(),
                    () -> convertPoliticiansToSortResults(getSortedPoliticians(sortPoliticianBy.getValue(), politicianAsc.isSelected())));
        } catch (Exception e) {
            System.err.println("ERROR in handleSortPoliticians: " + e.getMessage());
            showAlert("Sort failed: " + e.getMessage());
        }
    }

    private void handleSortElections() {
        if (sortElectionBy == null) return;
        try {
            handleSort("Elections", sortElectionBy.getValue(), electionAsc.isSelected(),
                    () -> convertElectionsToSortResults(getSortedElections(sortElectionBy.getValue(), electionAsc.isSelected())));
        } catch (Exception e) {
            System.err.println("ERROR in handleSortElections: " + e.getMessage());
            showAlert("Sort failed: " + e.getMessage());
        }
    }

    private void handleSortCandidates() {
        if (sortCandidateBy == null) return;
        try {
            handleSort("Candidates", sortCandidateBy.getValue(), candidateAsc.isSelected(),
                    () -> convertCandidatesToSortResults(getSortedCandidates(sortCandidateBy.getValue(), candidateAsc.isSelected())));
        } catch (Exception e) {
            System.err.println("ERROR in handleSortCandidates: " + e.getMessage());
            showAlert("Sort failed: " + e.getMessage());
        }
    }

    private void handleAddPolitician() {
        try {
            if (!validatePoliticianForm()) return;

            String name = getTrimmedText(politicianNameField);
            String party = getTrimmedText(politicianPartyField);
            String county = getTrimmedText(politicianCountyField);

            LocalDate dob = LocalDate.parse(getTrimmedText(politicianDOBField));

            String imageUrl = getTrimmedText(politicianImageUrlField);
            if (imageUrl.isEmpty()) imageUrl = photoUrl;

            boolean success = politicianManager.addPolitician(name, dob, party, county, imageUrl);
            if (success) {
                showAlert("Politician added successfully.");
                refreshTableData();
                clearPoliticianForm();
            } else {
                showAlert("Failed to add politician.");
            }
        } catch (Exception e) {
            showAlert("Failed to add politician: " + e.getMessage());
        }
    }

    private void handleUpdatePolitician() {
        Politician selected = politicianTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No politician selected.");
            return;
        }

        try {
            String currentName = selected.getName();
            if (currentName == null || currentName.trim().isEmpty()) {
                showAlert("Selected politician has no name.");
                return;
            }

            String newName = getTrimmedText(politicianNameField);
            String party = getTrimmedText(politicianPartyField);
            String county = getTrimmedText(politicianCountyField);
            LocalDate dob = null;
            try {
                dob = LocalDate.parse(getTrimmedText(politicianDOBField));
            } catch (Exception e) {
                showAlert("Please enter a valid date in YYYY-MM-DD format.");
                return;
            }

            String imageUrl = getTrimmedText(politicianImageUrlField);
            if (imageUrl.isEmpty()) imageUrl = photoUrl;

            boolean updated = false;
            if (!newName.isEmpty() && !newName.equals(currentName)) {
                if (politicianManager.updatePoliticianName(currentName, newName)) {
                    currentName = newName;
                    updated = true;
                }
            }
            if (!party.isEmpty() && !party.equals(selected.getCurrentParty())) {
                politicianManager.updatePoliticianParty(currentName, party);
                updated = true;
            }
            if (!county.isEmpty() && !county.equals(selected.getHomeCounty())) {
                politicianManager.updatePoliticianCounty(currentName, county);
                updated = true;
            }
            if (dob != null && !dob.equals(selected.getDateOfBirth())) {
                politicianManager.updatePoliticianDOB(currentName, dob);
                updated = true;
            }
            if (!imageUrl.isEmpty() && !imageUrl.equals(selected.getImageUrl())) {
                politicianManager.updatePoliticianURL(currentName, imageUrl);
                updated = true;
            }

            if (updated) {
                politicianTableView.refresh();
                showAlert("Politician updated successfully.");
                politicianTableView.getSelectionModel().clearSelection();
                clearPoliticianForm();
            } else {
                showAlert("No changes were made.");
            }
        } catch (Exception e) {
            showAlert("Failed to update politician: " + e.getMessage());
        }
    }

    private void handleDeletePolitician() {
        Politician selected = politicianTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No politician selected.");
            return;
        }

        if (!showConfirmation("Delete Politician",
                "Are you sure you want to delete '" + selected.getName() + "'?")) return;

        boolean success = politicianManager.removePolitician(selected.getName());
        if (success) {
            showAlert("Politician deleted successfully.");
            refreshTableData();
            clearPoliticianForm();
        } else {
            showAlert("Failed to delete politician.");
        }
    }

    private void handleAddElection() {
        try {
            if (!validateElectionForm()) return;

            ElectionType type = electionTypeComboBox.getValue();
            String location = getTrimmedText(electionLocationField);
            int year = Integer.parseInt(getTrimmedText(electionYearField));
            int month = Integer.parseInt(getTrimmedText(electionMonthField));
            int day = Integer.parseInt(getTrimmedText(electionDayField));
            int seats = Integer.parseInt(getTrimmedText(electionSeatsField));
            LocalDate date = LocalDate.of(year, month, day);

            boolean success = electionManager.addElection(type, location, date, seats);
            if (success) {
                showAlert("Election added successfully.");
                refreshTableData();
                clearElectionForm();
            } else {
                showAlert("Failed to add election.");
            }
        } catch (Exception e) {
            showAlert("Failed to add election: " + e.getMessage());
        }
    }

    private void handleUpdateElection() {
        Election selected = electionTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No election selected.");
            return;
        }

        try {
            if (!validateElectionForm()) return;

            ElectionType type = electionTypeComboBox.getValue();
            String location = getTrimmedText(electionLocationField);
            int year = Integer.parseInt(getTrimmedText(electionYearField));
            int month = Integer.parseInt(getTrimmedText(electionMonthField));
            int day = Integer.parseInt(getTrimmedText(electionDayField));
            int seats = Integer.parseInt(getTrimmedText(electionSeatsField));
            LocalDate date = LocalDate.of(year, month, day);

            electionManager.updateElectionType(selected, type);
            electionManager.updateElectionLocation(selected, location);
            electionManager.updateElectionDate(selected, date);
            electionManager.updateElectionSeats(selected, seats);

            electionTableView.refresh();
            showAlert("Election updated successfully.");
        } catch (Exception e) {
            showAlert("Failed to update election: " + e.getMessage());
        }
    }

    private void handleDeleteElection() {
        Election selected = electionTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No election selected.");
            return;
        }

        if (!showConfirmation("Delete Election",
                "Are you sure you want to delete this election?")) return;

        boolean success = electionManager.removeElection(selected);
        if (success) {
            showAlert("Election deleted successfully.");
            refreshTableData();
            clearElectionForm();
        } else {
            showAlert("Failed to delete election.");
        }
    }

    private void handleAddCandidate() {
        try {
            if (!validateCandidateForm()) return;

            String politicianName = candidatePoliticianComboBox.getValue();
            String electionInfo = candidateElectionComboBox.getValue();
            String party = candidatePartyComboBox.getValue();
            int votes = Integer.parseInt(getTrimmedText(candidateVotesField));
            boolean isWinner = candidateWinnerCheckBox.isSelected();

            if (!validateComboBoxSelections(politicianName, electionInfo, party)) return;

            Election election = findElectionFromCombo(electionInfo);
            if (election == null) {
                showAlert("Could not find selected election.");
                return;
            }

            boolean success = candidateManager.addCandidate(politicianName, election, party, votes, isWinner);
            if (success) {
                showAlert("Candidate added successfully.");
                refreshTableData();
                updatePoliticianElectionCounts();
                clearCandidateForm();
            } else {
                showAlert("Failed to add candidate.");
            }
        } catch (Exception e) {
            showAlert("Failed to add candidate: " + e.getMessage());
        }
    }

    private void handleUpdateCandidate() {
        Candidate selected = candidateTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No candidate selected.");
            return;
        }

        try {
            if (!validateCandidateForm()) return;

            String politicianName = candidatePoliticianComboBox.getValue();
            int votes = Integer.parseInt(getTrimmedText(candidateVotesField));
            boolean isWinner = candidateWinnerCheckBox.isSelected();
            String party = candidatePartyComboBox.getValue();

            if (politicianName == null || politicianName.equals("Select a politician...") ||
                    party == null || party.equals("Select a party...")) {
                showAlert("Please select both a politician and a party.");
                return;
            }

            Politician politician = politicianManager.findPolitician(politicianName);
            if (politician == null) {
                showAlert("Politician not found: " + politicianName);
                return;
            }

            Election election = selected.getElection();
            if (election == null) {
                showAlert("Candidate is not associated with an election.");
                return;
            }

            boolean votesUpdated = electionManager.updateCandidateVotes(politician, election, votes);
            boolean winnerUpdated = candidateManager.updateCandidateWinnerStatus(politician.getName(), election, isWinner);

            if (!party.equals(selected.getPartyAffiliation())) {
                boolean partyUpdated = electionManager.updateCandidateParty(politician, election, party);
            }
            electionManager.determineWinners(election);

            Platform.runLater(() -> {
                candidateTableView.refresh();
                electionTableView.refresh();


                if (electionTableView != null) {
                    electionTableView.getItems().clear();
                    HashList<Election> elections = electionSystemAPI.getAllElections();
                    for (int i = 0; i < elections.size(); i++) {
                        Election e = elections.getByIndex(i);
                        if (e != null) {
                            electionTableView.getItems().add(e);
                        }
                    }
                    electionTableView.refresh();
                }
            });
            showAlert("Candidate updated successfully.");

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            showAlert("Failed to update candidate: " + e.getMessage());
        }
    }

    private void handleDeleteCandidate() {
        Candidate selected = candidateTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No candidate selected.");
            return;
        }

        if (!showConfirmation("Delete Candidate",
                "Are you sure you want to delete this candidacy?")) return;

        boolean success = electionManager.removeCandidateFromElection(selected.getPolitician(), selected.getElection());
        if (success) {
            showAlert("Candidate deleted successfully.");
            refreshTableData();
            clearCandidateForm();
        } else {
            showAlert("Failed to delete candidate.");
        }
    }

    private void handleAddImage() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Politician Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
            );

            Stage stage = (Stage) addImageButton.getScene().getWindow();
            selectedPhotoFile = fileChooser.showOpenDialog(stage);

            if (selectedPhotoFile != null) {
                photoUrl = selectedPhotoFile.getAbsolutePath();
                setTextField(politicianImageUrlField, photoUrl);
                updateImagePreview();
            }
        } catch (Exception e) {
            System.err.println("ERROR in handleAddImage: " + e.getMessage());
            showAlert("Failed to add image: " + e.getMessage());
        }
    }

    private void handleRemoveImage() {
        try {
            clearImagePreview();
            clearTextField(politicianImageUrlField);
            photoUrl = "";
        } catch (Exception e) {
            System.err.println("ERROR in handleRemoveImage: " + e.getMessage());
            showAlert("Failed to remove image: " + e.getMessage());
        }
    }

    private void handleSave() {
        if (electionSystemAPI == null) {
            showAlert("Cannot save - system not initialized.");
            return;
        }

        try {
            String filename = "election_hash_data.dat";
            boolean success = electionSystemAPI.saveData(filename);
            showAlert(success ? "Data saved successfully." : "Failed to save data.");
        } catch (Exception e) {
            showAlert("Failed to save data: " + e.getMessage());
        }
    }

    private void handleLoad() {
        if (electionSystemAPI == null) {
            showAlert("Cannot load - system not initialized.");
            return;
        }

        try {
            String filename = "election_hash_data.dat";
            File file = new File(filename);

            if (!file.exists()) {
                showAlert("No saved data found.");
                return;
            }

            if (!showConfirmation("Load Election Data",
                    "Are you sure?")) return;

            boolean success = electionSystemAPI.loadData(filename);
            if (success) {
                electionManager = new ElectionManager(electionSystemAPI);
                politicianManager = new PoliticianManager(electionSystemAPI, electionManager);
                candidateManager = new CandidateManager(electionSystemAPI);

                refreshTableData();
                clearAllForms();
                showAlert("Data loaded successfully.");
            } else {
                showAlert("Failed to load data.");
            }
        } catch (Exception e) {
            showAlert("Failed to load data: " + e.getMessage());
        }
    }

    private void loadElectionIntoForm(Election election) {
        if (election == null) return;

        try {
            if (electionTypeComboBox != null) electionTypeComboBox.setValue(election.getType());
            setTextField(electionLocationField, election.getLocation());
            setTextField(electionSeatsField, String.valueOf(election.getSeatsAvailable()));

            LocalDate date = election.getDate();
            if (date != null) {
                setTextField(electionYearField, String.valueOf(date.getYear()));
                setTextField(electionMonthField, String.valueOf(date.getMonthValue()));
                setTextField(electionDayField, String.valueOf(date.getDayOfMonth()));
            }
        } catch (Exception e) {
            System.err.println("ERROR loading election into form: " + e.getMessage());
        }
    }

    private void loadCandidateIntoForm(Candidate candidate) {
        if (candidate == null) return;

        try {
            setTextField(candidateVotesField, String.valueOf(candidate.getVotesReceived()));
            if (candidatePoliticianComboBox != null && candidate.getPolitician() != null) {
                candidatePoliticianComboBox.setValue(candidate.getPolitician().getName());
            }
            if (candidateElectionComboBox != null && candidate.getElection() != null) {
                Election e = candidate.getElection();
                candidateElectionComboBox.setValue(e.getType() + " - " + e.getLocation() + " (" + e.getYear() + ")");
            }
            if (candidatePartyComboBox != null) candidatePartyComboBox.setValue(candidate.getPartyAffiliation());
            if (candidateWinnerCheckBox != null) candidateWinnerCheckBox.setSelected(candidate.isWinner());
        } catch (Exception e) {
            System.err.println("ERROR loading candidate into form: " + e.getMessage());
        }
    }

    private boolean validatePoliticianForm() {
        if (isFieldEmpty(politicianNameField, "politician name")) return false;
        if (isFieldEmpty(politicianPartyField, "political party")) return false;
        if (isFieldEmpty(politicianCountyField, "home county")) return false;

        String dobText = getTrimmedText(politicianDOBField);
        if (dobText.isEmpty()) {
            showAlert("Please enter a date of birth (YYYY-MM-DD).");
            return false;
        }

        try {
            LocalDate dob = LocalDate.parse(dobText);
            LocalDate minDate = LocalDate.of(1900, 1, 1);
            LocalDate maxDate = LocalDate.now().minusYears(18);

            if (dob.isBefore(minDate) || dob.isAfter(maxDate)) {
                showAlert("Please enter a valid date of birth between 1900-01-01 and " +
                        maxDate.toString() + " (must be at least 18 years old).");
                return false;
            }
        } catch (Exception e) {
            showAlert("Please enter a valid date in YYYY-MM-DD format.");
            return false;
        }

        return true;
    }

    private boolean validateElectionForm() {
        if (electionTypeComboBox.getValue() == null) {
            showAlert("Please select an election type.");
            return false;
        }
        if (isFieldEmpty(electionLocationField, "election location")) return false;

        String[] fieldNames = {"Year", "Month", "Day", "Seats"};
        TextField[] fields = {electionYearField, electionMonthField, electionDayField, electionSeatsField};

        for (int i = 0; i < fieldNames.length; i++) {
            try {
                int value = Integer.parseInt(getTrimmedText(fields[i]));
                if (value <= 0 && !fieldNames[i].equals("Year")) {
                    showAlert(fieldNames[i] + " must be a positive number.");
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert("Please enter a valid number for " + fieldNames[i] + ".");
                return false;
            }
        }

        try {
            int year = Integer.parseInt(getTrimmedText(electionYearField));
            int month = Integer.parseInt(getTrimmedText(electionMonthField));
            int day = Integer.parseInt(getTrimmedText(electionDayField));
            LocalDate.of(year, month, day);
        } catch (Exception e) {
            showAlert("Please enter a valid date.");
            return false;
        }
        return true;
    }

    private boolean validateCandidateForm() {
        if (!validateComboBoxSelection(candidatePoliticianComboBox, "politician")) return false;
        if (!validateComboBoxSelection(candidateElectionComboBox, "election")) return false;
        if (!validateComboBoxSelection(candidatePartyComboBox, "party")) return false;

        try {
            int votes = Integer.parseInt(getTrimmedText(candidateVotesField));
            if (votes < 0) {
                showAlert("Votes cannot be negative.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Please enter a valid number for votes.");
            return false;
        }
        return true;
    }

    private boolean validateComboBoxSelection(ComboBox<String> combo, String fieldName) {
        String value = combo.getValue();
        if (value == null || value.equals("Select a " + fieldName + "...")) {
            showAlert("Please select a " + fieldName + ".");
            return false;
        }
        return true;
    }

    private boolean validateComboBoxSelections(String politician, String election, String party) {
        return !(politician == null || politician.equals("Select a politician...") ||
                election == null || election.equals("Select an election...") ||
                party == null || party.equals("Select a party..."));
    }

    private boolean isFieldEmpty(TextField field, String fieldName) {
        if (getTrimmedText(field).isEmpty()) {
            showAlert("Please enter a " + fieldName + ".");
            return true;
        }
        return false;
    }

    private void handleSort(String type, String sortBy, boolean ascending, Runnable converter) {
        try {
            clearSortingTable();
            if (sortingResultsLabel != null) {
                sortingResultsLabel.setText(type + " sorted by " + sortBy + " (" + (ascending ? "ascending" : "descending") + "):");
            }
            converter.run();
            sortingResultsTableView.refresh();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            showAlert("Sort operation failed: " + e.getMessage());
        }
    }

    private void clearSortingTable() {
        sortingResultsTableView.getItems().clear();
    }

    private HashList<Politician> getSortedPoliticians(String sortBy, boolean ascending) {
        return sortItems(electionSystemAPI.getAllPoliticians(), sortBy, ascending, this::comparePoliticians);
    }

    private HashList<Election> getSortedElections(String sortBy, boolean ascending) {
        return sortItems(electionSystemAPI.getAllElections(), sortBy, ascending, this::compareElections);
    }

    private HashList<Candidate> getSortedCandidates(String sortBy, boolean ascending) {
        return sortItems(electionSystemAPI.getAllCandidates(), sortBy, ascending, this::compareCandidates);
    }

    private <T> HashList<T> sortItems(HashList<T> items, String sortBy, boolean ascending, java.util.function.BiFunction<T, T, Integer> comparator) {
        if (items.size() <= 1) return items;

        HashList<T> sorted = items.getSortedValues((a, b) -> {
            int compare = comparator.apply(a, b);
            return ascending ? compare : -compare;
        }, true);
        return sorted;
    }

    private int comparePoliticians(Politician p1, Politician p2) {
        if (p1 == null && p2 == null) return 0;
        if (p1 == null) return -1;
        if (p2 == null) return 1;

        String sortBy = sortPoliticianBy.getValue();
        switch (sortBy.toLowerCase()) {
            case "party":
                return p1.getCurrentParty().compareToIgnoreCase(p2.getCurrentParty());
            case "county":
                return p1.getHomeCounty().compareToIgnoreCase(p2.getHomeCounty());
            default:
                return p1.getName().compareToIgnoreCase(p2.getName());
        }
    }

    private int compareElections(Election e1, Election e2) {
        if (e1 == null && e2 == null) return 0;
        if (e1 == null) return -1;
        if (e2 == null) return 1;

        String sortBy = sortElectionBy.getValue();
        switch (sortBy.toLowerCase()) {
            case "type":
                return e1.getType().toString().compareToIgnoreCase(e2.getType().toString());
            case "year":
                return Integer.compare(e1.getYear(), e2.getYear());
            case "location":
                return e1.getLocation().compareToIgnoreCase(e2.getLocation());
            default:
                return e1.getDate().compareTo(e2.getDate());
        }
    }

    private int compareCandidates(Candidate c1, Candidate c2) {
        if (c1 == null && c2 == null) return 0;
        if (c1 == null) return -1;
        if (c2 == null) return 1;

        String sortBy = sortCandidateBy.getValue();
        switch (sortBy.toLowerCase()) {
            case "name":
                String name1 = c1.getPolitician() != null ? c1.getPolitician().getName() : "";
                String name2 = c2.getPolitician() != null ? c2.getPolitician().getName() : "";
                return name1.compareToIgnoreCase(name2);
            case "party":
                return c1.getPartyAffiliation().compareToIgnoreCase(c2.getPartyAffiliation());
            default:
                return Integer.compare(c1.getVotesReceived(), c2.getVotesReceived());
        }
    }

    private void convertPoliticiansToSortResults(HashList<Politician> politicians) {
        convertToSortResults(politicians, "Politician", p ->
                new SortResultItem("Politician", p.getName(),
                        p.getCurrentParty() + " | " + p.getHomeCounty(), ""));
    }

    private void convertElectionsToSortResults(HashList<Election> elections) {
        convertToSortResults(elections, "Election", e -> {
            String winnerName = getWinnerNameFromElection(e);
            return new SortResultItem("Election",
                    e.getLocation() + " (" + e.getYear() + ")",
                    e.getType() + " | " + e.getDate().toString(),
                    winnerName);
        });
    }

    private void convertCandidatesToSortResults(HashList<Candidate> candidates) {
        convertToSortResults(candidates, "Candidate", c -> {
            String politicianName = c.getPolitician() != null ? c.getPolitician().getName() : "Unknown";
            return new SortResultItem("Candidate", politicianName,
                    c.getPartyAffiliation() + " | " + c.getVotesReceived() + " votes",
                    c.isWinner() ? politicianName : "");
        });
    }

    private <T> void convertToSortResults(HashList<T> items, String typeName, java.util.function.Function<T, SortResultItem> mapper) {
        HashList<SortResultItem> results = new HashList<>();
        for (int i = 0; i < items.size(); i++) {
            T item = items.getByIndex(i);
            if (item != null) results.add(item.hashCode(), mapper.apply(item));
        }
        updateSortingTable(results);
    }

    private void updateSortingTable(HashList<SortResultItem> results) {
        for (int i = 0; i < results.size(); i++) {
            SortResultItem item = results.getByIndex(i);
            if (item != null) sortingResultsTableView.getItems().add(item);
        }
    }

    private void updateImagePreview() {
        try {
            if (politicianImageView != null && selectedPhotoFile != null) {
                System.out.println("  - Loading image from: " + selectedPhotoFile.getAbsolutePath());
                Image image = new Image(selectedPhotoFile.toURI().toString(), 140, 130, true, true, true);

                image.errorProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal) {
                        System.err.println("  - ERROR loading image: " + selectedPhotoFile.getAbsolutePath());
                        politicianImageView.setImage(loadPlaceholderImage(140, 130));
                        setLabelText(imageNameLabel, "Default profile image");
                    }
                });

                image.progressProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal.doubleValue() == 1.0 && !image.isError()) {
                        System.out.println("  - Image loaded successfully");
                        politicianImageView.setImage(image);
                        setLabelText(imageNameLabel, selectedPhotoFile.getName());
                    }
                });

                politicianImageView.setImage(image);
                politicianImageView.setVisible(true);
                if (imagePreviewPane != null) {
                    imagePreviewPane.setVisible(false);
                }

            } else if (politicianImageView != null) {
                politicianImageView.setImage(loadPlaceholderImage(140, 130));
                politicianImageView.setVisible(true);

                if (imagePreviewPane != null) {
                    imagePreviewPane.setVisible(false);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();

            if (politicianImageView != null) {
                politicianImageView.setImage(loadPlaceholderImage(140, 130));
            }
            if (imageNameLabel != null) {
                setLabelText(imageNameLabel, "Error loading image");
            }
        }
    }


    private Election findElectionFromCombo(String electionInfo) {
        if (electionInfo == null) return null;

        try {
            String[] parts = electionInfo.split(" - ");
            if (parts.length < 2) return null;

            String typeStr = parts[0];
            String[] restParts = parts[1].split(" \\(");
            if (restParts.length < 2) return null;

            String location = restParts[0];
            int year = Integer.parseInt(restParts[1].replace(")", ""));
            ElectionType type = ElectionType.valueOf(typeStr.toUpperCase());
            return electionManager.findElection(type, location, year);
        } catch (Exception e) {
            return null;
        }
    }

    private String getYearStatistics() {
        if (electionSystemAPI == null) return "System not initialized.";

        StringBuilder stats = new StringBuilder("=== ELECTION YEAR STATISTICS ===\n\n");
        HashList<Integer> years = new HashList<>();
        HashList<Election> allElections = electionSystemAPI.getAllElections();

        for (int i = 0; i < allElections.size(); i++) {
            Election e = allElections.getByIndex(i);
            if (e != null && !years.contains(e.getYear())) years.addAtLast(e.getYear());
        }

        years.selectionSort(Integer::compareTo);
        stats.append("Elections by Year:\n");

        for (int i = 0; i < years.size(); i++) {
            Integer year = years.getByIndex(i);
            if (year != null) {
                int count = electionSystemAPI.searchElectionsByYear(year).size();
                stats.append(String.format("  %-6d: %d elections\n", year, count));
            }
        }
        return stats.toString();
    }

    private String getLocationStatistics() {
        if (electionSystemAPI == null) return "System not initialized.";

        StringBuilder stats = new StringBuilder("=== ELECTION LOCATION STATISTICS ===\n\n");
        HashList<String> locations = new HashList<>();
        HashList<Election> allElections = electionSystemAPI.getAllElections();

        for (int i = 0; i < allElections.size(); i++) {
            Election e = allElections.getByIndex(i);
            if (e != null && !locations.contains(e.getLocation())) locations.addAtLast(e.getLocation());
        }

        locations.selectionSort(String::compareToIgnoreCase);
        stats.append("Elections by Location:\n");

        for (int i = 0; i < locations.size(); i++) {
            String location = locations.getByIndex(i);
            if (location != null) {
                int count = countElectionsByLocation(allElections, location);
                stats.append(String.format("%2d. %-30s: %d elections\n", i + 1, location, count));
            }
        }
        return stats.toString();
    }

    private int countElectionsByLocation(HashList<Election> elections, String location) {
        int count = 0;
        for (int i = 0; i < elections.size(); i++) {
            Election e = elections.getByIndex(i);
            if (e != null && location.equals(e.getLocation())) count++;
        }
        return count;
    }

    private String getFullReport() {
        if (electionSystemAPI == null) return "System not initialized.";

        return "=== FULL ELECTION SYSTEM REPORT ===\n\n" +
                getOverallStatistics() + "\n" +
                getPoliticianStatistics() + "\n" +
                getElectionStatistics() + "\n" +
                getCandidateStatistics() + "\n" +
                getPartyStatistics() + "\n" +
                getYearStatistics() + "\n" +
                getLocationStatistics();
    }

    private <T> void displayResults(TextArea textArea, HashList<T> results, String itemType, HashList.StringMapper<T> formatter) {
        if (textArea == null) return;

        if (results.isEmpty()) {
            textArea.setText("No " + itemType.replace("(s)", "") + "s found matching your criteria.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(results.size()).append(" ").append(itemType).append(":\n\n");

        for (int i = 0; i < results.size(); i++) {
            T item = results.getByIndex(i);
            if (item != null) {
                sb.append(String.format("%d. %s\n", i + 1, formatter.mapToString(item)));
            }
        }
        textArea.setText(sb.toString());
    }

    private String formatPoliticianResult(Politician p) {
        int age = calculateAge(p.getDateOfBirth());
        return String.format("%s (%s) - %s\n   Age: %d, Elections: %d",
                p.getName(), p.getCurrentParty(), p.getHomeCounty(), age, p.getElectionsParticipated().size());
    }

    private String formatElectionResult(Election e) {
        return String.format("%s - %s (%d)\n   Date: %s, Seats: %d, Candidates: %d",
                e.getType(), e.getLocation(), e.getYear(), e.getDate(), e.getSeatsAvailable(), e.getCandidates().size());
    }

    private String formatCandidateResult(Candidate c) {
        return String.format("%s - %s\n   Election: %s %s (%d)\n   Votes: %,d, Winner: %s",
                c.getPolitician().getName(), c.getPartyAffiliation(),
                c.getElection().getType(), c.getElection().getLocation(), c.getElection().getYear(),
                c.getVotesReceived(), c.isWinner() ? "Yes" : "No");
    }

    private void setTextArea(TextArea area, String text) {
        if (area != null) area.setText(text);
    }

    private void setLabelText(Label label, String text) {
        if (label != null) label.setText(text);
    }

    private String getFilenameFromUrl(String url) {
        if (url == null || url.isEmpty()) return "No image selected";
        String filename = url.substring(url.lastIndexOf('/') + 1);
        filename = filename.substring(filename.lastIndexOf('\\') + 1);
        return filename.contains("placeholder") ? "Default profile image" : filename;
    }

    private Integer parseInteger(String text) {
        try {
            return text.trim().isEmpty() ? null : Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int calculateAge(LocalDate dob) {
        return dob != null ? Period.between(dob, LocalDate.now()).getYears() : 0;
    }


    private void clearSearchFields(TextField field1, TextField field2) {
        clearTextField(field1);
        clearTextField(field2);
    }

    private void clearSearchFields(TextField field1, TextField field2, TextField field3) {
        clearTextField(field1);
        clearTextField(field2);
        clearTextField(field3);
    }

    private void handleReset() {
        if (electionSystemAPI == null) {
            showAlert("System not initialized. Cannot reset.");
            return;
        }

        if (!showConfirmation("Reset System Data",
                "Are you sure?")) return;

        try {
            electionSystemAPI.clearAllData();
            electionManager = new ElectionManager(electionSystemAPI);
            politicianManager = new PoliticianManager(electionSystemAPI, electionManager);
            candidateManager = new CandidateManager(electionSystemAPI);

            clearAllForms();
            refreshTableData();
            showAlert("System reset successfully.");
        } catch (Exception e) {
            showAlert("Failed to reset system: " + e.getMessage());
        }
    }

    @FXML
    private void handleElectionMap() {
        try {
            if (mapViewOpener == null) {
                showAlert("Map view failed.");
                return;
            }

            Stage currentStage = getCurrentStage();
            if (currentStage == null) {
                showAlert("Cannot determine current window.");
                return;
            }
            mapViewOpener.openMapView(currentStage);
        } catch (Exception e) {
            System.err.println("  - ERROR in handleElectionMap: " + e.getMessage());
            e.printStackTrace();
            showAlert("Failed to open map view: " + e.getMessage());
        }
    }

    private Stage getCurrentStage() {
        if (mapViewButton != null && mapViewButton.getScene() != null) {
            return (Stage) mapViewButton.getScene().getWindow();
        }
        if (exitButton != null && exitButton.getScene() != null) {
            return (Stage) exitButton.getScene().getWindow();
        }
        if (mainTabPane != null && mainTabPane.getScene() != null) {
            return (Stage) mainTabPane.getScene().getWindow();
        }
        if (politicianTableView != null && politicianTableView.getScene() != null) {
            return (Stage) politicianTableView.getScene().getWindow();
        }
        return null;
    }

    private void handleExit() {
        try {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.err.println( e.getMessage());
        }
    }

    private void showAlert(String message) {
        try {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            });
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private boolean showConfirmation(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            Optional<ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get() == ButtonType.OK;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    private String getTrimmedText(TextField field) {
        return field != null ? field.getText().trim() : "";
    }

    private void setTextField(TextField field, String text) {
        if (field != null) field.setText(text);
    }

    private void clearTextField(TextField field) {
        if (field != null) field.clear();
    }

    /**
     * IMAGE
     */
    private Image loadPlaceholderImage(int width, int height) {
        try {
            String[] paths = {
                    "src/main/java/images/placeholder.jpg",
                    "images/placeholder.jpg",
                    "resources/images/placeholder.jpg"
            };

            for (String path : paths) {
                File file = new File(path);
                if (file.exists()) return new Image(file.toURI().toString(), width, height, true, true, true);
            }

            return createColorPlaceholder(width, height);
        } catch (Exception e) {
            System.err.println("ERROR loading placeholder image: " + e.getMessage());
            return createColorPlaceholder(width, height);
        }
    }

    private Image createColorPlaceholder(int width, int height) {
        WritableImage img = new WritableImage(width, height);
        PixelWriter pw = img.getPixelWriter();
        Color color = Color.LIGHTGRAY;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pw.setColor(x, y, color);
            }
        }
        return img;
    }

    private void loadImageAsync(ImageView imageView, String imageUrl, int width, int height) {
        try {
            String resolvedUrl = resolveImagePath(imageUrl);
            if (resolvedUrl == null) {
                imageView.setImage(loadPlaceholderImage(width, height));
                return;
            }

            Image image = new Image(resolvedUrl, width, height, true, true, true);
            image.errorProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) imageView.setImage(loadPlaceholderImage(width, height));
            });

            image.progressProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() == 1.0 && !image.isError()) {
                    imageView.setImage(image);
                }
            });

            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("ERROR loading image async: " + e.getMessage());
            imageView.setImage(loadPlaceholderImage(width, height));
        }
    }

    private String resolveImagePath(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return null;

        try {
            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://") || imageUrl.startsWith("file://")) {
                return imageUrl;
            }

            if (imageUrl.length() > 2 && imageUrl.charAt(1) == ':') {
                File file = new File(imageUrl);
                if (file.exists()) return file.toURI().toString();
            }

            File file = new File(imageUrl);
            if (file.exists()) return file.toURI().toString();

            String[] locations = {
                    "images/" + imageUrl,
                    "src/main/resources/images/" + imageUrl,
                    "src/main/java/images/" + imageUrl,
                    "resources/images/" + imageUrl
            };

            for (String location : locations) {
                File testFile = new File(location);
                if (testFile.exists()) return testFile.toURI().toString();
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }


    private String getOverallStatistics() {
        return electionSystemAPI != null ? electionSystemAPI.getSystemStatistics() : "System not initialized.";
    }

    private String getPoliticianStatistics() {
        return politicianManager != null ? politicianManager.getPoliticianStatistics() : "Politician Manager not initialized.";
    }

    private String getElectionStatistics() {
        return electionManager != null ? electionManager.getElectionStatistics() : "Election Manager not initialized.";
    }

    private String getCandidateStatistics() {
        return candidateManager != null ? candidateManager.getCandidateStatistics() : "Candidate Manager not initialized.";
    }

    private String getPartyStatistics() {
        if (electionSystemAPI == null) return "System not initialized.";

        StringBuilder stats = new StringBuilder("=== PARTY STATISTICS ===\n\n");
        HashList<String> parties = new HashList<>();
        HashList<Politician> allPoliticians = electionSystemAPI.getAllPoliticians();

        for (int i = 0; i < allPoliticians.size(); i++) {
            Politician p = allPoliticians.getByIndex(i);
            if (p != null && !parties.contains(p.getCurrentParty())) parties.addAtLast(p.getCurrentParty());
        }

        stats.append("Number of Parties: ").append(parties.size()).append("\n\nParties in System:\n");

        int index = 1;
        for (int i = 0; i < parties.size(); i++) {
            String party = parties.getByIndex(i);
            if (party != null) {
                int count = electionSystemAPI.searchPoliticiansByParty(party).size();
                stats.append(String.format("%2d. %-30s: %d politicians\n", index++, party, count));
            }
        }
        return stats.toString();
    }

    private void showStatsDialog(String title, String content) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);

            TextArea textArea = new TextArea(content);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefSize(600, 400);

            ScrollPane scrollPane = new ScrollPane(textArea);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            alert.getDialogPane().setContent(scrollPane);
            alert.getDialogPane().setPrefSize(650, 450);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private String getWinnerNameFromElection(Election election) {
        if (election == null) {
            System.out.println("Election is null in getWinnerNameFromElection");
            return "No Winner";
        }
        HashList<Candidate> candidates = null;

        if (election.getCandidates() != null) {
            candidates = election.getCandidates();
            System.out.println("  - Direct candidates: " + candidates.size());
        }


        if ((candidates == null || candidates.size() == 0) && electionManager != null) {
            candidates = electionManager.getCandidatesForElection(election);
            System.out.println("  - Manager candidates: " + (candidates != null ? candidates.size() : 0));
        }

        if (candidates == null || candidates.size() == 0) {
            return "No Winner";
        }

        StringBuilder winners = new StringBuilder();
        int winnerCount = 0;

        for (int i = 0; i < candidates.size(); i++) {
            Candidate c = candidates.getByIndex(i);
            if (c != null && c.isWinner()) {
                winnerCount++;
                String name = "Unknown";
                if (c.getPolitician() != null) {
                    name = c.getPolitician().getName();
                    System.out.println("  - Found winner: " + name + " (Votes: " + c.getVotesReceived() + ")");
                }

                if (winners.length() > 0) winners.append(", ");
                winners.append(name);
            }
        }

        String result = winners.length() > 0 ? winners.toString() : "No Winner";
        System.out.println("  - Final winner(s): " + result + " (" + winnerCount + " winner(s))");

        return result;
    }

    private void showTopPerformersByVotes() {
        if (candidateManager == null) return;
        showStatsDialog("Top Performers by Votes", candidateManager.getTopPerformersByVotes(10));
    }

    private void showTopPerformersByWins() {
        if (candidateManager == null) return;
        showStatsDialog("Top Performers by Wins", candidateManager.getTopPerformersByWins(10));
    }

    private void clearPoliticianForm() {
        clearTextField(politicianNameField);
        clearTextField(politicianPartyField);
        clearTextField(politicianCountyField);
        LocalDate defaultDob = LocalDate.now().minusYears(30);
        setTextField(politicianDOBField, defaultDob.toString());
        clearTextField(politicianImageUrlField);

        if (politicianTableView != null) politicianTableView.getSelectionModel().clearSelection();
        clearImagePreview();
    }

    private void clearElectionForm() {
        if (electionTypeComboBox != null) electionTypeComboBox.setValue(ElectionType.GENERAL);
        clearTextField(electionLocationField);
        clearTextField(electionSeatsField);
        setTextField(electionYearField, String.valueOf(LocalDate.now().getYear()));
        setTextField(electionMonthField, "1");
        setTextField(electionDayField, "1");
        if (electionTableView != null) electionTableView.getSelectionModel().clearSelection();
    }

    private void clearCandidateForm() {
        clearTextField(candidateVotesField);
        if (candidatePoliticianComboBox != null) candidatePoliticianComboBox.setValue("Select a politician...");
        if (candidateElectionComboBox != null) candidateElectionComboBox.setValue("Select an election...");
        if (candidatePartyComboBox != null) candidatePartyComboBox.setValue("Select a party...");
        if (candidateWinnerCheckBox != null) candidateWinnerCheckBox.setSelected(false);
        if (candidateTableView != null) candidateTableView.getSelectionModel().clearSelection();
    }

    private void clearAllForms() {
        clearPoliticianForm();
        clearElectionForm();
        clearCandidateForm();
    }

    private void clearImagePreview() {
        if (politicianImageView != null) {
            politicianImageView.setImage(loadPlaceholderImage(140, 130));
            politicianImageView.setVisible(true);
        }
        if (imagePreviewPane != null) imagePreviewPane.setVisible(false);
        if (imageNameLabel != null) imageNameLabel.setText("Default profile image");
        selectedPhotoFile = null;
        photoUrl = "";
    }

    public static class SortResultItem {
        private final String type, name, details, winnerName;

        public SortResultItem(String type, String name, String details, String winnerName) {
            this.type = type;
            this.name = name;
            this.details = details;
            this.winnerName = winnerName;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getDetails() {
            return details;
        }

        public String getWinnerName() {
            return winnerName;
        }
    }

}