package org.xu.pan.server.modules.share.context;

import lombok.Data;
import org.xu.pan.server.modules.share.entity.YPanShare;
import org.xu.pan.server.modules.share.vo.ShareSimpleDetailVO;

import java.io.Serializable;

/**
 * 查询分享简单详情上下文实体信息
 */
@Data
public class QueryShareSimpleDetailContext implements Serializable {

    /**
     * 分享的ID
     */
    private Long shareId;

    /**
     * 分享对应的实体信息
     */
    private YPanShare record;

    /**
     * 简单分享详情的VO对象
     */
    private ShareSimpleDetailVO vo;

}
