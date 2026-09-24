package controllers.GUI.javaFX;

import controllers.API.ElectionSystemAPI;
import controllers.managers.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;

public class ElectionApp extends Application {
    private ElectionSystemAPI electionSystemAPI;
    private PoliticianManager politicianManager;
    private ElectionManager electionManager;
    private CandidateManager candidateManager;
    private Stage primaryStage;

    private static final String BINARY_SAVE_FILE = "election_hash.dat";
    private static final String CSV_DATA_FILE = "election.csv";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        new Thread(() -> {
            try {
                electionSystemAPI = new ElectionSystemAPI();
                initializeManagers();
                loadInitialData();

                Platform.runLater(() -> {
                    try {
                        openMenuView();
                    } catch (Exception e) {
                        e.printStackTrace();
                        showError("Failed to open main window: " + e.getMessage());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() ->
                        showError("Initialization failed: " + e.getMessage()));
            }
        }).start();
    }


    private void initializeManagers() {
        electionSystemAPI = new ElectionSystemAPI();
        electionManager = new ElectionManager(electionSystemAPI);
        politicianManager = new PoliticianManager(electionSystemAPI, electionManager);
    }

    private void loadInitialData() {
        try {
            File binaryFile = new File(BINARY_SAVE_FILE);
            if (binaryFile.exists()) {
                boolean loaded = electionSystemAPI.loadData(BINARY_SAVE_FILE);
                if (loaded && electionSystemAPI.getCandidateCount() > 0) {
                    return;
                }
            }

            File csvFile = new File(CSV_DATA_FILE);
            if (csvFile.exists()) {
                boolean loaded = electionSystemAPI.loadData(CSV_DATA_FILE);
                if (loaded && electionSystemAPI.getCandidateCount() > 0) {
                    electionSystemAPI.saveData(BINARY_SAVE_FILE);
                }
            }

        } catch (Exception e) {
            System.err.println("ERROR loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openMenuView() {
        try {
            String fxmlPath = "src/main/java/controllers/GUI/javaFX/ElectionMenuGUI.fxml";
            File fxmlFile = new File(fxmlPath);

            if (!fxmlFile.exists()) {
                showError("FXML file not found at:\n" + fxmlFile.getAbsolutePath());
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
            Pane root = loader.load();

            ElectionMenuController controller = loader.getController();
            if (controller != null) {
                controller.setSystemComponents(electionSystemAPI, politicianManager,
                        electionManager, candidateManager);
                controller.setMapViewOpener(stage -> openMapView());
            } else {
                showError("Failed to load controller");
                return;
            }

            Scene scene = new Scene(root, 900, 650);

            primaryStage.setTitle("Election Management System");
            primaryStage.setScene(scene);
            primaryStage.setX(100);
            primaryStage.setY(100);
            primaryStage.setIconified(false);
            primaryStage.show();
            primaryStage.toFront();

        } catch (Exception e) {
            System.err.println("FAILED TO OPEN MAIN WINDOW:");
            e.printStackTrace();
            showError("Failed to open main interface:\n" + e.getMessage());
        }
    }

    public void openMapView() {
        try {
            String fxmlPath = "src/main/java/controllers/GUI/javaFX/ElectionMapGUI.fxml";
            File fxmlFile = new File(fxmlPath);

            if (!fxmlFile.exists()) {
                showError("Map FXML not found");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
            Pane root = loader.load();
            Scene scene = new Scene(root, 2000, 500);

            ElectionMapController controller = loader.getController();
            if (controller != null) {
                controller.initializeWithSystem(electionSystemAPI);
            }

            Stage mapStage = new Stage();
            mapStage.setTitle("Election System Interactive Map");
            mapStage.setScene(scene);
            mapStage.setMaximized(true);
            mapStage.show();

        } catch (Exception e) {
            showError("Failed to open map view: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @Override
    public void stop() {
        try {
            if (electionSystemAPI != null) {
                electionSystemAPI.saveData(BINARY_SAVE_FILE);
            }
        } catch (Exception e) {
            System.out.println("ERROR saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public interface MapViewOpener {
        void openMapView(Stage currentStage);
    }
}