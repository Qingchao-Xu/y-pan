package org.xu.pan.server.common.stream.event.search;

import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

/**
 * 用户搜索事件
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class UserSearchEvent implements Serializable {

    private static final long serialVersionUID = 6452960983395644651L;

    private String keyword;

    private Long userId;

    public UserSearchEvent(String keyword, Long userId) {
        this.keyword = keyword;
        this.userId = userId;
    }

}
