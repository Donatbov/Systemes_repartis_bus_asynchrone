public enum RunTests {
    LAMPORT(0),
    BROADCAST(1),
    DEDICATED(2);

    private int value;

    private RunTests(int value) {
        this.value = value;
    }

    static RunTests fromValue(int value) {
        for (RunTests my: RunTests.values()) {
            if (my.value == value) {
                return my;
            }
        }
        return null;
    }

    int getValue() {
        return value;
    }
}