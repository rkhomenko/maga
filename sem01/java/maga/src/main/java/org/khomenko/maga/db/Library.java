package org.khomenko.maga.db;

import java.util.List;

public interface Library {
    /* Регистрация новой книги */
    void addNewBook(Book book);

    /* Добавление нового абонента */
    void addAbonent(Student student);

    /* Студент берет книгу */
    void borrowBook(Book book, Student student);

    /* Студент возвращает книгу */
    void returnBook(Book book, Student student);

    /* Получить список свободных книг */
    List<Book> findAvailableBooks();

    /* Список всех записанных в библиотеку студентов*/
    List<Student> getAllStudents();
}
