package org.xu.pan.server.modules.file.service;

import org.xu.pan.server.modules.file.context.FileChunkMergeAndSaveContext;
import org.xu.pan.server.modules.file.context.FileSaveContext;
import org.xu.pan.server.modules.file.context.QueryRealFileListContext;
import org.xu.pan.server.modules.file.entity.YPanFile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 23561
* @description 针对表【y_pan_file(物理文件信息表)】的数据库操作Service
* @createDate 2024-09-05 11:01:08
*/
public interface IFileService extends IService<YPanFile> {

    /**
     * 根据条件查询用户的实际文件列表
     *
     * @param context
     * @return
     */
    List<YPanFile> getFileList(QueryRealFileListContext context);

    /**
     * 上传单文件并保存实体记录
     *
     * @param context
     */
    void saveFile(FileSaveContext context);

    /**
     * 合并物理文件并保存物理文件记录
     *
     * @param context
     */
    void mergeFileChunkAndSaveFile(FileChunkMergeAndSaveContext context);

}
