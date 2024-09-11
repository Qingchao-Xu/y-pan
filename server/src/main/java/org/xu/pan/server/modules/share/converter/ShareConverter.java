package org.xu.pan.server.modules.share.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.xu.pan.server.modules.share.context.CreateShareUrlContext;
import org.xu.pan.server.modules.share.po.CreateShareUrlPO;

/**
 * 分享模块实体转化工具类
 */
@Mapper(componentModel = "spring")
public interface ShareConverter {

    @Mapping(target = "userId", expression = "java(org.xu.pan.server.common.utils.UserIdUtil.get())")
    CreateShareUrlContext createShareUrlPO2CreateShareUrlContext(CreateShareUrlPO createShareUrlPO);

}
