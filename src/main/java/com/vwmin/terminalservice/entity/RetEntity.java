package com.vwmin.terminalservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/10 10:59
 */
@NoArgsConstructor
@Data
public class RetEntity {

    /**
     * data : {"message_id":44}
     * retcode : 0
     * status : ok
     */

    private DataBean data;
    private int retcode;
    private String status;

    @NoArgsConstructor
    @Data
    public static class DataBean {
        /**
         * message_id : 44
         */

        private int message_id;
    }
}
