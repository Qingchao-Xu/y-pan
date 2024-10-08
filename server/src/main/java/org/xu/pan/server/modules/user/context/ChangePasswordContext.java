package org.xu.pan.server.modules.user.context;

import lombok.Data;
import org.xu.pan.server.modules.user.entity.YPanUser;

import java.io.Serializable;

/**
 * 用户在线修改密码上下文信息实体
 */
@Data
public class ChangePasswordContext implements Serializable {

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 旧密码
     */
    private String password;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 当前登录用户的实体信息
     */
    private YPanUser entity;

}
