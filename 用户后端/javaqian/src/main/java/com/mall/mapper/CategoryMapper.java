package com.mall.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CategoryMapper {

    @Select("SELECT id, category_name, icon, sort FROM term_product_categories WHERE parent_id = 0 AND status = 1 ORDER BY sort")
    List<Map<String, Object>> findMainCategories();

    @Select("SELECT id, category_name, icon, sort FROM term_product_categories WHERE parent_id = #{parentId} AND status = 1 ORDER BY sort")
    List<Map<String, Object>> findSubCategories(@Param("parentId") Long parentId);

    @Select("SELECT c.id, c.category_name, c.icon, COUNT(p.id) as product_count " +
            "FROM term_product_categories c LEFT JOIN term_products p ON c.id = p.category_id " +
            "WHERE c.parent_id = #{parentId} AND c.status = 1 AND p.deleted_at IS NULL " +
            "GROUP BY c.id ORDER BY c.sort")
    List<Map<String, Object>> findSubCategoriesWithProductCount(@Param("parentId") Long parentId);

    @Select("SELECT id, category_name, icon, sort FROM term_product_categories WHERE parent_id = #{parentId} AND status = 1 ORDER BY sort")
    List<Map<String, Object>> findThirdLevelCategories(@Param("parentId") Long parentId);

    @Select("SELECT c.id, c.category_name, c.icon, COUNT(p.id) as product_count " +
            "FROM term_product_categories c LEFT JOIN term_products p ON c.id = p.category_id " +
            "WHERE c.parent_id = #{parentId} AND c.status = 1 AND p.deleted_at IS NULL " +
            "GROUP BY c.id ORDER BY c.sort")
    List<Map<String, Object>> findThirdLevelCategoriesWithProductCount(@Param("parentId") Long parentId);

    @Select("SELECT id, category_name, parent_id FROM term_product_categories WHERE id = #{id} AND status = 1")
    Map<String, Object> findCategoryById(@Param("id") Long id);
}
