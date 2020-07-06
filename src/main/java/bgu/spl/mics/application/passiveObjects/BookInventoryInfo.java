package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.*;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {

	private String bookTitle; // the title of the book.
	private AtomicInteger amount; //the number books of bookTitle currently in the inventory.
	private int price; //  the price of the book.

    public BookInventoryInfo(String bookTitle, int amount, int price) {
        this.bookTitle = bookTitle;
        this.amount.set(amount);
        this.price = price;
    }

    /**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String getBookTitle() {
		return bookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return amount.get();
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return price;
	}

	public void addAmount()
	{
		amount.incrementAndGet();
	}
	public void reduceAmount() {
		amount.decrementAndGet();
	}
}