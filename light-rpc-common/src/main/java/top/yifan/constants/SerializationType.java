package top.yifan.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * SerializationType
 *
 * @author Star Zheng
 */
public enum SerializationType {

    KRYO((byte) 0x01, "kryo"),
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

    public static SerializationType getInstance(String name) {
        for (SerializationType c : SerializationType.values()) {
            if (StringUtils.equals(c.getName(), name)) {
                return c;
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
