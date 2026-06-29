package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.TermProducts;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper extends BaseMapper<TermProducts> {

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL AND main_image IS NOT NULL AND main_image != '' ORDER BY sales DESC LIMIT #{limit}")
    List<TermProducts> findHotProducts(@Param("limit") int limit);

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL AND main_image IS NOT NULL AND main_image != '' ORDER BY sales DESC LIMIT #{pageSize} OFFSET #{offset}")
    List<TermProducts> findHotProductsPaged(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT * FROM term_products WHERE status = 1 AND deleted_at IS NULL AND category_id = #{categoryId} AND main_image IS NOT NULL AND main_image != '' ORDER BY sales DESC LIMIT #{limit}")
    List<TermProducts> findByCategory(@Param("categoryId") Long categoryId, @Param("limit") int limit);

    @Select("SELECT DISTINCT category_id FROM term_products WHERE deleted_at IS NULL")
    List<Long> findAllCategoryIds();

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL AND main_image IS NOT NULL AND main_image != '' ORDER BY sales DESC, sort ASC LIMIT #{limit}")
    List<TermProducts> findTopRatedProducts(@Param("limit") int limit);

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL AND main_image IS NOT NULL AND main_image != '' ORDER BY RAND() LIMIT #{limit}")
    List<TermProducts> findRandomProducts(@Param("limit") int limit);

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL AND main_image IS NOT NULL AND main_image != '' AND is_hot = 1 ORDER BY sales DESC LIMIT #{limit}")
    List<TermProducts> findHotFlagProducts(@Param("limit") int limit);

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL AND main_image IS NOT NULL AND main_image != '' AND is_new = 1 ORDER BY created_at DESC LIMIT #{limit}")
    List<TermProducts> findNewProducts(@Param("limit") int limit);

    @Select("SELECT p.* FROM term_products p " +
            "JOIN term_product_categories c ON p.category_id = c.id " +
            "WHERE p.deleted_at IS NULL AND c.parent_id = #{mainCategoryId} " +
            "ORDER BY p.sales DESC LIMIT #{limit}")
    List<Map<String, Object>> findProductsByMainCategory(@Param("mainCategoryId") Long mainCategoryId, @Param("limit") int limit);

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL AND main_image IS NOT NULL AND main_image != '' AND product_name LIKE CONCAT('%', #{keyword}, '%') ORDER BY sales DESC LIMIT #{limit}")
    List<TermProducts> searchByName(@Param("keyword") String keyword, @Param("limit") int limit);

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL AND main_image IS NOT NULL AND main_image != '' AND product_name LIKE CONCAT('%', #{keyword}, '%') ORDER BY ${sortBy} ${sortOrder} LIMIT #{pageSize} OFFSET #{offset}")
    List<TermProducts> searchByNamePaged(@Param("keyword") String keyword, @Param("offset") int offset, @Param("pageSize") int pageSize, @Param("sortBy") String sortBy, @Param("sortOrder") String sortOrder);

    @Select("SELECT COUNT(*) FROM term_products WHERE deleted_at IS NULL AND main_image IS NOT NULL AND main_image != '' AND product_name LIKE CONCAT('%', #{keyword}, '%')")
    int countByName(@Param("keyword") String keyword);

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL AND main_image IS NOT NULL AND main_image != '' AND category_id = #{categoryId} AND product_name LIKE CONCAT('%', #{keyword}, '%') ORDER BY ${sortBy} ${sortOrder} LIMIT #{pageSize} OFFSET #{offset}")
    List<TermProducts> searchByNameAndCategory(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, @Param("offset") int offset, @Param("pageSize") int pageSize, @Param("sortBy") String sortBy, @Param("sortOrder") String sortOrder);

    @Select("SELECT COUNT(*) FROM term_products WHERE deleted_at IS NULL AND main_image IS NOT NULL AND main_image != '' AND category_id = #{categoryId} AND product_name LIKE CONCAT('%', #{keyword}, '%')")
    int countByNameAndCategory(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL AND main_image IS NOT NULL AND main_image != '' ORDER BY sales DESC LIMIT #{limit}")
    List<TermProducts> findProductsBySales(@Param("limit") int limit);

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL AND main_image IS NOT NULL AND main_image != '' AND " +
            "(category_id = #{level2Id} OR category_id IN (SELECT id FROM term_product_categories WHERE parent_id = #{level2Id} AND deleted_at IS NULL)) " +
            "ORDER BY sales DESC LIMIT #{limit}")
    List<TermProducts> findByLevel2CategoryIncludeChildren(@Param("level2Id") Long level2Id, @Param("limit") int limit);
}