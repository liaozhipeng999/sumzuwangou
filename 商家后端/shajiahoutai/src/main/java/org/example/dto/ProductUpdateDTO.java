package org.example.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductUpdateDTO {

    @NotNull(message = "商品ID不能为空")
    private Long id;

    @NotBlank(message = "商品名称不能为空")
    private String productName;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotNull(message = "商品售价不能为空")
    private BigDecimal price;

    @NotNull(message = "库存数量不能为空")
    private Integer stock;

    @NotBlank(message = "主图URL不能为空")
    private String mainImage;

    private String productCode;
    
    private BigDecimal originalPrice;
    
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