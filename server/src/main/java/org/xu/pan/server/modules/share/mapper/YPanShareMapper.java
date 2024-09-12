package org.xu.pan.server.modules.share.mapper;

import org.apache.ibatis.annotations.Param;
import org.xu.pan.server.modules.share.entity.YPanShare;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.xu.pan.server.modules.share.vo.YPanShareUrlListVO;

import java.util.List;

/**
* @author 23561
* @description 针对表【y_pan_share(用户分享表)】的数据库操作Mapper
* @createDate 2024-09-05 11:02:39
* @Entity org.xu.pan.server.modules.share.entity.YPanShare
*/
public interface YPanShareMapper extends BaseMapper<YPanShare> {

    /**
     * 查询用户的分享列表
     * @param userId
     * @return
     */
    List<YPanShareUrlListVO> selectShareVOListByUserId(@Param("userId") Long userId);

    /**
     * 滚动查询已存在的分享ID
     * @param startId
     * @param limit
     * @return
     */
    List<Long> rollingQueryShareId(@Param("startId") long startId, @Param("limit") long limit);
}




