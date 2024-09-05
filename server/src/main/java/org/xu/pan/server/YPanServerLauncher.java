package org.xu.pan.server;

import io.swagger.annotations.Api;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xu.pan.core.constants.YPanConstants;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.xu.pan.core.response.R;

import javax.validation.constraints.NotBlank;

@SpringBootApplication(scanBasePackages = YPanConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = YPanConstants.BASE_COMPONENT_SCAN_PATH)
@RestController
@Api("测试接口类")
@Validated
@EnableTransactionManagement
@MapperScan(basePackages = YPanConstants.BASE_COMPONENT_SCAN_PATH + ".server.modules.**.mapper")
public class YPanServerLauncher {

    public static void main(String[] args) {
        SpringApplication.run(YPanServerLauncher.class);
    }

    @GetMapping("hello")
    public R<String> hello(@NotBlank(message = "name不能为空") String name) {
        System.out.println(Thread.currentThread().getContextClassLoader());
        return R.success("hello" + name + "!");
    }

}
