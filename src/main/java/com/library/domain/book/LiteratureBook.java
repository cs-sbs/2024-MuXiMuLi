package com.library.domain.book;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiteratureBook extends Book {
    private String genre;
    private String era;
    private String language;

    public LiteratureBook() {
        setType("Literature");
    }

    @Override
    public String displayInfo() {
        return String.format("""
            文学图书：《%s》
            作者：%s
            流派：%s
            年代：%s
            语言：%s
            库存：%d
            """,
            getTitle(), getAuthor(), 
            genre, era, 
            language, getStock()
        );
    }

    @Override
    public String getCategory() {
        return "Literature";
    }

    @Override
    public void validate() {
        validateCommon();
        if (genre == null || genre.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre cannot be empty");
        }
        if (language == null || language.trim().isEmpty()) {
            throw new IllegalArgumentException("Language cannot be empty");
        }
    }
} 