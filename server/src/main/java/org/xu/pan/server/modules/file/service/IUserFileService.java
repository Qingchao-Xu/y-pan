package org.xu.pan.server.modules.file.service;

import org.xu.pan.server.modules.file.context.CreateFolderContext;
import org.xu.pan.server.modules.file.context.QueryFileListContext;
import org.xu.pan.server.modules.file.context.UpdateFilenameContext;
import org.xu.pan.server.modules.file.entity.YPanUserFile;
import com.baomidou.mybatisplus.extension.service.IService;
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

}
