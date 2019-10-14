public class BroadcastMessage {
    private final String machin;
    private final int estampille;
    private final String sender;

    public BroadcastMessage(String machin, int estampille, String sender) {
        this.machin = machin;
        this.estampille = estampille;
        this.sender = sender;
    }

    public String getMachin() {
        return this.machin;
    }

    public String getSender() {
        return sender;
    }

    public int getEstampille() {
        return this.estampille;
    }

    public String toString() {
        return "Ga Bu Zo Meu: " + this.machin + " estampille : " + this.estampille;
    }

}
