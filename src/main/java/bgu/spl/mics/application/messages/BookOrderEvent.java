package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class BookOrderEvent<T> implements Event<T> {
    private Customer customer;
    private int orderTick;

    public BookOrderEvent(Customer customer, String bookTitle, int orderTick) {
        this.customer = customer;
        this.bookTitle = bookTitle;
        this.orderTick = orderTick;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    private String bookTitle;

    public Customer getCustomer() {
        return customer;
    }

    public int getOrderTick() {
        return orderTick;
    }
}
