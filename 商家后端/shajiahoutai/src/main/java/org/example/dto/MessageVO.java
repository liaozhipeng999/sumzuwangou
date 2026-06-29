package org.example.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageVO {

    private Long messageId;
    private Long senderId;
    private String senderType;
    private String content;
    private LocalDateTime sendTime;
    private String messageType;
}
