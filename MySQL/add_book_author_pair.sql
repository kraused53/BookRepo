USE bookrepo;

SET @book_id = 1;
SET @author_id = 2;

INSERT INTO book_authors (book_id, author_id)
VALUES (@book_id, @author_id);