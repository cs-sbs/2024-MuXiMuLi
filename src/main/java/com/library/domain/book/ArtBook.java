package com.library.domain.book;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtBook extends Book {
    private String artForm;
    private String medium;
    private String style;

    public ArtBook() {
        setType("Art");
    }

    @Override
    public String displayInfo() {
        return String.format("""
            艺术图书：《%s》
            作者：%s
            艺术形式：%s
            创作媒介：%s
            艺术风格：%s
            库存：%d
            """,
            getTitle(), getAuthor(), 
            artForm, medium, 
            style, getStock()
        );
    }

    @Override
    public String getCategory() {
        return "Art";
    }

    @Override
    public void validate() {
        validateCommon();
        if (artForm == null || artForm.trim().isEmpty()) {
            throw new IllegalArgumentException("Art form cannot be empty");
        }
        if (medium == null || medium.trim().isEmpty()) {
            throw new IllegalArgumentException("Medium cannot be empty");
        }
        if (style == null || style.trim().isEmpty()) {
            throw new IllegalArgumentException("Style cannot be empty");
        }
    }
} 