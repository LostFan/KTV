package org.lostfan.ktv.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SUBSCRIBER")
public class Subscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private String id;

    @Column(name = "NAME", nullable = false)
    private String name;

    private int balance;

    private List<SubscriberSession> subscriberSessions;

    private boolean connected;

    private List<SubscriberTariff> subscriberTariffs;

    private List<SubscriberService> subscriberServices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public List<SubscriberSession> getSubscriberSessions() {
        return subscriberSessions;
    }

    public void setSubscriberSessions(List<SubscriberSession> subscriberSessions) {
        this.subscriberSessions = subscriberSessions;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public List<SubscriberTariff> getSubscriberTariffs() {
        return subscriberTariffs;
    }

    public void setSubscriberTariffs(List<SubscriberTariff> subscriberTariffs) {
        this.subscriberTariffs = subscriberTariffs;
    }

    public List<SubscriberService> getSubscriberServices() {
        return subscriberServices;
    }

    public void setSubscriberServices(List<SubscriberService> subscriberServices) {
        this.subscriberServices = subscriberServices;
    }
}