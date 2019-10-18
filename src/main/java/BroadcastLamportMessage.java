public class BroadcastLamportMessage extends LamportMessage {
    private final String sender;

    public BroadcastLamportMessage(String machin, int estampille, String sender) {
        super(machin,estampille);
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

}
