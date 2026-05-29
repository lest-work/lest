package com.lest.common.swagger.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger 配置属性
 *
 * @author yshan2028
 */
@Data
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {

    /** 是否启用 Swagger */
    private boolean enabled = true;

    /** API 文档标题 */
    private String title = "LEST Platform API";

    /** API 文档描述 */
    private String description = "LEST Platform API Documentation";

    /** API 版本 */
    private String version = "1.0.0";

    /** API 作者信息 */
    private String contactName = "LEST Platform Team";
    private String contactEmail = "lest@example.com";
    private String contactUrl = "";

    /** 许可证 */
    private String licenseName = "Apache 2.0";
    private String licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0";

    /** 全局忽略参数（用于过滤不想暴露的参数） */
    private List<String> ignoreParameters = new ArrayList<>();
}
