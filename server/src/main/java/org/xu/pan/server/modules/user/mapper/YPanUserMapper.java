package org.xu.pan.server.modules.user.mapper;

import org.apache.ibatis.annotations.Param;
import org.xu.pan.server.modules.user.entity.YPanUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 23561
* @description 针对表【y_pan_user(用户信息表)】的数据库操作Mapper
* @createDate 2024-09-05 10:58:27
* @Entity org.xu.pan.server.modules.user.entity.YPanUser
*/
public interface YPanUserMapper extends BaseMapper<YPanUser> {

    /**
     * 通过用户名称查询用户设置的密保问题
     * @param username
     * @return
     */
    String selectQuestionByUsername(@Param("username") String username);
}




