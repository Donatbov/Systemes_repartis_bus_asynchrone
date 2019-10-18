
public class GuavaExemple{
	 
	 public static void main(String[] args){
		 EventBusService bus;
		 bus = EventBusService.getInstance();
		 String[] ring = {"P1", "P2"};

	     Process p1 = new Process(ring[0], ring);
	     Process p2 = new Process(ring[1], ring);
	     // Lancement du Token une fois tous les processus créés
	     Token t = new Token(ring[0]);
	     bus.postEvent(t);

	     try{
		 	Thread.sleep(2000);
	     }catch(Exception e){
		 	e.printStackTrace();
	     }

	     p1.stop();
	     p2.stop();
	 }
}
