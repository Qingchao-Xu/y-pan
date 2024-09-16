package org.xu.pan.server.common.stream.event.log;

import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

/**
 * 错误日志事件
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class ErrorLogEvent implements Serializable {

    private static final long serialVersionUID = 6850677082983784404L;

    /**
     * 错误日志的内容
     */
    private String errorMsg;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    public ErrorLogEvent(String errorMsg, Long userId) {
        this.errorMsg = errorMsg;
        this.userId = userId;
    }

}
