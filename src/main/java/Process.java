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

    public Process(String name, String[] ring) {

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
    public void onTokenOnBus (Token t) {

        if (t.getRecipient().equals(this.thread.getName())) {
            if (this.attendJeton) {
                this.isInSectionCritique = true; // this.thread entre en section critique
                while (isInSectionCritique) {
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

    private void request () {
        this.attendJeton = true;
        while (!this.isInSectionCritique){
            // on attend
        }
        System.out.println("test");
    }

    private void release () {
        this.attendJeton = false;
        // On sort de la section critique
        this.isInSectionCritique = false;
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
