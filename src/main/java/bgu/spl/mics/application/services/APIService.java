package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {
    private int tick;
    private Customer customer;
    private CountDownLatch latch;
    private ArrayList<OrderSchedule> orderSchedules;

    public APIService(Customer customer, CountDownLatch latch, String name) {
        super(name);
        this.customer = customer;
        this.latch = latch;
        orderSchedules = customer.getorderSchedules();
        orderSchedules.sort(Comparator.comparing(OrderSchedule::getTick));
    }

    //subscribe to
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
            //order tick for customer reciept
             tick = tickBroadcast.getCurrTick();
            if (tickBroadcast.getDuration() == tick){
                terminate();

            }
            //when orderSchedule list not empty and tick>=first in list
            while (!orderSchedules.isEmpty() && tick >= orderSchedules.get(0).getTick()){
                OrderSchedule nextOrder = orderSchedules.remove(0);
                BookOrderEvent bookOrderEvent = new BookOrderEvent(customer, nextOrder.getBookTitle(),nextOrder.getTick());
                sendEvent(bookOrderEvent);

            }
        });
        latch.countDown();
    }

}
