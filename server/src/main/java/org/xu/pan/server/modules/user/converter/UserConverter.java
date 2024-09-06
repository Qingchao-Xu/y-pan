package org.xu.pan.server.modules.user.converter;

import org.xu.pan.server.modules.file.entity.YPanUserFile;
import org.xu.pan.server.modules.user.context.*;
import org.xu.pan.server.modules.user.entity.YPanUser;
import org.xu.pan.server.modules.user.po.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.xu.pan.server.modules.user.vo.UserInfoVO;

/**
 * 用户模块实体转化工具类
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    /**
     * UserRegisterPO转化成UserRegisterContext
     *
     * @param userRegisterPO
     * @return
     */
    UserRegisterContext userRegisterPO2UserRegisterContext(UserRegisterPO userRegisterPO);

    /**
     * UserRegisterContext转YPanUser
     *
     * @param userRegisterContext
     * @return
     */
    @Mapping(target = "password", ignore = true)
    YPanUser userRegisterContext2YPanUser(UserRegisterContext userRegisterContext);

    /**
     * UserLoginPO转UserLoginContext
     *
     * @param userLoginPO
     * @return
     */
    UserLoginContext userLoginPO2UserLoginContext(UserLoginPO userLoginPO);

    /**
     * CheckUsernamePO转CheckUsernameContext
     *
     * @param checkUsernamePO
     * @return
     */
    CheckUsernameContext checkUsernamePO2CheckUsernameContext(CheckUsernamePO checkUsernamePO);

    /**
     * CheckAnswerPO转CheckAnswerContext
     *
     * @param checkAnswerPO
     * @return
     */
    CheckAnswerContext checkAnswerPO2CheckAnswerContext(CheckAnswerPO checkAnswerPO);

    /**
     * ResetPasswordPO转ResetPasswordContext
     *
     * @param resetPasswordPO
     * @return
     */
    ResetPasswordContext resetPasswordPO2ResetPasswordContext(ResetPasswordPO resetPasswordPO);

    /**
     * ChangePasswordPO转ChangePasswordContext
     *
     * @param changePasswordPO
     * @return
     */
    ChangePasswordContext changePasswordPO2ChangePasswordContext(ChangePasswordPO changePasswordPO);

    /**
     * 拼装用户基本信息返回实体
     *
     * @param yPanUser
     * @param yPanUserFile
     * @return
     */
    @Mapping(source = "yPanUser.username", target = "username")
    @Mapping(source = "yPanUserFile.fileId", target = "rootFileId")
    @Mapping(source = "yPanUserFile.filename", target = "rootFilename")
    UserInfoVO assembleUserInfoVO(YPanUser yPanUser, YPanUserFile yPanUserFile);

}
