package controllers.managers;

import controllers.API.SupermarketAPI;
import models.Aisle;
import models.FloorArea;
import models.Shelf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import utils.CustomList.FunkierList;
import utils.Dimensions;

import static org.junit.jupiter.api.Assertions.*;

class AisleManagerTest {
    private AisleManager aisleManager;
    private SupermarketAPI api;
    private FloorArea floorArea;

    @BeforeEach
    void setUp() {
        api = new SupermarketAPI();
        aisleManager = new AisleManager(api);
        Dimensions floorDims = new Dimensions(100.0, 50.0, 0.0, 0.0, 10.0);
        floorArea = new FloorArea("Main Floor", 1, floorDims, new FunkierList<>());
    }

    @Nested
    class AddAisle {
        @Test
        void addAisleValid() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            boolean result = aisleManager.addAisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            assertTrue(result);
            assertEquals(1, api.getAisles().getSize());
        }

        @Test
        void addAisleNullFloorArea() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            boolean result = aisleManager.addAisle("Dairy", "Refrigerated", dims, null, new FunkierList<>());
            assertFalse(result);
            assertEquals(0, api.getAisles().getSize());
        }

        @Test
        void addAisleDuplicateName() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            aisleManager.addAisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            boolean result = aisleManager.addAisle("Dairy", "Frozen", dims, floorArea, new FunkierList<>());
            assertFalse(result);
            assertEquals(1, api.getAisles().getSize());
        }

        @Test
        void addAisleCaseInsensitiveDuplicate() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            aisleManager.addAisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            boolean result = aisleManager.addAisle("DAIRY", "Frozen", dims, floorArea, new FunkierList<>());
            assertFalse(result);
            assertEquals(1, api.getAisles().getSize());
        }
    }

    @Nested
    class FindMethods {
        @Test
        void findAisleValid() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            aisleManager.addAisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            Aisle found = aisleManager.findAisle("Dairy");
            assertNotNull(found);
            assertEquals("Dairy", found.getName());
        }

        @Test
        void findAisleCaseInsensitive() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            aisleManager.addAisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            Aisle found = aisleManager.findAisle("DAIRY");
            assertNotNull(found);
            assertEquals("Dairy", found.getName());
        }

        @Test
        void findAisleNotFound() {
            assertNull(aisleManager.findAisle("Nonexistent"));
        }

        @Test
        void findAislesByFloorArea() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            FloorArea floor2 = new FloorArea("Second Floor", 2, dims, new FunkierList<>());

            Aisle aisle1 = new Aisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            Aisle aisle2 = new Aisle("Bakery", "Unrefrigerated", dims, floor2, new FunkierList<>());

            api.getAisles().addAtLast(aisle1);
            api.getAisles().addAtLast(aisle2);

            FunkierList<Aisle> mainFloorAisles = aisleManager.findAislesByFloorArea("Main Floor");
            assertEquals(1, mainFloorAisles.getSize());
        }

        @Test
        void findAislesByFloorAreaCaseInsensitive() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            FloorArea floor2 = new FloorArea("Second Floor", 2, dims, new FunkierList<>());

            Aisle aisle1 = new Aisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            api.getAisles().addAtLast(aisle1);

            FunkierList<Aisle> mainFloorAisles = aisleManager.findAislesByFloorArea("MAIN FLOOR");
            assertEquals(1, mainFloorAisles.getSize());
        }

        @Test
        void findAislesByFloorAreaEmpty() {
            FunkierList<Aisle> result = aisleManager.findAislesByFloorArea("Nonexistent");
            assertTrue(result.isEmpty());
        }

        @Test
        void findAislesByTemp() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);

            Aisle aisle1 = new Aisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            Aisle aisle2 = new Aisle("Frozen Foods", "Frozen", dims, floorArea, new FunkierList<>());

            api.getAisles().addAtLast(aisle1);
            api.getAisles().addAtLast(aisle2);

            FunkierList<Aisle> refrigerated = aisleManager.findAislesByTemp("Refrigerated");
            assertEquals(1, refrigerated.getSize());
        }

        @Test
        void findAislesByTempCaseInsensitive() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);

            Aisle aisle1 = new Aisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            api.getAisles().addAtLast(aisle1);

            FunkierList<Aisle> result = aisleManager.findAislesByTemp("REFRIGERATED");
            assertEquals(1, result.getSize());
        }

        @Test
        void findAislesByTempEmpty() {
            FunkierList<Aisle> result = aisleManager.findAislesByTemp("Nonexistent");
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class UpdateMethods {
        @Test
        void updateAisleNameValid() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            aisleManager.addAisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());

            boolean result = aisleManager.updateAisleName("Dairy", "Dairy Products");
            assertTrue(result);
            assertNotNull(aisleManager.findAisle("Dairy Products"));
        }

        @Test
        void updateAisleNameSameName() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            aisleManager.addAisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());

            boolean result = aisleManager.updateAisleName("Dairy", "Dairy");
            assertTrue(result);
        }

        @Test
        void updateAisleNameCaseInsensitiveSame() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            aisleManager.addAisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());

            boolean result = aisleManager.updateAisleName("Dairy", "DAIRY");
            assertTrue(result);
        }

        @Test
        void updateAisleNameNotFound() {
            boolean result = aisleManager.updateAisleName("Nonexistent", "New Name");
            assertFalse(result);
        }

        @Test
        void updateAisleNameDuplicate() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            aisleManager.addAisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            aisleManager.addAisle("Bakery", "Unrefrigerated", dims, floorArea, new FunkierList<>());

            boolean result = aisleManager.updateAisleName("Dairy", "Bakery");
            assertFalse(result);
        }

        @Test
        void updateAisleTemperatureValid() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            aisleManager.addAisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());

            boolean result = aisleManager.updateAisleTemperature("Dairy", "Frozen");
            assertTrue(result);
            Aisle aisle = aisleManager.findAisle("Dairy");
            assertEquals("Frozen", aisle.getTemp());
        }

        @Test
        void updateAisleTemperatureNull() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            aisleManager.addAisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());

            boolean result = aisleManager.updateAisleTemperature("Dairy", null);
            assertFalse(result);
        }

        @Test
        void updateAisleTemperatureEmpty() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            aisleManager.addAisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());

            boolean result = aisleManager.updateAisleTemperature("Dairy", "");
            assertFalse(result);
        }

        @Test
        void updateAisleTemperatureNotFound() {
            boolean result = aisleManager.updateAisleTemperature("Nonexistent", "Frozen");
            assertFalse(result);
        }
    }

    @Nested
    class RemoveMethods {
        @Test
        void removeAisleValid() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            Aisle aisle = new Aisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            api.addAisle(aisle);
            floorArea.getAisles().addAtLast(aisle);

            boolean result = aisleManager.removeAisle(aisle);
            assertTrue(result);
            assertFalse(api.getAisles().contains(aisle));
            assertFalse(floorArea.getAisles().contains(aisle));
        }

        @Test
        void removeAisleNull() {
            boolean result = aisleManager.removeAisle(null);
            assertFalse(result);
        }

        @Test
        void removeAisleNotInSystem() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            Aisle aisle = new Aisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());

            boolean result = aisleManager.removeAisle(aisle);
            assertFalse(result);
        }

        @Test
        void removeAisleRemovesFromFloorArea() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            Aisle aisle = new Aisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            api.addAisle(aisle);
            floorArea.getAisles().addAtLast(aisle);

            boolean result = aisleManager.removeAisle(aisle);
            assertTrue(result);
            assertFalse(floorArea.getAisles().contains(aisle));
        }

        @Test
        void removeShelfFromAisleValid() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            Aisle aisle = new Aisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            api.addAisle(aisle);

            Shelf shelf = new Shelf(100.0, 50.0, 10, aisle, new FunkierList<>());
            aisle.getShelves().addAtLast(shelf);

            boolean result = aisleManager.removeShelfFromAisle("Main Floor", "Dairy", shelf);
            assertTrue(result);
            assertTrue(aisle.getShelves().isEmpty());
        }

        @Test
        void removeShelfFromAisleWrongFloor() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            Aisle aisle = new Aisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            api.addAisle(aisle);

            Shelf shelf = new Shelf(100.0, 50.0, 10, aisle, new FunkierList<>());
            aisle.getShelves().addAtLast(shelf);

            boolean result = aisleManager.removeShelfFromAisle("Wrong Floor", "Dairy", shelf);
            assertFalse(result);
        }

        @Test
        void removeShelfFromAisleShelfNotFound() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            Aisle aisle = new Aisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            api.addAisle(aisle);

            Shelf shelf = new Shelf(100.0, 50.0, 10, aisle, new FunkierList<>());
            Shelf differentShelf = new Shelf(50.0, 25.0, 5, aisle, new FunkierList<>());
            aisle.getShelves().addAtLast(shelf);

            boolean result = aisleManager.removeShelfFromAisle("Main Floor", "Dairy", differentShelf);
            assertFalse(result);
        }

        @Test
        void removeShelfFromAisleAisleNotFound() {
            Shelf shelf = new Shelf(100.0, 50.0, 10, null, new FunkierList<>());
            boolean result = aisleManager.removeShelfFromAisle("Main Floor", "Nonexistent", shelf);
            assertFalse(result);
        }

        @Test
        void removeAisleByNameValid() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            Aisle aisle = new Aisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            api.addAisle(aisle);
            floorArea.getAisles().addAtLast(aisle);

            boolean result = aisleManager.removeAisleByName("Dairy");
            assertTrue(result);
            assertFalse(api.getAisles().contains(aisle));
        }

        @Test
        void removeAisleByNameNotFound() {
            boolean result = aisleManager.removeAisleByName("Non-existent Aisle");
            assertFalse(result);
        }

        @Test
        void removeAisleByNameCaseInsensitive() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            Aisle aisle = new Aisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            api.addAisle(aisle);
            floorArea.getAisles().addAtLast(aisle);

            boolean result = aisleManager.removeAisleByName("DAIRY");
            assertTrue(result);
            assertFalse(api.getAisles().contains(aisle));
        }

        @Test
        void removeAisleByNameWithShelves() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            Aisle aisle = new Aisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            api.addAisle(aisle);
            floorArea.getAisles().addAtLast(aisle);

            Shelf shelf1 = new Shelf(100.0, 50.0, 10, aisle, new FunkierList<>());
            aisle.getShelves().addAtLast(shelf1);
            api.addShelf(shelf1);

            boolean result = aisleManager.removeAisleByName("Dairy");
            assertTrue(result);
            assertFalse(api.getAisles().contains(aisle));
        }
    }

    @Nested
    class ListMethods {
        @Test
        void listAllAislesEmpty() {
            String result = aisleManager.listAllAisles();
            assertEquals("Available aisles:\n", result);
        }

        @Test
        void listAllAislesWithAisles() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            aisleManager.addAisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());

            String result = aisleManager.listAllAisles();
            assertTrue(result.contains("Dairy"));
            assertTrue(result.contains("Refrigerated"));
            assertTrue(result.contains("Main Floor"));
        }

        @Test
        void listAllAislesUnknownFloor() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            Aisle aisle = new Aisle("Test", "Temp", dims, null, new FunkierList<>());
            api.getAisles().addAtLast(aisle);

            String result = aisleManager.listAllAisles();
            assertTrue(result.contains("Test"));
            assertTrue(result.contains("Unknown floor"));
        }

        @Test
        void listAisleTitles() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            aisleManager.addAisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            aisleManager.addAisle("Bakery", "Unrefrigerated", dims, floorArea, new FunkierList<>());

            String result = aisleManager.listAisleTitles();
            assertTrue(result.contains("Dairy"));
            assertTrue(result.contains("Bakery"));
            assertFalse(result.contains("Refrigerated")); // Should not contain temperature
        }

        @Test
        void listAislesByTemp() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            aisleManager.addAisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            aisleManager.addAisle("Frozen Foods", "Frozen", dims, floorArea, new FunkierList<>());

            String result = aisleManager.listAislesByTemp("Refrigerated");
            assertTrue(result.contains("Dairy"));
            assertFalse(result.contains("Frozen Foods"));
        }

        @Test
        void listAislesByTempEmpty() {
            String result = aisleManager.listAislesByTemp("Nonexistent");
            assertTrue(result.contains("No aisles"));
        }

        @Test
        void listAislesByFloorArea() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            aisleManager.addAisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());

            String result = aisleManager.listAislesByFloorArea("Main Floor");
            assertTrue(result.contains("Dairy"));
        }

        @Test
        void listAislesByFloorAreaEmpty() {
            String result = aisleManager.listAislesByFloorArea("Nonexistent");
            assertTrue(result.contains("No aisles"));
        }
    }

    @Nested
    class StatisticsMethods {
        @Test
        void getTotalValueInAisles() {
            // Add mock implementation or test with actual data
            double value = aisleManager.getTotalValueInAisles();
            assertTrue(value >= 0.0);
        }

        @Test
        void getTotalItemsInAisles() {
            int items = aisleManager.getTotalItemsInAisles();
            assertTrue(items >= 0);
        }

        @Test
        void getTotalShelfCount() {
            Dimensions dims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
            Aisle aisle = new Aisle("Dairy", "Refrigerated", dims, floorArea, new FunkierList<>());
            api.addAisle(aisle);

            Shelf shelf1 = new Shelf(100.0, 50.0, 10, aisle, new FunkierList<>());
            Shelf shelf2 = new Shelf(100.0, 50.0, 11, aisle, new FunkierList<>());
            aisle.getShelves().addAtLast(shelf1);
            aisle.getShelves().addAtLast(shelf2);

            int shelfCount = aisleManager.getTotalShelfCount();
            assertEquals(2, shelfCount);
        }

        @Test
        void getAisleStatistics() {
            String stats = aisleManager.getAisleStatistics();
            assertNotNull(stats);
            assertTrue(stats.contains("Aisle Statistics"));
        }
    }
}