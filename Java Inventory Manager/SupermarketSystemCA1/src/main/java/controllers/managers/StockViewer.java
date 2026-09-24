package controllers.managers;

import controllers.API.SupermarketAPI;
import models.Aisle;
import models.FloorArea;
import models.GoodItem;
import models.Shelf;
import utils.CustomList.FunkierList;
import utils.Validators.InputVal;
import utils.Validators.TempValidator;

/**
 * StockViewer Class
 * display stock details
 */

public class StockViewer {
    private final SupermarketAPI system;

    public StockViewer(SupermarketAPI system) {
        this.system = system;
    }

    /**
     * STOCK BY - Summary, floor, aisle, shelf, items, report
     */
    public void viewStockSummary() {
        System.out.println("\nOVERALL STOCK");

        System.out.println("\nFLOOR AREAS:");
        System.out.println("Total: " + system.getFloorAreaCount() + " areas");
        System.out.println(dispValAllFloors());

        System.out.println("\nAISLES:");
        System.out.println("Total: " + system.getAisleCount() + " aisles");

        System.out.println("\nSHELVES:");
        System.out.println("Total: " + system.getShelfCount() + " shelves");

        System.out.println("\nITEMS:");
        System.out.println("Total: " + system.getTotalInvQty() + " items");
        System.out.println("Value: €" + InputVal.twoDecPlaces(system.getTotalInvValue()));
    }

    public void floorStock() {
        FloorAreaManager floorAreaManager = new FloorAreaManager(system);
        System.out.println(floorAreaManager.getFloorAreaStatistics());
    }

    public void aisleStock() {
        AisleManager aisleManager = new AisleManager(system);
        System.out.println(aisleManager.getAisleStatistics());
    }

    public void shelfStock() {
        ShelfManager shelfManager = new ShelfManager(system);
        System.out.println(shelfManager.getShelfStatistics());
    }

    public void itemStock() {
        GoodItemManager itemManager = new GoodItemManager(system);
        System.out.println(itemManager.getItemStatistics());
    }

    public void stockReport() {
        if (system.isSystemEmpty()) {
            System.out.println("No stock available.");
            return;
        }

        System.out.println("\n STOCK REPORT");

        System.out.println("TOTAL:");
        System.out.println("Items: " + system.getTotalInvQty() + ", Value: €" +
                InputVal.twoDecPlaces(system.getTotalInvValue()));

        System.out.println("\nBREAKDOWN:");
        floorStock();
        aisleStock();
        shelfStock();
        itemStock();

        System.out.println("\nDISTRIBUTION:");
        System.out.println(dispValAllFloors());
        System.out.println(getStockByTemperature());
        System.out.println(getStockByPriceRange());
        System.out.println(getLowStockReport());
    }

    /**
     * DISPLAY ONE - floor, aisle, shelf, item
     */

    public void dispOneFloor(FloorArea area) {
        System.out.println("\n" + area.getTitle().toUpperCase() + " (level " + area.getLevel() + ")");

        int areaQty = calcFloorQty(area);
        double areaValue = calcFloorVal(area);
        int aisleCount = area.getAisles().getSize();

        System.out.println("area total: " + areaQty + " units - value: €" + InputVal.twoDecPlaces(areaValue) + " (" + aisleCount + " aisles)");
    }

    //AISLE
    public void dispOneAisle(Aisle aisle) {
        if (aisle == null) {
            System.out.println("aisle is null.");
            return;
        }
        System.out.println("\n" + aisle.getName().toUpperCase() + " (" + aisle.getTemp() + ")");

        int aisleQty = calcAisleQty(aisle);
        double aisleValue = calcAisleVal(aisle);
        dispAisleShelves(aisle);

        System.out.println("aisle total: " + aisleQty + " units - value: €" + InputVal.twoDecPlaces(aisleValue));
    }

    public void dispOneShelf(Shelf shelf) {

        System.out.println("Shelf #" + shelf.getShelfNumber() + " (" + InputVal.twoDecPlaces(shelf.getCurrentWeight()) + "/" + InputVal.twoDecPlaces(shelf.getCapacity()) + "kg)");

        if (shelf.getItems().isEmpty()) {
            System.out.println("Empty.");
            return;
        }

        dispShelfItems(shelf);

        System.out.println("Shelf total: " + system.calcItemTotalQty(shelf.getItems()) + " units - value: €" + InputVal.twoDecPlaces(system.calcItemTotalVal(shelf.getItems())));
    }

    //ITEM
    public void dispOneItem(GoodItem item) {
        if (item == null) {
            System.out.println("Item does not exist.");
            return;
        }

        System.out.println("Item: " + item.getDesc());
        System.out.println("Size: " + item.getUnitSize() + "g");
        System.out.println("Price: €" + InputVal.twoDecPlaces(item.getPrice()));
        System.out.println("Quantity: " + item.getQty());
        System.out.println("Total Value: €" + InputVal.twoDecPlaces(item.getTotalValue()));
        System.out.println("Temperature: " + item.getStorageTemp() + "°C (" + TempValidator.getCategoryDesc(item.getStorageCategory()) + ")");

        if (item.getAisle() != null) {
            Aisle aisle = item.getAisle();
            System.out.println("Location: " + (aisle.getFloorArea() != null ? aisle.getFloorArea().getTitle() : "Unknown") + " > " + aisle.getName());
        }

        if (item.getPhotoUrl() != null && !item.getPhotoUrl().equals("no photo.")) {
            System.out.println("Photo: " + item.getPhotoUrl());
        }
    }

    /**
     * DISPLAY X IN Y - (Value > Floors), (Aisle > Shelf), (Shelf > Item)
     */

    public String dispValAllFloors() {
        StringBuilder result = new StringBuilder("Stock Value by Area:\n");
        double totalValue = system.getTotalInvValue();

        system.getFloorAreas().forEach(area -> {
            if (area != null) {
                double areaValue = calcFloorVal(area);
                double pct = totalValue > 0 ? (areaValue / totalValue * 100) : 0;
                result.append("  ").append(area.getTitle()).append(": €").append(InputVal.twoDecPlaces(areaValue)).append(" (").append(pct).append("%)\n");
            }
        });
        return result.toString();
    }

    private void dispAisleShelves(Aisle aisle) {
        if (aisle == null) return;

        aisle.getShelves().forEach(shelf -> {
            if (shelf != null) dispOneShelf(shelf);
        });
    }

    private void dispShelfItems(Shelf shelf) {
        if (shelf == null || shelf.getItems().isEmpty()) return;

        shelf.getItems().forEach(item -> {
            if (item != null) {
                System.out.println(" " + item.getDesc() + " - " + item.getQty() + " units x €" + InputVal.twoDecPlaces(item.getPrice()) + " = €" + InputVal.twoDecPlaces(item.getTotalValue()));
            }
        });
    }

    /**
     * CALCULATE QUANTITY, VALUE - Floor, aisle
     */
    private int calcFloorQty(FloorArea area) {
        return area.getAisles().sumIntValues(aisle -> aisle != null ? calcAisleQty(aisle) : 0);
    }

    private double calcFloorVal(FloorArea area) {
        return area.getAisles().sumValues(aisle -> aisle != null ? calcAisleVal(aisle) : 0.0);
    }

    private int calcAisleQty(Aisle aisle) {
        return aisle.getShelves().sumIntValues(shelf -> shelf != null ? system.calcItemTotalQty(shelf.getItems()) : 0);
    }

    private double calcAisleVal(Aisle aisle) {
        return aisle.getShelves().sumValues(shelf -> shelf != null ? system.calcItemTotalVal(shelf.getItems()) : 0.0);
    }


    /**
     * GET STOCK BY - temperature, price range, below X stock
     */

    public String getStockByTemperature() {
        StringBuilder result = new StringBuilder("BY TEMPERATURE CATEGORY: \n");

        result.append("  Unrefrigerated: ").append(system.getUnrefrigeratedItems()).append(" items\n");
        result.append("  Refrigerated: ").append(system.getRefrigeratedItems()).append(" items\n");
        result.append("  Frozen: ").append(system.getFrozenItems()).append(" items\n");
        return result.toString();
    }

    public String getStockByPriceRange() {
        FunkierList<GoodItem> items = system.getGoodItems();
        int cheap = items.sumIntValues(item -> item != null && item.getPrice() < 5.0 ? 1 : 0);
        int medium = items.sumIntValues(item -> item != null && item.getPrice() >= 5.0 && item.getPrice() < 20.0 ? 1 : 0);
        int expensive = items.sumIntValues(item -> item != null && item.getPrice() >= 20.0 ? 1 : 0);

        return "Stock Distribution by Price Range:\n" +
                "  Cheap (<€5): " + cheap + " items\n" +
                "  Medium (€5-€20): " + medium + " items\n" +
                "  Expensive (>€20): " + expensive + " items\n";
    }

    public String getLowStockReport() {
        FunkierList<GoodItem> allItems = system.getGoodItems();
        int totalQty = system.getTotalInvQty();
        int itemCount = allItems.getSize();
        int avgStock = itemCount > 0 ? totalQty / itemCount : 0;
        int threshold = (int) (avgStock * 0.10);

        FunkierList<GoodItem> lowStockItems = allItems.findAll(item ->
                item != null && item.getQty() < threshold
        );
        StringBuilder result = new StringBuilder("Low Stock Report (below 10% of average)\n");

        if (lowStockItems.isEmpty()) {
            result.append("  No items below threshold.\n");
        } else {
            lowStockItems.forEach(item ->
                    result.append("  ").append(item.getDesc())
                            .append(" - ").append(item.getQty())
                            .append(" units remaining\n")
            );
        }
        return result.toString();
    }

}