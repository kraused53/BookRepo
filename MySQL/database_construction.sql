USE bookrepo;

CREATE TABLE books (
    id INT NOT NULL AUTO_INCREMENT,
    title VARCHAR(120) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    description TEXT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE authors (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE book_authors (
    book_id INT NOT NULL,
    author_id INT NOT NULL,
    PRIMARY KEY(book_id, author_id)
);
