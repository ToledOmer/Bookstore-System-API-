package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable {
//	key is the book’s title(String), value is the amount of this book(Integer)
	private ConcurrentHashMap<String,BookInventoryInfo> booksMap;


	public Inventory() {
		this.booksMap = new ConcurrentHashMap<String,BookInventoryInfo>();
	}

	/**
     * Retrieves the single instance of this class.
     */

	//Singleton design pattern
	private static class InventoryHolder implements Serializable {
		private static Inventory instance = new Inventory();
	}
	public static Inventory getInstance() {
		return InventoryHolder.instance;
	}

	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[] inventory ) {
		//add books to hashmap
		for (BookInventoryInfo book : inventory){
			if(!booksMap.containsKey(book.getBookTitle())){
				booksMap.put(book.getBookTitle(), book);
			}
		}
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {
		//locking the amount of the book itself
		Semaphore sem = new Semaphore(booksMap.get(book).getAmountInInventory());
		OrderResult resul;
		//if can acquire
		if (sem.tryAcquire()){
			booksMap.get(book).reduceAmount();
			resul = OrderResult.SUCCESSFULLY_TAKEN;
		}
		else
		{  //book not available
			resul = OrderResult.NOT_IN_STOCK;
		}
		return resul;
	}



	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		//check if the book exist and if there is at least one book
		if (booksMap.containsKey(book) && booksMap.get(book).getAmountInInventory() > 0)
		{
			return booksMap.get(book).getPrice();
	}
		else
			return -1;
	}
	public ConcurrentHashMap<String, BookInventoryInfo> getBooksMap() {
		return booksMap;
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename){
		HashMap<String, Integer> hashMapToPrint = new HashMap<String, Integer>();
		for (BookInventoryInfo bookToAdd: booksMap.values() ) {
			if(!hashMapToPrint.containsKey(bookToAdd.getBookTitle())){
				hashMapToPrint.put(bookToAdd.getBookTitle(),bookToAdd.getAmountInInventory()); //adding a book title and book amount
			}
		}
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(hashMapToPrint);
			oos.close();
			fos.close();
		}catch(IOException ex)
		{
		}
	}
}
