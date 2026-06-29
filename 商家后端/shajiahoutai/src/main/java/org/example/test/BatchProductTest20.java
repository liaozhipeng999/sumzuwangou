package org.example.test;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class BatchProductTest20 {
    
    private static final String BASE_URL = "http://localhost:8080/api/product/add";
    private static final Long MERCHANT_ID = 24L;
    
    private static final String[] PRODUCTS = {
        "{\"merchantId\":24,\"productName\":\"无线充电器\",\"categoryId\":1,\"price\":89.00,\"stock\":200,\"mainImage\":\"https://example.com/charger.jpg\",\"brief\":\"15W快充无线充电器\",\"tags\":[{\"tagName\":\"数码\",\"tagColor\":\"#3498DB\"}]}",
        "{\"merchantId\":24,\"productName\":\"USB数据线\",\"categoryId\":1,\"price\":29.00,\"stock\":500,\"mainImage\":\"https://example.com/cable.jpg\",\"brief\":\"Type-C快充数据线\"}",
        "{\"merchantId\":24,\"productName\":\"手机壳\",\"categoryId\":1,\"price\":39.00,\"stock\":300,\"mainImage\":\"https://example.com/case.jpg\",\"brief\":\"防摔手机保护壳\",\"tags\":[{\"tagName\":\"配件\",\"tagColor\":\"#9B59B6\"}]}"
    };
    
    private static final String[] PRODUCT_NAMES = {
        "蓝牙耳机", "智能音箱", "平板电脑", "电子书阅读器", "无线鼠标",
        "机械键盘", "游戏手柄", "移动硬盘", "U盘", "路由器",
        "摄像头", "麦克风", "显示器", "笔记本支架", "散热底座",
        "充电宝", "数据线收纳", "手机支架", "车载充电器", "耳机收纳盒"
    };
    
    private static final String[] BRIEFS = {
        "高品质音频体验", "智能语音助手", "便携办公利器", "阅读爱好者必备", "精准操控",
        "RGB背光", "游戏必备", "大容量存储", "高速传输", "全屋覆盖",
        "高清画质", "专业录音", "超清显示", "人体工学设计", "高效散热",
        "超大容量", "整理收纳", "懒人必备", "出行必备", "随身携带"
    };
    
    public static void main(String[] args) throws Exception {
        System.out.println("========== 批量上架20条商品测试 ==========");
        System.out.println("商家ID: " + MERCHANT_ID);
        System.out.println("========================================\n");
        
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 0; i < 20; i++) {
            String json = generateProductJson(i);
            String response = sendPostRequest(json);
            
            if (response.contains("\"code\":200")) {
                successCount++;
                System.out.println("商品 " + (i + 1) + ": " + PRODUCT_NAMES[i] + " - ✅ 成功");
            } else {
                failCount++;
                System.out.println("商品 " + (i + 1) + ": " + PRODUCT_NAMES[i] + " - ❌ 失败");
            }
            
            Thread.sleep(50);
        }
        
        System.out.println("\n========== 测试结果 ==========");
        System.out.println("成功: " + successCount + " 条");
        System.out.println("失败: " + failCount + " 条");
        System.out.println("==============================");
    }
    
    private static String generateProductJson(int index) {
        double price = 29.00 + (index * 15);
        int stock = 50 + (index * 10);
        
        return String.format(
            "{\"merchantId\":24,\"productName\":\"%s\",\"categoryId\":1,\"price\":%.2f,\"stock\":%d,\"mainImage\":\"https://example.com/product%d.jpg\",\"brief\":\"%s\",\"tags\":[{\"tagName\":\"数码\",\"tagColor\":\"#3498DB\"}]}",
            PRODUCT_NAMES[index], price, stock, index + 1, BRIEFS[index]
        );
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