package models;

import utils.CustomList.FunkierList;
import utils.Validators.InputVal;
import utils.Validators.TempValidator;

/**
 * GoodItem class stores:
 * description of item unit
 * the unit size/weight (e.g. in grams)
 * unit price
 * unit quantity
 * storage temp (i.e.Unrefrigerated, Refrigerated, or Frozen)
 * photo of the good item (as a URL)
 * -
 * unique good item methods handled in GoodItemManager.java
 */

public class GoodItem implements java.io.Serializable {
    private final double storageTemp;
    //declaring variables
    private String description = "UNKNOWN";
    private double unitSize = 0.0;
    private double unitPrice = 0.00;
    private int quantity = 0;
    private String photoUrl;

    //associate classes
    private Aisle aisle;
    private FunkierList<Shelf> shelves;

    /**
     * Good item construction
     * @param description detail of good item
     * @param unitSize weight (g) of item
     * @param unitPrice cost of item
     * @param quantity total amount of item
     * @param storageTemp temperature
     * @param photoUrl url link (optional)
     * @param aisle associated aisle
     * @param shelves associated shelves
     */
    public GoodItem(String description, double unitSize, double unitPrice, int quantity, double storageTemp, String photoUrl, Aisle aisle, FunkierList<Shelf> shelves) {
        setDescription(description);
        setUnitSize(unitSize);
        setUnitPrice(unitPrice);
        setQty(quantity);
        this.storageTemp = storageTemp;
        setPhotoUrl(photoUrl);
        setAisle(aisle);
        setShelves(shelves);
    }

    //setters

    public void setDescription(String description) {
        if (InputVal.validStringlength(description, 30)) {
            this.description = description;
        }
    }

    public void setUnitPrice(double unitPrice) {
        if (InputVal.validRangeDouble(unitPrice, 1, 999.99)) {
            this.unitPrice = unitPrice;
        }
    }

    public void setQty(int quantity) {
        if (InputVal.validRangeInt(quantity, 1, 999)) {
            this.quantity = quantity;
        }
    }

    public String getDesc() {
        return description;
    }

    public double getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(double unitSize) {
        if (InputVal.validRangeDouble(unitSize, 1, 999.99)) {
            this.unitSize = unitSize;
        }
    }

    public double getPrice() {
        return unitPrice;
    }
    //getters

    public int getQty() {
        return quantity;
    }

    public double getStorageTemp() {
        return storageTemp;
    }

    public String getStorageCategory() {
        return TempValidator.tempToCategory(storageTemp);
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        if (InputVal.validStringlength(photoUrl, 150)) {
            this.photoUrl = photoUrl;
        } else {
            System.out.println("Photo URL too long");
            this.photoUrl = "No photo.";
        }
    }

    public double getTotalValue() {
        return unitPrice * quantity;
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

    public FunkierList<Shelf> getShelves() {
        return shelves;
    }

    public void setShelves(FunkierList<Shelf> shelves) {
        if (shelves == null) {
            this.shelves = new FunkierList<>();
        } else {
            this.shelves = shelves;
        }
    }

    public boolean matches(GoodItem other) {
        if (other == null) return false;
        return this.description.equalsIgnoreCase(other.description) && this.unitSize == other.unitSize;
    }

    @Override
    public String toString() {
        return getStorageCategory() + description + " of weight (g) " + unitSize + " and price " + unitPrice + " total " + quantity + " units with total value of " + getTotalValue();
    }
}
