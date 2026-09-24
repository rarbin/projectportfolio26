package models;

import utils.CustomList.FunkierList;

/**
 * Shelf class stores:
 * shelf number incrementally
 * capacity of shelf (weight [kg])
 * current weight of items on shelf
 * current quantity of items on shelf
 * -
 * unique shelf methods handled in ShelfManager.java
 */

public class Shelf implements java.io.Serializable {
    //declaring variables
    private static int shelfCount = 0;
    private final double capacity;
    private final int shelfNumber;
    private double currentWeight = 0.0;
    private int currentQuantity = 0;

    //associate classes
    private Aisle aisle;
    private FunkierList<GoodItem> items;

    /**
     * Shelf constructor
     * @param capacity total kg capacity
     * @param currentWeight current item weight
     * @param currentQuantity current item amount
     * @param aisle aisle summary
     * @param items items summary
     */
    public Shelf(double capacity, double currentWeight, int currentQuantity, Aisle aisle, FunkierList<GoodItem> items) {
        this.shelfNumber = shelfCount++;
        this.capacity = capacity;
        setCurrentWeight(currentWeight);
        setCurrentQuantity(currentQuantity);
        setAisle(aisle);
        setItems(items);
    }

    //setters

    public int getShelfNumber() {
        return shelfNumber;
    }

    public double getCapacity() {
        return capacity;
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(double currentWeight) {
        if (currentWeight >= 0 && currentWeight <= capacity) {
            this.currentWeight = currentWeight;
        }
    }

    //getters

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int quantity) {
        if (quantity >= 0) {
            this.currentQuantity = quantity;
        }
    }

    public Aisle getAisle() {
        return this.aisle;
    }

    public void setAisle(Aisle aisle) {
        if (aisle == null) {
            System.out.println("Aisle must be set.");
        }
        this.aisle = aisle;
    }

    public FunkierList<GoodItem> getItems() {
        return items;
    }

    public void setItems(FunkierList<GoodItem> items) {
        if (items == null) {
            this.items = new FunkierList<>();
        } else {
            this.items = items;
        }
    }

    public double getAvailableSpace() {
        return capacity - currentWeight;
    }

    @Override
    public String toString() {
        return "Shelf #" + shelfNumber + " capacity: " + capacity + "kg, currently using " + currentWeight + "kg, " + currentQuantity + " items, " + getAvailableSpace() + "kg available";
    }
}