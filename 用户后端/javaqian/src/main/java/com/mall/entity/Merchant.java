package com.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("term_merchants")
public class Merchant {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("merchant_name")
    private String merchantName;

    @TableField("merchant_logo")
    private String merchantLogo;

    @TableField("merchant_brief")
    private String merchantBrief;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("contact_name")
    private String contactName;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("email")
    private String email;

    @TableField("main_category_id")
    private Long mainCategoryId;

    @TableField("merchant_level")
    private Integer merchantLevel;

    @TableField("status")
    private Integer status;

    @TableField("sort")
    private Integer sort;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantLogo() {
        return merchantLogo;
    }

    public void setMerchantLogo(String merchantLogo) {
        this.merchantLogo = merchantLogo;
    }

    public String getMerchantBrief() {
        return merchantBrief;
    }

    public void setMerchantBrief(String merchantBrief) {
        this.merchantBrief = merchantBrief;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getMainCategoryId() {
        return mainCategoryId;
    }

    public void setMainCategoryId(Long mainCategoryId) {
        this.mainCategoryId = mainCategoryId;
    }

    public Integer getMerchantLevel() {
        return merchantLevel;
    }

    public void setMerchantLevel(Integer merchantLevel) {
        this.merchantLevel = merchantLevel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
