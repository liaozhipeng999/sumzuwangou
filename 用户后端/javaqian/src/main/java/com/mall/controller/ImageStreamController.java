package com.mall.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.dto.GenerateImageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI 图片生成流式接口
 */
@RestController
@RequestMapping("/image")
public class ImageStreamController {

    @Value("${sd-webui.base-url:http://localhost:7860}")
    private String sdWebUiBaseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    /**
     * 文生图流式接口（Server-Sent Events）
     * 
     * 前端使用方式：
     * const eventSource = new EventSource('/image/generimage/stream');
     * eventSource.onmessage = (event) => {
     *   const data = JSON.parse(event.data);
     *   if (data.type === 'progress') { console.log(data.progress); }
     *   if (data.type === 'complete') { displayImage(data.image); }
     *   if (data.type === 'error') { console.error(data.message); }
     * };
     */
    @PostMapping(value = "/generimage/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateImageStream(@RequestBody GenerateImageDTO dto) {
        SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时

        executorService.execute(() -> {
            try {
                // 1. 验证参数
                if (dto.getPrompt() == null || dto.getPrompt().trim().isEmpty()) {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"type\":\"error\",\"message\":\"提示词不能为空\"}"));
                    emitter.complete();
                    return;
                }

                // 2. 检查 SD WebUI 状态
                if (!checkServiceAvailable()) {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"type\":\"error\",\"message\":\"SD WebUI 服务不可用\"}"));
                    emitter.complete();
                    return;
                }

                // 3. 发送开始事件
                emitter.send(SseEmitter.event()
                        .name("start")
                        .data("{\"type\":\"start\",\"prompt\":\"" + escapeJson(dto.getPrompt()) + "\"}"));

                // 4. 轮询进度并推送
                Thread progressThread = new Thread(() -> {
                    try {
                        while (true) {
                            String progressInfo = getProgress();
                            if (progressInfo != null) {
                                JsonNode progress = objectMapper.readTree(progressInfo);
                                double progressPercent = progress.has("progress") ? 
                                    progress.get("progress").asDouble() * 100 : 0;
                                
                                emitter.send(SseEmitter.event()
                                        .name("progress")
                                        .data("{\"type\":\"progress\",\"percent\":" + 
                                              progressPercent + ",\"info\":\"" + 
                                              escapeJson(progress.toString()) + "\"}"));
                            }
                            
                            if (isGenerationComplete()) {
                                break;
                            }
                            Thread.sleep(500); // 每500ms轮询一次
                        }
                    } catch (Exception e) {
                        // 进度轮询异常忽略
                    }
                });
                progressThread.start();

                // 5. 调用 SD WebUI 生成图片
                String base64Image = callTxt2Img(dto);

                // 6. 等待进度线程结束
                progressThread.join();

                // 7. 发送完成事件
                emitter.send(SseEmitter.event()
                        .name("complete")
                        .data("{\"type\":\"complete\",\"image\":\"data:image/png;base64," + 
                              base64Image + "\",\"prompt\":\"" + 
                              escapeJson(dto.getPrompt()) + "\"}"));

                emitter.complete();

            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"type\":\"error\",\"message\":\"" + 
                                  escapeJson(e.getMessage()) + "\"}"));
                } catch (IOException ignored) {}
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    /**
     * 检查服务可用性
     */
    private boolean checkServiceAvailable() {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(sdWebUiBaseUrl + "/sdapi/v1/progress"))
                    .timeout(java.time.Duration.ofSeconds(5))
                    .GET()
                    .build();
            java.net.http.HttpResponse<String> response = client.send(request, 
                    java.net.http.HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取生成进度
     */
    private String getProgress() {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(sdWebUiBaseUrl + "/sdapi/v1/progress"))
                    .timeout(java.time.Duration.ofSeconds(3))
                    .GET()
                    .build();
            java.net.http.HttpResponse<String> response = client.send(request, 
                    java.net.http.HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (Exception e) {}
        return null;
    }

    /**
     * 检查生成是否完成
     */
    private boolean isGenerationComplete() {
        try {
            String progress = getProgress();
            if (progress != null) {
                JsonNode node = objectMapper.readTree(progress);
                return !node.has("state") || !node.get("state").has("job") || 
                       node.get("state").get("job").isNull();
            }
        } catch (Exception e) {}
        return false;
    }

    /**
     * 调用 SD WebUI txt2img 接口
     */
    private String callTxt2Img(GenerateImageDTO dto) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("prompt", dto.getPrompt());
        payload.put("negative_prompt", dto.getNegativePrompt() != null ? dto.getNegativePrompt() : "");
        payload.put("steps", dto.getSteps() != null ? dto.getSteps() : 20);
        payload.put("width", dto.getWidth() != null ? dto.getWidth() : 512);
        payload.put("height", dto.getHeight() != null ? dto.getHeight() : 512);
        payload.put("cfg_scale", dto.getCfgScale() != null ? dto.getCfgScale() : 7.0);
        payload.put("sampler_name", dto.getSamplerName() != null ? dto.getSamplerName() : "Euler");
        payload.put("seed", dto.getSeed() != null ? dto.getSeed() : -1);
        payload.put("n_iter", dto.getNIter() != null ? dto.getNIter() : 1);

        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(sdWebUiBaseUrl + "/sdapi/v1/txt2img"))
                .header("Content-Type", "application/json")
                .timeout(java.time.Duration.ofSeconds(300))
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                .build();

        java.net.http.HttpResponse<String> response = client.send(request, 
                java.net.http.HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode result = objectMapper.readTree(response.body());
            JsonNode images = result.get("images");
            if (images != null && images.size() > 0) {
                return images.get(0).asText();
            }
            throw new RuntimeException("未返回图片");
        } else {
            throw new RuntimeException("请求失败: " + response.statusCode());
        }
    }

    /**
     * JSON 字符串转义
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }
}
