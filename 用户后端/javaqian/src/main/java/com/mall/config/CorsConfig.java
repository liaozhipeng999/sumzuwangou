package com.mall.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // 本地图片目录
    private static final String LOCAL_IMAGE_PATH = "file:F:/temp/image/";
    private static final String LOCAL_CATALOG_PATH = "file:F:/temp/catalog/";
    private static final String LOCAL_BRAND_PATH = "file:F:/temp/brand/";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    /**
     * 配置静态资源映射
     * 将 /images/** 请求映射到本地图片目录 F:/temp/image/
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 /images/** 到本地图片目录
        registry.addResourceHandler("/images/**")
                .addResourceLocations(LOCAL_IMAGE_PATH)
                .setCachePeriod(3600); // 缓存时间（秒）

        // 映射 /catalog/** 到本地目录 F:/temp/catalog/
        registry.addResourceHandler("/catalog/**")
                .addResourceLocations(LOCAL_CATALOG_PATH)
                .setCachePeriod(3600);

        // 映射 /brand/** 到本地目录 F:/temp/brand/
        registry.addResourceHandler("/brand/**")
                .addResourceLocations(LOCAL_BRAND_PATH)
                .setCachePeriod(3600);

        // 映射 /static/** 到默认静态资源目录
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
