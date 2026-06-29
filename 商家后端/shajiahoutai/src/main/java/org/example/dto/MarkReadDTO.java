package org.example.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class MarkReadDTO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "店铺ID不能为空")
    private Long shopId;
}
