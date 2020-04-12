package com.vwmin.terminalservice.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/9 21:35
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "bot")
public class BotConfigurationProperties {
    private String cqHttpUrl;
    private String cqHome;
}
