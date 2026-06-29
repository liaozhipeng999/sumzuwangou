package com.mall.service;

import com.mall.entity.Message;

import java.util.List;
import java.util.Map;

public interface MessageService {

    /**
     * 获取消息分类概览
     * @param userId 用户ID
     * @return 每个分类的：类型、标签、图标、未读数、最新消息
     */
    List<Map<String, Object>> getMessageSummary(Long userId);

    /**
     * 获取总未读数
     * @param userId 用户ID
     * @return 未读消息总数
     */
    int getUnreadCount(Long userId);

    /**
     * 获取某分类的消息列表（分页）
     * @param userId 用户ID
     * @param type 消息类型
     * @param page 页码
     * @param pageSize 每页数量
     * @return 消息列表和分页信息
     */
    Map<String, Object> getMessagesByType(Long userId, String type, int page, int pageSize);

    /**
     * 标记单条消息已读
     * @param id 消息ID
     * @param userId 用户ID（验证权限）
     * @return 是否成功
     */
    boolean markAsRead(Long id, Long userId);

    /**
     * 标记某分类全部已读
     * @param userId 用户ID
     * @param type 消息类型
     * @return 是否成功
     */
    boolean markAllAsReadByType(Long userId, String type);

    /**
     * 标记用户所有消息已读
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAllAsRead(Long userId);

    /**
     * 删除单条消息（软删除）
     * @param id 消息ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteMessage(Long id, Long userId);

    /**
     * 批量删除某类型消息（软删除）
     * @param userId 用户ID
     * @param type 消息类型
     * @return 删除数量
     */
    int deleteMessagesByType(Long userId, String type);
}
