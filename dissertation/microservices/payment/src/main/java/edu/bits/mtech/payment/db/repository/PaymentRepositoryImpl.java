/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.payment.db.repository;

import edu.bits.mtech.common.bo.Event;
import edu.bits.mtech.payment.db.bo.Order;
import edu.bits.mtech.payment.db.bo.Payment;
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
public class PaymentRepositoryImpl implements PaymentRepository {

    private static final Logger logger = Logger.getLogger(PaymentRepositoryImpl.class.getName());

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Payment payment) {
        logger.info("Save Payment called: " + payment);
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(payment.getOrder());
            session.saveOrUpdate(payment);

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
    public void save(Event event) {
        logger.info("Save Event called: " + event);
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(event);

            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to save event entity", e);
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

        logger.info("Get payment called: ");
        Transaction transaction = null;
        Session session = null;
        Payment payment = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            payment = session.get(Payment.class, key);
            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to get payment entity", e);
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
        logger.info("Get order called ");
        Transaction transaction = null;
        Session session = null;
        Order order = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            order = session.get(Order.class, key);
            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to get order entity", e);
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

    @Override
    public Event findEventById(String key) {
        logger.info("Get Event called ");
        Transaction transaction = null;
        Session session = null;
        Event event = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            event = session.get(Event.class, key);
            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to get event entity", e);
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return event;
    }

    @Override
    public void update(Payment payment) {
        logger.info("Update Payment called: " + payment);
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.update(payment);

            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to update entity", e);
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
    public void update(Order order) {
        logger.info("Update Order called: " + order);
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.update(order);

            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to update order ", e);
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
}
