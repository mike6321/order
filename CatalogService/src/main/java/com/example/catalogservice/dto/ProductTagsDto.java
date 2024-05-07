package com.example.catalogservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Deprecated
@Getter
@AllArgsConstructor
public class ProductTagsDto {

    private Long productId;
    private List<String> tags;

}
