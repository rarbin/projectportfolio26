package models;

/**
 * Smart add class:
 * result object for smart add manager
 */
public class SmartAdd {
    private final boolean success;
    private final String message;
    private final FloorArea floorArea;
    private final Aisle aisle;
    private final Shelf shelf;

    public SmartAdd(boolean success, String message, FloorArea floorArea, Aisle aisle, Shelf shelf) {
        this.success = success;
        this.message = message;
        this.floorArea = floorArea;
        this.aisle = aisle;
        this.shelf = shelf;
    }

    public boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public FloorArea getFloorArea() {
        return floorArea;
    }

    public Aisle getAisle() {
        return aisle;
    }

    public Shelf getShelf() {
        return shelf;
    }

    @Override
    public String toString() {
        if (success && floorArea != null && aisle != null && shelf != null) {
            return "Smart Add successful: " + message +
                    " - Location: " + floorArea.getTitle() +
                    " in aisle " + aisle.getName() +
                    " and Shelf #" + shelf.getShelfNumber();
        } else if (success) {
            return "Smart Add successful: " + message;
        } else {
            return "Smart Add Failed: " + message;
        }
    }
}