package org.lostfan.ktv.domain;

public class Payment {

    int id;

    private String name;

    Service servicePayment;

    Subscriber subscriber;

    int price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Service getServicePayment() {
        return servicePayment;
    }

    public void setServicePayment(Service servicePayment) {
        this.servicePayment = servicePayment;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
