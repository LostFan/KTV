package org.lostfan.ktv.model;

import org.lostfan.ktv.dao.*;
import org.lostfan.ktv.domain.Subscriber;
import org.lostfan.ktv.domain.SubscriberSession;
import org.lostfan.ktv.domain.Tariff;
import org.lostfan.ktv.model.dto.SubscriberAndTariffDTO;
import org.lostfan.ktv.model.entity.BaseModel;
import org.lostfan.ktv.utils.BaseObservable;
import org.lostfan.ktv.utils.excel.ConnectedDisconnectSubscribersReportExcel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectedDisconnectedSubscribersReportModel extends BaseObservable implements BaseModel {

    private SubscriberDAO subscriberDAO = DAOFactory.getDefaultDAOFactory().getSubscriberDAO();
    private StreetDAO streetDAO = DAOFactory.getDefaultDAOFactory().getStreetDAO();
    private TariffDAO tariffDAO = DAOFactory.getDefaultDAOFactory().getTariffDAO();

    public ConnectedDisconnectedSubscribersReportModel() {
    }

    @Override
    public String getEntityNameKey() {
        return "connectedDisconnectedSubscribersSheet";
    }

    @Override
    public List<EntityField> getFields() {
        return new ArrayList<>();
    }



    private List<SubscriberAndTariffDTO> getConnectedSheetData(LocalDate date) {

        List<SubscriberAndTariffDTO> tableDTO = new ArrayList<>();

        List <Subscriber> subscribers = subscriberDAO.getAll();
        List <Integer> connectionSubscriberIds = subscriberDAO.getConnectedSubscribers(date);

        List<Tariff> tariffs = tariffDAO.getAll();

        subscribers.stream().filter(e -> connectionSubscriberIds.contains(e.getId()))
                .forEach(e -> {
                    SubscriberAndTariffDTO serviceReportSheetTableDTO = new SubscriberAndTariffDTO();
                    serviceReportSheetTableDTO.setSubscriberAccount(e.getId());
                    serviceReportSheetTableDTO.setSubscriber(e);
                    tableDTO.add(serviceReportSheetTableDTO);
                });


        for (SubscriberAndTariffDTO serviceReportSheetTableDTO : tableDTO) {
            if(serviceReportSheetTableDTO.getSubscriber() != null) {
                serviceReportSheetTableDTO.setSubscriberStreet(streetDAO.get(serviceReportSheetTableDTO.getSubscriber().getStreetId()));
                Integer tariffId = subscriberDAO.getTariffIdByDate(serviceReportSheetTableDTO.getSubscriber().getId(), date);
                Tariff tariff = tariffs.stream().filter(e -> e.getId().equals(tariffId)).findFirst().orElseGet(Tariff::new);
                serviceReportSheetTableDTO.setTariff(tariff);
            }
        }
        return tableDTO;
    }

    private List<SubscriberAndTariffDTO> getDisconnectedSheetData(LocalDate date) {

        List<SubscriberAndTariffDTO> tableDTO = new ArrayList<>();

        List <Subscriber> subscribers = subscriberDAO.getAll();
        List <Integer> connectionSubscriberIds = subscriberDAO.getConnectedSubscribers(date);

        subscribers.stream().filter(e -> !connectionSubscriberIds.contains(e.getId()))
                .forEach(e -> {
                    SubscriberAndTariffDTO serviceReportSheetTableDTO = new SubscriberAndTariffDTO();
                    serviceReportSheetTableDTO.setSubscriberAccount(e.getId());
                    serviceReportSheetTableDTO.setSubscriber(e);
                    tableDTO.add(serviceReportSheetTableDTO);
                });


        for (SubscriberAndTariffDTO serviceReportSheetTableDTO : tableDTO) {
            if(serviceReportSheetTableDTO.getSubscriber() != null) {
                serviceReportSheetTableDTO.setSubscriberStreet(streetDAO.get(serviceReportSheetTableDTO.getSubscriber().getStreetId()));
            }
            SubscriberSession subscriberSession = subscriberDAO.getClosedSubscriberSession(serviceReportSheetTableDTO.getSubscriberAccount(), date);
            if (subscriberSession != null) {
                serviceReportSheetTableDTO.setDate(subscriberSession.getDisconnectionDate());
            }
        }
        return tableDTO.stream().filter(e -> e.getDate() != null).collect(Collectors.toList());
    }

    public String generateExcelReport(
                                      LocalDate date, boolean isConnected) {
        ConnectedDisconnectSubscribersReportExcel connectedDisconnectSubscribersReportExcel = new ConnectedDisconnectSubscribersReportExcel();
        connectedDisconnectSubscribersReportExcel.setServiceReportSheetTableDTOs(getSheetData(date, isConnected));
        connectedDisconnectSubscribersReportExcel.setDate(date);
        connectedDisconnectSubscribersReportExcel.setIsConnected(isConnected);

        return connectedDisconnectSubscribersReportExcel.generate();
    }

    public List<SubscriberAndTariffDTO> getSheetData(LocalDate date, boolean isConnected) {
        return isConnected ? getConnectedSheetData(date) : getDisconnectedSheetData(date);
    }

}
