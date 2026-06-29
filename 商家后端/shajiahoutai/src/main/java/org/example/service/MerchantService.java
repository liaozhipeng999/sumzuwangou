package org.example.service;

import org.example.dto.LoginDTO;
import org.example.dto.RegisterDTO;
import org.example.entity.TermMerchant;

public interface MerchantService {
    /**
     * 商家注册
     */
    TermMerchant register(RegisterDTO registerDTO);
    
    /**
     * 商家登录（支持手机号或邮箱）
     */
    String login(LoginDTO loginDTO);
    
    /**
     * 根据用户名查询商家
     */
    TermMerchant getByUsername(String username);
    
    /**
     * 根据登录ID（手机号或邮箱）查询商家
     */
    TermMerchant getByLoginId(String loginId);
}