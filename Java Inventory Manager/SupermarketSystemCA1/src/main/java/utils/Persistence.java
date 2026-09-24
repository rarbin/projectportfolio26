package utils;

import controllers.API.SupermarketAPI;
import models.*;
import utils.CustomList.FunkierList;
import utils.Validators.ScannerInput;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

import java.io.*;

/**
 * Persistence Class
 * handles load/save/export/import
 */
public class Persistence {
    private final SupermarketAPI system;
    private File file = new File("supermarket_data.xml");

    public Persistence(SupermarketAPI system) {
        this.system = system;
    }


    //getters
    public String getFileName() {
        return file.getPath();
    }

    /**
     * Keep all system data in one static class
     */
    private static class DataWrapper {
        public FunkierList<FloorArea> floorAreas;
        public FunkierList<Aisle> aisles;
        public FunkierList<Shelf> shelves;
        public FunkierList<GoodItem> goodItems;

        //initialiser
        public DataWrapper() {
        }

        public DataWrapper(FunkierList<FloorArea> floorAreas, FunkierList<Aisle> aisles, FunkierList<Shelf> shelves, FunkierList<GoodItem> goodItems) {
            this.floorAreas = floorAreas;
            this.aisles = aisles;
            this.shelves = shelves;
            this.goodItems = goodItems;
        }
    }
    public void saveData(String filePath) throws Exception {
        DataWrapper wrapper = new DataWrapper(system.getFloorAreas(), system.getAisles(), system.getShelves(), system.getGoodItems());
        var xstream = setupXStream();
        xstream.alias("dataWrapper", DataWrapper.class);

        try (FileWriter writer = new FileWriter(filePath)) {
            xstream.toXML(wrapper, writer);
        }
    }
    public void loadData(String filePath) throws Exception {
        File loadFile = new File(filePath);
        if (!loadFile.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        var xstream = setupXStream();
        xstream.alias("dataWrapper", DataWrapper.class);

        try (FileReader reader = new FileReader(loadFile)) {
            DataWrapper wrapper = (DataWrapper) xstream.fromXML(reader);
            system.clearAllData();
            copyData(wrapper);
        }
    }
    /**
     * Save all system data to xml file
     *
     * @throws Exception in case of error saving
     */
    public void saveAllData() throws Exception {
        DataWrapper wrapper = new DataWrapper(system.getFloorAreas(), system.getAisles(), system.getShelves(), system.getGoodItems());
        var xstream = setupXStream();
        //call xml tag identifiably for xstream
        xstream.alias("dataWrapper", DataWrapper.class);
        try (FileWriter writer = new FileWriter(file)) {
            xstream.toXML(wrapper, writer);
        }
        System.out.println("Data saved to " + getFileName());
    }

    /**
     * Load all system data from xml file
     *
     * @throws Exception in case of error saving
     */
    public void loadAllData() throws Exception {
        if (!file.exists()) {
            System.out.println("File not found: " + getFileName());
            return;
        }
        var xstream = setupXStream();
        //know where to match tag from
        xstream.alias("dataWrapper", DataWrapper.class);

        try (FileReader reader = new FileReader(file)) {
            DataWrapper wrapper = (DataWrapper) xstream.fromXML(reader);
            system.clearAllData();
            copyData(wrapper);
            printLoadStats();
        }
        System.out.println("Data loaded from " + getFileName());
    }

    /**
     * Create new XStream instance with any permissions to avoid conflicts
     *
     * @return configured xstream instance
     */
    private XStream setupXStream() {
        var xstream = new XStream(new DomDriver());
        xstream.addPermission(AnyTypePermission.ANY);
        return xstream;
    }

    /**
     * Copy all data from wrapper to active list
     *
     * @param wrapper existing data to load
     */

    private void copyData(DataWrapper wrapper) {
        if (wrapper == null) return;

        if (wrapper.floorAreas != null) system.getFloorAreas().addAll(wrapper.floorAreas);
        if (wrapper.aisles != null) system.getAisles().addAll(wrapper.aisles);
        if (wrapper.shelves != null) system.getShelves().addAll(wrapper.shelves);
        if (wrapper.goodItems != null) system.getGoodItems().addAll(wrapper.goodItems);
    }


    /**
     * Confirm to user what has been loaded
     */
    private void printLoadStats() {
        System.out.println("Loaded: " + system.getFloorAreas().getSize() + " floors, " + system.getAisles().getSize() + " aisles, " + system.getShelves().getSize() + " shelves, " + system.getGoodItems().getSize() + " items");
    }
}