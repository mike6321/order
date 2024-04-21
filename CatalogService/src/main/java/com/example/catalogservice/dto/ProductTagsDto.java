package com.example.catalogservice.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ProductTagsDto {

    private Long productId;
    private List<String> tags;

}
