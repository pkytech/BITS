/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.billing.db.repository;

import edu.bits.mtech.billing.db.bo.Bill;
import edu.bits.mtech.billing.db.bo.Order;
import edu.bits.mtech.billing.db.bo.Payment;
import edu.bits.mtech.common.bo.Event;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository implementation.
 *
 * @author Tushar Phadke
 */
@Service
public class BillRepositoryImpl implements BillRepository {

    private static final Logger logger = Logger.getLogger(BillRepositoryImpl.class.getName());

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Object object) {
        logger.info("Save called: ");
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(object);

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

            payment = session.get(Payment.class, key);

            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to fetch entity", e);
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

            order = session.get(Order.class, key);

            logger.info("Order fetched from db: " + order);

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

    @Override
    public Event findEventById(String key) {
        logger.info("Save event called: ");
        Transaction transaction = null;
        Session session = null;
        Event event = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            event = session.get(Event.class, key);

            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to fetch entity", e);
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
    public void update(Object object) {
        logger.info("Update Payment called: ");
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.update(object);

            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to update ", e);
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
    public Bill findBillByKey(String key) {
        logger.info("Save Bill called: ");
        Transaction transaction = null;
        Session session = null;
        Bill bill = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            bill = session.get(Bill.class, key);

            logger.info("Bill fetched from db: " + bill);

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
        return bill;
    }
}
