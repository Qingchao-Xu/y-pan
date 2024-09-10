package org.xu.pan.server.modules.file.service;

import org.xu.pan.server.modules.file.context.*;
import org.xu.pan.server.modules.file.entity.YPanUserFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.xu.pan.server.modules.file.vo.FileChunkUploadVO;
import org.xu.pan.server.modules.file.vo.UploadedChunksVO;
import org.xu.pan.server.modules.file.vo.YPanUserFileVO;

import java.util.List;

/**
* @author 23561
* @description 针对表【y_pan_user_file(用户文件信息表)】的数据库操作Service
* @createDate 2024-09-05 11:01:08
*/
public interface IUserFileService extends IService<YPanUserFile> {

    /**
     * 创建文件夹信息
     * @param createFolderContext
     * @return
     */
    Long createFolder(CreateFolderContext createFolderContext);

    /**
     * 查询用户的根文件夹信息
     *
     * @param userId
     * @return
     */
    YPanUserFile getUserRootFile(Long userId);

    /**
     * 查询用户的文件列表
     *
     * @param context
     * @return
     */
    List<YPanUserFileVO> getFileList(QueryFileListContext context);

    /**
     * 更新文件名称
     *
     * @param context
     */
    void updateFilename(UpdateFilenameContext context);

    /**
     * 批量删除用户文件
     *
     * @param context
     */
    void deleteFile(DeleteFileContext context);

    /**
     * 文件秒传功能
     *
     * @param context
     * @return
     */
    boolean secUpload(SecUploadFileContext context);

    /**
     * 单文件上传
     *
     * @param context
     */
    void upload(FileUploadContext context);

    /**
     * 文件分片上传
     *
     * @param context
     * @return
     */
    FileChunkUploadVO chunkUpload(FileChunkUploadContext context);

    /**
     * 查询用户已上传的分片列表
     *
     * @param context
     * @return
     */
    UploadedChunksVO getUploadedChunks(QueryUploadedChunksContext context);

}
