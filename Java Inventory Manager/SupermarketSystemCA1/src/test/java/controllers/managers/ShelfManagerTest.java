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

class ShelfManagerTest {
    private ShelfManager shelfManager;
    private SupermarketAPI api;
    private Aisle aisle;
    private Shelf testShelf;

    @BeforeEach
    void setUp() {
        api = new SupermarketAPI();
        shelfManager = new ShelfManager(api);

        Dimensions floorDims = new Dimensions(100.0, 50.0, 0.0, 0.0, 10.0);
        FloorArea floorArea = new FloorArea("Main Floor", 1, floorDims, new FunkierList<>());
        Dimensions aisleDims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
        aisle = new Aisle("Dairy", "REFRIGERATED", aisleDims, floorArea, new FunkierList<>());

        testShelf = new Shelf(100.0, 50.0, 10, aisle, new FunkierList<>());
        api.addShelf(testShelf);
        aisle.getShelves().addAtLast(testShelf);
    }

    @Nested
    class AddShelf {
        @Test
        void addShelfValid() {
            boolean result = shelfManager.addShelf(50.0, 10.0, 5, aisle, new FunkierList<>());
            assertTrue(result);
            assertEquals(2, api.getShelves().getSize());
        }

        @Test
        void addShelfNullAisle() {
            boolean result = shelfManager.addShelf(50.0, 10.0, 5, null, new FunkierList<>());
            assertFalse(result);
            assertEquals(1, api.getShelves().getSize());
        }
    }

    @Nested
    class FindMethods {
        @Test
        void findShelfByNumberValid() {
            Shelf found = shelfManager.findShelfByNumber(testShelf.getShelfNumber());
            assertNotNull(found);
            assertEquals(testShelf.getShelfNumber(), found.getShelfNumber());
        }

        @Test
        void findShelfByNumberNotFound() {
            Shelf found = shelfManager.findShelfByNumber(9999);
            assertNull(found);
        }

        @Test
        void findShelvesByAisle() {
            Shelf shelf2 = new Shelf(50.0, 10.0, 5, aisle, new FunkierList<>());
            api.addShelf(shelf2);
            aisle.getShelves().addAtLast(shelf2);

            FunkierList<Shelf> result = shelfManager.findShelvesByAisle(aisle);
            assertEquals(2, result.getSize());
        }

        @Test
        void findShelvesByAisleDifferentAisle() {
            Aisle differentAisle = new Aisle("Bakery", "UNREFRIGERATED",
                    new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0),
                    aisle.getFloorArea(), new FunkierList<>());

            FunkierList<Shelf> result = shelfManager.findShelvesByAisle(differentAisle);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class ListMethods {
        @Test
        void listShelfNumbersEmpty() {
            api = new SupermarketAPI();
            shelfManager = new ShelfManager(api);
            assertEquals("No shelves.", shelfManager.listShelfNumbers());
        }

        @Test
        void listShelfNumbersWithShelves() {
            String result = shelfManager.listShelfNumbers();
            assertTrue(result.contains("All Shelf Numbers:"));
            assertTrue(result.contains("Shelf #"));
            assertTrue(result.contains(String.valueOf(testShelf.getShelfNumber())));
        }

        @Test
        void listShelvesByAisleValid() {
            String result = shelfManager.listShelvesByAisle(aisle);
            assertTrue(result.contains("Shelves in aisle Dairy:"));
            assertTrue(result.contains("Shelf #"));
        }

        @Test
        void listShelvesByAisleEmpty() {
            Aisle emptyAisle = new Aisle("Empty", "UNREFRIGERATED",
                    new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0), aisle.getFloorArea(), new FunkierList<>());

            String result = shelfManager.listShelvesByAisle(emptyAisle);
            assertTrue(result.contains("No shelves in aisle: Empty"));
        }
    }

    @Nested
    class ShelfOperations {
        @Test
        void hasSpaceValid() {
            boolean result = shelfManager.hasSpace(testShelf.getShelfNumber(), 49.0);
            assertTrue(result);
        }

        @Test
        void hasSpaceExactFit() {
            boolean result = shelfManager.hasSpace(testShelf.getShelfNumber(), 50.0);
            assertTrue(result);
        }

        @Test
        void hasSpaceInsufficient() {
            boolean result = shelfManager.hasSpace(testShelf.getShelfNumber(), 51.0);
            assertFalse(result);
        }

        @Test
        void hasSpaceNotFound() {
            boolean result = shelfManager.hasSpace(9999, 10.0);
            assertFalse(result);
        }

        @Test
        void addWeightValid() {
            boolean result = shelfManager.addWeight(testShelf.getShelfNumber(), 10.0);
            assertTrue(result);
            assertEquals(60.0, testShelf.getCurrentWeight(), 0.001);
        }

        @Test
        void addWeightInsufficientSpace() {
            boolean result = shelfManager.addWeight(testShelf.getShelfNumber(), 60.0);
            assertFalse(result);
            assertEquals(50.0, testShelf.getCurrentWeight(), 0.001);
        }

        @Test
        void addWeightShelfNotFound() {
            boolean result = shelfManager.addWeight(9999, 10.0);
            assertFalse(result);
        }

        @Test
        void addQuantityValid() {
            boolean result = shelfManager.addQuantity(testShelf.getShelfNumber(), 5);
            assertTrue(result);
            assertEquals(15, testShelf.getCurrentQuantity());
        }

        @Test
        void addQuantityShelfNotFound() {
            boolean result = shelfManager.addQuantity(9999, 5);
            assertFalse(result);
        }
    }

    @Nested
    class ItemOperations {
        @Test
        void addItemToShelfValid() {
            GoodItem item = new GoodItem("Milk", 500.0, 1.99, 1,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            boolean result = shelfManager.addItemToShelf(testShelf.getShelfNumber(), item);
            assertTrue(result);
            assertEquals(1, testShelf.getItems().getSize());
            assertEquals(50.5, testShelf.getCurrentWeight(), 0.001); // 50 + (4*1/1000)
        }

        @Test
        void addItemToShelfInsufficientSpace() {
            Shelf fullShelf = new Shelf(100.0, 99.5, 0, aisle, new FunkierList<>());
            int shelfNumber = fullShelf.getShelfNumber();
            api.addShelf(fullShelf);
            aisle.getShelves().addAtLast(fullShelf);

            GoodItem item = new GoodItem("Heavy Item", 600.0, 1.99, 1,
                    4.0, "heavy.jpg", aisle, new FunkierList<>());
            boolean result = shelfManager.addItemToShelf(shelfNumber, item);

            assertFalse(result);
            assertTrue(fullShelf.getItems().isEmpty());
            assertEquals(99.5, fullShelf.getCurrentWeight(), 0.001);
        }

        @Test
        void addItemToShelfNullItem() {
            boolean result = shelfManager.addItemToShelf(testShelf.getShelfNumber(), null);
            assertFalse(result);
        }

        @Test
        void addItemToShelfShelfNotFound() {
            GoodItem item = new GoodItem("Milk", 500.0, 1.99, 1,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            boolean result = shelfManager.addItemToShelf(9999, item);
            assertFalse(result);
        }

        @Test
        void removeItemFromShelfValid() {
            GoodItem item = new GoodItem("Milk", 500.0, 1.99, 1,
                    4.0, "milk.jpg", aisle, new FunkierList<>());

            boolean added = shelfManager.addItemToShelf(testShelf.getShelfNumber(), item);
            assertTrue(added);
            assertEquals(1, testShelf.getItems().getSize());
            assertEquals(50.5, testShelf.getCurrentWeight(), 0.001);

            boolean removed = shelfManager.removeItemFromShelf(testShelf.getShelfNumber(), item);
            assertTrue(removed);
            assertEquals(0, testShelf.getItems().getSize());
            assertEquals(50.0, testShelf.getCurrentWeight(), 0.001);
        }

        @Test
        void removeItemFromShelfNotFound() {
            GoodItem item = new GoodItem("Milk", 500.0, 1.99, 1,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            boolean result = shelfManager.removeItemFromShelf(testShelf.getShelfNumber(), item);
            assertFalse(result);
        }

        @Test
        void removeItemFromShelfNullItem() {
            boolean result = shelfManager.removeItemFromShelf(testShelf.getShelfNumber(), null);
            assertFalse(result);
        }

        @Test
        void removeItemFromShelfShelfNotFound() {
            GoodItem item = new GoodItem("Milk", 500.0, 1.99, 1,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            boolean result = shelfManager.removeItemFromShelf(9999, item);
            assertFalse(result);
        }
    }

    @Nested
    class RemoveMethods {
        @Test
        void removeShelfValid() {
            Shelf shelf = new Shelf(50.0, 0.0, 0, aisle, new FunkierList<>());
            api.addShelf(shelf);
            boolean result = shelfManager.removeShelf(shelf);
            assertTrue(result);
            assertFalse(api.getShelves().contains(shelf));
        }

        @Test
        void removeShelfNull() {
            boolean result = shelfManager.removeShelf(null);
            assertFalse(result);
        }

        @Test
        void removeShelfWithItems() {
            GoodItem item1 = new GoodItem("Milk", 500.0, 1.99, 2,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            GoodItem item2 = new GoodItem("Bread", 300.0, 2.49, 1,
                    20.0, "bread.jpg", aisle, new FunkierList<>());

            testShelf.getItems().addAtLast(item1);
            testShelf.getItems().addAtLast(item2);

            boolean result = shelfManager.removeShelf(testShelf);
            assertTrue(result);
            assertEquals(0, testShelf.getItems().getSize());
            assertFalse(api.getShelves().contains(testShelf));
        }

        @Test
        void removeShelfWithNullItemsInList() {
            testShelf.getItems().addAtLast(null);
            GoodItem item = new GoodItem("Milk", 500.0, 1.99, 1,
                    4.0, "milk.jpg", aisle, new FunkierList<>());
            testShelf.getItems().addAtLast(item);

            boolean result = shelfManager.removeShelf(testShelf);
            assertTrue(result);
        }

        @Test
        void removeShelfByNumberValid() {
            boolean result = shelfManager.removeShelfByNumber(testShelf.getShelfNumber());
            assertTrue(result);
            assertFalse(api.getShelves().contains(testShelf));
        }

        @Test
        void removeShelfByNumberNotFound() {
            boolean result = shelfManager.removeShelfByNumber(9999);
            assertFalse(result);
        }
    }

    @Nested
    class StatisticsMethods {
        @Test
        void getAllShelfItems() {
            int items = shelfManager.getAllShelfItems();
            assertEquals(10, items); // testShelf has 10 items
        }

        @Test
        void getEmptyShelves() {
            Shelf emptyShelf = new Shelf(50.0, 0.0, 0, aisle, new FunkierList<>());
            api.addShelf(emptyShelf);

            FunkierList<Shelf> emptyShelves = shelfManager.getEmptyShelves();
            assertEquals(1, emptyShelves.getSize());
            assertEquals(emptyShelf.getShelfNumber(), emptyShelves.get(0).getShelfNumber());
        }

        @Test
        void getTotalShelfWeight() {
            double weight = shelfManager.getTotalShelfWeight();
            assertEquals(50.0, weight, 0.001);
        }

        @Test
        void getShelfStatistics() {
            String stats = shelfManager.getShelfStatistics();
            assertNotNull(stats);
            assertTrue(stats.contains("Shelf Statistics"));
            assertTrue(stats.contains("Total Shelves: 1"));
        }
    }
}