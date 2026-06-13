package com.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("term_products")
public class TermProducts {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("merchant_id")
    private Long merchantId;

    @TableField("product_name")
    private String productName;

    @TableField("product_code")
    private String productCode;

    @TableField("category_id")
    private Long categoryId;

    private BigDecimal price;

    @TableField("original_price")
    private BigDecimal originalPrice;

    private Integer stock;

    private Integer sales;

    @TableField("main_image")
    private String mainImage;

    private String brief;

    private Integer status;

    private Integer sort;

    @TableField("is_hot")
    private Integer isHot;

    @TableField("is_new")
    private Integer isNew;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("deleted_at")
    private LocalDateTime deletedAt;
}