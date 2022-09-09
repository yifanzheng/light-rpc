package top.yifan.constants;

/**
 * @author Star Zheng
 */
public enum CompressType {

    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    CompressType(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName(byte code) {
        for (CompressType c : CompressType.values()) {
            if (c.code == code) {
                return c.name;
            }
        }
        return null;
    }

    public byte getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
