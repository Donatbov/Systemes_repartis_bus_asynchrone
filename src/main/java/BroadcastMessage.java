public class BroadcastMessage extends LamportMessage {
    private final String sender;

    public BroadcastMessage(String machin, int estampille, String sender) {
        super(machin,estampille);
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

}
