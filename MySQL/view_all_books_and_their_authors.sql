USE bookrepo;

SELECT b.title AS Title, a.name AS Author, b.isbn AS ISBN
FROM books b
INNER JOIN book_authors ba ON b.id = ba.book_id
INNER JOIN authors a ON ba.author_id = a.id
ORDER BY b.title;