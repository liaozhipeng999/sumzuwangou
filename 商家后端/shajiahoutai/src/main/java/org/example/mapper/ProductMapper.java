package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.example.entity.TermProduct;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductMapper extends BaseMapper<TermProduct> {
    
    List<TermProduct> selectByMerchantId(Long merchantId);
}