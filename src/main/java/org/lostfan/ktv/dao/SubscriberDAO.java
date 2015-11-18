package org.lostfan.ktv.dao;

import java.time.LocalDate;
import java.util.List;

import org.lostfan.ktv.domain.Subscriber;
import org.lostfan.ktv.domain.SubscriberSession;
import org.lostfan.ktv.domain.SubscriberTariff;

public interface SubscriberDAO extends EntityDAO<Subscriber> {

    List<Subscriber> getAll();

    Subscriber get(int id);

    Subscriber save(Subscriber subscriber);

    Subscriber update(Subscriber subscriber);

    void delete(int subscriberId);

    int getBalanceByDate(int subscriberId, LocalDate date);

    Integer getTariffIdByDate(int subscriberId, LocalDate date);

    Integer getSessionIdByDate(int subscriberId, LocalDate date);

    List<SubscriberSession> getSubscriberSessions(int subscriberId);

    List<SubscriberTariff> getSubscriberTariffs(int subscriberId);

    SubscriberSession getSubscriberSession(int subscriberSessionId);

    void saveSubscriberSession(SubscriberSession subscriberSession);

    void updateSubscriberSession(SubscriberSession subscriberSession);

    SubscriberTariff getSubscriberTariff(int subscriberTariffId);

    void saveSubscriberTariff(SubscriberTariff subscriberTariff);

    void updateSubscriberTariff(SubscriberTariff subscriberTariff);

    List<Subscriber> getSubscribersByBeginningPartOfName(String str);

    List<Subscriber> getSubscribersByBeginningPartOfAccount(String str);

    List<Subscriber> getAllContainsInName(String str);
}
