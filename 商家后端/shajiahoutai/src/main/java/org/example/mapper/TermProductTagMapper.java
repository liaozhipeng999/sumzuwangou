package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.entity.TermProductTag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface TermProductTagMapper extends BaseMapper<TermProductTag> {

    @Delete("DELETE FROM term_product_tags WHERE product_id = #{productId}")
    void deleteByProductId(@Param("productId") Long productId);

    List<TermProductTag> selectByProductId(@Param("productId") Long productId);
}