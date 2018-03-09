/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.db.repository;

import edu.bits.mtech.order.db.bo.Bill;
import edu.bits.mtech.order.db.bo.Order;
import edu.bits.mtech.order.db.bo.Payment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository for saving payment object.
 *
 * @author Tushar Phadke
 */
@Service
public class OrderRepositoryImpl implements OrderRepository {

    private static final Logger logger = Logger.getLogger(OrderRepositoryImpl.class.getName());

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Payment payment) {
        logger.info("Save Payment called: ");
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(payment);

            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to save payment entity", e);
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void save(Order order) {
        logger.info("Save Order called: ");
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(order);

            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to save order entity", e);
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void save(Bill bill) {
        logger.info("Save Bill called: ");
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(bill);

            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to save entity", e);
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Payment findPaymentByKey(String key) {

        logger.info("Save Bill called: ");
        Transaction transaction = null;
        Session session = null;
        Payment payment = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            payment = session.load(Payment.class, key);

            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to get entity", e);
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return payment;
    }

    @Override
    public Order findOrderByKey(String key) {
        logger.info("Save Bill called: ");
        Transaction transaction = null;
        Session session = null;
        Order order = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            order = session.load(Order.class, key);

            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to get entity", e);
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return order;
    }
}
