package controllers.managers;

import controllers.API.SupermarketAPI;
import models.Aisle;
import models.GoodItem;
import models.Shelf;
import utils.CustomList.FunkierList;
import utils.Validators.InputVal;

/**
 * ShelfManager
 * handles methods unique to Shelf model.
 */
public class ShelfManager {
    private final SupermarketAPI system;

    public ShelfManager(SupermarketAPI system) {
        this.system = system;
    }

    /**
     * ADD - object, weight, quantity, item to shelf
     */
    public boolean addShelf(double capacity, double currentWeight, int currentQuantity, Aisle aisle, FunkierList<GoodItem> items) {
        if (aisle == null) {
            System.out.println("Aisle has not been assigned.");
            return false;
        }

        Shelf newShelf = new Shelf(capacity, currentWeight, currentQuantity, aisle, items);
        aisle.getShelves().addAtLast(newShelf);
        system.addShelf(newShelf);

        System.out.println("Added " + newShelf);
        return true;
    }

    public boolean addWeight(int shelfNumber, double weight) {
        Shelf shelf = findShelfByNumber(shelfNumber);
        if (shelf == null) return false;

        if (hasSpace(shelfNumber, weight)) {
            shelf.setCurrentWeight(shelf.getCurrentWeight() + weight);
            return true;
        }
        return false;
    }

    public boolean addQuantity(int shelfNumber, int quantity) {
        Shelf shelf = findShelfByNumber(shelfNumber);
        if (shelf == null) return false;

        shelf.setCurrentQuantity(shelf.getCurrentQuantity() + quantity);
        return true;
    }

    public boolean addItemToShelf(int shelfNumber, GoodItem item) {
        Shelf shelf = findShelfByNumber(shelfNumber);
        if (shelf == null || item == null) return false;

        double itemWeight = item.getUnitSize() * item.getQty() / 1000.0;
        if (hasSpace(shelfNumber, itemWeight)) {
            shelf.getItems().addAtLast(item);
            addWeight(shelfNumber, itemWeight);
            addQuantity(shelfNumber, item.getQty());
            System.out.println("Added item to shelf #" + shelfNumber);
            return true;
        } else {
            System.out.println("Cannot add item shelf #" + shelfNumber + " has insufficient space");
            return false;
        }
    }

    /**
     * FIND BY - number, aisle
     */

    public Shelf findShelfByNumber(int shelfNumber) {
        return system.findShelfByNumber(shelfNumber);  // Remove print - let caller handle
    }

    public FunkierList<Shelf> findShelvesByAisle(Aisle aisle) {
        return system.getShelves().findAll(shelf -> shelf != null && shelf.getAisle() != null && shelf.getAisle().equals(aisle));
    }

    /**
     * LIST BY - number, aisle
     */

    public String listShelfNumbers() {
        FunkierList<Shelf> allShelves = system.getShelves();
        if (allShelves.isEmpty()) return "No shelves.";

        StringBuilder result = new StringBuilder("All Shelf Numbers:\n");
        allShelves.forEach(shelf -> {
            if (shelf != null) {
                result.append("Shelf #").append(shelf.getShelfNumber()).append("\n");
            }
        });
        return result.toString();
    }

    public String listShelvesByAisle(Aisle aisle) {
        FunkierList<Shelf> shelves = findShelvesByAisle(aisle);
        if (shelves.isEmpty()) {
            return "No shelves in aisle: " + aisle.getName();
        }

        StringBuilder result = new StringBuilder("Shelves in aisle " + aisle.getName() + ":\n");
        shelves.forEach(shelf -> {
            if (shelf != null) {
                result.append(shelf).append("\n");
            }
        });
        return result.toString();
    }

    /**
     * GET ALL SHELF - items, shelves, weight, available shelves
     *
     * @return
     */

    public int getAllShelfItems() {
        return system.getShelves().sumIntValues(shelf -> shelf != null ? shelf.getCurrentQuantity() : 0);
    }

    public FunkierList<Shelf> getEmptyShelves() {
        return system.getShelves().findAll(shelf -> shelf != null && shelf.getCurrentWeight() == 0);
    }

    public double getTotalShelfWeight() {
        return system.getShelves().sumValues(shelf -> shelf != null ? shelf.getCurrentWeight() : 0.0);
    }

    /**
     * REMOVE BY - object, number
     *
     * @param shelf
     * @return
     */

    public boolean removeShelf(Shelf shelf) {
        if (shelf == null) return false;

        //clear items
        int itemCount = shelf.getItems().getSize();
        if (itemCount > 0) {
            shelf.getItems().forEach(item -> {
                if (item != null && item.getShelves() != null) {
                    item.getShelves().removeFirstHit(shelf);
                }
            });
            shelf.getItems().clear();
            System.out.println("Cleared " + itemCount + " items from shelf #" + shelf.getShelfNumber());
        }

        shelf.setCurrentWeight(0.0);
        shelf.setCurrentQuantity(0);
        if (shelf.getAisle() != null) {
            shelf.getAisle().getShelves().removeFirstHit(shelf);
        }
        boolean removed = system.removeShelf(shelf);
        if (removed) {
            System.out.println("Removed shelf: #" + shelf.getShelfNumber());
        }
        return removed;
    }

    public boolean removeShelfByNumber(int shelfNumber) {
        Shelf shelf = findShelfByNumber(shelfNumber);
        if (shelf == null) {
            System.out.println("Shelf #" + shelfNumber + " not found.");
            return false;
        }
        return removeShelf(shelf);
    }

    /**
     * REMOVE X FROM SHELF - item
     */
    public boolean removeItemFromShelf(int shelfNumber, GoodItem item) {
        Shelf shelf = findShelfByNumber(shelfNumber);
        if (shelf == null || item == null) return false;

        boolean removed = shelf.getItems().removeFirstHit(item);
        if (!removed) {
            System.out.println("Item not found on shelf #" + shelfNumber);
            return false;
        }

        double itemWeight = item.getUnitSize() * item.getQty() / 1000.0;
        shelf.setCurrentWeight(shelf.getCurrentWeight() - itemWeight);
        shelf.setCurrentQuantity(shelf.getCurrentQuantity() - item.getQty());

        System.out.println("Removed item from shelf #" + shelfNumber);
        return true;
    }

    /**
     * VALIDATOR - check shelf has space to add item
     */
    public boolean hasSpace(int shelfNumber, double weight) {
        Shelf shelf = findShelfByNumber(shelfNumber);
        if (shelf == null) return false;

        return shelf.getCurrentWeight() + weight <= shelf.getCapacity();
    }

    /**
     * SHELF STATISTICS
     */
    public String getShelfStatistics() {
        int totalShelves = system.getShelfCount();
        double totalCapacity = system.getShelves().sumValues(shelf -> shelf != null ? shelf.getCapacity() : 0.0);
        double totalUsed = system.getShelves().sumValues(shelf -> shelf != null ? shelf.getCurrentWeight() : 0.0);
        int totalItems = system.getShelves().sumIntValues(shelf -> shelf != null ? shelf.getCurrentQuantity() : 0);

        double totalAvailable = totalCapacity - totalUsed;
        double usedPercentage = totalCapacity > 0 ? (totalUsed / totalCapacity * 100) : 0;
        int emptyShelves = getEmptyShelves().getSize();
        double avgItemsPerShelf = totalShelves > 0 ? (double) totalItems / totalShelves : 0.0;

        StringBuilder result = new StringBuilder();
        result.append("Shelf Statistics:\n")
                .append("Total Shelves: ").append(totalShelves).append("\n")
                .append("Total Capacity: ").append(InputVal.twoDecPlaces(totalCapacity)).append(" kg\n")
                .append("Total Used: ").append(InputVal.twoDecPlaces(totalUsed)).append(" kg (").append(InputVal.twoDecPlaces(usedPercentage)).append("%)\n")
                .append("Total Available: ").append(InputVal.twoDecPlaces(totalAvailable)).append(" kg\n")
                .append("Total Items: ").append(totalItems).append("\n")
                .append("Average Items per Shelf: ").append(InputVal.twoDecPlaces(avgItemsPerShelf)).append("\n")
                .append("Empty Shelves: ").append(emptyShelves).append("\n");

        return result.toString();
    }
}