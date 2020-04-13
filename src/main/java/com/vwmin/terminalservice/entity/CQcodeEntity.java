package com.vwmin.terminalservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/13 11:56
 */

@Getter
@Setter
@AllArgsConstructor
public class CQcodeEntity {
    private String type;
    private Map<String, String> data;
}
