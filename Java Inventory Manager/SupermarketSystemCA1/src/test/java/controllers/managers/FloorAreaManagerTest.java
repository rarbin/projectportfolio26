package controllers.managers;

import controllers.API.SupermarketAPI;
import models.Aisle;
import models.FloorArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import utils.CustomList.FunkierList;
import utils.Dimensions;

import static org.junit.jupiter.api.Assertions.*;

class FloorAreaManagerTest {
    private FloorAreaManager floorAreaManager;
    private SupermarketAPI api;
    private Dimensions dims;

    @BeforeEach
    void setUp() {
        api = new SupermarketAPI();
        floorAreaManager = new FloorAreaManager(api);
        dims = new Dimensions(100.0, 50.0, 0.0, 0.0, 10.0);
    }

    @Nested
    class AddFloorArea {
        @Test
        void addFloorAreaValid() {
            FloorArea floorArea = new FloorArea("Main Floor", 1, dims, new FunkierList<>());
            boolean result = api.addFloorArea(floorArea);
            assertTrue(result);
            assertEquals(1, api.getFloorAreas().getSize());
        }

        @Test
        void addFloorAreaWhenApiReturnsFalse() {

            FloorArea floorArea = new FloorArea("Main Floor", 1, dims, new FunkierList<>());


            api.addFloorArea(floorArea);


            boolean result = floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            assertFalse(result);

        }

        @Test
        void addFloorAreaDuplicateTitle() {

            boolean firstResult = floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            assertTrue(firstResult);

            boolean secondResult = floorAreaManager.addFloorArea("Main Floor", 2, dims, new FunkierList<>());
            assertFalse(secondResult);

            assertEquals(1, api.getFloorAreas().getSize());
        }

        @Test
        void addFloorAreaCaseInsensitiveDuplicate() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            boolean result = floorAreaManager.addFloorArea("MAIN FLOOR", 2, dims, new FunkierList<>());
            assertFalse(result);
            assertEquals(1, api.getFloorAreas().getSize());
        }

        @Test
        void addFloorAreaTitleTooLong() {
            boolean result = floorAreaManager.addFloorArea("A".repeat(21), 1, dims, new FunkierList<>());
            assertFalse(result);
            assertEquals(0, api.getFloorAreas().getSize());
        }

        @Test
        void addFloorAreaMaxLengthTitle() {
            boolean result = floorAreaManager.addFloorArea("A".repeat(20), 1, dims, new FunkierList<>());
            assertTrue(result);
            assertEquals(1, api.getFloorAreas().getSize());
        }
    }

    @Nested
    class FindMethods {
        @Test
        void findFloorAreaByIndexValid() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            FloorArea found = floorAreaManager.findFloorAreaByIndex(0);
            assertNotNull(found);
            assertEquals("Main Floor", found.getTitle());
        }

        @Test
        void findFloorAreaByIndexInvalid() {
            assertNull(floorAreaManager.findFloorAreaByIndex(-1));
            assertNull(floorAreaManager.findFloorAreaByIndex(0));
            assertNull(floorAreaManager.findFloorAreaByIndex(100));
        }

        @Test
        void findFloorAreaByTitleValid() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            FloorArea found = floorAreaManager.findFloorAreaByTitle("Main Floor");
            assertNotNull(found);
            assertEquals("Main Floor", found.getTitle());
        }

        @Test
        void findFloorAreaByTitleCaseInsensitive() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            FloorArea found = floorAreaManager.findFloorAreaByTitle("MAIN FLOOR");
            assertNotNull(found);
            assertEquals("Main Floor", found.getTitle());
        }

        @Test
        void findFloorAreaByTitleNotFound() {
            assertNull(floorAreaManager.findFloorAreaByTitle("Nonexistent"));
        }

        @Test
        void findFloorAreasByLevel() {
            floorAreaManager.addFloorArea("Ground Floor", 0, dims, new FunkierList<>());
            floorAreaManager.addFloorArea("First Floor", 1, dims, new FunkierList<>());
            floorAreaManager.addFloorArea("Second Floor", 1, dims, new FunkierList<>());

            FunkierList<FloorArea> level1Floors = floorAreaManager.findFloorAreasByLevel(1);
            assertEquals(2, level1Floors.getSize());

            FunkierList<FloorArea> level0Floors = floorAreaManager.findFloorAreasByLevel(0);
            assertEquals(1, level0Floors.getSize());
            assertEquals("Ground Floor", level0Floors.get(0).getTitle());
        }

        @Test
        void findFloorAreasByLevelEmpty() {
            FunkierList<FloorArea> result = floorAreaManager.findFloorAreasByLevel(99);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class ListMethods {

        @Test
        void listFloorAreaTitlesEmpty() {
            assertEquals("All Floor Area Titles:\n", floorAreaManager.listFloorAreaTitles());
        }

        @Test
        void listFloorAreaTitlesWithFloors() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            floorAreaManager.addFloorArea("Second Floor", 2, dims, new FunkierList<>());

            String result = floorAreaManager.listFloorAreaTitles();
            assertTrue(result.contains("All Floor Area Titles:"));
            assertTrue(result.contains("Main Floor"));
            assertTrue(result.contains("Second Floor"));
        }

        @Test
        void listAllFloorAreasEmpty() {
            String result = floorAreaManager.listFloorAreaTitles();
            assertTrue(result.contains("All Floor Area Titles"));
        }
        @Test
        void listAllFloorAreasWithFloors() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            String result = floorAreaManager.listFloorAreaTitles();
            assertTrue(result.contains("All Floor Area Titles"));
            assertTrue(result.contains("Main Floor"));
        }

        @Test
        void listFloorAreasByIndex() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            String result = floorAreaManager.listFloorByIndex();
            assertTrue(result.contains("0. Main Floor"));
        }

        @Test
        void listFloorAreasByLevel() {
            floorAreaManager.addFloorArea("Ground Floor", 0, dims, new FunkierList<>());
            floorAreaManager.addFloorArea("First Floor", 1, dims, new FunkierList<>());

            String level0Result = floorAreaManager.listFloorAreasByLevel(0);
            assertTrue(level0Result.contains("Ground Floor"));
            assertTrue(level0Result.contains("Floor areas on level 0"));

            String level1Result = floorAreaManager.listFloorAreasByLevel(1);
            assertTrue(level1Result.contains("First Floor"));
        }

        @Test
        void listFloorAreasByLevelEmpty() {
            String result = floorAreaManager.listFloorAreasByLevel(99);
            assertTrue(result.contains("No floor areas on level 99"));
        }
    }

    @Nested
    class UpdateMethods {
        @Test
        void updateFloorAreaTitleValid() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());

            boolean result = floorAreaManager.updateFloorAreaTitle("Main Floor", "Updated Floor");
            assertTrue(result);

            FloorArea updated = floorAreaManager.findFloorAreaByTitle("Updated Floor");
            assertNotNull(updated);
            assertEquals("Updated Floor", updated.getTitle());
        }

        @Test
        void updateFloorAreaTitleSameName() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());

            boolean result = floorAreaManager.updateFloorAreaTitle("Main Floor", "Main Floor");
            assertTrue(result);
        }

        @Test
        void updateFloorAreaTitleCaseInsensitiveSame() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());

            boolean result = floorAreaManager.updateFloorAreaTitle("Main Floor", "MAIN FLOOR");
            assertTrue(result);
        }

        @Test
        void updateFloorAreaTitleDuplicate() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            floorAreaManager.addFloorArea("Second Floor", 2, dims, new FunkierList<>());

            boolean result = floorAreaManager.updateFloorAreaTitle("Main Floor", "Second Floor");
            assertFalse(result);
        }

        @Test
        void updateFloorAreaTitleTooLong() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());

            boolean result = floorAreaManager.updateFloorAreaTitle("Main Floor", "A".repeat(21));
            assertFalse(result);
        }

        @Test
        void updateFloorAreaTitleNotFound() {
            boolean result = floorAreaManager.updateFloorAreaTitle("Nonexistent", "New Name");
            assertFalse(result);
        }
    }

    @Nested
    class AisleOperations {
        @Test
        void addAisleToFloorAreaValid() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            Aisle aisle = new Aisle("Dairy", "Refrigerated",
                    new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0),
                    null, new FunkierList<>());

            boolean result = floorAreaManager.addAisleToFloorArea("Main Floor", aisle);
            assertTrue(result);
            assertEquals("Main Floor", aisle.getFloorArea().getTitle());
        }

        @Test
        void addAisleToFloorAreaFloorNotFound() {
            Aisle aisle = new Aisle("Dairy", "Refrigerated",
                    new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0),
                    null, new FunkierList<>());

            boolean result = floorAreaManager.addAisleToFloorArea("Nonexistent", aisle);
            assertFalse(result);
        }

        @Test
        void addAisleToFloorAreaNullAisle() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());

            boolean result = floorAreaManager.addAisleToFloorArea("Main Floor", null);
            assertFalse(result);
        }

    }

    @Nested
    class RemoveMethods {
        @Test
        void removeAisleFromFloorArea() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            FloorArea floor = floorAreaManager.findFloorAreaByTitle("Main Floor");
            Aisle aisle = new Aisle("Dairy", "Refrigerated",
                    new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0),
                    floor, new FunkierList<>());
            boolean resultf = floorAreaManager.removeAisleFromFloorArea("Main Floor", aisle);
            assertFalse(resultf);
            floor.getAisles().addAtLast(aisle);

            boolean result = floorAreaManager.removeAisleFromFloorArea("Main Floor", aisle);
            assertTrue(result);
            assertTrue(floor.getAisles().isEmpty());
        }

        @Test
        void removeFloorAreaByIndex() {
            boolean resultn = floorAreaManager.removeAllFloorAreasByLevel(1);
            assertFalse(resultn);
            boolean resultf = floorAreaManager.removeFloorAreaByIndex(-1);
            assertFalse(resultf);

            resultf = floorAreaManager.removeFloorAreaByIndex(99);
            assertFalse(resultf);
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());

            boolean result = floorAreaManager.removeFloorAreaByIndex(0);
            assertTrue(result);
            assertEquals(0, api.getFloorAreas().getSize());
        }

        @Test
        void removeFloorAreaByIndexWithAisles() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            FloorArea floor = floorAreaManager.findFloorAreaByTitle("Main Floor");

            Aisle aisle = new Aisle("Dairy", "Refrigerated",
                    new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0),
                    floor, new FunkierList<>());
            floor.getAisles().addAtLast(aisle);

            api.addAisle(aisle);

            boolean result = floorAreaManager.removeFloorAreaByIndex(0);
            if (result) {
                assertEquals(0, api.getFloorAreas().getSize());
                assertEquals(0, api.getAisles().getSize());
            } else {
                assertEquals(1, api.getFloorAreas().getSize());
            }
        }

        @Test
        void removeAllFloorAreasByLevel() {
            floorAreaManager.addFloorArea("Ground Floor", 0, dims, new FunkierList<>());
            floorAreaManager.addFloorArea("First Floor", 1, dims, new FunkierList<>());

            boolean result = floorAreaManager.removeAllFloorAreasByLevel(1);
            assertTrue(result);
            assertEquals(1, api.getFloorAreas().getSize());
            assertEquals("Ground Floor", api.getFloorAreas().get(0).getTitle());
        }

        @Test
        void removeAllFloorAreasByLevelWithAisles() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            FloorArea floor = floorAreaManager.findFloorAreaByTitle("Main Floor");

            Aisle aisle = new Aisle("Dairy", "Refrigerated",
                    new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0),
                    floor, new FunkierList<>());
            floor.getAisles().addAtLast(aisle);

            api.addAisle(aisle);

            boolean result = floorAreaManager.removeAllFloorAreasByLevel(1);
            assertTrue(result);
            assertEquals(0, api.getFloorAreas().getSize());
            assertEquals(0, api.getAisles().getSize());
        }

        @Test
        void removeFloorAreaValid() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            FloorArea floor = floorAreaManager.findFloorAreaByTitle("Main Floor");

            boolean result = floorAreaManager.removeFloorArea(floor);
            assertTrue(result);
            assertEquals(0, api.getFloorAreas().getSize());
        }

        @Test
        void removeFloorAreaByTitle() {
            boolean resultf = floorAreaManager.removeFloorAreaByTitle("Non-existent Floor");
            assertFalse(resultf);
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());

            boolean result = floorAreaManager.removeFloorAreaByTitle("Main Floor");
            assertTrue(result);
            assertEquals(0, api.getFloorAreas().getSize());
        }

        @Test
        void removeFloorAreaByTitleWithAisles() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            FloorArea floor = floorAreaManager.findFloorAreaByTitle("Main Floor");

            Aisle aisle = new Aisle("Dairy", "Refrigerated",
                    new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0),
                    floor, new FunkierList<>());
            floor.getAisles().addAtLast(aisle);
            api.addAisle(aisle);

            boolean result = floorAreaManager.removeFloorAreaByTitle("Main Floor");
            assertTrue(result);
            assertEquals(0, api.getFloorAreas().getSize());
            assertEquals(0, api.getAisles().getSize());
        }
    }

    @Nested
    class StatisticsAndTotalMethods {
        @Test
        void getTotalValueInFloorAreas() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            double value = floorAreaManager.getTotalValueInFloorAreas();
            assertTrue(value >= 0.0);
        }

        @Test
        void getTotalItemsInFloorAreas() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            int items = floorAreaManager.getTotalItemsInFloorAreas();
            assertTrue(items >= 0);
        }

        @Test
        void getTotalAisleCountInFloorAreas() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            int aisles = floorAreaManager.getTotalAisleCountInFloorAreas();
            assertEquals(0, aisles);

            FloorArea floor = floorAreaManager.findFloorAreaByTitle("Main Floor");
            Aisle aisle = new Aisle("Dairy", "Refrigerated",
                    new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0),
                    floor, new FunkierList<>());
            floor.getAisles().addAtLast(aisle);

            aisles = floorAreaManager.getTotalAisleCountInFloorAreas();
            assertEquals(1, aisles);
        }

        @Test
        void getFloorAreaStatistics() {
            String stats = floorAreaManager.getFloorAreaStatistics();
            assertNotNull(stats);
            assertTrue(stats.contains("Floor Area Statistics"));
        }

        @Test
        void getFloorAreaStatisticsWithData() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            String stats = floorAreaManager.getFloorAreaStatistics();
            assertTrue(stats.contains("Total Floor Areas: 1"));
        }
    }

    @Nested
    class HelperMethods {
        @Test
        void testIsFloorAreaTitleUnique() {
            floorAreaManager.addFloorArea("Main Floor", 1, dims, new FunkierList<>());
            boolean result = floorAreaManager.addFloorArea("Main Floor", 2, dims, new FunkierList<>());
            assertFalse(result);

            result = floorAreaManager.addFloorArea("MAIN FLOOR", 3, dims, new FunkierList<>());
            assertFalse(result);

            result = floorAreaManager.addFloorArea("Second Floor", 2, dims, new FunkierList<>());
            assertTrue(result);
        }
    }


}