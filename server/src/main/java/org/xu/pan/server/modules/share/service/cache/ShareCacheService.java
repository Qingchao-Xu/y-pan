package org.xu.pan.server.modules.share.service.cache;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xu.pan.server.common.cache.AbstractManualCacheService;
import org.xu.pan.server.modules.share.entity.YPanShare;
import org.xu.pan.server.modules.share.mapper.YPanShareMapper;

/**
 * 手动缓存实现分享业务的查询等操作
 */
@Component(value = "shareManualCacheService")
public class ShareCacheService extends AbstractManualCacheService<YPanShare> {

    @Autowired
    private YPanShareMapper mapper;

    @Override
    protected BaseMapper<YPanShare> getBaseMapper() {
        return mapper;
    }

    /**
     * 获取缓存Key的模板信息
     *
     * @return
     */
    @Override
    public String getKeyFormat() {
        return "SHARE:ID:%s";
    }
}
