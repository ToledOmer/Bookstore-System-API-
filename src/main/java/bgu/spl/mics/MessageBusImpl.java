package bgu.spl.mics;

import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	//round robin of messages
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> messagesOfMicroService;
	//subscribe microservice to message
	private ConcurrentHashMap<Class<? extends Message>,BlockingQueue<MicroService>> serviceByMessage;
	//**********future get null but ConcurrentHashMap cant work with null!!
	private ConcurrentHashMap<Message, Future>  futureOfMessage;

	private MessageBusImpl(){ // // initialization code.
		messagesOfMicroService = new ConcurrentHashMap<>();
		serviceByMessage = new ConcurrentHashMap<>();
		futureOfMessage = new ConcurrentHashMap<>();

	}
	//Singleton design pattern
	private static class messageBusImplHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	public static MessageBusImpl getInstance() {
		return messageBusImplHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		//sync microService HashMap in order to prevent same key(message) twice
		synchronized (serviceByMessage){
			if(serviceByMessage.containsKey(type))//add if there is queue for the type of Broadcast Message
				//add microservice to queue
				serviceByMessage.get(type).add(m);
			else { //if there is not queue for the type of Broadcast Message
				BlockingQueue qu = new LinkedBlockingQueue<MicroService>(); //init queue
				qu.add(m); //add micro service to the queue
				serviceByMessage.put(type, qu); //adding to the hash
			}
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		//sync microService HashMap in order to prevent same key(message) twice
		synchronized (serviceByMessage){
			if (!serviceByMessage.containsKey(type)){ //checks iif there is queue for the type of Broadcast Message
				BlockingQueue qu = new LinkedBlockingQueue<MicroService>();
				qu.add(m);
				serviceByMessage.put(type, qu);
			}
			if(!serviceByMessage.get(type).contains(m))
				serviceByMessage.get(type).add(m); //adding the Microservice to the exist queue
		}

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
			futureOfMessage.get(e).resolve(result);
			futureOfMessage.remove(e);
	}

	@Override
	public  void sendBroadcast(Broadcast b) {
		BlockingQueue<MicroService> microServiceQueue = serviceByMessage.get(b.getClass());
		if (microServiceQueue != null && !microServiceQueue.isEmpty()) {
			for (MicroService microservice : microServiceQueue){  //for each
				//microService can take message from messageQueue on the same time trying to add to the queue
				synchronized (messagesOfMicroService.get(microservice)) {
					//going through all the microservice that subscribe to the given broadcast message
					messagesOfMicroService.get(microservice).add(b);
					//notify all the messages of the queue because it's broadCast
					messagesOfMicroService.get(microservice).notifyAll();
				}
			}
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future <T> future = new Future<>();
		BlockingQueue<MicroService> microServiceQueue = serviceByMessage.get(e.getClass());
		//keep the round robin of microServices queue safe
		synchronized (microServiceQueue){
			if (microServiceQueue != null && !microServiceQueue.isEmpty()) {
				//microService can take message from messageQueue on the same time trying to add to the queue
				MicroService microService = null;
				try {
					microService = microServiceQueue.take();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				synchronized (messagesOfMicroService.get(microService)) {
					if (messagesOfMicroService.get(microService)!=null){
						messagesOfMicroService.get(microService).add(e);
						futureOfMessage.put(e, future);
						microServiceQueue.add(microService);
					}
					else {
						future.resolve(null);
					}
				}
				//won't notify the same microservice twice
				synchronized (microService) {
					microService.notify();
				}
			}
			else {
				future.resolve(null);
			}
			return future;
		}
	}

	@Override
	public void register(MicroService m) {
		//checks if there allocated queue is already exist for the give microservice
		if(messagesOfMicroService.get(m)==null) {
			BlockingQueue microQueue = new LinkedBlockingQueue<MicroService>(); //init queue
			messagesOfMicroService.put(m , microQueue); //adding allocating queue and micro service to the bus
		}
	}

	@Override
	public void unregister(MicroService m) {
		if(messagesOfMicroService.get(m)!=null) //removing only if the microservice exist
		{
			//resolving futures that we are going to remove
			for(Message msg: messagesOfMicroService.get(m))
			{
				if(Event.class.isAssignableFrom(msg.getClass())) {
					if (!futureOfMessage.get(msg).isDone()) {
						futureOfMessage.get(msg).resolve(null);
					}
				}
			}
			messagesOfMicroService.remove(m); //removing the microservice
			for (BlockingQueue<MicroService> removeFrom  : serviceByMessage.values()){
				//removing from (if contains) each Queue that contains it related to Event
				for (MicroService ms : removeFrom){
					if (ms == m){
						removeFrom.remove(ms);
					}
				}
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {

		BlockingQueue<Message> messagesQueue = messagesOfMicroService.get(m);
		Message message = null;
		if (messagesQueue!=null){
			//should change to semaphore of 1 permits
			synchronized (messagesQueue) {
				while (messagesQueue.isEmpty())
					messagesQueue.wait();
				message = messagesQueue.take();
			}
		}
		return message;
	}
}
