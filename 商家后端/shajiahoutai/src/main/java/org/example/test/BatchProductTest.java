package org.example.test;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class BatchProductTest {
    
    private static final String BASE_URL = "http://localhost:8080/api/product/add";
    private static final Long MERCHANT_ID = 24L; // 商家ID
    
    // 5条商品数据
    private static final String[] PRODUCTS = {
        "{\"merchantId\":24,\"productName\":\"无线蓝牙耳机\",\"categoryId\":1,\"price\":299.00,\"stock\":200,\"mainImage\":\"https://example.com/headphone.jpg\",\"brief\":\"高品质无线耳机\",\"isHot\":1,\"isNew\":0}",
        "{\"merchantId\":24,\"productName\":\"智能手表\",\"categoryId\":1,\"price\":899.00,\"stock\":100,\"mainImage\":\"https://example.com/watch.jpg\",\"brief\":\"运动智能手表\",\"isHot\":1,\"isNew\":1}",
        "{\"merchantId\":24,\"productName\":\"便携充电宝\",\"categoryId\":1,\"price\":159.00,\"stock\":300,\"mainImage\":\"https://example.com/powerbank.jpg\",\"brief\":\"20000mAh大容量\",\"isHot\":0,\"isNew\":0}",
        "{\"merchantId\":24,\"productName\":\"机械键盘\",\"categoryId\":1,\"price\":459.00,\"stock\":80,\"mainImage\":\"https://example.com/keyboard.jpg\",\"brief\":\"RGB背光机械键盘\",\"isHot\":1,\"isNew\":0}",
        "{\"merchantId\":24,\"productName\":\"游戏鼠标\",\"categoryId\":1,\"price\":249.00,\"stock\":150,\"mainImage\":\"https://example.com/mouse.jpg\",\"brief\":\"高精度游戏鼠标\",\"isHot\":0,\"isNew\":1}"
    };
    
    public static void main(String[] args) throws Exception {
        System.out.println("========== 批量上架商品测试 ==========");
        System.out.println("商家ID: " + MERCHANT_ID);
        System.out.println("=====================================\n");
        
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 0; i < PRODUCTS.length; i++) {
            String json = PRODUCTS[i];
            String response = sendPostRequest(json);
            
            System.out.println("商品 " + (i + 1) + ":");
            System.out.println("请求: " + json);
            System.out.println("响应: " + response);
            System.out.println();
            
            if (response.contains("\"code\":200")) {
                successCount++;
            } else {
                failCount++;
            }
            
            // 间隔100ms避免请求过快
            Thread.sleep(100);
        }
        
        System.out.println("========== 测试结果 ==========");
        System.out.println("成功: " + successCount + " 条");
        System.out.println("失败: " + failCount + " 条");
        System.out.println("==============================");
    }
    
    private static String sendPostRequest(String json) throws Exception {
        URL url = new URL(BASE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        
        try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
            writer.write(json);
            writer.flush();
        }
        
        java.io.BufferedReader reader = new java.io.BufferedReader(
            new java.io.InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        conn.disconnect();
        
        return response.toString();
    }
}