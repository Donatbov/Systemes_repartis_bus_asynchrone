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
        if (!b.getSender().equals(Thread.currentThread().getName())) {
            System.out.println(Thread.currentThread().getName() + " receives: " + b.getMachin() + " for "
                    + this.thread.getName() + " and estampille: " + b.getEstampille());
            if (b.getEstampille() > this.clock + 1) {
                this.clock = b.getEstampille();
            } else {
                this.clock++;
            }
            System.out.println("new clock: " + this.clock);
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
                    this.clock++;
                    BroadcastMessage b1 = new BroadcastMessage("ga", this.clock, Thread.currentThread().getName());
                    BroadcastMessage b2 = new BroadcastMessage("bu", this.clock, Thread.currentThread().getName());
                    System.out.println(Thread.currentThread().getName() + " send : " + b1.getMachin() +
                            " with lamport clock : " + b1.getEstampille());
                    bus.postEvent(b1);
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
