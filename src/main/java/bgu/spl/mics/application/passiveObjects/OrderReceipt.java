package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable {
	private int orderId;//the id of the order.
	private String seller ; //the name of the service which handled the order.
	private int  id; //the id of the customer the receipt is issued to.
	private String bookTitle;//  title of the book bought.
	private int price ;  //the price the customer paid for the book.
	private int issuedTick; //tick in which this receipt was issued (upon completing the corresponding event).
	private int orderTick; //tick in which the customer ordered the book.
	private int proccessTick; //tick in which the selling service started processing the order.

	public OrderReceipt(int orderId, String seller, int id, String bookTitle, int price, int issuedTick, int orderTick, int proccessTick) {
		this.orderId = orderId;
		this.seller = seller;
		this.id = id;
		this.bookTitle = bookTitle;
		this.price = price;
		this.issuedTick = issuedTick;
		this.orderTick = orderTick;
		this.proccessTick = proccessTick;
	}

	/**
     * Retrieves the orderId of this receipt.
     */
	public int getOrderId() {
		return orderId;
	}
	
	/**
     * Retrieves the name of the selling service which handled the order.
     */
	public String getSeller() {
		return seller;
	}
	
	/**
     * Retrieves the ID of the customer to which this receipt is issued to.
     * <p>
     * @return the ID of the customer
     */
	public int getCustomerId() {
		return id;
	}
	
	/**
     * Retrieves the name of the book which was bought.
     */
	public String getBookTitle() {
		return bookTitle;
	}
	
	/**
     * Retrieves the price the customer paid for the book.
     */
	public int getPrice() {
		return price;
	}
	
	/**
     * Retrieves the tick in which this receipt was issued.
     */
	public int getIssuedTick() {
		return issuedTick;
	}
	
	/**
     * Retrieves the tick in which the customer sent the purchase request.
     */
	public int getOrderTick() {
		return orderTick;
	}
	
	/**
     * Retrieves the tick in which the treating selling service started 
     * processing the order.
     */
	public int getProcessTick() {
		return proccessTick;
	}
}
