package org.xu.pan.server.modules.share.service;

import org.xu.pan.server.modules.file.vo.YPanUserFileVO;
import org.xu.pan.server.modules.share.context.*;
import org.xu.pan.server.modules.share.entity.YPanShare;
import com.baomidou.mybatisplus.extension.service.IService;
import org.xu.pan.server.modules.share.vo.*;

import java.util.List;

/**
* @author 23561
* @description 针对表【y_pan_share(用户分享表)】的数据库操作Service
* @createDate 2024-09-05 11:02:39
*/
public interface IShareService extends IService<YPanShare> {

    /**
     * 创建分享链接
     *
     * @param context
     * @return
     */
    YPanShareUrlVO create(CreateShareUrlContext context);

    /**
     * 查询用户的分享列表
     *
     * @param context
     * @return
     */
    List<YPanShareUrlListVO> getShares(QueryShareListContext context);

    /**
     * 取消分享链接
     *
     * @param context
     */
    void cancelShare(CancelShareContext context);

    /**
     * 校验分享码
     *
     * @param context
     * @return
     */
    String checkShareCode(CheckShareCodeContext context);

    /**
     * 查询分享的详情
     *
     * @param context
     * @return
     */
    ShareDetailVO detail(QueryShareDetailContext context);

    /**
     * 查询分享的简单详情
     *
     * @param context
     * @return
     */
    ShareSimpleDetailVO simpleDetail(QueryShareSimpleDetailContext context);

    /**
     * 获取下一级的文件列表
     *
     * @param context
     * @return
     */
    List<YPanUserFileVO> fileList(QueryChildFileListContext context);

    /**
     * 转存至我的网盘
     *
     * @param context
     */
    void saveFiles(ShareSaveContext context);

    /**
     * 分享的文件下载
     *
     * @param context
     */
    void download(ShareFileDownloadContext context);

    /**
     * 刷新受影响的对应的分享的状态
     *
     * @param allAvailableFileIdList
     */
    void refreshShareStatus(List<Long> allAvailableFileIdList);
}
