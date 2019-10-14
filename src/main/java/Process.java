import com.google.common.eventbus.Subscribe;


public class Process implements Runnable {
    private Thread thread;
    private EventBusService bus;
    private boolean alive;
    private boolean dead;
    private static int nbProcess = 0;
    private int id = Process.nbProcess++;
    private int clock;

    public Process(String name) {

        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.


        this.thread = new Thread(this);
        this.thread.setName(name);
        this.clock = 0;
        this.alive = true;
        this.dead = false;
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
        System.out.println("new clock: " + this.clock);
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

            System.out.println("new clock: " + this.clock);
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

    public void run() {
        int loop = 0;

        System.out.println(Thread.currentThread().getName() + " id :" + this.id);

        while (this.alive) {
            System.out.println(Thread.currentThread().getName() + " Loop : " + loop);
            try {
                Thread.sleep(500);

                if (Thread.currentThread().getName().equals("P1")) {
                    // LamportMessage b1 = new LamportMessage("ga", ++this.clock);
                    // LamportMessage b2 = new LamportMessage("bu", ++this.clock);
//                     BroadcastMessage b1 = new BroadcastMessage("ga", ++this.clock, Thread.currentThread().getName());
//                     BroadcastMessage b2 = new BroadcastMessage("bu", ++this.clock, Thread.currentThread().getName());
                    DedicatedMessage b1 = new DedicatedMessage("ga", ++this.clock, "P1");
                    DedicatedMessage b2 = new DedicatedMessage("bu", ++this.clock, "P2");
                    System.out.println(Thread.currentThread().getName() + " send : " + b1.getMachin() +
                            " with lamport clock : " + b1.getEstampille());
                    System.out.println(Thread.currentThread().getName() + " send : " + b2.getMachin() +
                            " with lamport clock : " + b2.getEstampille());
                    bus.postEvent(b1);
                    bus.postEvent(b2);
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
