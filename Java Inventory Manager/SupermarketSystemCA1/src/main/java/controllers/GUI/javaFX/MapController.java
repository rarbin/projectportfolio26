package controllers.GUI.javaFX;

import controllers.API.SupermarketAPI;
import controllers.managers.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import models.Aisle;
import models.FloorArea;
import models.GoodItem;
import models.Shelf;
import utils.CustomList.FunkierList;
import utils.Dimensions;
import utils.Validators.InputVal;

import java.net.URL;
import java.util.ResourceBundle;

public class MapController implements Initializable {
    private final double MAX_SCALE = 4.0, ZOOM_FACTOR = 1.15;
    private final double PIXELS_PER_METER = 2.0; //50px to 1m
    @FXML private Canvas mapCanvas;
    @FXML private ListView<FloorArea> floorAreaListView;
    @FXML private ListView<Aisle> aisleListView;
    @FXML private ListView<Shelf> shelfListView;
    @FXML private ListView<GoodItem> itemListView;
    @FXML private TextArea detailsDisplay, stockViewerDisplay;
    @FXML private Label statusLabel, totalLabel;
    @FXML private Button backButton;
    @FXML private Accordion navigationAccordion;
    @FXML private TitledPane floorAreaPane, aislesPane, shelvesPane, itemsPane;

    private SupermarketAPI supermarketAPI;
    private StockViewer stockViewer;
    private GraphicsContext gc;
    private final FunkierList<ViewMode> modeHistory = new FunkierList<>();
    private ViewMode currentMode = ViewMode.FLOORS;


    private FloorArea selectedFloorArea = null;
    private Aisle selectedAisle = null;
    private Shelf selectedShelf = null;
    private GoodItem selectedItem = null;


    private double scale = 0.8, offsetX = 0, offsetY = 0;
    private double contentWidth = 0, contentHeight = 0, viewportWidth = 0, viewportHeight = 0;
    private double dragStartX, dragStartY;
    private boolean isDragging = false;

    /**
     * INITIALIZERS
     */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = mapCanvas.getGraphicsContext2D();
        viewportWidth = mapCanvas.getWidth();
        viewportHeight = mapCanvas.getHeight();
        setupEventHandlers();
        setupListViewRenderers();
        setupSelectionListeners();
        updateBackButton();
        calculateContentSize();
        navigationAccordion.setExpandedPane(floorAreaPane);
        drawMap();
    }

    public void initializeWithSystem(SupermarketAPI api) {
        this.supermarketAPI = api;
        this.stockViewer = new StockViewer(api);
        loadData();
        updateTotalLabel();
        updateStockOverview();
        statusLabel.setText("System initialized");
    }

    /**
     * LOAD DATA into map
     */
    private void loadData() {
        if (supermarketAPI == null) return;

        clearSelectItems();
        floorAreaListView.getItems().clear();
        aisleListView.getItems().clear();
        shelfListView.getItems().clear();
        itemListView.getItems().clear();

        currentMode = ViewMode.FLOORS;
        modeHistory.clear();

        supermarketAPI.getFloorAreas().forEach(floor -> {
            if (floor != null) floorAreaListView.getItems().add(floor);
        });

        calculateContentSize();
        centerView();
        updateTotalLabel();
        updateBackButton();
        updateStockOverview();
        drawMap();
        statusLabel.setText("Data loaded - Viewing floor areas");
    }

    private void updateTotalLabel() {
        if (supermarketAPI == null) return;
        totalLabel.setText("Total: " + supermarketAPI.getFloorAreaCount() + " floors, " + supermarketAPI.getAisleCount() + " aisles, " + supermarketAPI.getShelfCount() + " shelves, " + supermarketAPI.getItemCount() + " items");
    }

    /**
     * SETUP - mouse/scroll event handler for map canvas
     * drag/click/zoom interactions
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
     * SETUP LISTVIEW - custom display for ListView items:
     * FloorArea "Title (Level)"
     * Aisle "Name (Temp)"
     * Shelf "Shelf # (Items)"
     * GoodItem "Desc (Unit Qty)"
     */

    private void setupListViewRenderers() {
        setupCell(floorAreaListView, area -> area.getTitle() + " (L" + area.getLevel() + ")");
        setupCell(aisleListView, aisle -> aisle.getName() + " (" + aisle.getTemp() + ")");
        setupCell(shelfListView, shelf -> "Shelf #" + shelf.getShelfNumber() + " (" + shelf.getItems().getSize() + " items)");
        setupCell(itemListView, item -> item.getDesc() + " (" + item.getQty() + " units)");
    }

    /**
     * SETUP CELL - format items with FunkierList string map.
     * convert model object to text, handle null.
     */
    private <T> void setupCell(ListView<T> list, FunkierList.StringMapper<T> mapper) {
        list.setCellFactory(lv -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : mapper.mapToString(item));
            }
        });
    }

    /**
     * SETUP SELECT LISTENER - call item corresponding method when selected
     */
    private void setupSelectionListeners() {
        floorAreaListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> onFloorAreaSelected(newVal));
        aisleListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> onAisleSelected(newVal));
        shelfListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> onShelfSelected(newVal));
        itemListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> onItemSelected(newVal));
    }


    /**
     * NAVIGATION - ZOOM/DRAG
     * <p>
     * Calculates the content dimensions for the current view mode.
     * Width/Height based on model dimensions.
     */
    private void calculateContentSize() {
        switch (currentMode) {
            case FLOORS -> calculateFloorsSize();
            case AISLES -> calculateAislesSize();
            case SHELVES -> calculateShelvesSize();
            case ITEMS -> calculateItemsSize();
        }
        contentWidth = Math.max(contentWidth, viewportWidth * 0.6);
        contentHeight = Math.max(contentHeight, viewportHeight * 0.4);
    }

    /**
     * Calculate content size of floors, distribute to overall view area
     * Determine canvas size needed to display by:
     * 1) bounding box
     * 2) dimensions provided (using PIXELS_PER_METER)
     * 3) canvas is 80% of viewport size with margin.
     */
    private void calculateFloorsSize() {
        if (supermarketAPI == null || supermarketAPI.getFloorAreaCount() == 0) {
            contentWidth = 400;
            contentHeight = 300;
            return;
        }

        final double[] b = {Double.MAX_VALUE, Double.MAX_VALUE, 0, 0};

        supermarketAPI.getFloorAreas().forEach(f -> {
            if (f == null || f.getDimensions() == null) return;
            Dimensions d = f.getDimensions();
            b[0] = Math.min(b[0], d.getPosX());
            b[1] = Math.min(b[1], d.getPosY());
            b[2] = Math.max(b[2], d.getPosX() + d.getWidth());
            b[3] = Math.max(b[3], d.getPosY() + d.getLength());
        });

        contentWidth = Math.max(viewportWidth * 0.8, (b[2] - b[0]) * PIXELS_PER_METER + 100);
        contentHeight = Math.max(viewportHeight * 0.8, (b[3] - b[1]) * PIXELS_PER_METER + 100);
    }

    /**
     * Calculate content size for aisles within selected floor.
     */
    private void calculateAislesSize() {
        if (selectedFloorArea == null || selectedFloorArea.getAisles().getSize() == 0) {
            contentWidth = 400;
            contentHeight = 300;
            return;
        }

        final double[] b = {Double.MAX_VALUE, Double.MAX_VALUE, 0, 0};

        selectedFloorArea.getAisles().forEach(a -> {
            if (a == null || a.getDimensions() == null) return;
            Dimensions d = a.getDimensions();
            b[0] = Math.min(b[0], d.getPosX());
            b[1] = Math.min(b[1], d.getPosY());
            b[2] = Math.max(b[2], d.getPosX() + d.getWidth());
            b[3] = Math.max(b[3], d.getPosY() + d.getLength());
        });

        contentWidth = Math.max(viewportWidth * 0.8, (b[2] - b[0]) * PIXELS_PER_METER + 100);
        contentHeight = Math.max(viewportHeight * 0.8, (b[3] - b[1]) * PIXELS_PER_METER + 100);
    }

    /**
     * Calculate content size for shelves within selected aisle.
     */

    private void calculateShelvesSize() {
        if (selectedAisle == null || selectedAisle.getShelves().getSize() == 0) {
            contentWidth = 400;
            contentHeight = 300;
            return;
        }

        int shelfCount = selectedAisle.getShelves().getSize();
        contentWidth = viewportWidth * 0.9;
        contentHeight = 50 + shelfCount * 400;
    }

    /**
     * Calculate content size for items within selected shelf.
     */
    private void calculateItemsSize() {
        if (selectedShelf == null || selectedShelf.getItems().getSize() == 0) {
            contentWidth = 400;
            contentHeight = 200;
            return;
        }

        int itemCount = selectedShelf.getItems().getSize();
        int perRow = Math.max(2, (int) ((viewportWidth - 100) / 350));
        int rows = (int) Math.ceil(itemCount / (double) perRow);

        contentWidth = 50 + perRow * 375;
        contentHeight = 100 + rows * 185;
    }

    /**
     * OFFSET BOUNDARY - Prevent content going outside visible area.
     */
    private void offsetBoundary() {
        double sw = contentWidth * scale, sh = contentHeight * scale;
        double maxX = Math.max(0, (sw - viewportWidth) / 2);
        double maxY = Math.max(0, (sh - viewportHeight) / 2);

        offsetX = Math.max(-maxX - 50, Math.min(maxX + 50, offsetX));
        offsetY = Math.max(-maxY - 50, Math.min(maxY + 50, offsetY));
    }


    /**
     * Centers content in viewport by midpoint offset.
     */
    private void centerView() {
        calculateContentSize();
        offsetX = (viewportWidth - contentWidth * scale) / 2;
        offsetY = (viewportHeight - contentHeight * scale) / 2;
        offsetBoundary();
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
        scale = Math.min(scale * factor, MAX_SCALE);
        double zoomRatio = scale / oldScale;

        offsetX = viewportWidth / 2 - zoomRatio * (viewportWidth / 2 - offsetX);
        offsetY = viewportHeight / 2 - zoomRatio * (viewportHeight / 2 - offsetY);
        offsetBoundary();
        drawMap();
    }

    @FXML
    private void handleResetView() {
        scale = 1.0;
        centerView();
        drawMap();
    }


    /**
     * CLICK HANDLERS - FLOOR, AISLE, SHELF, ITEM
     */


    private void handleCanvasMouseClick(double x, double y) {
        double canvasX = (x - offsetX) / scale;
        double canvasY = (y - offsetY) / scale;

        switch (currentMode) {
            case FLOORS -> handleFloorClick(canvasX, canvasY);
            case AISLES -> handleAisleClick(canvasX, canvasY);
            case SHELVES -> handleShelfClick(canvasX, canvasY);
            case ITEMS -> handleItemClick(canvasX, canvasY);
        }
    }

    private void handleFloorClick(double canvasX, double canvasY) {
        if (supermarketAPI == null) return;

        supermarketAPI.getFloorAreas().forEach(floor -> {
            if (floor == null || floor.getDimensions() == null) return;

            Dimensions dims = floor.getDimensions();
            double x = dims.getPosX() * PIXELS_PER_METER;
            double y = dims.getPosY() * PIXELS_PER_METER;
            double w = dims.getWidth() * PIXELS_PER_METER;
            double h = dims.getLength() * PIXELS_PER_METER;
            //check if in bounds
            if (canvasX >= x && canvasX <= x + w && canvasY >= y && canvasY <= y + h) {
                floorAreaListView.getSelectionModel().select(floor);
                onFloorAreaSelected(floor);
            }
        });
    }

    private void handleAisleClick(double canvasX, double canvasY) {
        if (selectedFloorArea == null || selectedFloorArea.getDimensions() == null) return;

        Dimensions floorDims = selectedFloorArea.getDimensions();
        double floorX = floorDims.getPosX() * PIXELS_PER_METER;
        double floorY = floorDims.getPosY() * PIXELS_PER_METER;

        selectedFloorArea.getAisles().forEach(aisle -> {
            if (aisle == null || aisle.getDimensions() == null) return;

            Dimensions dims = aisle.getDimensions();
            double x = floorX + (dims.getPosX() * PIXELS_PER_METER);
            double y = floorY + (dims.getPosY() * PIXELS_PER_METER);
            double w = dims.getWidth() * PIXELS_PER_METER;
            double h = dims.getLength() * PIXELS_PER_METER;

            if (canvasX >= x && canvasX <= x + w && canvasY >= y && canvasY <= y + h) {
                aisleListView.getSelectionModel().select(aisle);
                onAisleSelected(aisle);
            }
        });
    }

    private void handleShelfClick(double canvasX, double canvasY) {
        if (selectedAisle == null) return;

        double shelfW = contentWidth * 0.85, shelfH = 350;
        double startX = (contentWidth - shelfW) / 2, startY = contentHeight * 0.05, spacing = 40;

        FunkierList<Shelf> shelves = selectedAisle.getShelves();
        for (int i = 0; i < shelves.getSize(); i++) {
            Shelf shelf = shelves.get(i);
            if (shelf == null) continue;

            double rectX = startX, rectY = startY + (i * (shelfH + spacing));
            if (canvasX >= rectX && canvasX <= rectX + shelfW && canvasY >= rectY && canvasY <= rectY + shelfH) {
                shelfListView.getSelectionModel().select(shelf);
                onShelfSelected(shelf);
                return;
            }
        }
    }

    private void handleItemClick(double canvasX, double canvasY) {
        if (selectedShelf == null) return;

        double itemW = 350, itemH = 160, spacing = 25;
        int maxPerRow = Math.max(2, (int) ((contentWidth - 100) / (itemW + spacing)));
        double startX = (contentWidth - (maxPerRow * itemW + (maxPerRow - 1) * spacing)) / 2;
        double startY = 80;

        int drawnItems = 0;
        FunkierList<GoodItem> items = selectedShelf.getItems();
        for (int i = 0; i < items.getSize(); i++) {
            GoodItem item = items.get(i);
            if (item == null) continue;

            int row = drawnItems / maxPerRow;
            int col = drawnItems % maxPerRow;
            double rectX = startX + col * (itemW + spacing);
            double rectY = startY + row * (itemH + spacing);

            if (canvasX >= rectX && canvasX <= rectX + itemW && canvasY >= rectY && canvasY <= rectY + itemH) {
                itemListView.getSelectionModel().select(item);
                onItemSelected(item);
                return;
            }
            drawnItems++;
        }
    }

    /**
     * BACK BUTTON
     */
    @FXML
    private void handleBack() {
        if (modeHistory.isEmpty()) return;

        ViewMode previousMode = modeHistory.removeLast();

        switch (previousMode) {
            case FLOORS -> {
                clearSelectItems();
                currentMode = ViewMode.FLOORS;
                //clear + reload
                floorAreaListView.getItems().clear();
                if (supermarketAPI != null) {
                    supermarketAPI.getFloorAreas().forEach(floor -> {
                        if (floor != null) floorAreaListView.getItems().add(floor);
                    });
                }
            }
            case AISLES -> {
                selectedAisle = null;
                selectedShelf = null;
                selectedItem = null;
                currentMode = ViewMode.AISLES;
                if (selectedFloorArea != null) {
                    loadAislesForFloor(selectedFloorArea);
                    floorAreaListView.getSelectionModel().select(selectedFloorArea);
                }
            }
            case SHELVES -> {
                selectedShelf = null;
                selectedItem = null;
                currentMode = ViewMode.SHELVES;
                if (selectedAisle != null) {
                    loadShelvesForAisle(selectedAisle);
                    aisleListView.getSelectionModel().select(selectedAisle);
                }
            }
            case ITEMS -> {
                selectedItem = null;
                currentMode = ViewMode.ITEMS;
                if (selectedShelf != null) {
                    loadItemsForShelf(selectedShelf);
                    shelfListView.getSelectionModel().select(selectedShelf);
                }
            }
        }

        refreshDisplay();
    }


    /**
     * DRILL DOWN METHODS - floor, aisle, shelf, item
     */

    private void onFloorAreaSelected(FloorArea floor) {
        if (floor == selectedFloorArea) return;

        selectedFloorArea = floor;
        selectedAisle = null;
        selectedShelf = null;
        selectedItem = null;

        //get rid of lower levels
        aisleListView.getItems().clear();
        shelfListView.getItems().clear();
        itemListView.getItems().clear();

        if (floor != null) {
            //add view mode to history
            modeHistory.addAtLast(currentMode);
            currentMode = ViewMode.AISLES;
            loadAislesForFloor(floor);
            statusLabel.setText("Viewing aisles in " + floor.getTitle());
        }

        refreshDisplay();
    }

    private void onAisleSelected(Aisle a) {
        if (a == selectedAisle) return;

        selectedAisle = a;
        selectedShelf = null;
        selectedItem = null;


        shelfListView.getItems().clear();
        itemListView.getItems().clear();

        if (a != null) {
            modeHistory.addAtLast(currentMode);
            currentMode = ViewMode.SHELVES;
            loadShelvesForAisle(a);
            statusLabel.setText("Viewing shelves in " + a.getName());
        }

        refreshDisplay();
    }

    private void onShelfSelected(Shelf s) {
        if (s == selectedShelf) return;

        selectedShelf = s;
        selectedItem = null;

        itemListView.getItems().clear();

        if (s != null) {
            modeHistory.addAtLast(currentMode);
            currentMode = ViewMode.ITEMS;
            loadItemsForShelf(s);
            statusLabel.setText("Viewing " + s.getItems().getSize() + " items on Shelf #" + s.getShelfNumber());
        }

        refreshDisplay();
    }

    private void onItemSelected(GoodItem i) {
        selectedItem = i;
        updateDetails();
        updateStockOverview();
        statusLabel.setText(i != null ? "Selected: " + i.getDesc() : "No item selected");
    }

    /**
     * CLEAR/REFRESH HELPERS
     */

    private void clearSelectItems() {
        selectedFloorArea = null;
        selectedAisle = null;
        selectedShelf = null;
        selectedItem = null;
        floorAreaListView.getSelectionModel().clearSelection();
        aisleListView.getSelectionModel().clearSelection();
        shelfListView.getSelectionModel().clearSelection();
        itemListView.getSelectionModel().clearSelection();
    }

    private void refreshDisplay() {
        calculateContentSize();
        centerView();
        updateAccordionForMode();
        updateDetails();
        updateStockOverview();
        updateBackButton();
        drawMap();
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
     * DRAW - render map based on elements
     */
    private void drawMap() {
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        gc.save();
        gc.translate(offsetX, offsetY);
        gc.scale(scale, scale);
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, contentWidth, contentHeight);

        switch (currentMode) {
            case FLOORS -> drawFloorAreas();
            case AISLES -> {
                if (selectedFloorArea != null) drawAisles();
            }
            case SHELVES -> {
                if (selectedAisle != null) drawShelves();
            }
            case ITEMS -> {
                if (selectedShelf != null) drawItems();
            }
        }

        gc.restore();
    }

    /**
     * FLOOR AREA - position based on co-ordinates
     * show basic info and preview of aisles
     */
    private void drawFloorAreas() {
        if (supermarketAPI == null) return;

        supermarketAPI.getFloorAreas().forEach(floor -> {
            if (floor == null || floor.getDimensions() == null) return;

            Dimensions dims = floor.getDimensions();
            double x = dims.getPosX() * PIXELS_PER_METER;
            double y = dims.getPosY() * PIXELS_PER_METER;
            double w = dims.getWidth() * PIXELS_PER_METER;
            double h = dims.getLength() * PIXELS_PER_METER;

            // dynamic font size
            double titleFontSize = Math.max(12, Math.min(24, w / 30));
            double infoFontSize = Math.max(10, Math.min(16, w / 40));

            Color fill = getLevelColor(floor.getLevel());
            Color border = floor == selectedFloorArea ? Color.RED : Color.BLACK;
            int borderWidth = floor == selectedFloorArea ? 4 : 2;
            drawBox(x, y, w, h, fill, border, borderWidth);

            // basic floor info
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(titleFontSize));
            gc.fillText(floor.getTitle(), x + 10, y + titleFontSize + 5);

            gc.setFont(Font.font(infoFontSize));
            gc.fillText("Level: " + floor.getLevel(), x + 10, y + titleFontSize + infoFontSize + 15);
            gc.fillText("Aisles: " + floor.getAisles().getSize(), x + 10, y + titleFontSize + (infoFontSize * 2) + 25);
        });
    }

    /**
     * AISLE - position based on dimensions
     * show temperature, shelf count and shelf preview
     */

    private void drawAisles() {
        if (selectedFloorArea == null || selectedFloorArea.getDimensions() == null) return;

        Dimensions floorDims = selectedFloorArea.getDimensions();
        double floorX = floorDims.getPosX() * PIXELS_PER_METER;
        double floorY = floorDims.getPosY() * PIXELS_PER_METER;

        selectedFloorArea.getAisles().forEach(aisle -> {
            if (aisle == null || aisle.getDimensions() == null) return;

            Dimensions dims = aisle.getDimensions();
            double x = floorX + (dims.getPosX() * PIXELS_PER_METER);
            double y = floorY + (dims.getPosY() * PIXELS_PER_METER);
            double w = dims.getWidth() * PIXELS_PER_METER;
            double h = dims.getLength() * PIXELS_PER_METER;

            // dynamic font size
            double nameFontSize = Math.max(10, Math.min(20, Math.min(w, h) / 15));
            double infoFontSize = Math.max(8, Math.min(14, Math.min(w, h) / 20));

            Color fill = Color.valueOf(getTempColour(aisle.getTemp()));
            Color border = aisle == selectedAisle ? Color.RED : Color.BLACK;
            int borderWidth = aisle == selectedAisle ? 4 : 2;
            drawBox(x, y, w, h, fill, border, borderWidth);

            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(nameFontSize));


            String aisleName = aisle.getName();
            double nameY = y - 5;
            gc.fillText(aisleName, x, nameY);
            //temp
            gc.setFont(Font.font(infoFontSize));
            String tempText = aisle.getTemp();
            double tempY = nameY - infoFontSize - 5;
            gc.fillText(tempText, x, tempY);

            gc.fillText("S: " + aisle.getShelves().getSize(), x + 5, y + h - 5);
        });
    }

    /**
     * SHELVES - vertical stacked layout
     * show capacity w/ bar, current weight usage and item count.
     */

    private void drawShelves() {
        if (selectedAisle == null) return;

        double shelfW = contentWidth * 0.85, shelfH = 350;
        double startX = (contentWidth - shelfW) / 2, startY = contentHeight * 0.05, spacing = 40;

        FunkierList<Shelf> shelves = selectedAisle.getShelves();
        for (int i = 0; i < shelves.getSize(); i++) {
            Shelf shelf = shelves.get(i);
            if (shelf == null) continue;

            double x = startX, y = startY + (i * (shelfH + spacing));
            contentHeight = Math.max(contentHeight, y + shelfH + 50);

            double util = shelf.getCurrentWeight() / shelf.getCapacity();
            Color fill = util > 0.9 ? Color.RED : util > 0.7 ? Color.ORANGE : Color.LIGHTGREEN;
            Color border = shelf == selectedShelf ? Color.RED : Color.BLACK;
            int borderWidth = shelf == selectedShelf ? 4 : 2;
            drawBox(x, y, shelfW, shelfH, fill, border, borderWidth);

            //text
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(20));
            gc.fillText("SHELF #" + shelf.getShelfNumber(), x + 20, y + 40);
            gc.setFont(Font.font(16));
            gc.fillText("Capacity: " + InputVal.twoDecPlaces(shelf.getCapacity()) + " kg", x + 20, y + 80);
            gc.fillText("Current: " + InputVal.twoDecPlaces(shelf.getCurrentWeight()) + " kg", x + 20, y + 110);
            gc.fillText("Usage: " + InputVal.twoDecPlaces(util * 100) + "%", x + 20, y + 140);
            gc.fillText("Items: " + shelf.getItems().getSize(), x + 20, y + 170);

            // capacity bar
            double barW = shelfW * 0.7, barH = 25;
            double barX = x + 20, barY = y + 200;

            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(barX, barY, barW, barH);
            gc.setFill(util > 0.9 ? Color.RED : util > 0.7 ? Color.ORANGE : Color.GREEN);
            gc.fillRect(barX, barY, barW * util, barH);

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeRect(barX, barY, barW, barH);

            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(12));
            gc.fillText(InputVal.twoDecPlaces(shelf.getCurrentWeight()) + "/" + InputVal.twoDecPlaces(shelf.getCapacity()) + " kg (" + InputVal.twoDecPlaces(util * 100) + "%)", barX + 10, barY + 17);
        }
    }

    /**
     * ITEMS - grid layout in given shelf
     * show description, quantity, price, total vlaue and size
     */
    private void drawItems() {
        if (selectedShelf == null) return;

        drawBox(0, 0, contentWidth, contentHeight, Color.WHITE, Color.WHITE, 0);
        gc.setFill(Color.DARKBLUE);
        gc.setFont(Font.font(24));
        gc.fillText("SHELF #" + selectedShelf.getShelfNumber() + " - ITEMS INVENTORY", contentWidth * 0.05, 40);

        if (selectedShelf.getItems().findAll(item -> item != null).isEmpty()) {
            gc.setFill(Color.GRAY);
            gc.setFont(Font.font(20));
            gc.fillText("SHELF IS EMPTY", contentWidth * 0.35, contentHeight * 0.5);
            return;
        }

        double itemW = 350, itemH = 160, spacing = 25;
        int maxPerRow = Math.max(2, (int) ((contentWidth - 100) / (itemW + spacing)));
        double startX = (contentWidth - (maxPerRow * itemW + (maxPerRow - 1) * spacing)) / 2;
        double startY = 80;

        int drawnItems = 0;
        FunkierList<GoodItem> items = selectedShelf.getItems();
        for (int i = 0; i < items.getSize(); i++) {
            GoodItem item = items.get(i);
            if (item == null) continue;

            int row = drawnItems / maxPerRow;
            int col = drawnItems % maxPerRow;
            double x = startX + col * (itemW + spacing);
            double y = startY + row * (itemH + spacing);
            contentHeight = Math.max(contentHeight, y + itemH + 50);
            contentWidth = Math.max(contentWidth, x + itemW + 50);

            drawBox(x, y, itemW, itemH, Color.LIGHTBLUE, Color.DARKBLUE, 2);

            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(14));
            String desc = item.getDesc().length() > 30 ? item.getDesc().substring(0, 30) + "..." : item.getDesc();
            gc.fillText(desc, x + 10, y + 25);
            gc.setFont(Font.font(12));
            gc.fillText("Quantity: " + item.getQty() + " units", x + 10, y + 55);
            gc.fillText("Price: €" + InputVal.twoDecPlaces(item.getPrice()), x + 10, y + 80);
            gc.fillText("Total: €" + InputVal.twoDecPlaces(item.getTotalValue()), x + 10, y + 105);
            gc.fillText("Size: " + item.getUnitSize() + "g each", x + 10, y + 130);
            drawnItems++;
        }
    }

    /**
     * STOCK OVERVIEW
     */

    private void updateStockOverview() {
        if (stockViewer == null || stockViewerDisplay == null) return;

        StringBuilder stockInfo = new StringBuilder("STOCK OVERVIEW\n\n");

        if (selectedItem != null) {
            stockInfo.append(getSelectedItemInfo());
        } else if (selectedShelf != null) {
            stockInfo.append(getShelfInfo());
        } else if (selectedAisle != null) {
            stockInfo.append(getAisleInfo());
        } else if (selectedFloorArea != null) {
            stockInfo.append(getFloorInfo());
        } else {
            stockInfo.append(getSystemOverview());
        }

        stockViewerDisplay.setText(stockInfo.toString());
    }

    private String getSelectedItemInfo() {
        StringBuilder info = new StringBuilder("SELECTED ITEM:\n").append("Description: ").append(selectedItem.getDesc()).append("\n").append("Quantity: ").append(selectedItem.getQty()).append(" units\n").append("Price: €").append(InputVal.twoDecPlaces(selectedItem.getPrice())).append("\n").append("Total Value: €").append(InputVal.twoDecPlaces(selectedItem.getTotalValue())).append("\n").append("Size: ").append(selectedItem.getUnitSize()).append("g each\n").append("Storage: ").append(selectedItem.getStorageCategory()).append("\n\n");

        if (selectedShelf != null) {
            info.append("OTHER ITEMS ON THIS SHELF:\n");
            selectedShelf.getItems().forEach(item -> {
                if (item != null && item != selectedItem) {
                    info.append("• ").append(item.getDesc()).append(" (").append(item.getQty()).append(" units)\n");
                }
            });
        }
        return info.toString();
    }

    private String getShelfInfo() {
        ShelfManager manager = new ShelfManager(supermarketAPI);
        return manager.getShelfStatistics() + "\nSHELF #" + selectedShelf.getShelfNumber() + " (" + selectedShelf.getCurrentWeight() + "/" + selectedShelf.getCapacity() + "kg)\n";
    }

    private String getAisleInfo() {
        AisleManager manager = new AisleManager(supermarketAPI);
        return manager.getAisleStatistics() + "\nAISLE: " + selectedAisle.getName() + " (" + selectedAisle.getTemp() + ")\n";
    }

    private String getFloorInfo() {
        FloorAreaManager manager = new FloorAreaManager(supermarketAPI);
        return manager.getFloorAreaStatistics() + "\nFLOOR: " + selectedFloorArea.getTitle() + " (Level " + selectedFloorArea.getLevel() + ")\n";
    }

    private String getSystemOverview() {
        return new FloorAreaManager(supermarketAPI).getFloorAreaStatistics() + "\n" + new AisleManager(supermarketAPI).getAisleStatistics() + "\n" + new ShelfManager(supermarketAPI).getShelfStatistics() + "\n" + new GoodItemManager(supermarketAPI).getItemStatistics();
    }

    /**
     * DETAIL PANE
     */
    private void updateDetails() {
        StringBuilder details = new StringBuilder();
        if (selectedFloorArea != null) {
            details.append("FLOOR AREA\n").append("Title: ").append(selectedFloorArea.getTitle()).append("\n").append("Level: ").append(selectedFloorArea.getLevel()).append("\n").append("Aisles: ").append(selectedFloorArea.getAisles().getSize()).append("\n\n");
        }
        if (selectedAisle != null) {
            details.append("AISLE\n").append("Name: ").append(selectedAisle.getName()).append("\n").append("Temperature: ").append(selectedAisle.getTemp()).append("\n").append("Shelves: ").append(selectedAisle.getShelves().getSize()).append("\n\n");
        }
        if (selectedShelf != null) {
            details.append("SHELF\n").append("Shelf #").append(selectedShelf.getShelfNumber()).append("\n").append("Capacity: ").append(InputVal.twoDecPlaces(selectedShelf.getCapacity())).append("kg\n").append("Current: ").append(InputVal.twoDecPlaces(selectedShelf.getCurrentWeight())).append("kg\n").append("Items: ").append(selectedShelf.getItems().getSize()).append("\n\n");
        }
        if (selectedItem != null) {
            details.append("ITEM\n").append("Description: ").append(selectedItem.getDesc()).append("\n").append("Quantity: ").append(selectedItem.getQty()).append("\n").append("Price: €").append(selectedItem.getPrice()).append("\n").append("Unit Size: ").append(selectedItem.getUnitSize()).append("g\n");
        }
        detailsDisplay.setText(details.toString());
    }

    /**
     * UPDATE - back button, accordion
     */
    private void updateBackButton() {
        backButton.setDisable(modeHistory.isEmpty());
    }

    private void updateAccordionForMode() {
        switch (currentMode) {
            case FLOORS -> navigationAccordion.setExpandedPane(floorAreaPane);
            case AISLES -> navigationAccordion.setExpandedPane(aislesPane);
            case SHELVES -> navigationAccordion.setExpandedPane(shelvesPane);
            case ITEMS -> navigationAccordion.setExpandedPane(itemsPane);
        }
    }

    /**
     * LOAD UTILITIES
     */
    private void loadAislesForFloor(FloorArea floorArea) {
        aisleListView.getItems().clear();
        shelfListView.getItems().clear();
        itemListView.getItems().clear();
        if (floorArea == null) return;

        floorArea.getAisles().forEach(aisle -> {
            if (aisle != null) aisleListView.getItems().add(aisle);
        });
    }

    private void loadShelvesForAisle(Aisle aisle) {
        shelfListView.getItems().clear();
        itemListView.getItems().clear();
        if (aisle == null) return;

        aisle.getShelves().forEach(shelf -> {
            if (shelf != null) shelfListView.getItems().add(shelf);
        });
    }

    private void loadItemsForShelf(Shelf shelf) {
        itemListView.getItems().clear();
        if (shelf == null) return;

        shelf.getItems().forEach(item -> {
            if (item != null) itemListView.getItems().add(item);
        });
    }

    /**
     * COLOURS - by level/temp
     */

    private Color getLevelColor(int level) {
        return switch (level) {
            case 0 -> Color.LIGHTGREEN;
            case 1 -> Color.LIGHTBLUE;
            case 2 -> Color.LIGHTCORAL;
            default -> Color.LIGHTGRAY;
        };
    }

    private String getTempColour(String temperature) {
        if (temperature == null) return "#FFD700";
        if (temperature.toLowerCase().contains("frozen")) return "#ADD8E6";
        else if (temperature.toLowerCase().contains("refrigerated")) return "#90EE90";
        else return "#FFD700";
    }

    /**
     * Map navigation
     */
    private enum ViewMode {FLOORS, AISLES, SHELVES, ITEMS}
}