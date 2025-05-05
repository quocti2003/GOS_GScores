package com.example.gscores.service.impl;

import com.example.gscores.dto.StudentScoreDTO;
import com.example.gscores.dto.TopStudentDTO;
import com.example.gscores.model.Student;
import com.example.gscores.repository.StudentRepository;
import com.example.gscores.service.StudentService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final MongoTemplate mongoTemplate;

    public StudentServiceImpl(StudentRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void initIndexes() {
        mongoTemplate.indexOps(Student.class)
                .ensureIndex(new Index()
                        .on("toan", Sort.Direction.ASC)
                        .on("vatLi", Sort.Direction.ASC)
                        .on("hoaHoc", Sort.Direction.ASC));
    }

    @Override
    public Optional<StudentScoreDTO> getScoreBySbd(String id) {
        Optional<Student> studentOptional = repository.findById(id);
        if (studentOptional.isEmpty()) {
            return Optional.empty();
        }
        Student student = studentOptional.get();
        return Optional.of(toStudentScoreDTO(student));
    }

    @Override
    public List<TopStudentDTO> getTop10GroupA() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("toan").ne(null)
                        .and("vatLi").ne(null)
                        .and("hoaHoc").ne(null)),
                Aggregation.project()
                        .and("sbd").as("sbd")
                        .and("toan").as("toan")
                        .and("vatLi").as("vatLi")
                        .and("hoaHoc").as("hoaHoc")
                        .andExpression("toan + vatLi + hoaHoc").as("total"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "total")),
                Aggregation.limit(10)
        );

        AggregationResults<TopStudentDTO> results = mongoTemplate.aggregate(aggregation, Student.class, TopStudentDTO.class);
        return results.getMappedResults();
    }

    private StudentScoreDTO toStudentScoreDTO(Student student) {
        StudentScoreDTO dto = new StudentScoreDTO();
        dto.setSbd(student.getSbd());
        dto.setToan(student.getToan());
        dto.setNguVan(student.getNguVan());
        dto.setNgoaiNgu(student.getNgoaiNgu());
        dto.setMaNgoaiNgu(student.getMaNgoaiNgu());
        dto.setVatLi(student.getVatLi());
        dto.setHoaHoc(student.getHoaHoc());
        dto.setSinhHoc(student.getSinhHoc());
        dto.setLichSu(student.getLichSu());
        dto.setDiaLi(student.getDiaLi());
        dto.setGdcd(student.getGdcd());
        return dto;
    }
}