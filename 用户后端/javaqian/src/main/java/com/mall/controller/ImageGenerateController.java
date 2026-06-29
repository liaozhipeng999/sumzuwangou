package com.mall.controller;

import com.mall.dto.GenerateImageDTO;
import com.mall.service.ImageGenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 图片生成接口
 */
@RestController
@RequestMapping("/image")
public class ImageGenerateController {

    @Autowired
    private ImageGenerateService imageGenerateService;

    /**
     * 文生图接口
     *
     * @param dto 生成参数
     * @return 包含base64图片的结果
     */
    @PostMapping("/generimage")
    public Map<String, Object> generateImage(@RequestBody GenerateImageDTO dto) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 验证必填参数
            if (dto.getPrompt() == null || dto.getPrompt().trim().isEmpty()) {
                result.put("code", 400);
                result.put("message", "提示词不能为空");
                return result;
            }

            // 检查服务是否可用
            if (!imageGenerateService.isServiceAvailable()) {
                result.put("code", 503);
                result.put("message", "SD WebUI 服务不可用，请确保已启动");
                return result;
            }

            // 调用 SD WebUI 生成图片
            String base64Image = imageGenerateService.generateImage(dto);

            result.put("code", 200);
            result.put("message", "生成成功");
            result.put("data", Map.of(
                    "image", "data:image/png;base64," + base64Image,
                    "prompt", dto.getPrompt()
            ));
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "生成失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 检查 SD WebUI 服务状态
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> result = new HashMap<>();
        boolean available = imageGenerateService.isServiceAvailable();
        result.put("code", available ? 200 : 503);
        result.put("message", available ? "SD WebUI 服务正常" : "SD WebUI 服务不可用");
        result.put("data", Map.of("available", available));
        return result;
    }
}
