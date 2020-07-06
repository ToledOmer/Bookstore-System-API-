package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;


/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	private CountDownLatch latch;
	private ConcurrentLinkedQueue<Future<DeliveryVehicle>> notResolved = new ConcurrentLinkedQueue<>();
	private boolean toTerminate;

	public ResourceService(CountDownLatch latch,String name) {
		super(name);
		this.latch=latch;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, tickBroadcast ->{
			if (tickBroadcast.getDuration()==tickBroadcast.getCurrTick()){
				//resolving future that wont be completed due to time issues
				for (Future<DeliveryVehicle> future : notResolved) {
					if (!future.isDone()) {
						future.resolve(null);
					}
				}
				toTerminate =true;
			}
			if (toTerminate){
				terminate();
			}
		} );
		//acquire vehicle event
		subscribeEvent(AcquireVehicleEvent.class, acquireVehicleEvent -> {
			Future<DeliveryVehicle> deliveryVehicleFuture = new Future<DeliveryVehicle>();
			deliveryVehicleFuture = ResourcesHolder.getInstance().acquireVehicle();
			if(deliveryVehicleFuture.isDone()){
				if (deliveryVehicleFuture!=null){
					complete(acquireVehicleEvent,deliveryVehicleFuture);
				}
			}
			else{
				notResolved.add(deliveryVehicleFuture);
				complete(acquireVehicleEvent,null);
			}
//
		});
		//release vehicle
		subscribeEvent(ReleaseVehicleEvent.class, releaseVehicleEvent -> {
			ResourcesHolder.getInstance().releaseVehicle(releaseVehicleEvent.getVehicle());
			complete(releaseVehicleEvent,null);
		});
		latch.countDown();

	}

}


