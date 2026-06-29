package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.dto.LoginDTO;
import org.example.dto.RegisterDTO;
import org.example.entity.TermMerchant;
import org.example.mapper.TermMerchantMapper;
import org.example.service.MerchantService;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class MerchantServiceImpl implements MerchantService {
    
    @Autowired
    private TermMerchantMapper merchantMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    @Override
    public TermMerchant register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        TermMerchant existMerchant = getByUsername(registerDTO.getUsername());
        if (existMerchant != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号是否已被注册
        LambdaQueryWrapper<TermMerchant> phoneQuery = new LambdaQueryWrapper<>();
        phoneQuery.eq(TermMerchant::getContactPhone, registerDTO.getContactPhone());
        if (merchantMapper.selectOne(phoneQuery) != null) {
            throw new RuntimeException("该手机号已被注册");
        }
        
        // 检查邮箱是否已被注册（如果提供了邮箱）
        if (registerDTO.getEmail() != null && !registerDTO.getEmail().isEmpty()) {
            LambdaQueryWrapper<TermMerchant> emailQuery = new LambdaQueryWrapper<>();
            emailQuery.eq(TermMerchant::getEmail, registerDTO.getEmail());
            if (merchantMapper.selectOne(emailQuery) != null) {
                throw new RuntimeException("该邮箱已被注册");
            }
        }
        
        // 创建商家实体
        TermMerchant merchant = new TermMerchant();
        merchant.setMerchantName(registerDTO.getMerchantName());
        merchant.setUsername(registerDTO.getUsername());
        // 使用BCrypt加密密码
        merchant.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        merchant.setContactName(registerDTO.getContactName());
        merchant.setContactPhone(registerDTO.getContactPhone());
        merchant.setEmail(registerDTO.getEmail());
        merchant.setMerchantLogo(registerDTO.getMerchantLogo());
        merchant.setMerchantBrief(registerDTO.getMerchantBrief());
        merchant.setMainCategoryId(registerDTO.getMainCategoryId());
        merchant.setMerchantLevel(1); // 默认普通商家
        merchant.setStatus(1); // 默认营业中
        merchant.setSort(0);
        
        // 保存到数据库
        merchantMapper.insert(merchant);
        
        return merchant;
    }
    
    @Override
    public String login(LoginDTO loginDTO) {
        // 根据登录ID（手机号或邮箱）查询用户
        TermMerchant merchant = getByLoginId(loginDTO.getLoginId());
        if (merchant == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 使用BCrypt验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), merchant.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 检查状态
        if (merchant.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        // 生成token
        return jwtUtil.generateToken(merchant.getId(), merchant.getUsername());
    }
    
    @Override
    public TermMerchant getByUsername(String username) {
        LambdaQueryWrapper<TermMerchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TermMerchant::getUsername, username);
        return merchantMapper.selectOne(queryWrapper);
    }
    
    @Override
    public TermMerchant getByLoginId(String loginId) {
        LambdaQueryWrapper<TermMerchant> queryWrapper = new LambdaQueryWrapper<>();
        
        if (isPhone(loginId)) {
            // 手机号登录
            queryWrapper.eq(TermMerchant::getContactPhone, loginId);
        } else if (isEmail(loginId)) {
            // 邮箱登录
            queryWrapper.eq(TermMerchant::getEmail, loginId);
        } else {
            // 只能用手机号或邮箱登录
            throw new RuntimeException("请使用手机号或邮箱登录");
        }
        
        return merchantMapper.selectOne(queryWrapper);
    }
    
    /**
     * 判断是否为手机号
     */
    private boolean isPhone(String str) {
        return PHONE_PATTERN.matcher(str).matches();
    }
    
    /**
     * 判断是否为邮箱
     */
    private boolean isEmail(String str) {
        return EMAIL_PATTERN.matcher(str).matches();
    }
}