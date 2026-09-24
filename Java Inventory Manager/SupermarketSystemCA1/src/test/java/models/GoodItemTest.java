package models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.CustomList.FunkierList;
import utils.Dimensions;

import static org.junit.jupiter.api.Assertions.*;

class GoodItemTest {

    private GoodItem item;
    private Aisle aisle;
    private FunkierList<Shelf> shelves;

    @BeforeEach
    void setUp() {
        Dimensions dimensions = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
        FloorArea floorArea = new FloorArea("Main Floor", 1, new Dimensions(100.0, 50.0, 0.0, 0.0, 10.0), new FunkierList<>());
        aisle = new Aisle("Dairy", "Refrigerated", dimensions, floorArea, new FunkierList<>());
        shelves = new FunkierList<>();
        Shelf shelf = new Shelf(100.0, 0.0, 0, aisle, new FunkierList<>());
        shelves.addAtLast(shelf);
        item = new GoodItem("Milk", 500.0, 1.99, 10, 4.0, "abc", aisle, shelves);
    }

    @AfterEach
    void tearDown() {
        item = null;
        aisle = null;
        shelves = null;
    }

    @Test
    void setDescription() {
        item.setDescription("Fresh Milk");
        assertEquals("Fresh Milk", item.getDesc());
    }

    @Test
    void setDescription_Invalid() {
        String original = item.getDesc();
        item.setDescription("A".repeat(31));
        assertEquals(original, item.getDesc());
    }

    @Test
    void setUnitSize() {
        item.setUnitSize(600.0);
        assertEquals(600.0, item.getUnitSize(), 0.001);
    }

    @Test
    void setUnitSize_Invalid() {
        double original = item.getUnitSize();
        item.setUnitSize(0.5);
        assertEquals(original, item.getUnitSize(), 0.001);
    }

    @Test
    void setUnitPrice() {
        item.setUnitPrice(2.49);
        assertEquals(2.49, item.getPrice(), 0.001);
    }

    @Test
    void setUnitPrice_Invalid() {
        double original = item.getPrice();
        item.setUnitPrice(0.99);
        assertEquals(original, item.getPrice(), 0.001);
    }

    @Test
    void setQty() {
        item.setQty(20);
        assertEquals(20, item.getQty());
    }

    @Test
    void setQty_Invalid() {
        int original = item.getQty();
        item.setQty(0);
        assertEquals(original, item.getQty());
    }


    @Test
    void matches_Null() {
        assertFalse(item.matches(null));
    }

    @Test
    void getStorageTemp() {
        assertEquals(4.0, item.getStorageTemp(), 0.001);
    }

    @Test
    void setAisle_Null() {
        item.setAisle(null);
        assertNull(item.getAisle());
    }

    @Test
    void setPhotoUrl() {
        item.setPhotoUrl("new.jpg");
        assertEquals("new.jpg", item.getPhotoUrl());
    }

    @Test
    void setPhotoUrl_TooLong() {
        item.setPhotoUrl("A".repeat(151));
        assertEquals("No photo.", item.getPhotoUrl());
    }

    @Test
    void getTotalValue() {
        assertEquals(19.90, item.getTotalValue(), 0.001);
    }

    @Test
    void getStorageCategory() {
        assertEquals("REFRIGERATED", item.getStorageCategory());
    }

    @Test
    void getAisle() {
        assertSame(aisle, item.getAisle());
    }

    @Test
    void setAisle() {
        Aisle newAisle = new Aisle("New", "Frozen", new Dimensions(5.0, 3.0, 0.0, 0.0, 10.0), aisle.getFloorArea(), new FunkierList<>());
        item.setAisle(newAisle);
        assertSame(newAisle, item.getAisle());
    }

    @Test
    void getShelves() {
        assertSame(shelves, item.getShelves());
        assertEquals(1, item.getShelves().getSize());
    }

    @Test
    void setShelves() {
        FunkierList<Shelf> newShelves = new FunkierList<>();
        item.setShelves(newShelves);
        assertSame(newShelves, item.getShelves());
    }

    @Test
    void setShelves_Null() {
        item.setShelves(null);
        assertNotNull(item.getShelves());
        assertTrue(item.getShelves().isEmpty());
    }

    @Test
    void matches() {
        GoodItem sameItem = new GoodItem("Milk", 500.0, 2.49, 5, 4.0, "abc", aisle, shelves);
        assertTrue(item.matches(sameItem));

        GoodItem differentItem = new GoodItem("Cheese", 500.0, 3.99, 5, 4.0, "abc", aisle, shelves);
        assertFalse(item.matches(differentItem));
    }

    @Test
    void testToString() {
        String result = item.toString();
        assertTrue(result.startsWith("REFRIGERATEDMilk"));
        assertTrue(result.contains(" of weight (g) 500.0"));
        assertTrue(result.contains(" and price 1.99"));
        assertTrue(result.contains(" total 10 units"));
        assertTrue(result.contains(" with total value of 19.9"));
    }

    @Test
    void constructor_WithNullDescription() {
        GoodItem nullDescItem = new GoodItem(null, 500.0, 2.99, 5, 4.0, "abc", aisle, shelves);
        assertEquals("UNKNOWN", nullDescItem.getDesc());
    }

    @Test
    void constructor_WithNullShelves() {
        GoodItem nullShelvesItem = new GoodItem("Test", 500.0, 2.99, 5, 4.0, "abc", aisle, null);
        assertNotNull(nullShelvesItem.getShelves());
        assertTrue(nullShelvesItem.getShelves().isEmpty());
    }
}