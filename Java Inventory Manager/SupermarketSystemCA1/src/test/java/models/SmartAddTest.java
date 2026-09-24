package models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.CustomList.FunkierList;

import static org.junit.jupiter.api.Assertions.*;

class SmartAddTest {

    private FloorArea testFloorArea;
    private Aisle testAisle;
    private Shelf testShelf;

    @BeforeEach
    void setUp() {
        testFloorArea = new FloorArea("Test Floor", 1,
                new utils.Dimensions(50.0, 30.0, 0.0, 0.0, 0.0),
                new FunkierList<>());

        testAisle = new Aisle("Dairy", "Refrigerated",
                new utils.Dimensions(10.0, 2.0, 5.0, 5.0, 90.0),
                testFloorArea,
                new FunkierList<>());

        testShelf = new Shelf(100.0, 25.5, 10, testAisle, new FunkierList<>());
    }

    @AfterEach
    void tearDown() {
        testFloorArea = null;
        testAisle = null;
        testShelf = null;
    }

    @Test
    void getSuccess() {
        SmartAdd successAdd = new SmartAdd(true, "Item added successfully",
                testFloorArea, testAisle, testShelf);
        assertTrue(successAdd.getSuccess());

        SmartAdd failureAdd = new SmartAdd(false, "No suitable location",
                null, null, null);
        assertFalse(failureAdd.getSuccess());
    }

    @Test
    void getMessage() {
        String expectedMessage = "Test message";
        SmartAdd smartAdd = new SmartAdd(true, expectedMessage,
                testFloorArea, testAisle, testShelf);

        assertEquals(expectedMessage, smartAdd.getMessage());

        SmartAdd nullMessageAdd = new SmartAdd(false, null, null, null, null);
        assertNull(nullMessageAdd.getMessage());
    }

    @Test
    void getFloorArea() {
        SmartAdd smartAdd = new SmartAdd(true, "Test",
                testFloorArea, testAisle, testShelf);

        assertSame(testFloorArea, smartAdd.getFloorArea());

        SmartAdd nullFloorAdd = new SmartAdd(false, "Test",
                null, testAisle, testShelf);
        assertNull(nullFloorAdd.getFloorArea());
    }

    @Test
    void getAisle() {
        SmartAdd smartAdd = new SmartAdd(true, "Test",
                testFloorArea, testAisle, testShelf);

        assertSame(testAisle, smartAdd.getAisle());

        SmartAdd nullAisleAdd = new SmartAdd(false, "Test",
                testFloorArea, null, testShelf);
        assertNull(nullAisleAdd.getAisle());
    }

    @Test
    void getShelf() {
        SmartAdd smartAdd = new SmartAdd(true, "Test",
                testFloorArea, testAisle, testShelf);

        assertSame(testShelf, smartAdd.getShelf());

        SmartAdd nullShelfAdd = new SmartAdd(false, "Test",
                testFloorArea, testAisle, null);
        assertNull(nullShelfAdd.getShelf());
    }

    @Test
    void testToString_SuccessfulWithFullLocation() {
        SmartAdd smartAdd = new SmartAdd(true, "Added to duplicate items",
                testFloorArea, testAisle, testShelf);

        String result = smartAdd.toString();

        assertTrue(result.contains("Smart Add successful"));
        assertTrue(result.contains(testFloorArea.getTitle()));
        assertTrue(result.contains(testAisle.getName()));
        assertTrue(result.contains("Shelf #" + testShelf.getShelfNumber()));
        assertTrue(result.contains("Added to duplicate items"));
    }

    @Test
    void testToString_SuccessfuMiss() {
        // floor
        SmartAdd missingFloor = new SmartAdd(true, "Partial location",
                null, testAisle, testShelf);
        String result1 = missingFloor.toString();
        assertTrue(result1.contains("Smart Add successful"));
        assertFalse(result1.contains("Location:"));

        // aisle
        SmartAdd missingAisle = new SmartAdd(true, "Partial location",
                testFloorArea, null, testShelf);
        String result2 = missingAisle.toString();
        assertTrue(result2.contains("Smart Add successful"));
        assertFalse(result2.contains("Location:"));

        // shelf
        SmartAdd missingShelf = new SmartAdd(true, "Partial location",
                testFloorArea, testAisle, null);
        String result3 = missingShelf.toString();
        assertTrue(result3.contains("Smart Add successful"));
        assertFalse(result3.contains("Location:"));
    }

    @Test
    void testToString_Failure() {
        SmartAdd failureAdd = new SmartAdd(false, "No suitable location found",
                null, null, null);

        String result = failureAdd.toString();

        assertTrue(result.contains("Smart Add Failed"));
        assertTrue(result.contains("No suitable location found"));
        assertFalse(result.contains("Location:"));
    }

    @Test
    void testToString_SuccessfulNoLocation() {
        SmartAdd successNoLocation = new SmartAdd(true, "Item added but no location recorded",
                null, null, null);

        String result = successNoLocation.toString();

        assertTrue(result.contains("Smart Add successful"));
        assertTrue(result.contains("Item added but no location recorded"));
        assertFalse(result.contains("Location:"));
    }

    @Test
    void testToString_EmptyMessage() {
        SmartAdd emptyMessageAdd = new SmartAdd(true, "",
                testFloorArea, testAisle, testShelf);

        String result = emptyMessageAdd.toString();

        assertTrue(result.contains("Smart Add successful"));
        assertTrue(result.contains("Location:"));
    }

    @Test
    void testConstructor_AllParameters() {
        SmartAdd smartAdd = new SmartAdd(true, "Test Message",
                testFloorArea, testAisle, testShelf);

        assertTrue(smartAdd.getSuccess());
        assertEquals("Test Message", smartAdd.getMessage());
        assertSame(testFloorArea, smartAdd.getFloorArea());
        assertSame(testAisle, smartAdd.getAisle());
        assertSame(testShelf, smartAdd.getShelf());
    }
}