package top.yifan.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Star Zheng
 */
public enum CompressorType {

    IDENTITY((byte) 0x00, "identity"),
    GZIP((byte) 0x01, "gzip"),
    BZIP2((byte) 0x02, "bzip2"),
    SNAPPY((byte) 0x03, "snappy");

    private final byte code;
    private final String name;

    CompressorType(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName(byte code) {
        for (CompressorType c : CompressorType.values()) {
            if (c.code == code) {
                return c.name;
            }
        }
        return IDENTITY.getName();
    }

    public static CompressorType getInstance(String name) {
        for (CompressorType c : CompressorType.values()) {
            if (StringUtils.equals(c.getName(), name)) {
                return c;
            }
        }
        return IDENTITY;
    }

    public byte getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
