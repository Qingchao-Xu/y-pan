package org.xu.pan.server.modules.share.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.xu.pan.core.exception.YPanBusinessException;
import org.xu.pan.core.utils.IdUtil;
import org.xu.pan.server.modules.share.context.SaveShareFilesContext;
import org.xu.pan.server.modules.share.entity.YPanShareFile;
import org.xu.pan.server.modules.share.service.IShareFileService;
import org.xu.pan.server.modules.share.mapper.YPanShareFileMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author 23561
* @description 针对表【y_pan_share_file(用户分享文件表)】的数据库操作Service实现
* @createDate 2024-09-05 11:02:39
*/
@Service
public class ShareFileServiceImpl extends ServiceImpl<YPanShareFileMapper, YPanShareFile>
    implements IShareFileService {
    /**
     * 保存分享的文件的对应关系
     *
     * @param context
     */
    @Override
    public void saveShareFiles(SaveShareFilesContext context) {
        Long shareId = context.getShareId();
        List<Long> shareFileIdList = context.getShareFileIdList();
        Long userId = context.getUserId();

        List<YPanShareFile> records = Lists.newArrayList();

        for (Long shareFileId : shareFileIdList) {
            YPanShareFile record = new YPanShareFile();
            record.setId(IdUtil.get());
            record.setShareId(shareId);
            record.setFileId(shareFileId);
            record.setCreateUser(userId);
            record.setCreateTime(new Date());
            records.add(record);
        }

        if (!saveBatch(records)) {
            throw new YPanBusinessException("保存文件分享关联关系失败");
        }
    }
}




