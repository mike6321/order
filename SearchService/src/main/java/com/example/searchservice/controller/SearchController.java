package com.example.searchservice.controller;

import com.example.searchservice.dto.ProductTagsDto;
import com.example.searchservice.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/search/")
@RestController
public class SearchController {

    private final SearchService searchService;

    @PostMapping("addTagCache")
    public void addTagCache(@RequestBody ProductTagsDto dto) {
        searchService.addTagCache(dto.productId, dto.tags);
    }

    @DeleteMapping("removeTagCache")
    public void removeTagCache(@RequestBody ProductTagsDto dto) {
        searchService.removeTagCache(dto.productId, dto.tags);
    }

    @GetMapping("tags/{tag}/productIds")
    public List<Long> getTagProductIds(@PathVariable String tag) {
        return searchService.getProductIdsByTag(tag);
    }

}
