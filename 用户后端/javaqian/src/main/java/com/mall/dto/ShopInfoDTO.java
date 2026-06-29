package com.mall.dto;

public class ShopInfoDTO {

    private Long id;
    private String name;
    private String logo;
    private String level;
    private Integer totalSales;
    private Double recentGoodReviews;
    private Double recentOrders;
    private Integer reviewers;
    private String guarantee;
    private String operator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Integer totalSales) {
        this.totalSales = totalSales;
    }

    public Double getRecentGoodReviews() {
        return recentGoodReviews;
    }

    public void setRecentGoodReviews(Double recentGoodReviews) {
        this.recentGoodReviews = recentGoodReviews;
    }

    public Double getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(Double recentOrders) {
        this.recentOrders = recentOrders;
    }

    public Integer getReviewers() {
        return reviewers;
    }

    public void setReviewers(Integer reviewers) {
        this.reviewers = reviewers;
    }

    public String getGuarantee() {
        return guarantee;
    }

    public void setGuarantee(String guarantee) {
        this.guarantee = guarantee;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}