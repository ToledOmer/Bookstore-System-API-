package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {

	private int id; // The id number of the customer.
	private String name;   // The name of the customer.
	private String address;//The address of the customer.
	private int distance  ; //the distance of the customer’s address from the store.
	private List<OrderReceipt> orderReceipts = new LinkedList<OrderReceipt>(); //all the receipts issued to the customer.
	private CreditCard creditCard  ;//The number of the credit card of the customer.
	private ArrayList<OrderSchedule> orderSchedule;

	public Customer(int id, String name, String address, int distance, CreditCard creditCard, ArrayList<OrderSchedule> orderSchedule) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.distance = distance;
		this.creditCard = creditCard;
		this.orderSchedule = orderSchedule;
	}

	public void addReceipt(OrderReceipt order){
	//init orderReceipts (because the json doesnt init it)
		if(this.orderReceipts == null) this.orderReceipts = new LinkedList<>();
		orderReceipts.add(order);
	}

	/**
	 * Retrieves the name of the customer.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the ID of the customer  .
	 */
	public int getId() {
		return id;
	}

	/**
	 * Retrieves the address of the customer.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Retrieves the distance of the customer from the store.
	 */
	public int getDistance() {
		return distance;
	}


	/**
	 * Retrieves a list of receipts for the purchases this customer has made.
	 * <p>
	 * @return A list of receipts.
	 */
	public List<OrderReceipt> getCustomerReceiptList() {
		return orderReceipts;
	}
	//sync because: when 2 selling services order book on the same tick
	// and he has only money for one book,
	// he will still get the book
	public synchronized int checkAndCharge (int tocharge){
		if (creditCard.getAmount()-tocharge>=0){
			chargeAmount(tocharge);
			return tocharge;
		}
		else
			return  -1;
	}
	/**
	 * Retrieves the amount of money left on this customers credit card.
	 * <p>
	 * @return Amount of money left.
	 */

	public int getAvailableCreditAmount() {
		return creditCard.getAmount();
	}

	/**
	 charge ammount from credit card
	 */
	public  void chargeAmount(int amount){
		creditCard.chargeAmount(amount);
	}

	/**
	 * Retrieves this customers credit card serial number.
	 */
	public int getCreditNumber(){
		return creditCard.getNumber();
	}
	public CreditCard getCreditcard() {
		return creditCard;
	}
	public ArrayList<OrderSchedule> getorderSchedules() {
		return orderSchedule;
	}

	public int getTickByBookTitle(String book) {
		//searching for a book at the order Schedule array using his name
		for (OrderSchedule bookToFind : orderSchedule)
			// retrurn the book tick if find
			if (book == bookToFind.getBookTitle()) return bookToFind.getTick();
		//return -1 if the book has not found (wont happen , using only for debugging)
		return -1;
	}
}
