package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.TermProducts;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<TermProducts> {

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL ORDER BY sales DESC LIMIT #{limit}")
    List<TermProducts> findHotProducts(@Param("limit") int limit);

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL AND category_id = #{categoryId} ORDER BY sales DESC LIMIT #{limit}")
    List<TermProducts> findByCategory(@Param("categoryId") Long categoryId, @Param("limit") int limit);

    @Select("SELECT DISTINCT category_id FROM term_products WHERE deleted_at IS NULL")
    List<Long> findAllCategoryIds();

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL ORDER BY sales DESC, sort ASC LIMIT #{limit}")
    List<TermProducts> findTopRatedProducts(@Param("limit") int limit);

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL ORDER BY RAND() LIMIT #{limit}")
    List<TermProducts> findRandomProducts(@Param("limit") int limit);

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL AND is_hot = 1 ORDER BY sales DESC LIMIT #{limit}")
    List<TermProducts> findHotFlagProducts(@Param("limit") int limit);

    @Select("SELECT * FROM term_products WHERE deleted_at IS NULL AND is_new = 1 ORDER BY created_at DESC LIMIT #{limit}")
    List<TermProducts> findNewProducts(@Param("limit") int limit);
}