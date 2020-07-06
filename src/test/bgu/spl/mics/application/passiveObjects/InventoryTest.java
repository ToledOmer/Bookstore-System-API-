package bgu.spl.mics.application.passiveObjects;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class InventoryTest {
    private Inventory inventory;

    @Before
    public void setUp() throws Exception {
        inventory = Inventory.getInstance();
        BookInventoryInfo[] inventoryb = new BookInventoryInfo[3];
        BookInventoryInfo book1 = new BookInventoryInfo("Harry Potter", 2,50);
        BookInventoryInfo book2 = new BookInventoryInfo("Zbang", 5, 70);
        BookInventoryInfo book3 = new BookInventoryInfo("Naruto", 0,12);
        inventoryb[0] = book1;
        inventoryb[1] = book2;
        inventoryb[2] = book3;
        inventory.load(inventoryb);
    }

    @After
    public void tearDown() throws Exception {
        inventory = null;
    }

    @Test
    public void getInstance() {
        assertEquals(Inventory.getInstance(), inventory);
    }

    @Test
    public void load() {
        BookInventoryInfo[] inventory1 = new BookInventoryInfo[1];
        BookInventoryInfo book3 = new BookInventoryInfo("Bible", 2,40);
        inventory1[0] = book3;
        //check books that are availvable
        assertTrue(inventory.checkAvailabiltyAndGetPrice("Harry Potter") == 50);
        assertTrue(inventory.checkAvailabiltyAndGetPrice("Zbang") == 70);
        //check book with 0 amount
        assertTrue(inventory.checkAvailabiltyAndGetPrice("Naruto") == -1);
        //cehck book that not exist
        assertTrue(inventory.checkAvailabiltyAndGetPrice("noExist") == -1);
        inventory.load(inventory1);
        //check if th new load worked
        assertTrue(inventory.checkAvailabiltyAndGetPrice("Bible") == 40);


    }

    @Test
    public void take() {
        //if book not avalaivable return not in stock
        assertTrue(inventory.take("Naruto") == OrderResult.NOT_IN_STOCK);
        //take book and check if still exist
        inventory.take("Harry Potter");
        assertTrue(inventory.checkAvailabiltyAndGetPrice("Harry Potter") == 50) ;
        //the book won't be available
        inventory.take("Harry Potter");
        assertFalse(inventory.checkAvailabiltyAndGetPrice("Harry Potter") == 50) ;
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        //regular check the if price is false
        assertFalse(inventory.checkAvailabiltyAndGetPrice("Harry Potter") == 54) ;
        assertFalse(inventory.checkAvailabiltyAndGetPrice("Zbang") == 72);
        assertFalse(inventory.checkAvailabiltyAndGetPrice("Naruto") == 12) ;
        //check not available books
        assertTrue(inventory.checkAvailabiltyAndGetPrice("Naruto") == -1) ;
        assertTrue(inventory.checkAvailabiltyAndGetPrice("noExist") == -1);



    }

    @Test
    public void printInventoryToFile() {
    }
}