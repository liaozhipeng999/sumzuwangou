package com.mall.dto;

public class GenerateImageDTO {

    private String prompt;

    private String negativePrompt = "";

    private Integer steps = 20;

    private Integer width = 512;

    private Integer height = 512;

    private Double cfgScale = 7.0;

    private String samplerName = "Euler";

    private Long seed = -1L;

    private Integer nIter = 1;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getNegativePrompt() {
        return negativePrompt;
    }

    public void setNegativePrompt(String negativePrompt) {
        this.negativePrompt = negativePrompt;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Double getCfgScale() {
        return cfgScale;
    }

    public void setCfgScale(Double cfgScale) {
        this.cfgScale = cfgScale;
    }

    public String getSamplerName() {
        return samplerName;
    }

    public void setSamplerName(String samplerName) {
        this.samplerName = samplerName;
    }

    public Long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public Integer getNIter() {
        return nIter;
    }

    public void setNIter(Integer nIter) {
        this.nIter = nIter;
    }
}
