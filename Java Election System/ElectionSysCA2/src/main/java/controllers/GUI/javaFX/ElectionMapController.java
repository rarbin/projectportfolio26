package controllers.GUI.javaFX;

import controllers.API.ElectionSystemAPI;
import controllers.managers.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import models.Politician;
import models.Candidate;
import models.Election;
import utils.CustomList.HashList;
import utils.Validators.InputVal;

import java.net.URL;
import java.util.ResourceBundle;

public class ElectionMapController implements Initializable {
    private final double MAX_SCALE = 4.0, ZOOM_FACTOR = 1.15;
    @FXML private Canvas mapCanvas;
    @FXML private ListView<Politician> politicianListView;
    @FXML private ListView<Election> electionListView;
    @FXML private ListView<Candidate> candidateListView;
    @FXML private TextArea detailsDisplay, resultsViewerDisplay;
    @FXML private Label statusLabel, totalLabel;
    @FXML private Button backButton;
    @FXML private Accordion navigationAccordion;
    @FXML private TitledPane politicianPane, electionsPane, candidatesPane;

    private ElectionSystemAPI electionSystemAPI;
    private GraphicsContext gc;
    private TitledPane activePane = null;

    private Politician selectedPolitician = null;
    private Election selectedElection = null;
    private Candidate selectedCandidate = null;

    private double scale = 1.0, offsetX = 0, offsetY = 0;
    private double contentWidth = 0, contentHeight = 0, viewportWidth = 0, viewportHeight = 0;
    private double dragStartX, dragStartY;
    private boolean isDragging = false;

    /**
     * INITIALIZERS
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("ElectionMapController.initialize() called");

        gc = mapCanvas.getGraphicsContext2D();
        viewportWidth = mapCanvas.getWidth();
        viewportHeight = mapCanvas.getHeight();
        setupEventHandlers();
        setupListViewRenderers();
        setupSelectionListeners();
        setupTabListeners();
        updateBackButton();

        drawPlaceholder();

        navigationAccordion.setExpandedPane(politicianPane);
        activePane = politicianPane;
    }

    private void drawPlaceholder() {
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(20));
        gc.fillText("Loading election data...",
                mapCanvas.getWidth() / 2 - 100,
                mapCanvas.getHeight() / 2);
    }

    public void initializeWithSystem(ElectionSystemAPI api) {
        this.electionSystemAPI = api;
        loadInitialData();
        updateTotalLabel();
        updateResultsViewer();
        statusLabel.setText("System initialized");
    }

    /**
     * SETUP TAB LISTENERS
     */
    private void setupTabListeners() {
        politicianPane.expandedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && electionSystemAPI != null) {
                activePane = politicianPane;
                loadPoliticiansView();
            }
        });

        electionsPane.expandedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && electionSystemAPI != null) {
                activePane = electionsPane;
                loadElectionsView();
            }
        });

        candidatesPane.expandedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && electionSystemAPI != null) {
                activePane = candidatesPane;
                loadCandidatesView();
            }
        });
    }

    /**
     * LOAD INITIAL DATA
     */
    private void loadInitialData() {
        if (electionSystemAPI == null) {
            System.err.println("ERROR");
            statusLabel.setText("ERROR: System not initialized");
            return;
        }

        clearSelectItems();

        ensureDataConsistency();

        if (activePane == politicianPane) {
            loadPoliticiansView();
        } else if (activePane == electionsPane) {
            loadElectionsView();
        } else if (activePane == candidatesPane) {
            loadCandidatesView();
        }

        updateTotalLabel();
        updateResultsViewer();
        statusLabel.setText("Data loaded");
        System.out.println("Initial data loaded successfully");
    }

    /**
     * LOAD VIEWS FOR EACH TAB
     */
    private void loadPoliticiansView() {
        clearSelectItems();
        politicianListView.getItems().clear();
        electionListView.getItems().clear();
        candidateListView.getItems().clear();

        if (electionSystemAPI != null) {
            HashList<Politician> politicians = electionSystemAPI.getAllPoliticians();
            System.out.println("Loading " + politicians.size() + " politicians");
            for (int i = 0; i < politicians.size(); i++) {
                Politician politician = politicians.getByIndex(i);
                if (politician != null) {
                    politicianListView.getItems().add(politician);
                }
            }
        }

        ensureFullContentVisible();
        statusLabel.setText("Viewing all politicians");
    }

    private void loadElectionsView() {

        clearSelectItems();
        politicianListView.getItems().clear();
        electionListView.getItems().clear();
        candidateListView.getItems().clear();

        if (electionSystemAPI != null) {
            HashList<Election> elections = electionSystemAPI.getAllElections();
            System.out.println("Loading " + elections.size() + " elections");
            for (int i = 0; i < elections.size(); i++) {
                Election election = elections.getByIndex(i);
                if (election != null) {
                    electionListView.getItems().add(election);
                }
            }
        }

        ensureFullContentVisible();
        statusLabel.setText("Viewing all elections");
    }

    private void loadCandidatesView() {
        clearSelectItems();
        politicianListView.getItems().clear();
        electionListView.getItems().clear();
        candidateListView.getItems().clear();

        if (electionSystemAPI != null) {
            HashList<Candidate> candidates = electionSystemAPI.getAllCandidates();
            System.out.println("Loading " + candidates.size() + " candidates");
            for (int i = 0; i < candidates.size(); i++) {
                Candidate candidate = candidates.getByIndex(i);
                if (candidate != null) {
                    candidateListView.getItems().add(candidate);
                }
            }
        }

        ensureFullContentVisible();
        statusLabel.setText("Viewing all candidates");
    }

    /**
     * SETUP - mouse/scroll event handler for map canvas
     */
    private void setupEventHandlers() {
        mapCanvas.setOnMousePressed(e -> {
            dragStartX = e.getX();
            dragStartY = e.getY();
            isDragging = false;
        });

        mapCanvas.setOnMouseDragged(e -> {
            if (!isDragging) isDragging = true;
            offsetX += e.getX() - dragStartX;
            offsetY += e.getY() - dragStartY;
            dragStartX = e.getX();
            dragStartY = e.getY();
            offsetBoundary();
            drawMap();
        });

        mapCanvas.setOnMouseReleased(e -> {
            if (!isDragging) handleCanvasMouseClick(e.getX(), e.getY());
            isDragging = false;
        });
    }

    /**
     * SETUP LISTVIEW - custom display
     */
    private void setupListViewRenderers() {
        setupCell(politicianListView, politician ->
                politician.getName() + " (" + politician.getCurrentParty() + ")");
        setupCell(electionListView, election ->
                election.getType() + " - " + election.getLocation() + " (" + election.getYear() + ")");
        setupCell(candidateListView, candidate ->
                candidate.getPolitician().getName() + " - " +
                        candidate.getPartyAffiliation() + " (" + candidate.getVotesReceived() + " votes)");
    }

    private <T> void setupCell(ListView<T> list, HashList.StringMapper<T> mapper) {
        list.setCellFactory(lv -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : mapper.mapToString(item));
            }
        });
    }

    /**
     * SETUP SELECT LISTENER
     */
    private void setupSelectionListeners() {
        politicianListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> onPoliticianSelected(newVal));
        electionListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> onElectionSelected(newVal));
        candidateListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> onCandidateSelected(newVal));
    }

    /**
     * SELECTION HANDLERS
     */
    private void onPoliticianSelected(Politician politician) {
        selectedPolitician = politician;
        selectedElection = null;
        selectedCandidate = null;
        updateDetails();
        updateResultsViewer();
        statusLabel.setText(politician != null ?
                "Selected politician: " + politician.getName() :
                "No politician selected");
        drawMap();
    }

    private void onElectionSelected(Election election) {
        selectedElection = election;
        selectedCandidate = null;
        updateDetails();
        updateResultsViewer();
        statusLabel.setText(election != null ?
                "Selected election: " + election.getType() + " " + election.getLocation() + " (" + election.getYear() + ")" :
                "No election selected");
        drawMap();
    }

    private void onCandidateSelected(Candidate candidate) {
        selectedCandidate = candidate;
        updateDetails();
        updateResultsViewer();
        statusLabel.setText(candidate != null ?
                "Selected candidate: " + candidate.getPolitician().getName() :
                "No candidate selected");
        drawMap();
    }

    /**
     * CONTENT SIZE CALCULATION
     */
    private void calculateContentSize() {
        if (activePane == politicianPane) {
            calculatePoliticiansSize();
        } else if (activePane == electionsPane) {
            calculateElectionsSize();
        } else if (activePane == candidatesPane) {
            calculateCandidatesSize();
        }
        contentWidth = Math.max(contentWidth + 100, viewportWidth);
        contentHeight = Math.max(contentHeight + 100, viewportHeight);
    }

    private void calculatePoliticiansSize() {
        if (electionSystemAPI == null) {
            contentWidth = 400;
            contentHeight = 300;
            return;
        }

        HashList<Politician> politicians = electionSystemAPI.getAllPoliticians();
        int politicianCount = politicians.size();

        if (politicianCount == 0) {
            contentWidth = 400;
            contentHeight = 300;
            return;
        }

        int itemsPerRow = Math.max(2, (int) Math.ceil(Math.sqrt(politicianCount)));
        int rows = (int) Math.ceil(politicianCount / (double) itemsPerRow);

        double itemWidth = 350;
        double itemHeight = 250;
        double horizontalSpacing = 30;
        double verticalSpacing = 30;
        double horizontalPadding = 50;
        double verticalPadding = 50;

        contentWidth = Math.max(
                viewportWidth,
                itemsPerRow * itemWidth + (itemsPerRow - 1) * horizontalSpacing + 2 * horizontalPadding
        );
        contentHeight = Math.max(
                viewportHeight,
                rows * itemHeight + (rows - 1) * verticalSpacing + 2 * verticalPadding
        );
    }

    private void calculateElectionsSize() {
        if (electionSystemAPI == null) {
            contentWidth = 400;
            contentHeight = 300;
            return;
        }

        HashList<Election> elections = electionSystemAPI.getAllElections();
        int electionCount = elections.size();

        if (electionCount == 0) {
            contentWidth = 400;
            contentHeight = 300;
            return;
        }

        int itemsPerRow = Math.max(2, (int) Math.ceil(Math.sqrt(electionCount)));
        int rows = (int) Math.ceil(electionCount / (double) itemsPerRow);

        double itemWidth = 350;
        double itemHeight = 250;
        double horizontalSpacing = 30;
        double verticalSpacing = 30;
        double horizontalPadding = 50;
        double verticalPadding = 50;

        contentWidth = Math.max(
                viewportWidth,
                itemsPerRow * itemWidth + (itemsPerRow - 1) * horizontalSpacing + 2 * horizontalPadding
        );
        contentHeight = Math.max(
                viewportHeight,
                rows * itemHeight + (rows - 1) * verticalSpacing + 2 * verticalPadding
        );
    }

    private void calculateCandidatesSize() {
        if (electionSystemAPI == null) {
            contentWidth = 400;
            contentHeight = 300;
            return;
        }

        HashList<Candidate> candidates = electionSystemAPI.getAllCandidates();
        int candidateCount = candidates.size();

        if (candidateCount == 0) {
            contentWidth = 400;
            contentHeight = 300;
            return;
        }

        int itemsPerRow = Math.max(2, (int) Math.ceil(Math.sqrt(candidateCount)));
        int rows = (int) Math.ceil(candidateCount / (double) itemsPerRow);

        double itemWidth = 350;
        double itemHeight = 250;
        double horizontalSpacing = 30;
        double verticalSpacing = 30;
        double horizontalPadding = 50;
        double verticalPadding = 50;

        contentWidth = Math.max(
                viewportWidth,
                itemsPerRow * itemWidth + (itemsPerRow - 1) * horizontalSpacing + 2 * horizontalPadding
        );
        contentHeight = Math.max(
                viewportHeight,
                rows * itemHeight + (rows - 1) * verticalSpacing + 2 * verticalPadding
        );
    }

    /**
     * ADJUST CANVAS SIZE to fit content
     */
    private void adjustCanvasSize() {
        double requiredWidth = Math.max(contentWidth, viewportWidth);
        double requiredHeight = Math.max(contentHeight, viewportHeight);

        if (mapCanvas.getWidth() < requiredWidth || mapCanvas.getHeight() < requiredHeight) {
            mapCanvas.setWidth(requiredWidth);
            mapCanvas.setHeight(requiredHeight);
        }

        viewportWidth = mapCanvas.getWidth();
        viewportHeight = mapCanvas.getHeight();
    }

    /**
     * NAVIGATION - ZOOM/DRAG
     */
    private void offsetBoundary() {
        double scaledWidth = contentWidth * scale;
        double scaledHeight = contentHeight * scale;
        double maxX = Math.max(0, (scaledWidth - viewportWidth) / 2);
        double maxY = Math.max(0, (scaledHeight - viewportHeight) / 2);

        offsetX = Math.max(-maxX, Math.min(maxX, offsetX));
        offsetY = Math.max(-maxY, Math.min(maxY, offsetY));
    }

    private void centerView() {
        calculateContentSize();
        adjustCanvasSize();

        offsetX = (viewportWidth - contentWidth * scale) / 2;
        offsetY = (viewportHeight - contentHeight * scale) / 2;
        offsetBoundary();
    }

    private void ensureFullContentVisible() {
        calculateContentSize();
        adjustCanvasSize();

        if (activePane == politicianPane) {
            scale = 0.8;
        } else if (activePane == electionsPane) {
            scale = 0.8;
        } else if (activePane == candidatesPane) {
            scale = 0.8;
        }

        double scaledWidth = contentWidth * scale;
        double scaledHeight = contentHeight * scale;

        if (scaledWidth > viewportWidth || scaledHeight > viewportHeight) {
            double widthRatio = viewportWidth / scaledWidth;
            double heightRatio = viewportHeight / scaledHeight;
            double minRatio = Math.min(widthRatio, heightRatio);
            scale *= minRatio * 0.95;
        }

        centerView();
        drawMap();
    }

    @FXML
    private void handleZoomIn() {
        zoom(ZOOM_FACTOR);
    }

    @FXML
    private void handleZoomOut() {
        zoom(1.0 / ZOOM_FACTOR);
    }

    private void zoom(double factor) {
        double oldScale = scale;
        scale = Math.min(Math.max(scale * factor, 0.1), MAX_SCALE);
        double zoomRatio = scale / oldScale;

        offsetX = viewportWidth / 2 - zoomRatio * (viewportWidth / 2 - offsetX);
        offsetY = viewportHeight / 2 - zoomRatio * (viewportHeight / 2 - offsetY);
        offsetBoundary();
        drawMap();
    }

    @FXML
    private void handleResetView() {
        ensureFullContentVisible();
    }

    /**
     * CLICK HANDLERS
     */
    private void handleCanvasMouseClick(double x, double y) {
        double canvasX = (x - offsetX) / scale;
        double canvasY = (y - offsetY) / scale;

        if (activePane == politicianPane) {
            handlePoliticianClick(canvasX, canvasY);
        } else if (activePane == electionsPane) {
            handleElectionClick(canvasX, canvasY);
        } else if (activePane == candidatesPane) {
            handleCandidateClick(canvasX, canvasY);
        }
    }

    private void handlePoliticianClick(double canvasX, double canvasY) {
        if (electionSystemAPI == null) return;

        HashList<Politician> politicians = electionSystemAPI.getAllPoliticians();
        int politicianCount = politicians.size();
        if (politicianCount == 0) return;

        int itemsPerRow = Math.max(2, (int) Math.ceil(Math.sqrt(politicianCount)));
        int rows = (int) Math.ceil(politicianCount / (double) itemsPerRow);

        double itemWidth = 350;
        double itemHeight = 250;
        double horizontalSpacing = 30;
        double verticalSpacing = 30;
        double horizontalPadding = 50;
        double verticalPadding = 50;

        double totalGridWidth = itemsPerRow * itemWidth + (itemsPerRow - 1) * horizontalSpacing;
        double totalGridHeight = rows * itemHeight + (rows - 1) * verticalSpacing;

        double startX = (contentWidth - totalGridWidth) / 2;
        double startY = verticalPadding;

        int drawnItems = 0;
        for (int i = 0; i < politicianCount; i++) {
            Politician politician = politicians.getByIndex(i);
            if (politician == null) continue;

            int row = drawnItems / itemsPerRow;
            int col = drawnItems % itemsPerRow;
            double rectX = startX + col * (itemWidth + horizontalSpacing);
            double rectY = startY + row * (itemHeight + verticalSpacing);

            if (canvasX >= rectX && canvasX <= rectX + itemWidth &&
                    canvasY >= rectY && canvasY <= rectY + itemHeight) {
                politicianListView.getSelectionModel().select(politician);
                onPoliticianSelected(politician);
                return;
            }
            drawnItems++;
        }
    }

    private void handleElectionClick(double canvasX, double canvasY) {
        if (electionSystemAPI == null) return;

        HashList<Election> elections = electionSystemAPI.getAllElections();
        int electionCount = elections.size();
        if (electionCount == 0) return;

        int itemsPerRow = Math.max(2, (int) Math.ceil(Math.sqrt(electionCount)));
        int rows = (int) Math.ceil(electionCount / (double) itemsPerRow);

        double itemWidth = 350;
        double itemHeight = 250;
        double horizontalSpacing = 30;
        double verticalSpacing = 30;
        double horizontalPadding = 50;
        double verticalPadding = 50;

        double totalGridWidth = itemsPerRow * itemWidth + (itemsPerRow - 1) * horizontalSpacing;
        double totalGridHeight = rows * itemHeight + (rows - 1) * verticalSpacing;

        double startX = (contentWidth - totalGridWidth) / 2;
        double startY = verticalPadding;

        int drawnItems = 0;
        for (int i = 0; i < electionCount; i++) {
            Election election = elections.getByIndex(i);
            if (election == null) continue;

            int row = drawnItems / itemsPerRow;
            int col = drawnItems % itemsPerRow;
            double rectX = startX + col * (itemWidth + horizontalSpacing);
            double rectY = startY + row * (itemHeight + verticalSpacing);

            if (canvasX >= rectX && canvasX <= rectX + itemWidth &&
                    canvasY >= rectY && canvasY <= rectY + itemHeight) {
                electionListView.getSelectionModel().select(election);
                onElectionSelected(election);
                return;
            }
            drawnItems++;
        }
    }

    private void handleCandidateClick(double canvasX, double canvasY) {
        if (electionSystemAPI == null) return;

        HashList<Candidate> candidates = electionSystemAPI.getAllCandidates();
        int candidateCount = candidates.size();
        if (candidateCount == 0) return;

        int itemsPerRow = Math.max(2, (int) Math.ceil(Math.sqrt(candidateCount)));
        int rows = (int) Math.ceil(candidateCount / (double) itemsPerRow);

        double itemWidth = 350;
        double itemHeight = 250;
        double horizontalSpacing = 30;
        double verticalSpacing = 30;
        double horizontalPadding = 50;
        double verticalPadding = 50;

        double totalGridWidth = itemsPerRow * itemWidth + (itemsPerRow - 1) * horizontalSpacing;
        double totalGridHeight = rows * itemHeight + (rows - 1) * verticalSpacing;

        double startX = (contentWidth - totalGridWidth) / 2;
        double startY = verticalPadding;

        int drawnItems = 0;
        for (int i = 0; i < candidateCount; i++) {
            Candidate candidate = candidates.getByIndex(i);
            if (candidate == null) continue;

            int row = drawnItems / itemsPerRow;
            int col = drawnItems % itemsPerRow;
            double rectX = startX + col * (itemWidth + horizontalSpacing);
            double rectY = startY + row * (itemHeight + verticalSpacing);

            if (canvasX >= rectX && canvasX <= rectX + itemWidth &&
                    canvasY >= rectY && canvasY <= rectY + itemHeight) {
                candidateListView.getSelectionModel().select(candidate);
                onCandidateSelected(candidate);
                return;
            }
            drawnItems++;
        }
    }

    /**
     * BACK BUTTON - now resets to show all items in current tab
     */
    @FXML
    private void handleBack() {
        clearSelectItems();

        if (activePane == politicianPane) {
            loadPoliticiansView();
        } else if (activePane == electionsPane) {
            loadElectionsView();
        } else if (activePane == candidatesPane) {
            loadCandidatesView();
        }

        statusLabel.setText("Viewing all " + activePane.getText().toLowerCase());
    }

    /**
     * DRAW HELPERS
     */
    private void drawBox(double x, double y, double w, double h, Color fill, Color border, int borderWidth) {
        gc.setFill(fill);
        gc.setStroke(border);
        gc.setLineWidth(borderWidth);
        gc.fillRect(x, y, w, h);
        gc.strokeRect(x, y, w, h);
    }

    /**
     * DRAW - render map based on active tab
     */
    private void drawMap() {
        adjustCanvasSize();

        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        gc.save();
        gc.translate(offsetX, offsetY);
        gc.scale(scale, scale);
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, contentWidth, contentHeight);

        if (activePane == politicianPane) {
            drawPoliticians();
        } else if (activePane == electionsPane) {
            drawElections();
        } else if (activePane == candidatesPane) {
            drawCandidates();
        }

        gc.restore();
    }

    /**
     * DRAW POLITICIANS - grid layout
     */
    private void drawPoliticians() {
        if (electionSystemAPI == null) return;

        HashList<Politician> politicians = electionSystemAPI.getAllPoliticians();
        int politicianCount = politicians.size();
        if (politicianCount == 0) return;

        int itemsPerRow = Math.max(2, (int) Math.ceil(Math.sqrt(politicianCount)));
        int rows = (int) Math.ceil(politicianCount / (double) itemsPerRow);

        double itemWidth = 350;
        double itemHeight = 250;
        double horizontalSpacing = 30;
        double verticalSpacing = 30;
        double horizontalPadding = 50;
        double verticalPadding = 50;

        double totalGridWidth = itemsPerRow * itemWidth + (itemsPerRow - 1) * horizontalSpacing;
        double totalGridHeight = rows * itemHeight + (rows - 1) * verticalSpacing;

        double startX = (contentWidth - totalGridWidth) / 2;
        double startY = verticalPadding;

        int drawnItems = 0;
        for (int i = 0; i < politicianCount; i++) {
            Politician politician = politicians.getByIndex(i);
            if (politician == null) continue;

            int row = drawnItems / itemsPerRow;
            int col = drawnItems % itemsPerRow;
            double x = startX + col * (itemWidth + horizontalSpacing);
            double y = startY + row * (itemHeight + verticalSpacing);

            Color fill = getPartyColor(politician.getCurrentParty());
            Color border = politician == selectedPolitician ? Color.RED : Color.BLACK;
            int borderWidth = politician == selectedPolitician ? 4 : 2;
            drawBox(x, y, itemWidth, itemHeight, fill, border, borderWidth);

            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(18));
            gc.fillText(politician.getName(), x + 10, y + 30);

            gc.setFont(Font.font(14));
            gc.fillText("Party: " + politician.getCurrentParty(), x + 10, y + 60);
            gc.fillText("County: " + politician.getHomeCounty(), x + 10, y + 90);
            gc.fillText("Elections: " + politician.getElectionsParticipated().size(), x + 10, y + 120);
            int wins = 0;
            HashList<Candidate> participations = politician.getElectionsParticipated();
            for (int j = 0; j < participations.size(); j++) {
                Candidate c = participations.getByIndex(j);
                if (c != null && c.isWinner()) wins++;
            }
            gc.fillText("Wins: " + wins, x + 10, y + 150);

            drawnItems++;
        }
    }

    /**
     * DRAW ELECTIONS - grid layout
     */
    private void drawElections() {
        if (electionSystemAPI == null) return;

        HashList<Election> elections = electionSystemAPI.getAllElections();
        int electionCount = elections.size();
        if (electionCount == 0) return;

        int itemsPerRow = Math.max(2, (int) Math.ceil(Math.sqrt(electionCount)));
        int rows = (int) Math.ceil(electionCount / (double) itemsPerRow);

        double itemWidth = 350;
        double itemHeight = 250;
        double horizontalSpacing = 30;
        double verticalSpacing = 30;
        double horizontalPadding = 50;
        double verticalPadding = 50;

        double totalGridWidth = itemsPerRow * itemWidth + (itemsPerRow - 1) * horizontalSpacing;
        double totalGridHeight = rows * itemHeight + (rows - 1) * verticalSpacing;

        double startX = (contentWidth - totalGridWidth) / 2;
        double startY = verticalPadding;

        int drawnItems = 0;
        for (int i = 0; i < electionCount; i++) {
            Election election = elections.getByIndex(i);
            if (election == null) continue;

            int row = drawnItems / itemsPerRow;
            int col = drawnItems % itemsPerRow;
            double x = startX + col * (itemWidth + horizontalSpacing);
            double y = startY + row * (itemHeight + verticalSpacing);

            Color fill = getElectionTypeColor(election.getType());
            Color border = election == selectedElection ? Color.RED : Color.BLACK;
            int borderWidth = election == selectedElection ? 4 : 2;
            drawBox(x, y, itemWidth, itemHeight, fill, border, borderWidth);

            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(18));
            gc.fillText(election.getType() + " " + election.getYear(), x + 10, y + 30);

            gc.setFont(Font.font(14));
            gc.fillText("Location: " + election.getLocation(), x + 10, y + 60);
            gc.fillText("Date: " + InputVal.formatDate(election.getDate()), x + 10, y + 90);
            gc.fillText("Seats: " + election.getSeatsAvailable(), x + 10, y + 120);
            gc.fillText("Candidates: " + election.getCandidates().size(), x + 10, y + 150);

            // Find winner(s)
            StringBuilder winners = new StringBuilder();
            HashList<Candidate> candidates = election.getCandidates();
            for (int j = 0; j < candidates.size(); j++) {
                Candidate c = candidates.getByIndex(j);
                if (c != null && c.isWinner()) {
                    if (winners.length() > 0) winners.append(", ");
                    winners.append(c.getPolitician().getName());
                }
            }

            if (winners.length() > 0) {
                gc.fillText("Winner(s): " + winners.toString(), x + 10, y + 180);
            }

            drawnItems++;
        }
    }

    /**
     * DRAW CANDIDATES - grid layout
     */
    private void drawCandidates() {
        if (electionSystemAPI == null) return;

        HashList<Candidate> candidates = electionSystemAPI.getAllCandidates();
        int candidateCount = candidates.size();
        if (candidateCount == 0) return;

        int itemsPerRow = Math.max(2, (int) Math.ceil(Math.sqrt(candidateCount)));
        int rows = (int) Math.ceil(candidateCount / (double) itemsPerRow);

        double itemWidth = 350;
        double itemHeight = 250;
        double horizontalSpacing = 30;
        double verticalSpacing = 30;
        double horizontalPadding = 50;
        double verticalPadding = 50;

        double totalGridWidth = itemsPerRow * itemWidth + (itemsPerRow - 1) * horizontalSpacing;
        double totalGridHeight = rows * itemHeight + (rows - 1) * verticalSpacing;

        double startX = (contentWidth - totalGridWidth) / 2;
        double startY = verticalPadding;

        int drawnItems = 0;
        for (int i = 0; i < candidateCount; i++) {
            Candidate candidate = candidates.getByIndex(i);
            if (candidate == null) continue;

            int row = drawnItems / itemsPerRow;
            int col = drawnItems % itemsPerRow;
            double x = startX + col * (itemWidth + horizontalSpacing);
            double y = startY + row * (itemHeight + verticalSpacing);

            Color fill = candidate.isWinner() ? Color.LIGHTGREEN : Color.LIGHTBLUE;
            Color border = candidate == selectedCandidate ? Color.RED : Color.BLACK;
            int borderWidth = candidate == selectedCandidate ? 4 : 2;
            drawBox(x, y, itemWidth, itemHeight, fill, border, borderWidth);

            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(16));
            gc.fillText(candidate.getPolitician().getName(), x + 10, y + 30);

            gc.setFont(Font.font(14));
            gc.fillText("Party: " + candidate.getPartyAffiliation(), x + 10, y + 60);
            gc.fillText("Votes: " + String.format("%,d", candidate.getVotesReceived()), x + 10, y + 90);

            if (candidate.getElection() != null) {
                gc.fillText("Election: " + candidate.getElection().getType() +
                        " " + candidate.getElection().getYear(), x + 10, y + 120);
                gc.fillText("Location: " + candidate.getElection().getLocation(), x + 10, y + 150);
            }

            if (candidate.isWinner()) {
                gc.setFill(Color.GREEN);
                gc.setFont(Font.font(16));
                gc.fillText("WINNER", x + 10, y + 180);
            }

            drawnItems++;
        }
    }

    /**
     * RESULTS VIEWER
     */
    private void updateResultsViewer() {
        if (resultsViewerDisplay == null) return;

        StringBuilder resultsInfo = new StringBuilder("ELECTION RESULTS VIEWER\n\n");

        if (selectedCandidate != null) {
            resultsInfo.append(getSelectedCandidateInfo());
        } else if (selectedElection != null) {
            resultsInfo.append(getElectionResults());
        } else if (selectedPolitician != null) {
            resultsInfo.append(getPoliticianResults());
        } else {
            resultsInfo.append(getSystemOverview());
        }

        resultsViewerDisplay.setText(resultsInfo.toString());
    }

    private String getSelectedCandidateInfo() {
        StringBuilder info = new StringBuilder("SELECTED CANDIDATE:\n")
                .append("Name: ").append(selectedCandidate.getPolitician().getName()).append("\n")
                .append("Party: ").append(selectedCandidate.getPartyAffiliation()).append("\n")
                .append("Votes: ").append(String.format("%,d", selectedCandidate.getVotesReceived())).append("\n")
                .append("Status: ").append(selectedCandidate.isWinner() ? "WINNER" : "NOT WINNER").append("\n\n");

        if (selectedElection != null) {
            info.append("ELECTION DETAILS:\n")
                    .append("Type: ").append(selectedElection.getType()).append("\n")
                    .append("Location: ").append(selectedElection.getLocation()).append("\n")
                    .append("Year: ").append(selectedElection.getYear()).append("\n")
                    .append("Seats: ").append(selectedElection.getSeatsAvailable()).append("\n\n");

            info.append("OTHER CANDIDATES IN THIS ELECTION:\n");
            HashList<Candidate> candidates = selectedElection.getCandidates();
            for (int i = 0; i < candidates.size(); i++) {
                Candidate candidate = candidates.getByIndex(i);
                if (candidate != null && candidate != selectedCandidate) {
                    info.append("• ").append(candidate.getPolitician().getName())
                            .append(" (").append(candidate.getPartyAffiliation()).append(") - ")
                            .append(String.format("%,d", candidate.getVotesReceived()))
                            .append(" votes").append(candidate.isWinner() ? " [WINNER]" : "").append("\n");
                }
            }
        }
        return info.toString();
    }

    private String getElectionResults() {
        ElectionManager manager = new ElectionManager(electionSystemAPI);
        return manager.getElectionResults(selectedElection);
    }

    private String getPoliticianResults() {
        ElectionManager electionManager = new ElectionManager(electionSystemAPI);
        PoliticianManager manager = new PoliticianManager(electionSystemAPI, electionManager);
        return manager.getPoliticianElectionHistory(selectedPolitician.getName());
    }

    private String getSystemOverview() {
        CandidateManager manager = new CandidateManager(electionSystemAPI);
        return manager.getCandidateStatistics();
    }

    /**
     * DETAIL PANE
     */
    private void updateDetails() {
        StringBuilder details = new StringBuilder();
        if (selectedPolitician != null) {
            details.append("POLITICIAN\n")
                    .append("Name: ").append(selectedPolitician.getName()).append("\n")
                    .append("Party: ").append(selectedPolitician.getCurrentParty()).append("\n")
                    .append("County: ").append(selectedPolitician.getHomeCounty()).append("\n")
                    .append("DOB: ").append(InputVal.formatDate(selectedPolitician.getDateOfBirth())).append("\n")
                    .append("Elections: ").append(selectedPolitician.getElectionsParticipated().size()).append("\n\n");
        }
        if (selectedElection != null) {
            details.append("ELECTION\n")
                    .append("Type: ").append(selectedElection.getType()).append("\n")
                    .append("Location: ").append(selectedElection.getLocation()).append("\n")
                    .append("Date: ").append(InputVal.formatDate(selectedElection.getDate())).append("\n")
                    .append("Year: ").append(selectedElection.getYear()).append("\n")
                    .append("Seats: ").append(selectedElection.getSeatsAvailable()).append("\n")
                    .append("Candidates: ").append(selectedElection.getCandidates().size()).append("\n\n");
        }
        if (selectedCandidate != null) {
            details.append("CANDIDATE\n")
                    .append("Politician: ").append(selectedCandidate.getPolitician().getName()).append("\n")
                    .append("Party: ").append(selectedCandidate.getPartyAffiliation()).append("\n")
                    .append("Votes: ").append(String.format("%,d", selectedCandidate.getVotesReceived())).append("\n")
                    .append("Status: ").append(selectedCandidate.isWinner() ? "WINNER" : "NOT WINNER").append("\n");
        }
        detailsDisplay.setText(details.toString());
    }

    /**
     * UPDATE - back button, total label
     */
    private void updateBackButton() {
        backButton.setDisable(selectedPolitician == null && selectedElection == null && selectedCandidate == null);
    }

    private void updateTotalLabel() {
        if (electionSystemAPI == null) return;
        totalLabel.setText("Total: " + electionSystemAPI.getPoliticianCount() +
                " politicians, " + electionSystemAPI.getElectionCount() +
                " elections, " + electionSystemAPI.getCandidateCount() + " candidates");
    }

    /**
     * CLEAR/REFRESH HELPERS
     */
    private void clearSelectItems() {
        selectedPolitician = null;
        selectedElection = null;
        selectedCandidate = null;
        politicianListView.getSelectionModel().clearSelection();
        electionListView.getSelectionModel().clearSelection();
        candidateListView.getSelectionModel().clearSelection();
    }

    /**
     * COLOR UTILITIES
     */
    private Color getPartyColor(String party) {
        if (party == null) return Color.LIGHTGRAY;
        int hash = party.toLowerCase().hashCode();
        double hue = Math.abs(hash % 360);
        return Color.hsb(hue, 0.3, 0.9);
    }

    private Color getElectionTypeColor(models.ElectionType type) {
        if (type == null) return Color.LIGHTGRAY;
        switch (type) {
            case GENERAL: return Color.LIGHTBLUE;
            case LOCAL: return Color.LIGHTGREEN;
            case EUROPEAN: return Color.LIGHTYELLOW;
            case PRESIDENTIAL: return Color.LIGHTPINK;
            default: return Color.LIGHTGRAY;
        }
    }

    /**
     * DATA CONSISTENCY
     */
    private void ensureDataConsistency() {
        if (electionSystemAPI == null) return;

        HashList<Politician> politicians = electionSystemAPI.getAllPoliticians();
        HashList<Election> elections = electionSystemAPI.getAllElections();
        HashList<Candidate> candidates = electionSystemAPI.getAllCandidates();


        int linkedCandidates = 0;
        for (int i = 0; i < candidates.size(); i++) {
            Candidate c = candidates.getByIndex(i);
            if (c != null && c.getPolitician() != null && c.getElection() != null) {
                for (int j = 0; j < politicians.size(); j++) {
                    Politician p = politicians.getByIndex(j);
                    if (p != null && p.getId() == c.getPolitician().getId()) {

                        if (!p.getElectionsParticipated().contains(c)) {
                            p.addElectionParticipation(c);
                            linkedCandidates++;
                        }
                        break;
                    }
                }

                for (int j = 0; j < elections.size(); j++) {
                    Election e = elections.getByIndex(j);
                    if (e != null && e.getId() == c.getElection().getId()) {
                        if (!e.getCandidates().contains(c)) {
                            e.addCandidate(c);
                        }
                        break;
                    }
                }
            }
        }

    }
}