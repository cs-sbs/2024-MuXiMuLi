package com.library.domain.book;

import com.library.domain.book.ComputerBook;
import com.library.domain.book.LiteratureBook;
import com.library.domain.book.ScienceBook;
import com.library.domain.book.ArtBook;
import com.library.domain.book.HistoryBook;
import com.library.domain.book.PhilosophyBook;
import com.library.domain.book.EconomicsBook;
import com.library.domain.book.MedicineBook;
import com.library.domain.book.LawBook;
import com.library.domain.book.EducationBook;

public class BookFactory {
    public static Book createBook(String type) {
        return switch (type.toLowerCase()) {
            case "computer" -> new ComputerBook();
            case "literature" -> new LiteratureBook();
            case "science" -> new ScienceBook();
            case "art" -> new ArtBook();
            case "history" -> new HistoryBook();
            case "philosophy" -> new PhilosophyBook();
            case "economics" -> new EconomicsBook();
            case "medicine" -> new MedicineBook();
            case "law" -> new LawBook();
            case "education" -> new EducationBook();
            default -> throw new IllegalArgumentException("Unknown book type: " + type);
        };
    }

    public static Book createBookFromExisting(Book book) {
        Book newBook = createBook(book.getCategory());
        newBook.setIsbn(book.getIsbn());
        newBook.setTitle(book.getTitle());
        newBook.setAuthor(book.getAuthor());
        newBook.setStock(book.getStock());
        
        if (book instanceof ComputerBook computerBook && newBook instanceof ComputerBook newComputerBook) {
            newComputerBook.setProgrammingLanguage(computerBook.getProgrammingLanguage());
            newComputerBook.setFramework(computerBook.getFramework());
            newComputerBook.setDifficulty(computerBook.getDifficulty());
        } else if (book instanceof LiteratureBook literatureBook && newBook instanceof LiteratureBook newLiteratureBook) {
            newLiteratureBook.setGenre(literatureBook.getGenre());
            newLiteratureBook.setEra(literatureBook.getEra());
            newLiteratureBook.setLanguage(literatureBook.getLanguage());
        }
        
        return newBook;
    }
} 