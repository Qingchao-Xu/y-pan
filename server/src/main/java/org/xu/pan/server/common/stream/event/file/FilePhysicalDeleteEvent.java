package org.xu.pan.server.common.stream.event.file;

import lombok.*;
import org.springframework.context.ApplicationEvent;
import org.xu.pan.server.modules.file.entity.YPanUserFile;

import java.io.Serializable;
import java.util.List;

/**
 * 文件被物理删除的事件实体
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class FilePhysicalDeleteEvent implements Serializable {

    private static final long serialVersionUID = 1436844402341789993L;

    /**
     * 所有被物理删除的文件实体集合
     */
    private List<YPanUserFile> allRecords;

    public FilePhysicalDeleteEvent(List<YPanUserFile> allRecords) {
        this.allRecords = allRecords;
    }

}
