package org.lostfan.ktv.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;

import org.lostfan.ktv.domain.RenderedService;

public interface RenderedServiceDAO extends EntityDAO<RenderedService> {

    List<RenderedService> getByMonth(LocalDate date);

    List<RenderedService> getByDate(LocalDate date);

    List<RenderedService> getBySubscriber(int subscriberAccount);

    List<RenderedService> getByService(int serviceId);

    List<RenderedService> getAll(int subscriberAccount, int serviceId);

    List<RenderedService> getAll(int serviceId, LocalDate date);

    List<RenderedService> getAllForMonth(int serviceId, LocalDate date);

    List<RenderedService> getAllForMonth(int serviceId, int subscriberAccount, LocalDate date);

    Map<Integer, BigDecimal> getAllRenderedServicesPriceInMonthForSubscriber(LocalDate date);

    Map<Integer, BigDecimal> getAllRenderedServicesPriceInMonthForSubscriberByServiceId(int serviceId, LocalDate date);

    Map<Integer, BigDecimal> getAllRenderedServicesPriceBeforeDate(LocalDate date);

    Map<Integer, BigDecimal> getAllRenderedServicesPriceForSubscriberByServiceIdBeforeDate(int serviceId, LocalDate date);

    RenderedService getFirstRenderedServiceLessDate(int serviceId, int subscriberAccount, LocalDate date);

    Map<Integer, List<RenderedService>> getServiceAndSubscriberRenderedServiceMap();

    List<RenderedService> getSubscriptionFeesByPeriodDate(LocalDate beginDate, LocalDate endDate);

    void deleteSubscriptionFeesByPeriodDate(LocalDate beginDate, LocalDate endDate);

    Map<Integer, RenderedService> getAllRenderedServicesBetweenDates(int serviceId, LocalDate beginDate, LocalDate endDate);
}
