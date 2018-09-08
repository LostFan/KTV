package org.lostfan.ktv.model;

import org.lostfan.ktv.dao.*;
import org.lostfan.ktv.domain.RenderedService;
import org.lostfan.ktv.domain.Service;
import org.lostfan.ktv.model.dto.ServiceReportSheetTableDTO;
import org.lostfan.ktv.model.entity.BaseModel;
import org.lostfan.ktv.utils.BaseObservable;
import org.lostfan.ktv.utils.excel.ServiceReportExcel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceReportModel extends BaseObservable implements BaseModel {

    private ServiceDAO serviceDAO = DAOFactory.getDefaultDAOFactory().getServiceDAO();
    private SubscriberDAO subscriberDAO = DAOFactory.getDefaultDAOFactory().getSubscriberDAO();
    private StreetDAO streetDAO = DAOFactory.getDefaultDAOFactory().getStreetDAO();
    private RenderedServiceDAO renderedServiceDAO = DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO();
    private PaymentDAO paymentDAO = DAOFactory.getDefaultDAOFactory().getPaymentDAO();

    public ServiceReportModel() {
    }

    @Override
    public String getEntityNameKey() {
        return "serviceSheet";
    }

    @Override
    public List<EntityField> getFields() {
        return new ArrayList<>();
    }

    public Service getService(Integer id) {
        return serviceDAO.get(id);
    }

    public List<Service> getAllServices() {
        return  serviceDAO.getAll();
    }

    public List<ServiceReportSheetTableDTO> getTurnoverSheetDataByAdditionalServices(LocalDate startDate, LocalDate endDate) {
        List<Service> services = getAllServices().stream().filter(e -> e.isAdditionalService()).collect(Collectors.toList());
        List<ServiceReportSheetTableDTO> serviceReportSheetTableDTOs = new ArrayList<>();
        Integer count = 1;
        for (Service service : services) {
            notifyObservers(100 * count++ / services.size());
            serviceReportSheetTableDTOs.addAll(getTurnoverSheetData(startDate, endDate, service.getId()));
        }
        notifyObservers(100);
        return serviceReportSheetTableDTOs;
    }


    public List<ServiceReportSheetTableDTO> getTurnoverSheetData(LocalDate startDate, LocalDate endDate, Integer serviceId) {
        List<ServiceReportSheetTableDTO> serviceReportSheetTableDTOs;
        final boolean isAddAllNullUsers;
        isAddAllNullUsers = true;

        Map<Integer, ServiceReportSheetTableDTO> tableDTOHashMap = new HashMap<>();
        Map<Integer, RenderedService> beginPeriodDebit = renderedServiceDAO.getAllRenderedServicesBetweenDates(serviceId, startDate, endDate);
        beginPeriodDebit.forEach((k, v) -> {
                    ServiceReportSheetTableDTO serviceReportSheetTableDTO = new ServiceReportSheetTableDTO();
                    serviceReportSheetTableDTO.setSubscriberAccount(k);
                    serviceReportSheetTableDTO.setServiceId(serviceId);
                    serviceReportSheetTableDTO.setDate(v.getDate());
                    tableDTOHashMap.put(k, serviceReportSheetTableDTO);
                }
        );

        serviceReportSheetTableDTOs = tableDTOHashMap.values().stream().sorted((dto1, dto2) -> dto1.getSubscriberAccount() - dto2.getSubscriberAccount())
                .filter(e -> isAddAllNullUsers)
                .collect(Collectors.toList());

        for (ServiceReportSheetTableDTO serviceReportSheetTableDTO : serviceReportSheetTableDTOs) {
            serviceReportSheetTableDTO.setSubscriber(subscriberDAO.get(serviceReportSheetTableDTO.getSubscriberAccount()));
            if(serviceReportSheetTableDTO.getSubscriber() != null) {
                serviceReportSheetTableDTO.setSubscriberStreet(streetDAO.get(serviceReportSheetTableDTO.getSubscriber().getStreetId()));
            }
        }
        return serviceReportSheetTableDTOs;
    }

    public String generateExcelReport(Boolean isAdditional,
                                      Integer serviceId, LocalDate startDate, LocalDate endDate) {
        ServiceReportExcel serviceReportExcel = new ServiceReportExcel();
        if (serviceId != null) {
            serviceReportExcel.setService(getService(serviceId));
        }
        if (isAdditional) {
            serviceReportExcel.setServiceReportSheetTableDTOs(getTurnoverSheetDataByAdditionalServices(startDate, endDate));
        } else {
            serviceReportExcel.setServiceReportSheetTableDTOs(getTurnoverSheetData(startDate, endDate, serviceId));
        }
        serviceReportExcel.setStartDate(startDate);
        serviceReportExcel.setEndDate(endDate);
        serviceReportExcel.setAdditionalServices(getAllServices().stream().filter(e -> e.isAdditionalService()).collect(Collectors.toList()));
        serviceReportExcel.setIsAdditional(isAdditional);
        return serviceReportExcel.generate();
    }

}
