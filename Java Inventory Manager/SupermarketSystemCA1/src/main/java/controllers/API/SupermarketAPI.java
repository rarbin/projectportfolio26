package controllers.API;

import models.Aisle;
import models.FloorArea;
import models.GoodItem;
import models.Shelf;
import utils.CustomList.FunkierList;
import utils.Validators.TempValidator;

/**
 * SupermarketAPI Class
 * overhead of all managers.
 */
public class SupermarketAPI {
    private final FunkierList<FloorArea> floorAreas;
    private final FunkierList<Aisle> aisles;
    private final FunkierList<Shelf> shelves;
    private final FunkierList<GoodItem> goodItems;

    public SupermarketAPI() {
        this.floorAreas = new FunkierList<>();
        this.aisles = new FunkierList<>();
        this.shelves = new FunkierList<>();
        this.goodItems = new FunkierList<>();
    }

    // GETTERS

    /**
     * MODELS - floor, aisle, shelf, good items
     */

    public FunkierList<FloorArea> getFloorAreas() {
        return floorAreas;
    }

    public FunkierList<Aisle> getAisles() {
        return aisles;
    }

    public FunkierList<Shelf> getShelves() {
        return shelves;
    }

    public FunkierList<GoodItem> getGoodItems() {
        return goodItems;
    }

    public int getFloorAreaCount() {
        return floorAreas.getSize();
    }

    public int getAisleCount() {
        return aisles.getSize();
    }

    public int getShelfCount() {
        return shelves.getSize();
    }

    public int getItemCount() {
        return goodItems.getSize();
    }


    /**
     * INVENTORY - value, quantity
     */

    public double getTotalInvValue() {
        return floorAreas.sumValues(this::getValueFromFloorArea);
    }

    public int getTotalInvQty() {
        return floorAreas.sumIntValues(this::getItemsFromFloor);
    }

    /**
     * CALCULATE - value, quantity
     */
    public double calcItemTotalVal(FunkierList<GoodItem> items) {
        return items.sumValues(item ->
                item != null ? item.getPrice() * item.getQty() : 0.0
        );
    }

    public int calcItemTotalQty(FunkierList<GoodItem> items) {
        return items.sumIntValues(item ->
                item != null ? item.getQty() : 0
        );
    }

    /**
     * GET ITEMS FROM - floor, aisle, shelf
     */

    public int getItemsFromFloor(FloorArea area) {
        FunkierList<Aisle> aisleList = area.getAisles();
        return aisleList.sumIntValues(this::getItemsFromAisle);
    }

    public int getItemsFromAisle(Aisle aisle) {
        FunkierList<Shelf> shelfList = aisle.getShelves();
        return shelfList.sumIntValues(this::getItemsFromShelf);
    }

    public int getItemsFromShelf(Shelf shelf) {
        FunkierList<GoodItem> items = shelf.getItems();
        return calcItemTotalQty(items);
    }
    /**
     * GET VALUE FROM - floor, aisle, shelf
     */

    public double getValueFromFloorArea(FloorArea area) {
        FunkierList<Aisle> aisleList = area.getAisles();
        return aisleList.sumValues(this::getValueFromAisle);
    }

    public double getValueFromAisle(Aisle aisle) {
        FunkierList<Shelf> shelfList = aisle.getShelves();
        return shelfList.sumValues(this::getValueFromShelf);
    }

    public double getValueFromShelf(Shelf shelf) {
        FunkierList<GoodItem> items = shelf.getItems();
        return calcItemTotalVal(items);
    }


    /**
     * GET ITEMS BY TEMPERATURE
     */

    public FunkierList<GoodItem> getItemsByTemp(String temperature) {
        return goodItems.findAll(item ->
                item != null && item.getStorageCategory().equals(temperature));
    }

    public int getTotalQtyByTemp(String temperature) {
        return goodItems.sumIntValues(item -> {
            if (item != null && item.getStorageCategory().equals(temperature)) {
                return item.getQty();
            }
            return 0;
        });
    }

    public int getUnrefrigeratedItems() {
        return getTotalQtyByTemp(TempValidator.UNREFRIGERATED);
    }

    public int getRefrigeratedItems() {
        return getTotalQtyByTemp(TempValidator.REFRIGERATED);
    }

    public int getFrozenItems() {
        return getTotalQtyByTemp(TempValidator.FROZEN);
    }

    /**
     * ADD - floor, aisle, shelf, item
     */

    public boolean addFloorArea(FloorArea area) {
        if (area == null || !isFloorAreaTitleUnique(area.getTitle())) {
            return false;
        }
        floorAreas.addAtLast(area);
        return true;
    }

    public boolean addAisle(Aisle aisle) {
        if (aisle == null || !isAisleNameUnique(aisle.getName())) {
            return false;
        }
        aisles.addAtLast(aisle);
        return true;
    }

    public boolean addShelf(Shelf shelf) {
        if (shelf == null || !isShelfNumberUnique(shelf.getShelfNumber())) {
            return false;
        }
        shelves.addAtLast(shelf);
        return true;
    }

    public boolean addGoodItem(GoodItem item) {
        if (item == null) {
            return false;
        }
        goodItems.addAtLast(item);
        return true;
    }

    /**
     * FIND BY - floor, aisle, shelf
     */

    public FloorArea findFloorAreaByTitle(String title) {
        return floorAreas.find(area -> area != null && area.getTitle().equalsIgnoreCase(title));
    }

    public Aisle findAisleByName(String name) {
        return aisles.find(aisle -> aisle != null && aisle.getName().equalsIgnoreCase(name));
    }

    public Shelf findShelfByNumber(int number) {
        return shelves.find(shelf -> shelf != null && shelf.getShelfNumber() == number);
    }

    /**
     * REMOVE - floor, aisle, shelf, item
     */

    public boolean removeFloorArea(FloorArea area) {
        if (area == null) return false;
        return floorAreas.removeFirstHit(area);
    }

    public boolean removeAisle(Aisle aisle) {
        if (aisle == null) return false;
        return aisles.removeFirstHit(aisle);
    }

    public boolean removeShelf(Shelf shelf) {
        if (shelf == null) return false;
        return shelves.removeFirstHit(shelf);
    }

    public boolean removeGoodItem(GoodItem item) {
        if (item == null) return false;
        return goodItems.removeFirstHit(item);
    }

    /**
     * UNIQUE CHECK
     */

    public boolean isFloorAreaTitleUnique(String title) {
        return findFloorAreaByTitle(title) == null;
    }

    public boolean isAisleNameUnique(String name) {
        return findAisleByName(name) == null;
    }

    public boolean isShelfNumberUnique(int number) {
        return findShelfByNumber(number) == null;
    }

    /**
     * PERSISTENCE
     */

    public boolean isSystemEmpty() {
        return floorAreas.isEmpty() && aisles.isEmpty() && shelves.isEmpty() && goodItems.isEmpty();
    }

    public void clearAllData() {
        floorAreas.clear();
        aisles.clear();
        shelves.clear();
        goodItems.clear();
        System.out.println("All system data has been cleared.");
    }

    public int resetFloorArea(String floorAreaTitle) {
        FloorArea area = findFloorAreaByTitle(floorAreaTitle);
        if (area == null) {
            System.out.println("Floor area '" + floorAreaTitle + "' not found");
            return 0;
        }

        int aisleCount = area.getAisles().getSize();
        area.getAisles().clear();
        System.out.println("Reset floor area '" + floorAreaTitle + "' - removed " + aisleCount + " aisles");
        return aisleCount;
    }

    public int resetAisle(String floorAreaTitle, String aisleName) {
        Aisle aisle = findAisleByName(aisleName);
        if (aisle == null) {
            System.out.println("Aisle '" + aisleName + "' not found in floor area '" + floorAreaTitle + "'");
            return 0;
        }

        int shelfCount = aisle.getShelves().getSize();
        aisle.getShelves().clear();
        System.out.println("Reset aisle '" + aisleName + "' - removed " + shelfCount + " shelves");
        return shelfCount;
    }

    public int resetShelf(int shelfNumber) {
        Shelf shelf = findShelfByNumber(shelfNumber);
        if (shelf == null) {
            System.out.println("Shelf #" + shelfNumber + " not found");
            return 0;
        }

        int itemCount = shelf.getItems().getSize();
        shelf.getItems().clear();
        shelf.setCurrentWeight(0.0);
        shelf.setCurrentQuantity(0);
        System.out.println("Reset shelf #" + shelfNumber + " - removed " + itemCount + " items");
        return itemCount;
    }
}