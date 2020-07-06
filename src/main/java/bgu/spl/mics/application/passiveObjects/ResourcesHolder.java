package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */


//hold collection of vehicles

public class ResourcesHolder implements Serializable {
	private LinkedBlockingQueue<DeliveryVehicle> vehicles;
	private LinkedBlockingQueue<Future<DeliveryVehicle>> futureVehicles;
	private Semaphore semVehicle;
	private Semaphore semFutureVehicle;
//	private static ResourcesHolder resourcesHolder;

	public ResourcesHolder() {
		this.vehicles = new LinkedBlockingQueue<DeliveryVehicle>();
		this.futureVehicles = new LinkedBlockingQueue<Future<DeliveryVehicle>>();
		semFutureVehicle = new Semaphore(0);
		semVehicle = new Semaphore(0);

//		resourcesHolder = getInstance();
	}

	//Singleton design pattern
	private static class resourcesHolderHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}
	public static ResourcesHolder getInstance() {
		return resourcesHolderHolder.instance;
	}


	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		//semaphore keep they will take only amount og current vehicles
		Future<DeliveryVehicle> futureVehicle=new Future<DeliveryVehicle>();
		if (semVehicle.tryAcquire()){
			try {
				// resolve/set the first vechile in the blocking queue
				futureVehicle.resolve(vehicles.take());
				return futureVehicle;
			} catch (InterruptedException e) {
			}
		}
		else {
			futureVehicles.add(futureVehicle);
			semFutureVehicle.release();
		}
		return futureVehicle;
	}
	
	/**
	 *
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		//acquire future only if there are permits
		if (semFutureVehicle.tryAcquire()){
			try {
				//instead of returning to the blocking queue,
				// the car will be used for another waiting delivery
				futureVehicles.take().resolve(vehicle);
			} catch (InterruptedException e) {
			}
		}
		else {
			// add it to the vehicles queue and release permit(there is one more available vehicle)
			vehicles.add(vehicle);
			semVehicle.release();

		}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for (DeliveryVehicle vechile: vehicles) {
			this.vehicles.add(vechile);
			semVehicle.release(); //adding one permit
		}
	}

}
