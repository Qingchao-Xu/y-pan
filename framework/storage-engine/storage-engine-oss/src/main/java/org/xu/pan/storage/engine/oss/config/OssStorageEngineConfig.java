package org.xu.pan.storage.engine.oss.config;

import com.aliyun.oss.OSSClient;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.xu.pan.core.exception.YPanFrameworkException;

/**
 * OSS文件存储引擎配置类
 */
@Component
@Data
@ConfigurationProperties(prefix = "org.xu.pan.storage.engine.oss")
public class OssStorageEngineConfig {

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucketName;

    private Boolean autoCreateBucket = Boolean.TRUE;

    /**
     * 注入OSS操作客户端对象
     *
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public OSSClient ossClient() {
        if (StringUtils.isAnyBlank(getEndpoint(), getAccessKeyId(), getAccessKeySecret(), getBucketName())) {
            throw new YPanFrameworkException("the oss config is missed!");
        }
        return new OSSClient(getEndpoint(), getAccessKeyId(), getAccessKeySecret());
    }


}
