package controllers.managers;

import controllers.API.SupermarketAPI;
import models.Aisle;
import models.FloorArea;
import utils.CustomList.FunkierList;
import utils.Dimensions;
import utils.Validators.InputVal;

/**
 * FloorAreaManager
 * handles methods unique to FloorArea model.
 */

public class FloorAreaManager {
    private final SupermarketAPI system;

    public FloorAreaManager(SupermarketAPI system) {
        this.system = system;
    }

    /**
     * ADD - floor area
     */
    public boolean addFloorArea(String title, int level, Dimensions dimensions, FunkierList<Aisle> aisles) {
        if (!InputVal.validStringlength(title, 20)) {
            System.out.println("Title must be less than 20 characters.");
            return false;
        }

        if (!isFloorAreaTitleUnique(title)) {
            System.out.println(title + " already exists.");
            return false;
        }

        FloorArea newArea = new FloorArea(title, level, dimensions, aisles);
        system.addFloorArea(newArea);

        System.out.println("Added " + newArea);
        return true;
    }

    /**
     * ADD - aisle to floor
     */
    public boolean addAisleToFloorArea(String floorAreaTitle, Aisle aisle) {
        FloorArea area = findFloorAreaByTitle(floorAreaTitle);
        if (area == null || aisle == null) return false;

        aisle.setFloorArea(area);
        area.getAisles().addAtLast(aisle);

        System.out.println("Added aisle '" + aisle.getName() + "' to floor area '" + floorAreaTitle + "'");
        return true;
    }

    /**
     * FIND BY - index, title, level
     */

    //INDEX
    public FloorArea findFloorAreaByIndex(int index) {
        if (index < 0 || index >= system.getFloorAreaCount()) {
            System.out.println("Invalid floor area index: " + index);
            return null;
        }
        return system.getFloorAreas().get(index);
    }

    //TITLE
    public FloorArea findFloorAreaByTitle(String title) {
        FloorArea area = system.getFloorAreas().find(fa -> fa != null && fa.getTitle().equalsIgnoreCase(title));
        if (area == null) {
            System.out.println("Floor area '" + title + "' not found.");
        }
        return area;
    }

    //LEVEL
    public FunkierList<FloorArea> findFloorAreasByLevel(int level) {
        FunkierList<FloorArea> levelAreas = system.getFloorAreas().findAll(fa -> fa != null && fa.getLevel() == level);
        System.out.println("Found " + levelAreas.getSize() + " floor areas on level " + level);
        return levelAreas;
    }

    /**
     * LIST BY - title, index, level,  ALL
     */

    public String listFloorAreaTitles() {
        //titles only boolean allows this listing to work with the same generic as other methods
        return listAreas(system.getFloorAreas(), "All Floor Area Titles:\n", true);
    }

    public String listFloorByIndex() {
        return listAreas(system.getFloorAreas(), "All Floor Areas (Index)", false);
    }

    public String listFloorAreasByLevel(int level) {
        FunkierList<FloorArea> areasOnLevel = findFloorAreasByLevel(level);

        if (areasOnLevel.isEmpty()) {
            return "No floor areas on level " + level + ".";
        }
        return listAreas(areasOnLevel, "Floor areas on level " + level + ":\n", false);
    }

    //generic
    private String listAreas(FunkierList<FloorArea> areas, String header, boolean titlesOnly) {
        if (areas.isEmpty()) {
            return header.isEmpty() ? "No floor areas." : header;
        }
        StringBuilder result = new StringBuilder(header);
        final int[] index = {0};
        areas.forEach(area -> {
            if (area != null) {
                result.append(index[0]++).append(". ").append(titlesOnly ? area.getTitle() : area.toString()).append("\n");
            }
        });

        return result.toString();
    }

    /**
     * UPDATE
     */
    public boolean updateFloorAreaTitle(String currentTitle, String newTitle) {
        if (!InputVal.validStringlength(newTitle, 20)) {
            System.out.println("New title must be 20 characters or less.");
            return false;
        }

        FloorArea area = findFloorAreaByTitle(currentTitle);
        if (area == null) return false;

        if (!currentTitle.equalsIgnoreCase(newTitle) && !isFloorAreaTitleUnique(newTitle)) {
            System.out.println("Floor area title '" + newTitle + "' already exists.");
            return false;
        }

        area.setTitle(newTitle);
        System.out.println("Updated floor area title from '" + currentTitle + "' to '" + newTitle + "'");
        return true;
    }

    /**
     * REMOVE BY - object, title, index, level
     */
    //OBJECT
    public boolean removeFloorArea(FloorArea area) {
        if (area == null) return false;

        int removedAisles = area.getAisles().sumIntValues(aisle ->
                new AisleManager(system).removeAisle(aisle) ? 1 : 0
        );

        boolean removed = system.removeFloorArea(area);
        if (removed) {
            System.out.println("Removed floor area: " + area.getTitle() + " and " + removedAisles + " aisles");
        }
        return removed;
    }

    //TITLE
    public boolean removeFloorAreaByTitle(String title) {
        FloorArea area = findFloorAreaByTitle(title);
        if (area == null) {
            System.out.println("Floor area '" + title + "' not found.");
            return false;
        }
        return removeFloorArea(area);
    }

    //INDEX
    public boolean removeFloorAreaByIndex(int index) {
        FloorArea area = findFloorAreaByIndex(index);
        if (area == null) return false;

        return removeFloorArea(area);
    }

    public boolean removeAllFloorAreasByLevel(int level) {
        FunkierList<FloorArea> areasToRemove = findFloorAreasByLevel(level);

        if (areasToRemove.isEmpty()) {
            System.out.println("No floor areas found on level " + level);
            return false;
        }

        int removedCount = areasToRemove.sumIntValues(area -> removeFloorArea(area) ? 1 : 0);
        System.out.println("Removed " + removedCount + " floor area(s) from level " + level);
        return removedCount > 0;
    }

    /**
     * REMOVE X FROM FLOOR AREA - aisle
     */
    public boolean removeAisleFromFloorArea(String floorAreaTitle, Aisle aisle) {
        FloorArea area = findFloorAreaByTitle(floorAreaTitle);
        if (area == null || aisle == null) return false;

        boolean removed = area.getAisles().removeFirstHit(aisle);
        if (removed) {
            System.out.println("Removed aisle '" + aisle.getName() + "' from floor area '" + floorAreaTitle + "'");
        } else {
            System.out.println("Failed to remove.");
        }
        return removed;
    }

    /**
     * TOTAL - value, items, shelves
     */

    public double getTotalValueInFloorAreas() {
        return system.getFloorAreas().sumValues(area -> area != null ? system.getValueFromFloorArea(area) : 0.0);
    }

    public int getTotalItemsInFloorAreas() {
        return system.getFloorAreas().sumIntValues(area -> area != null ? system.getItemsFromFloor(area) : 0);
    }

    public int getTotalAisleCountInFloorAreas() {
        return system.getFloorAreas().sumIntValues(area -> area != null ? area.getAisles().getSize() : 0);
    }

    /**
     * HELPERS
     * Floor area unique title
     */
    private boolean isFloorAreaTitleUnique(String title) {
        return !system.getFloorAreas().anyMatch(area -> area != null && area.getTitle().equalsIgnoreCase(title));
    }

    /**
     * FLOOR STATISTICS
     */
    public String getFloorAreaStatistics() {
        int totalAreas = system.getFloorAreaCount();
        int totalItems = getTotalItemsInFloorAreas();
        double totalValue = getTotalValueInFloorAreas();
        int totalAisles = getTotalAisleCountInFloorAreas();
        double avgAislesPerArea = totalAreas > 0 ? (double) totalAisles / totalAreas : 0.0;

        StringBuilder result = new StringBuilder();
        result.append("Floor Area Statistics:\n")
                .append("Total Floor Areas: ").append(totalAreas).append("\n")
                .append("Total Aisles: ").append(totalAisles).append("\n")
                .append("Total Items: ").append(totalItems).append("\n")
                .append("Total Value: €").append(InputVal.twoDecPlaces(totalValue)).append("\n")
                .append("Average Aisles per Floor Area: ").append(avgAislesPerArea).append("\n");

        return result.toString();
    }

}