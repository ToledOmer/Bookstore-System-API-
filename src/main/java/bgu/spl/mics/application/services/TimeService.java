package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private static TimeService timeService ;
	private Timer timer;
	private TimerTask Task;
	private int speed; //will be received from json
	private int tick;
	private int duration; //will be received from json

	//constructor
	public TimeService(int speed , int duration) {
		super("timeService");
		//starts tick from '0' and will become '1' when initialize
		tick = 1;
		timer  = new Timer();
        this.speed = speed;
        this.duration = duration;
        Task = new TimerTask() {
			@Override
			public void run() {
				if (tick==duration) {
					terminate();
					this.cancel(); //cancel the timerTask
					timer.cancel();
				}
				//increments by one
				tick++;
				//send broadcast message right after incrementing
				sendBroadcast(new TickBroadcast(tick,duration));
			}
		};
	}
	@Override
	protected void initialize() {
		timer.schedule(Task,0,speed);
		terminate();
	}
}

