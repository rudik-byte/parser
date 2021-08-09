package com.example.testparse.model;


import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class Product {
    private String productName;
    private String brandName;
    private List<String> color;
    private String price;
    private List<String> productSize;
    private String articleId;
}
