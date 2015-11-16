package org.lostfan.ktv.domain;

import java.time.LocalDate;

public class Payment extends DefaultEntity {

    private Integer id;

    private Integer paymentTypeId;

    private Integer servicePaymentId;

    private int subscriberId;

    private int price;

    private LocalDate date;

    public Integer getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(Integer paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public Integer getServicePaymentId() {
        return servicePaymentId;
    }

    public void setServicePaymentId(Integer servicePaymentId) {
        this.servicePaymentId = servicePaymentId;
    }

    public int getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String getName() {
        return id.toString();
    }
}
