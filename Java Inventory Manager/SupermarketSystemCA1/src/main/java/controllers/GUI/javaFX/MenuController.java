package controllers.GUI.javaFX;

import controllers.API.SupermarketAPI;
import controllers.managers.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Aisle;
import models.FloorArea;
import models.GoodItem;
import models.Shelf;
import utils.CustomList.FunkierList;
import utils.Dimensions;
import utils.Persistence;
import utils.Validators.InputVal;

import java.io.File;
import java.util.Optional;

public class MenuController {

    // BUTTONS
    @FXML private Button stockButton, resetButton, mapViewButton, exitButton, saveButton;
    @FXML private Button overallStockBtn, floorStockBtn, aisleStockBtn, shelfStockBtn, itemStockBtn;
    @FXML private Button tempStockBtn, priceRangeBtn, lowStockBtn, distributionBtn, fullReportBtn;
    @FXML private Button addItemButton, updateItemButton, deleteItemButton, clearFormButton;
    @FXML private Button addFloorButton, addAisleButton, addShelfButton, manageStructuresButton;
    @FXML private Button addImageButton, removeImageButton;

    // TABLE
    @FXML private TableView<GoodItem> itemTableView;
    @FXML private TableColumn<GoodItem, Integer> idColumn, quantityColumn;
    @FXML private TableColumn<GoodItem, String> nameColumn, tempColumn, iconColumn;
    @FXML private TableColumn<GoodItem, Double> weightColumn, priceColumn;

    // FORM FOR ITEMS
    @FXML private TextField nameField, weightField, priceField, quantityField;
    @FXML private TextField floorOrientationField, aisleOrientationField;

    // FORM FOR STRUCT
    @FXML private TextField floorTitleField, floorLevelField, floorWidthField, floorLengthField;
    @FXML private TextField floorPosXField, floorPosYField;
    @FXML private TextField aisleNameField, aisleWidthField, aisleLengthField, aislePosXField, aislePosYField;
    @FXML private TextField shelfCapacityField, shelfWeightField, shelfQuantityField;

    // COMBOBOXES
    @FXML private ComboBox<String> tempComboBox, weightUnitComboBox, currencyComboBox;
    @FXML private ComboBox<String> aisleComboBox, floorAreaComboBox, shelfAisleComboBox, aisleTempComboBox;

    // IMG
    @FXML private AnchorPane imagePreviewPane;
    @FXML private ImageView itemImageView;
    @FXML private Label imageNameLabel;

    // TAB
    @FXML private TabPane mainTabPane;
    @FXML private Tab stockViewerTab, itemsTab, floorsTab, aislesTab, shelvesTab;

    private SupermarketAPI supermarketAPI;
    private FloorAreaManager floorAreaManager;
    private AisleManager aisleManager;
    private ShelfManager shelfManager;
    private GoodItemManager goodItemManager;

    private File selectedPhotoFile;
    private String photoUrl = "no photo.";
    private FunkierList<GoodItem> itemData = new FunkierList<>();
    private FunkierList.MapViewOpener mapViewOpener;


    /**
     * INITIALIZERS
     */

    @FXML
    private void initialize() {
        System.out.println("MenuController initialized");
        setupButtonActions();
        initializeImagePreview();
        initializeFormComponents();
        setupTable();
        setupTableSelectionListener();
    }

    private void initializeImagePreview() {
        if (itemImageView != null) {
            itemImageView.setFitWidth(140);
            itemImageView.setFitHeight(130);
            itemImageView.setPreserveRatio(true);
        }
        if (imageNameLabel != null) {
            imageNameLabel.setText("No image selected");
        }
    }

    private void initializeFormComponents() {
        if (tempComboBox != null) {
            tempComboBox.getItems().addAll("FROZEN", "REFRIGERATED", "UNREFRIGERATED");
            tempComboBox.setValue("UNREFRIGERATED");
        }
        if (aisleTempComboBox != null) {
            aisleTempComboBox.getItems().addAll("FROZEN", "REFRIGERATED", "UNREFRIGERATED");
            aisleTempComboBox.setValue("UNREFRIGERATED");
        }
        if (weightUnitComboBox != null) {
            weightUnitComboBox.getItems().addAll("g", "kg", "lb");
            weightUnitComboBox.setValue("g");
        }
        if (currencyComboBox != null) {
            currencyComboBox.getItems().addAll("€", "$", "£");
            currencyComboBox.setValue("€");
        }
    }

    /**
     * SETUP
     */
    //ACTIONS
    private void setupButtonActions() {
        assignButton(stockButton, e -> handleStock());
        assignButton(resetButton, e -> handleReset());
        assignButton(mapViewButton, e -> handleMapView());
        assignButton(exitButton, e -> handleExit());
        assignButton(saveButton, e -> handleSave());

        // CRUD
        assignButton(addItemButton, e -> handleAddItem());
        assignButton(updateItemButton, e -> handleUpdateItem());
        assignButton(deleteItemButton, e -> handleDeleteItem());
        assignButton(clearFormButton, e -> clearItemForm());
        assignButton(addFloorButton, e -> handleAddFloor());
        assignButton(addAisleButton, e -> handleAddAisle());
        assignButton(addShelfButton, e -> handleAddShelf());

        // image
        assignButton(addImageButton, e -> handleAddImage());
        assignButton(removeImageButton, e -> handleRemoveImage());

        // STOCK REPORT
        assignButton(overallStockBtn, e -> showStockInDialog("Overall Stock Summary", getOverallStockSummary()));
        assignButton(floorStockBtn, e -> showStockInDialog("Floor Area Statistics", getFloorStockStatistics()));
        assignButton(aisleStockBtn, e -> showStockInDialog("Aisle Statistics", getAisleStockStatistics()));
        assignButton(shelfStockBtn, e -> showStockInDialog("Shelf Statistics", getShelfStockStatistics()));
        assignButton(itemStockBtn, e -> showStockInDialog("Item Statistics", getItemStockStatistics()));
        assignButton(tempStockBtn, e -> showStockInDialog("Stock by Temperature", getStockByTemperature()));
        assignButton(priceRangeBtn, e -> showStockInDialog("Stock by Price Range", getStockByPriceRange()));
        assignButton(lowStockBtn, e -> showStockInDialog("Low Stock Report", getLowStockReport()));
        assignButton(distributionBtn, e -> showStockInDialog("Stock Distribution Analysis", getStockDistribution()));
        assignButton(fullReportBtn, e -> showStockInDialog("Full Stock Report", getFullStockReport()));
    }

    private void assignButton(Button button, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        if (button != null) {
            button.setOnAction(handler);
        }
    }

    //TABLE
    private void setupTable() {
        if (itemTableView == null) {
            System.out.println("Table view is null - check FXML file");
            return;
        }
        setupColumn(nameColumn, "desc");
        setupColumn(tempColumn, "storageCategory");
        setupColumn(priceColumn, "price");
        setupColumn(quantityColumn, "qty");
        setupColumn(iconColumn, "photoUrl");

        if (weightColumn != null) {
            weightColumn.setCellValueFactory(cellData ->
                    javafx.beans.binding.Bindings.createObjectBinding(() ->
                            cellData.getValue().getUnitSize() * cellData.getValue().getQty() / 1000.0
                    )
            );
        }
        updateTableData();
    }
    private void setupColumn(TableColumn column, String property) {
        if (column != null) {
            column.setCellValueFactory(new PropertyValueFactory<>(property));
        }
    }

    private void setupTableSelectionListener() {
        itemTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadItemIntoForm(newSelection);
                if (mainTabPane != null && itemsTab != null) {
                    mainTabPane.getSelectionModel().select(itemsTab);
                }
            }
        });
    }

    private void updateTableData() {
        if (itemTableView != null) {
            ObservableList<GoodItem> observableList = FXCollections.observableArrayList();
            itemData.forEach(item -> observableList.add(item));
            itemTableView.setItems(observableList);
        }
    }

    public void setSystemComponents(SupermarketAPI api, FloorAreaManager fam,
                                    AisleManager am, ShelfManager sm, GoodItemManager gim) {
        this.supermarketAPI = api;
        this.floorAreaManager = fam;
        this.aisleManager = am;
        this.shelfManager = sm;
        this.goodItemManager = gim;
        if (itemTableView != null) {
            refreshTableData();
        }
        loadComboBoxData();
    }

    /**
     * LOAD
     */
    private void loadComboBoxData() {
        loadComboBox(floorAreaComboBox, "Select a floor area...", true);
        loadComboBox(aisleComboBox, "Select an aisle...", true);
        loadComboBox(shelfAisleComboBox, "Select an aisle...", false);
    }

    private void loadComboBox(ComboBox<String> comboBox, String val, boolean includeTemp) {
        if (comboBox == null || supermarketAPI == null) return;

        comboBox.getItems().clear();
        comboBox.getItems().add(val);

        if (comboBox == floorAreaComboBox) {
            supermarketAPI.getFloorAreas().forEach(floor -> {
                if (floor != null) {
                    comboBox.getItems().add(floor.getTitle() + " (Level " + floor.getLevel() + ")");
                }
            });
        } else if (comboBox == aisleComboBox || comboBox == shelfAisleComboBox) {
            supermarketAPI.getAisles().forEach(aisle -> {
                if (aisle != null) {
                    String displayText = includeTemp ?
                            aisle.getName() + " (" + aisle.getTemp() + ")" :
                            aisle.getName();
                    comboBox.getItems().add(displayText);
                }
            });
        }
        comboBox.setValue(val);
    }

    private void loadItemIntoForm(GoodItem item) {
        if (item == null) return;

        nameField.setText(item.getDesc());
        weightField.setText(String.valueOf(item.getUnitSize()));
        priceField.setText(String.valueOf(item.getPrice()));
        quantityField.setText(String.valueOf(item.getQty()));

        if (tempComboBox != null) {
            tempComboBox.setValue(item.getStorageCategory());
        }

        if (aisleComboBox != null && item.getAisle() != null) {
            aisleComboBox.setValue(item.getAisle().getName() + " (" + item.getAisle().getTemp() + ")");
        }

        if (item.getPhotoUrl() != null && !item.getPhotoUrl().equals("no photo.")) {
            photoUrl = item.getPhotoUrl();
            updateImagePreview();
        } else {
            clearImagePreview();
        }
    }

    /**
     * SAVE
     */

    private void handleSave() {
        if (supermarketAPI == null) {
            showAlert("Error", "Cannot save.");
            return;
        }

        try {
            Persistence persistence = new Persistence(supermarketAPI);
            persistence.saveAllData();

            showAlert("Success", "Saved. \n" +
                    "Floor Areas: " + supermarketAPI.getFloorAreaCount() + "\n" +
                    "Aisles: " + supermarketAPI.getAisleCount() + "\n" +
                    "Shelves: " + supermarketAPI.getShelfCount() + "\n" +
                    "Items: " + supermarketAPI.getItemCount());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save data: " + e.getMessage());
        }
    }



    private void refreshTableData() {
        if (supermarketAPI != null && itemTableView != null) {
            itemData.clear();

            supermarketAPI.getGoodItems().forEach(item -> {
                if (item != null) {
                    itemData.addAtLast(item);
                }
            });

            updateTableData();
            System.out.println("Refreshed table with " + itemData.getSize() + " items");
        }
    }

    /**
     * HANDLE - STOCK
     */
    private void handleStock() {

        if (supermarketAPI == null) {
            showAlert("Error", "System not initialized.");
            return;
        }
        if (mainTabPane != null && stockViewerTab != null) {
            mainTabPane.getSelectionModel().select(stockViewerTab);
        }
    }

    /**
     * HANDLE ITEM - add, update, delete
     */

    //ADD
    private void handleAddItem() {
        try {
            if (!validateItemForm()) return;

            String description = nameField.getText().trim();
            double unitSize = Double.parseDouble(weightField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            String temperature = tempComboBox.getValue();
            double storageTemp = convertTemperatureToValue(temperature);

            Aisle aisle = getSelectedAisle();
            if (aisle == null) {
                showAlert("Error", "Select an aisle.");
                return;
            }
            FunkierList<Shelf> shelves = new FunkierList<>();
            boolean success = goodItemManager.addGoodItem(
                    description, unitSize, price, quantity, storageTemp,
                    photoUrl, aisle, shelves
            );
            if (success) {
                showAlert("Success", "Item added.");
                refreshTableData();
                clearItemForm();
                loadComboBoxData();
            } else {
                showAlert("Error", "Failed to add item.");
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid num.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add item: " + e.getMessage());
        }
    }

    //UPDATE
    private void handleUpdateItem() {
        GoodItem selectedItem = itemTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Error", "No item selected.");
            return;
        }
        try {
            if (!validateItemForm()) return;
            String description = nameField.getText().trim();
            double unitSize = Double.parseDouble(weightField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());

            selectedItem.setDescription(description);
            selectedItem.setUnitSize(unitSize);
            selectedItem.setUnitPrice(price);
            selectedItem.setQty(quantity);
            selectedItem.setPhotoUrl(photoUrl);

            Aisle newAisle = getSelectedAisle();
            if (newAisle != null && !newAisle.equals(selectedItem.getAisle())) {
                selectedItem.setAisle(newAisle);
            }
            itemTableView.refresh();
            showAlert("Success", "Item updated.");
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid num.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update item: " + e.getMessage());
        }
    }

    //DELETE
    private void handleDeleteItem() {
        GoodItem selectedItem = itemTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Error", "No item selected.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Item");
        alert.setContentText("Are you sure you want to delete '" + selectedItem.getDesc() + "'?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = goodItemManager.removeGoodItem(selectedItem);
            if (success) {
                showAlert("Success", "Item deleted.");
                refreshTableData();
                clearItemForm();
            } else {
                showAlert("Error", "Failed to delete item.");
            }
        }
    }

    /**
     * ITEM FORM - clear, validate
     */
    private void clearItemForm() {
        nameField.clear();
        weightField.clear();
        priceField.clear();
        quantityField.clear();
        if (tempComboBox != null) tempComboBox.setValue("UNREFRIGERATED");
        if (aisleComboBox != null) aisleComboBox.setValue("Select an aisle...");
        itemTableView.getSelectionModel().clearSelection();
        clearImagePreview();
    }

    private boolean validateItemForm() {
        if (nameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter an item name.");
            return false;
        }
        String[] numericFields = {"Weight", "Price", "Quantity"};
        TextField[] fieldRefs = {weightField, priceField, quantityField};

        for (int i = 0; i < numericFields.length; i++) {
            try {
                double value = Double.parseDouble(fieldRefs[i].getText().trim());
                if (value <= 0) {
                    showAlert("Validation Error", numericFields[i] + " must be a positive number.");
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert("Validation Error", "Please enter a valid number for " + numericFields[i] + ".");
                return false;
            }
        }
        if (aisleComboBox.getValue() == null || aisleComboBox.getValue().equals("Select an aisle...")) {
            showAlert("Validation Error", "Please select an aisle.");
            return false;
        }
        return true;
    }

    /**
     * HANDLE ADD - floor, aisle, shelf
     */

    private void handleAddFloor() {
        try {
            if (!validateFloorForm()) return;

            String title = floorTitleField.getText().trim();
            int level = Integer.parseInt(floorLevelField.getText().trim());
            double width = Double.parseDouble(floorWidthField.getText().trim());
            double length = Double.parseDouble(floorLengthField.getText().trim());
            double posX = Double.parseDouble(floorPosXField.getText().trim());
            double posY = Double.parseDouble(floorPosYField.getText().trim());
            double orientation = Double.parseDouble(floorOrientationField.getText().trim());

            Dimensions dimensions = new Dimensions(width, length, posX, posY, orientation);
            FunkierList<Aisle> aisles = new FunkierList<>();

            boolean success = floorAreaManager.addFloorArea(title, level, dimensions, aisles);

            if (success) {
                showAlert("Success", "Floor area added.");
                loadComboBoxData();
                clearFloorForm();
            } else {
                showAlert("Error", "Failed to add.");
            }

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid num.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add.");
        }
    }

    private void handleAddAisle() {
        try {
            if (!validateAisleForm()) return;

            String name = aisleNameField.getText().trim();
            String temperature = aisleTempComboBox.getValue();
            double width = Double.parseDouble(aisleWidthField.getText().trim());
            double length = Double.parseDouble(aisleLengthField.getText().trim());
            double posX = Double.parseDouble(aislePosXField.getText().trim());
            double posY = Double.parseDouble(aislePosYField.getText().trim());
            double orientation = Double.parseDouble(aisleOrientationField.getText().trim());

            Dimensions dimensions = new Dimensions(width, length, posX, posY, orientation);

            FloorArea floorArea = getSelectedFloorArea();
            if (floorArea == null) {
                showAlert("Error", "Please select a floor area.");
                return;
            }

            FunkierList<Shelf> shelves = new FunkierList<>();
            boolean success = aisleManager.addAisle(name, temperature, dimensions, floorArea, shelves);

            if (success) {
                showAlert("Success", "Aisle added.");
                loadComboBoxData();
                clearAisleForm();
            } else {
                showAlert("Error", "Failed to add.");
            }

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid num.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add");
        }
    }

    private void handleAddShelf() {
        try {
            if (!validateShelfForm()) return;
            double capacity = Double.parseDouble(shelfCapacityField.getText().trim());
            double currentWeight = Double.parseDouble(shelfWeightField.getText().trim());
            int currentQuantity = Integer.parseInt(shelfQuantityField.getText().trim());

            Aisle aisle = getSelectedAisleForShelf();
            if (aisle == null) {
                showAlert("Error", "Please select an aisle.");
                return;
            }
            FunkierList<GoodItem> items = new FunkierList<>();
            boolean success = shelfManager.addShelf(capacity, currentWeight, currentQuantity, aisle, items);
            if (success) {
                showAlert("Success", "Shelf added.");
                clearShelfForm();
            } else {
                showAlert("Error", "Failed to add.");
            }

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid num.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add.");
        }
    }

    /**
     * VALIDATE FORM - floor, aisle, shelf
     */
    private boolean validateFloorForm() {
        if (floorTitleField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter a floor title.");
            return false;
        }

        String[] numericFields = {"Level", "Width", "Length", "Position X", "Position Y", "Orientation"};
        TextField[] fieldRefs = {floorLevelField, floorWidthField, floorLengthField, floorPosXField, floorPosYField, floorOrientationField};

        for (int i = 0; i < numericFields.length; i++) {
            try {
                Double.parseDouble(fieldRefs[i].getText().trim());
            } catch (NumberFormatException e) {
                showAlert("Validation Error", "Please enter a valid number for " + numericFields[i] + ".");
                return false;
            }
        }

        return true;
    }

    private boolean validateAisleForm() {
        if (aisleNameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter an aisle name.");
            return false;
        }

        if (floorAreaComboBox.getValue() == null || floorAreaComboBox.getValue().equals("Select a floor area...")) {
            showAlert("Validation Error", "Please select a floor area.");
            return false;
        }
        String[] numericFields = {"Width", "Length", "Position X", "Position Y", "Orientation"};
        TextField[] fieldRefs = {aisleWidthField, aisleLengthField, aislePosXField, aislePosYField, aisleOrientationField};
        for (int i = 0; i < numericFields.length; i++) {
            try {
                double value = Double.parseDouble(fieldRefs[i].getText().trim());
                if (value <= 0 && !numericFields[i].equals("Orientation")) {
                    showAlert("Validation Error", numericFields[i] + " must be a positive number.");
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert("Validation Error", "Please enter a valid number for " + numericFields[i] + ".");
                return false;
            }
        }
        return true;
    }

    private boolean validateShelfForm() {
        if (shelfAisleComboBox.getValue() == null || shelfAisleComboBox.getValue().equals("Select an aisle...")) {
            showAlert("Validation Error", "Please select an aisle.");
            return false;
        }

        String[] numericFields = {"Capacity", "Current Weight", "Current Quantity"};
        TextField[] fieldRefs = {shelfCapacityField, shelfWeightField, shelfQuantityField};

        for (int i = 0; i < numericFields.length; i++) {
            try {
                double value = Double.parseDouble(fieldRefs[i].getText().trim());
                if (value < 0) {
                    showAlert("Validation Error", numericFields[i] + " cannot be negative.");
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert("Validation Error", "Please enter a valid number for " + numericFields[i] + ".");
                return false;
            }
        }
        return true;
    }


    /**
     * CLEAR FORM - floor, aisle, shelf
     */

    private void clearFloorForm() {
        floorTitleField.clear();
        floorLevelField.setText("0");
        floorWidthField.setText("10");
        floorLengthField.setText("10");
        floorPosXField.setText("0");
        floorPosYField.setText("0");
        floorOrientationField.setText("0");
    }

    private void clearAisleForm() {
        aisleNameField.clear();
        aisleWidthField.setText("2");
        aisleLengthField.setText("8");
        aislePosXField.setText("0");
        aislePosYField.setText("0");
        aisleOrientationField.setText("0");
        if (aisleTempComboBox != null) aisleTempComboBox.setValue("UNREFRIGERATED");
        if (floorAreaComboBox != null) floorAreaComboBox.setValue("Select a floor area...");
    }

    private void clearShelfForm() {
        shelfCapacityField.setText("50");
        shelfWeightField.setText("0");
        shelfQuantityField.setText("0");
        if (shelfAisleComboBox != null) shelfAisleComboBox.setValue("Select an aisle...");
    }

    /**
     * HANDLE IMAGE - add, remove
     */
    private void handleAddImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Item Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        Stage stage = (Stage) addImageButton.getScene().getWindow();
        selectedPhotoFile = fileChooser.showOpenDialog(stage);

        if (selectedPhotoFile != null) {
            try {
                photoUrl = selectedPhotoFile.getAbsolutePath();
                updateImagePreview();

                showAlert("Success", "Image added.");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load image: " + e.getMessage());
            }
        }
    }

    private void handleRemoveImage() {
        clearImagePreview();
        showAlert("Info", "Image removed.");
    }


    /**
     * UPDATE IMAGE - with preview
     */
    private void updateImagePreview() {
        if (itemImageView != null && selectedPhotoFile != null) {
            try {
                Image image = new Image(selectedPhotoFile.toURI().toString());
                itemImageView.setImage(image);
                itemImageView.setVisible(true);
                if (imagePreviewPane != null) {
                    imagePreviewPane.setVisible(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to display image: " + e.getMessage());
            }
        }
    }

    private void clearImagePreview() {
        if (itemImageView != null) {
            itemImageView.setImage(null);
            itemImageView.setVisible(false);
        }

        if (imagePreviewPane != null) {
            imagePreviewPane.setVisible(true);
        }
        selectedPhotoFile = null;
        photoUrl = "no photo.";
    }



    private double convertTemperatureToValue(String temperature) {
        switch (temperature.toUpperCase()) {
            case "FROZEN":
                return -18.0;
            case "REFRIGERATED":
                return 4.0;
            case "UNREFRIGERATED":
            default:
                return 20.0;
        }
    }

    /**
     * GETTERS - floor, aisle, aisle for shelf
     */
    private FloorArea getSelectedFloorArea() {
        if (floorAreaComboBox == null || supermarketAPI == null) return null;

        String selected = floorAreaComboBox.getValue();
        if (selected == null || selected.equals("Select a floor area...")) return null;

        String floorTitle = selected.split("\\s+\\(")[0];
        return floorAreaManager.findFloorAreaByTitle(floorTitle);
    }


    private Aisle getSelectedAisle() {
        if (aisleComboBox == null || supermarketAPI == null) return null;

        String selected = aisleComboBox.getValue();
        if (selected == null || selected.equals("Select an aisle...")) return null;

        String aisleName = selected.split("\\s+\\(")[0];
        return aisleManager.findAisle(aisleName);
    }


    private Aisle getSelectedAisleForShelf() {
        if (shelfAisleComboBox == null || supermarketAPI == null) return null;

        String selected = shelfAisleComboBox.getValue();
        if (selected == null || selected.equals("Select an aisle...")) return null;

        return aisleManager.findAisle(selected);
    }


    /**
     * GET STOCK BY - temperature, price range, low stock
     */

    private String getStockByTemperature() {
        if (supermarketAPI == null) return "System not initialized.";

        StockViewer stockViewer = new StockViewer(supermarketAPI);
        return stockViewer.getStockByTemperature();
    }

    private String getStockByPriceRange() {
        if (supermarketAPI == null) return "System not initialized.";

        StockViewer stockViewer = new StockViewer(supermarketAPI);
        return stockViewer.getStockByPriceRange();
    }

    private String getLowStockReport() {
        if (supermarketAPI == null) return "System not initialized.";

        StockViewer stockViewer = new StockViewer(supermarketAPI);
        return stockViewer.getLowStockReport();
    }

    private String getStockDistribution() {
        StringBuilder distribution = new StringBuilder();
        distribution.append("=== STOCK DISTRIBUTION ANALYSIS ===\n\n");

        distribution.append(getStockByTemperature()).append("\n");
        distribution.append(getStockByPriceRange()).append("\n");
        distribution.append(getLowStockReport());

        return distribution.toString();
    }

    /**
     * MENU DISPLAY - show stock, overall summary, by floor, aisle, shelf
     */
    private void showStockInDialog(String title, String content) {
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
    }

    private String getOverallStockSummary() {
        if (supermarketAPI == null) return "System not initialized.";

        StringBuilder summary = new StringBuilder();
        summary.append("OVERALL STOCK");

        summary.append("Total Floor Areas: ").append(supermarketAPI.getFloorAreaCount()).append("\n");
        summary.append("Total Aisles: ").append(supermarketAPI.getAisleCount()).append("\n");
        summary.append("Total Shelves: ").append(supermarketAPI.getShelfCount()).append("\n");
        summary.append("Unique Items: ").append(supermarketAPI.getItemCount()).append("\n");
        summary.append("Total Quantity: ").append(supermarketAPI.getTotalInvQty()).append(" units\n");
        summary.append("Total Value: €").append(String.format("%.2f", supermarketAPI.getTotalInvValue())).append("\n\n");

        // Temperature breakdown
        summary.append("Temperature Breakdown:\n");
        summary.append("  Frozen: ").append(supermarketAPI.getFrozenItems()).append(" items\n");
        summary.append("  Refrigerated: ").append(supermarketAPI.getRefrigeratedItems()).append(" items\n");
        summary.append("  Unrefrigerated: ").append(supermarketAPI.getUnrefrigeratedItems()).append(" items\n");

        return summary.toString();
    }

    private String getFloorStockStatistics() {
        if (floorAreaManager == null) return "Floor Area Manager not initialized.";
        return floorAreaManager.getFloorAreaStatistics();
    }

    private String getAisleStockStatistics() {
        if (aisleManager == null) return "Aisle Manager not initialized.";
        return aisleManager.getAisleStatistics();
    }

    private String getShelfStockStatistics() {
        if (shelfManager == null) return "Shelf Manager not initialized.";
        return shelfManager.getShelfStatistics();
    }

    private String getItemStockStatistics() {
        if (goodItemManager == null) return "Item Manager not initialized.";
        return goodItemManager.getItemStatistics();
    }

    private String getFullStockReport() {
        if (supermarketAPI == null) return "System not initialized.";

        StringBuilder report = new StringBuilder();
        report.append("FULL REPORT\n\n");
        report.append(getOverallStockSummary()).append("\n");

        report.append("DETAILED STAT\n\n");
        report.append(getFloorStockStatistics()).append("\n");
        report.append(getAisleStockStatistics()).append("\n");
        report.append(getShelfStockStatistics()).append("\n");
        report.append(getItemStockStatistics()).append("\n");

        report.append("Stock Value by Floor Area:\n");
        double totalValue = supermarketAPI.getTotalInvValue();
        supermarketAPI.getFloorAreas().forEach(area -> {
            if (area != null) {
                double areaValue = floorAreaManager.getTotalValueInFloorAreas();
                double pct = totalValue > 0 ? (areaValue / totalValue * 100) : 0;
                report.append("  ").append(area.getTitle())
                        .append(": €").append(InputVal.twoDecPlaces(areaValue))
                        .append(" (").append(InputVal.twoDecPlaces(pct)).append("%)\n");
            }
        });
        return report.toString();
    }

    /**
     * RESET HANDLER
     */
    private void handleReset() {
        if (supermarketAPI == null) {
            showAlert("Error", "System not initialized. Cannot reset.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Reset");
        confirmAlert.setHeaderText("Reset System Data");
        confirmAlert.setContentText("Are you sure you want to reset ALL system data?\n\n" +
                "This will delete:\n" +
                "• All items (" + supermarketAPI.getItemCount() + " items)\n" +
                "• All shelves (" + supermarketAPI.getShelfCount() + " shelves)\n" +
                "• All aisles (" + supermarketAPI.getAisleCount() + " aisles)\n" +
                "• All floor areas (" + supermarketAPI.getFloorAreaCount() + " areas)\n\n" +
                "This action cannot be undone!");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                supermarketAPI.clearAllData();

                floorAreaManager = new FloorAreaManager(supermarketAPI);
                aisleManager = new AisleManager(supermarketAPI);
                shelfManager = new ShelfManager(supermarketAPI);
                goodItemManager = new GoodItemManager(supermarketAPI);

                clearItemForm();
                clearFloorForm();
                clearAisleForm();
                clearShelfForm();
                refreshTableData();
                loadComboBoxData();

                showAlert("Success", "System reset.\n\n");

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to reset system: " + e.getMessage());
            }
        }
    }

    public void setMapViewOpener(FunkierList.MapViewOpener opener) {
        this.mapViewOpener = opener;
    }

    private void handleMapView() {
        if (mapViewOpener != null) {
            Stage menuStage = (Stage) mapViewButton.getScene().getWindow();
            mapViewOpener.openMapView(menuStage);
        }
    }

    private void handleExit() {
        System.out.println("Exit clicked");
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}