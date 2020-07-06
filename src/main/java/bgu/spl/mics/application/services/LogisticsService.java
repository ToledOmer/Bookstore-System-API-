package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private CountDownLatch latch;
	private int tick;
	private int duration;
	private int speed;
	public LogisticsService(CountDownLatch latch,String name,int speed) {
		super(name);
		this.latch=latch;
		this.speed = speed;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, tickBroadcast ->{
			tick = tickBroadcast.getCurrTick();
			duration = tickBroadcast.getDuration();
			if (tickBroadcast.getDuration()==tickBroadcast.getCurrTick()) terminate();
		} );
		subscribeEvent(DeliveryEvent.class, deliveryEvent -> {
			AcquireVehicleEvent acquireVehicle = new AcquireVehicleEvent();
			//wait until can acquire deliveryVechile
			Future<Future<DeliveryVehicle>>  deliveryVehicle =  sendEvent(acquireVehicle);
			//then, deliver it
			//vehicle can be required
			if(deliveryVehicle != null)
			{	//gets the available vehicle
				Future<DeliveryVehicle> vehicleCanBeAcquired = deliveryVehicle.get();
				//init a deliveryVehicle
				DeliveryVehicle vehicle = null;
				if (vehicleCanBeAcquired != null)
					vehicle = vehicleCanBeAcquired.get();
				if (vehicle != null){
					//deliver
					vehicle.deliver(deliveryEvent.getCustomer().getAddress(),
							deliveryEvent.getCustomer().getDistance());
					//release the vehicle
					ReleaseVehicleEvent releaseVehicleEvent = new ReleaseVehicleEvent(vehicle);
					sendEvent(releaseVehicleEvent);
				}
			}
		});
		latch.countDown();

	}

}
