package com.mall.dto;

import java.util.List;

public class BrandInfoDTO {

    private Long id;
    private String name;
    private String since;
    private String logo;
    private String introduction;
    private List<FeatureItem> features;

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

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public List<FeatureItem> getFeatures() {
        return features;
    }

    public void setFeatures(List<FeatureItem> features) {
        this.features = features;
    }

    public static class FeatureItem {
        private String label;
        private String value;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}