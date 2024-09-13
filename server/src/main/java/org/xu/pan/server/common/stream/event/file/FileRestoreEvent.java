package org.xu.pan.server.common.stream.event.file;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.List;

/**
 * 文件还原事件实体
 */
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class FileRestoreEvent implements Serializable {

    private static final long serialVersionUID = 5773261342109653385L;

    /**
     * 被成功还原的文件记录ID集合
     */
    private List<Long> fileIdList;

    public FileRestoreEvent(List<Long> fileIdList) {
        this.fileIdList = fileIdList;
    }

}
