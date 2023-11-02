package org.lostfan.ktv.model.dto;

import org.lostfan.ktv.domain.Street;
import org.lostfan.ktv.domain.Subscriber;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreditSheetTableDTO {

    public CreditSheetTableDTO(TurnoverSheetTableDTO turnoverSheetTableDTO) {
        this.broughtForwardBalanceCredit = turnoverSheetTableDTO.getBroughtForwardBalanceCredit();
        this.broughtForwardBalanceDebit = turnoverSheetTableDTO.getBroughtForwardBalanceDebit();
        this.carriedForwardBalanceCredit = turnoverSheetTableDTO.getCarriedForwardBalanceCredit();
        this.carriedForwardBalanceDebit = turnoverSheetTableDTO.getCarriedForwardBalanceDebit();
        this.turnoverBalanceCredit = turnoverSheetTableDTO.getTurnoverBalanceCredit();
        this.turnoverBalanceDebit = turnoverSheetTableDTO.getTurnoverBalanceDebit();
        this.taxesBase = turnoverSheetTableDTO.getTaxesBase();
        this.subscriberAccount = turnoverSheetTableDTO.getSubscriberAccount();
        this.subscriberStreet = turnoverSheetTableDTO.getSubscriberStreet();
        this.subscriber = turnoverSheetTableDTO.getSubscriber();
        this.serviceId = turnoverSheetTableDTO.getServiceId();
        this.date = turnoverSheetTableDTO.getDate();
    }


    private LocalDate disconnectionDate;

    private Integer subscriberAccount;

    private LocalDate date;

    private BigDecimal broughtForwardBalanceCredit;

    private BigDecimal broughtForwardBalanceDebit;

    private BigDecimal carriedForwardBalanceCredit;

    private BigDecimal carriedForwardBalanceDebit;

    private BigDecimal turnoverBalanceCredit;

    private BigDecimal turnoverBalanceDebit;

    private Integer serviceId;

    private Subscriber subscriber;

    private Street subscriberStreet;

    private BigDecimal taxesBase;

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

    public BigDecimal getBroughtForwardBalanceCredit() {
        return broughtForwardBalanceCredit;
    }

    public void setBroughtForwardBalanceCredit(BigDecimal broughtForwardBalanceCredit) {
        this.broughtForwardBalanceCredit = broughtForwardBalanceCredit;
    }

    public BigDecimal getBroughtForwardBalanceDebit() {
        return broughtForwardBalanceDebit;
    }

    public void setBroughtForwardBalanceDebit(BigDecimal broughtForwardBalanceDebit) {
        this.broughtForwardBalanceDebit = broughtForwardBalanceDebit;
    }

    public BigDecimal getCarriedForwardBalanceCredit() {
        return carriedForwardBalanceCredit;
    }

    public void setCarriedForwardBalanceCredit(BigDecimal carriedForwardBalanceCredit) {
        this.carriedForwardBalanceCredit = carriedForwardBalanceCredit;
    }

    public BigDecimal getCarriedForwardBalanceDebit() {
        return carriedForwardBalanceDebit;
    }

    public void setCarriedForwardBalanceDebit(BigDecimal carriedForwardBalanceDebit) {
        this.carriedForwardBalanceDebit = carriedForwardBalanceDebit;
    }

    public BigDecimal getTurnoverBalanceCredit() {
        return turnoverBalanceCredit;
    }

    public void setTurnoverBalanceCredit(BigDecimal turnoverBalanceCredit) {
        this.turnoverBalanceCredit = turnoverBalanceCredit;
    }

    public BigDecimal getTurnoverBalanceDebit() {
        return turnoverBalanceDebit;
    }

    public void setTurnoverBalanceDebit(BigDecimal turnoverBalanceDebit) {
        this.turnoverBalanceDebit = turnoverBalanceDebit;
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

    public BigDecimal getTaxesBase() {
        return taxesBase;
    }

    public void setTaxesBase(BigDecimal taxesBase) {
        this.taxesBase = taxesBase;
    }

    public LocalDate getDisconnectionDate() {
        return disconnectionDate;
    }

    public void setDisconnectionDate(LocalDate disconnectionDate) {
        this.disconnectionDate = disconnectionDate;
    }
}
