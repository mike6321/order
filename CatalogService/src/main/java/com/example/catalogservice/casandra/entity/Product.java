package com.example.catalogservice.casandra.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Getter
@AllArgsConstructor
@Table
public class Product {

    @PrimaryKey
    private Long id;

    @Column
    private Long sellerId;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Long price;

    @Column
    private Long stockCount;

    @Column
    private List<String> tags;

    public void decreaseStockCount(Long decreaseCount) {
        this.stockCount = this.stockCount - decreaseCount;
    }

}
