package com.mall.service;

import com.mall.entity.Cart;
import com.mall.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartMapper cartMapper;

    public List<Cart> getCartList(Long userId) {
        return cartMapper.findByUserId(userId);
    }

    @Transactional
    public Cart addToCart(Long userId, Long productId, String productName, String productImage, BigDecimal price, Integer quantity) {
        Cart existing = cartMapper.findByUserIdAndProductId(userId, productId);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            existing.setUpdatedAt(LocalDateTime.now());
            cartMapper.updateById(existing);
            return existing;
        }

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setProductName(productName);
        cart.setProductImage(productImage);
        cart.setPrice(price);
        cart.setQuantity(quantity);
        cart.setSelected(1);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        cartMapper.insert(cart);
        return cart;
    }

    @Transactional
    public boolean updateQuantity(Long userId, Long cartId, Integer quantity) {
        if (quantity <= 0) {
            return false;
        }
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return false;
        }
        cart.setQuantity(quantity);
        cart.setUpdatedAt(LocalDateTime.now());
        cartMapper.updateById(cart);
        return true;
    }

    @Transactional
    public boolean updateSelected(Long userId, Long cartId, Integer selected) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return false;
        }
        cart.setSelected(selected);
        cart.setUpdatedAt(LocalDateTime.now());
        cartMapper.updateById(cart);
        return true;
    }

    @Transactional
    public boolean removeFromCart(Long userId, Long cartId) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return false;
        }
        cartMapper.deleteById(cartId);
        return true;
    }

    @Transactional
    public boolean clearCart(Long userId) {
        List<Cart> carts = cartMapper.findByUserId(userId);
        for (Cart cart : carts) {
            cartMapper.deleteById(cart.getId());
        }
        return true;
    }

    @Transactional
    public boolean updateAllSelected(Long userId, Integer selected) {
        cartMapper.updateAllSelectedByUserId(userId, selected);
        return true;
    }

    public int getCartCount(Long userId) {
        return cartMapper.countByUserId(userId);
    }
}
