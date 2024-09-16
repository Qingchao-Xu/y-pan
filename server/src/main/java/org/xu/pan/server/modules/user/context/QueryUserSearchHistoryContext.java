package org.xu.pan.server.modules.user.context;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户搜索历史记录上下文实体
 */
@Data
public class QueryUserSearchHistoryContext implements Serializable {
    private static final long serialVersionUID = 2623670623597783034L;

    /**
     * 当前用户id
     */
    private Long userId;

}
