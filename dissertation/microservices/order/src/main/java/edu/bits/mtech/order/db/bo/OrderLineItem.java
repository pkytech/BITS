/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization.
 */

package edu.bits.mtech.order.db.bo;

import javax.persistence.*;

/**
 * Entity for OrderLineItem.
 *
 * @author Tushar Phadke
 */
@Entity
@Table(name = "ORDER_LINE_ITEM")
public class OrderLineItem {

    private String orderLineItemId;
    private Order order;
    private String productName;
    private double quantity;
    private double price;
    private double cost;

    @Id
    @Column(name = "ORDER_LINE_ITEM_ID", length = 50)
    public String getOrderLineItemId() {
        return orderLineItemId;
    }

    public void setOrderLineItemId(String orderLineItemId) {
        this.orderLineItemId = orderLineItemId;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Column(name = "PRODUCT_NAME", length = 50)
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Column(name = "ORDERED_QUANTITY")
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Column(name = "COST")
    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Column(name = "PRICE")
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "OrderLineItem{" +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", cost=" + cost +
                '}';
    }
}
