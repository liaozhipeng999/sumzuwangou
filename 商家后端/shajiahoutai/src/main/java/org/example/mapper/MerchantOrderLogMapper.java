package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.MerchantOrderLog;

import java.util.List;

public interface MerchantOrderLogMapper extends BaseMapper<MerchantOrderLog> {

    List<MerchantOrderLog> findLatestByOrderNo(@Param("orderNo") String orderNo);
}
