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
    private int msgId;

    /**
     * message type: request、response
     */
    private byte msgType;

    /**
     * 编解码 type
     */
    private byte codec;

    /**
     * compress type
     */
    private byte compress;

    /**
     * body data
     */
    private Object data;

    public Message() {
        this.msgId = newId();
    }

    public Message(int msgId, byte msgType, byte codec, byte compress) {
        this.msgId = msgId;
        this.msgType = msgType;
        this.codec = codec;
        this.compress = compress;
    }

    public static int newId() {
        // getAndIncrement() When it grows to MAX_VALUE, it will grow to MIN_VALUE, and the negative can be used as ID
        return INVOKE_ID.getAndIncrement();
    }
}
