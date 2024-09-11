package org.xu.pan.server.modules.share.service;

import org.xu.pan.server.modules.share.context.SaveShareFilesContext;
import org.xu.pan.server.modules.share.entity.YPanShareFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 23561
* @description 针对表【y_pan_share_file(用户分享文件表)】的数据库操作Service
* @createDate 2024-09-05 11:02:39
*/
public interface IShareFileService extends IService<YPanShareFile> {
    /**
     * 保存分享的文件的对应关系
     *
     * @param context
     */
    void saveShareFiles(SaveShareFilesContext context);
}
