package com.vwmin.terminalservice;

import com.vwmin.terminalservice.entity.MessageSegment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/9 12:12
 */
public class MessageSegmentBuilder {
    private final List<MessageSegment> segments;

    public MessageSegmentBuilder(){
        this.segments = new ArrayList<>();
    }

    /**
     * 发送纯文本消息
     * @param text 要发送的文本
     * @return builder实例自身
     */
    public MessageSegmentBuilder plainText(String text){
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
    public MessageSegmentBuilder at(Long userId){
        MessageSegment segment = new MessageSegment();
        segment.setType("at");
        segment.addData("qq", userId);

        segments.add(segment);

        return this;
    }

    /**
     * at 全体成员
     * @return 实例自身
     */
    public MessageSegmentBuilder atAll(){
        MessageSegment segment = new MessageSegment();
        segment.setType("at");
        segment.addData("qq", "all");

        segments.add(segment);

        return this;
    }


    public MessageSegmentBuilder image(String file, String url){
        MessageSegment segment = new MessageSegment();
        segment.setType("image");
        segment.addData("file", file);

        try{
            if (!ImageUtils.isExist(file)){
                ImageUtils.downloadImage(file, url);
            }
        }catch (IOException e){
            e.printStackTrace();
            return this;
        }

        segments.add(segment);
        return this;
    }


    public List<MessageSegment> build(){
        return segments;
    }
}
