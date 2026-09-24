package controllers.managers;

import controllers.API.SupermarketAPI;
import models.Aisle;
import models.FloorArea;
import models.GoodItem;
import models.Shelf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import utils.CustomList.FunkierList;
import utils.Dimensions;

import static org.junit.jupiter.api.Assertions.*;

class GoodItemManagerTest {
    private GoodItemManager goodItemManager;
    private SupermarketAPI api;
    private FloorArea floorArea;
    private Aisle aisle;
    private Shelf shelf;

    @BeforeEach
    void setUp() {
        api = new SupermarketAPI();
        goodItemManager = new GoodItemManager(api);

        Dimensions floorDims = new Dimensions(100.0, 50.0, 0.0, 0.0, 10.0);
        floorArea = new FloorArea("Main Floor", 1, floorDims, new FunkierList<>());
        Dimensions aisleDims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
        aisle = new Aisle("Dairy", "REFRIGERATED", aisleDims, floorArea, new FunkierList<>());
        shelf = new Shelf(100.0, 0.0, 0, aisle, new FunkierList<>());
        aisle.getShelves().addAtLast(shelf);
    }

    @Nested
    class AddGoodItem {
        @Test
        void addGoodItemValid() {
            boolean result = goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            assertTrue(result);
            assertEquals(1, api.getGoodItems().getSize());
        }

        @Test
        void addGoodItemNullAisle() {
            boolean result = goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", null, new FunkierList<>());
            assertFalse(result);
            assertEquals(0, api.getGoodItems().getSize());
        }

        @Test
        void addGoodItemWrongTemperature() {
            Aisle frozenAisle = new Aisle("Frozen Foods", "FROZEN",
                    new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0), floorArea, new FunkierList<>());

            boolean result = goodItemManager.addGoodItem("Ice Cream", 500.0, 4.99, 5,
                    4.0, "icecream.jpg", frozenAisle, new FunkierList<>());
            assertFalse(result);
            assertEquals(0, api.getGoodItems().getSize());
        }

        @Test
        void addGoodItemWithShelves() {
            FunkierList<Shelf> shelves = new FunkierList<>();
            shelves.addAtLast(shelf);

            boolean result = goodItemManager.addGoodItem("Milk", 4.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, shelves);
            assertTrue(result);
            assertEquals(1, api.getGoodItems().getSize());
            assertFalse(shelf.getItems().isEmpty());
            assertEquals(0.04, shelf.getCurrentWeight(), 0.001);
        }
    }

    @Nested
    class FindMethods {
        @Test
        void findItemsByDesc() {
            goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            goodItemManager.addGoodItem("Milk", 500.0, 0.99, 5,
                    4.0, "milk2.jpg", aisle, new FunkierList<>());

            FunkierList<GoodItem> results = goodItemManager.findItemsByDesc("Milk");
            assertEquals(2, results.getSize());
        }

        @Test
        void findItemsByDescCaseInsensitive() {
            goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());

            FunkierList<GoodItem> results = goodItemManager.findItemsByDesc("MILK");
            assertEquals(1, results.getSize());
        }

        @Test
        void findItemsByDescNotFound() {
            FunkierList<GoodItem> results = goodItemManager.findItemsByDesc("Nonexistent");
            assertTrue(results.isEmpty());
        }

        @Test
        void listItemsByAisle() {
            Shelf dairyShelf = new Shelf(100.0, 0.0, 0, aisle, new FunkierList<>());
            aisle.getShelves().addAtLast(dairyShelf);
            api.addShelf(dairyShelf);

            FunkierList<Shelf> shelves = new FunkierList<>();
            shelves.addAtLast(dairyShelf);
            goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, shelves);

            Aisle bakery = new Aisle("Bakery", "UNREFRIGERATED",
                    new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0), floorArea, new FunkierList<>());

            Shelf bakeryShelf = new Shelf(50.0, 0.0, 0, bakery, new FunkierList<>());
            bakery.getShelves().addAtLast(bakeryShelf);
            api.addShelf(bakeryShelf);

            FunkierList<Shelf> bakeryShelves = new FunkierList<>();
            bakeryShelves.addAtLast(bakeryShelf);
            goodItemManager.addGoodItem("Bread", 500.0, 2.49, 8,
                    20.0, "bread.jpg", bakery, bakeryShelves);

            FunkierList<GoodItem> dairyItems = goodItemManager.listItemsByAisle(aisle);
            assertEquals(1, dairyItems.getSize());
            assertEquals("Milk", dairyItems.get(0).getDesc());
        }

        @Test
        void listItemsByAisleNull() {
            FunkierList<GoodItem> results = goodItemManager.listItemsByAisle(null);
            assertTrue(results.isEmpty());
        }

        @Test
        void listItemsByAisleEmpty() {
            Aisle emptyAisle = new Aisle("Empty Aisle", "UNREFRIGERATED",
                    new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0), floorArea, new FunkierList<>());

            FunkierList<GoodItem> results = goodItemManager.listItemsByAisle(emptyAisle);
            assertTrue(results.isEmpty());
        }

        @Test
        void findItemsByShelf() {
            FunkierList<Shelf> shelves = new FunkierList<>();
            shelves.addAtLast(shelf);

            goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, shelves);

            FunkierList<GoodItem> shelfItems = goodItemManager.findItemsByShelf(shelf);
            assertEquals(1, shelfItems.getSize());
        }

        @Test
        void findItemsByShelfEmpty() {
            Shelf emptyShelf = new Shelf(50.0, 0.0, 0, aisle, new FunkierList<>());
            FunkierList<GoodItem> results = goodItemManager.findItemsByShelf(emptyShelf);
            assertTrue(results.isEmpty());
        }
    }

    @Nested
    class ListMethods {
        @Test
        void listItemDescEmpty() {
            assertEquals("No items available.", goodItemManager.listItemDesc());
        }

        @Test
        void listItemDescWithItems() {
            goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());

            String result = goodItemManager.listItemDesc();
            assertTrue(result.contains("All Item Descriptions:"));
            assertTrue(result.contains("Milk"));
            assertTrue(result.contains("Qty: 10"));
        }

        @Test
        void listItemsByDesc() {
            goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            goodItemManager.addGoodItem("Chocolate Milk", 1000.0, 2.49, 5,
                    4.0, "chocmilk.jpg", aisle, new FunkierList<>());

            FunkierList<GoodItem> results = goodItemManager.listItemsByDesc("milk");
            assertEquals(2, results.getSize());
        }

        @Test
        void listItemsByDescNotFound() {
            FunkierList<GoodItem> results = goodItemManager.listItemsByDesc("nonexistent");
            assertTrue(results.isEmpty());
        }

        @Test
        void listItemsByTemp() {
            goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());

            String result = goodItemManager.listItemsByTemp("REFRIGERATED");
            assertTrue(result.contains("Items in REFRIGERATED category"));
            assertTrue(result.contains("Milk"));
        }

        @Test
        void listItemsByTempEmpty() {
            String result = goodItemManager.listItemsByTemp("FROZEN");
            assertTrue(result.contains("No items in FROZEN category"));
        }
    }

    @Nested
    class LocationMethods {
        @Test
        void getItemLocation() {
            FunkierList<Shelf> shelves = new FunkierList<>();
            shelves.addAtLast(shelf);

            GoodItem item = new GoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, shelves);
            api.addGoodItem(item);

            String location = goodItemManager.getItemLocation(item);
            assertTrue(location.contains("Main Floor"));
            assertTrue(location.contains("Dairy"));
            assertTrue(location.contains("Shelf #"));
        }

        @Test
        void getItemLocationNoShelf() {
            GoodItem item = new GoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            api.addGoodItem(item);

            String location = goodItemManager.getItemLocation(item);
            assertTrue(location.contains("Main Floor"));
            assertTrue(location.contains("Dairy"));
            assertTrue(location.contains("Unknown shelf"));
        }

        @Test
        void getItemLocationNoAisle() {
            GoodItem item = new GoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", null, new FunkierList<>());
            api.addGoodItem(item);

            String location = goodItemManager.getItemLocation(item);
            assertEquals("Unknown location", location);
        }

        @Test
        void getItemLocationNullItem() {
            String location = goodItemManager.getItemLocation(null);
            assertEquals("Unknown location", location);
        }
    }

    @Nested
    class RangeMethods {
        @Test
        void getItemsByPriceRange() {
            goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            goodItemManager.addGoodItem("Cheese", 500.0, 4.99, 5,
                    4.0, "cheese.jpg", aisle, new FunkierList<>());

            FunkierList<GoodItem> results = goodItemManager.getItemsByPriceRange(1.0, 3.0);
            assertEquals(1, results.getSize());
            assertEquals("Milk", results.get(0).getDesc());
        }

        @Test
        void getItemsByQuantityRange() {
            goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            goodItemManager.addGoodItem("Cheese", 500.0, 4.99, 5,
                    4.0, "cheese.jpg", aisle, new FunkierList<>());

            FunkierList<GoodItem> results = goodItemManager.getItemsByQuantityRange(5, 10);
            assertEquals(2, results.getSize());
        }

        @Test
        void getAverageItemPrice() {
            goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            goodItemManager.addGoodItem("Cheese", 500.0, 4.99, 5,
                    4.0, "cheese.jpg", aisle, new FunkierList<>());

            double avg = goodItemManager.getAverageItemPrice();
            assertEquals(3.49, avg, 0.01); // (1.99 + 4.99) / 2 = 3.49
        }

        @Test
        void getAverageItemPriceEmpty() {
            double avg = goodItemManager.getAverageItemPrice();
            assertEquals(0.0, avg, 0.001);
        }

        @Test
        void getLowStockItems() {
            goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 3,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            goodItemManager.addGoodItem("Cheese", 500.0, 4.99, 10,
                    4.0, "cheese.jpg", aisle, new FunkierList<>());

            FunkierList<GoodItem> lowStock = goodItemManager.getLowStockItems(5);
            assertEquals(1, lowStock.getSize());
            assertEquals("Milk", lowStock.get(0).getDesc());
        }

        @Test
        void getLowStockItemsEmpty() {
            FunkierList<GoodItem> lowStock = goodItemManager.getLowStockItems(1);
            assertTrue(lowStock.isEmpty());
        }
    }

    @Nested
    class ItemMethods {
        @Test
        void getTotalItemWeight() {
            goodItemManager.addGoodItem("Milk", 4.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());

            double weight = goodItemManager.getTotalItemWeight();
            assertEquals(0.04, weight, 0.001);
        }

        @Test
        void getTotalItemWeightEmpty() {
            double weight = goodItemManager.getTotalItemWeight();
            assertEquals(0.0, weight, 0.001);
        }

        @Test
        void getMostExpensiveItem() {
            goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            goodItemManager.addGoodItem("Cheese", 500.0, 4.99, 5,
                    4.0, "cheese.jpg", aisle, new FunkierList<>());

            GoodItem mostExpensive = goodItemManager.getMostExpensiveItem();
            assertNotNull(mostExpensive);
            assertEquals("Cheese", mostExpensive.getDesc());
        }

        @Test
        void getMostExpensiveItemEmpty() {
            GoodItem mostExpensive = goodItemManager.getMostExpensiveItem();
            assertNull(mostExpensive);
        }

        @Test
        void getHighestQuantityItem() {
            goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            goodItemManager.addGoodItem("Cheese", 500.0, 4.99, 5,
                    4.0, "cheese.jpg", aisle, new FunkierList<>());

            GoodItem highestQty = goodItemManager.getHighestQuantityItem();
            assertNotNull(highestQty);
            assertEquals("Milk", highestQty.getDesc());
        }

        @Test
        void getHighestQuantityItemEmpty() {
            GoodItem highestQty = goodItemManager.getHighestQuantityItem();
            assertNull(highestQty);
        }
    }

    @Nested
    class RemoveMethods {
        @Test
        void removeGoodItem() {
            GoodItem item = new GoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            api.addGoodItem(item);

            boolean result = goodItemManager.removeGoodItem(item);
            assertTrue(result);
            assertEquals(0, api.getGoodItems().getSize());
        }

        @Test
        void removeGoodItemNull() {
            boolean result = goodItemManager.removeGoodItem(null);
            assertFalse(result);
        }

        @Test
        void removeGoodItemWithShelves() {
            FunkierList<Shelf> shelves = new FunkierList<>();
            shelves.addAtLast(shelf);

            GoodItem item = new GoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, shelves);
            api.addGoodItem(item);
            shelf.getItems().addAtLast(item);

            boolean result = goodItemManager.removeGoodItem(item);
            assertTrue(result);
            assertTrue(shelf.getItems().isEmpty());
        }

        @Test
        void removeGoodItemByDesc() {
            FunkierList<Shelf> shelves = new FunkierList<>();
            shelves.addAtLast(shelf);

            goodItemManager.addGoodItem("Milk", 4.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, shelves);

            boolean result = goodItemManager.removeGoodItemByDesc("Milk", 5);
            assertTrue(result);

            GoodItem remaining = api.getGoodItems().get(0);
            assertEquals(5, remaining.getQty());
        }

        @Test
        void removeGoodItemByDescNotFound() {
            boolean result = goodItemManager.removeGoodItemByDesc("Nonexistent", 5);
            assertFalse(result);
        }

        @Test
        void removeGoodItemByDescInvalidQuantity() {
            goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());

            boolean result = goodItemManager.removeGoodItemByDesc("Milk", 15);
            assertFalse(result); // Only 10 available, requested 15
        }

        @Test
        void removeGoodItemFromShelf() {
            FunkierList<Shelf> shelves = new FunkierList<>();
            shelves.addAtLast(shelf);

            GoodItem item = new GoodItem("Milk", 4.0, 1.99, 10,  // unitSize = 4.0
                    4.0, "milk.jpg", aisle, shelves);
            api.addGoodItem(item);
            shelf.getItems().addAtLast(item);

            shelf.setCurrentWeight(0.04);
            shelf.setCurrentQuantity(10);

            boolean result = goodItemManager.removeGoodItemFromShelf(item, shelf, 5);
            assertTrue(result);
            assertEquals(5, item.getQty());
            assertEquals(0.02, shelf.getCurrentWeight(), 0.001);
            assertEquals(5, shelf.getCurrentQuantity());
        }

        @Test
        void removeGoodItemFromShelfNull() {
            GoodItem item = new GoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());

            boolean result = goodItemManager.removeGoodItemFromShelf(null, shelf, 5);
            assertFalse(result);

            result = goodItemManager.removeGoodItemFromShelf(item, null, 5);
            assertFalse(result);
        }

        @Test
        void removeGoodItemFromShelfInvalidQuantity() {
            FunkierList<Shelf> shelves = new FunkierList<>();
            shelves.addAtLast(shelf);

            GoodItem item = new GoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, shelves);
            api.addGoodItem(item);
            shelf.getItems().addAtLast(item);

            boolean result = goodItemManager.removeGoodItemFromShelf(item, shelf, 0);
            assertFalse(result);

            result = goodItemManager.removeGoodItemFromShelf(item, shelf, 15);
            assertFalse(result);
        }

        @Test
        void removeGoodItemFromShelfNotOnShelf() {
            GoodItem item = new GoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            api.addGoodItem(item);

            boolean result = goodItemManager.removeGoodItemFromShelf(item, shelf, 5);
            assertFalse(result);
        }
    }

    @Nested
    class StatisticsMethods {
        @Test
        void getItemStatistics() {
            goodItemManager.addGoodItem("Milk", 1000.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, new FunkierList<>());

            String stats = goodItemManager.getItemStatistics();
            assertNotNull(stats);
            assertTrue(stats.contains("Item Statistics"));
            assertTrue(stats.contains("Total Unique Items: 1"));
        }

        @Test
        void getItemStatisticsEmpty() {
            String stats = goodItemManager.getItemStatistics();
            assertNotNull(stats);
            assertTrue(stats.contains("Item Statistics"));
        }
    }

    @Nested
    class AdditionalCoverageTests {

        @Test
        void removeGoodItemByDescMultipleItemsPartialRemoval() {
            goodItemManager.addGoodItem("Milk", 4.0, 1.99, 5,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            goodItemManager.addGoodItem("Milk", 4.0, 2.99, 5,
                    4.0, "milk2.jpg", aisle, new FunkierList<>());

            boolean result = goodItemManager.removeGoodItemByDesc("Milk", 7);
            assertTrue(result);

            assertEquals(1, api.getGoodItems().getSize());
            assertEquals(3, api.getGoodItems().get(0).getQty());
        }

        @Test
        void addGoodItemInsufficientSpaceOnAllShelves() {
            Shelf smallShelf = new Shelf(0.01, 0.0, 0, aisle, new FunkierList<>()); // 0.01 kg capacity
            api.addShelf(smallShelf);
            aisle.getShelves().addAtLast(smallShelf);

            FunkierList<Shelf> shelves = new FunkierList<>();
            shelves.addAtLast(smallShelf);

            boolean result = goodItemManager.addGoodItem("Milk", 4.0, 1.99, 10,
                    4.0, "milk.jpg", aisle, shelves);
            assertTrue(result);
            assertTrue(smallShelf.getItems().isEmpty());
            assertEquals(0.0, smallShelf.getCurrentWeight(), 0.001);
        }

    }


}