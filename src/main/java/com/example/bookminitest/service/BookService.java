package com.example.bookminitest.service;

import com.example.bookminitest.model.Book;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Service
public class BookService implements IBookService {

    private static SessionFactory sessionFactory;
    private static EntityManager entityManager;

    static {
        try {
            sessionFactory = new Configuration()
                    .configure("hibernate.conf.xml")
                    .buildSessionFactory();
            entityManager = sessionFactory.createEntityManager();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Book> findAll() {
        String str ="select b from Book b";
        TypedQuery<Book> query = entityManager.createQuery(str, Book.class);
        return query.getResultList();
    }

    @Override
    public void save(Book book) {
        Transaction transaction = null;
        Book origin = new Book();
        if (book.getId() == 0) {
            origin = new Book();
        } else {
            origin = findById(book.getId());
        }
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            origin.setName(book.getName());
            origin.setAuthor(book.getAuthor());
            origin.setPrice(book.getPrice());
            origin.setCategory(book.getCategory());
            origin.setImage(book.getImage());
            session.saveOrUpdate(origin);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    @Override
    public Book findById(int id) {
        String queryStr = "SELECT b FROM Book AS b WHERE b.id = :id";
        TypedQuery<Book> query = entityManager.createQuery(queryStr, Book.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public void update(int id, Book book) {
        Transaction transaction = null;
        Book existingBook = findById(id);

        if (existingBook != null) {
            try (Session session = sessionFactory.openSession()){
                transaction = session.beginTransaction();
                existingBook.setName(book.getName());
                existingBook.setAuthor(book.getAuthor());
                existingBook.setPrice(book.getPrice());
                existingBook.setCategory(book.getCategory());
                existingBook.setImage(book.getImage());

                session.saveOrUpdate(existingBook);
                transaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void remove(int id) {
        Book book = findById(id);
        if (book != null) {
            Transaction transaction = null;
            try (Session session = sessionFactory.openSession()) {
                transaction = session.beginTransaction();
                session.delete(book);
                transaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
                if (transaction != null) {
                    transaction.rollback();
                }
            }
        }
    }
}
