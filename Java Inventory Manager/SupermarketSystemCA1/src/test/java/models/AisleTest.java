package models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.CustomList.FunkierList;
import utils.Dimensions;

import static org.junit.jupiter.api.Assertions.*;

class AisleTest {

    private Aisle aisle;
    private FloorArea floorArea;
    private Dimensions dimensions;
    private FunkierList<Shelf> shelves;

    @BeforeEach
    void setUp() {
        Dimensions floorDimensions = new Dimensions(100.0, 50.0, 0.0, 0.0, 10.0);
        floorArea = new FloorArea("Main Floor", 1, floorDimensions, new FunkierList<>());
        dimensions = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
        shelves = new FunkierList<>();
        aisle = new Aisle("Dairy", "Refrigerated", dimensions, floorArea, shelves);
    }

    @AfterEach
    void tearDown() {
        aisle = null;
        floorArea = null;
        dimensions = null;
        shelves = null;
    }

    @Test
    void setName() {
        aisle.setName("123456789012345678901234567890");
        assertEquals("123456789012345678901234567890", aisle.getName());

        aisle.setName("Fresh Produce Aisle");
        assertEquals("Fresh Produce Aisle", aisle.getName());
    }

    @Test
    void setName_InvalidLength() {
        String originalName = aisle.getName();
        aisle.setName("1234567890123456789012345678901");
        assertEquals(originalName, aisle.getName());
    }

    @Test
    void setTemperature() {
        aisle.setTemperature("Frozen");
        assertEquals("Frozen", aisle.getTemp());

        aisle.setTemperature(null);
        assertEquals("Frozen", aisle.getTemp());

        aisle.setTemperature("");
        assertEquals("", aisle.getTemp());
    }

    @Test
    void setFloorArea() {
        Dimensions newFloorDims = new Dimensions(200.0, 100.0, 0.0, 0.0, 10.0);
        FloorArea newFloorArea = new FloorArea("Second Floor", 2, newFloorDims, new FunkierList<>());

        aisle.setFloorArea(newFloorArea);
        assertSame(newFloorArea, aisle.getFloorArea());
        assertEquals("Second Floor", aisle.getFloorArea().getTitle());

        aisle.setFloorArea(null);
        assertNull(aisle.getFloorArea());
    }

    @Test
    void getName() {
        assertEquals("Dairy", aisle.getName());

        aisle.setName("Bakery");
        assertEquals("Bakery", aisle.getName());
    }

    @Test
    void getId() {
        int id = aisle.getId();
        assertTrue(id >= 1000);

        Aisle anotherAisle = new Aisle("Meat", "Frozen", dimensions, floorArea, shelves);
        assertEquals(id + 1, anotherAisle.getId());
    }

    @Test
    void getDimensions() {
        Dimensions retrievedDimensions = aisle.getDimensions();
        assertNotNull(retrievedDimensions);
        assertSame(dimensions, retrievedDimensions);
        assertEquals(5.0, retrievedDimensions.getWidth(), 0.001);
        assertEquals(10.0, retrievedDimensions.getLength(), 0.001);
        assertEquals(10.0, retrievedDimensions.getOrientation());
    }

    @Test
    void getTemp() {
        assertEquals("Refrigerated", aisle.getTemp());
        aisle.setTemperature("Frozen");
        assertEquals("Frozen", aisle.getTemp());
    }

    @Test
    void getFloorArea() {
        FloorArea retrievedFloorArea = aisle.getFloorArea();
        assertNotNull(retrievedFloorArea);
        assertSame(floorArea, retrievedFloorArea);
        assertEquals("Main Floor", retrievedFloorArea.getTitle());
        assertEquals(1, retrievedFloorArea.getLevel());

    }

    @Test
    void getShelves() {
        assertNotNull(aisle.getShelves());


        assertTrue(aisle.getShelves().isEmpty());

        Shelf shelf = new Shelf(100.0, 0.0, 0, aisle, new FunkierList<>());
        aisle.getShelves().addAtLast(shelf);

        assertFalse(aisle.getShelves().isEmpty());
        assertEquals(1, aisle.getShelves().getSize());
        assertEquals(100.0, aisle.getShelves().get(0).getCapacity(), 0.001);
    }

    @Test
    void setShelves() {
        aisle.setShelves(null);
        assertNotNull(aisle.getShelves());
        assertTrue(aisle.getShelves().isEmpty());

        FunkierList<Shelf> newShelves = new FunkierList<>();
        aisle.setShelves(newShelves);
        assertSame(newShelves, aisle.getShelves());
        assertTrue(aisle.getShelves().isEmpty());

        Shelf shelf1 = new Shelf(50.0, 10.0, 5, aisle, new FunkierList<>());
        newShelves.addAtLast(shelf1);
        aisle.setShelves(newShelves);

        assertEquals(1, aisle.getShelves().getSize());
        assertEquals(50.0, aisle.getShelves().get(0).getCapacity(), 0.001);
    }


    @Test
    void getArea() {
        assertEquals(50.0, aisle.getArea(), 0.001);
    }

    @Test
    void testToString() {
        String toStringResult = aisle.toString();
        String expectedStart = aisle.getId() + " Dairy on floor Main Floor (Refrigerated)";
        assertTrue(toStringResult.startsWith(expectedStart));
    }


}