package com.mall.controller;

import com.mall.dto.RegisterDTO;
import com.mall.entity.MallUser;
import com.mall.mapper.UserFavoriteMapper;
import com.mall.service.CaptchaService;
import com.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");
            MallUser user = userService.login(username, password);
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "nickname", user.getNickname() != null ? user.getNickname() : "",
                "phone", user.getPhone() != null ? user.getPhone() : "",
                "email", user.getEmail() != null ? user.getEmail() : ""
            ));
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取图形验证码
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response) throws Exception {
        String key = captchaService.generateKey();
        cn.hutool.captcha.LineCaptcha captcha = captchaService.generateCaptcha(key);
        
        response.setContentType("image/png");
        response.setHeader("Captcha-Key", key);
        captcha.write(response.getOutputStream());
    }

    /**
     * 调试接口：获取验证码内容（测试用）
     */
    @GetMapping("/captcha/debug")
    public Map<String, Object> getCaptchaDebug() {
        Map<String, Object> result = new HashMap<>();
        String key = captchaService.generateKey();
        cn.hutool.captcha.LineCaptcha captcha = captchaService.generateCaptcha(key);
        
        result.put("key", key);
        result.put("code", captcha.getCode());
        return result;
    }

    /**
     * 注册接口
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestHeader(value = "Captcha-Key", required = false) String captchaKey,
                                        @Validated @RequestBody RegisterDTO dto) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            MallUser user = new MallUser();
            user.setUsername(dto.getUsername());
            user.setPassword(dto.getPassword());
            user.setPhone(dto.getPhone());
            user.setEmail(dto.getEmail());
            user.setNickname(dto.getNickname());
            
            userService.register(captchaKey, dto.getCaptcha(), user);
            
            result.put("code", 200);
            result.put("message", "注册成功");
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取用户收藏商品列表（分页）
     * GET /user/favorites
     */
    @GetMapping("/favorites")
    public Map<String, Object> getUserFavorites(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            int offset = (page - 1) * pageSize;
            int total = userFavoriteMapper.countFavoriteProducts(userId);
            List<Map<String, Object>> favorites = userFavoriteMapper.findFavoriteProducts(userId, offset, pageSize);
            
            Map<String, Object> data = new HashMap<>();
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);
            data.put("list", favorites);
            
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取收藏失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 添加商品到收藏
     * POST /user/favorites
     */
    @PostMapping("/favorites")
    public Map<String, Object> addFavorite(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            Long productId = ((Number) request.get("productId")).longValue();
            
            // 检查是否已收藏
            if (userFavoriteMapper.findByUserIdAndProductId(userId, productId) != null) {
                result.put("code", 400);
                result.put("message", "该商品已在收藏列表中");
                return result;
            }
            
            com.mall.entity.UserFavorite favorite = new com.mall.entity.UserFavorite();
            favorite.setUserId(userId);
            favorite.setProductId(productId);
            userFavoriteMapper.insert(favorite);
            
            result.put("code", 200);
            result.put("message", "收藏成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "收藏失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 取消收藏商品
     * DELETE /user/favorites
     */
    @DeleteMapping("/favorites")
    public Map<String, Object> removeFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int deleted = userFavoriteMapper.deleteByUserIdAndProductId(userId, productId);
            
            if (deleted > 0) {
                result.put("code", 200);
                result.put("message", "取消收藏成功");
            } else {
                result.put("code", 400);
                result.put("message", "该商品不在收藏列表中");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "取消收藏失败: " + e.getMessage());
        }
        
        return result;
    }
}
