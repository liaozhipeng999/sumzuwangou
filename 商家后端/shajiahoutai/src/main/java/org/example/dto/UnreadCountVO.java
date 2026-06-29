package org.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class UnreadCountVO {

    private Integer total;
    private List<ShopUnread> shops;

    /** 商家视角按用户分组 */
    private List<UserUnread> users;

    @Data
    public static class ShopUnread {
        private Long shopId;
        private Integer unreadCount;
    }

    @Data
    public static class UserUnread {
        private Long userId;
        private Integer unreadCount;
    }
}
