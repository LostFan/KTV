package org.lostfan.ktv.model;

import org.lostfan.ktv.dao.*;
import org.lostfan.ktv.domain.RenderedService;
import org.lostfan.ktv.domain.Service;
import org.lostfan.ktv.domain.Subscriber;
import org.lostfan.ktv.domain.SubscriberSession;
import org.lostfan.ktv.model.dto.ServiceReportSheetTableDTO;
import org.lostfan.ktv.model.entity.BaseModel;
import org.lostfan.ktv.utils.BaseObservable;
import org.lostfan.ktv.utils.excel.DisconnectSubscribersReportExcel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DisconnectedSubscribersReportModel extends BaseObservable implements BaseModel {

    private ServiceDAO serviceDAO = DAOFactory.getDefaultDAOFactory().getServiceDAO();
    private SubscriberDAO subscriberDAO = DAOFactory.getDefaultDAOFactory().getSubscriberDAO();
    private StreetDAO streetDAO = DAOFactory.getDefaultDAOFactory().getStreetDAO();
    private RenderedServiceDAO renderedServiceDAO = DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO();
    private PaymentDAO paymentDAO = DAOFactory.getDefaultDAOFactory().getPaymentDAO();

    public DisconnectedSubscribersReportModel() {
    }

    @Override
    public String getEntityNameKey() {
        return "disconnectedSubscribersSheet";
    }

    @Override
    public List<EntityField> getFields() {
        return new ArrayList<>();
    }



    public List<ServiceReportSheetTableDTO> getTurnoverSheetData(LocalDate date) {

        List<ServiceReportSheetTableDTO> tableDTO = new ArrayList<>();

        List <Subscriber> subscribers = subscriberDAO.getAll();
        List <Integer> connectionSubscriberIds = subscriberDAO.getConnectedSubscribers(date);

        subscribers.stream().filter(e -> !connectionSubscriberIds.contains(e.getId()))
                .forEach(e -> {
                    ServiceReportSheetTableDTO serviceReportSheetTableDTO = new ServiceReportSheetTableDTO();
                    serviceReportSheetTableDTO.setSubscriberAccount(e.getId());
                    serviceReportSheetTableDTO.setSubscriber(e);
                    tableDTO.add(serviceReportSheetTableDTO);
                });


        for (ServiceReportSheetTableDTO serviceReportSheetTableDTO : tableDTO) {
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
                                      LocalDate date) {
        DisconnectSubscribersReportExcel disconnectSubscribersReportExcel = new DisconnectSubscribersReportExcel();
        disconnectSubscribersReportExcel.setServiceReportSheetTableDTOs(getTurnoverSheetData(date));
        disconnectSubscribersReportExcel.setDate(date);

        return disconnectSubscribersReportExcel.generate();
    }

}
