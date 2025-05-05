package com.example.gscores.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ScoreDistributionDTO {
    private String subject;
    private Map<String, Long> distribution;
}