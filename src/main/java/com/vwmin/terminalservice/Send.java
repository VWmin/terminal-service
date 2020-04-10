package com.vwmin.terminalservice;

import com.vwmin.terminalservice.entity.PostEntity;

/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/10 10:42
 */
public interface Send {
    void call(CQClientApi cqClientApi, PostEntity postEntity);
}
