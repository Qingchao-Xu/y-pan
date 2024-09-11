package org.xu.pan.server.modules.share.context;

import lombok.Data;
import org.xu.pan.server.modules.share.entity.YPanShare;
import org.xu.pan.server.modules.share.vo.ShareDetailVO;

import java.io.Serializable;

/**
 * 查询分享详情的上下文实体对象
 */
@Data
public class QueryShareDetailContext implements Serializable {

    /**
     * 对应的分享ID
     */
    private Long shareId;

    /**
     * 分享实体
     */
    private YPanShare record;

    /**
     * 分享详情的VO对象
     */
    private ShareDetailVO vo;

}
