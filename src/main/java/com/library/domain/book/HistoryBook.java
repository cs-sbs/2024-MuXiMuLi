package com.library.domain.book;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryBook extends Book {
    private String timePeriod;
    private String region;
    private String historicalFigures;

    public HistoryBook() {
        setType("History");
    }

    @Override
    public String displayInfo() {
        return String.format("""
            历史图书：《%s》
            作者：%s
            时代：%s
            地区：%s
            历史人物：%s
            库存：%d
            """,
            getTitle(), getAuthor(), 
            timePeriod, region, 
            historicalFigures, getStock()
        );
    }

    @Override
    public String getCategory() {
        return "History";
    }

    @Override
    public void validate() {
        validateCommon();
        if (timePeriod == null || timePeriod.trim().isEmpty()) {
            throw new IllegalArgumentException("Time period cannot be empty");
        }
        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException("Region cannot be empty");
        }
    }
} 