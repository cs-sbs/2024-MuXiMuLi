package com.library.domain.book;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhilosophyBook extends Book {
    private String philosophicalSchool;
    private String keyConcepts;
    private String thinkers;

    public PhilosophyBook() {
        setType("Philosophy");
    }

    @Override
    public String displayInfo() {
        return String.format("""
            哲学图书：《%s》
            作者：%s
            哲学流派：%s
            核心概念：%s
            主要思想家：%s
            库存：%d
            """,
            getTitle(), getAuthor(), 
            philosophicalSchool, keyConcepts, 
            thinkers, getStock()
        );
    }

    @Override
    public String getCategory() {
        return "Philosophy";
    }

    @Override
    public void validate() {
        validateCommon();
        if (philosophicalSchool == null || philosophicalSchool.trim().isEmpty()) {
            throw new IllegalArgumentException("Philosophical school cannot be empty");
        }
        if (keyConcepts == null || keyConcepts.trim().isEmpty()) {
            throw new IllegalArgumentException("Key concepts cannot be empty");
        }
    }
} 