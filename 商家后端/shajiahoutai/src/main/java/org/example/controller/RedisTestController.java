package org.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/redis")
public class RedisTestController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/test")
    public Map<String, Object> testRedis() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 测试写入
            String testKey = "test:connection:" + System.currentTimeMillis();
            String testValue = "Redis连接测试成功！";
            
            redisTemplate.opsForValue().set(testKey, testValue, 10, TimeUnit.SECONDS);
            
            // 测试读取
            Object readValue = redisTemplate.opsForValue().get(testKey);
            
            // 清理测试数据
            redisTemplate.delete(testKey);

            result.put("code", 200);
            result.put("message", "Redis连接测试成功");
            result.put("data", Map.of(
                "write", testValue,
                "read", readValue,
                "match", testValue.equals(readValue)
            ));
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "Redis连接失败");
            result.put("error", e.getMessage());
        }

        return result;
    }

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String pong = redisTemplate.getConnectionFactory().getConnection().ping();
            result.put("code", 200);
            result.put("message", "Redis服务正常");
            result.put("pong", pong);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "Redis服务异常");
            result.put("error", e.getMessage());
        }

        return result;
    }
}