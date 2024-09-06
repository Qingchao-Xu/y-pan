package org.xu.pan.server.modules.user.service;

import org.xu.pan.server.modules.user.context.*;
import org.xu.pan.server.modules.user.entity.YPanUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 23561
* @description 针对表【y_pan_user(用户信息表)】的数据库操作Service
* @createDate 2024-09-05 10:58:27
*/
public interface IUserService extends IService<YPanUser> {

    /**
     * 用户注册业务
     * @param userRegisterContext
     * @return
     */
    Long register(UserRegisterContext userRegisterContext);

    /**
     * 用户登录业务
     *
     * @param userLoginContext
     * @return
     */
    String login(UserLoginContext userLoginContext);

    /**
     * 用户退出登录
     *
     * @param userId
     */
    void exit(Long userId);

    /**
     * 用户忘记密码-校验用户名
     *
     * @param checkUsernameContext
     * @return
     */
    String checkUsername(CheckUsernameContext checkUsernameContext);

    /**
     * 用户忘记密码-校验密保答案
     *
     * @param checkAnswerContext
     * @return
     */
    String checkAnswer(CheckAnswerContext checkAnswerContext);

    /**
     * 重置用户密码
     *
     * @param resetPasswordContext
     */
    void resetPassword(ResetPasswordContext resetPasswordContext);
}
