package com.example.gscores.service;

import com.example.gscores.dto.ScoreDistributionDTO;

import java.util.List;

public interface ReportService {
    List<ScoreDistributionDTO> getScoreDistributions();
}