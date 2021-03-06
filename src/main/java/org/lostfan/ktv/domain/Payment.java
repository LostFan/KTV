package org.lostfan.ktv.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Payment extends DefaultEntity implements Document  {

    private Integer id;

    private Integer paymentTypeId;

    private Integer servicePaymentId;

    private Integer renderedServicePaymentId;

    private Integer subscriberAccount;

    private BigDecimal price;

    private LocalDate date;

    private String bankFileName;

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

    public Integer getRenderedServicePaymentId() {
        return renderedServicePaymentId;
    }

    public void setRenderedServicePaymentId(Integer renderedServicePaymentId) {
        this.renderedServicePaymentId = renderedServicePaymentId;
    }

    public Integer getSubscriberAccount() {
        return subscriberAccount;
    }

    public void setSubscriberAccount(Integer subscriberAccount) {
        this.subscriberAccount = subscriberAccount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
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

    public String getBankFileName() {
        return bankFileName;
    }

    public void setBankFileName(String bankFileName) {
        this.bankFileName = bankFileName;
    }

    @Override
    public String getName() {
        return id.toString();
    }
}
