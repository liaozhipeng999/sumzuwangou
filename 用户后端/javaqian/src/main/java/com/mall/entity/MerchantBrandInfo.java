package com.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("merchant_brand_info")
public class MerchantBrandInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("merchant_id")
    private Long merchantId;

    @TableField("brand_name")
    private String brandName;

    @TableField("brand_logo")
    private String brandLogo;

    @TableField("brand_slogan")
    private String brandSlogan;

    @TableField("brand_story")
    private String brandStory;

    @TableField("brand_intro")
    private String brandIntro;

    @TableField("established_year")
    private Integer establishedYear;

    @TableField("total_sales")
    private String totalSales;

    @TableField("good_review_count")
    private String goodReviewCount;

    @TableField("recent_review_count")
    private String recentReviewCount;

    @TableField("recent_group_count")
    private String recentGroupCount;

    @TableField("reviewer_count")
    private String reviewerCount;

    @TableField("guarantee_tags")
    private String guaranteeTags;

    @TableField("shop_tags")
    private String shopTags;

    @TableField("material_desc")
    private String materialDesc;

    @TableField("performance_desc")
    private String performanceDesc;

    @TableField("rd_desc")
    private String rdDesc;

    @TableField("status")
    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }

    public String getBrandSlogan() {
        return brandSlogan;
    }

    public void setBrandSlogan(String brandSlogan) {
        this.brandSlogan = brandSlogan;
    }

    public String getBrandStory() {
        return brandStory;
    }

    public void setBrandStory(String brandStory) {
        this.brandStory = brandStory;
    }

    public String getBrandIntro() {
        return brandIntro;
    }

    public void setBrandIntro(String brandIntro) {
        this.brandIntro = brandIntro;
    }

    public Integer getEstablishedYear() {
        return establishedYear;
    }

    public void setEstablishedYear(Integer establishedYear) {
        this.establishedYear = establishedYear;
    }

    public String getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(String totalSales) {
        this.totalSales = totalSales;
    }

    public String getGoodReviewCount() {
        return goodReviewCount;
    }

    public void setGoodReviewCount(String goodReviewCount) {
        this.goodReviewCount = goodReviewCount;
    }

    public String getRecentReviewCount() {
        return recentReviewCount;
    }

    public void setRecentReviewCount(String recentReviewCount) {
        this.recentReviewCount = recentReviewCount;
    }

    public String getRecentGroupCount() {
        return recentGroupCount;
    }

    public void setRecentGroupCount(String recentGroupCount) {
        this.recentGroupCount = recentGroupCount;
    }

    public String getReviewerCount() {
        return reviewerCount;
    }

    public void setReviewerCount(String reviewerCount) {
        this.reviewerCount = reviewerCount;
    }

    public String getGuaranteeTags() {
        return guaranteeTags;
    }

    public void setGuaranteeTags(String guaranteeTags) {
        this.guaranteeTags = guaranteeTags;
    }

    public String getShopTags() {
        return shopTags;
    }

    public void setShopTags(String shopTags) {
        this.shopTags = shopTags;
    }

    public String getMaterialDesc() {
        return materialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        this.materialDesc = materialDesc;
    }

    public String getPerformanceDesc() {
        return performanceDesc;
    }

    public void setPerformanceDesc(String performanceDesc) {
        this.performanceDesc = performanceDesc;
    }

    public String getRdDesc() {
        return rdDesc;
    }

    public void setRdDesc(String rdDesc) {
        this.rdDesc = rdDesc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
