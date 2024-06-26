package com.example.orderservice.feign;

import com.example.orderservice.dto.DecreaseStockCountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "catalogClient", url = "${catalog}")
public interface CatalogClient {

    @GetMapping(value = "/catalog/products/{productId}")
    Map<String, Object> getProduct(@PathVariable("productId") Long productId);

    @PutMapping(value = "/catalog/products/{productId}/decreaseStockCount")
    void decreaseStockCount(@PathVariable("productId") Long productId, @RequestBody DecreaseStockCountDto dto);

}
