package com.example.testparse.service.impl;

import com.example.testparse.model.Product;
import com.example.testparse.service.Parser;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ParserImpl implements Parser {

    private static final String attribute = "data-test-id";

    public List<Product> parse(String url) {
        List<Product> products = new CopyOnWriteArrayList<>();
        int counterRequest = 0;
        int counterItems = 0;

        try {
            Document doc = Jsoup.connect(url).get();
            ++counterRequest;
            Elements elements = doc.getElementsByAttributeValue(attribute, "ProductTile").nextAll();
            counterItems = elements.size();
            for (Element element : elements) {
                String linkHref = element.attr("href");
                Document item = Jsoup.connect("https://www.aboutyou.de" + linkHref).get();

                Thread.sleep(1000);

                ++counterRequest;
                products.add(parseItem(item));
            }

        } catch (IOException ex) {
            System.out.println("Problems with connection");
        } catch (InterruptedException ex) {
            System.out.println("Unexpected interrupt");
        }

        System.out.println("Amount of triggered HTTP requests: " + counterRequest);
        System.out.println("Amount of extracted products: " + counterItems);

        return products;
    }

    private Product parseItem(Document item) {
        String productName = item.getElementsByAttributeValue(attribute, "ProductName").text();

        String brand = item.getElementsByAttributeValue(attribute, "BrandLogo").attr("alt");

        Elements colorEl = item.getElementsByAttributeValue(attribute, "ColorVariantColorInfo");

        List<String> colors = colorEl.stream().map(Element::text).collect(Collectors.toList());

        Element priceEl = item.getElementsByAttributeValue(attribute, "ProductPriceFormattedBasePrice").first();
        String price = priceEl == null
                ? item.getElementsByAttributeValue(attribute, "FormattedSalePrice").text() : priceEl.text();

        String articleID = item.getElementsByAttributeValue(attribute, "ArticleNumber")
                .text().replaceFirst("Art.-Nr. ", "");
        Elements sizeInfo = item.getElementsByAttributeValue(attribute, "SizeInfo");

        List<String> sizes = sizeInfo.stream()
                .map(Element::text)
                .collect(Collectors.toList());

        return Product.builder()
                .productName(productName)
                .brandName(brand)
                .color(colors)
                .price(price)
                .productSize(sizes)
                .articleId(articleID)
                .build();
    }

    public static void writeToFile(List<Product> items) {
        Gson gson = new Gson();
        String json = gson.toJson(items);

        try {
            FileWriter fileWriter = new FileWriter("result_of_parsing.txt");
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write(json);

            writer.close();
            fileWriter.close();
        } catch (IOException ex) {
            System.out.println("Unable to save file");
        }
    }
}
