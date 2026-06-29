package org.example.test;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ProductWithTagsTest {
    public static void main(String[] args) throws Exception {
        URL url = new URL("http://localhost:8080/api/product/add");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        
        // 带多个标签的商品
        String json = "{\"merchantId\":24,\"productName\":\"智能手环\",\"categoryId\":1,\"price\":199.00,\"stock\":150,\"mainImage\":\"https://example.com/bracelet.jpg\",\"brief\":\"健康监测手环\",\"isHot\":0,\"isNew\":1,\"tags\":[{\"tagName\":\"健康\",\"tagColor\":\"#2ECC71\"},{\"tagName\":\"运动\",\"tagColor\":\"#3498DB\"},{\"tagName\":\"新品\",\"tagColor\":\"#E74C3C\"}]}";
        
        try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
            writer.write(json);
            writer.flush();
        }
        
        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        
        java.io.BufferedReader reader = new java.io.BufferedReader(
            new java.io.InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        conn.disconnect();
        
        System.out.println("Response Body: " + response.toString());
    }
}