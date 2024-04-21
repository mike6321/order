package com.example.searchservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SearchService {

    private final StringRedisTemplate redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public void addTagCache(Long productId, List<String> tags) {
        tags.forEach(tag -> stringRedisTemplate.opsForSet()
                .add(tag, String.valueOf(productId)));
    }

    public void removeTagCache(Long productId, List<String> tags) {
        tags.forEach(tag -> stringRedisTemplate.opsForSet()
                .remove(tag, String.valueOf(productId)));
    }

    public List<Long> getProductIdsByTag(String tag) {
        var ops = redisTemplate.opsForSet();

        var values = ops.members(tag);
        if (values != null) {
            return values.stream()
                    .map(Long::parseLong)
                    .toList();
        }

        return Collections.emptyList();
    }

}
