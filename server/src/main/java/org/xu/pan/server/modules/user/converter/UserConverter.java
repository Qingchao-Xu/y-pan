package org.xu.pan.server.modules.user.converter;

import org.xu.pan.server.modules.user.context.*;
import org.xu.pan.server.modules.user.entity.YPanUser;
import org.xu.pan.server.modules.user.po.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

}
