package io.github.kraused53.service;

import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.models.Author;
import io.github.kraused53.models.Book;
import io.github.kraused53.repository.author.AuthorRepository;
import io.github.kraused53.repository.book.BookRepository;
import io.github.kraused53.repository.book_authors.BookAuthorsRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestLibraryService {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookAuthorsRepository bookAuthorsRepository;

    @Mock
    private Connection connection;

    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        libraryService = new LibraryService(
                bookRepository,
                authorRepository,
                bookAuthorsRepository
        );
    }

    // ---------------------------------------------------------
    // Author operations
    // ---------------------------------------------------------

    @Test
    void getAllAuthorsReturnsRepositoryResults() throws SQLException {
        List<Author> expected = List.of(new Author(), new Author());

        when(authorRepository.findAll()).thenReturn(expected);

        List<Author> actual = libraryService.getAllAuthors();

        assertSame(expected, actual);
        verify(authorRepository).findAll();
    }

    @Test
    void getAllAuthorsPropagatesSQLException() throws SQLException {
        when(authorRepository.findAll())
                .thenThrow(new SQLException("Database error"));

        assertThrows(
                SQLException.class,
                () -> libraryService.getAllAuthors()
        );
    }

    @Test
    void addNewAuthorReturnsGeneratedId()
            throws SQLException, DuplicateEntryException {
        Author author = new Author();
        author.setName("Test Author");

        when(authorRepository.insert(author)).thenReturn(12);

        int id = libraryService.addNewAuthor(author);

        assertEquals(12, id);
        verify(authorRepository).insert(author);
    }

    @Test
    void addNewAuthorPropagatesDuplicateException()
            throws SQLException, DuplicateEntryException {
        Author author = new Author();
        author.setName("Existing Author");

        when(authorRepository.insert(author))
                .thenThrow(new DuplicateEntryException("Duplicate"));

        assertThrows(
                DuplicateEntryException.class,
                () -> libraryService.addNewAuthor(author)
        );
    }

    @Test
    void deleteAuthorByIdDelegatesToRepository() throws Exception {
        libraryService.deleteAuthorById(7);

        verify(authorRepository).delete(7);
    }

    @Test
    void deleteAuthorDelegatesUsingAuthorId() throws Exception {
        Author author = new Author();
        author.setId(7);

        libraryService.deleteAuthor(author);

        verify(authorRepository).delete(7);
    }

    @Test
    void updateAuthorNameByIdDelegatesToRepository() throws Exception {
        libraryService.updateAuthorName(7, "Updated Author");

        verify(authorRepository).update(7, "Updated Author");
    }

    @Test
    void updateAuthorNameRejectsNonPositiveId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> libraryService.updateAuthorName(0, "Valid Name")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> libraryService.updateAuthorName(-1, "Valid Name")
        );

        verifyNoInteractions(authorRepository);
    }

    @Test
    void updateAuthorNameRejectsNullOrBlankName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> libraryService.updateAuthorName(1, null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> libraryService.updateAuthorName(1, "")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> libraryService.updateAuthorName(1, "   ")
        );

        verifyNoInteractions(authorRepository);
    }

    @Test
    void findAuthorByNameReturnsRepositoryResult() throws Exception {
        Author expected = new Author();
        expected.setId(3);
        expected.setName("Test Author");

        when(authorRepository.lookupByName("Test Author"))
                .thenReturn(expected);

        Author actual = libraryService.findAuthorByName("Test Author");

        assertSame(expected, actual);
    }

    @Test
    void findAuthorByNamePropagatesNotFoundException()
            throws Exception {
        when(authorRepository.lookupByName("Missing Author"))
                .thenThrow(new EntryNotFoundException("Not found"));

        assertThrows(
                EntryNotFoundException.class,
                () -> libraryService.findAuthorByName("Missing Author")
        );
    }

    // ---------------------------------------------------------
    // Book retrieval
    // ---------------------------------------------------------

    @Test
    void getAllBooksReturnsRepositoryResults() throws SQLException {
        List<Book> expected = List.of(new Book(), new Book());

        when(bookAuthorsRepository.getAllBooks()).thenReturn(expected);

        List<Book> actual = libraryService.getAllBooks();

        assertSame(expected, actual);
        verify(bookAuthorsRepository).getAllBooks();
    }

    @Test
    void getAllBooksPropagatesSQLException() throws SQLException {
        when(bookAuthorsRepository.getAllBooks())
                .thenThrow(new SQLException("Database error"));

        assertThrows(
                SQLException.class,
                () -> libraryService.getAllBooks()
        );
    }

    // ---------------------------------------------------------
    // Book insertion and transactions
    // ---------------------------------------------------------

    private MockedStatic<Database> mockDatabaseConnection() {
        MockedStatic<Database> databaseMock = mockStatic(Database.class);
        databaseMock.when(Database::getConnection).thenReturn(connection);
        return databaseMock;
    }

    @Test
    void addBookInsertsBookAndLinksExistingAuthor()
            throws Exception {
        Book book = mock(Book.class);
        Author requestedAuthor = mock(Author.class);
        Author existingAuthor = mock(Author.class);

        when(book.getTitle()).thenReturn("Test Book");
        when(book.getAuthors()).thenReturn(List.of(requestedAuthor));
        when(requestedAuthor.getName()).thenReturn("Existing Author");
        when(existingAuthor.getId()).thenReturn(15);

        when(bookRepository.exists(connection, book)).thenReturn(false);

        when(authorRepository.lookupByName(connection, "Existing Author"))
                .thenReturn(existingAuthor);

        when(bookRepository.insert(connection, book)).thenReturn(42);

        try (MockedStatic<Database> ignored = mockDatabaseConnection()) {
            libraryService.addBook(book);
        }

        verify(connection).setAutoCommit(false);
        verify(requestedAuthor).setId(15);
        verify(book).setId(42);

        verify(bookAuthorsRepository).makeLink(
                connection, book, requestedAuthor
        );

        verify(connection).commit();
        verify(connection, never()).rollback();
    }

    @Test
    void addBookInsertsNewAuthorWhenLookupFails()
            throws Exception {
        Book book = mock(Book.class);
        Author author = mock(Author.class);

        when(book.getTitle()).thenReturn("New Book");
        when(book.getAuthors()).thenReturn(List.of(author));
        when(author.getName()).thenReturn("New Author");

        when(bookRepository.exists(connection, book)).thenReturn(false);

        when(authorRepository.lookupByName(connection, "New Author"))
                .thenThrow(new EntryNotFoundException("Not found"));

        when(authorRepository.insert(connection, author)).thenReturn(21);
        when(bookRepository.insert(connection, book)).thenReturn(43);

        try (MockedStatic<Database> ignored = mockDatabaseConnection()) {
            libraryService.addBook(book);
        }

        verify(authorRepository).insert(connection, author);
        verify(author).setId(21);

        verify(bookAuthorsRepository).makeLink(
                connection, book, author
        );

        verify(connection).commit();
        verify(connection, never()).rollback();
    }

    @Test
    void addBookRejectsDuplicateAndRollsBack()
            throws Exception {
        Book book = mock(Book.class);

        when(book.getTitle()).thenReturn("Existing Book");
        when(bookRepository.exists(connection, book)).thenReturn(true);

        try (MockedStatic<Database> ignored = mockDatabaseConnection()) {
            assertThrows(
                    DuplicateEntryException.class,
                    () -> libraryService.addBook(book)
            );
        }

        verify(connection).rollback();
        verify(connection, never()).commit();

        verify(authorRepository, never())
                .insert(any(Connection.class), any());

        verify(bookRepository, never())
                .insert(any(Connection.class), any());

        verify(bookAuthorsRepository, never())
                .makeLink(any(Connection.class), any(), any());
    }

    @Test
    void addBookRollsBackWhenBookInsertionFails() throws Exception {
        Book book = mock(Book.class);

        // Do not stub getTitle(): the insertion exception occurs
        // before the success log uses the title.
        when(book.getAuthors()).thenReturn(List.of());
        when(bookRepository.exists(connection, book)).thenReturn(false);

        when(bookRepository.insert(connection, book))
                .thenThrow(new SQLException("Insert failed"));

        try (MockedStatic<Database> ignored = mockDatabaseConnection()) {
            assertThrows(
                    SQLException.class,
                    () -> libraryService.addBook(book)
            );
        }

        verify(bookRepository).exists(connection, book);
        verify(bookRepository).insert(connection, book);

        verify(connection).setAutoCommit(false);
        verify(connection).rollback();
        verify(connection, never()).commit();
    }

    @Test
    void addBookRollsBackWhenLinkCreationFails() throws Exception {
        Book book = mock(Book.class);
        Author author = mock(Author.class);
        Author existingAuthor = mock(Author.class);

        // getTitle() is intentionally not stubbed: the SQLException
        // from makeLink propagates before the success log is reached.
        when(book.getAuthors()).thenReturn(List.of(author));
        when(author.getName()).thenReturn("Existing Author");
        when(existingAuthor.getId()).thenReturn(17);

        when(bookRepository.exists(connection, book)).thenReturn(false);

        when(authorRepository.lookupByName(connection, "Existing Author"))
                .thenReturn(existingAuthor);

        when(bookRepository.insert(connection, book)).thenReturn(44);

        doThrow(new SQLException("Link insert failed"))
                .when(bookAuthorsRepository)
                .makeLink(connection, book, author);

        try (MockedStatic<Database> ignored = mockDatabaseConnection()) {
            assertThrows(
                    SQLException.class,
                    () -> libraryService.addBook(book)
            );
        }

        verify(bookRepository).exists(connection, book);

        verify(authorRepository).lookupByName(
                connection, "Existing Author"
        );

        verify(bookRepository).insert(connection, book);

        verify(bookAuthorsRepository).makeLink(
                connection, book, author
        );

        verify(connection).setAutoCommit(false);
        verify(connection).rollback();
        verify(connection, never()).commit();
    }

    @Test
    void addBookRollsBackWhenCommitFails() throws Exception {
        Book book = mock(Book.class);

        // The commit exception occurs before the success log uses
        // getTitle(), so no getTitle() stubbing is necessary.
        when(book.getAuthors()).thenReturn(List.of());
        when(bookRepository.exists(connection, book)).thenReturn(false);
        when(bookRepository.insert(connection, book)).thenReturn(45);

        doThrow(new SQLException("Commit failed"))
                .when(connection).commit();

        try (MockedStatic<Database> ignored = mockDatabaseConnection()) {
            assertThrows(
                    SQLException.class,
                    () -> libraryService.addBook(book)
            );
        }

        verify(bookRepository).exists(connection, book);
        verify(bookRepository).insert(connection, book);

        verify(connection).setAutoCommit(false);
        verify(connection).commit();
        verify(connection).rollback();
    }
}