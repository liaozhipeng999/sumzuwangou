package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 获取用户某类型消息列表（按时间倒序）
     */
    @Select("SELECT * FROM messages WHERE user_id = #{userId} AND type = #{type} AND is_deleted = 0 ORDER BY created_at DESC")
    List<Message> findByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 获取用户未读消息数量
     */
    @Select("SELECT COUNT(*) FROM messages WHERE user_id = #{userId} AND is_read = 0 AND is_deleted = 0")
    int countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 获取用户某类型未读消息数量
     */
    @Select("SELECT COUNT(*) FROM messages WHERE user_id = #{userId} AND type = #{type} AND is_read = 0 AND is_deleted = 0")
    int countUnreadByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 标记单条消息已读
     */
    @Update("UPDATE messages SET is_read = 1, updated_at = NOW() WHERE id = #{id}")
    int markAsRead(@Param("id") Long id);

    /**
     * 标记用户某类型全部消息已读
     */
    @Update("UPDATE messages SET is_read = 1, updated_at = NOW() WHERE user_id = #{userId} AND type = #{type} AND is_read = 0 AND is_deleted = 0")
    int markAllAsReadByType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 获取每个消息类型的概览信息（最新一条消息 + 未读数）
     */
    @Select("SELECT type, COUNT(*) as total, SUM(CASE WHEN is_read = 0 THEN 1 ELSE 0 END) as unread_count " +
            "FROM messages WHERE user_id = #{userId} AND is_deleted = 0 GROUP BY type")
    List<Map<String, Object>> getMessageSummary(@Param("userId") Long userId);

    /**
     * 获取用户某类型最新一条消息
     */
    @Select("SELECT * FROM messages WHERE user_id = #{userId} AND type = #{type} AND is_deleted = 0 ORDER BY created_at DESC LIMIT 1")
    Message findLatestByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 分页获取用户某类型消息列表（SQL 层分页）
     */
    @Select("SELECT * FROM messages WHERE user_id = #{userId} AND type = #{type} AND is_deleted = 0 ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<Message> findByUserIdAndTypePaged(@Param("userId") Long userId, @Param("type") String type,
                                           @Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 获取用户某类型消息总数
     */
    @Select("SELECT COUNT(*) FROM messages WHERE user_id = #{userId} AND type = #{type} AND is_deleted = 0")
    int countByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 标记用户所有消息已读
     */
    @Update("UPDATE messages SET is_read = 1, updated_at = NOW() WHERE user_id = #{userId} AND is_read = 0 AND is_deleted = 0")
    int markAllAsRead(@Param("userId") Long userId);

    /**
     * 删除单条消息（验证归属）
     */
    @Update("UPDATE messages SET is_deleted = 1, updated_at = NOW() WHERE id = #{id} AND user_id = #{userId}")
    int softDelete(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 批量删除某类型消息
     */
    @Update("UPDATE messages SET is_deleted = 1, updated_at = NOW() WHERE user_id = #{userId} AND type = #{type} AND is_deleted = 0")
    int softDeleteByType(@Param("userId") Long userId, @Param("type") String type);
}
