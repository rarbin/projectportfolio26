package models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.CustomList.FunkierList;
import utils.Dimensions;

import static org.junit.jupiter.api.Assertions.*;

class ShelfTest {

    private Shelf shelf;
    private Aisle aisle;
    private FunkierList<GoodItem> items;

    @BeforeEach
    void setUp() {
        Dimensions floorDims = new Dimensions(100.0, 50.0, 0.0, 0.0, 10.0);
        FloorArea floorArea = new FloorArea("Main Floor", 1, floorDims, new FunkierList<>());
        Dimensions aisleDims = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
        aisle = new Aisle("Dairy", "Refrigerated", aisleDims, floorArea, new FunkierList<>());
        items = new FunkierList<>();
        shelf = new Shelf(100.0, 50.0, 10, aisle, items);
    }

    @AfterEach
    void tearDown() {
        shelf = null;
        aisle = null;
        items = null;
    }

    @Test
    void getShelfNumber() {
        int number = shelf.getShelfNumber();
        assertTrue(number >= 0);

        Shelf anotherShelf = new Shelf(50.0, 10.0, 5, aisle, items);
        assertEquals(number + 1, anotherShelf.getShelfNumber());
    }

    @Test
    void getCapacity() {
        assertEquals(100.0, shelf.getCapacity(), 0.001);
    }

    @Test
    void getCurrentWeight() {
        assertEquals(50.0, shelf.getCurrentWeight(), 0.001);
    }

    @Test
    void setCurrentWeight() {
        shelf.setCurrentWeight(75.0);
        assertEquals(75.0, shelf.getCurrentWeight(), 0.001);

        shelf.setCurrentWeight(0.0);
        assertEquals(0.0, shelf.getCurrentWeight(), 0.001);

        shelf.setCurrentWeight(100.0);
        assertEquals(100.0, shelf.getCurrentWeight(), 0.001);
    }

    @Test
    void setCurrentWeight_Invalid() {
        shelf.setCurrentWeight(50.0);
        shelf.setCurrentWeight(-10.0);
        assertEquals(50.0, shelf.getCurrentWeight(), 0.001);

        shelf.setCurrentWeight(150.0);
        assertEquals(50.0, shelf.getCurrentWeight(), 0.001);
    }

    @Test
    void getCurrentQuantity() {
        assertEquals(10, shelf.getCurrentQuantity());
    }

    @Test
    void setCurrentQuantity() {
        shelf.setCurrentQuantity(20);
        assertEquals(20, shelf.getCurrentQuantity());
    }

    @Test
    void setCurrentQuantity_Invalid() {
        shelf.setCurrentQuantity(15);
        shelf.setCurrentQuantity(-5);
        assertEquals(15, shelf.getCurrentQuantity());
    }

    @Test
    void getAisle() {
        assertSame(aisle, shelf.getAisle());
    }

    @Test
    void setAisle() {
        Dimensions floorDims = new Dimensions(100.0, 50.0, 0.0, 0.0, 10.0);
        FloorArea floorArea = new FloorArea("Second Floor", 2, floorDims, new FunkierList<>());
        Dimensions aisleDims = new Dimensions(5.0, 3.0, 0.0, 0.0, 10.0);
        Aisle newAisle = new Aisle("Test Aisle", "Frozen", aisleDims, floorArea, new FunkierList<>());

        shelf.setAisle(newAisle);
        assertSame(newAisle, shelf.getAisle());
    }

    @Test
    void setAisle_Null() {
        shelf.setAisle(null);
        assertNull(shelf.getAisle());
    }

    @Test
    void getItems() {
        assertNotNull(shelf.getItems());
        assertTrue(shelf.getItems().isEmpty());
    }

    @Test
    void setItems() {
        FunkierList<GoodItem> newItems = new FunkierList<>();
        shelf.setItems(newItems);
        assertSame(newItems, shelf.getItems());
    }


    @Test
    void getAvailableSpace() {
        assertEquals(50.0, shelf.getAvailableSpace(), 0.001);
        shelf.setCurrentWeight(33.33);
        assertEquals(66.67, shelf.getAvailableSpace(), 0.001);
    }

    @Test
    void testToString() {
        String result = shelf.toString();
        assertTrue(result.contains("Shelf #" + shelf.getShelfNumber()));
        assertTrue(result.contains("capacity: 100.0kg"));
        assertTrue(result.contains("currently using 50.0kg"));
        assertTrue(result.contains("10 items"));
        assertTrue(result.contains("50.0kg available"));
    }

    @Test
    void constructor_WithNullAisle() {
        Shelf shelfWithNullAisle = new Shelf(50.0, 10.0, 5, null, items);
        assertNull(shelfWithNullAisle.getAisle());
    }

    @Test
    void constructor_WithNullItems() {
        Shelf shelfWithNullItems = new Shelf(50.0, 10.0, 5, aisle, null);
        assertNotNull(shelfWithNullItems.getItems());
        assertTrue(shelfWithNullItems.getItems().isEmpty());
    }

    @Test
    void shelfNumber_Increment() {
        int firstNumber = shelf.getShelfNumber();
        Shelf secondShelf = new Shelf(50.0, 10.0, 2, aisle, items);
        assertEquals(firstNumber + 1, secondShelf.getShelfNumber());
    }
}