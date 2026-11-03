# Book Repo

---

I am rebuilding this to make testing better / easier. I will be working on branch: REBUILD until work is complete. Then I will move back to MAIN before implemementing an api endpoint

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
  - [ ] Fetch All Authors
  - [ ] Fetch Author By ID
  - [ ] Insert New Author (return new author ID)
  - [ ] Delete Author By ID
  - [ ] Check If Author Exists By ID
  - [ ] Check If Author Exists By Name
  - [ ] Fetch List Of Authors By Name Search Term
  - [ ] Update An Author
- [ ] Book-Authors Repository
  - [ ] Fetch List Of All Books With Their Authors Filled In
  - [ ] Fetch All Books Associated With Specific Author ID
  - [ ] Fetch All Authors Associated With Specific Book ID
  - [ ] Make A Relation Between A Book And An Author (With IDs)
  - [ ] Delete A Relation Between A Book And An Author (With IDs)
  - [ ] Check If A Book And Author Are Related (With IDs)
- [ ] Service Layer (Expose A Set Of API Functions That Will Handle The Logistics For Each Of The Basic CRUD Operations)
  - [ ] Create A New Book By Passing Book Object With Author Objects Filled In
  - [ ] Update New Book By Passing Book Object With Author Objects Filled In
  - [ ] Delete Book By Passing Book Object