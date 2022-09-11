package top.yifan.rpc.compressor;

import org.apache.commons.lang3.StringUtils;
import top.yifan.constants.CompressorType;
import top.yifan.extension.ExtensionLoader;

/**
 * CompressorSupport
 *
 * @author Star Zheng
 */
public class CompressorSupport {

    private CompressorSupport() {
    }

    public static Compressor getCompressor(byte compressorId) {
        String compressorName = CompressorType.getName(compressorId);
        if (StringUtils.equalsIgnoreCase(Identity.NAME, compressorName)) {
            return new Identity();
        }
        return ExtensionLoader.getExtensionLoader(Compressor.class).getExtension(compressorName);
    }
}
