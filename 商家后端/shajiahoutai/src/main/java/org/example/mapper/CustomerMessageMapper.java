package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.CustomerMessage;
import java.util.List;
import java.util.Map;

@Mapper
public interface CustomerMessageMapper extends BaseMapper<CustomerMessage> {

    /** 用户视角：按 userId 获取会话列表（每个店铺最近一条消息） */
    List<Map<String, Object>> getConversationsByUser(@Param("userId") Long userId);

    /** 商家视角：按 shopId 获取会话列表（每个用户最近一条消息） */
    List<Map<String, Object>> getConversationsByShop(@Param("shopId") Long shopId);

    /** 用户视角：未读消息总数 */
    int countUnreadByUser(@Param("userId") Long userId);

    /** 用户视角：按店铺分组的未读数 */
    List<Map<String, Object>> countUnreadByUserGroupByShop(@Param("userId") Long userId);

    /** 商家视角：未读消息总数 */
    int countUnreadByShop(@Param("shopId") Long shopId);

    /** 商家视角：按用户分组的未读数 */
    List<Map<String, Object>> countUnreadByShopGroupByUser(@Param("shopId") Long shopId);

    /** 用户视角：标记某店铺发给自己的消息已读 */
    int markAsReadByUser(@Param("userId") Long userId, @Param("shopId") Long shopId);

    /** 商家视角：标记某用户发给自己的消息已读 */
    int markAsReadByShop(@Param("shopId") Long shopId, @Param("userId") Long userId);

    /** 清空用户与某店铺的聊天记录 */
    int clearMessages(@Param("userId") Long userId, @Param("shopId") Long shopId);
}
