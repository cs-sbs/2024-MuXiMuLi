package com.library.domain.book;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LawBook extends Book {
    private String legalSystem;
    private String jurisdiction;
    private String legalField;

    public LawBook() {
        setType("Law");
    }

    @Override
    public String displayInfo() {
        return String.format("""
            法律图书：《%s》
            作者：%s
            法律体系：%s
            司法管辖：%s
            法律领域：%s
            库存：%d
            """,
            getTitle(), getAuthor(), 
            legalSystem, jurisdiction, 
            legalField, getStock()
        );
    }

    @Override
    public String getCategory() {
        return "Law";
    }

    @Override
    public void validate() {
        validateCommon();
        if (legalSystem == null || legalSystem.trim().isEmpty()) {
            throw new IllegalArgumentException("Legal system cannot be empty");
        }
        if (jurisdiction == null || jurisdiction.trim().isEmpty()) {
            throw new IllegalArgumentException("Jurisdiction cannot be empty");
        }
        if (legalField == null || legalField.trim().isEmpty()) {
            throw new IllegalArgumentException("Legal field cannot be empty");
        }
    }
} 