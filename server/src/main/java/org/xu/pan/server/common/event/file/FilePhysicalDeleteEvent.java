package org.xu.pan.server.common.event.file;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;
import org.xu.pan.server.modules.file.entity.YPanUserFile;

import java.util.List;

/**
 * 文件被物理删除的事件实体
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class FilePhysicalDeleteEvent extends ApplicationEvent {

    /**
     * 所有被物理删除的文件实体集合
     */
    private List<YPanUserFile> allRecords;

    public FilePhysicalDeleteEvent(Object source, List<YPanUserFile> allRecords) {
        super(source);
        this.allRecords = allRecords;
    }

}
