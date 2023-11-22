package org.lostfan.ktv.model;

import org.lostfan.ktv.dao.*;
import org.lostfan.ktv.domain.Tariff;
import org.lostfan.ktv.model.dto.SubscriberAndTariffDTO;
import org.lostfan.ktv.utils.excel.SubscriberAndTariffExcel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChannelsReportModel  {

    private SubscriberDAO subscriberDAO = DAOFactory.getDefaultDAOFactory().getSubscriberDAO();
    private TariffDAO tariffDAO = DAOFactory.getDefaultDAOFactory().getTariffDAO();
    private StreetDAO streetDAO = DAOFactory.getDefaultDAOFactory().getStreetDAO();

    public ChannelsReportModel() {
    }


    public String getEntityNameKey() {
        return "channelsReport";
    }

    public List<SubscriberAndTariffDTO> getData(LocalDate date) {

        List<SubscriberAndTariffDTO> subscriberAndTariffDTOsResult = new ArrayList<>();
        Map<Integer, Integer> subscribersWithCurrentTariffs = subscriberDAO.getSubscribersWithCurrentTariffsByDate(date);

        for (Map.Entry<Integer, Integer> subscriberWithCurrentTariffs : subscribersWithCurrentTariffs.entrySet()) {
            if(subscriberWithCurrentTariffs.getValue() != null && subscriberWithCurrentTariffs.getValue() != 0) {
                if (subscriberDAO.getSubscriberSessionAtDate(subscriberWithCurrentTariffs.getKey(), date) != null) {


//            if (integerBigDecimalEntry.getValue().add(payedSum.negate()).compareTo(BigDecimal.ZERO) != 0) {
//                if(!subscribersWithsubscriptionFeeByPeriod.contains(integerBigDecimalEntry.getKey())) {
//                      if (subscriberDAO.getNotClosedSubscriberSession(integerBigDecimalEntry.getKey(), LocalDate.now()) == null &&
//                              subscriberDAO.getSubscriberSessions(integerBigDecimalEntry.getKey()).size() != 0) {
//
                    SubscriberAndTariffDTO subscriberAndTariffDTO = new SubscriberAndTariffDTO();
                    subscriberAndTariffDTO.setSubscriberAccount(subscriberWithCurrentTariffs.getKey());
                    Tariff tariff = tariffDAO.get(subscriberWithCurrentTariffs.getValue());
                    subscriberAndTariffDTO.setTariff(tariff);
                    subscriberAndTariffDTO.setSubscriber(subscriberDAO.get(subscriberAndTariffDTO.getSubscriberAccount()));
                    if (subscriberAndTariffDTO.getSubscriber() != null) {
                        subscriberAndTariffDTO.setSubscriberStreet(streetDAO.get(subscriberAndTariffDTO.getSubscriber().getStreetId()));
                    }
                    subscriberAndTariffDTOsResult.add(subscriberAndTariffDTO);
//                      }
//                }
//            }
                }
            }
        }
        System.out.println(subscriberAndTariffDTOsResult.size());
        return subscriberAndTariffDTOsResult;
    }

    public String generateExcelReport(LocalDate date) {
        SubscriberAndTariffExcel subscriberAndTariffDTOExcel = new SubscriberAndTariffExcel("channelsReport", date);
        subscriberAndTariffDTOExcel.setAllTariffs(tariffDAO.getAll().stream().filter(e -> e.getId() != 0).collect(Collectors.toList()));
        subscriberAndTariffDTOExcel.setSubscriberAndTariffDTOList(getData(date));

        return subscriberAndTariffDTOExcel.generate();
    }

}
