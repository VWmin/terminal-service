package com.vwmin.terminalservice;

import com.vwmin.terminalservice.entity.CQcodeEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/13 11:55
 */
public class CQcodeExtracter {
    private static final Pattern REGEX = Pattern.compile("\\[CQ:([a-zA-z]+),(.+=.+)+]");

    public static CQcodeEntity parse(String s){
        Matcher matcher = REGEX.matcher(s);
        if (matcher.find()){
            String type = matcher.group(1);
            String kvs = matcher.group(2);
            Map<String, String> kvmap = extractData(kvs);
            return new CQcodeEntity(type, kvmap);
        }
        return null;
    }

    private static Map<String, String> extractData(String kvs){
        String[] kvarr = kvs.split(",");
        HashMap<String, String> kvmap = new HashMap<>(kvarr.length);
        for (String kv : kvarr) {
            String[] split = kv.split("=");
            String key = split[0];
            String value = split[1];
            kvmap.put(key, value);
        }
        return kvmap;
    }
}
