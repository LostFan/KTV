package org.lostfan.ktv.model.dto;

import org.lostfan.ktv.domain.Street;
import org.lostfan.ktv.domain.Subscriber;
import org.lostfan.ktv.domain.Tariff;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SubscriberAndTariffDTO {

    private Integer subscriberAccount;

    private Integer tariffId;

    private Tariff tariff;

    private Subscriber subscriber;

    private Street subscriberStreet;

    private LocalDate date;

    public SubscriberAndTariffDTO() {
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

    public Integer getTariffId() {
        return tariffId;
    }

    public void setTariffId(Integer tariffId) {
        this.tariffId = tariffId;
    }

    public Tariff getTariff() {
        return tariff;
    }

    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
