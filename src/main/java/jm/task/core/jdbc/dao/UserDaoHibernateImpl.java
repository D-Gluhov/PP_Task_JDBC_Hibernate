package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    private static final String SQL_QUERY_CREATE_TABLE_USERS = """
            CREATE TABLE IF NOT EXISTS users (id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                name VARCHAR(30) NOT NULL,
                                                last_name VARCHAR(30),
                                                age TINYINT)""";
    private static final String SQL_QUERY_DROP_TABLE_USERS = "DROP TABLE IF EXISTS users";
    private static final String SQL_QUERY_CLEAN_TABLE_USERS = "TRUNCATE TABLE users";
    private static final String SQL_QUERY_SELECT_ALL_USERS = "SELECT id, name, last_name, age FROM users";


    public UserDaoHibernateImpl() {
    }


    @Override
    public void createUsersTable() {
        try (SessionFactory sessionFactory = Util.getSessionFactory();
             Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.createSQLQuery(SQL_QUERY_CREATE_TABLE_USERS).executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Override
    public void dropUsersTable() {
        try (SessionFactory sessionFactory = Util.getSessionFactory();
             Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.createSQLQuery(SQL_QUERY_DROP_TABLE_USERS).executeUpdate();
            session.getTransaction().commit();
        }
    }
        // закрытие с использованием try with resources
    @Override
    public void saveUser(String name, String lastName, byte age) {
        SessionFactory sessionFactory = Util.getSessionFactory();
        Session session = sessionFactory.getCurrentSession();
        var transaction = session.getTransaction();
        try (sessionFactory; session) {
            transaction.begin();
            session.save(new User(name, lastName, age));
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }
                        //  ручное закрытие
    @Override
    public void removeUserById(long id) {
        SessionFactory sessionFactory = null;
        Session session = null;
        try {
            sessionFactory = Util.getSessionFactory();
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.remove(session.get(User.class, id));
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            if(sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = null;
        try (SessionFactory sessionFactory = Util.getSessionFactory();
             Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            users = session.createSQLQuery(SQL_QUERY_SELECT_ALL_USERS).addEntity(User.class).getResultList();
            session.getTransaction().commit();
        }
        return users;
    }

    @Override
    public void cleanUsersTable() {
        try (SessionFactory sessionFactory = Util.getSessionFactory();
             Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.createSQLQuery(SQL_QUERY_CLEAN_TABLE_USERS).executeUpdate();
            session.getTransaction().commit();
        }
    }
}
