package view;

import controllers.API.SupermarketAPI;
import controllers.GUI.javaFX.SupermarketApp;
import controllers.managers.*;
import models.*;
import utils.CustomList.FunkierList;
import utils.Dimensions;
import utils.Persistence;
import utils.Validators.InputVal;
import utils.Validators.ScannerInput;
import utils.Validators.TempValidator;

import java.util.Scanner;

public class Driver {
    private final SupermarketAPI system;
    private final Scanner scanner;
    private FloorAreaManager areaManager;
    private AisleManager aisleManager;
    private ShelfManager shelfManager;
    private GoodItemManager itemManager;
    private SmartAddManager smartAddManager;
    private StockViewer stockViewer;
    private Persistence persistence;

    public Driver() {
        system = new SupermarketAPI();
        scanner = new Scanner(System.in);
        initializeManagers();
        runMainMenu();
    }

    public static void main(String[] args) {
        new Driver();
    }

    private void initializeManagers() {
        areaManager = new FloorAreaManager(system);
        aisleManager = new AisleManager(system);
        shelfManager = new ShelfManager(system);
        itemManager = new GoodItemManager(system);
        smartAddManager = new SmartAddManager(system);
        stockViewer = new StockViewer(system);
        persistence = new Persistence(system);
    }

    /**
     * MAIN CLI MENU
     * Redirects to submenus of appropriate function
     */
    private int mainMenu() {
        System.out.println("""
                -------CLI Supermarket System -------
                |  1) Create facilities             |
                |  2) View stock options            |
                |  3) Interactive GUI Map           |
                |  4) Search facilities             |
                |  5) List facilities               |
                |  6) Update facilities             |
                |  7) Remove facilities             |
                |  8) File options                  |
                -------------------------------------
                |  0) Exit                         |
                ------------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runMainMenu() {
        int option = mainMenu();
        while (option != 0) {
            switch (option) {
                case 1 -> runCreateMenu();
                case 2 -> runStockOptions();
                case 3 -> runInteractiveGUI();
                case 4 -> runSearchMenu();
                case 5 -> runListMenu();
                case 6 -> runUpdateMenu();
                case 7 -> runRemoveMenu();
                case 8 -> runFileOptions();
                //case 9 -> runJavaFX();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = mainMenu();
        }
        exitApp();
    }

    /**
     * CREATE MENU
     * Add entities to the system.
     */

    private int createMenu() {
        System.out.println("""
                ------------ Create Menu --------------
                |  1) Add floor                       |
                |  2) Add aisle                       |
                |  3) Add shelf                       |
                |  4) Add item                        |
                |  5) Smart add (item)                |
                |  0) Back to Main Menu               |
                -------------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runCreateMenu() {
        int option = createMenu();
        while (option != 0) {
            switch (option) {
                case 1 -> addFloorArea();
                case 2 -> addAisle();
                case 3 -> addShelf();
                case 4 -> addItem();
                case 5 -> smartAddItem();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = createMenu();
        }
    }

    /**
     * HELPER - Dimensions for reuse
     *
     * @return object: length, width, posX, posY and orientation
     */
    private Dimensions getDimensionsFromUser() {
        System.out.println("Enter dimensions:");
        double length = ScannerInput.readNextDouble("Length (m): ");
        double width = ScannerInput.readNextDouble("Width (m): ");
        double posX = ScannerInput.readNextDouble("Position X: ");
        double posY = ScannerInput.readNextDouble("Position Y: ");
        double orientation = ScannerInput.readNextDouble("Orientation (degrees): ");

        return new Dimensions(length, width, posX, posY, orientation);
    }

    /**
     * ADD - Floor Area, Aisle, Shelf, Item
     */

    //FLOOR
    private void addFloorArea() {
        String floorName = ScannerInput.readNextLine("Enter name of the floor area: ");
        int level = ScannerInput.readNextInt("Enter floor level: ");
        Dimensions dimensions = getDimensionsFromUser();
        boolean success = areaManager.addFloorArea(floorName, level, dimensions, new FunkierList<>());

        if (success) {
            System.out.println("Floor area added.");
        }
    }

    //AISLE
    private void addAisle() {
        System.out.println("Available floor areas:");
        System.out.println(areaManager.listFloorByIndex());

        if (system.getFloorAreas().isEmpty()) {
            System.out.println("No floor areas available. Please add a floor area first.");
            addFloorArea();
        }

        int floorIndex = ScannerInput.readNextInt("Enter floor area index: ");
        if (floorIndex < 0 || floorIndex >= system.getFloorAreas().getSize()) {
            System.out.println("Invalid floor area.");
            return;
        }

        FloorArea floorArea = system.getFloorAreas().get(floorIndex);
        String aisleName = ScannerInput.readNextLine("Enter aisle name: ");

        String temperature = null;
        while (temperature == null) {
            System.out.println(TempValidator.getCategoryMenu());
            int tempChoice = ScannerInput.readNextInt("Choice: ");
            temperature = TempValidator.getCategoryFromChoice(tempChoice);

            if (temperature == null) {
                System.out.println("Invalid choice.");
            }
        }
        Dimensions dimensions = getDimensionsFromUser();
        boolean success = aisleManager.addAisle(aisleName, temperature, dimensions, floorArea, new FunkierList<>());

        if (success) {
            System.out.println("Aisle added.");
        }
    }

    //SHELF
    private void addShelf() {
        System.out.println(aisleManager.listAllAisles());

        if (system.getAisles().isEmpty()) {
            int option = ScannerInput.readNextInt("No aisles available. 1) Create a new aisle 2) Cancel shelf creation");
            if (option == 1) {
                addAisle();
                if (system.getAisles().isEmpty()) {
                    System.out.println("Failed to create aisle.");
                    return;
                }
                System.out.println("Available aisles after creation:");
                System.out.println(aisleManager.listAllAisles());
            } else {
                System.out.println("Cancelled.");
                return;
            }
        }

        System.out.print("Enter aisle name: ");
        String aisleName = scanner.nextLine();

        Aisle aisle = aisleManager.findAisle(aisleName);
        if (aisle == null) {
            System.out.println("Aisle not found.");
            return;
        }
        double capacity = 0;
        while (capacity <= 0) {
            capacity = ScannerInput.readNextDouble("Enter shelf capacity (kg): ");
            if (capacity <= 0) {
                System.out.println("Capacity must be greater than 0.");
            }
        }
        boolean success = shelfManager.addShelf(capacity, 0.0, 0, aisle, new FunkierList<>());

        if (success) {
            System.out.println("Shelf added to aisle '" + aisleName);
        }
    }

    //ITEM
    private void addItem() {
        System.out.println(aisleManager.listAllAisles());

        String aisleName = ScannerInput.readNextLine("Enter aisle name: ");
        Aisle aisle = aisleManager.findAisle(aisleName);

        if (aisle == null) {
            System.out.println("Aisle not found.");
            return;
        }

        String desc = ScannerInput.readNextLine("Enter item description: ");
        double unitSize = ScannerInput.readNextDouble("Enter unit size in grams: ");
        double unitPrice = ScannerInput.readNextDouble("Enter unit price: ");
        int quantity = ScannerInput.readNextInt("Enter quantity: ");
        double storageTemp = ScannerInput.readNextDouble("Enter storage temperature (C): ");
        String photoUrl = ScannerInput.readNextLine("Enter photo URL (optional): ");

        // shelf currently unassigned
        boolean success = itemManager.addGoodItem(desc, unitSize, unitPrice, quantity, storageTemp, photoUrl, aisle, new FunkierList<>());

        if (success) {
            System.out.print("Would you like to add this item to a shelf? (Y/N): ");
            char response = ScannerInput.readNextChar("Enter Y/N: ");
            if (ScannerInput.YNtoBoolean(response)) {
                addItemToShelf(desc);
            }
        }
    }

    //ITEM TO SHELF
    private void addItemToShelf(String itemDesc) {
        System.out.println(itemManager.listItemDesc());

        if (system.getGoodItems().isEmpty()) {
            System.out.println("No items available to add to shelf. Please add items first.");
            return;
        }
        int shelfNumber = ScannerInput.readNextInt("Enter shelf number: ");
        Shelf shelf = shelfManager.findShelfByNumber(shelfNumber);
        if (shelf == null) {
            System.out.println("Shelf not found.");
            return;
        }
        FunkierList<GoodItem> foundItems = itemManager.findItemsByDesc(itemDesc);
        if (foundItems.isEmpty()) {
            System.out.println("Item not found.");
            return;
        }
        GoodItem item = foundItems.get(0);
        if (item == null) {
            System.out.println("Item not found.");
            return;
        }
        boolean added = shelfManager.addItemToShelf(shelfNumber, item);
        if (added) {
            System.out.println("Item '" + item.getDesc() + "' added to shelf #" + shelfNumber);
        }
    }

    //SMART ADD
    private void smartAddItem() {
        String description = ScannerInput.readNextLine("Enter item description: ");
        double unitSize = ScannerInput.readNextDouble("Enter unit size in grams: ");
        double unitPrice = ScannerInput.readNextDouble("Enter unit price: ");
        int quantity = ScannerInput.readNextInt("Enter quantity: ");
        double storageTemp = ScannerInput.readNextDouble("Enter storage temperature (Celsius): ");
        String photoUrl = ScannerInput.readNextLine("Enter photo URL (optional): ");

        GoodItem tempItem = new GoodItem(description, unitSize, unitPrice, quantity, storageTemp, photoUrl, null, new FunkierList<>());
        SmartAdd result = smartAddManager.smartAddItem(tempItem);
        System.out.println(result);
    }

    /**
     * STOCK MENU
     */

    private int stockOptionsMenu() {
        System.out.println("""
                ------------ Stock Menu -------------
                |  1) View stock summary            |
                |  2) View specific floor           |
                |  3) View specific aisle           |
                |  4) View specific shelf           |
                |  5) View specific items           |
                |  6) View a shelf's items          |
                |  7) Stock Full Report             |
                |  0) Back to Main Menu             |
                -------------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runStockOptions() {
        int option = stockOptionsMenu();
        while (option != 0) {
            switch (option) {
                case 1 -> viewStockSummary();
                case 2 -> displayFloorAreaStock();
                case 3 -> displayAisleStock();
                case 4 -> displayShelfStock();
                case 5 -> displayItemStock();
                case 6 -> displayShelfItemStock();
                case 7 -> stockReport();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = stockOptionsMenu();
        }
    }

    /**
     * VIEW STOCK - Summary, floor area, aisles, shelves, items, shelf items, full report
     */

    private void viewStockSummary() {
        stockViewer.stockReport();
    }

    //FLOOR
    private void displayFloorAreaStock() {
        if (system.getFloorAreas().isEmpty()) {
            System.out.println("No floor areas available.");
            return;
        }
        System.out.println("Available floor areas:");
        System.out.println(areaManager.listFloorByIndex());

        int choice = ScannerInput.readNextInt("Select floor area (index): ");
        if (choice < 0 || choice >= system.getFloorAreas().getSize()) {
            System.out.println("Invalid selection.");
            return;
        }
        FloorArea selectedArea = system.getFloorAreas().get(choice);
        stockViewer.dispOneFloor(selectedArea);
    }

    //AISLE
    private void displayAisleStock() {
        if (system.getAisles().isEmpty()) {
            System.out.println("No aisles available.");
            return;
        }

        System.out.println("Available aisles:");
        System.out.println(aisleManager.listAllAisles());

        int choice = ScannerInput.readNextInt("Select aisle (index): ");
        if (choice < 0 || choice >= system.getAisles().getSize()) {
            System.out.println("Invalid selection.");
            return;
        }
        Aisle selectedAisle = system.getAisles().get(choice);
        stockViewer.dispOneAisle(selectedAisle);
    }

    //SHELF
    private void displayShelfStock() {
        if (system.getShelves().isEmpty()) {
            System.out.println("No shelves available.");
            return;
        }

        System.out.println("Available shelves:");
        System.out.println(shelfManager.listShelfNumbers());

        int choice = ScannerInput.readNextInt("Select shelf (shelf number): ");

        Shelf selectedShelf = shelfManager.findShelfByNumber(choice);
        if (selectedShelf == null) {
            System.out.println("Shelf #" + choice + " not found.");
            return;
        }
        stockViewer.dispOneShelf(selectedShelf);
    }

    //ITEM
    private void displayItemStock() {
        if (system.getGoodItems().isEmpty()) {
            System.out.println("No items available.");
            return;
        }

        System.out.println("Available items:");
        System.out.println(itemManager.listItemDesc());


        int itemIndex = ScannerInput.readNextInt("Enter item index: ");
        if (itemIndex < 0 || itemIndex >= system.getGoodItems().getSize()) {
            System.out.println("Invalid item index.");
            return;
        }
        GoodItem selectedItem = system.getGoodItems().get(itemIndex);
        stockViewer.dispOneItem(selectedItem);
    }

    //SHELF ITEM
    private void displayShelfItemStock() {
        if (system.getShelves().isEmpty()) {
            System.out.println("No shelves available. Please add a shelf first.");
            return;
        }

        int shelfNumber = ScannerInput.readNextInt("Enter shelf number: ");

        Shelf shelf = shelfManager.findShelfByNumber(shelfNumber);

        if (shelf == null) {
            System.out.println("Shelf #" + shelfNumber + " not found.");
            return;
        }

        stockViewer.dispOneShelf(shelf);
    }

    //FULL REPORT
    private void stockReport() {
        stockViewer.stockReport();
    }

    /**
     * JAVAFX GUI MAP
     */
    private void runInteractiveGUI() {
        SupermarketApp app = new SupermarketApp();
        app.openMapView();
    }

    /**
     * SEARCH MENU
     */
    private int searchMenu() {
        System.out.println("""
                ------------- Search Menu ------------
                |  1) Search items                  |
                |  2) Search shelves                |
                |  3) Search aisles                 |
                |  4) Search floors                 |
                |  0) Back to Main Menu             |
                -------------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runSearchMenu() {
        int option = searchMenu();
        while (option != 0) {
            switch (option) {
                case 1 -> runItemSearch();
                case 2 -> runShelfSearch();
                case 3 -> runAisleSearch();
                case 4 -> runFloorSearch();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = searchMenu();
        }
    }

    /**
     * SEARCH ITEMS
     */
    private int searchItemMenu() {
        System.out.println("""
                ------------- Search Items ------------
                |  1) By name                         |
                |  2) By aisle                        |
                |  3) By shelf                        |
                |  4) By price range                  |
                |  5) By quantity range               |
                |  6) By temperature                  |
                |  7) Stock lower than x              |
                |  0) Back to Main Menu               |
                -------------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runItemSearch() {
        int option = searchItemMenu();
        while (option != 0) {
            switch (option) {
                case 1 -> searchItemsByName();
                case 2 -> searchItemsByAisle();
                case 3 -> searchItemsByShelf();
                case 4 -> searchItemsByPriceRange();
                case 5 -> searchItemsByQtyRange();
                case 6 -> searchItemsByTemp();
                case 7 -> searchLowStockItems();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = searchItemMenu();
        }
    }

    /**
     * SEARCH ITEMS BY - name, aisle, shelf, price range, quantity range, temperature, low stock
     */

    //NAME
    private void searchItemsByName() {
        System.out.print("Enter item name to search: ");
        String itemName = scanner.nextLine();

        FunkierList<GoodItem> results = itemManager.listItemsByDesc(itemName);
        System.out.println("Found " + results.getSize() + " items:");

        results.forEach(item -> {
            if (item != null) {
                System.out.println(itemManager.getItemLocation(item));
            }
        });
    }

    //AISLE
    private void searchItemsByAisle() {
        System.out.print("Enter aisle name: ");
        String aisleName = scanner.nextLine();

        Aisle aisle = aisleManager.findAisle(aisleName);
        if (aisle == null) {
            System.out.println("Aisle not found.");
            return;
        }

        FunkierList<GoodItem> items = itemManager.listItemsByAisle(aisle);
        System.out.println("Found " + items.getSize() + " items in aisle " + aisleName + ":");

        items.forEach(item -> {
            if (item != null) {
                System.out.println("  - " + item.getDesc() + " (Qty: " + item.getQty() + ", Price: €" + item.getPrice() + ")");
            }
        });
    }

    //SHELF
    private void searchItemsByShelf() {
        int shelfNumber = ScannerInput.readNextInt("Enter shelf number: ");

        Shelf shelf = shelfManager.findShelfByNumber(shelfNumber);
        if (shelf == null) {
            System.out.println("Shelf not found.");
            return;
        }

        FunkierList<GoodItem> items = itemManager.findItemsByShelf(shelf);
        System.out.println("Found " + items.getSize() + " items on shelf #" + shelfNumber + ":");

        items.forEach(item -> {
            if (item != null) {
                System.out.println("  - " + item);
            }
        });
    }

    //PRICE RANGE
    private void searchItemsByPriceRange() {
        double minPrice = ScannerInput.readNextDouble("Enter minimum price: €");
        double maxPrice = ScannerInput.readNextDouble("Enter maximum price: €");

        if (minPrice > maxPrice) {
            System.out.println("Minimum price cannot be greater than maximum.");
            return;
        }

        FunkierList<GoodItem> items = itemManager.getItemsByPriceRange(minPrice, maxPrice);
        System.out.println("Found " + items.getSize() + " items in price range €" + minPrice + " - €" + maxPrice + ":");

        items.forEach(item -> {
            if (item != null) {
                System.out.println("  - " + item.getDesc() + " (Price: €" + item.getPrice() + ", Qty: " + item.getQty() + ", Total Value: €" + item.getTotalValue() + ")");
            }
        });
    }

    //QUANTITY RANGE
    private void searchItemsByQtyRange() {
        int minQty = ScannerInput.readNextInt("Enter minimum quantity: ");
        int maxQty = ScannerInput.readNextInt("Enter maximum quantity: ");

        if (minQty > maxQty) {
            System.out.println("Minimum quantity cannot be greater than maximum.");
            return;
        }

        FunkierList<GoodItem> items = itemManager.getItemsByQuantityRange(minQty, maxQty);
        System.out.println("Found " + items.getSize() + " items in quantity range " + minQty + " - " + maxQty + ":");

        items.forEach(item -> {
            if (item != null) {
                System.out.println("  - " + item.getDesc() + " (Qty: " + item.getQty() + ", Price: €" + item.getPrice() + ", Location: " + itemManager.getItemLocation(item) + ")");
            }
        });
    }

    //TEMPERATURE
    private void searchItemsByTemp() {
        System.out.println(TempValidator.getCategoryMenu());
        int tempChoice = ScannerInput.readNextInt("Select temperature category: ");

        String temperature = TempValidator.getCategoryFromChoice(tempChoice);
        if (temperature == null) {
            System.out.println("Invalid temperature choice.");
            return;
        }

        FunkierList<GoodItem> items = system.getItemsByTemp(temperature);
        System.out.println("Found " + items.getSize() + " items in " + temperature + " category:");

        items.forEach(item -> {
            if (item != null) {
                System.out.println("  - " + item.getDesc() + " (Qty: " + item.getQty() + ", Temp: " + item.getStorageTemp() + "°C" + ", Location: " + itemManager.getItemLocation(item) + ")");
            }
        });
    }

    //LOW STOCK
    private void searchLowStockItems() {
        int threshold = ScannerInput.readNextInt("Enter low stock threshold (minimum quantity): ");

        FunkierList<GoodItem> items = itemManager.getLowStockItems(threshold);
        System.out.println("Found " + items.getSize() + " items with stock below " + threshold + ":");

        if (items.isEmpty()) {
            System.out.println("No low stock items found.");
            return;
        }

        items.forEach(item -> {
            if (item != null) {
                System.out.println("  - " + item.getDesc() + " (Current Qty: " + item.getQty() + ", Threshold: " + threshold + ", Location: " + itemManager.getItemLocation(item) + ")");
            }
        });
    }


    private int searchShelfMenu() {
        System.out.println("""
                ------------- Search Shelves ------------
                |  1) By shelf number                   |
                |  2) By aisle                          |
                |  3) By capacity range                 |
                |  0) Back to Search Menu               |
                ----------------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runShelfSearch() {
        int option = searchShelfMenu();
        while (option != 0) {
            switch (option) {
                case 1 -> searchShelfByNumber();
                case 2 -> searchShelvesByAisle();
                case 3 -> searchShelvesByCapacity();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = searchShelfMenu();
        }
    }

    /**
     * SEARCH SHELF BY - number, aisle, capacity range
     */

// NUMBER
    private void searchShelfByNumber() {
        int shelfNumber = ScannerInput.readNextInt("Enter shelf number: ");

        Shelf shelf = shelfManager.findShelfByNumber(shelfNumber);
        if (shelf == null) {
            System.out.println("Shelf #" + shelfNumber + " not found.");
            return;
        }

        stockViewer.dispOneShelf(shelf);
    }

    // AISLE
    private void searchShelvesByAisle() {
        System.out.print("Enter aisle name: ");
        String aisleName = scanner.nextLine();

        Aisle aisle = aisleManager.findAisle(aisleName);
        if (aisle == null) {
            System.out.println("Aisle not found.");
            return;
        }

        FunkierList<Shelf> shelves = shelfManager.findShelvesByAisle(aisle);
        if (shelves.isEmpty()) {
            System.out.println("No shelves found in aisle: " + aisleName);
            return;
        }

        System.out.println("\nShelves in aisle '" + aisleName + "':");
        shelves.forEach(shelf -> {
            if (shelf != null) {
                System.out.println("Shelf #" + shelf.getShelfNumber() + " - Capacity: " + InputVal.twoDecPlaces(shelf.getCapacity()) + "kg" + " - Used: " + InputVal.twoDecPlaces(shelf.getCurrentWeight()) + "kg" + " - Items: " + shelf.getItems().getSize());
            }
        });

        System.out.print("\nWould you like to view details of a specific shelf? (Y/N): ");
        String response = scanner.nextLine();
        if (!response.isEmpty() && ScannerInput.YNtoBoolean(response.charAt(0))) {
            int shelfNum = ScannerInput.readNextInt("Enter shelf number to view: ");
            Shelf selectedShelf = shelfManager.findShelfByNumber(shelfNum);
            if (selectedShelf != null) {
                stockViewer.dispOneShelf(selectedShelf);
            }
        }
    }

    //CAPACITY RANGE
    private void searchShelvesByCapacity() {
        double minCapacity = ScannerInput.readNextDouble("Enter minimum capacity (kg): ");
        double maxCapacity = ScannerInput.readNextDouble("Enter maximum capacity (kg): ");

        if (minCapacity > maxCapacity) {
            System.out.println("Minimum capacity cannot be greater than maximum.");
            return;
        }

        FunkierList<Shelf> allShelves = system.getShelves();
        FunkierList<Shelf> matchingShelves = allShelves.findAll(shelf -> shelf != null && shelf.getCapacity() >= minCapacity && shelf.getCapacity() <= maxCapacity);

        if (matchingShelves.isEmpty()) {
            System.out.println("No shelves found with capacity between " + minCapacity + "kg and " + maxCapacity + "kg");
            return;
        }

        System.out.println("\nFound " + matchingShelves.getSize() + " shelf(es) with capacity between " + minCapacity + "kg and " + maxCapacity + "kg:");

        matchingShelves.forEach(shelf -> {
            if (shelf != null) {
                double usagePercent = shelf.getCapacity() > 0 ? (shelf.getCurrentWeight() / shelf.getCapacity() * 100) : 0;
                System.out.println("Shelf #" + shelf.getShelfNumber() + " - Capacity: " + InputVal.twoDecPlaces(shelf.getCapacity()) + "kg" + " - Used: " + InputVal.twoDecPlaces(shelf.getCurrentWeight()) + "kg (" + InputVal.twoDecPlaces(usagePercent) + "%)" + " - Aisle: " + (shelf.getAisle() != null ? shelf.getAisle().getName() : "None"));
            }
        });
    }


    /**
     * SEARCH AISLE
     */

    private int searchAisleMenu() {
        System.out.println("""
                -------- Aisle Search ------------
                |  1) By floor area              |
                |  2) By name                    |
                |  3) By temperature             |
                |  0) Back to Main Menu          |
                -------------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runAisleSearch() {
        int option = searchAisleMenu();
        while (option != 0) {
            switch (option) {
                case 1 -> searchAislesByFloorArea();
                case 2 -> searchAislesByName();
                case 3 -> searchAislesByTemperature();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = searchAisleMenu();
        }
    }

    /**
     * SEARCH AISLE BY - floor area, name, temperature
     */

    //FLOOR AREA
    private void searchAislesByFloorArea() {
        System.out.print("Enter floor area name: ");
        String floorAreaName = scanner.nextLine();

        FunkierList<Aisle> aisles = aisleManager.findAislesByFloorArea(floorAreaName);

        if (aisles.isEmpty()) {
            System.out.println("No aisles found in floor area: " + floorAreaName);
            return;
        }

        System.out.println("\nAisles in floor area '" + floorAreaName + "':");
        aisles.forEach(aisle -> {
            if (aisle != null) {
                System.out.println("  - " + aisle.getName() + " [" + aisle.getTemp() + ", Shelves: " + aisle.getShelves().getSize() + "]");
            }
        });
    }

    //NAME
    private void searchAislesByName() {
        System.out.print("Enter aisle name to search: ");
        String searchName = scanner.nextLine().toLowerCase();

        FunkierList<Aisle> allAisles = system.getAisles();
        FunkierList<Aisle> matchingAisles = allAisles.findAll(aisle -> aisle != null && aisle.getName().toLowerCase().contains(searchName));

        if (matchingAisles.isEmpty()) {
            System.out.println("No aisles found matching '" + searchName + "'");
            return;
        }
        System.out.println("\nFound " + matchingAisles.getSize() + " aisle(s) matching '" + searchName + "':");
        matchingAisles.forEach(aisle -> {
            if (aisle != null) {
                System.out.println("  - " + aisle.getName() + " (Floor: " + aisle.getFloorArea().getTitle() + ", Temp: " + aisle.getTemp() + ", Shelves: " + aisle.getShelves().getSize() + ")");
            }
        });
    }

    //TEMPERATURE
    private void searchAislesByTemperature() {
        int choice = ScannerInput.readNextInt("Select temperature category: \n 1) UNREFRIGERATED \n 2) REFRIGERATED \n 3) FROZEN");
        String temperature;
        switch (choice) {
            case 1 -> temperature = "UNREFRIGERATED";
            case 2 -> temperature = "REFRIGERATED";
            case 3 -> temperature = "FROZEN";
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }

        FunkierList<Aisle> aisles = aisleManager.findAislesByTemp(temperature);

        if (aisles.isEmpty()) {
            System.out.println("\nNo aisles found with " + temperature + " temperature.");
            return;
        }

        System.out.println("\nFound " + aisles.getSize() + " aisle(s) with " + temperature + " temperature:");
        aisles.forEach(aisle -> {
            if (aisle != null) {
                System.out.println("  - " + aisle.getName() + " (Floor: " + aisle.getFloorArea().getTitle() + ", Shelves: " + aisle.getShelves().getSize() + ")");
            }
        });
    }


    private int searchFloorMenu() {
        System.out.println("""
                -------- Floor Search ------------
                |  1) By title                   |
                |  2) By level                   |
                |  0) Back to Main Menu          |
                -------------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runFloorSearch() {
        int option = searchFloorMenu();
        while (option != 0) {
            switch (option) {
                case 1 -> searchFloorByTitle();
                case 2 -> searchFloorByLevel();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = searchFloorMenu();
        }
    }

    /**
     * Search floor area by title
     */
    private void searchFloorByTitle() {
        System.out.print("Enter floor area title to search: ");
        String searchTitle = scanner.nextLine();

        FloorArea foundArea = areaManager.findFloorAreaByTitle(searchTitle);

        if (foundArea != null) {
            System.out.println("\nFound floor area:");
            System.out.println("  Title: " + foundArea.getTitle());
            System.out.println("  Level: " + foundArea.getLevel());
            System.out.println("  Aisles: " + foundArea.getAisles().getSize());
            System.out.println("  Area: " + foundArea.getArea() + "m²");

            Dimensions dims = foundArea.getDimensions();
            System.out.println("  Dimensions: " + dims.getLength() + "m × " + dims.getWidth() + "m");

            if (!foundArea.getAisles().isEmpty()) {
                System.out.println("  Aisle names:");
                foundArea.getAisles().forEach(aisle -> {
                    if (aisle != null) {
                        System.out.println("    - " + aisle.getName() + " (" + aisle.getTemp() + ")");
                    }
                });
            }
        } else {
            System.out.println("Floor area '" + searchTitle + "' not found.");
        }
    }

    /**
     * Search floor areas by level
     */
    private void searchFloorByLevel() {
        int level = ScannerInput.readNextInt("Enter level number to search: ");

        FunkierList<FloorArea> foundAreas = areaManager.findFloorAreasByLevel(level);

        if (foundAreas.isEmpty()) {
            System.out.println("\nNo floor areas found on level " + level);
            return;
        }

        System.out.println("\nFound " + foundAreas.getSize() + " floor area(s) on level " + level + ":");

        final int[] index = {1};
        foundAreas.forEach(area -> {
            if (area != null) {
                System.out.println("\n  " + index[0] + ") " + area.getTitle());
                System.out.println("     Aisles: " + area.getAisles().getSize());
                System.out.println("     Area: " + area.getArea() + "m²");
                System.out.println("     Total Items: " + system.getItemsFromFloor(area));
                System.out.println("     Total Value: €" + InputVal.twoDecPlaces(system.getValueFromFloorArea(area)));
                index[0]++;
            }
        });
    }

    /**
     * LIST MENU
     */

    private int listMenu() {
        System.out.println("""
                -------------- List Menu --------------
                |  1) By floor                        |
                |  2) By aisle                        |
                |  3) By shelf                        |
                |  4) By item                         |
                |  0) Back to Main Menu               |
                -------------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runListMenu() {
        int option = listMenu();
        while (option != 0) {
            switch (option) {
                case 1 -> runListFloor();
                case 2 -> runListAisle();
                case 3 -> runListShelf();
                case 4 -> runListItem();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = listMenu();
        }
    }

    /**
     * LIST FLOOR BY - title, index, level, full stat
     */
    private int listFloor() {
        System.out.println("""
                ------------- List Floor --------------
                |  1) By title                        |
                |  2) By index                        |
                |  3) By level                        |
                |  0) Back to Main Menu               |
                -------------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runListFloor() {
        int option = listFloor();
        while (option != 0) {
            switch (option) {
                case 1 -> listFloorAreaTitles();
                case 2 -> listFloorAreaByIndex();
                case 3 -> listFloorAreaByLevel();
                case 4 -> listFloorAreaStatistics();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = listFloor();
        }
    }

    private void listFloorAreaTitles() {
        System.out.println(areaManager.listFloorAreaTitles());
    }

    private void listFloorAreaByIndex() {
        System.out.println(areaManager.listFloorByIndex());
    }

    private void listFloorAreaByLevel() {
        int level = ScannerInput.readNextInt("Enter level: ");
        String result = areaManager.listFloorAreasByLevel(level);
        System.out.println(result);
    }

    private void listFloorAreaStatistics() {
        System.out.println(areaManager.getFloorAreaStatistics());
    }


    /**
     * LIST AISLE BY - index, title, floor, temperature, aisle stat
     */
    private int listAisle() {
        System.out.println("""
                ------------- List Aisle --------------
                |  1) All with index                  |
                |  2) By given title                  |
                |  3) By floor                        |
                |  4) By temperature                  |
                |  5) Aisle statistics                |
                |  0) Back to Main Menu               |
                ---------------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runListAisle() {
        int option = listAisle();
        while (option != 0) {
            switch (option) {
                case 1 -> listAllAisles();
                case 2 -> listAisleTitles();
                case 3 -> listAislesByFloor();
                case 4 -> listAislesByTemperature();
                case 5 -> listAisleStatistics();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = listAisle();
        }
    }

    private void listAisleTitles() {
        System.out.println(aisleManager.listAisleTitles());
    }

    private void listAllAisles() {
        System.out.println(aisleManager.listAllAisles());
    }

    private void listAislesByFloor() {
        System.out.print("Enter floor area name: ");
        String floorName = scanner.nextLine();

        String result = aisleManager.listAislesByFloorArea(floorName);
        System.out.println(result);
    }

    private void listAislesByTemperature() {
        int tempChoice = ScannerInput.readNextInt("""
                Select temperature type:
                1) UNREFRIGERATED
                2) REFRIGERATED
                3) FROZEN
                """);

        String temperature;
        switch (tempChoice) {
            case 1 -> temperature = "UNREFRIGERATED";
            case 2 -> temperature = "REFRIGERATED";
            case 3 -> temperature = "FROZEN";
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }

        System.out.println(aisleManager.listAislesByTemp(temperature));
    }

    private void listAisleStatistics() {
        System.out.println(aisleManager.getAisleStatistics());
    }

    /**
     * LIST SHELF BY - number, aisle, shelf stat
     */
    private int listShelf() {
        System.out.println("""
                ---------- List Shelf ------------
                |  1) List all shelf numbers     |
                |  2) List shelves by aisle      |
                |  3) Shelf statistics           |
                |  0) Back to Main Menu          |
                -------------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runListShelf() {
        int option = listShelf();
        while (option != 0) {
            switch (option) {
                case 1 -> listAllShelfNumbers();
                case 2 -> listShelvesByAisle();
                case 3 -> listShelfStatistics();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = listShelf();
        }
    }

    private void listAllShelfNumbers() {
        System.out.println(shelfManager.listShelfNumbers());
    }

    private void listShelvesByAisle() {
        System.out.print("Enter aisle name: ");
        String aisleName = scanner.nextLine();

        Aisle aisle = aisleManager.findAisle(aisleName);
        if (aisle == null) {
            System.out.println("Aisle not found.");
            return;
        }

        System.out.println(shelfManager.listShelvesByAisle(aisle));
    }

    private void listShelfStatistics() {
        System.out.println(shelfManager.getShelfStatistics());
    }

    /**
     * LIST ITEM BY -
     */
    private int listItem() {
        System.out.println("""
                 ---------- List Item ------------
                |  1) List all item descriptions |
                |  2) View specific item         |
                |  3) List by description        |
                |  4) List by aisle              |
                |  5) List by shelf              |
                |  6) List by temperature        |
                |  7) Item statistics            |
                |  0) Back to Main Menu          |
                 -------------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runListItem() {
        int option = listItem();
        while (option != 0) {
            switch (option) {
                case 1 -> listAllItemDesc();
                case 2 -> viewSpecificItem();
                case 3 -> listItemsByDesc();
                case 4 -> listItemsByAisle();
                case 5 -> listItemsByShelf();
                case 6 -> listItemsByTemp();
                case 7 -> listItemStatistics();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = listItem();
        }
    }

    private void listAllItemDesc() {
        System.out.println(itemManager.listItemDesc());
    }

    private void viewSpecificItem() {
        System.out.println(itemManager.listItemDesc());

        int itemIndex = ScannerInput.readNextInt("Enter item index to view details: ");
        if (itemIndex < 0 || itemIndex >= system.getGoodItems().getSize()) {
            System.out.println("Invalid item index.");
            return;
        }

        GoodItem selectedItem = system.getGoodItems().get(itemIndex);
        stockViewer.dispOneItem(selectedItem);
    }

    private void listItemsByDesc() {
        System.out.print("Enter item description to search for: ");
        String description = scanner.nextLine().trim();

        if (description.isEmpty()) {
            System.out.println("Empty input.");
            return;
        }

        System.out.println(itemManager.listItemsByDesc(description));
    }

    private void listItemsByAisle() {
        System.out.print("Enter aisle name: ");
        String aisleName = scanner.nextLine().trim();

        Aisle aisle = aisleManager.findAisle(aisleName);
        if (aisle == null) {
            System.out.println("Aisle not found.");
            return;
        }
        System.out.println(itemManager.listItemsByAisle(aisle));
    }

    private void listItemsByShelf() {
        int shelfNumber = ScannerInput.readNextInt("Enter shelf number: ");

        Shelf shelf = shelfManager.findShelfByNumber(shelfNumber);
        if (shelf == null) {
            System.out.println("Shelf #" + shelfNumber + " not found.");
            return;
        }

        FunkierList<GoodItem> items = itemManager.findItemsByShelf(shelf);

        if (items.isEmpty()) {
            System.out.println("Shelf #" + shelfNumber + " has no items.");
            return;
        }
        System.out.println("\nItems on Shelf #" + shelfNumber + ":");
        items.forEach(item -> {
            if (item != null) {
                System.out.println("  - " + item.getDesc() + " (Qty: " + item.getQty() + ")");
            }
        });
    }

    //TEMPERATURE
    private void listItemsByTemp() {
        int choice = ScannerInput.readNextInt("Select temperature: \n1) UNREFRIGERATED\n2) REFRIGERATED\n3) FROZEN\n");

        String temperature;
        switch (choice) {
            case 1 -> temperature = "UNREFRIGERATED";
            case 2 -> temperature = "REFRIGERATED";
            case 3 -> temperature = "FROZEN";
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }
        System.out.println(itemManager.listItemsByTemp(temperature));
    }

    private void listItemStatistics() {
        System.out.println(itemManager.getItemStatistics());
    }

    /**
     * UPDATE BY - Floor, aisle, shelf. item
     */

    private int updateMenu() {
        System.out.println("""
                 ---------- Update By ---------
                |  1) Floor                   |
                |  2) Aisle                   |
                |  3) Shelf                   |
                |  4) Item                    |
                |  0) Back to Main Menu       |
                 -----------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runUpdateMenu() {
        int option = updateMenu();
        while (option != 0) {
            switch (option) {
                case 1 -> runFloorUpdateMenu();
                case 2 -> runAisleUpdateMenu();
                case 3 -> runShelfUpdateMenu();
                case 4 -> runItemUpdateMenu();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = updateMenu();
        }
    }

    /**
     * UPDATE FLOOR BY - title, add floor
     */
    private int updateFloorMenu() {
        System.out.println("""
                 ---------- Update Floor ---------
                |  1) Update floor title         |
                |  2) Add aisle to floor         |
                |  0) Back to Update Menu        |
                 --------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runFloorUpdateMenu() {
        int option = updateFloorMenu();
        while (option != 0) {
            switch (option) {
                case 1 -> updateFloorTitle();
                case 2 -> addAisleToFloor();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = updateFloorMenu();
        }
    }

    //TITLE
    private void updateFloorTitle() {
        System.out.println("Available floor areas:");
        System.out.println(areaManager.listFloorAreaTitles());

        String currentTitle = ScannerInput.readNextLine("Enter current floor area title: ");
        String newTitle = ScannerInput.readNextLine("Enter new floor area title: ");

        boolean success = areaManager.updateFloorAreaTitle(currentTitle, newTitle);
        System.out.println(success ? "Floor title updated successfully." : "Failed to update floor title.");
    }

    //ADD FLOOR
    private void addAisleToFloor() {
        System.out.println("Available floor areas:");
        System.out.println(areaManager.listFloorAreaTitles());

        String floorTitle = ScannerInput.readNextLine("Enter floor area name: ");
        String aisleName = ScannerInput.readNextLine("Enter aisle name to add: ");

        FloorArea floorArea = areaManager.findFloorAreaByTitle(floorTitle);
        if (floorArea == null) {
            System.out.println("Floor area '" + floorTitle + "' not found.");
            return;
        }

        Aisle aisle = aisleManager.findAisle(aisleName);
        if (aisle == null) {
            System.out.println("Aisle '" + aisleName + "' not found.");
            return;
        }

        boolean success = areaManager.addAisleToFloorArea(floorTitle, aisle);
        System.out.println(success ?
                "Aisle '" + aisleName + "' added to floor '" + floorTitle + "' successfully." :
                "Failed to add aisle to floor.");
    }


    /**
     * UPDATE AISLE BY - name, temperature, move floor
     */

    private int updateAisleMenu() {
        System.out.println("""
                -------- Update Aisle--------
                |  1) Aisle name              |
                |  2) Aisle temperature       |
                |  0) Back to Main Menu       |
                 -----------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runAisleUpdateMenu() {
        int option = updateAisleMenu();
        while (option != 0) {
            switch (option) {
                case 1 -> updateAisleName();
                case 2 -> updateAisleTemp();
                case 3 -> moveAisleToFloor();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = updateAisleMenu();
        }
    }

    //name
    private void updateAisleName() {
        System.out.println("Available aisles:");
        System.out.println(aisleManager.listAisleTitles());

        String currentName = ScannerInput.readNextLine("Enter current aisle name: ");
        String newName = ScannerInput.readNextLine("Enter new aisle name: ");

        if (currentName.isEmpty() || newName.isEmpty()) {
            System.out.println("Aisle names cannot be empty.");
            return;
        }

        boolean success = aisleManager.updateAisleName(currentName, newName);
        System.out.println(success ? "Aisle name updated successfully." : "Failed to update aisle name.");
    }

    //temperature
    private void updateAisleTemp() {
        System.out.println("Available aisles:");
        System.out.println(aisleManager.listAisleTitles());

        String aisleName = ScannerInput.readNextLine("Enter aisle name: ");

        System.out.println(TempValidator.getCategoryMenu());
        int choice = ScannerInput.readNextInt("Select temperature category (1-3): ");

        String temperature = switch (choice) {
            case 1 -> "UNREFRIGERATED";
            case 2 -> "REFRIGERATED";
            case 3 -> "FROZEN";
            default -> null;
        };

        if (temperature == null) {
            System.out.println("Invalid temperature choice.");
            return;
        }
        boolean success = aisleManager.updateAisleTemperature(aisleName, temperature);
        System.out.println(success ?
                "Aisle temperature updated to " + temperature + " successfully." :
                "Failed to update aisle temperature.");
    }

    //MOVE FLOOR
    private void moveAisleToFloor() {
        System.out.println("Available aisles:");
        System.out.println(aisleManager.listAllAisles());

        String aisleName = ScannerInput.readNextLine("Enter aisle name to move: ");

        Aisle aisle = aisleManager.findAisle(aisleName);
        if (aisle == null) {
            System.out.println("Aisle '" + aisleName + "' not found.");
            return;
        }

        System.out.println("Current floor: " + aisle.getFloorArea().getTitle());

        System.out.println("Available floors:");
        System.out.println(areaManager.listFloorAreaTitles());

        String newFloorTitle = ScannerInput.readNextLine("Enter new floor area name: ");

        FloorArea newFloor = areaManager.findFloorAreaByTitle(newFloorTitle);
        if (newFloor == null) {
            System.out.println("Floor area '" + newFloorTitle + "' not found.");
            return;
        }

        if (aisle.getFloorArea() != null) {
            aisle.getFloorArea().getAisles().removeFirstHit(aisle);
        }

        aisle.setFloorArea(newFloor);
        newFloor.getAisles().addAtLast(aisle);

        System.out.println("Aisle '" + aisleName + "' moved to floor '" + newFloorTitle + "' successfully.");
    }


    /**
     * UPDATE SHELF - move shelf
     *
     * @return
     */

    private int updateShelfMenu() {
        System.out.println("""
                -------- Update Shelf --------
                |  1) Move shelf to aisle     |
                |  0) Back to Update Menu     |
                ------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runShelfUpdateMenu() {
        int option = updateShelfMenu();
        while (option != 0) {
            switch (option) {
                case 1 -> moveShelfToAisle();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = updateShelfMenu();
        }
    }

    private void moveShelfToAisle() {
        System.out.println("Available shelves:");
        System.out.println(shelfManager.listShelfNumbers());

        int shelfNumber = ScannerInput.readNextInt("Enter shelf number to move: ");

        Shelf shelf = shelfManager.findShelfByNumber(shelfNumber);
        if (shelf == null) {
            System.out.println("Shelf #" + shelfNumber + " not found.");
            return;
        }

        System.out.println("Current aisle: " + (shelf.getAisle() != null ? shelf.getAisle().getName() : "None"));

        System.out.println("Available aisles:");
        System.out.println(aisleManager.listAisleTitles());

        String newAisleName = ScannerInput.readNextLine("Enter new aisle name: ");

        Aisle newAisle = aisleManager.findAisle(newAisleName);
        if (newAisle == null) {
            System.out.println("Aisle '" + newAisleName + "' not found.");
            return;
        }

        if (shelf.getAisle() != null) {
            shelf.getAisle().getShelves().removeFirstHit(shelf);
        }

        shelf.setAisle(newAisle);
        newAisle.getShelves().addAtLast(shelf);

        System.out.println("Shelf #" + shelfNumber + " moved to aisle '" + newAisleName + "' successfully.");
    }


    /**
     * UPDATE ITEM BY - select choice, temperature, move to aisle
     */

    private int updateItemMenu() {
        System.out.println("""
                -------- Update Item--------
                |  1) Select item to update |
                |  2) Move to new shelf     |
                |  0) Back to Update Menu   |
                -----------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runItemUpdateMenu() {
        int option = updateItemMenu();
        while (option != 0) {
            switch (option) {
                case 1 -> selectAndUpdateItem();
                case 2 -> moveItemToShelf();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = updateItemMenu();
        }
    }

    //SELECT ITEM TO UPDATE
    private void selectAndUpdateItem() {
        System.out.println("Available items:");
        System.out.println(itemManager.listItemDesc());

        if (system.getGoodItems().isEmpty()) {
            System.out.println("No items available to update.");
            return;
        }

        int itemIndex = ScannerInput.readNextInt("Enter item index to update: ");
        if (itemIndex < 0 || itemIndex >= system.getGoodItems().getSize()) {
            System.out.println("Invalid item index.");
            return;
        }
        GoodItem item = system.getGoodItems().get(itemIndex);
        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        System.out.println("\nCurrent item details:");
        stockViewer.dispOneItem(item);

        // UPDATE MENU FOR CHOSEN ITEM
        int updateChoice = ScannerInput.readNextInt("""
                  ---------- Update Item---------
                  |  1) Description             |
                  |  2) Unit Size               |
                  |  3) Unit Price              |
                  |  4) Quantity                |
                  |  5) Photo URL               |
                  |  0) Back to Update Menu     |
                  --------------------------------
                """);

        switch (updateChoice) {
            case 1 -> {
                String newDesc = ScannerInput.readNextLine("Enter new description: ");
                item.setDescription(newDesc);
                System.out.println("Description updated to: " + newDesc);
            }
            case 2 -> {
                double newSize = ScannerInput.readNextDouble("Enter new unit size (grams): ");
                item.setUnitSize(newSize);
                System.out.println("Unit size updated to: " + newSize + "g");
            }
            case 3 -> {
                double newPrice = ScannerInput.readNextDouble("Enter new unit price: €");
                item.setUnitPrice(newPrice);
                System.out.println("Unit price updated to: €" + newPrice);
            }
            case 4 -> {
                int newQty = ScannerInput.readNextInt("Enter new quantity: ");
                item.setQty(newQty);
                System.out.println("Quantity updated to: " + newQty);
            }
            case 5 -> {
                String newPhotoUrl = ScannerInput.readNextLine("Enter new photo URL: ");
                item.setPhotoUrl(newPhotoUrl);
                System.out.println("Photo URL updated.");
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    //MOVE TO SHELF
    private void moveItemToShelf() {
        System.out.println("Available items:");
        System.out.println(itemManager.listItemDesc());

        if (system.getGoodItems().isEmpty()) {
            System.out.println("No items available to move.");
            return;
        }

        int itemIndex = ScannerInput.readNextInt("Enter item index: ");
        if (itemIndex < 0 || itemIndex >= system.getGoodItems().getSize()) {
            System.out.println("Invalid item index.");
            return;
        }

        GoodItem item = system.getGoodItems().get(itemIndex);
        if (item == null) {
            System.out.println("Item not found.");
            return;
        }
        System.out.println("Current location: " + itemManager.getItemLocation(item));
        int shelfNumber = ScannerInput.readNextInt("Enter shelf number to move to: ");

        Shelf shelf = shelfManager.findShelfByNumber(shelfNumber);
        if (shelf == null) {
            System.out.println("Shelf #" + shelfNumber + " not found.");
            return;
        }
        if (shelf.getAisle() == null ||
                !TempValidator.tempToCategory(item.getStorageTemp()).equals(shelf.getAisle().getTemp())) {
            System.out.println("TEMPERATURE MISMATCH.");
            return;
        }

        double itemWeight = item.getUnitSize() * item.getQty() / 1000.0;
        if (!shelfManager.hasSpace(shelfNumber, itemWeight)) {
            System.out.println("No space.");
            return;
        }

        if (item.getShelves() != null) {
            item.getShelves().forEach(currentShelf -> {
                if (currentShelf != null) {
                    shelfManager.removeItemFromShelf(currentShelf.getShelfNumber(), item);
                }
            });
            item.getShelves().clear();
        }

        boolean success = shelfManager.addItemToShelf(shelfNumber, item);
        System.out.println(success ?
                "Item moved to shelf #" + shelfNumber :
                "Failed to move item to shelf.");
    }

    /**
     * REMOVE MENU - title, level, aisle from floor, aisle, shelf, shelf, shelf from aisle, item, item from shelf
     */
    private int removeMenu() {
        System.out.println("""
                ------------ Remove Menu --------------
                |  1) Remove Floors by Title          |
                |  2) Remove Floors by Level          |
                |  3) Remove Aisle from Floor         |
                |  4) Remove Aisle                    |
                |  5) Remove Shelf                    |
                |  6) Remove Shelf from Aisle         |
                |  7) Remove Item                     |
                |  8) Remove Item from Shelf          |
                |  0) Back to Main Menu               |
                -------------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runRemoveMenu() {
        int option = removeMenu();
        while (option != 0) {
            switch (option) {
                case 1 -> removeFloorArea();
                case 2 -> removeFloorsByLevel();
                case 3 -> removeAisleFromFloor();
                case 4 -> removeAisle();
                case 5 -> removeShelf();
                case 6 -> removeShelfFromSpecificAisle();
                case 7 -> removeItem();
                case 8 -> removeItemFromShelf();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = removeMenu();
        }
    }

    /**
     * REMOVE - Floor, Floor by Level, Aisle from Floor, Aisle, Shelf, Item from Shelf, Shelf from specific Aisle
     */
    private void removeFloorArea() {
        System.out.println("Available floor areas:");
        System.out.println(areaManager.listFloorByIndex());

        String floorAreaTitle = ScannerInput.readNextLine("Enter floor area title to remove: ");

        boolean success = areaManager.removeFloorAreaByTitle(floorAreaTitle);
        System.out.println(success ? "Floor area '" + floorAreaTitle + "' removed." : "Failed to remove floor area.");
    }

    private void removeFloorsByLevel() {
        int level = ScannerInput.readNextInt("Enter level number: ");
        boolean success = areaManager.removeAllFloorAreasByLevel(level);
        System.out.println(success ? "Floor areas on level " + level + " removed." : "No floor areas removed.");
    }

    private void removeAisleFromFloor() {
        System.out.print("Enter floor area name: ");
        String floorAreaTitle = scanner.nextLine();

        System.out.print("Enter aisle name to remove: ");
        String aisleName = scanner.nextLine();

        Aisle aisle = aisleManager.findAisle(aisleName);
        if (aisle == null) {
            System.out.println("Aisle not found.");
            return;
        }
        boolean success = areaManager.removeAisleFromFloorArea(floorAreaTitle, aisle);
        System.out.println(success ? "Aisle '" + aisleName + "' removed from floor area '" + floorAreaTitle + "'." : "Failed to remove aisle.");
    }

    private void removeAisle() {
        System.out.println("Available aisles:");
        System.out.println(aisleManager.listAllAisles());

        String aisleName = ScannerInput.readNextLine("Enter aisle name to remove: ");

        boolean success = aisleManager.removeAisleByName(aisleName);
        System.out.println(success ? "Aisle '" + aisleName + "' removed." : "Failed to remove aisle.");
    }

    private void removeShelf() {
        System.out.println("Available shelves:");
        System.out.println(shelfManager.listShelfNumbers());

        int shelfNumber = ScannerInput.readNextInt("Enter shelf number to remove: ");

        Shelf shelf = shelfManager.findShelfByNumber(shelfNumber);
        if (shelf == null) {
            System.out.println("Shelf #" + shelfNumber + " not found.");
            return;
        }

        boolean success = shelfManager.removeShelf(shelf);
        System.out.println(success ? "Shelf #" + shelfNumber + " removed." : "Failed to remove shelf.");
    }

    private void removeItemFromShelf() {
        int shelfNumber = ScannerInput.readNextInt("Enter shelf number: ");

        Shelf shelf = shelfManager.findShelfByNumber(shelfNumber);
        if (shelf == null) {
            System.out.println("Shelf #" + shelfNumber + " not found.");
            return;
        }

        if (shelf.getItems().isEmpty()) {
            System.out.println("Shelf #" + shelfNumber + " has no items.");
            return;
        }

        System.out.println("Items on shelf #" + shelfNumber + ":");
        FunkierList<GoodItem> shelfItems = shelf.getItems();
        for (int i = 0; i < shelfItems.getSize(); i++) {
            GoodItem item = shelfItems.get(i);
            if (item != null) {
                System.out.println("  " + (i + 1) + ". " + item.getDesc() + " (Qty: " + item.getQty() + ")");
            }
        }

        int itemIndex = ScannerInput.readNextInt("Enter item number to remove: ") - 1;
        if (itemIndex < 0 || itemIndex >= shelfItems.getSize()) {
            System.out.println("Invalid item selection.");
            return;
        }

        GoodItem itemToRemove = shelfItems.get(itemIndex);
        if (itemToRemove == null) {
            System.out.println("Item not found.");
            return;
        }
        boolean success = shelfManager.removeItemFromShelf(shelfNumber, itemToRemove);
        System.out.println(success ? "Item '" + itemToRemove.getDesc() + "' removed from shelf #" + shelfNumber + "." : "Failed to remove item.");
    }

    private void removeShelfFromSpecificAisle() {
        String floorArea = ScannerInput.readNextLine("Enter floor area name: ");
        String aisleName = ScannerInput.readNextLine("Enter aisle name: ");

        Aisle aisle = aisleManager.findAisle(aisleName);
        if (aisle == null) {
            System.out.println("Aisle '" + aisleName + "' not found.");
            return;
        }

        if (aisle.getFloorArea() == null || !aisle.getFloorArea().getTitle().equalsIgnoreCase(floorArea)) {
            System.out.println("Aisle '" + aisleName + "' is not in floor area '" + floorArea + "'.");
            return;
        }

        FunkierList<Shelf> shelves = aisle.getShelves();
        if (shelves.isEmpty()) {
            System.out.println("This aisle has no shelves.");
            return;
        }

        System.out.println("Shelves in aisle '" + aisleName + "':");
        final int[] index = {1};
        shelves.forEach(shelf -> {
            if (shelf != null) {
                System.out.println("  " + index[0] + ". Shelf #" + shelf.getShelfNumber() +
                        " - Capacity: " + shelf.getCapacity() + "kg" +
                        " - Items: " + shelf.getCurrentQuantity());
                index[0]++;
            }
        });

        int shelfNumber = ScannerInput.readNextInt("Enter shelf number to remove: ");
        Shelf shelfToRemove = shelves.find(shelf ->
                shelf != null && shelf.getShelfNumber() == shelfNumber
        );

        if (shelfToRemove == null) {
            System.out.println("Shelf #" + shelfNumber + " not found in aisle '" + aisleName + "'.");
            return;
        }

        boolean success = shelfManager.removeShelf(shelfToRemove);
        System.out.println(success ?
                "Shelf #" + shelfNumber + " removed from aisle '" + aisleName + "' successfully." :
                "Failed to remove shelf.");
    }

    private void removeItem() {
        System.out.println("Available items:");
        System.out.println(itemManager.listItemDesc());

        String itemDesc = ScannerInput.readNextLine("Enter item description to remove: ");

        FunkierList<GoodItem> items = itemManager.findItemsByDesc(itemDesc);
        if (items.isEmpty()) {
            System.out.println("No items found with description: " + itemDesc);
            return;
        }

        GoodItem itemToRemove;
        if (items.getSize() > 1) {
            System.out.println("Multiple items found:");

            final int[] index = {1};
            items.forEach(item -> {
                if (item != null) {
                    System.out.println("  " + index[0] + ". " + item.getDesc() +
                            " (Aisle: " + (item.getAisle() != null ? item.getAisle().getName() : "None") +
                            ", Qty: " + item.getQty() + ")");
                    index[0]++;
                }
            });

            int choice = ScannerInput.readNextInt("Select item to remove (1-" + items.getSize() + "): ") - 1;
            if (choice < 0 || choice >= items.getSize()) {
                System.out.println("Invalid selection.");
                return;
            }
            itemToRemove = items.get(choice);
        } else {
            itemToRemove = items.get(0);
        }

        boolean success = itemManager.removeGoodItem(itemToRemove);
        System.out.println(success ?
                "Item '" + itemDesc + "' removed from system." :
                "Failed to remove item.");
    }

    /**
     * PERSISTENCE - save, load,
     *
     */
    private int fileOptions() {
        System.out.println("""
                --------- System Operations ---------
                |  1) Save All Data                |
                |  2) Load All Data                |
                |  3) Reset System                 |
                |  0) Back to Main Menu            |
                ------------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runFileOptions() {
        int option = fileOptions();
        while (option != 0) {
            switch (option) {
                case 1 -> saveAllData();
                case 2 -> loadAllData();
                case 3 -> runResetMenu();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = fileOptions();
        }
    }

    private void saveAllData() {
        try {
            persistence.saveAllData();
        } catch (Exception e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    private void loadAllData() {
        try {
            persistence.loadAllData();
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    private int resetMenu() {
        System.out.println("""
                ---------- Reset ------------
                |  1) Entire system         |
                |  2) Floor area            |
                |  3) Aisle                 |
                |  4) Shelf                 |
                |  0) Back to Main Menu     |
                ----------------------------------""");
        return ScannerInput.readNextInt("==>> ");
    }

    private void runResetMenu() {
        int option = resetMenu();
        while (option != 0) {
            switch (option) {
                case 1 -> resetSystem();
                case 2 -> resetFloorArea();
                case 3 -> resetAisle();
                case 4 -> resetShelf();
                default -> System.out.println("Invalid option: " + option);
            }
            ScannerInput.readNextLine("\nPress Enter to continue...");
            option = resetMenu();
        }
    }

    private void resetSystem() {
        char response = ScannerInput.readNextChar("Are you sure? (Y/N): ");

        if (ScannerInput.YNtoBoolean(response)) {
            system.clearAllData();
            System.out.println("Reset.");
        } else {
            System.out.println("Reset cancelled.");
        }
    }

    private void resetFloorArea() {
        System.out.println("Available floor areas:");
        System.out.println(areaManager.listFloorAreaTitles());

        String floorTitle = ScannerInput.readNextLine("Enter floor area name to reset: ");

        char response = ScannerInput.readNextChar("Reset all aisles in '" + floorTitle + "'? (Y/N): ");

        if (ScannerInput.YNtoBoolean(response)) {
            int aislesRemoved = system.resetFloorArea(floorTitle);
            System.out.println("Reset floor area '" + floorTitle + "' - removed " + aislesRemoved + " aisles");
        } else {
            System.out.println("Reset cancelled.");
        }
    }

    private void resetAisle() {
        String floorTitle = ScannerInput.readNextLine("Enter floor area name: ");
        String aisleName = ScannerInput.readNextLine("Enter aisle name to reset: ");

        char response = ScannerInput.readNextChar("Reset all shelves in aisle '" + aisleName + "'? (Y/N): ");

        if (ScannerInput.YNtoBoolean(response)) {
            int shelvesRemoved = system.resetAisle(floorTitle, aisleName);
            System.out.println("Reset aisle '" + aisleName + "' - removed " + shelvesRemoved + " shelves");
        } else {
            System.out.println("Reset cancelled.");
        }
    }

    private void resetShelf() {
        int shelfNumber = ScannerInput.readNextInt("Enter shelf number to reset: ");

        char response = ScannerInput.readNextChar("Reset all items on shelf #" + shelfNumber + "? (Y/N): ");

        if (ScannerInput.YNtoBoolean(response)) {
            int itemsRemoved = system.resetShelf(shelfNumber);
            System.out.println("Reset shelf #" + shelfNumber + " - removed " + itemsRemoved + " items");
        } else {
            System.out.println("Reset cancelled.");
        }
    }

    private void exitApp() {
        System.out.println("Exiting...");
        System.exit(0);
    }
}