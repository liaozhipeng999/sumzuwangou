package com.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.entity.MallUser;
import com.mall.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CaptchaService captchaService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public MallUser login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("登录账号不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }

        LambdaQueryWrapper<MallUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MallUser::getUsername, username)
               .or()
               .eq(MallUser::getEmail, username)
               .or()
               .eq(MallUser::getPhone, username);

        MallUser user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("账号不存在");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        return user;
    }

    public void register(String captchaKey, String captcha, MallUser user) {
        // 第二层：校验验证码
        if (!captchaService.verify(captchaKey, captcha)) {
            throw new RuntimeException("验证码错误");
        }

        // 第三层：校验两次密码
        if (!user.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("两次密码不一致");
        }

        // 第四层：查询数据库，判断账号/手机号是否已注册
        LambdaQueryWrapper<MallUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MallUser::getUsername, user.getUsername())
               .or()
               .eq(MallUser::getPhone, user.getPhone());
        Long count = userMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("账号或手机号已注册");
        }

        // 第五层：密码 BCrypt 加密
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        user.setStatus(1);

        // 第六层：插入数据库
        userMapper.insert(user);
    }
}
