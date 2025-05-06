package com.example.gscores.service.impl;

import com.example.gscores.model.Student;
import com.example.gscores.service.CsvImportService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvImportServiceImpl implements CsvImportService {

    private static final Logger logger = LoggerFactory.getLogger(CsvImportServiceImpl.class);
    private static final int BATCH_SIZE = 10_000; // Kích thước batch: 10,000 bản ghi

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void importCsv() {
        try {
            // Đọc file CSV và lưu theo batch
            List<Student> batch = new ArrayList<>(BATCH_SIZE);
            try (CSVReader reader = new CSVReader(new InputStreamReader(
                    new ClassPathResource("data/diem_thi_thpt_2024.csv").getInputStream()))) {
                reader.readNext(); // Bỏ qua dòng tiêu đề
                String[] line;
                int lineCount = 0;

                while ((line = reader.readNext()) != null) {
                    Student student = parseLineToStudent(line);
                    batch.add(student);
                    lineCount++;

                    // Lưu batch khi đạt kích thước BATCH_SIZE
                    if (batch.size() >= BATCH_SIZE) {
                        mongoTemplate.insert(batch, Student.class);
                        logger.info("Inserted batch of {} records (total: {}).", BATCH_SIZE, lineCount);
                        batch.clear(); // Giải phóng bộ nhớ
                    }
                }

                // Lưu batch cuối nếu còn dữ liệu
                if (!batch.isEmpty()) {
                    mongoTemplate.insert(batch, Student.class);
                    logger.info("Inserted final batch of {} records.", batch.size());
                }

                logger.info("Total records imported: {}.", lineCount);
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Failed to import CSV", e);
            throw new RuntimeException("Không thể import file CSV", e);
        }
    }

    private Student parseLineToStudent(String[] line) {
        Student student = new Student();
        // Chuẩn hóa SBD: Loại bỏ tiền tố 0 bằng cách parse thành số và chuyển lại thành chuỗi
        String sbd = String.valueOf(Integer.parseInt(line[0].trim()));
        student.setSbd(sbd);
        student.setToan(parseDouble(line[1]));
        student.setNguVan(parseDouble(line[2]));
        student.setNgoaiNgu(parseDouble(line[3]));
        student.setVatLi(parseDouble(line[4]));
        student.setHoaHoc(parseDouble(line[5]));
        student.setSinhHoc(parseDouble(line[6]));
        student.setLichSu(parseDouble(line[7]));
        student.setDiaLi(parseDouble(line[8]));
        student.setGdcd(parseDouble(line[9]));
        student.setMaNgoaiNgu(line[10]);
        return student;
    }

    private Double parseDouble(String value) {
        return value.isEmpty() ? null : Double.parseDouble(value);
    }
}