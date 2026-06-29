package com.mall.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.dto.GenerateImageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * SD WebUI 文生图服务
 */
@Service
public class ImageGenerateService {

    @Value("${sd-webui.base-url:http://localhost:7860}")
    private String sdWebUiBaseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 调用 SD WebUI 文生图接口
     *
     * @param dto 生成参数
     * @return base64编码的图片数据
     */
    public String generateImage(GenerateImageDTO dto) {
        String url = sdWebUiBaseUrl + "/sdapi/v1/txt2img";

        Map<String, Object> payload = new HashMap<>();
        payload.put("prompt", dto.getPrompt());
        payload.put("negative_prompt", dto.getNegativePrompt());
        payload.put("steps", dto.getSteps());
        payload.put("width", dto.getWidth());
        payload.put("height", dto.getHeight());
        payload.put("cfg_scale", dto.getCfgScale());
        payload.put("sampler_name", dto.getSamplerName());
        payload.put("seed", dto.getSeed());
        payload.put("n_iter", dto.getNIter());

        try {
            HttpResponse response = HttpRequest.post(url)
                    .header("Content-Type", "application/json")
                    .body(objectMapper.writeValueAsString(payload))
                    .timeout(300000) // 5分钟超时
                    .execute();

            if (response.isOk()) {
                JsonNode result = objectMapper.readTree(response.body());
                JsonNode images = result.get("images");
                if (images != null && images.size() > 0) {
                    return images.get(0).asText();
                }
                throw new RuntimeException("SD WebUI 未返回图片");
            } else {
                throw new RuntimeException("SD WebUI 请求失败: " + response.getStatus() + " - " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("调用 SD WebUI 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取 SD WebUI 服务状态
     */
    public boolean isServiceAvailable() {
        try {
            String url = sdWebUiBaseUrl + "/sdapi/v1/progress";
            HttpResponse response = HttpRequest.get(url)
                    .timeout(5000)
                    .execute();
            return response.isOk();
        } catch (Exception e) {
            return false;
        }
    }
}
