package top.yifan.rpc.compressor;

import top.yifan.constants.CompressorType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Gzip
 *
 * @author Star Zheng
 */
public class Gzip implements Compressor {
    @Override
    public byte compressorId() {
        return CompressorType.GZIP.getCode();
    }

    @Override
    public byte[] compress(byte[] dataByteArr) {
        if (dataByteArr == null || dataByteArr.length == 0) {
            return new byte[0];
        }

        try (ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteOutStream)) {
            gzipOutputStream.write(dataByteArr);

            return byteOutStream.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    @Override
    public byte[] decompress(byte[] dataByteArr) {
        if (dataByteArr == null || dataByteArr.length == 0) {
            return new byte[0];
        }

        try (ByteArrayInputStream byteInStream = new ByteArrayInputStream(dataByteArr);
             ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
             GZIPInputStream gzipInputStream = new GZIPInputStream(byteInStream)) {
            int readByteNum;
            byte[] bufferArr = new byte[256];
            while ((readByteNum = gzipInputStream.read(bufferArr)) >= 0) {
                byteOutStream.write(bufferArr, 0, readByteNum);
            }

            return byteOutStream.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
