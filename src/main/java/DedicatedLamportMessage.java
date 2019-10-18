public class DedicatedLamportMessage extends LamportMessage {
    private final String recipient;

    public DedicatedLamportMessage(String machin, int estampille, String recipient) {
        super(machin, estampille);
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }
}
