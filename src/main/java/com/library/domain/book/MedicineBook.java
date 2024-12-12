package com.library.domain.book;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MedicineBook extends Book {
    private String medicalSpecialty;
    private String clinicalFocus;
    private String practiceArea;

    public MedicineBook() {
        setType("Medicine");
    }

    @Override
    public String displayInfo() {
        return String.format("""
            医学图书：《%s》
            作者：%s
            医学专业：%s
            临床方向：%s
            实践领域：%s
            库存：%d
            """,
            getTitle(), getAuthor(), 
            medicalSpecialty, clinicalFocus, 
            practiceArea, getStock()
        );
    }

    @Override
    public String getCategory() {
        return "Medicine";
    }

    @Override
    public void validate() {
        validateCommon();
        if (medicalSpecialty == null || medicalSpecialty.trim().isEmpty()) {
            throw new IllegalArgumentException("Medical specialty cannot be empty");
        }
        if (clinicalFocus == null || clinicalFocus.trim().isEmpty()) {
            throw new IllegalArgumentException("Clinical focus cannot be empty");
        }
        if (practiceArea == null || practiceArea.trim().isEmpty()) {
            throw new IllegalArgumentException("Practice area cannot be empty");
        }
    }
} 