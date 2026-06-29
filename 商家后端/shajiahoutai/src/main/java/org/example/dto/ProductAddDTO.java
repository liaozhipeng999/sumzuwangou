package org.example.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductAddDTO {
    
    @NotNull(message = "商家ID不能为空")
    private Long merchantId;
    
    @NotBlank(message = "商品名称不能为空")
    private String productName;
    
    private String productCode;
    
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    @NotNull(message = "商品售价不能为空")
    @Min(value = 0, message = "商品售价不能为负数")
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能为负数")
    private Integer stock;
    
    @NotBlank(message = "主图URL不能为空")
    private String mainImage;
    
    private String brief;
    
    private Integer status;
    
    private Integer sort;
    
    private Integer isHot;
    
    private Integer isNew;
    
    private List<TagDTO> tags;
    
    @Data
    public static class TagDTO {
        private String tagName;
        private String tagColor;
    }
}