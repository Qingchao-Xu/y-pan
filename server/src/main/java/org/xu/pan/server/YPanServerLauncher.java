package org.xu.pan.server;

import io.swagger.annotations.Api;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xu.pan.core.constants.YPanConstants;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.xu.pan.core.response.R;

@SpringBootApplication(scanBasePackages = YPanConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = YPanConstants.BASE_COMPONENT_SCAN_PATH)
@RestController
@Api("测试接口类")
public class YPanServerLauncher {

    public static void main(String[] args) {
        SpringApplication.run(YPanServerLauncher.class);
    }

    @GetMapping("hello")
    public R<String> hello(String name) {
        return R.success("hello" + name + "!");
    }

}
