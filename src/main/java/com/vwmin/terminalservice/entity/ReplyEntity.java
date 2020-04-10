package com.vwmin.terminalservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/9 12:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyEntity {
    private List<MessageSegment> reply;
}
