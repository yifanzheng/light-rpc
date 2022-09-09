package top.yifan.compressor;

import top.yifan.extension.SPI;

/**
 * @author Star Zheng
 */
@SPI
public interface Compressor {

    byte[] compress(byte[] data);
    byte[] decompress(byte[] data);
}
