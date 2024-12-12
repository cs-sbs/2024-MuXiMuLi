package com.library.domain.book;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EducationBook extends Book {
    private String educationLevel;
    private String subject;
    private String teachingMethod;

    public EducationBook() {
        setType("Education");
    }

    @Override
    public String displayInfo() {
        return String.format("""
            教育图书：《%s》
            作者：%s
            教育层次：%s
            学科：%s
            教学方法：%s
            库存：%d
            """,
            getTitle(), getAuthor(), 
            educationLevel, subject, 
            teachingMethod, getStock()
        );
    }

    @Override
    public String getCategory() {
        return "Education";
    }

    @Override
    public void validate() {
        validateCommon();
        if (educationLevel == null || educationLevel.trim().isEmpty()) {
            throw new IllegalArgumentException("Education level cannot be empty");
        }
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be empty");
        }
        if (teachingMethod == null || teachingMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Teaching method cannot be empty");
        }
    }
} 