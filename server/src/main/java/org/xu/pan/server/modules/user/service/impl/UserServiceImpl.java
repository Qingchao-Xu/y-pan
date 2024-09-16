package org.xu.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DuplicateKeyException;
import org.xu.pan.cache.core.constants.CacheConstants;
import org.xu.pan.core.exception.YPanBusinessException;
import org.xu.pan.core.response.ResponseCode;
import org.xu.pan.core.utils.IdUtil;
import org.xu.pan.core.utils.JwtUtil;
import org.xu.pan.core.utils.PasswordUtil;
import org.xu.pan.server.common.cache.AnnotationCacheService;
import org.xu.pan.server.modules.file.constants.FileConstants;
import org.xu.pan.server.modules.file.context.CreateFolderContext;
import org.xu.pan.server.modules.file.entity.YPanUserFile;
import org.xu.pan.server.modules.file.service.IUserFileService;
import org.xu.pan.server.modules.user.constants.UserConstants;
import org.xu.pan.server.modules.user.context.*;
import org.xu.pan.server.modules.user.converter.UserConverter;
import org.xu.pan.server.modules.user.entity.YPanUser;
import org.xu.pan.server.modules.user.service.IUserService;
import org.xu.pan.server.modules.user.mapper.YPanUserMapper;
import org.springframework.stereotype.Service;
import org.xu.pan.server.modules.user.vo.UserInfoVO;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
* @author 23561
* @description 针对表【y_pan_user(用户信息表)】的数据库操作Service实现
* @createDate 2024-09-05 10:58:27
*/
@Service(value = "userService")
public class UserServiceImpl extends ServiceImpl<YPanUserMapper, YPanUser>
    implements IUserService {

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private IUserFileService iUserFileService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    @Qualifier(value = "userAnnotationCacheService")
    private AnnotationCacheService<YPanUser> cacheService;

    /**
     * 用户注册的业务实现
     * 需要实现的功能点：
     * 1、注册用户信息
     * 2、创建新用户的根本目录信息
     * <p>
     * 需要实现的技术难点：
     * 1、该业务是幂等的
     * 2、要保证用户名全局唯一
     * <p>
     * 实现技术难点的处理方案：
     * 1、幂等性通过数据库表对于用户名字段添加唯一索引，我们上有业务捕获对应的冲突异常，转化返回
     *
     * @param userRegisterContext
     * @return
     */
    @Override
    public Long register(UserRegisterContext userRegisterContext) {
        assembleUserEntity(userRegisterContext);
        doRegister(userRegisterContext);
        createUserRootFolder(userRegisterContext);
        return userRegisterContext.getEntity().getUserId();
    }

    /**
     * 用户登录业务实现
     * <p>
     * 需要实现的功能：
     * 1、用户的登录信息校验
     * 2、生成一个具有时效性的accessToken
     * 3、将accessToken缓存起来，去实现单机登录
     *
     * @param userLoginContext
     * @return
     */
    @Override
    public String login(UserLoginContext userLoginContext) {
        checkLoginInfo(userLoginContext);
        generateAndSaveAccessToken(userLoginContext);
        return userLoginContext.getAccessToken();
    }

    /**
     * 用户退出登录
     * <p>
     * 1、清除用户的登录凭证缓存
     *
     * @param userId
     */
    @Override
    public void exit(Long userId) {
        try {
            Cache cache = cacheManager.getCache(CacheConstants.Y_PAN_CACHE_NAME);
            cache.evict(UserConstants.USER_LOGIN_PREFIX + userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new YPanBusinessException("用户退出登录失败");
        }
    }

    /**
     * 用户忘记密码-校验用户名称
     *
     * @param checkUsernameContext
     * @return
     */
    @Override
    public String checkUsername(CheckUsernameContext checkUsernameContext) {
        String question = baseMapper.selectQuestionByUsername(checkUsernameContext.getUsername());
        if (StringUtils.isBlank(question)) {
            throw new YPanBusinessException("没有此用户");
        }
        return question;
    }

    /**
     * 用户忘记密码-校验密保答案
     *
     * @param checkAnswerContext
     * @return
     */
    @Override
    public String checkAnswer(CheckAnswerContext checkAnswerContext) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", checkAnswerContext.getUsername());
        queryWrapper.eq("question", checkAnswerContext.getQuestion());
        queryWrapper.eq("answer", checkAnswerContext.getAnswer());
        int count = count(queryWrapper);

        if (count == 0) {
            throw new YPanBusinessException("密保答案错误");
        }

        return generateCheckAnswerToken(checkAnswerContext);
    }

    /**
     * 重置用户密码
     * 1、校验token是不是有效
     * 2、重置密码
     *
     * @param resetPasswordContext
     */
    @Override
    public void resetPassword(ResetPasswordContext resetPasswordContext) {
        checkForgetPasswordToken(resetPasswordContext);
        checkAndResetUserPassword(resetPasswordContext);
    }

    /**
     * 在线修改密码
     * 1、校验旧密码
     * 2、重置新密码
     * 3、退出当前的登录状态
     *
     * @param changePasswordContext
     */
    @Override
    public void changePassword(ChangePasswordContext changePasswordContext) {
        checkOldPassword(changePasswordContext);
        doChangePassword(changePasswordContext);
        exitLoginStatus(changePasswordContext);
    }

    /**
     * 查询在线用户的基本信息
     * 1、查询用户的基本信息实体
     * 2、查询用户的根文件夹信息
     * 3、拼装VO对象返回
     *
     * @param userId
     * @return
     */
    @Override
    public UserInfoVO info(Long userId) {
        YPanUser entity = getById(userId);
        if (Objects.isNull(entity)) {
            throw new YPanBusinessException("用户信息查询失败");
        }

        YPanUserFile yPanUserFile = getUserRootFileInfo(userId);
        if (Objects.isNull(yPanUserFile)) {
            throw new YPanBusinessException("查询用户根文件夹信息失败");
        }

        return userConverter.assembleUserInfoVO(entity, yPanUserFile);
    }

    @Override
    public boolean removeById(Serializable id) {
        return cacheService.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        throw new YPanBusinessException("请更换为手动缓存");
    }

    @Override
    public boolean updateById(YPanUser entity) {
        return cacheService.updateById(entity.getUserId(), entity);
    }

    @Override
    public boolean updateBatchById(Collection<YPanUser> entityList) {
        throw new YPanBusinessException("请更换为手动缓存");
    }

    @Override
    public YPanUser getById(Serializable id) {
        return cacheService.getById(id);
    }

    @Override
    public List<YPanUser> listByIds(Collection<? extends Serializable> idList) {
        throw new YPanBusinessException("请更换为手动缓存");
    }

    /****************private*****************/

    /**
     * 获取用户根文件夹信息实体
     *
     * @param userId
     * @return
     */
    private YPanUserFile getUserRootFileInfo(Long userId) {
        return iUserFileService.getUserRootFile(userId);
    }

    /**
     * 退出用户的登录状态
     *
     * @param changePasswordContext
     */
    private void exitLoginStatus(ChangePasswordContext changePasswordContext) {
        exit(changePasswordContext.getUserId());
    }

    /**
     * 修改新密码
     *
     * @param changePasswordContext
     */
    private void doChangePassword(ChangePasswordContext changePasswordContext) {
        String newPassword = changePasswordContext.getNewPassword();
        YPanUser entity = changePasswordContext.getEntity();
        String salt = entity.getSalt();

        String encNewPassword = PasswordUtil.encryptPassword(salt, newPassword);

        entity.setPassword(encNewPassword);

        if (!updateById(entity)) {
            throw new YPanBusinessException("修改用户密码失败");
        }
    }

    /**
     * 校验用户的旧密码
     * 改不周会查询并封装用户的实体信息到上下文对象中
     *
     * @param changePasswordContext
     */
    private void checkOldPassword(ChangePasswordContext changePasswordContext) {
        Long userId = changePasswordContext.getUserId();
        String oldPassword = changePasswordContext.getPassword();

        YPanUser entity = getById(userId);
        if (Objects.isNull(entity)) {
            throw new YPanBusinessException("用户信息不存在");
        }
        changePasswordContext.setEntity(entity);

        String encOldPassword = PasswordUtil.encryptPassword(entity.getSalt(), oldPassword);
        String dbOldPassword = entity.getPassword();
        if (!Objects.equals(encOldPassword, dbOldPassword)) {
            throw new YPanBusinessException("旧密码不正确");
        }
    }

    /**
     * 验证忘记密码的token是否有效
     *
     * @param resetPasswordContext
     */
    private void checkForgetPasswordToken(ResetPasswordContext resetPasswordContext) {
        String token = resetPasswordContext.getToken();
        Object value = JwtUtil.analyzeToken(token, UserConstants.FORGET_USERNAME);
        if (Objects.isNull(value)) {
            throw new YPanBusinessException(ResponseCode.TOKEN_EXPIRE);
        }
        String tokenUsername = String.valueOf(value);
        if (!Objects.equals(tokenUsername, resetPasswordContext.getUsername())) {
            throw new YPanBusinessException("token错误");
        }
    }

    /**
     * 校验用户信息并重置用户密码
     *
     * @param resetPasswordContext
     */
    private void checkAndResetUserPassword(ResetPasswordContext resetPasswordContext) {
        String username = resetPasswordContext.getUsername();
        String password = resetPasswordContext.getNewPassword();
        YPanUser entity = getYPanUserByUsername(username);
        if (Objects.isNull(entity)) {
            throw new YPanBusinessException("用户信息不存在");
        }

        String newDbPassword = PasswordUtil.encryptPassword(entity.getSalt(), password);
        entity.setPassword(newDbPassword);
        entity.setUpdateTime(new Date());

        if (!updateById(entity)) {
            throw new YPanBusinessException("重置用户密码失败");
        }
    }


    /**
     * 生成用户忘记密码-校验密保答案通过的临时token
     * token的失效时间为五分钟之后
     *
     * @param checkAnswerContext
     * @return
     */
    private String generateCheckAnswerToken(CheckAnswerContext checkAnswerContext) {
        String token = JwtUtil.generateToken(checkAnswerContext.getUsername(), UserConstants.FORGET_USERNAME, checkAnswerContext.getUsername(), UserConstants.FIVE_MINUTES_LONG);
        return token;
    }


    /**
     * 生成并保存登陆之后的凭证
     *
     * @param userLoginContext
     */
    private void generateAndSaveAccessToken(UserLoginContext userLoginContext) {
        YPanUser entity = userLoginContext.getEntity();

        String accessToken = JwtUtil.generateToken(entity.getUsername(), UserConstants.LOGIN_USER_ID, entity.getUserId(), UserConstants.ONE_DAY_LONG);

        Cache cache = cacheManager.getCache(CacheConstants.Y_PAN_CACHE_NAME);
        cache.put(UserConstants.USER_LOGIN_PREFIX + entity.getUserId(), accessToken);

        userLoginContext.setAccessToken(accessToken);
    }

    /**
     * 校验用户名密码
     *
     * @param userLoginContext
     */
    private void checkLoginInfo(UserLoginContext userLoginContext) {
        String username = userLoginContext.getUsername();
        String password = userLoginContext.getPassword();

        YPanUser entity = getYPanUserByUsername(username);
        if (Objects.isNull(entity)) {
            throw new YPanBusinessException("用户名称不存在");
        }

        String salt = entity.getSalt();
        String encPassword = PasswordUtil.encryptPassword(salt, password);
        String dbPassword = entity.getPassword();
        if (!Objects.equals(encPassword, dbPassword)) {
            throw new YPanBusinessException("密码信息不正确");
        }

        userLoginContext.setEntity(entity);
    }

    /**
     * 通过用户名称获取用户实体信息
     *
     * @param username
     * @return
     */
    private YPanUser getYPanUserByUsername(String username) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        return getOne(queryWrapper);
    }

    /**
     * 创建用户的根目录信息
     *
     * @param userRegisterContext
     */
    private void createUserRootFolder(UserRegisterContext userRegisterContext) {
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(FileConstants.TOP_PARENT_ID);
        createFolderContext.setUserId(userRegisterContext.getEntity().getUserId());
        createFolderContext.setFolderName(FileConstants.ALL_FILE_CN_STR);
        iUserFileService.createFolder(createFolderContext);
    }

    /**
     * 实现注册用户的业务
     * 需要捕获数据库的唯一索引冲突异常，来实现全局用户名称唯一
     *
     * @param userRegisterContext
     */
    private void doRegister(UserRegisterContext userRegisterContext) {
        YPanUser entity = userRegisterContext.getEntity();
        if (Objects.nonNull(entity)) {
            try {
                if (!save(entity)) {
                    throw new YPanBusinessException("用户注册失败");
                }
            } catch (DuplicateKeyException duplicateKeyException) {
                throw new YPanBusinessException("用户名已存在");
            }
            return;
        }
        throw new YPanBusinessException(ResponseCode.ERROR);
    }

    /**
     * 实体转化
     * 由上下文信息转化成用户实体，封装进上下文
     *
     * @param userRegisterContext
     */
    private void assembleUserEntity(UserRegisterContext userRegisterContext) {
        YPanUser entity = userConverter.userRegisterContext2YPanUser(userRegisterContext);
        String salt = PasswordUtil.getSalt(),
                dbPassword = PasswordUtil.encryptPassword(salt, userRegisterContext.getPassword());
        entity.setUserId(IdUtil.get());
        entity.setSalt(salt);
        entity.setPassword(dbPassword);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        userRegisterContext.setEntity(entity);
    }
}




