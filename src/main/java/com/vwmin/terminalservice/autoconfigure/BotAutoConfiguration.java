package com.vwmin.terminalservice.autoconfigure;

import com.vwmin.restproxy.RestProxy;
import com.vwmin.terminalservice.CQClientApi;
import com.vwmin.terminalservice.ImageUtils;
import com.vwmin.terminalservice.PostController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/10 11:49
 */
@Configuration
@EnableConfigurationProperties(BotConfigurationProperties.class)
public class BotAutoConfiguration {
    private final BotConfigurationProperties properties;

    public BotAutoConfiguration(BotConfigurationProperties properties){
        this.properties = properties;

        String cqHome = properties.getCqHome();
        if (cqHome == null || cqHome.length() == 0){
            throw new IllegalArgumentException("请务必完成配置`bot.cqHome`（酷Q的安装目录）");
        }
        ImageUtils.setImageHome(cqHome+"/data/image/");
    }

    @Bean
    @ConditionalOnMissingBean(CQClientApi.class)
    public CQClientApi cqClientApi(){
        return new RestProxy<>(properties.getCqHttpUrl(), CQClientApi.class, new RestTemplate()).getApi();
    }

    @Bean
    public PostController postController(ApplicationContext context, CQClientApi cqClientApi){
        return new PostController(context, cqClientApi);
    }
}
