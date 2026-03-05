package com.milktea.bot.service;

import com.milktea.bot.entity.Product;
import com.milktea.bot.repository.ProductRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvInitService {

    private final ProductRepository productRepository;

    @PostConstruct
    public void initData() {
        if (productRepository.count() > 0) {
            log.info("Database already contains product data. Skipping CSV initialization.");
            return;
        }

        log.info("Starting CSV data initialization...");
        try (Reader reader = new InputStreamReader(new ClassPathResource("Menu.csv").getInputStream());
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

            List<String[]> records = csvReader.readAll();
            for (String[] record : records) {
                if (record.length >= 7) {
                    Product product = Product.builder()
                            .category(record[0].trim())
                            .itemId(record[1].trim())
                            .name(record[2].trim())
                            .description(record[3].trim())
                            .priceM(new BigDecimal(record[4].trim()))
                            .priceL(new BigDecimal(record[5].trim()))
                            .available(Boolean.parseBoolean(record[6].trim()))
                            .build();
                    productRepository.save(product);
                }
            }
            log.info("Successfully imported {} products from CSV.", records.size());
        } catch (Exception e) {
            log.error("Failed to parse Menu.csv", e);
        }
    }
}
