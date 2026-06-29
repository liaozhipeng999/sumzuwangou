package com.mall.controller;

import com.mall.entity.Cart;
import com.mall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/list")
    public Map<String, Object> getCartList(@RequestParam("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Cart> carts = cartService.getCartList(userId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", carts);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/add")
    public Map<String, Object> addToCart(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            Long productId = Long.valueOf(params.get("productId").toString());
            String productName = (String) params.get("productName");
            String productImage = (String) params.get("productImage");
            BigDecimal price = new BigDecimal(params.get("price").toString());
            Integer quantity = params.get("quantity") != null ? Integer.valueOf(params.get("quantity").toString()) : 1;

            Cart cart = cartService.addToCart(userId, productId, productName, productImage, price, quantity);
            result.put("code", 200);
            result.put("message", "添加成功");
            result.put("data", cart);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/update")
    public Map<String, Object> updateQuantity(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            Long cartId = Long.valueOf(params.get("cartId").toString());
            Integer quantity = Integer.valueOf(params.get("quantity").toString());

            boolean success = cartService.updateQuantity(userId, cartId, quantity);
            if (success) {
                result.put("code", 200);
                result.put("message", "更新成功");
            } else {
                result.put("code", 400);
                result.put("message", "更新失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/select")
    public Map<String, Object> updateSelected(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            Long cartId = Long.valueOf(params.get("cartId").toString());
            Integer selected = Integer.valueOf(params.get("selected").toString());

            boolean success = cartService.updateSelected(userId, cartId, selected);
            if (success) {
                result.put("code", 200);
                result.put("message", "更新成功");
            } else {
                result.put("code", 400);
                result.put("message", "更新失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/selectAll")
    public Map<String, Object> updateAllSelected(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            Integer selected = Integer.valueOf(params.get("selected").toString());

            boolean success = cartService.updateAllSelected(userId, selected);
            if (success) {
                result.put("code", 200);
                result.put("message", "更新成功");
            } else {
                result.put("code", 400);
                result.put("message", "更新失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/remove")
    public Map<String, Object> removeFromCart(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            Long cartId = Long.valueOf(params.get("cartId").toString());

            boolean success = cartService.removeFromCart(userId, cartId);
            if (success) {
                result.put("code", 200);
                result.put("message", "删除成功");
            } else {
                result.put("code", 400);
                result.put("message", "删除失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/clear")
    public Map<String, Object> clearCart(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            cartService.clearCart(userId);
            result.put("code", 200);
            result.put("message", "清空成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/count")
    public Map<String, Object> getCartCount(@RequestParam("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            int count = cartService.getCartCount(userId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", count);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
