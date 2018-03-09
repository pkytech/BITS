/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.payment.db.repository;

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
    public Payment findPaymentByKey(String key) {

        logger.info("Get payment called: ");
        Transaction transaction = null;
        Session session = null;
        Payment payment = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            payment = session.load(Payment.class, key);

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

            order = session.load(Order.class, key);

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
}
