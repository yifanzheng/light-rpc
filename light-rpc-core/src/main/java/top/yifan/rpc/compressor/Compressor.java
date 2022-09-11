package top.yifan.rpc.compressor;

import top.yifan.extension.SPI;

/**
 * @author Star Zheng
 */
@SPI
public interface Compressor {

    byte compressorId();

    byte[] compress(byte[] data);
    byte[] decompress(byte[] data);
}
