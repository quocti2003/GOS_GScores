package com.example.gscores.config;

import com.example.gscores.repository.StudentRepository;
import com.example.gscores.service.CsvImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private CsvImportService csvImportService;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public void run(String... args) {
        if (studentRepository.count() == 0) {
            logger.info("Collection 'students' is empty. Starting CSV import...");
            csvImportService.importCsv();
            logger.info("CSV import completed successfully.");
        } else {
            logger.info("Database already seeded. Skipping CSV import.");
        }
    }
}