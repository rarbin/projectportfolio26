package controllers.managers;

import controllers.API.SupermarketAPI;
import models.*;
import utils.CustomList.FunkierList;

/**
 * SmartAddManager: add based on following criteria in order
 * 1. duplicates
 * 2. keywords
 * 3. match temp + space
 */
public class SmartAddManager {
    private final SupermarketAPI system;
    private final ShelfManager shelfManager;
    private FunkierList<String> keywords;

    public SmartAddManager(SupermarketAPI system) {
        this.system = system;
        this.shelfManager = new ShelfManager(system);
        this.keywords = extractKeywords();
    }

    /**
     * Identify unique keywords from all existing item descriptions.
     *
     * @return list of unique keywords
     */
    private FunkierList<String> extractKeywords() {
        FunkierList<String> keywords = new FunkierList<>();

        system.getGoodItems().forEach(item -> {
            String[] words = item.getDesc().toLowerCase().split("[\\s\\-\\.]+");
            for (String word : words) {
                String cleanWord = word.replaceAll("[^a-z]", "");
                if (cleanWord.length() > 3 && !keywords.contains(cleanWord)) {
                    keywords.addAtLast(cleanWord);
                }
            }
        });

        return keywords;
    }


    /**
     * Priority as follows:
     * 1. identical dupes 2. keyword 3) temp + space
     *
     * @param newItem the item to add
     * @return success/fail + location
     */

    public SmartAdd smartAddItem(GoodItem newItem) {
        String reqTemp = newItem.getStorageCategory();
        double weight = newItem.getUnitSize() * newItem.getQty() / 1000.0;

        // 1. check dupe
        Shelf duplicateShelf = findDuplicateShelf(newItem);
        if (duplicateShelf != null && addItemToShelf(newItem, duplicateShelf)) {
            return successResult("Added with duplicate item", duplicateShelf);
        }

        // 2. check keywords
        Shelf keywordShelf = findKeywordShelf(newItem, reqTemp, weight);
        if (keywordShelf != null && addItemToShelf(newItem, keywordShelf)) {
            return successResult("Added with similar items", keywordShelf);
        }

        // 3. check temp + space
        Shelf tempSpaceShelf = system.getShelves().find(shelf -> shelf.getAisle().getTemp().equals(reqTemp) && shelfManager.hasSpace(shelf.getShelfNumber(), weight));
        if (tempSpaceShelf != null && addItemToShelf(newItem, tempSpaceShelf)) {
            return successResult("Added with matching temperature and space", tempSpaceShelf);
        }

        return new SmartAdd(false, "No suitable location found", null, null, null);
    }

    private Shelf findDuplicateShelf(GoodItem newItem) {
        return system.getShelves().find(shelf -> shelf.getItems().find(item -> item.matches(newItem)) != null);
    }

    private Shelf findKeywordShelf(GoodItem newItem, String reqTemp, double weight) {
        String matchingKeyword = getMatchingKeyword(newItem.getDesc());
        if (matchingKeyword.isEmpty()) return null;

        return system.getShelves().find(shelf -> shelf.getAisle().getTemp().equals(reqTemp) && shelfManager.hasSpace(shelf.getShelfNumber(), weight) && shelf.getItems().find(item -> item.getDesc().toLowerCase().contains(matchingKeyword)) != null);
    }



    /**
     * Extract keyword to match for grouping
     *
     * @param description the item description to analyze
     * @return a matching keyword, or empty string if no match found
     */
    private String getMatchingKeyword(String description) {
        String desc = description.toLowerCase();
        String[] words = desc.split("[\\s\\-\\.]+");

        for (String word : words) {
            String clean = word.replaceAll("[^a-z]", "");
            if (clean.length() > 3 && keywords.contains(clean)) {
                return clean;
            }
        }
        return "";
    }

    /**
     * yay
     *
     * @param message
     * @param shelf   shelf where item was added
     * @return success true/false + location
     */
    private SmartAdd successResult(String message, Shelf shelf) {
        Aisle aisle = shelf.getAisle();
        FloorArea area = aisle.getFloorArea();
        return new SmartAdd(true, message + ": ", area, aisle, shelf);
    }

    private boolean addItemToShelf(GoodItem item, Shelf shelf) {
        return shelfManager.addItemToShelf(shelf.getShelfNumber(), item);
    }
}