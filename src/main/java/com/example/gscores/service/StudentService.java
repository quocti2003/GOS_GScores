package com.example.gscores.service;

import com.example.gscores.dto.StudentScoreDTO;
import com.example.gscores.dto.TopStudentDTO;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    Optional<StudentScoreDTO> getScoreBySbd(String sbd);
    List<TopStudentDTO> getTop10GroupA();
}