package org.xu.pan.server.modules.file.converter;

import org.xu.pan.server.modules.file.context.*;
import org.xu.pan.server.modules.file.po.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 文件模块实体转化工具类
 */
@Mapper(componentModel = "spring")
public interface FileConverter {

    @Mapping(target = "parentId", expression = "java(org.xu.pan.core.utils.IdUtil.decrypt(createFolderPO.getParentId()))")
    @Mapping(target = "userId", expression = "java(org.xu.pan.server.common.utils.UserIdUtil.get())")
    CreateFolderContext createFolderPO2CreateFolderContext(CreateFolderPO createFolderPO);

}
