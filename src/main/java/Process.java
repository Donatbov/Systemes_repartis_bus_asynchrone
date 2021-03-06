import com.google.common.eventbus.Subscribe;


public class Process implements Runnable {
    private Thread thread;
    private EventBusService bus;
    private boolean alive;
    private boolean dead;
    private static int nbProcess = 0;
    private int id = Process.nbProcess++;
    private int clock;
    private String[] ring;
    private boolean isInSectionCritique;
    private boolean attendJeton;
    private RunTests runTests;

    public Process(String name, String[] ring, RunTests runTests) {

        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.


        this.thread = new Thread(this);
        this.thread.setName(name);
        this.clock = 0;
        this.ring = ring;
        this.isInSectionCritique = false;
        this.attendJeton = false;
        this.alive = true;
        this.dead = false;
        this.runTests = runTests;
        this.thread.start();
    }

    // Declaration de la methode de callback invoquee lorsqu'un message de type Bidule transite sur le bus
    @Subscribe
    public void onTrucSurBus(LamportMessage b) {
        System.out.println(Thread.currentThread().getName() + " receives: " + b.getMachin() + " for "
                + this.thread.getName() + " and estampille: " + b.getEstampille());
        if (b.getEstampille() > this.clock) {
            this.clock = b.getEstampille();
        }
        this.clock++;
        System.out.println(this.thread.getName() + " new clock: " + this.clock);
    }

    @Subscribe
    public void onBroadCastMessageOnBus(BroadcastMessage b) {
        if (!b.getSender().equals(this.thread.getName())) {
            System.out.println(this.thread.getName() + " receives: " + b.getMachin() + " from "
                    + b.getSender() + " and estampille: " + b.getEstampille());
            if (b.getEstampille() > this.clock) {
                this.clock = b.getEstampille();
            }
            this.clock++;

            System.out.println(this.thread.getName() + " new clock: " + this.clock);
        }
    }

    @Subscribe
    public void onDedicatedMessageOnBus(DedicatedMessage b) {
        if (b.getRecipient().equals(this.thread.getName())) {
            System.out.println(this.thread.getName() + " get " + b.getMachin() + " with estampille " + b.getEstampille());
            if (b.getEstampille() > this.clock) {
                this.clock = b.getEstampille();
            }
            this.clock++;
            System.out.println(this.thread.getName() + " new clock: " + this.clock);
        }
    }

    @Subscribe
    public void onTokenOnBus (Token t) throws InterruptedException {
        if (t.getRecipient().equals(this.thread.getName())) {
            if (this.attendJeton) {
                this.isInSectionCritique = true; // this.thread entre en section critique
                while (isInSectionCritique) {
                    Thread.sleep(50);
                    // on attend de ne plus etre en section critique
                }
            }
            t.setRecipient(this.ring[(this.indexOnBus() + 1) % this.ring.length]);
            bus.postEvent(t);
        }
    }

    private int indexOnBus () {
        for (int i = 0; i < this.ring.length; ++i) {
            if (this.ring[i].equals(this.thread.getName())) {
                return i;
            }
        }
        System.err.println("l'index du processus local n'existe pas dans le ring");
        return -1;
    }

    private void request () throws InterruptedException {
        this.attendJeton = true;
        while (!this.isInSectionCritique){
            // on attend
            Thread.sleep(50);
        }
    }

    private void release () {
        this.attendJeton = false;
        // On sort de la section critique
        this.isInSectionCritique = false;
    }

    private void display_test(LamportMessage b1, LamportMessage b2) {
        System.out.println(Thread.currentThread().getName() + " send : " + b1.getMachin() +
                " with lamport clock : " + b1.getEstampille());
        System.out.println(Thread.currentThread().getName() + " send : " + b2.getMachin() +
                " with lamport clock : " + b2.getEstampille());
    }

    private void postEvents(LamportMessage b1, LamportMessage b2) {
        bus.postEvent(b1);
        bus.postEvent(b2);
    }

    private void runLamportTests() {
        System.out.println("Running lamport tests");
        LamportMessage b1 = new LamportMessage("ga", ++this.clock);
        LamportMessage b2 = new LamportMessage("bu", ++this.clock);
        display_test(b1,b2);
        postEvents(b1,b2);
    }

    private void runBroadcastTests() {
        System.out.println("Running broadcast tests");
        BroadcastMessage b1 = new BroadcastMessage("ga", ++this.clock, Thread.currentThread().getName());
        BroadcastMessage b2 = new BroadcastMessage("bu", ++this.clock, Thread.currentThread().getName());
        display_test(b1,b2);
        postEvents(b1,b2);
    }

    private void runDedicatedTests () {
        System.out.println("Running dedicated tests");
        DedicatedMessage b1 = new DedicatedMessage("ga", ++this.clock, "P1");
        DedicatedMessage b2 = new DedicatedMessage("bu", ++this.clock, "P2");
        display_test(b1,b2);
        postEvents(b1,b2);
    }

    public void run() {
        int loop = 0;

        // System.out.println(Thread.currentThread().getName() + " id :" + this.id);

        while (this.alive) {
            // System.out.println(Thread.currentThread().getName() + " Loop : " + loop);
            try {
                this.request();  // bloquant jusqu'à l'obtention du token
                // Do some stuff
                Thread.sleep(500);
                if (Thread.currentThread().getName().equals("P1")) {
                    switch (this.runTests) {
                        case LAMPORT :
                            runLamportTests();
                            break;
                        case BROADCAST :
                            runBroadcastTests();
                            break;
                        case DEDICATED :
                            runDedicatedTests();
                            break;
                        default :
                            System.out.println("Exiting program...");
                            break;
                    }
                }

                System.out.println(this.thread.getName() + " est en section critique");
                this.release(); // revoie le token au prochain process sur l'anneau
            } catch (Exception e) {
                e.printStackTrace();
            }
            loop++;
        }

        // liberation du bus
        this.bus.unRegisterSubscriber(this);
        this.bus = null;
        System.out.println(Thread.currentThread().getName() + " stoped");
        this.dead = true;
    }

    public void waitStoped() {
        while (!this.dead) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.alive = false;
    }
}
