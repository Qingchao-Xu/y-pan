package org.xu.pan.server.modules.share.context;

import lombok.Data;
import org.xu.pan.server.modules.share.entity.YPanShare;

import java.io.Serializable;

/**
 * 查询下一级文件列表的上下文实体信息
 */
@Data
public class QueryChildFileListContext implements Serializable {

    private static final long serialVersionUID = 884255624221527918L;

    /**
     * 分享的ID
     */
    private Long shareId;

    /**
     * 父文件夹的ID
     */
    private Long parentId;

    /**
     * 分享对应的实体信息
     */
    private YPanShare record;

}
