
import java.util.concurrent.Executors;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

public class EventBusService {
	 
	private static EventBusService instance = null;
	 
	private EventBus eventBus = null;
	
	private EventBusService() {
		eventBus = new AsyncEventBus(Executors.newCachedThreadPool());
	}
	 
	public static EventBusService getInstance() {
		if (instance==null){
			instance = new EventBusService();
		}
		return instance;
	}
	
	public void registerSubscriber(Object subscriber) {
		eventBus.register(subscriber);
	}
	 
	public void unRegisterSubscriber(Object subscriber) {
		eventBus.unregister(subscriber);
	}
	 
	public void postEvent(Object e) {
		try{
			eventBus.post(e);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
