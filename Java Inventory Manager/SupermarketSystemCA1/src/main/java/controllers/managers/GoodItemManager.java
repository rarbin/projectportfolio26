package controllers.managers;

import controllers.API.SupermarketAPI;
import models.Aisle;
import models.GoodItem;
import models.Shelf;
import utils.CustomList.FunkierList;
import utils.Validators.InputVal;
import utils.Validators.TempValidator;

/**
 * GoodItemManager
 * handles methods unique to Good Item model.
 */
public class GoodItemManager {
    private final SupermarketAPI system;

    public GoodItemManager(SupermarketAPI system) {
        this.system = system;
    }

    /**
     * ADD - good item
     **/

    public boolean addGoodItem(String description, double unitSize, double unitPrice, int quantity, double storageTemp, String photoUrl, Aisle aisle, FunkierList<Shelf> shelves) {
        if (aisle == null) {
            System.out.println("Aisle has not been assigned.");
            return false;
        }

        String itemTempCategory = TempValidator.tempToCategory(storageTemp);
        if (!itemTempCategory.equals(aisle.getTemp())) {
            System.out.println("Item temperature category " + itemTempCategory + " does not match aisle temperature " + aisle.getTemp());
            return false;
        }

        GoodItem newItem = new GoodItem(description, unitSize, unitPrice, quantity, storageTemp, photoUrl, aisle, shelves);
        system.addGoodItem(newItem);

        if (shelves != null && !shelves.isEmpty()) {
            placeItemOnShelves(newItem, shelves, unitSize, quantity);
        }

        System.out.println("Added " + newItem);
        return true;
    }

    /**
     * FIND BY - description, aisle, shelf
     */

    //DESCRIPTION
    public FunkierList<GoodItem> findItemsByDesc(String description) {
        FunkierList<GoodItem> matchingItems = system.getGoodItems().findAll(item -> item != null && item.getDesc().equalsIgnoreCase(description));

        if (matchingItems.isEmpty()) {
            System.out.println("No items found with description: " + description);
        } else {
            System.out.println("Found " + matchingItems.getSize() + " items with description: " + description);
        }
        return matchingItems;
    }

    //AISLE
    public FunkierList<GoodItem> listItemsByAisle(Aisle aisle) {
        if (aisle == null) {
            System.out.println("Aisle is null.");
            return new FunkierList<>();
        }
        FunkierList<GoodItem> aisleItems = new FunkierList<>();

        aisle.getShelves().forEach(shelf -> {
            if (shelf != null) {
                aisleItems.addAll(shelf.getItems());
            }
        });
        if (aisleItems.isEmpty()) {
            System.out.println("No items found in aisle: " + aisle.getName());
        } else {
            System.out.println("Found " + aisleItems.getSize() + " items in aisle: " + aisle.getName());
        }
        return aisleItems;
    }


    //SHELF
    public FunkierList<GoodItem> findItemsByShelf(Shelf shelf) {
        return shelf.getItems();
    }

    /**
     * LIST BY - all, description, temperature
     */

    public String listItemDesc() {
        if (system.getGoodItems().isEmpty()) {
            return "No items available.";
        }

        StringBuilder result = new StringBuilder("All Item Descriptions:\n");


        system.getGoodItems().forEach(item -> {
            if (item != null) {
                result.append("  - ").append(item.getDesc())
                        .append(" (Qty: ").append(item.getQty()).append(")\n");
            }
        });

        return result.toString();
    }


    public FunkierList<GoodItem> listItemsByDesc(String itemName) {
        String searchTerm = itemName.toLowerCase();
        FunkierList<GoodItem> results = system.getGoodItems().findAll(item -> item != null && item.getDesc().toLowerCase().contains(searchTerm));

        System.out.println("Found " + results.getSize() + " results for: " + itemName);
        return results;
    }



    public String listItemsByTemp(String temperature) {
        FunkierList<GoodItem> tempItems = system.getItemsByTemp(temperature);
        if (tempItems.isEmpty()) return "No items in " + temperature + " category.";

        StringBuilder result = new StringBuilder("Items in " + temperature + " category:\n");
        for (int i = 0; i < tempItems.getSize(); i++) {
            GoodItem item = tempItems.get(i);
            if (item != null) {
                result.append(i + 1).append(". ").append(item.getDesc()).append(" - Qty: ").append(item.getQty()).append(" - Price: €").append(item.getPrice()).append("\n");
            }
        }
        return result.toString();
    }

    /**
     * GET RANGE - price, quantity, average, below stock
     */

    public FunkierList<GoodItem> getItemsByPriceRange(double minPrice, double maxPrice) {
        return system.getGoodItems().findAll(item -> item != null && item.getPrice() >= minPrice && item.getPrice() <= maxPrice);
    }

    public FunkierList<GoodItem> getItemsByQuantityRange(int minQty, int maxQty) {
        return system.getGoodItems().findAll(item -> item != null && item.getQty() >= minQty && item.getQty() <= maxQty);
    }

    public double getAverageItemPrice() {
        FunkierList<GoodItem> items = system.getGoodItems();
        if (items.isEmpty()) return 0.0;

        double totalPrice = items.sumValues(item -> item != null ? item.getPrice() : 0.0);
        int validItemCount = items.sumIntValues(item -> item != null ? 1 : 0);

        return validItemCount > 0 ? totalPrice / validItemCount : 0.0;
    }

    public FunkierList<GoodItem> getLowStockItems(int threshold) {
        return system.getGoodItems().findAll(item -> item != null && item.getQty() < threshold);
    }

    /**
     * GET ITEM - weight, location, most expensive, highest quantity
     */

    public double getTotalItemWeight() {
        return system.getGoodItems().sumValues(item -> item != null ? (item.getUnitSize() * item.getQty() / 1000.0) : 0.0);
    }

    public String getItemLocation(GoodItem item) {
        if (item == null || item.getAisle() == null) return "Unknown location";

        Aisle aisle = item.getAisle();
        String shelfInfo = "Unknown shelf";

        if (item.getShelves() != null && !item.getShelves().isEmpty()) {
            Shelf shelf = item.getShelves().get(0);
            if (shelf != null) shelfInfo = "Shelf #" + shelf.getShelfNumber();
        }

        return aisle.getFloorArea().getTitle() + " " + aisle.getName() + " " + shelfInfo;
    }

    public GoodItem getMostExpensiveItem() {
        return system.getGoodItems().max((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
    }

    public GoodItem getHighestQuantityItem() {
        return system.getGoodItems().max((a, b) -> Integer.compare(a.getQty(), b.getQty()));
    }

    /**
     * REMOVE BY - object, description
     */
    public boolean removeGoodItem(GoodItem item) {
        if (item == null) return false;

        int removedFromShelves = item.getShelves() != null ? item.getShelves().sumIntValues(shelf -> shelf != null && removeGoodItemFromShelf(item, shelf, item.getQty()) ? 1 : 0) : 0;

        boolean removed = system.removeGoodItem(item);
        if (removed)
            System.out.println("Removed item: " + item.getDesc() + " from " + removedFromShelves + " shelf(s)");
        return removed;
    }

    //DESCRIPTION
    public boolean removeGoodItemByDesc(String description, int qty) {
        if (description == null || description.trim().isEmpty()) return false;

        FunkierList<GoodItem> items = system.getGoodItems().findAll(item ->
                item != null && item.getDesc().equalsIgnoreCase(description));

        if (items.isEmpty()) {
            System.out.println("No items found: " + description);
            return false;
        }

        int total = items.sumIntValues(item -> item != null ? item.getQty() : 0);
        if (!InputVal.validRangeInt(qty, 1, total)) {
            System.out.println("Invalid quantity. Available: " + total + ", Requested: " + qty);
            return false;
        }

        int remain = qty;
        for (int i = 0; i < items.getSize() && remain > 0; i++) {
            GoodItem item = items.get(i);
            if (item == null) continue;

            if (item.getQty() <= remain) {
                //remove entire item if full qty
                removeGoodItem(item);
                remain -= item.getQty();
            } else {
                //  remove partial
                if (removePartialQuantity(item, remain)) {
                    System.out.println("Reduced '" + description + "' by " + remain +
                            ". New quantity: " + item.getQty());
                }
                remain = 0;
            }
        }

        System.out.println("Removed " + qty + " of '" + description + "'");
        return true;
    }

    /**
     * REMOVE FROM - item, shelf
     */

    private boolean removePartialQuantity(GoodItem item, int qty) {
        // remove items from shelf if present
        if (item.getShelves() != null && !item.getShelves().isEmpty()) {
            Shelf shelf = item.getShelves().get(0);
            if (shelf != null && shelf.getItems().contains(item)) {
                return removeGoodItemFromShelf(item, shelf, qty);
            }
        }

        // if not on shelf, directly reduce item quantity
        item.setQty(item.getQty() - qty);
        System.out.println("Reduced quantity of '" + item.getDesc() +
                "' by " + qty + " (not on shelf)");
        return true;
    }

    public boolean removeGoodItemFromShelf(GoodItem item, Shelf shelf, int qty) {
        if (item == null || shelf == null || qty <= 0 || !shelf.getItems().contains(item) || qty > item.getQty()) {
            if (item != null && shelf != null && qty > item.getQty()) {
                System.out.println("Cannot remove " + qty + " of '" + item.getDesc() + "'. Only " + item.getQty() + " available on shelf #" + shelf.getShelfNumber());
            }
            return false;
        }

        if (qty == item.getQty()) shelf.getItems().removeFirstHit(item);

        double itemWeight = item.getUnitSize() * qty / 1000.0;
        shelf.setCurrentWeight(shelf.getCurrentWeight() - itemWeight);
        shelf.setCurrentQuantity(shelf.getCurrentQuantity() - qty);
        item.setQty(item.getQty() - qty);

        System.out.println("Removed " + qty + " of '" + item.getDesc() + "' from shelf #" + shelf.getShelfNumber());
        return true;
    }

    /**
     * VALIDATOR
     * place items onto shelves after adding
     */

    private void placeItemOnShelves(GoodItem item, FunkierList<Shelf> shelves, double unitSize, int qty) {
        double weight = unitSize * qty / 1000.0;

        int added = shelves.sumIntValues(shelf -> {
            if (weight <= shelf.getAvailableSpace()) {
                shelf.getItems().addAtLast(item);
                shelf.setCurrentWeight(shelf.getCurrentWeight() + weight);
                shelf.setCurrentQuantity(shelf.getCurrentQuantity() + qty);
                return 1;
            }
            return 0;
        });

        if (added == 0) {
            System.out.println("Item '" + item.getDesc() + "' does not fit on any shelf.");
        }
    }
    /**
     * ITEM STAT SUMMARY
     */
    public String getItemStatistics() {
        int totalItems = system.getItemCount();
        int totalQty = system.getTotalInvQty();
        double totalValue = system.getTotalInvValue();
        double avgPrice = getAverageItemPrice();
        int unrefrigerated = system.getUnrefrigeratedItems();
        int refrigerated = system.getRefrigeratedItems();
        int frozen = system.getFrozenItems();

        StringBuilder result = new StringBuilder();
        result.append("Item Statistics:\n")
                .append("Total Unique Items: ").append(totalItems).append("\n")
                .append("Total Quantity: ").append(totalQty).append("\n")
                .append("Total Inventory Value: €").append(InputVal.twoDecPlaces(totalValue)).append("\n")
                .append("Average Item Price: €").append(InputVal.twoDecPlaces(avgPrice)).append("\n")
                .append("Unrefrigerated Items: ").append(unrefrigerated).append("\n")
                .append("Refrigerated Items: ").append(refrigerated).append("\n")
                .append("Frozen Items: ").append(frozen).append("\n");

        return result.toString();
    }


}