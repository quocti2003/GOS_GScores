package com.example.gscores.model;

public enum Subject {
    TOAN("Toán", "toan"),
    NGU_VAN("Ngữ văn", "nguVan"),
    NGOAI_NGU("Ngoại ngữ", "ngoaiNgu"),
    VAT_LI("Vật lý", "vatLi"),
    HOA_HOC("Hóa học", "hoaHoc"),
    SINH_HOC("Sinh học", "sinhHoc"),
    LICH_SU("Lịch sử", "lichSu"),
    DIA_LI("Địa lý", "diaLi"),
    GDCD("Giáo dục công dân", "gdcd");

    private final String displayName;
    private final String fieldName;

    Subject(String displayName, String fieldName) {
        this.displayName = displayName;
        this.fieldName = fieldName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFieldName() {
        return fieldName;
    }
}