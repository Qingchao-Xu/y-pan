package org.xu.pan.storage.engine.oss.initializer;

import com.aliyun.oss.OSSClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.xu.pan.core.exception.YPanFrameworkException;
import org.xu.pan.storage.engine.oss.config.OssStorageEngineConfig;


/**
 * OSS桶初始化器
 */
@Component
@Slf4j
public class OssBucketInitializer implements CommandLineRunner {

    @Autowired
    private OssStorageEngineConfig config;

    @Autowired
    private OSSClient client;

    @Override
    public void run(String... args) throws Exception {
        boolean bucketExist = client.doesBucketExist(config.getBucketName());

        if (!bucketExist && config.getAutoCreateBucket()) {
            client.createBucket(config.getBucketName());
        }

        if (!bucketExist && !config.getAutoCreateBucket()) {
            throw new YPanFrameworkException("the bucket " + config.getBucketName() + " is not available");
        }

        log.info("the bucket " + config.getBucketName() + " have been created!");
    }

}
