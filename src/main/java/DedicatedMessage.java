public class DedicatedMessage extends LamportMessage {
    private final String recipient;

    public DedicatedMessage(String machin, int estampille, String recipient) {
        super(machin, estampille);
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }
}
