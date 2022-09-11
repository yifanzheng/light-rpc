package top.yifan.constants;

/**
 * CodecConstants
 *
 * @author Star Zheng
 */
public class CodecConstants {

    public static final int HEAD_LENGTH = 16;

    /**
     * Magic Number.
     */
    public static final byte[] MAGIC_NUM = {(byte) 'l', (byte) 'r', (byte) 'p', (byte) 'c'};
    public static final byte VERSION = 1;

    public static final byte HEARTBEAT_TYPE = 3;

    /**
     * 8M
     */
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

    public static final String CODEC_TRANSPROT = "transport";
}
