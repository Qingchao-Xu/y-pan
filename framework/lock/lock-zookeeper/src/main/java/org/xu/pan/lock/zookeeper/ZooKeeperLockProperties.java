package org.xu.pan.lock.zookeeper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.xu.pan.lock.core.LockConstants;

@Data
@Component
@ConfigurationProperties(prefix = "org.xu.pan.lock.zookeeper")
public class ZooKeeperLockProperties {

    /**
     * zk链接地址，多个使用逗号隔开
     */
    private String host = "127.0.0.1:2181";

    /**
     * zk分布式锁的根路径
     */
    private String rootPath = LockConstants.Y_PAN_LOCK_PATH;

}
