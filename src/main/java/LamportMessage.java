public class LamportMessage {
    protected final String machin;
    protected final int estampille;

    public LamportMessage(String machin, int estampille) {
        this.machin = machin;
        this.estampille = estampille;
    }

    public String getMachin() {
        return this.machin;
    }

    public int getEstampille() {
        return this.estampille;
    }

    public String toString() {
        return "Ga Bu Zo Meu: " + this.machin + " estampille : " + this.estampille;
    }
}
