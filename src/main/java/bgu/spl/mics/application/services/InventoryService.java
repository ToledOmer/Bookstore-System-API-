package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.CountDownLatch;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private CountDownLatch latch;

	public InventoryService(CountDownLatch latch,String name) {
		super(name);
		this.latch=latch;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, tickBroadcast ->{
			if (tickBroadcast.getDuration()==tickBroadcast.getCurrTick()) terminate();
		} );

		subscribeEvent(CheckAvailabilityEvent.class, checkAvailabilityEvent->{
		//check availvable of book, -1 if not availvable, else return price
				Integer bookPrice = Inventory.getInstance().checkAvailabiltyAndGetPrice(checkAvailabilityEvent.getBookTitle());
				complete(checkAvailabilityEvent, bookPrice);
		});
		subscribeEvent(TakeBookEvent.class , takeBookEvent -> {
				OrderResult orderResult = Inventory.getInstance().take(takeBookEvent.getBookTitle());
				complete(takeBookEvent,orderResult);
		});
		latch.countDown();
	}

}
