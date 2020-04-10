package com.vwmin.terminalservice;

import com.vwmin.terminalservice.entity.PostEntity;
import com.vwmin.terminalservice.entity.ReplyEntity;

/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/9 18:57
 */
public interface Reply {
    ReplyEntity call(PostEntity postEntity);
}
