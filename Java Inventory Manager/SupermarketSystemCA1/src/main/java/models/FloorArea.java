package models;

import utils.CustomList.FunkierList;
import utils.Dimensions;
import utils.Validators.InputVal;

/**
 * FloorArea class stores:
 * floor area title (e.g. Household, Fruit and Veg, Dairy, Meat)
 * level (e.g. ground-floor, first floor, etc.)
 * -
 * floor area methods handled in FloorAreaManager.java.
 */

public class FloorArea implements java.io.Serializable {

    //declaring variables
    private String title = "Untitled";
    private final int level;
    private final Dimensions dimensions;

    //associate classes
    //may contain many:
    private FunkierList<Aisle> aisle = null;


    /**
     * Floor area constructor
     *
     * @param title      provided name
     * @param level      floor level
     * @param dimensions width, length, posx, posy, orientation
     */

    public FloorArea(String title, int level, Dimensions dimensions, FunkierList<Aisle> aisle) {
        setTitle(title);
        this.level = level;
        this.dimensions = dimensions;
        setAisles(aisle);
    }


    //setters
    public void setTitle(String title) {
        if (InputVal.validStringlength(title, 20)) {
            this.title = title;
        }
    }

    public void setAisles(FunkierList<Aisle> aisle) {
        if (aisle == null) {
            this.aisle = new FunkierList<>();
        } else {
            this.aisle = aisle;
        }
    }

    //getters
    public String getTitle() {
        return title;
    }

    public int getLevel() {
        return level;
    }

    public FunkierList<Aisle> getAisle() {
        return aisle;
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public double getArea() {
        return dimensions.getArea();
    }

    public FunkierList<Aisle> getAisles() {
        return aisle;
    }

    @Override
    public String toString() {
        return title + " is on level " + level + " with dimensions " + dimensions + " and total area of " + getArea() + "m^2. Contains " + aisle.getSize() + " aisles.";
    }

}
