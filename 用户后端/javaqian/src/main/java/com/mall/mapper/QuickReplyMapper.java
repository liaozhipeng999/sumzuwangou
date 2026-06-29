package com.mall.mapper;

import com.mall.entity.QuickReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Options;

import java.util.List;

@Mapper
public interface QuickReplyMapper {

    @Select("SELECT id, shop_id as shopId, content, sort FROM quick_reply WHERE shop_id = #{shopId} ORDER BY sort ASC")
    List<QuickReply> findByShopId(@Param("shopId") Long shopId);

    @Insert("INSERT INTO quick_reply (shop_id, content, sort, created_at) VALUES (#{shopId}, #{content}, #{sort}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(QuickReply quickReply);

    @Update("UPDATE quick_reply SET content = #{content}, sort = #{sort} WHERE id = #{id}")
    int update(QuickReply quickReply);

    @Delete("DELETE FROM quick_reply WHERE id = #{id} AND shop_id = #{shopId}")
    int deleteById(@Param("id") Long id, @Param("shopId") Long shopId);
}