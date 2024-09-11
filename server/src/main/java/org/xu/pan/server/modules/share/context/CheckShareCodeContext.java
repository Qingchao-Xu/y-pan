package org.xu.pan.server.modules.share.context;

import lombok.Data;
import org.xu.pan.server.modules.share.entity.YPanShare;

import java.io.Serializable;

/**
 * 校验分享码上下文实体对象
 */
@Data
public class CheckShareCodeContext implements Serializable {

    private static final long serialVersionUID = -5492075515460473471L;

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 分享码
     */
    private String shareCode;

    /**
     * 对应的分享实体
     */
    private YPanShare record;

}
