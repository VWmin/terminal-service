package com.vwmin.terminalservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/9 11:27
 */
@NoArgsConstructor
@Data
public class PostEntity {

    /**
     * font : 56296416
     * message : rank day
     * message_id : 1
     * message_type : private
     * post_type : message
     * raw_message : rank day
     * self_id : 3456206672
     * sender : {"age":0,"nickname":"废怯","sex":"unknown","user_id":1903215898}
     * sub_type : friend
     * time : 1586402768
     * user_id : 1903215898
     */

    private int font;
    private String message;
    private int message_id;
    private String message_type;
    private String post_type;
    private String raw_message;
    private long self_id;
    private SenderBean sender;
    private String sub_type;
    private int time;
    private long user_id;

    @NoArgsConstructor
    @Data
    public static class SenderBean {
        /**
         * age : 0
         * nickname : 废怯
         * sex : unknown
         * user_id : 1903215898
         */

        private int age;
        private String nickname;
        private String sex;
        private long user_id;
    }
}
