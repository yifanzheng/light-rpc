package top.yifan.rpc.compressor;

import top.yifan.constants.CompressorType;

/**
 * Default compressor
 *
 * @author Star Zheng
 */
public class Identity implements Compressor {

    public static final String NAME = CompressorType.IDENTITY.getName();

    @Override
    public byte compressorId() {
        return CompressorType.IDENTITY.getCode();
    }

    @Override
    public byte[] compress(byte[] data) {
        return new byte[0];
    }

    @Override
    public byte[] decompress(byte[] data) {
        return new byte[0];
    }
}
