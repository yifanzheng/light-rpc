package top.yifan.rpc.compressor;

import top.yifan.constants.CompressorType;
import top.yifan.exception.RpcException;

import java.io.IOException;

/**
 * snappy compressor
 * <p>
 * Provide high-speed compression speed and reasonable compression ratio
 *
 * @author Star Zheng
 * @link https://github.com/google/snappy
 */
public class Snappy implements Compressor {

    @Override
    public byte compressorId() {
        return CompressorType.SNAPPY.getCode();
    }

    @Override
    public byte[] compress(byte[] dataByteArr) {
        if (dataByteArr == null || dataByteArr.length == 0) {
            return new byte[0];
        }
        try {
            return org.xerial.snappy.Snappy.compress(dataByteArr);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public byte[] decompress(byte[] dataByteArr) {
        if (dataByteArr == null || dataByteArr.length == 0) {
            return new byte[0];
        }
        try {
            return org.xerial.snappy.Snappy.uncompress(dataByteArr);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
