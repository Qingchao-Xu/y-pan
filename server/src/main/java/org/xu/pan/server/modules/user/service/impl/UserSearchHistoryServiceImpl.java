package org.xu.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.xu.pan.server.modules.user.context.QueryUserSearchHistoryContext;
import org.xu.pan.server.modules.user.entity.YPanUserSearchHistory;
import org.xu.pan.server.modules.user.service.IUserSearchHistoryService;
import org.xu.pan.server.modules.user.mapper.YPanUserSearchHistoryMapper;
import org.springframework.stereotype.Service;
import org.xu.pan.server.modules.user.vo.UserSearchHistoryVO;

import java.util.List;

/**
* @author 23561
* @description 针对表【y_pan_user_search_history(用户搜索历史表)】的数据库操作Service实现
* @createDate 2024-09-05 10:58:27
*/
@Service(value = "userSearchHistoryService")
public class UserSearchHistoryServiceImpl extends ServiceImpl<YPanUserSearchHistoryMapper, YPanUserSearchHistory>
    implements IUserSearchHistoryService {

    /**
     * 获取用户最新的搜索历史记录，默认10条
     *
     * @param context
     * @return
     */
    @Override
    public List<UserSearchHistoryVO> getUserSearchHistories(QueryUserSearchHistoryContext context) {
        return baseMapper.selectUserSearchHistories(context);
    }
}




