package com.example.testparse;

import com.example.testparse.model.Product;
import com.example.testparse.service.impl.ParserImpl;

import java.util.List;

import static com.example.testparse.service.impl.ParserImpl.writeToFile;

public class TestParseApplication {

    public static void main(String[] args) {
        ParserImpl parser = new ParserImpl();
        List<Product> items = parser.parse("https://www.aboutyou.de/maenner/bekleidung");

        writeToFile(items);
    }
}
