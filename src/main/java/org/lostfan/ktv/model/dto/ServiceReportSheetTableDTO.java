package org.lostfan.ktv.model.dto;

import org.lostfan.ktv.domain.Street;
import org.lostfan.ktv.domain.Subscriber;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ServiceReportSheetTableDTO {

    private Integer subscriberAccount;

    private LocalDate date;

    private Integer serviceId;

    private Subscriber subscriber;

    private Street subscriberStreet;

    public Integer getSubscriberAccount() {
        return subscriberAccount;
    }

    public void setSubscriberAccount(Integer subscriberAccount) {
        this.subscriberAccount = subscriberAccount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
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
