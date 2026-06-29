package com.mall.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CaptchaService {

    private static final Map<String, String> CAPTCHA_CACHE = new HashMap<>();

    public LineCaptcha generateCaptcha(String key) {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 50);
        String code = captcha.getCode();
        CAPTCHA_CACHE.put(key, code.toLowerCase());
        return captcha;
    }

    public String generateKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public boolean verify(String key, String captcha) {
        String cached = CAPTCHA_CACHE.get(key);
        if (cached == null) {
            return false;
        }
        boolean result = cached.equals(captcha.toLowerCase());
        if (result) {
            CAPTCHA_CACHE.remove(key);
        }
        return result;
    }

    public String getCachedCode(String key) {
        return CAPTCHA_CACHE.get(key);
    }
}
