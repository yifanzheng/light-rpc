package top.yifan.constants;

/**
 * MessageType
 *
 * @author Star Zheng
 */
public enum MessageType {

    REQUEST((byte) 0x01, "request"),
    RESPONSE((byte) 0x02, "response"),
    HEARTBEAT((byte) 0X03, "heartbeat");

    private final byte code;
    private final String name;

    MessageType(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName(byte code) {
        for (MessageType m : MessageType.values()) {
            if (m.getCode() == code) {
                return m.name;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public byte getCode() {
        return code;
    }
}
