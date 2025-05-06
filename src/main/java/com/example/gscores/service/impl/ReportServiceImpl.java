package com.example.gscores.service.impl;

import com.example.gscores.dto.ScoreDistributionDTO;
import com.example.gscores.model.ScoreCategory;
import com.example.gscores.model.Student;
import com.example.gscores.model.Subject;
import com.example.gscores.repository.StudentRepository;
import com.example.gscores.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final StudentRepository studentRepository;

    public ReportServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<ScoreDistributionDTO> getScoreDistributions() {
        // Gọi aggregation từ repository
        List<ScoreDistributionDTO> distributions = studentRepository.getScoreDistributions(
                Subject.TOAN.getDisplayName(),
                Subject.NGU_VAN.getDisplayName(),
                Subject.NGOAI_NGU.getDisplayName(),
                Subject.VAT_LI.getDisplayName(),
                Subject.HOA_HOC.getDisplayName(),
                Subject.SINH_HOC.getDisplayName(),
                Subject.LICH_SU.getDisplayName(),
                Subject.DIA_LI.getDisplayName(),
                Subject.GDCD.getDisplayName()
        );

        // In log cho từng môn học
        for (ScoreDistributionDTO dto : distributions) {
            String subject = dto.getSubject();
            Map<String, Long> distribution = dto.getDistribution();
            logger.debug("Phân phối điểm cho môn {}:", subject);
            distribution.forEach((category, count) -> {
                logger.debug("  - {}: {} học sinh", category, count);
            });
        }

        return distributions;
    }
}