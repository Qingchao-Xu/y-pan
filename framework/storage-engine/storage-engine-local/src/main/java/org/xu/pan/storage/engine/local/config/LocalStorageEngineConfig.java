package org.xu.pan.storage.engine.local.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.xu.pan.core.utils.FileUtils;

@Component
@ConfigurationProperties(prefix = "org.xu.pan.storage.engine.local")
@Data
public class LocalStorageEngineConfig {

    /**
     * 实际存放路径的前缀
     */
    private String rootFilePath = FileUtils.generateDefaultStoreFileRealPath();

    /**
     * 实际存放文件分片的路径的前缀
     */
    private String rootFileChunkPath = FileUtils.generateDefaultStoreFileChunkRealPath();

}
