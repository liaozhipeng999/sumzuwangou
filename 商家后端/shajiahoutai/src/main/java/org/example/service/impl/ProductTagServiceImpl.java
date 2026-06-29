package org.example.service.impl;

import org.example.entity.TermProductTag;
import org.example.mapper.TermProductTagMapper;
import org.example.service.ProductTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductTagServiceImpl implements ProductTagService {

    @Autowired
    private TermProductTagMapper termProductTagMapper;

    @Override
    @Transactional
    public void saveTags(Long productId, List<TermProductTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return;
        }
        
        deleteTagsByProductId(productId);
        
        for (TermProductTag tag : tags) {
            tag.setProductId(productId);
            termProductTagMapper.insert(tag);
        }
    }

    @Override
    public List<TermProductTag> getTagsByProductId(Long productId) {
        return termProductTagMapper.selectByProductId(productId);
    }

    @Override
    @Transactional
    public void deleteTagsByProductId(Long productId) {
        termProductTagMapper.deleteByProductId(productId);
    }
}