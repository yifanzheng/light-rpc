package top.yifan.rpc.exchange;

import lombok.Data;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 传输的消息
 *
 * @author Star Zheng
 */
@Data
@ToString
public class Message {

    private static final AtomicInteger INVOKE_ID = new AtomicInteger(0);

    /**
     * message id
     */
    private int mId;

    /**
     * message type: request、response
     */
    private byte mType;

    /**
     * 编解码 type
     */
    private byte codec;

    /**
     * compress type
     */
    private byte compress;

    /**
     * 是否解码失败
     */
    private boolean broken = false;

    /**
     * body data
     */
    private Object data;

    public Message() {
        this.mId = newId();
    }

    public Message(int mId, byte mType, byte codec, byte compress) {
        this.mId = mId;
        this.mType = mType;
        this.codec = codec;
        this.compress = compress;
    }

    public static int newId() {
        // getAndIncrement() When it grows to MAX_VALUE, it will grow to MIN_VALUE, and the negative can be used as ID
        return INVOKE_ID.getAndIncrement();
    }
}
