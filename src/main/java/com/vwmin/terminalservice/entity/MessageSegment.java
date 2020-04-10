package com.vwmin.terminalservice.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/9 12:12
 */
@Data
public class MessageSegment {
    private String type;
    private Map<String, Object> data;

    public MessageSegment(){
        this.data = new HashMap<>();
    }

    public void addData(String key, Object val){
        data.put(key, val);
    }

}
