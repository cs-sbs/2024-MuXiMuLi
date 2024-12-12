package com.library.domain.book;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ComputerBook.class, name = "Computer"),
    @JsonSubTypes.Type(value = LiteratureBook.class, name = "Literature"),
    @JsonSubTypes.Type(value = ScienceBook.class, name = "Science"),
    @JsonSubTypes.Type(value = ArtBook.class, name = "Art"),
    @JsonSubTypes.Type(value = HistoryBook.class, name = "History"),
    @JsonSubTypes.Type(value = PhilosophyBook.class, name = "Philosophy"),
    @JsonSubTypes.Type(value = EconomicsBook.class, name = "Economics"),
    @JsonSubTypes.Type(value = MedicineBook.class, name = "Medicine"),
    @JsonSubTypes.Type(value = EducationBook.class, name = "Education"),
    @JsonSubTypes.Type(value = LawBook.class, name = "Law")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Book implements Serializable {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String type;
    private int stock;
    private String createTime;
    private String updateTime;
    
    @JsonProperty("bookType")
    public String getBookType() {
        return this.type;
    }
    
    @JsonProperty("bookType")
    public void setBookType(String type) {
        this.type = type;
    }
    
    public abstract String displayInfo();
    public abstract String getCategory();
    public abstract void validate();
    
    public boolean hasStock() {
        return stock > 0;
    }
    
    public void decreaseStock() {
        if (!hasStock()) {
            throw new IllegalStateException("No stock available");
        }
        stock--;
        updateTime = LocalDateTime.now().toString();
    }
    
    public void increaseStock() {
        stock++;
        updateTime = LocalDateTime.now().toString();
    }
    
    protected void validateCommon() {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
    }
}