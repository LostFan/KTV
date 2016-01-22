package org.lostfan.ktv.dao;

import java.time.LocalDate;
import java.util.List;

import org.lostfan.ktv.domain.Service;
import org.lostfan.ktv.domain.ServicePrice;

public interface ServiceDAO extends EntityDAO<Service> {

    int getPriceByDate(int serviceId, LocalDate date);

    void savePrice(ServicePrice servicePrice);

    List<ServicePrice> getServicePricesByServiceId(int serviceId);

    void saveServicePrice(ServicePrice servicePrice);

    void deleteServicePrice(ServicePrice servicePrice);

    List<ServicePrice> getServicePrices(int tariffId);

    List<Service> getServicesByBeginningPartOfName(String str);
}
