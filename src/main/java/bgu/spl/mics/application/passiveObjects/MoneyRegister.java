package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {


	private LinkedList<OrderReceipt> list ;
	private static MoneyRegister moneyRegister;

	public MoneyRegister() {
		this.list = new LinkedList<OrderReceipt>() {

		};
	}

	//Singleton design pattern
	private static class moneyRegisterHolder implements Serializable {
		private static MoneyRegister instance = new MoneyRegister();
	}
	public static MoneyRegister getInstance() {
		return moneyRegisterHolder.instance;
	}

	public LinkedList<OrderReceipt> getList() {
		return list;
	}

	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		list.add(r);
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
	int earning =0 ;
		for (OrderReceipt receipt: list) {
			earning = earning + receipt.getPrice();
		}
			return earning;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
	    c.chargeAmount(amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		try {
			FileOutputStream fileoutput = new FileOutputStream(filename);
			ObjectOutputStream stream = new ObjectOutputStream(fileoutput);
			stream.writeObject(list);
			stream.close();
			fileoutput.close();
		}catch(IOException ex){
		}
	}
}
