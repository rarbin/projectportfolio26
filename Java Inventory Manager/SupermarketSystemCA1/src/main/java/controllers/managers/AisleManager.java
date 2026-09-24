package controllers.managers;

import controllers.API.SupermarketAPI;
import models.Aisle;
import models.FloorArea;
import models.Shelf;
import utils.CustomList.FunkierList;
import utils.Dimensions;
import utils.Validators.InputVal;

/**
 * AisleAreaManager
 * handles methods unique to Aisle model.
 */

public class AisleManager {
    private final SupermarketAPI system;

    public AisleManager(SupermarketAPI system) {
        this.system = system;
    }

    /**
     * ADD - object
     */
    public boolean addAisle(String aisleName, String temperature, Dimensions dimensions, FloorArea floorArea, FunkierList<Shelf> shelves) {
        if (floorArea == null) {
            System.out.println("Floor area has not been assigned.");
            return false;
        }
        if (!system.isAisleNameUnique(aisleName)) {
            System.out.println(aisleName + " is an existing aisle.");
            return false;
        }

        Aisle newAisle = new Aisle(aisleName, temperature, dimensions, floorArea, shelves);
        floorArea.getAisles().addAtLast(newAisle);

        if (!system.getAisles().contains(newAisle)) {
            system.addAisle(newAisle);
        }

        System.out.println("Added aisle: " + newAisle);
        return true;
    }

    /**
     * FIND BY - name, floor area, temperature
     */
    public Aisle findAisle(String aisleName) {
        return system.getAisles().find(aisle -> aisle != null && aisle.getName().equalsIgnoreCase(aisleName));
    }

    public FunkierList<Aisle> findAislesByFloorArea(String floorAreaTitle) {
        return system.getAisles().findAll(aisle -> aisle != null && aisle.getFloorArea() != null && aisle.getFloorArea().getTitle().equalsIgnoreCase(floorAreaTitle));
    }

    public FunkierList<Aisle> findAislesByTemp(String temperature) {
        return system.getAisles().findAll(aisle -> aisle != null && aisle.getTemp() != null && aisle.getTemp().equalsIgnoreCase(temperature));
    }

    /**
     * LIST BY - temperature, floor area
     */

    public String listAllAisles() {
        return listAisles(system.getAisles(), "Available aisles:\n", false);
    }

    public String listAisleTitles() {
        return listAisles(system.getAisles(), "All Aisle Titles:\n", true);
    }

    public String listAislesByTemp(String temperature) {
        FunkierList<Aisle> tempAisles = findAislesByTemp(temperature);
        if (tempAisles.isEmpty()) {
            return "No aisles with temperature: " + temperature;
        }
        return listAisles(tempAisles, "Aisles with temperature " + temperature + ":\n", false);
    }

    public String listAislesByFloorArea(String floorAreaTitle) {
        FunkierList<Aisle> floorAisles = findAislesByFloorArea(floorAreaTitle);
        if (floorAisles.isEmpty()) {
            return "No aisles on floor area: " + floorAreaTitle;
        }
        return listAisles(floorAisles, "Aisles on floor area '" + floorAreaTitle + "':\n", false);
    }

    //generic
    private String listAisles(FunkierList<Aisle> aisles, String header, boolean titlesOnly) {
        if (aisles.isEmpty()) {
            return header.isEmpty() ? "No aisles." : header;
        }
        StringBuilder result = new StringBuilder(header);
        final int[] index = {1};
        aisles.forEach(aisle -> {
            if (aisle != null) {
                if (titlesOnly) {
                    result.append("  ").append(index[0]++).append(") ").append(aisle.getName()).append("\n");
                } else {
                    String floorTitle = aisle.getFloorArea() != null ? aisle.getFloorArea().getTitle() : "Unknown floor";
                    result.append("  ").append(index[0]++).append(") ").append(aisle.getName()).append(" - Floor: ").append(floorTitle).append(", Temp: ").append(aisle.getTemp()).append(", Shelves: ").append(aisle.getShelves().getSize()).append("\n");
                }
            }
        });
        return result.toString();
    }

    /**
     * UPDATE - name, temperature
     */

    public boolean updateAisleName(String currentName, String newName) {
        Aisle aisle = findAisle(currentName);
        if (aisle == null) return false;

        if (!currentName.equalsIgnoreCase(newName) && !system.isAisleNameUnique(newName)) {
            System.out.println("Aisle '" + newName + "' already exists.");
            return false;
        }

        aisle.setName(newName);
        System.out.println("Updated aisle name from '" + currentName + "' to '" + newName + "'");
        return true;
    }

    public boolean updateAisleTemperature(String aisleName, String newTemperature) {
        Aisle aisle = findAisle(aisleName);
        if (aisle == null) return false;

        if (newTemperature == null || newTemperature.trim().isEmpty()) {
            System.out.println("Temperature cannot be empty.");
            return false;
        }

        aisle.setTemperature(newTemperature);
        System.out.println("Updated aisle '" + aisleName + "' temperature to " + newTemperature);
        return true;
    }

    /**
     * REMOVE BY - object, name
     */

    public boolean removeAisle(Aisle aisle) {
        if (aisle == null) return false;

        //remove associated shelves
        int shelfCount = aisle.getShelves().sumIntValues(shelf -> {
            new ShelfManager(system).removeShelf(shelf);
            return 1;
        });

        if (aisle.getFloorArea() != null) {
            aisle.getFloorArea().getAisles().removeFirstHit(aisle);
        }

        boolean removed = system.removeAisle(aisle);
        if (removed) {
            System.out.println("Removed aisle: " + aisle.getName() + " and " + shelfCount + " shelves");
        }
        return removed;
    }

    public boolean removeAisleByName(String aisleName) {
        Aisle aisle = findAisle(aisleName);
        if (aisle == null) {
            System.out.println("Aisle '" + aisleName + "' not found.");
            return false;
        }
        return removeAisle(aisle);
    }

    /**
     * REMOVE X FROM AISLE - shelf
     */

    public boolean removeShelfFromAisle(String floorAreaTitle, String aisleName, Shelf shelf) {
        Aisle aisle = findAisle(aisleName);
        if (aisle == null) return false;

        if (aisle.getFloorArea() == null || !aisle.getFloorArea().getTitle().equalsIgnoreCase(floorAreaTitle)) {
            System.out.println("Aisle '" + aisleName + "' is not in floor area '" + floorAreaTitle + "'");
            return false;
        }

        boolean removed = aisle.getShelves().removeFirstHit(shelf);
        if (removed) {
            System.out.println("Removed shelf #" + shelf.getShelfNumber() + " from aisle '" + aisleName + "' in floor area '" + floorAreaTitle + "'");
        } else {
            System.out.println("Shelf #" + shelf.getShelfNumber() + " not found in aisle '" + aisleName + "'");
        }
        return removed;
    }


    /**
     * TOTAL - value, items, shelves
     */
    public double getTotalValueInAisles() {
        return system.getAisles().sumValues(aisle -> aisle != null ? system.getValueFromAisle(aisle) : 0.0);
    }

    public int getTotalItemsInAisles() {
        return system.getAisles().sumIntValues(aisle -> aisle != null ? system.getItemsFromAisle(aisle) : 0);
    }

    public int getTotalShelfCount() {
        return system.getAisles().sumIntValues(aisle -> aisle != null ? aisle.getShelves().getSize() : 0);
    }

    /**
     * AISLE STATISTICS
     */
    public String getAisleStatistics() {
        int totalAisles = system.getAisleCount();
        int totalShelves = getTotalShelfCount();
        int totalItems = getTotalItemsInAisles();
        double totalValue = getTotalValueInAisles();
        double avgShelvesPerAisle = totalAisles > 0 ? (double) totalShelves / totalAisles : 0.0;

        StringBuilder result = new StringBuilder();
        result.append("Aisle Statistics:\n")
                .append("Total Aisles: ").append(totalAisles).append("\n")
                .append("Total Shelves: ").append(totalShelves).append("\n")
                .append("Total Items: ").append(totalItems).append("\n")
                .append("Total Value: €").append(InputVal.twoDecPlaces(totalValue)).append("\n")
                .append("Average Shelves per Aisle: ").append(avgShelvesPerAisle).append("\n");

        return result.toString();
    }

}