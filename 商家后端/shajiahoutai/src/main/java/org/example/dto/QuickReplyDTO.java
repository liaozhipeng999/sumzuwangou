package org.example.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class QuickReplyDTO {

    @NotNull(message = "店铺ID不能为空")
    private Long shopId;

    @NotBlank(message = "回复内容不能为空")
    @Size(max = 500, message = "回复内容不能超过500字符")
    private String content;

    private Integer sort = 0;
}
