package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private BlockingQueue<CheckAvailabilityEvent> checkAvailabilityEvents;
//	private Customer customer;
	private int currTick;
	private  Integer orderId;
	private CountDownLatch latch;
	public SellingService(CountDownLatch latch,String name) {
		super(name);
		checkAvailabilityEvents = new LinkedBlockingQueue();
//		this.customer = customer;
		//*** maybe should move orderID to webApi
		this.orderId = 0;
		this.latch = latch;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, tickBroadcast ->{
			currTick = tickBroadcast.getCurrTick();
			if (tickBroadcast.getDuration()==currTick) terminate();
		} );
		 //pr
		subscribeEvent(BookOrderEvent.class, bookOrderEvent->{

			//tick to start work on event(process tick)
			int processTick = currTick;
			Customer customer = bookOrderEvent.getCustomer();
			int customerOrderTick = bookOrderEvent.getOrderTick();
			//future result is book price
			CheckAvailabilityEvent checkAvailability = new CheckAvailabilityEvent(bookOrderEvent.getBookTitle());
			Future<Integer> isBookAvailableFuture = sendEvent(checkAvailability);
			Future<OrderResult> orderResultFuture = new Future<OrderResult>();
			OrderReceipt orderReceipt = null;
			TakeBookEvent takeBook = null;
			if (isBookAvailableFuture.get()!=null && isBookAvailableFuture.get()!=-1){
				if (customer.checkAndCharge(isBookAvailableFuture.get())!= -1) {
					//currentTick is issuedTick, now I create OrderReciept
					takeBook = new TakeBookEvent(bookOrderEvent.getBookTitle());
					orderResultFuture = sendEvent(takeBook);
					if (orderResultFuture!=null&&orderResultFuture.get()!=null && orderResultFuture.get().equals(OrderResult.SUCCESSFULLY_TAKEN)) {
						//OrderTick is getting from customer WebAPI
						orderReceipt = new OrderReceipt(orderId,
								getName(),
								bookOrderEvent.getCustomer().getId(), bookOrderEvent.getBookTitle(), isBookAvailableFuture.get(),
								currTick, customerOrderTick
								, processTick);
						MoneyRegister.getInstance().file(orderReceipt);
						DeliveryEvent delivery = new DeliveryEvent(bookOrderEvent.getCustomer());
						sendEvent(delivery);
						orderId++;
						customer.addReceipt(orderReceipt);
						complete(bookOrderEvent, orderReceipt);
					}
				}
				else {
					complete(bookOrderEvent,null);
				}
			}
			else {
				complete(bookOrderEvent,null);
			}

		});
		latch.countDown();
	}

}
