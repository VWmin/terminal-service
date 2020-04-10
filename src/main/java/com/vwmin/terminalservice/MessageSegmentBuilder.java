package com.vwmin.terminalservice;

import com.vwmin.terminalservice.entity.MessageSegment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/9 12:12
 */
public class MessageSegmentBuilder {
    private List<MessageSegment> segments;

    public MessageSegmentBuilder(){
        this.segments = new ArrayList<>();
    }

    /**
     * 发送纯文本消息
     * @param text 要发送的文本
     * @return builder实例自身
     */
    public MessageSegmentBuilder addTextSegment(String text){
        MessageSegment segment = new MessageSegment();
        segment.setType("text");
        segment.addData("text", text);
        segments.add(segment);
        return this;
    }

    /**
     * at某个QQ
     * @param userId QQ号
     * @return 实例自身
     */
    public MessageSegmentBuilder addAtSegment(Long userId){
        MessageSegment segment = new MessageSegment();
        segment.setType("at");
        segment.addData("qq", userId);

        segments.add(segment);

        return this;
    }

    public List<MessageSegment> build(){
        return segments;
    }
}
