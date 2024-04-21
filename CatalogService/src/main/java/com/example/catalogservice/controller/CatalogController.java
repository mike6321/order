package com.example.catalogservice.controller;

import com.example.catalogservice.casandra.entity.Product;
import com.example.catalogservice.dto.DecreaseStockCountDto;
import com.example.catalogservice.dto.RegisterProductDto;
import com.example.catalogservice.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/catalog/")
@RestController
public class CatalogController {

    private final CatalogService catalogService;

    @PostMapping("products")
    public Product registerProduct(@RequestBody RegisterProductDto dto) {
        return catalogService.registerProduct(
            dto.sellerId,
            dto.name,
            dto.description,
            dto.price,
            dto.stockCount,
            dto.tags
        );
    }

    @DeleteMapping("products/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        catalogService.deleteProduct(productId);
    }

    @GetMapping("products/{productId}")
    public Product getProductById(@PathVariable Long productId) throws Exception {
        return catalogService.getProductById(productId);
    }

    @GetMapping("sellers/{sellerId}/products")
    public List<Product> getProductsBySellerId (@PathVariable Long sellerId) throws Exception {
        return catalogService.getProductsBySellerId(sellerId);
    }

    @PutMapping("products/{productId}/decreaseStockCount")
    public Product decreaseStockCount(@PathVariable Long productId, @RequestBody DecreaseStockCountDto dto) {
        return catalogService.decreaseStockCount(productId, dto.decreaseCount);
    }
}
