package controllers.managers;

import controllers.API.SupermarketAPI;
import models.Aisle;
import models.FloorArea;
import models.GoodItem;
import models.Shelf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.CustomList.FunkierList;
import utils.Dimensions;

import static org.junit.jupiter.api.Assertions.*;

class SmartAddManagerTest {
    private SmartAddManager smartAddManager;
    private SupermarketAPI api;
    private Aisle refrigeratedAisle;
    private Shelf shelf1;
    private Shelf shelf2;

    @BeforeEach void setUp() {
        api = new SupermarketAPI();
        smartAddManager = new SmartAddManager(api);
        Dimensions floorDims = new Dimensions(100.0,50.0,0.0,0.0,10.0);
        FloorArea floorArea = new FloorArea("Main Floor",1,floorDims,new FunkierList<>());
        Dimensions aisleDims = new Dimensions(10.0,5.0,0.0,0.0,10.0);
        refrigeratedAisle = new Aisle("Dairy","REFRIGERATED",aisleDims,floorArea,new FunkierList<>());
        shelf1 = new Shelf(100.0,50.0,10,refrigeratedAisle,new FunkierList<>());
        shelf2 = new Shelf(100.0,30.0,5,refrigeratedAisle,new FunkierList<>());
        api.addShelf(shelf1); api.addShelf(shelf2);
        refrigeratedAisle.getShelves().addAtLast(shelf1); refrigeratedAisle.getShelves().addAtLast(shelf2);
    }

    @Test void smartAddItemDuplicate() {
        GoodItem milk = new GoodItem("Milk",500.0,1.99,1,4.0,"",refrigeratedAisle,new FunkierList<>());
        shelf1.getItems().addAtLast(milk); api.getGoodItems().addAtLast(milk);
        GoodItem moreMilk = new GoodItem("Milk",500.0,1.99,1,4.0,"",refrigeratedAisle,new FunkierList<>());
        var result = smartAddManager.smartAddItem(moreMilk);
        assertTrue(result.getSuccess()); assertTrue(result.getMessage().contains("duplicate"));
    }

    @Test void smartAddItemSimilarKeyword() {
        GoodItem cheddar = new GoodItem("Cheddar",250.0,3.99,1,4.0,"",refrigeratedAisle,new FunkierList<>());
        shelf1.getItems().addAtLast(cheddar); api.getGoodItems().addAtLast(cheddar);
        smartAddManager = new SmartAddManager(api);
        GoodItem slices = new GoodItem("Cheddar Slices",200.0,2.99,1,4.0,"",refrigeratedAisle,new FunkierList<>());
        var result = smartAddManager.smartAddItem(slices);
        assertTrue(result.getSuccess()); assertTrue(result.getMessage().contains("similar"));
    }

    @Test void smartAddItemTemperatureAndSpace() {
        GoodItem yogurt = new GoodItem("Yogurt",150.0,1.49,1,4.0,"",refrigeratedAisle,new FunkierList<>());
        var result = smartAddManager.smartAddItem(yogurt);
        assertTrue(result.getSuccess()); assertTrue(result.getMessage().contains("temperature and space"));
    }

    @Test void smartAddItemNoSuitableLocation() {
        shelf1.setCurrentWeight(100.0); shelf2.setCurrentWeight(100.0);
        GoodItem cheese = new GoodItem("Cheese",300.0,4.99,1,4.0,"",refrigeratedAisle,new FunkierList<>());
        var result = smartAddManager.smartAddItem(cheese);
        assertFalse(result.getSuccess()); assertTrue(result.getMessage().contains("No suitable location"));
    }
}