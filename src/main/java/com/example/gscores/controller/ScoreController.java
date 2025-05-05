package com.example.gscores.controller;

import com.example.gscores.dto.ScoreDistributionDTO;
import com.example.gscores.dto.StudentScoreDTO;
import com.example.gscores.dto.TopStudentDTO;
import com.example.gscores.service.ReportService;
import com.example.gscores.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ReportService reportService;

    @GetMapping("/{id}")
    public ResponseEntity<StudentScoreDTO> getStudentScores(@PathVariable String id) {
        return studentService.getScoreBySbd(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/statistics")
    public List<ScoreDistributionDTO> getStatistics() {
        return reportService.getScoreDistributions();
    }

    @GetMapping("/top10")
    public List<TopStudentDTO> getTop10GroupA() {
        return studentService.getTop10GroupA();
    }
}