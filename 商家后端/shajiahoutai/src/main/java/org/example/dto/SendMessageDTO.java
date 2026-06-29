package org.example.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class SendMessageDTO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "店铺ID不能为空")
    private Long shopId;

    private Long productId;

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 1000, message = "消息内容不能超过1000字符")
    private String content;

    private String messageType = "TEXT";
}
