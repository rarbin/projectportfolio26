package controllers.managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import controllers.API.SupermarketAPI;
import models.*;
import utils.CustomList.FunkierList;
import utils.Dimensions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class StockViewerTest {

    private StockViewer stockViewer;
    private SupermarketAPI system;
    private FloorArea floorArea;
    private Aisle aisle;
    private Shelf shelf;
    private GoodItem goodItem;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        system = new SupermarketAPI();
        stockViewer = new StockViewer(system);

        Dimensions floorDimensions = new Dimensions(100.0, 50.0, 0.0, 0.0, 10.0);
        floorArea = new FloorArea("Dairy Section", 0, floorDimensions, new FunkierList<>());
        Dimensions aisleDimensions = new Dimensions(10.0, 5.0, 0.0, 0.0, 10.0);
        aisle = new Aisle("Cheese", "Refrigerated", aisleDimensions, floorArea, new FunkierList<>());
        shelf = new Shelf(50.0, 0.0, 0, aisle, new FunkierList<>());
        goodItem = new GoodItem("Cheddar Cheese", 500.0, 5.99, 10, 4.0,
                "https://example.com/cheddar.jpg", aisle, new FunkierList<>());

        floorArea.getAisles().addAtLast(aisle);
        aisle.getShelves().addAtLast(shelf);
        shelf.getItems().addAtLast(goodItem);
        goodItem.getShelves().addAtLast(shelf);

        system.addFloorArea(floorArea);
        system.getGoodItems().addAtLast(goodItem);

        shelf.setCurrentWeight((500.0 * 10) / 1000.0);
        shelf.setCurrentQuantity(10);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        stockViewer = null;
        system = null;
        floorArea = null;
        aisle = null;
        shelf = null;
        goodItem = null;
        outputStream.reset();
    }


    @Test
    void viewStockSummaryEmpty() {
        SupermarketAPI emptySystem = new SupermarketAPI();
        StockViewer emptyViewer = new StockViewer(emptySystem);
        emptyViewer.viewStockSummary();
    }

    @Test
    void viewStockSummaryTotalValItems() {
        stockViewer.viewStockSummary();
    }


    @Test
    void displayFloorAreaStock_ValidArea_ShouldDisplayOneAreaStock() {
        stockViewer.dispOneFloor(floorArea);
    }



    @Test
    void displayItemStock_NullItem_ShouldDispOneErrorMessage() {
        stockViewer.dispOneItem(null);

    }


    @Test
    void dispOneItemStock_ItemNoPhoto_ShouldHandleMissingPhoto() {
        GoodItem noPhotoItem = new GoodItem("Butter", 250.0, 2.99, 5, 4.0, null, aisle, new FunkierList<>());
        stockViewer.dispOneItem(noPhotoItem);

    }

    @Test
    void displayAisleStock_NullAisle_ShouldDisplayOneErrorMessage() {
        stockViewer.dispOneAisle(null);
    }


    @Test
    void viewStockSummary_EmptySystem_ShouldDisplayNoStockMessage() {
        SupermarketAPI emptySystem = new SupermarketAPI();
        StockViewer emptyViewer = new StockViewer(emptySystem);

        emptyViewer.stockReport();
    }


    @Test
    void stockReport_MultipleTemperatures_ShouldCountCorrectly() {
        Aisle unrefrigAisle = new Aisle("Canned Goods", "Unrefrigerated",
                new Dimensions(12.0, 4.0, 0.0, 0.0, 10.0),
                floorArea, new FunkierList<>());

        Shelf unrefrigShelf = new Shelf(60.0, 0.0, 0, unrefrigAisle, new FunkierList<>());
        GoodItem unrefrigItem = new GoodItem("Canned Beans", 400.0, 1.29, 25, 20.0,
                "no photo", unrefrigAisle, new FunkierList<>());

        floorArea.getAisles().addAtLast(unrefrigAisle);
        unrefrigAisle.getShelves().addAtLast(unrefrigShelf);
        unrefrigShelf.getItems().addAtLast(unrefrigItem);
        unrefrigItem.getShelves().addAtLast(unrefrigShelf);

        unrefrigShelf.setCurrentWeight((400.0 * 25) / 1000.0); // 10kg
        unrefrigShelf.setCurrentQuantity(25);

        system.getGoodItems().addAtLast(unrefrigItem);


        stockViewer.stockReport();
    }


    @Test
    void displayAisleShelves_PrivateMethod_ShouldBeCalledByDispOneAisle() {
        Shelf shelf2 = new Shelf(25.0, 0.0, 0, aisle, new FunkierList<>());
        aisle.getShelves().addAtLast(shelf2);

        stockViewer.dispOneAisle(aisle);
    }


    @Test
    void dispOneFloorAreaStock_WithNullAisles_ShouldHandleGracefully() {
        FloorArea brokenArea = new FloorArea("Broken Area", 1,
                new Dimensions(50.0, 30.0, 0.0, 0.0, 10.0),
                null);

        stockViewer.dispOneFloor(brokenArea);
    }
    @Nested
    class LowStockReportTests {
        @Test
        void getLowStockReportWithItemsBelowThreshold() {
            system.getGoodItems().clear();


            GoodItem item1 = new GoodItem("Item1", 100.0, 1.0, 500, 4.0, "", aisle, new FunkierList<>());
            GoodItem item2 = new GoodItem("Item2", 100.0, 1.0, 20, 4.0, "", aisle, new FunkierList<>()); // This should be low stock


            Shelf newShelf = new Shelf(100.0, 0.0, 2, aisle, new FunkierList<>());
            newShelf.getItems().addAtLast(item1);
            newShelf.getItems().addAtLast(item2);
            item1.getShelves().addAtLast(newShelf);
            item2.getShelves().addAtLast(newShelf);
            aisle.getShelves().addAtLast(newShelf);


            system.getGoodItems().addAtLast(item1);
            system.getGoodItems().addAtLast(item2);

            String report = stockViewer.getLowStockReport();
            System.out.println("Report: " + report);

            assertTrue(report.contains("Item2"));
            assertTrue(report.contains("20 units remaining"));
        }
    }

}