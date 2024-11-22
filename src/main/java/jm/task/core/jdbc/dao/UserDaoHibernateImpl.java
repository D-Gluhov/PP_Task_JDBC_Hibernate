package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    private static final String SQL_QUERY_CREATE_TABLE_USERS = """
            CREATE TABLE IF NOT EXISTS users (id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                name VARCHAR(30) NOT NULL,
                                                last_name VARCHAR(30),
                                                age TINYINT)""";
    private static final String SQL_QUERY_DROP_TABLE_USERS = "DROP TABLE IF EXISTS users";
    private static final String SQL_QUERY_CLEAN_TABLE_USERS = "TRUNCATE TABLE users";

    private final SessionFactory sessionFactory = Util.getSessionFactory();

    public UserDaoHibernateImpl() {}

    @Override
    public void createUsersTable() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.createSQLQuery(SQL_QUERY_CREATE_TABLE_USERS).executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Override
    public void dropUsersTable() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.createSQLQuery(SQL_QUERY_DROP_TABLE_USERS).executeUpdate();
            session.getTransaction().commit();
        }
    }

    // закрытие с использованием try with resources
    @Override
    public void saveUser(String name, String lastName, byte age) {
        Session session = sessionFactory.getCurrentSession();
        var transaction = session.getTransaction();
        try (session) {
            transaction.begin();
            session.save(new User(name, lastName, age));
            transaction.commit();
        } catch (HibernateException e) {
            try {
            transaction.rollback();
            } catch (HibernateException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //  ручное закрытие
    @Override
    public void removeUserById(long id) {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.createQuery("DELETE FROM User WHERE id =:id")
                    .setParameter("id", id)
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            try {
            session.getTransaction().rollback();
            } catch (HibernateException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = null;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            users = session.createQuery("from User", User.class).getResultList();
            session.getTransaction().commit();
        }
        return users;
    }

    @Override
    public void cleanUsersTable() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.createSQLQuery(SQL_QUERY_CLEAN_TABLE_USERS).executeUpdate();
            session.getTransaction().commit();
        }
    }
}
