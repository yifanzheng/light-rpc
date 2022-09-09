package top.yifan.constants;

/**
 * SerializationType
 *
 * @author Star Zheng
 */
public enum SerializationType {

    KYRO((byte) 0x01, "kyro"),
    PROTOSTUFF((byte) 0x02, "protostuff"),
    HESSIAN2((byte) 0X03, "hessian2"),
    JSON((byte) 0X04, "json");

    private final byte code;
    private final String name;

    SerializationType(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName(byte code) {
        for (SerializationType c : SerializationType.values()) {
            if (c.getCode() == code) {
                return c.name;
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
