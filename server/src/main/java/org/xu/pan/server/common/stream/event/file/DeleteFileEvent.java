package org.xu.pan.server.common.stream.event.file;

import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.List;

/**
 * 文件删除事件
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class DeleteFileEvent implements Serializable {

    private static final long serialVersionUID = -4858632894534904465L;

    private List<Long> fileIdList;

    public DeleteFileEvent(List<Long> fileIdList) {
        this.fileIdList = fileIdList;
    }

}
