package models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.CustomList.FunkierList;
import utils.Dimensions;

import static org.junit.jupiter.api.Assertions.*;

class FloorAreaTest {

    private FloorArea floorArea;
    private Dimensions dimensions;
    private FunkierList<Aisle> aisles;

    @BeforeEach
    void setUp() {
        dimensions = new Dimensions(10.0, 20.0, 0.0, 0.0, 32.0);
        floorArea = new FloorArea("Test Floor", 1, dimensions, aisles);
    }

    @AfterEach
    void tearDown() {
        floorArea = null;
        dimensions = null;
        aisles = null;
    }

    @Test
    void setTitle_InvalidLength() {
        String originalTitle = floorArea.getTitle();
        floorArea.setTitle("123456789012345678901");
        assertEquals(originalTitle, floorArea.getTitle());
    }

    @Test
    void setTitle_Null() {
        String originalTitle = floorArea.getTitle();
        floorArea.setTitle(null);
        assertEquals(originalTitle, floorArea.getTitle());
    }

    @Test
    void setTitle_EmptyString() {
        floorArea.setTitle("");
        assertEquals("", floorArea.getTitle());
    }

    @Test
    void setAisles_Null() {
        floorArea.setAisles(null);
        assertNotNull(floorArea.getAisles());
        assertTrue(floorArea.getAisles().isEmpty());
    }

    @Test
    void setAisles_WithAisles() {
        FunkierList<Aisle> newAisles = new FunkierList<>();
        floorArea.setAisles(newAisles);
        assertSame(newAisles, floorArea.getAisles());
    }

    @Test
    void getAisle() {

        assertNotNull(floorArea.getAisle());
        assertTrue(floorArea.getAisle().isEmpty());

        assertSame(floorArea.getAisle(), floorArea.getAisles());

        Dimensions aisleDims = new Dimensions(5.0, 2.0, 0.0, 0.0, 10.0);
        FunkierList<Shelf> shelves = new FunkierList<>();
        Aisle aisle1 = new Aisle("Dairy", "Refrigerated", aisleDims, floorArea, shelves);

        floorArea.getAisle().addAtLast(aisle1);
        assertFalse(floorArea.getAisle().isEmpty());
        assertEquals(1, floorArea.getAisle().getSize());
        assertEquals("Dairy", floorArea.getAisle().get(0).getName());
    }

    @Test
    void getDimensions() {
        Dimensions retrievedDimensions = floorArea.getDimensions();
        assertNotNull(retrievedDimensions);
        assertSame(dimensions, retrievedDimensions);

        assertEquals(20.0, retrievedDimensions.getWidth(), 0.001);
        assertEquals(10.0, retrievedDimensions.getLength(), 0.001);
        assertEquals(0.0, retrievedDimensions.getPosX(), 0.001);
        assertEquals(0.0, retrievedDimensions.getPosY(), 0.001);
        assertEquals(32.0, retrievedDimensions.getOrientation());
    }


    @Test
    void constructor_WithNullAisles() {
        FloorArea floorWithNullAisles = new FloorArea("Test", 1, dimensions, null);
        assertNotNull(floorWithNullAisles.getAisles());
        assertTrue(floorWithNullAisles.getAisles().isEmpty());
    }

    @Test
    void constructor_WithNullTitle() {
        FloorArea floorWithNullTitle = new FloorArea(null, 1, dimensions, aisles);
        assertEquals("Untitled", floorWithNullTitle.getTitle());
    }

    @Test
    void toString_WithAisles() {
        Dimensions aisleDims = new Dimensions(5.0, 2.0, 0.0, 0.0, 10.0);
        FunkierList<Shelf> shelves = new FunkierList<>();
        Aisle aisle1 = new Aisle("Dairy", "Refrigerated", aisleDims, floorArea, shelves);
        Aisle aisle2 = new Aisle("Bakery", "Unrefrigerated", aisleDims, floorArea, shelves);

        floorArea.getAisles().addAtLast(aisle1);
        floorArea.getAisles().addAtLast(aisle2);

        String result = floorArea.toString();
        assertTrue(result.contains("2 aisles"));
    }

    @Test
    void getLevel() {
        assertEquals(1, floorArea.getLevel());
        FloorArea groundFloor = new FloorArea("Ground", 0, dimensions, aisles);
        assertEquals(0, groundFloor.getLevel());
    }


}
