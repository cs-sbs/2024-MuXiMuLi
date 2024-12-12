package com.library.domain.book;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EconomicsBook extends Book {
    private String economicSchool;
    private String marketType;
    private String applicationField;

    public EconomicsBook() {
        setType("Economics");
    }

    @Override
    public String displayInfo() {
        return String.format("""
            经济学图书：《%s》
            作者：%s
            经济学派：%s
            市场类型：%s
            应用领域：%s
            库存：%d
            """,
            getTitle(), getAuthor(), 
            economicSchool, marketType, 
            applicationField, getStock()
        );
    }

    @Override
    public String getCategory() {
        return "Economics";
    }

    @Override
    public void validate() {
        validateCommon();
        if (economicSchool == null || economicSchool.trim().isEmpty()) {
            throw new IllegalArgumentException("Economic school cannot be empty");
        }
        if (marketType == null || marketType.trim().isEmpty()) {
            throw new IllegalArgumentException("Market type cannot be empty");
        }
        if (applicationField == null || applicationField.trim().isEmpty()) {
            throw new IllegalArgumentException("Application field cannot be empty");
        }
    }
} 