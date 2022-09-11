package top.yifan.rpc.serialize;

import lombok.extern.slf4j.Slf4j;
import top.yifan.extension.ExtensionLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SerializeSupport
 *
 * @author Star Zheng
 */
@Slf4j
public class SerializeSupport {

    private static final Map<Byte, Serialization> ID_SERIALIZATION_MAP = new HashMap<>();

    static {
        ExtensionLoader<Serialization> extensionLoader = ExtensionLoader.getExtensionLoader(Serialization.class);
        Set<String> supportedExtensions = extensionLoader.getSupportedExtensions();
        for (String name : supportedExtensions) {
            Serialization serialization = extensionLoader.getExtension(name);
            byte idByte = serialization.getSerializeId();
            if (ID_SERIALIZATION_MAP.containsKey(idByte)) {
                log.error("Serialization extension " + serialization.getClass().getName()
                        + " has duplicate id to Serialization extension "
                        + ID_SERIALIZATION_MAP.get(idByte).getClass().getName()
                        + ", ignore this Serialization extension");
                continue;
            }
            ID_SERIALIZATION_MAP.put(idByte, serialization);
        }
    }

    private SerializeSupport() {
    }

    public static Serialization getSerializationById(Byte id) {
        return ID_SERIALIZATION_MAP.get(id);
    }

}
