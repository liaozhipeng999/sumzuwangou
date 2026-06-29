package org.example.service;

import org.example.dto.ProductAddDTO;
import org.example.dto.ProductListDTO;
import org.example.dto.ProductUpdateDTO;
import org.example.entity.TermProduct;
import java.util.List;

public interface ProductService {
    
    /**
     * 商家上架商品
     */
    TermProduct addProduct(ProductAddDTO productAddDTO);
    
    /**
     * 根据ID获取商品信息
     */
    TermProduct getById(Long id);
    
    /**
     * 根据商家ID获取商品列表（含标签）
     */
    List<ProductListDTO> getProductsByMerchantId(Long merchantId);
    
    /**
     * 更新商品信息
     */
    TermProduct updateProduct(ProductUpdateDTO productUpdateDTO);
    
    /**
     * 商品下架
     */
    TermProduct downProduct(Long id);
    
    /**
     * 商品上架（恢复上架）
     */
    TermProduct upProduct(Long id);
    
    /**
     * 删除商品（物理删除）
     */
    void deleteProduct(Long id);
}