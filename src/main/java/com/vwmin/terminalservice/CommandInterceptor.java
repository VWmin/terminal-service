package com.vwmin.terminalservice;

import com.vwmin.terminalservice.entity.PostEntity;

/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/13 10:41
 */
public interface CommandInterceptor {
    /**
     * 如果返回值为true，在从原字符串拆分命令前，原字符串将会通过handle函数
     * @param post 上报内容
     * @return 是否拦截
     */
    boolean isMatch(PostEntity post);

    /**
     * 对原串进行预处理
     * @param raw 接收到的字符串内容
     * @return 处理后的字符串内容
     */
    default String handle(String raw){
        return raw;
    }

}
