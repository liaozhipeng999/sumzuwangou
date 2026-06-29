package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("term_merchants")
public class TermMerchant {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String merchantName;
    
    private String merchantLogo;
    
    private String merchantBrief;
    
    private String username;
    
    private String password;
    
    private String contactName;
    
    private String contactPhone;
    
    private String email;
    
    private Long mainCategoryId;
    
    private Integer merchantLevel;
    
    private Integer status;
    
    private Integer sort;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic(value = "NULL", delval = "NOW()")
    private LocalDateTime deletedAt;
}