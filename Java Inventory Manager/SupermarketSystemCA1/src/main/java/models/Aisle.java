package models;

import utils.CustomList.FunkierList;
import utils.Dimensions;
import utils.Validators.InputVal;

/**
 * Aisle class stores:
 * aisle name (e.g. Cheese, Bread),
 * aisle dimensions (length, width, etc.),
 * temperature (i.e. Unrefrigerated, Refrigerated, or Frozen)
 * -
 * unique aisle methods are handled in AisleManager.java
 */

public class Aisle implements java.io.Serializable {
    //declaring variables
    private static int idIncrement = 1000;
    //dimensions
    private final Dimensions dimensions;
    private final int id;
    private String name = "Untitled";
    private String temperature;
    //associate classes
    //belong to only one
    private FloorArea floorArea;
    //may contain many:
    private FunkierList<Shelf> shelves;

    /**
     * Aisle constructor with Dimensions object
     *
     * @param name        aisle name
     * @param temperature temperature category
     * @param dimensions  dimensions object containing length, width, posX, posY, orientation
     * @param floorArea   parent floor area
     * @param shelves     list of shelves in this aisle
     */
    public Aisle(String name, String temperature, Dimensions dimensions, FloorArea floorArea, FunkierList<Shelf> shelves) {
        this.id = idIncrement++;
        setName(name);
        setTemperature(temperature);
        this.dimensions = dimensions;
        setFloorArea(floorArea);
        setShelves(shelves);
    }

    //getters
    public String getName() {
        return name;
    }

    //setters
    public void setName(String name) {
        if (InputVal.validStringlength(name, 30)) {
            this.name = name;
        }
    }

    public int getId() {
        return id;
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public String getTemp() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        if (temperature != null) {
            this.temperature = temperature;
        }
    }

    public FloorArea getFloorArea() {
        return this.floorArea;
    }

    public void setFloorArea(FloorArea floorArea) {
        if (floorArea == null) {
            System.out.println("Floor must be set.");
        }
        this.floorArea = floorArea;
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

    public double getArea() {
        return dimensions.getArea();
    }

    @Override
    public String toString() {
        return id + " " + name + " on floor " + getFloorArea().getTitle() + " (" + temperature + ") with dimensions " + dimensions + " and area of " + getArea() + "m^2";
    }
}