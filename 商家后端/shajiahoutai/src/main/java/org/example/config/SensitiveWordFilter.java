package org.example.config;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 敏感词过滤器
 * 使用 DFA（确定性有限自动机）算法实现高效敏感词检测
 */
@Component
public class SensitiveWordFilter {

    private static final Map<String, Object> sensitiveWordMap = new HashMap<>();

    // 默认敏感词列表（可根据需要扩展或从数据库加载）
    private static final String[] DEFAULT_SENSITIVE_WORDS = {
        "色情", "赌博", "毒品", "枪支", "暴力", "诈骗", "传销",
        "假货", "仿冒", "代购", "刷单", "套现", "洗钱",
        "政治敏感词1", "政治敏感词2"
    };

    @PostConstruct
    public void init() {
        addSensitiveWords(Arrays.asList(DEFAULT_SENSITIVE_WORDS));
    }

    /**
     * 批量添加敏感词
     */
    public void addSensitiveWords(List<String> words) {
        for (String word : words) {
            addSensitiveWord(word);
        }
    }

    /**
     * 添加单个敏感词
     */
    private void addSensitiveWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            return;
        }
        Map<String, Object> currentMap = sensitiveWordMap;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            String key = String.valueOf(c);
            Map<String, Object> subMap = (Map<String, Object>) currentMap.get(key);
            if (subMap == null) {
                subMap = new HashMap<>();
                currentMap.put(key, subMap);
            }
            currentMap = subMap;
            if (i == word.length() - 1) {
                currentMap.put("isEnd", "Y");
            }
        }
    }

    /**
     * 检测文本是否包含敏感词
     * @return 包含的敏感词列表
     */
    public List<String> findSensitiveWords(String text) {
        List<String> foundWords = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return foundWords;
        }
        for (int i = 0; i < text.length(); i++) {
            int length = checkSensitiveWord(text, i);
            if (length > 0) {
                foundWords.add(text.substring(i, i + length));
                i += length - 1;
            }
        }
        return foundWords;
    }

    /**
     * 检查是否包含敏感词
     */
    public boolean containsSensitiveWord(String text) {
        return !findSensitiveWords(text).isEmpty();
    }

    /**
     * 过滤敏感词，用 * 替换
     */
    public String filter(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        StringBuilder result = new StringBuilder(text);
        for (int i = 0; i < text.length(); i++) {
            int length = checkSensitiveWord(text, i);
            if (length > 0) {
                for (int j = 0; j < length; j++) {
                    result.setCharAt(i + j, '*');
                }
                i += length - 1;
            }
        }
        return result.toString();
    }

    /**
     * 从指定位置开始检查敏感词长度
     */
    private int checkSensitiveWord(String text, int beginIndex) {
        Map<String, Object> currentMap = sensitiveWordMap;
        int length = 0;
        for (int i = beginIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            String key = String.valueOf(c);
            Map<String, Object> subMap = (Map<String, Object>) currentMap.get(key);
            if (subMap == null) {
                break;
            }
            length++;
            currentMap = subMap;
            if ("Y".equals(subMap.get("isEnd"))) {
                return length;
            }
        }
        return 0;
    }
}
