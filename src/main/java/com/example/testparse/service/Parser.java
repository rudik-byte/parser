package com.example.testparse.service;

import com.example.testparse.model.Product;

import java.util.List;

public interface Parser {
    List<Product> parse(String url);
}
