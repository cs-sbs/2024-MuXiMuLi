package com.library.repository.impl;

import com.library.domain.book.*;
import com.library.repository.BookRepository;
import com.library.util.DBHandler;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepositoryImpl implements BookRepository {
    private static final Logger logger = Logger.getLogger(BookRepositoryImpl.class);

    @Override
    public Book save(Book book) {
        Connection conn = null;
        try {
            conn = DBHandler.getConnection();
            String sql = """
                INSERT INTO books (isbn, title, author, type, stock, 
                    legal_system, jurisdiction, legal_field)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
                
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, book.getIsbn());
                stmt.setString(2, book.getTitle());
                stmt.setString(3, book.getAuthor());
                stmt.setString(4, book.getType());
                stmt.setInt(5, book.getStock());
                
                // 根据图书类型设置特定字段
                if (book instanceof LawBook) {
                    LawBook lawBook = (LawBook) book;
                    stmt.setString(6, lawBook.getLegalSystem());
                    stmt.setString(7, lawBook.getJurisdiction());
                    stmt.setString(8, lawBook.getLegalField());
                } else {
                    // 如果不是法律类图书，设置这些字段为 null
                    stmt.setNull(6, Types.VARCHAR);
                    stmt.setNull(7, Types.VARCHAR);
                    stmt.setNull(8, Types.VARCHAR);
                }
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating book failed, no rows affected.");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Creating book failed, no ID obtained.");
                    }
                }
                
                conn.commit();
                return book;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction", ex);
                }
            }
            logger.error("Error saving book", e);
            throw new RuntimeException("Error saving book", e);
        } finally {
            DBHandler.closeConnection(conn);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        Connection conn = null;
        try {
            conn = DBHandler.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Book book = createBookFromResultSet(rs);
                        conn.commit();  // 提交事务
                        return Optional.of(book);
                    }
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // 回滚事务
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction", ex);
                }
            }
            logger.error("Error finding book by id", e);
            throw new RuntimeException("Error finding book by id", e);
        } finally {
            DBHandler.closeConnection(conn);  // 使用 DBHandler 关闭连接
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        String sql = "SELECT * FROM books";
        List<Book> books = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DBHandler.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
                conn.commit();  // 提交事务
                logger.debug("Found " + books.size() + " books");
                return books;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // 回滚事务
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction", ex);
                }
            }
            logger.error("Error finding all books", e);
            throw new RuntimeException("查找图书失败，请稍后重试", e);
        } finally {
            DBHandler.closeConnection(conn);  // 使用 DBHandler 关闭连接
        }
    }

    @Override
    public List<Book> findByType(String type) {
        String sql = "SELECT * FROM books WHERE type = ?";
        List<Book> books = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DBHandler.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, type);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
                conn.commit();  // 提交事务
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // 回滚事务
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction", ex);
                }
            }
            logger.error("Error finding books by type", e);
            throw new RuntimeException("Error finding books by type", e);
        } finally {
            DBHandler.closeConnection(conn);  // 使用 DBHandler 关闭连接
        }
        return books;
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        Connection conn = null;
        try {
            conn = DBHandler.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, isbn);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    Book book = mapResultSetToBook(rs);
                    conn.commit();  // 提交事务
                    return Optional.of(book);
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // 回滚事务
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction", ex);
                }
            }
            logger.error("Error finding book by ISBN", e);
            throw new RuntimeException("Error finding book by ISBN", e);
        } finally {
            DBHandler.closeConnection(conn);  // 使用 DBHandler 关闭连接
        }
        return Optional.empty();
    }

    @Override
    public void updateStock(Long id, int newStock) {
        String sql = "UPDATE books SET stock = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = DBHandler.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, newStock);
                stmt.setLong(2, id);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new RuntimeException("No book found with id: " + id);
                }
                conn.commit();  // 提交事务
                logger.debug("Successfully updated stock for book id: " + id + " to " + newStock);
            }
        } catch (SQLException e) {
            DBHandler.rollback(conn);  // 使用 DBHandler 的回滚方法
            logger.error("Error updating book stock", e);
            throw new RuntimeException("Error updating book stock", e);
        } finally {
            DBHandler.closeConnection(conn);  // 使用 DBHandler 的关闭方法
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        Connection conn = null;
        try {
            conn = DBHandler.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new RuntimeException("No book found with id: " + id);
                }
                conn.commit();  // 提交事务
                logger.debug("Successfully deleted book with id: " + id);
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // 回滚事务
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction", ex);
                }
            }
            logger.error("Error deleting book", e);
            throw new RuntimeException("Error deleting book", e);
        } finally {
            DBHandler.closeConnection(conn);  // 使用 DBHandler 关闭连接
        }
    }

    @Override
    public boolean exists(String isbn) {
        String sql = "SELECT COUNT(*) FROM books WHERE isbn = ?";
        Connection conn = null;
        try {
            conn = DBHandler.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, isbn);
                ResultSet rs = stmt.executeQuery();
                boolean exists = rs.next() && rs.getInt(1) > 0;
                conn.commit();  // 提交事务
                return exists;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // 回滚事务
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction", ex);
                }
            }
            logger.error("Error checking book existence", e);
            throw new RuntimeException("Error checking book existence", e);
        } finally {
            DBHandler.closeConnection(conn);  // 使用 DBHandler 关闭连接
        }
    }

    @Override
    public List<Book> findByTitle(String title) {
        String sql = "SELECT * FROM books WHERE title LIKE ?";
        List<Book> books = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DBHandler.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "%" + title + "%");
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    books.add(createBookFromResultSet(rs));
                }
                conn.commit();  // 提交事务
                logger.debug("Found " + books.size() + " books with title containing: " + title);
                return books;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // 回滚事务
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction", ex);
                }
            }
            logger.error("Error finding books by title: " + title, e);
            throw new RuntimeException("Error finding books by title", e);
        } finally {
            DBHandler.closeConnection(conn);  // 使用 DBHandler 关闭连接
        }
    }

    @Override
    public List<Book> searchBooks(String keyword, String type) {
        List<Book> books = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM books WHERE 1=1");
        List<Object> params = new ArrayList<>();
        Connection conn = null;

        // 如果提供了关键字，搜索标题和作者
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (title LIKE ? OR author LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        // 如果提供了类型，按类型筛选
        if (type != null && !type.trim().isEmpty()) {
            sql.append(" AND type = ?");
            params.add(type);
        }

        try {
            conn = DBHandler.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                // 设置参数
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
                
                conn.commit();  // 提交事务
                logger.debug("Found " + books.size() + " books matching search criteria");
                return books;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // 回滚事务
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction", ex);
                }
            }
            logger.error("Error searching books", e);
            throw new RuntimeException("Error searching books", e);
        } finally {
            DBHandler.closeConnection(conn);  // 使用 DBHandler 关闭连接
        }
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        String type = rs.getString("type");
        Book book = switch (type) {
            case "Computer" -> new ComputerBook();
            case "Literature" -> new LiteratureBook();
            case "Science" -> new ScienceBook();
            case "Art" -> new ArtBook();
            case "History" -> new HistoryBook();
            case "Philosophy" -> new PhilosophyBook();
            case "Economics" -> new EconomicsBook();
            case "Medicine" -> new MedicineBook();
            case "Education" -> new EducationBook();
            case "Law" -> new LawBook();
            default -> throw new IllegalArgumentException("Unknown book type: " + type);
        };

        // 设置通用属性
        book.setId(rs.getLong("id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setType(type);
        book.setStock(rs.getInt("stock"));

        // 根据类型设置特定属性
        switch (type) {
            case "Computer" -> {
                ComputerBook cb = (ComputerBook) book;
                cb.setProgrammingLanguage(rs.getString("programming_language"));
                cb.setFramework(rs.getString("framework"));
                cb.setDifficulty(rs.getString("difficulty"));
            }
            case "Literature" -> {
                LiteratureBook lb = (LiteratureBook) book;
                lb.setGenre(rs.getString("genre"));
                lb.setEra(rs.getString("era"));
                lb.setLanguage(rs.getString("language"));
            }
            case "Science" -> {
                ScienceBook sb = (ScienceBook) book;
                sb.setSubjectArea(rs.getString("subject_area"));
                sb.setResearchField(rs.getString("research_field"));
                sb.setAcademicLevel(rs.getString("academic_level"));
            }
            case "Art" -> {
                ArtBook ab = (ArtBook) book;
                ab.setArtForm(rs.getString("art_form"));
                ab.setMedium(rs.getString("medium"));
                ab.setStyle(rs.getString("style"));
            }
            case "History" -> {
                HistoryBook hb = (HistoryBook) book;
                hb.setTimePeriod(rs.getString("time_period"));
                hb.setRegion(rs.getString("region"));
                hb.setHistoricalFigures(rs.getString("historical_figures"));
            }
            case "Philosophy" -> {
                PhilosophyBook pb = (PhilosophyBook) book;
                pb.setPhilosophicalSchool(rs.getString("philosophical_school"));
                pb.setKeyConcepts(rs.getString("key_concepts"));
                pb.setThinkers(rs.getString("thinkers"));
            }
            case "Economics" -> {
                EconomicsBook eb = (EconomicsBook) book;
                eb.setEconomicSchool(rs.getString("economic_school"));
                eb.setMarketType(rs.getString("market_type"));
                eb.setApplicationField(rs.getString("application_field"));
            }
            case "Medicine" -> {
                MedicineBook mb = (MedicineBook) book;
                mb.setMedicalSpecialty(rs.getString("medical_specialty"));
                mb.setClinicalFocus(rs.getString("clinical_focus"));
                mb.setPracticeArea(rs.getString("practice_area"));
            }
            case "Education" -> {
                EducationBook eb = (EducationBook) book;
                eb.setEducationLevel(rs.getString("education_level"));
                eb.setSubject(rs.getString("subject"));
                eb.setTeachingMethod(rs.getString("teaching_method"));
            }
            case "Law" -> {
                LawBook lb = (LawBook) book;
                lb.setLegalSystem(rs.getString("legal_system"));
                lb.setJurisdiction(rs.getString("jurisdiction"));
                lb.setLegalField(rs.getString("legal_field"));
            }
        }
        return book;
    }

    private Book createBookFromResultSet(ResultSet rs) throws SQLException {
        String type = rs.getString("type");
        Book book = switch (type) {
            case "Computer" -> {
                ComputerBook computerBook = new ComputerBook();
                computerBook.setProgrammingLanguage(rs.getString("programming_language"));
                computerBook.setFramework(rs.getString("framework"));
                computerBook.setDifficulty(rs.getString("difficulty"));
                yield computerBook;
            }
            case "Literature" -> {
                LiteratureBook literatureBook = new LiteratureBook();
                literatureBook.setGenre(rs.getString("genre"));
                literatureBook.setEra(rs.getString("era"));
                literatureBook.setLanguage(rs.getString("language"));
                yield literatureBook;
            }
            case "Science" -> {
                Book scienceBook = new Book() {
                    @Override
                    public String displayInfo() {
                        try {
                            return String.format("""
                                科学图书：《%s》
                                作者：%s
                                研究领域：%s
                                学术水平：%s
                                库存：%d
                                """,
                                getTitle(), getAuthor(),
                                rs.getString("research_field"),
                                rs.getString("academic_level"),
                                getStock());
                        } catch (SQLException e) {
                            logger.error("Error displaying science book info", e);
                            return "Error displaying book info";
                        }
                    }

                    @Override
                    public void validate() {
                        validateCommon();
                    }

                    @Override
                    public String getCategory() {
                        return "Science";
                    }
                };
                yield scienceBook;
            }
            // ... 其他类型的图书
            default -> new Book() {
                @Override
                public String displayInfo() {
                    return String.format("""
                        图书：《%s》
                        作者：%s
                        类型：%s
                        库存：%d
                        """,
                        getTitle(), getAuthor(), type, getStock());
                }

                @Override
                public void validate() {
                    validateCommon();
                }

                @Override
                public String getCategory() {
                    return type;
                }
            };
        };
        
        // 设置通用属性
        book.setId(rs.getLong("id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setType(type);
        book.setStock(rs.getInt("stock"));
        
        return book;
    }

    // 注册关闭钩子
    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Application is shutting down...");
            // 在这里添加关闭数据库连接的逻辑
        }));
    }

    // 注册Ctrl+C处理器
    private void registerCtrlCHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Ctrl+C pressed, shutting down...");
            // 在这里添加关闭数据库连接的逻辑
        }));
    }
} 