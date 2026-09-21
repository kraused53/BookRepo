USE bookrepo;

SELECT b.id AS book_id, b.title AS Title, a.id AS author_id, a.name AS Author, b.isbn AS ISBN
FROM books b
INNER JOIN book_authors ba ON b.id = ba.book_id
INNER JOIN authors a ON ba.author_id = a.id
ORDER BY b.id;