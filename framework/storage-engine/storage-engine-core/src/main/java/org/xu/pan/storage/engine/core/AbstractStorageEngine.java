package org.xu.pan.storage.engine.core;

import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.xu.pan.cache.core.constants.CacheConstants;
import org.xu.pan.core.exception.YPanFrameworkException;

import java.io.IOException;
import java.util.Objects;

/**
 * 文件存储引擎模块公用抽象类
 * 具体的文件存储实现方案的公用逻辑需要抽离到该类中
 */
public abstract class AbstractStorageEngine implements StorageEngine {

    @Autowired
    private CacheManager cacheManager;

    /**
     * 公用的获取缓存的方法
     * @return
     */
    protected Cache getCache() {
        if (Objects.isNull(cacheManager)) {
            throw new YPanFrameworkException("具体的缓存实现需要引用到项目中");
        }
        return cacheManager.getCache(CacheConstants.Y_PAN_CACHE_NAME);
    }

}
