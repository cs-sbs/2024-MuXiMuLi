package com.library.domain.book;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("Science")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScienceBook extends Book {
    private String subjectArea;
    private String researchField;
    private String academicLevel;

    public ScienceBook() {
        setType("Science");
    }

    @Override
    public String displayInfo() {
        return String.format("""
            科学图书：《%s》
            作者：%s
            学科领域：%s
            研究方向：%s
            学术水平：%s
            库存：%d
            """,
            getTitle(), getAuthor(), 
            subjectArea, researchField, 
            academicLevel, getStock()
        );
    }

    @Override
    public String getCategory() {
        return "Science";
    }

    @Override
    public void validate() {
        validateCommon();
        if (subjectArea == null || subjectArea.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject area cannot be empty");
        }
        if (researchField == null || researchField.trim().isEmpty()) {
            throw new IllegalArgumentException("Research field cannot be empty");
        }
        if (academicLevel == null || academicLevel.trim().isEmpty()) {
            throw new IllegalArgumentException("Academic level cannot be empty");
        }
    }
} 