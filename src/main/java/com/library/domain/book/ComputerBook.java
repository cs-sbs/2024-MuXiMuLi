package com.library.domain.book;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComputerBook extends Book {
    private String programmingLanguage;
    private String framework;
    private String difficulty;
    
    public ComputerBook() {
        setType("Computer");
    }
    
    @Override
    public String displayInfo() {
        return String.format("""
            计算机图书：《%s》
            作者：%s
            编程语言：%s
            框架：%s
            难度：%s
            库存：%d
            """,
            getTitle(), getAuthor(), 
            programmingLanguage, framework, 
            difficulty, getStock()
        );
    }
    
    @Override
    public String getCategory() {
        return "Computer";
    }

    @Override
    public void validate() {
        validateCommon();
        if (programmingLanguage == null || programmingLanguage.trim().isEmpty()) {
            throw new IllegalArgumentException("Programming language cannot be empty");
        }
        if (difficulty == null || difficulty.trim().isEmpty()) {
            throw new IllegalArgumentException("Difficulty level cannot be empty");
        }
    }
} 