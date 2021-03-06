import java.util.Scanner;

public class GuavaExemple{
	 
	 public static void main(String[] args){
		 EventBusService bus;
		 bus = EventBusService.getInstance();
		 String[] ring = {"P1", "P2"};

		 Scanner sc = new Scanner(System.in);
		 int answer = -1;

	     // Lancement du Token une fois tous les processus créés
	     Token t = new Token(ring[0]);
	     bus.postEvent(t);

	     while (true) {
			 System.out.println("run tests for lamport messages : 0");
			 System.out.println("run tests for broadcast messages : 1");
			 System.out.println("run tests for dedicated messages : 2");
			 System.out.println("exit program : anything else than precedent commands");
			 try {
				 answer = sc.nextInt();
			 } catch (Exception e) {
				 break;
			 }

			 if (answer < 0 || answer > 2) {
				 break;
			 }

			 RunTests answer_enum = RunTests.fromValue(answer);
			 Process p1 = new Process(ring[0], ring, answer_enum);
			 Process p2 = new Process(ring[0], ring, answer_enum);

			 try {
				 Thread.sleep(2000);
			 } catch (Exception e) {
				 e.printStackTrace();
			 }

			 p1.stop();
			 p2.stop();
		 }
	 }
}
