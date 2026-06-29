package org.example.service;

import org.example.entity.TermProductTag;
import java.util.List;

public interface ProductTagService {

    void saveTags(Long productId, List<TermProductTag> tags);

    List<TermProductTag> getTagsByProductId(Long productId);

    void deleteTagsByProductId(Long productId);
}