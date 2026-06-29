package org.example.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductListDTO {

    private Long id;
    private Long merchantId;
    private String productName;
    private String productCode;
    private Long categoryId;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private String mainImage;
    private String brief;
    private Integer status;
    private Integer sort;
    private Integer isHot;
    private Integer isNew;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private List<TagDTO> tags;

    @Data
    public static class TagDTO {
        private String tagName;
        private String tagColor;
    }
}