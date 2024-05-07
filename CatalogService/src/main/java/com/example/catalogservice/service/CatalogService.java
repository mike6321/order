package com.example.catalogservice.service;

import blackfriday.protobuf.EdaMessage;
import com.example.catalogservice.casandra.entity.Product;
import com.example.catalogservice.casandra.repository.ProductRepository;
import com.example.catalogservice.mysql.entity.SellerProduct;
import com.example.catalogservice.mysql.repository.SellerProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CatalogService {

    private final SellerProductRepository sellerProductRepository;
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public Product registerProduct(
        Long sellerId,
        String name,
        String description,
        Long price,
        Long stockCount,
        List<String> tags
    ) {
        SellerProduct sellerProduct = new SellerProduct(sellerId);
        sellerProductRepository.save(sellerProduct);

        Product product = new Product(
                sellerProduct.getId(),
                sellerId,
                name,
                description,
                price,
                stockCount,
                tags
        );

        var message = EdaMessage.ProductTags.newBuilder()
                .setProductId(product.getId())
                .addAllTags(product.getTags())
                .build();

        kafkaTemplate.send("product_tags_added", message.toByteArray());

//        searchClient.addTagCache(new ProductTagsDto(product.getId(), product.getTags()));

        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        productRepository.findById(productId)
                .ifPresent(product -> {
                    var message = EdaMessage.ProductTags.newBuilder()
                            .setProductId(product.getId())
                            .addAllTags(product.getTags())
                            .build();

                    kafkaTemplate.send("product_tags_removed", message.toByteArray());
                    // searchClient.removeTagCache(new ProductTagsDto(value.getId(), value.getTags()));
                    }
                );

        productRepository.deleteById(productId);
        sellerProductRepository.deleteById(productId);
    }

    public List<Product> getProductsBySellerId(Long sellerId) {
        List<SellerProduct> sellerProducts = sellerProductRepository.findBySellerId(sellerId);
        List<Product> products = new ArrayList<>();

        sellerProducts.forEach(sellerProduct ->
                productRepository.findById(sellerProduct.getId())
                .ifPresent(products::add));

        return products;
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow();
    }

    public Product decreaseStockCount(Long productId, Long decreaseCount) {
        var product = productRepository.findById(productId).orElseThrow();
        product.decreaseStockCount(decreaseCount);

        return productRepository.save(product);
    }

}
