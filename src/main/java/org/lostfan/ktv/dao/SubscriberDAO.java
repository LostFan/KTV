package org.lostfan.ktv.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lostfan.ktv.domain.Subscriber;
import org.lostfan.ktv.domain.SubscriberSession;
import org.lostfan.ktv.domain.SubscriberTariff;
import org.lostfan.ktv.model.searcher.SubscriberSearchCriteria;

public interface SubscriberDAO extends EntityDAO<Subscriber> {

    Double getBalanceByDate(int subscriberId, LocalDate date);

    Integer getTariffIdByDate(int subscriberId, LocalDate date);

    Integer getSessionIdByDate(int subscriberId, LocalDate date);

    List<SubscriberSession> getSubscriberSessions(int subscriberId);

    List<SubscriberTariff> getSubscriberTariffs(int subscriberId);

    SubscriberSession getSubscriberSessionByConnectionDate(Integer subscriberAccount, LocalDate connectionDate);

    SubscriberSession getSubscriberSessionByDisconnectionDate(Integer subscriberAccount, LocalDate disconnectionDate);

    void saveSubscriberSession(SubscriberSession subscriberSession);

    SubscriberSession getSubscriberSessionAfterDate(Integer subscriberId, LocalDate localDate);

    SubscriberSession getNotClosedSubscriberSession(Integer subscriberAccount, LocalDate connectionDate);

    SubscriberSession getClosedSubscriberSession(Integer subscriberAccount, LocalDate date);

    SubscriberSession getSubscriberSessionAtDate(Integer subscriberId, LocalDate localDate);

    void updateSubscriberSession(SubscriberSession subscriberSession);

    void deleteSubscriberSession(Integer subscriberId, LocalDate localDate);

    SubscriberTariff getSubscriberTariffByConnectionDate(Integer subscriberId, LocalDate localDate);

    SubscriberTariff getSubscriberTariffByDisconnectionDate(Integer subscriberId, LocalDate localDate);

    SubscriberTariff getSubscriberTariffAtDate(Integer subscriberId, LocalDate localDate);

    SubscriberTariff getSubscriberTariffAfterDate(Integer subscriberId, LocalDate localDate);

    SubscriberTariff getNotClosedSubscriberTariff(Integer subscriberId, LocalDate localDate);

    List<SubscriberTariff> getSubscriberTariffsForInterval(Integer subscriberId, LocalDate beginDate, LocalDate endDate);

    default List<SubscriberTariff> getSubscriberTariffsForMonth(Integer subscriberId, LocalDate localDate) {
        LocalDate beginDate = localDate.withDayOfMonth(1);
        LocalDate endDate = localDate.withDayOfMonth(1).plusMonths(1);
        return getSubscriberTariffsForInterval(subscriberId ,beginDate ,endDate);
    };

    List<SubscriberSession> getSubscriberSessionsForMonth(Integer subscriberId, LocalDate localDate);

    SubscriberTariff getSubscriberTariffAllMonth(Integer subscriberId, LocalDate localDate);

    SubscriberSession getSubscriberSessionAllMonth(Integer subscriberId, LocalDate localDate);

    SubscriberTariff getSubscriberTariffBeginInPrevMonthEndInCurrentMonth(Integer subscriberId, LocalDate localDate);

    List<SubscriberTariff> getSubscriberTariffsBeginInCurrentMonthEndInCurrentMonth(Integer subscriberId, LocalDate localDate);

    SubscriberTariff getSubscriberTariffBeginInCurrentMonth(Integer subscriberId, LocalDate localDate);

    void saveSubscriberTariff(SubscriberTariff subscriberTariff);

    void updateSubscriberTariff(SubscriberTariff subscriberTariff);

    void deleteSubscriberTariff(Integer subscriberId, LocalDate localDate);

    List<Subscriber> getSubscribersByBeginningPartOfName(String str);

    List<Subscriber> getSubscribersByBeginningPartOfAccount(String str);

    HashMap<Integer, BigDecimal> getServicesBalanceBySubscriberIdAndDate(Integer subscriberId, LocalDate date);

    HashMap<Integer, BigDecimal> getServicesBalance(Integer subscriberAccount);

    List<Subscriber> search(SubscriberSearchCriteria criteria);

    Integer getLastSubscriberAccount();

    default Map<Integer, Integer> getSubscribersWithCurrentTariffs() {
        return getSubscribersWithCurrentTariffsByDate(LocalDate.now());
    };

    Map<Integer, Integer> getSubscribersWithCurrentTariffsByDate(LocalDate date);

    List<Integer> getConnectedSubscribers();

    List<Integer> getConnectedSubscribers(LocalDate date);

    List<Integer> getInactiveSubscribersForPeriod(LocalDate startDate, LocalDate endDate);
}
