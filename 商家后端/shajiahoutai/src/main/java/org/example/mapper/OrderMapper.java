package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.TermOrder;

import java.util.List;
import java.util.Map;

public interface OrderMapper extends BaseMapper<TermOrder> {

    List<Map<String, Object>> selectMerchantOrderPage(@Param("shopId") Long shopId,
                                                      @Param("status") String status,
                                                      @Param("keyword") String keyword,
                                                      @Param("startDate") String startDate,
                                                      @Param("endDate") String endDate,
                                                      @Param("offset") int offset,
                                                      @Param("limit") int limit);

    int countMerchantOrders(@Param("shopId") Long shopId,
                            @Param("status") String status,
                            @Param("keyword") String keyword,
                            @Param("startDate") String startDate,
                            @Param("endDate") String endDate);

    Map<String, Object> selectOrderDetailByNo(@Param("orderNo") String orderNo);

    List<Map<String, Object>> selectOrderItemsByOrderNo(@Param("orderNo") String orderNo,
                                                       @Param("shopId") Long shopId);

    Map<String, Object> selectOrderByNo(@Param("orderNo") String orderNo);

    int updateStatus(@Param("orderNo") String orderNo, @Param("status") int status);

    int updateShipInfo(@Param("orderNo") String orderNo,
                       @Param("express") String express,
                       @Param("trackingNo") String trackingNo,
                       @Param("remark") String remark);
}
