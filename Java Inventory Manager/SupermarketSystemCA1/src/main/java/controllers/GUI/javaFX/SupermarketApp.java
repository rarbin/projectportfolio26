package controllers.GUI.javaFX;

import controllers.API.SupermarketAPI;
import controllers.managers.*;
import utils.Persistence;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.File;

public class SupermarketApp extends Application {

    private SupermarketAPI supermarketAPI;
    private FloorAreaManager floorAreaManager;
    private AisleManager aisleManager;
    private ShelfManager shelfManager;
    private GoodItemManager goodItemManager;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        System.out.println("Starting Supermarket Application...");
        supermarketAPI = new SupermarketAPI();
        initializeManagers();
        loadData();

        openMenuView();
    }

    private void initializeManagers() {
        floorAreaManager = new FloorAreaManager(supermarketAPI);
        aisleManager = new AisleManager(supermarketAPI);
        shelfManager = new ShelfManager(supermarketAPI);
        goodItemManager = new GoodItemManager(supermarketAPI);
        System.out.println("Managers initialized");
    }

    private void loadData() {
        try {
            new Persistence(supermarketAPI).loadAllData();
            System.out.println("Default data loaded successfully");
        } catch (Exception e) {
            System.out.println("Could not load default data: " + e.getMessage());
        }
    }


    public void openMapView() {
        try {
            String fxmlPath = "src/main/java/controllers/GUI/javaFX/InteractiveGUI.fxml";
            File fxmlFile = new File(fxmlPath);

            if (!fxmlFile.exists()) {
                showError("FXML file not found at: " + fxmlFile.getAbsolutePath());
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
            Scene scene = new Scene(loader.load(), 1200, 800);

            MapController controller = loader.getController();
            controller.initializeWithSystem(supermarketAPI);

            Stage mapStage = new Stage();
            mapStage.setTitle("Supermarket Interactive Map");
            mapStage.setScene(scene);
            mapStage.setMaximized(true);
            mapStage.setOnCloseRequest(e -> {
                mapStage.close();
            });

            mapStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open map view: " + e.getMessage());
        }
    }

    private void openMenuView() {
        try {
            String fxmlPath = "src/main/java/controllers/GUI/javaFX/MenuGUI.fxml";
            File fxmlFile = new File(fxmlPath);

            if (!fxmlFile.exists()) {
                fxmlPath = "MenuGUI.fxml";
                fxmlFile = new File(fxmlPath);
            }

            if (!fxmlFile.exists()) {
                showError("File not found.");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
            Scene scene = new Scene(loader.load(), 900, 600);

            MenuController controller = loader.getController();
            controller.setSystemComponents(supermarketAPI, floorAreaManager, aisleManager, shelfManager, goodItemManager);
            controller.setMapViewOpener(stage -> openMapView());
            primaryStage.setTitle("Supermarket Inventory Menu");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open menu view: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        System.out.println("Launching Supermarket Application...");
        launch(args);
    }
}