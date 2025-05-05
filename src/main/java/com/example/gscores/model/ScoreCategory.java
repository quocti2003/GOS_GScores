package com.example.gscores.model;

public enum ScoreCategory {
    BELOW_4("<4") {
        @Override
        public boolean matches(double score) {
            return score < 4.0;
        }
    },
    BETWEEN_4_6("4-6") {
        @Override
        public boolean matches(double score) {
            return score >= 4.0 && score < 6.0;
        }
    },
    BETWEEN_6_8("6-8") {
        @Override
        public boolean matches(double score) {
            return score >= 6.0 && score < 8.0;
        }
    },
    ABOVE_8(">=8") {
        @Override
        public boolean matches(double score) {
            return score >= 8.0;
        }
    };

    private final String label;

    ScoreCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public abstract boolean matches(double score);
}