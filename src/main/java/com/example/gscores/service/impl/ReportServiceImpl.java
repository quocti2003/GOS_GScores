package com.example.gscores.service.impl;

import com.example.gscores.dto.ScoreDistributionDTO;
import com.example.gscores.model.ScoreCategory;
import com.example.gscores.model.Student;
import com.example.gscores.model.Subject;
import com.example.gscores.repository.StudentRepository;
import com.example.gscores.service.ReportService;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final StudentRepository studentRepository;

    public ReportServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<ScoreDistributionDTO> getScoreDistributions() {
        return Arrays.stream(Subject.values()).map(subject -> {
            ScoreDistributionDTO dto = new ScoreDistributionDTO();
            dto.setSubject(subject.getDisplayName());
            Map<String, Long> distribution = new HashMap<>();
            for (ScoreCategory category : ScoreCategory.values()) {
                distribution.put(category.getLabel(), countStudentsInCategory(subject, category));
            }
            dto.setDistribution(distribution);
            return dto;
        }).collect(Collectors.toList());
    }

    private long countStudentsInCategory(Subject subject, ScoreCategory category) {
        return studentRepository.findAll().stream()
                .map(student -> {
                    try {
                        Field field = Student.class.getDeclaredField(subject.getFieldName());
                        field.setAccessible(true);
                        Double score = (Double) field.get(student);
                        return score != null && category.matches(score) ? 1 : 0;
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        return 0;
                    }
                })
                .mapToLong(Integer::longValue)
                .sum();
    }
}