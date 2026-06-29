package org.example.controller;

import org.example.common.Result;
import org.example.dto.ProductAddDTO;
import org.example.dto.ProductListDTO;
import org.example.dto.ProductUpdateDTO;
import org.example.entity.TermProduct;
import org.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 商家上架商品
     */
    @PostMapping("/add")
    public Result<TermProduct> addProduct(@Validated @RequestBody ProductAddDTO productAddDTO) {
        try {
            TermProduct product = productService.addProduct(productAddDTO);
            return Result.success("商品上架成功", product);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据ID获取商品信息
     */
    @GetMapping("/{id}")
    public Result<TermProduct> getProductById(@PathVariable Long id) {
        try {
            TermProduct product = productService.getById(id);
            if (product != null) {
                return Result.success(product);
            } else {
                return Result.error("商品不存在");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据商家ID获取商品列表（含标签）
     */
    @GetMapping("/list/{merchantId}")
    public Result<List<ProductListDTO>> getProductsByMerchantId(@PathVariable Long merchantId) {
        try {
            List<ProductListDTO> products = productService.getProductsByMerchantId(merchantId);
            return Result.success("查询成功", products);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新商品信息
     */
    @PutMapping("/update")
    public Result<TermProduct> updateProduct(@Validated @RequestBody ProductUpdateDTO productUpdateDTO) {
        try {
            TermProduct product = productService.updateProduct(productUpdateDTO);
            return Result.success("修改成功", product);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 商品下架
     */
    @PutMapping("/down/{id}")
    public Result<TermProduct> downProduct(@PathVariable Long id) {
        try {
            TermProduct product = productService.downProduct(id);
            return Result.success("商品下架成功", product);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 商品上架（恢复上架）
     */
    @PutMapping("/up/{id}")
    public Result<TermProduct> upProduct(@PathVariable Long id) {
        try {
            TermProduct product = productService.upProduct(id);
            return Result.success("商品上架成功", product);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除商品
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return Result.success("商品删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}