# Book Repo

---

I am now working on implementing the web api system so that I can access this system from a web page

---

This is a project I am using to learn how to combine SQL with other languages. I am using a MySQL database
to store information about my book catalogue. I then use Java and the JDBC library to query this database.
In the future, this project will have a GUI that will show thumbnails for each of the books in the database
and will allow the user to add, edit, and delete books from the database.

---

run this command to compile and run the program

```bash
mvn clean compile exec:java -Dcom.mysql.cj.disableAbandonedConnectionCleanup=true
```

run this command to compile and run the test suite

```bash
mvn clean test
```

---

## Road map

### Server Side

This is what I plan to implement (and unit test) before setting up the api system
- [X] Github Workflows
  - [X] Run JUnit Testing On Push
  - [X] Create SQL System And Generate Fake database.properties File For Testing
- [X] Book Model
- [X] Author Model
- [X] Basic Database Connection
- [ ] Book Repository
  - [X] Fetch All Books (no author information)
  - [X] Fetch Book By ID (no author information)
  - [X] Fetch Book By ISBN (auto detect isbn 10 or isbn 13)
  - [X] Insert New Book (return new book ID)
  - [X] Delete Book By ID
  - [X] Delete Book By ISBN
  - [X] Check If Book Exists By ID
  - [X] Check If Book Exists By ISBN
  - [ ] Fetch List Of Books By Title Search Term (no author information)
  - [X] Update Book Title
  - [X] Update Book ISBN10
  - [X] Update Book ISBN13
  - [X] Update Book Page Count
  - [X] Update Book Description
- [ ] Author Repository
  - [X] Fetch All Authors
  - [X] Fetch Author By ID
  - [X] Fetch Author By Exact Name
  - [X] Insert New Author (return new author ID)
  - [X] Delete Author By ID
  - [X] Delete Author By Exact Name
  - [X] Check If Author Exists By ID
  - [X] Check If Author Exists By Name
  - [ ] Fetch List Of Authors By Name Search Term
  - [X] Update An Author's Name
- [X] Book-Authors Repository
  - [x] Fetch All Books Associated With Specific Author ID
  - [x] Fetch All Authors Associated With Specific Book ID
  - [x] Make A Relation Between A Book And An Author (With IDs)
  - [x] Delete A Relation Between A Book And An Author (With IDs)
  - [x] Check If A Book And Author Are Related (With IDs)
- [X] Service Layer (Expose A Set Of API Functions That Will Handle The Logistics For Each Of The Basic CRUD Operations)
  - [X] Fetch List Of All Books With Their Authors Filled In or Not
  - [X] Create A New Book And New Authors By Passing Book Object With Author Objects Filled In
  - [X] Update A Book By Passing Book Object With Author Objects Filled In
  - [X] Delete Book By Passing Book Id
  - [X] Passthroughs For The Above Read Operations
    - [X] Fetch book by id
    - [X] Fetch book by isbn
    - [X] Fetch author by id
    - [X] Fetch author by name
    - [X] Fetch authors for book
    - [X] Fetch all books for author

### Client Side

- [ ] Starting point
  - [ ] Homepage with menu / display
  - [ ] GET endpoints
    - [ ] /books endpoint: return all books with authors
    - [ ] /authors endpoint: return all authors
  - [ ] POST endpoints
    - [ ] Add new book
    - [ ] Update existing book
    - [ ] Add new author
    - [ ] Update existing author

