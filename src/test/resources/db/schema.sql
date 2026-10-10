-- Drop tables in dependency order.
DROP TABLE IF EXISTS book_authors;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS authors;

-- Create books.
CREATE TABLE books (
    id INT AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    isbn_13 VARCHAR(13),
    isbn_10 VARCHAR(10),
    page_count INT NOT NULL,
    date_created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
       ON UPDATE CURRENT_TIMESTAMP,
    description TEXT,

    PRIMARY KEY (id),
    CONSTRAINT unique_book_isbn_13 UNIQUE (isbn_13),
    CONSTRAINT unique_book_isbn_10 UNIQUE (isbn_10)
);

-- Create authors.
CREATE TABLE authors (
    id INT AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    date_created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
     ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT unique_author_name UNIQUE (name)
);

-- Create the many-to-many relationship table.
CREATE TABLE book_authors (
    book_id INT NOT NULL,
    author_id INT NOT NULL,

    PRIMARY KEY (book_id, author_id),

    CONSTRAINT fk_book_authors_book
      FOREIGN KEY (book_id) REFERENCES books(id),

    CONSTRAINT fk_book_authors_author
      FOREIGN KEY (author_id) REFERENCES authors(id)
);