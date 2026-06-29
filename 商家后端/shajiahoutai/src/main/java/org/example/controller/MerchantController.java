package org.example.controller;

import org.example.common.Result;
import org.example.dto.LoginDTO;
import org.example.dto.RegisterDTO;
import org.example.entity.TermMerchant;
import org.example.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {
    
    @Autowired
    private MerchantService merchantService;
    
    /**
     * 商家注册
     */
    @PostMapping("/register")
    public Result<TermMerchant> register(@Validated @RequestBody RegisterDTO registerDTO) {
        try {
            TermMerchant merchant = merchantService.register(registerDTO);
            // 清除密码，不返回给前端
            merchant.setPassword(null);
            return Result.success("注册成功", merchant);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 商家登录（支持手机号或邮箱登录）
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginDTO loginDTO) {
        try {
            String token = merchantService.login(loginDTO);
            TermMerchant merchant = merchantService.getByLoginId(loginDTO.getLoginId());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("merchant", merchant);
            
            return Result.success("登录成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据用户名查询商家信息
     */
    @GetMapping("/info/{username}")
    public Result<TermMerchant> getMerchantInfo(@PathVariable String username) {
        try {
            TermMerchant merchant = merchantService.getByUsername(username);
            if (merchant != null) {
                merchant.setPassword(null);
            }
            return Result.success(merchant);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}