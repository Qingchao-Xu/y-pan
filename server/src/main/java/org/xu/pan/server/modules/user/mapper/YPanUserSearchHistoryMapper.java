package org.xu.pan.server.modules.user.mapper;

import org.apache.ibatis.annotations.Param;
import org.xu.pan.server.modules.user.context.QueryUserSearchHistoryContext;
import org.xu.pan.server.modules.user.entity.YPanUserSearchHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.xu.pan.server.modules.user.vo.UserSearchHistoryVO;

import java.util.List;

/**
* @author 23561
* @description 针对表【y_pan_user_search_history(用户搜索历史表)】的数据库操作Mapper
* @createDate 2024-09-05 10:58:27
* @Entity org.xu.pan.server.modules.user.entity.YPanUserSearchHistory
*/
public interface YPanUserSearchHistoryMapper extends BaseMapper<YPanUserSearchHistory> {

    /**
     * 获取用户最新的搜索历史记录，默认10条
     * @param context
     * @return
     */
    List<UserSearchHistoryVO> selectUserSearchHistories(@Param("param") QueryUserSearchHistoryContext context);
}




