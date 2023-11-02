package org.lostfan.ktv.model;

import org.lostfan.ktv.dao.DAOFactory;
import org.lostfan.ktv.dao.SubscriberDAO;
import org.lostfan.ktv.domain.Service;
import org.lostfan.ktv.domain.SubscriberSession;
import org.lostfan.ktv.model.dto.CreditSheetTableDTO;
import org.lostfan.ktv.model.dto.TurnoverSheetTableDTO;
import org.lostfan.ktv.utils.excel.CreditReportExcel;
import org.lostfan.ktv.utils.excel.TurnoverReportExcel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreditReportModel extends TurnoverReportModel {

    private SubscriberDAO subscriberDAO = DAOFactory.getDefaultDAOFactory().getSubscriberDAO();

    @Override
    public String getEntityNameKey() {
        return "creditReport";
    }

    public List<CreditSheetTableDTO> geCreditData(LocalDate date, Integer serviceId) {
        List<TurnoverSheetTableDTO> turnoverSheetData = super.getTurnoverSheetData(date, serviceId);
        List<CreditSheetTableDTO> creditSheetTableDTOS = turnoverSheetData.stream()
                .map(CreditSheetTableDTO::new)
                .filter(e -> BigDecimal.ZERO.compareTo(e.getCarriedForwardBalanceCredit()) != 0)
                .filter(e -> BigDecimal.ZERO.compareTo(e.getTurnoverBalanceDebit()) == 0)
                .collect(Collectors.toList());

        for (CreditSheetTableDTO creditSheetTableDTO : creditSheetTableDTOS) {
            SubscriberSession subscriberSession = subscriberDAO.getClosedSubscriberSession(creditSheetTableDTO.getSubscriberAccount(), date);
            if (subscriberSession != null) {
                creditSheetTableDTO.setDisconnectionDate(subscriberSession.getDisconnectionDate());
            }
        }
    return creditSheetTableDTOS;
    }

    public List<CreditSheetTableDTO> getCreditDataByAdditionalServices(LocalDate date) {
        List<Service> services = getAllServices().stream().filter(e -> e.isAdditionalService()).collect(Collectors.toList());
        List<CreditSheetTableDTO> turnoverSheetTableDTOs = new ArrayList<>();
        Integer count = 1;
        for (Service service : services) {
            notifyObservers(100 * count++ / services.size());
            turnoverSheetTableDTOs.addAll(geCreditData(date, service.getId()));
        }
        notifyObservers(100);
        return turnoverSheetTableDTOs;
    }

    public String generateExcelReport(Boolean isAdditional,
                                      Integer serviceId, LocalDate date) {
        CreditReportExcel turnoverReportExcel = new CreditReportExcel();
        if (serviceId != null) {
            turnoverReportExcel.setService(getService(serviceId));
        }
        if (isAdditional) {
            turnoverReportExcel.setCreditSheetTableDTOS(getCreditDataByAdditionalServices(date));
        } else {
            turnoverReportExcel.setCreditSheetTableDTOS(geCreditData(date, serviceId));
        }
        turnoverReportExcel.setDate(date);
        turnoverReportExcel.setAdditionalServices(getAllServices().stream().filter(e -> e.isAdditionalService()).collect(Collectors.toList()));
        turnoverReportExcel.setIsAdditional(isAdditional);
        return turnoverReportExcel.generate();
    }
}
