package org.example.test;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ProductTest {
    public static void main(String[] args) throws Exception {
        URL url = new URL("http://localhost:8080/api/product/add");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        
        String json = "{\"merchantId\":1,\"productName\":\"荣耀Magic6 Pro\",\"categoryId\":1,\"price\":5499.00,\"stock\":70,\"mainImage\":\"https://example.com/honor.jpg\",\"brief\":\"荣耀旗舰手机\",\"isHot\":1,\"isNew\":1}";
        
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
        
        System.out.println("Response Body: " + response.toString());
        conn.disconnect();
    }
}