package com.vwmin.terminalservice;

import com.vwmin.restproxy.annotations.Body;
import com.vwmin.restproxy.annotations.POST;
import com.vwmin.terminalservice.entity.RetEntity;
import com.vwmin.terminalservice.entity.SendEntity;


/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/8 11:15
 */
public interface CQClientApi {
    @POST("/send_private_msg")
    RetEntity sendPrivateMsg(@Body SendEntity.PrivateSendEntity send);

    @POST("/send_group_msg")
    RetEntity sendGroupMsg(@Body SendEntity.GroupSendEntity send);

    @POST("/send_discuss_msg")
    RetEntity sendDiscussMsg(@Body SendEntity.DiscussSendEntity send);
}
