package org.xu.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.xu.pan.core.exception.YPanBusinessException;
import org.xu.pan.core.utils.FileUtils;
import org.xu.pan.core.utils.IdUtil;
import org.xu.pan.server.common.event.log.ErrorLogEvent;
import org.xu.pan.server.modules.file.context.FileSaveContext;
import org.xu.pan.server.modules.file.context.QueryRealFileListContext;
import org.xu.pan.server.modules.file.entity.YPanFile;
import org.xu.pan.server.modules.file.service.IFileService;
import org.xu.pan.server.modules.file.mapper.YPanFileMapper;
import org.springframework.stereotype.Service;
import org.xu.pan.storage.engine.core.StorageEngine;
import org.xu.pan.storage.engine.core.context.DeleteFileContext;
import org.xu.pan.storage.engine.core.context.StoreFileContext;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
* @author 23561
* @description 针对表【y_pan_file(物理文件信息表)】的数据库操作Service实现
* @createDate 2024-09-05 11:01:08
*/
@Service
public class FileServiceImpl extends ServiceImpl<YPanFileMapper, YPanFile>
    implements IFileService, ApplicationContextAware {

    @Autowired
    private StorageEngine storageEngine;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    /**
     * 根据条件查询用户的实际文件列表
     *
     * @param context
     * @return
     */
    @Override
    public List<YPanFile> getFileList(QueryRealFileListContext context) {
        Long userId = context.getUserId();
        String identifier = context.getIdentifier();
        LambdaQueryWrapper<YPanFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Objects.nonNull(userId), YPanFile::getCreateUser, userId);
        queryWrapper.eq(StringUtils.isNotBlank(identifier), YPanFile::getIdentifier, identifier);
        return list(queryWrapper);
    }

    /**
     * 上传单文件并保存实体记录
     * <p>
     * 1、上传单文件
     * 2、保存实体记录
     *
     * @param context
     */
    @Override
    public void saveFile(FileSaveContext context) {
        storeMultipartFile(context);
        YPanFile record = doSaveFile(context.getFilename(),
                context.getRealPath(),
                context.getTotalSize(),
                context.getIdentifier(),
                context.getUserId());
        context.setRecord(record);
    }

    /***************private*****************/

    /**
     * 保存实体文件记录
     *
     * @param filename
     * @param realPath
     * @param totalSize
     * @param identifier
     * @param userId
     * @return
     */
    private YPanFile doSaveFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        YPanFile record = assembleYPanFile(filename, realPath, totalSize, identifier, userId);
        if (!save(record)) {
            try {
                DeleteFileContext deleteFileContext = new DeleteFileContext();
                deleteFileContext.setRealFilePathList(Lists.newArrayList(realPath));
                storageEngine.delete(deleteFileContext);
            } catch (IOException e) {
                e.printStackTrace();
                ErrorLogEvent errorLogEvent = new ErrorLogEvent(this, "文件物理删除失败，请执行手动删除！文件路径: " + realPath, userId);
                applicationContext.publishEvent(errorLogEvent);
            }
        }
        return record;
    }

    /**
     * 拼装文件实体对象
     *
     * @param filename
     * @param realPath
     * @param totalSize
     * @param identifier
     * @param userId
     * @return
     */
    private YPanFile assembleYPanFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        YPanFile record = new YPanFile();

        record.setFileId(IdUtil.get());
        record.setFilename(filename);
        record.setRealPath(realPath);
        record.setFileSize(String.valueOf(totalSize));
        record.setFileSizeDesc(FileUtils.byteCountToDisplaySize(totalSize));
        record.setFileSuffix(FileUtils.getFileSuffix(filename));
        record.setIdentifier(identifier);
        record.setCreateUser(userId);
        record.setCreateTime(new Date());

        return record;
    }

    /**
     * 上传单文件
     * 该方法委托文件存储引擎实现
     *
     * @param context
     */
    private void storeMultipartFile(FileSaveContext context) {
        try {
            StoreFileContext storeFileContext = new StoreFileContext();
            storeFileContext.setInputStream(context.getFile().getInputStream());
            storeFileContext.setFilename(context.getFilename());
            storeFileContext.setTotalSize(context.getTotalSize());
            storageEngine.store(storeFileContext);
            context.setRealPath(storeFileContext.getRealPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new YPanBusinessException("文件上传失败");
        }
    }


}




