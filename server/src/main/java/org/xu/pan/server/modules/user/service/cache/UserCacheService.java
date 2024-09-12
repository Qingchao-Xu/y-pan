package org.xu.pan.server.modules.user.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.xu.pan.cache.core.constants.CacheConstants;
import org.xu.pan.server.common.cache.AnnotationCacheService;
import org.xu.pan.server.modules.user.entity.YPanUser;
import org.xu.pan.server.modules.user.mapper.YPanUserMapper;

import java.io.Serializable;

/**
 * 用户模块缓存业务处理类
 */
@Component(value = "userAnnotationCacheService")
public class UserCacheService implements AnnotationCacheService<YPanUser> {

    @Autowired
    private YPanUserMapper mapper;

    /**
     * 根据ID查询实体信息
     *
     * @param id
     * @return
     */
    @Cacheable(cacheNames = CacheConstants.Y_PAN_CACHE_NAME, keyGenerator = "userIdKeyGenerator", sync = true) // 配置sync通过本地锁解决缓存穿透
    @Override
    public YPanUser getById(Serializable id) {
        return mapper.selectById(id);
    }

    /**
     * 根据ID更新缓存信息
     *
     * @param id
     * @param entity
     * @return
     */
    @CachePut(cacheNames = CacheConstants.Y_PAN_CACHE_NAME, keyGenerator = "userIdKeyGenerator")
    @Override
    public boolean updateById(Serializable id, YPanUser entity) {
        return mapper.updateById(entity) == 1;
    }

    /**
     * 根据ID删除缓存信息
     *
     * @param id
     * @return
     */
    @CacheEvict(cacheNames = CacheConstants.Y_PAN_CACHE_NAME, keyGenerator = "userIdKeyGenerator")
    @Override
    public boolean removeById(Serializable id) {
        return mapper.deleteById(id) == 1;
    }
}
