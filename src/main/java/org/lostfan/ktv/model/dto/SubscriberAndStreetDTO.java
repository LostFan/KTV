package org.lostfan.ktv.model.dto;

import org.lostfan.ktv.domain.Street;
import org.lostfan.ktv.domain.Subscriber;
import org.lostfan.ktv.domain.Tariff;

public class SubscriberAndStreetDTO {

    private Integer subscriberAccount;

    private Subscriber subscriber;

    private Street subscriberStreet;

    public SubscriberAndStreetDTO() {
    }

    public Integer getSubscriberAccount() {
        return subscriberAccount;
    }

    public void setSubscriberAccount(Integer subscriberAccount) {
        this.subscriberAccount = subscriberAccount;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public Street getSubscriberStreet() {
        return subscriberStreet;
    }

    public void setSubscriberStreet(Street subscriberStreet) {
        this.subscriberStreet = subscriberStreet;
    }
}
