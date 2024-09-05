package org.xu.pan.server.modules.user;

import cn.hutool.core.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.xu.pan.core.exception.YPanBusinessException;
import org.xu.pan.server.YPanServerLauncher;
import org.xu.pan.server.modules.user.context.*;
import org.xu.pan.server.modules.user.service.IUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户模块单元测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = YPanServerLauncher.class)
@Transactional
public class UserTest {

    @Autowired
    private IUserService iUserService;

    /**
     * 测试成功注册用户信息
     */
    @Test
    public void testRegisterUser() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = iUserService.register(context);
        Assert.isTrue(register.longValue() > 0L);
    }

    /**
     * 测试重复用户名称注册幂等
     */
    @Test(expected = YPanBusinessException.class)
    public void testRegisterDuplicateUsername() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = iUserService.register(context);
        Assert.isTrue(register.longValue() > 0L);
        iUserService.register(context);
    }

    /**
     * 测试登陆成功
     */
    @Test
    public void loginSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = iUserService.register(context);
        Assert.isTrue(register.longValue() > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        String accessToken = iUserService.login(userLoginContext);

        Assert.isTrue(StringUtils.isNotBlank(accessToken));
    }

    /**
     * 测试登录失败：用户名不正确
     */
    @Test(expected = YPanBusinessException.class)
    public void wrongUsername() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = iUserService.register(context);
        Assert.isTrue(register.longValue() > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        userLoginContext.setUsername(userLoginContext.getUsername() + "_change");
        iUserService.login(userLoginContext);
    }

    /**
     * 测试登录失败：密码不正确
     */
    @Test(expected = YPanBusinessException.class)
    public void wrongPassword() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = iUserService.register(context);
        Assert.isTrue(register.longValue() > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        userLoginContext.setPassword(userLoginContext.getPassword() + "_change");
        iUserService.login(userLoginContext);
    }


    private final static String USERNAME = "stan";
    private final static String PASSWORD = "123456";
    private final static String QUESTION = "question";
    private final static String ANSWER = "answer";

    /**
     * 构建注册用户上下文信息
     *
     * @return
     */
    private UserRegisterContext createUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        context.setQuestion(QUESTION);
        context.setAnswer(ANSWER);
        return context;
    }

    /**
     * 构建用户登录上下文实体
     *
     * @return
     */
    private UserLoginContext createUserLoginContext() {
        UserLoginContext userLoginContext = new UserLoginContext();
        userLoginContext.setUsername(USERNAME);
        userLoginContext.setPassword(PASSWORD);
        return userLoginContext;
    }



}

