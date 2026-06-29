package org.example;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.*;
import java.util.Random;

public class TestDataGenerator {
    
    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    private static String[] merchantNames = {
        "鲜果坊", "美味餐厅", "时尚服饰", "数码科技", "家居生活馆",
        "美妆护肤", "运动户外", "母婴用品", "图书文具", "宠物乐园"
    };
    
    private static String[] contactNames = {
        "张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十", "郑一", "陈二"
    };
    
    private static String[] emails = {
        "merchant1@example.com", "merchant2@example.com", "merchant3@example.com",
        "merchant4@example.com", "merchant5@example.com", "merchant6@example.com",
        "merchant7@example.com", "merchant8@example.com", "merchant9@example.com",
        "merchant10@example.com"
    };
    
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/termshop?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        String user = "root";
        String password = "123456";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // 创建PreparedStatement来插入数据
            String sql = "INSERT INTO term_merchants (merchant_name, username, password, contact_name, contact_phone, email, merchant_brief, merchant_level, status, sort) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                Random random = new Random();
                String encodedPassword = passwordEncoder.encode("123456");
                System.out.println("Encoded password: " + encodedPassword);
                
                for (int i = 0; i < merchantNames.length; i++) {
                    stmt.setString(1, merchantNames[i]);
                    stmt.setString(2, "user" + (i + 1));
                    stmt.setString(3, encodedPassword);
                    stmt.setString(4, contactNames[i]);
                    stmt.setString(5, generatePhone(random));
                    stmt.setString(6, emails[i]);
                    stmt.setString(7, "欢迎光临" + merchantNames[i]);
                    stmt.setInt(8, random.nextInt(3) + 1);
                    stmt.setInt(9, 1);
                    stmt.setInt(10, i);
                    
                    stmt.executeUpdate();
                    System.out.println("插入商家: " + merchantNames[i]);
                }
                
                System.out.println("\n测试数据插入完成！");
                System.out.println("密码统一为: 123456");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static String generatePhone(Random random) {
        String prefix = "1" + (random.nextInt(7) + 3);
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < 9; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}