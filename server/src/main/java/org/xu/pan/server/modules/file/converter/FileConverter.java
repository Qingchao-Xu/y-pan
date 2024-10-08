package org.xu.pan.server.modules.file.converter;

import org.xu.pan.server.modules.file.context.*;
import org.xu.pan.server.modules.file.entity.YPanUserFile;
import org.xu.pan.server.modules.file.po.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.xu.pan.server.modules.file.vo.FolderTreeNodeVO;
import org.xu.pan.server.modules.file.vo.YPanUserFileVO;
import org.xu.pan.storage.engine.core.context.StoreFileChunkContext;

/**
 * 文件模块实体转化工具类
 */
@Mapper(componentModel = "spring")
public interface FileConverter {

    @Mapping(target = "parentId", expression = "java(org.xu.pan.core.utils.IdUtil.decrypt(createFolderPO.getParentId()))")
    @Mapping(target = "userId", expression = "java(org.xu.pan.server.common.utils.UserIdUtil.get())")
    CreateFolderContext createFolderPO2CreateFolderContext(CreateFolderPO createFolderPO);

    @Mapping(target = "fileId", expression = "java(org.xu.pan.core.utils.IdUtil.decrypt(updateFilenamePO.getFileId()))")
    @Mapping(target = "userId", expression = "java(org.xu.pan.server.common.utils.UserIdUtil.get())")
    UpdateFilenameContext updateFilenamePO2UpdateFilenameContext(UpdateFilenamePO updateFilenamePO);

    @Mapping(target = "userId", expression = "java(org.xu.pan.server.common.utils.UserIdUtil.get())")
    DeleteFileContext deleteFilePO2DeleteFileContext(DeleteFilePO deleteFilePO);

    @Mapping(target = "parentId", expression = "java(org.xu.pan.core.utils.IdUtil.decrypt(secUploadFilePO.getParentId()))")
    @Mapping(target = "userId", expression = "java(org.xu.pan.server.common.utils.UserIdUtil.get())")
    SecUploadFileContext secUploadFilePO2SecUploadFileContext(SecUploadFilePO secUploadFilePO);

    @Mapping(target = "parentId", expression = "java(org.xu.pan.core.utils.IdUtil.decrypt(fileUploadPO.getParentId()))")
    @Mapping(target = "userId", expression = "java(org.xu.pan.server.common.utils.UserIdUtil.get())")
    FileUploadContext fileUploadPO2FileUploadContext(FileUploadPO fileUploadPO);

    @Mapping(target = "record", ignore = true)
    FileSaveContext fileUploadContext2FileSaveContext(FileUploadContext context);

    @Mapping(target = "userId", expression = "java(org.xu.pan.server.common.utils.UserIdUtil.get())")
    FileChunkUploadContext fileChunkUploadPO2FileChunkUploadContext(FileChunkUploadPO fileChunkUploadPO);

    FileChunkSaveContext fileChunkUploadContext2FileChunkSaveContext(FileChunkUploadContext context);

    @Mapping(target = "realPath", ignore = true)
    StoreFileChunkContext fileChunkSaveContext2StoreFileChunkContext(FileChunkSaveContext context);

    @Mapping(target = "userId", expression = "java(org.xu.pan.server.common.utils.UserIdUtil.get())")
    QueryUploadedChunksContext queryUploadedChunksPO2QueryUploadedChunksContext(QueryUploadedChunksPO queryUploadedChunksPO);

    @Mapping(target = "userId", expression = "java(org.xu.pan.server.common.utils.UserIdUtil.get())")
    @Mapping(target = "parentId", expression = "java(org.xu.pan.core.utils.IdUtil.decrypt(fileChunkMergePO.getParentId()))")
    FileChunkMergeContext fileChunkMergePO2FileChunkMergeContext(FileChunkMergePO fileChunkMergePO);

    FileChunkMergeAndSaveContext fileChunkMergeContext2FileChunkMergeAndSaveContext(FileChunkMergeContext context);

    @Mapping(target = "label", source = "record.filename")
    @Mapping(target = "id", source = "record.fileId")
    @Mapping(target = "children", expression = "java(com.google.common.collect.Lists.newArrayList())")
    FolderTreeNodeVO yPanUserFile2FolderTreeNodeVO(YPanUserFile record);

    YPanUserFileVO yPanUserFile2RPanUserFileVO(YPanUserFile record);
}
