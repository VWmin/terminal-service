package com.vwmin.terminalservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/9 21:40
 */
@Data
public class SendEntity {
    protected List<MessageSegment> message;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static final class DiscussSendEntity extends SendEntity{
        @JsonProperty("discuss_id")
        private Long discussId;

        public DiscussSendEntity(Long sourceId, List<MessageSegment> message) {
            this.message = message;
            this.discussId = sourceId;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static final class GroupSendEntity extends SendEntity {
        @JsonProperty("group_id")
        private Long groupId;

        public GroupSendEntity(Long sourceId, List<MessageSegment> message) {
            this.message = message;
            this.groupId = sourceId;
        }
    }


    @EqualsAndHashCode(callSuper = true)
    @Data
    public static final class PrivateSendEntity extends SendEntity{
        @JsonProperty("user_id")
        private Long userId;

        public PrivateSendEntity(Long sourceId, List<MessageSegment> message) {
            this.message = message;
            this.userId = sourceId;
        }
    }

}
