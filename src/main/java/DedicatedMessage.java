public class DedicatedMessage {
    private final String machin;
    private final int estampille;
    private final String recipient;

    public DedicatedMessage(String machin, int estampille, String recipient) {
        this.machin = machin;
        this.estampille = estampille;
        this.recipient = recipient;
    }

    public String getMachin() {
        return this.machin;
    }

    public String getRecipient() {
        return recipient;
    }

    public int getEstampille() {
        return this.estampille;
    }

    public String toString() {
        return "Ga Bu Zo Meu: " + this.machin + " estampille : " + this.estampille;
    }

}
