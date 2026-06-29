package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.HistoryRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface HistoryRecordMapper extends BaseMapper<HistoryRecord> {

    @Select("SELECT * FROM history_records WHERE user_id = #{userId} AND product_id = #{productId} LIMIT 1")
    HistoryRecord findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Update("UPDATE history_records SET browse_time = #{browseTime}, browse_count = browse_count + #{browseCount}, updated_at = NOW() WHERE user_id = #{userId} AND product_id = #{productId}")
    int updateBrowseCount(@Param("userId") Long userId, @Param("productId") Long productId, 
                         @Param("browseTime") Long browseTime, @Param("browseCount") Integer browseCount);

    @Select("SELECT hr.*, p.sales, p.brief FROM history_records hr LEFT JOIN term_products p ON hr.product_id = p.id WHERE hr.user_id = #{userId} ORDER BY hr.browse_time DESC LIMIT #{offset}, #{pageSize}")
    List<Map<String, Object>> findByUserIdWithProduct(@Param("userId") Long userId, 
                                                      @Param("offset") int offset, 
                                                      @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM history_records WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);

    @Delete("DELETE FROM history_records WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);
}