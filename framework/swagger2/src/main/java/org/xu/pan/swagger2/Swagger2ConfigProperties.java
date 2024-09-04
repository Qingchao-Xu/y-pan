package org.xu.pan.swagger2;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.xu.pan.core.constants.YPanConstants;

/**
 * swagger2配置属性实体
 */
@Data
@Component
@ConfigurationProperties(prefix = "swagger2")
public class Swagger2ConfigProperties {

    private boolean show = true;

    private String groupName = "y-pan";

    private String basePackage = YPanConstants.BASE_COMPONENT_SCAN_PATH;

    private String title = "y-pan-server";

    private String description = "y-pan-server";

    private String termsOfServiceUrl = "http://127.0.0.1:${server.port}";

    private String contactName = "stan";

    private String contactUrl = "https://github.com/Qingchao-Xu";

    private String contactEmail = "15154739019@163.com";

    private String version = "1.0";

}
