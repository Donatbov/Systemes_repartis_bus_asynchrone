import com.google.common.eventbus.Subscribe;


public class Process implements Runnable {
    private Thread thread;
    private EventBusService bus;
    private boolean alive;
    private boolean dead;
    private static int nbProcess = 0;
    private int id = Process.nbProcess++;
    private int clock;
    private RunTests runTests;

    public Process(String name, RunTests runTests) {

        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.


        this.thread = new Thread(this);
        this.thread.setName(name);
        this.clock = 0;
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
    public void onBroadCastMessageOnBus(BroadcastLamportMessage b) {
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
    public void onDedicatedMessageOnBus(DedicatedLamportMessage b) {
        if (b.getRecipient().equals(this.thread.getName())) {
            System.out.println(this.thread.getName() + " get " + b.getMachin() + " with estampille " + b.getEstampille());
            if (b.getEstampille() > this.clock) {
                this.clock = b.getEstampille();
            }
            this.clock++;
            System.out.println(this.thread.getName() + " new clock: " + this.clock);
        }
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
        BroadcastLamportMessage b1 = new BroadcastLamportMessage("ga", ++this.clock, Thread.currentThread().getName());
        BroadcastLamportMessage b2 = new BroadcastLamportMessage("bu", ++this.clock, Thread.currentThread().getName());
        display_test(b1,b2);
        postEvents(b1,b2);
    }

    private void runDedicatedTests () {
        System.out.println("Running dedicated tests");
        DedicatedLamportMessage b1 = new DedicatedLamportMessage("ga", ++this.clock, "P1");
        DedicatedLamportMessage b2 = new DedicatedLamportMessage("bu", ++this.clock, "P2");
        display_test(b1,b2);
        postEvents(b1,b2);
    }

    public void run() {
        int loop = 0;

        // System.out.println(Thread.currentThread().getName() + " id :" + this.id);

        while (this.alive) {
            // System.out.println(Thread.currentThread().getName() + " Loop : " + loop);
            try {
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
