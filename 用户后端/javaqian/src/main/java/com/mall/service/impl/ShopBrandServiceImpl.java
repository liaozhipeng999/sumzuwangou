package com.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mall.dto.BrandInfoDTO;
import com.mall.dto.ShopInfoDTO;
import com.mall.entity.Merchant;
import com.mall.entity.MerchantBrandInfo;
import com.mall.mapper.MerchantBrandInfoMapper;
import com.mall.mapper.MerchantMapper;
import com.mall.service.ShopBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShopBrandServiceImpl implements ShopBrandService {

    private final MerchantMapper merchantMapper;
    private final MerchantBrandInfoMapper brandInfoMapper;

    @Override
    public ShopInfoDTO getShopInfo(Long shopId) {
        Merchant merchant = merchantMapper.selectById(shopId);
        if (merchant == null) {
            return null;
        }

        MerchantBrandInfo brandInfo = brandInfoMapper.selectOne(
            new QueryWrapper<MerchantBrandInfo>().eq("merchant_id", shopId)
        );

        ShopInfoDTO dto = new ShopInfoDTO();
        dto.setId(shopId);
        dto.setName(merchant.getMerchantName());
        dto.setLogo(merchant.getMerchantLogo());
        dto.setLevel(getLevelText(merchant.getMerchantLevel()));

        if (brandInfo != null) {
            dto.setTotalSales(parseSales(brandInfo.getTotalSales()));
            dto.setRecentGoodReviews(parseReviewsDouble(brandInfo.getGoodReviewCount()));
            dto.setRecentOrders(parseReviewsDouble(brandInfo.getRecentReviewCount()));
            dto.setReviewers(parseReviewers(brandInfo.getReviewerCount()));
            dto.setGuarantee(brandInfo.getGuaranteeTags());
            dto.setOperator(brandInfo.getBrandName() + "专卖店");
        } else {
            dto.setTotalSales(100);
            dto.setRecentGoodReviews(1.5);
            dto.setRecentOrders(2.0);
            dto.setReviewers(500);
            dto.setGuarantee("100%正品 · 假一赔十");
            dto.setOperator("官方旗舰店");
        }

        return dto;
    }

    @Override
    public BrandInfoDTO getBrandInfo(Long brandId) {
        MerchantBrandInfo brandInfo = brandInfoMapper.selectById(brandId);
        if (brandInfo == null) {
            return null;
        }

        BrandInfoDTO dto = new BrandInfoDTO();
        dto.setId(brandId);
        dto.setName(brandInfo.getBrandName());
        dto.setSince("始于" + brandInfo.getEstablishedYear() + "年");
        dto.setLogo(brandInfo.getBrandLogo());
        dto.setIntroduction(buildIntroduction(brandInfo));
        
        dto.setFeatures(Arrays.asList(
            new BrandInfoDTO.FeatureItem() {{ setLabel("材质"); setValue(brandInfo.getMaterialDesc()); }},
            new BrandInfoDTO.FeatureItem() {{ setLabel("性能"); setValue(brandInfo.getPerformanceDesc()); }},
            new BrandInfoDTO.FeatureItem() {{ setLabel("研发"); setValue(brandInfo.getRdDesc()); }}
        ));

        return dto;
    }

    private String buildIntroduction(MerchantBrandInfo brandInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>");
        sb.append(brandInfo.getBrandStory());
        sb.append("</p>");
        return sb.toString();
    }

    private String getLevelText(Integer level) {
        if (level == null) return "普通店铺";
        Map<Integer, String> levelMap = new HashMap<>();
        levelMap.put(1, "普通店铺");
        levelMap.put(2, "铜牌店铺");
        levelMap.put(3, "银牌店铺");
        levelMap.put(4, "金牌店铺");
        levelMap.put(5, "钻石店铺");
        return levelMap.getOrDefault(level, "普通店铺");
    }

    private Integer parseSales(String sales) {
        if (sales == null || sales.isEmpty()) return 100;
        String numStr = sales.replace("万", "").replace("+", "").trim();
        try {
            return Integer.parseInt(numStr);
        } catch (Exception e) {
            return 100;
        }
    }

    private Double parseReviewsDouble(String reviews) {
        if (reviews == null || reviews.isEmpty()) return 1.0;
        String numStr = reviews.replace("万", "").replace("条", "").replace("件", "").trim();
        try {
            return Double.parseDouble(numStr);
        } catch (Exception e) {
            return 1.0;
        }
    }

    private Integer parseReviewers(String reviewers) {
        if (reviewers == null || reviewers.isEmpty()) return 500;
        String numStr = reviewers.replace("人", "").replace("%", "").trim();
        if (numStr.contains("正品")) return 500;
        try {
            return Integer.parseInt(numStr);
        } catch (Exception e) {
            return 500;
        }
    }
}
