package org.lostfan.ktv.model;

import org.lostfan.ktv.dao.*;
import org.lostfan.ktv.domain.Service;
import org.lostfan.ktv.model.dto.TurnoverSheetTableDTO;
import org.lostfan.ktv.model.entity.BaseModel;
import org.lostfan.ktv.utils.BaseObservable;
import org.lostfan.ktv.utils.excel.TurnoverReportExcel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TurnoverReportModel extends BaseObservable implements BaseModel {

    private ServiceDAO serviceDAO = DAOFactory.getDefaultDAOFactory().getServiceDAO();
    private SubscriberDAO subscriberDAO = DAOFactory.getDefaultDAOFactory().getSubscriberDAO();
    private StreetDAO streetDAO = DAOFactory.getDefaultDAOFactory().getStreetDAO();
    private RenderedServiceDAO renderedServiceDAO = DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO();
    private PaymentDAO paymentDAO = DAOFactory.getDefaultDAOFactory().getPaymentDAO();

    public TurnoverReportModel() {
    }

    @Override
    public String getEntityNameKey() {
        return "turnoverSheet";
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

    public List<TurnoverSheetTableDTO> getTurnoverSheetDataByAdditionalServices(LocalDate date) {
        List<Service> services = getAllServices().stream().filter(e -> e.isAdditionalService()).collect(Collectors.toList());
        List<TurnoverSheetTableDTO> turnoverSheetTableDTOs = new ArrayList<>();
        Integer count = 1;
        for (Service service : services) {
            notifyObservers(100 * count++ / services.size());
            turnoverSheetTableDTOs.addAll(getTurnoverSheetData(date, service.getId()));
        }
        notifyObservers(100);
        return turnoverSheetTableDTOs;
    }


    public List<TurnoverSheetTableDTO> getTurnoverSheetData(LocalDate date, Integer serviceId) {
        List<TurnoverSheetTableDTO> turnoverSheetTableDTOs;
        final boolean isAddAllNullUsers;
        if(serviceId == FixedServices.SUBSCRIPTION_FEE.getId()) {
            isAddAllNullUsers = true;
        } else {
            isAddAllNullUsers = false;
        }

        Map<Integer, TurnoverSheetTableDTO> tableDTOHashMap = new HashMap<>();
        Map<Integer, BigDecimal> beginPeriodDebit = renderedServiceDAO.getAllRenderedServicesPriceForSubscriberByServiceIdBeforeDate(serviceId, date);
        beginPeriodDebit.forEach((k, v) -> {
                    TurnoverSheetTableDTO turnoverSheetTableDTO = new TurnoverSheetTableDTO();
                    turnoverSheetTableDTO.setSubscriberAccount(k);
                    turnoverSheetTableDTO.setServiceId(serviceId);
                    turnoverSheetTableDTO.setBroughtForwardBalanceDebit(v);
                    tableDTOHashMap.put(k, turnoverSheetTableDTO);
                }
        );
        Map<Integer, BigDecimal> beginPeriodCredit = paymentDAO.getAllPaymentsPriceForSubscriberToDate(serviceId, date);
        beginPeriodCredit.forEach((k, v) -> {
            if (tableDTOHashMap.containsKey(k)) {
                BigDecimal broughtForwardBalanceDebit = tableDTOHashMap.get(k).getBroughtForwardBalanceDebit();
                if(broughtForwardBalanceDebit.compareTo(v) <= 0) {
                    tableDTOHashMap.get(k).setBroughtForwardBalanceDebit(BigDecimal.ZERO);
                    tableDTOHashMap.get(k).setBroughtForwardBalanceCredit(v.add(broughtForwardBalanceDebit.negate()));
                } else {
                    tableDTOHashMap.get(k).setBroughtForwardBalanceDebit(broughtForwardBalanceDebit.add(v.negate()));
                }
            } else {
                TurnoverSheetTableDTO turnoverSheetTableDTO = new TurnoverSheetTableDTO();
                turnoverSheetTableDTO.setSubscriberAccount(k);
                turnoverSheetTableDTO.setServiceId(serviceId);
                if(v.compareTo(BigDecimal.ZERO) > 0) {
                    turnoverSheetTableDTO.setBroughtForwardBalanceCredit(v);
                } else {
                    turnoverSheetTableDTO.setBroughtForwardBalanceDebit(v.negate());
                }
                tableDTOHashMap.put(k, turnoverSheetTableDTO);
            }
                }
        );
        Map<Integer,BigDecimal> allRenderedServicesPriceInMonthForSubscriberByServiceId = renderedServiceDAO.getAllRenderedServicesPriceInMonthForSubscriberByServiceId(serviceId, date);
        allRenderedServicesPriceInMonthForSubscriberByServiceId.forEach((k, v) -> {
            if (tableDTOHashMap.containsKey(k)) {
                tableDTOHashMap.get(k).setTurnoverBalanceDebit(v);
            } else {
                TurnoverSheetTableDTO turnoverSheetTableDTO = new TurnoverSheetTableDTO();
                turnoverSheetTableDTO.setSubscriberAccount(k);
                turnoverSheetTableDTO.setServiceId(serviceId);
                turnoverSheetTableDTO.setTurnoverBalanceDebit(v);
                tableDTOHashMap.put(k, turnoverSheetTableDTO);
            }
        });
        Map<Integer, BigDecimal> allPaymentsPriceInMonthForSubscriberByServiceId = paymentDAO.getAllPaymentsPriceInMonthForSubscriberByServiceId(serviceId, date);
        allPaymentsPriceInMonthForSubscriberByServiceId.forEach((k, v) -> {
            if (tableDTOHashMap.containsKey(k)) {
                tableDTOHashMap.get(k).setTurnoverBalanceCredit(v);
            } else {
                TurnoverSheetTableDTO turnoverSheetTableDTO = new TurnoverSheetTableDTO();
                turnoverSheetTableDTO.setSubscriberAccount(k);
                turnoverSheetTableDTO.setServiceId(serviceId);
                turnoverSheetTableDTO.setTurnoverBalanceCredit(v);
                tableDTOHashMap.put(k, turnoverSheetTableDTO);
            }
        });
        Map<Integer, BigDecimal> endPeriodDebit = renderedServiceDAO.getAllRenderedServicesPriceForSubscriberByServiceIdBeforeDate(serviceId, date.plusMonths(1));
        endPeriodDebit.forEach((k, v) -> {
            if (tableDTOHashMap.containsKey(k)) {
                tableDTOHashMap.get(k).setCarriedForwardBalanceDebit(v);
            } else {
                TurnoverSheetTableDTO turnoverSheetTableDTO = new TurnoverSheetTableDTO();
                turnoverSheetTableDTO.setSubscriberAccount(k);
                turnoverSheetTableDTO.setServiceId(serviceId);
                turnoverSheetTableDTO.setCarriedForwardBalanceDebit(v);
                tableDTOHashMap.put(k, turnoverSheetTableDTO);
            }
                }
        );
        Map<Integer, BigDecimal> endPeriodCredit = paymentDAO.getAllPaymentsPriceForSubscriberToDate(serviceId, date.plusMonths(1));
        endPeriodCredit.forEach((k, v) -> {
                    TurnoverSheetTableDTO dto = tableDTOHashMap.get(k);
                    if (dto != null) {
                        BigDecimal carriedForwardBalanceDebit = dto.getCarriedForwardBalanceDebit();
                        if(carriedForwardBalanceDebit.compareTo(v) <= 0) {
                            dto.setCarriedForwardBalanceDebit(BigDecimal.ZERO);
                            dto.setCarriedForwardBalanceCredit(v.add(carriedForwardBalanceDebit.negate()));
                        } else {
                            dto.setCarriedForwardBalanceDebit(carriedForwardBalanceDebit.add(v.negate()));
                        }
                    } else {
                        TurnoverSheetTableDTO turnoverSheetTableDTO = new TurnoverSheetTableDTO();
                        turnoverSheetTableDTO.setSubscriberAccount(k);
                        turnoverSheetTableDTO.setServiceId(serviceId);
                        turnoverSheetTableDTO.setCarriedForwardBalanceCredit(v);
                        tableDTOHashMap.put(k, turnoverSheetTableDTO);
                    }

                }
        );

        turnoverSheetTableDTOs = tableDTOHashMap.values().stream().sorted((dto1, dto2) -> dto1.getSubscriberAccount() - dto2.getSubscriberAccount())
                .filter(e -> isAddAllNullUsers
                        || e.getBroughtForwardBalanceDebit().add(e.getBroughtForwardBalanceCredit().negate()).compareTo(BigDecimal.ZERO) != 0
                        || e.getTurnoverBalanceDebit().compareTo(BigDecimal.ZERO) != 0
                        || e.getTurnoverBalanceCredit().compareTo(BigDecimal.ZERO) != 0
                        || e.getCarriedForwardBalanceDebit().add(e.getCarriedForwardBalanceCredit().negate()).compareTo((BigDecimal.ZERO)) != 0)
                .collect(Collectors.toList());

        for (TurnoverSheetTableDTO ts : turnoverSheetTableDTOs) {
            ts.setSubscriber(subscriberDAO.get(ts.getSubscriberAccount()));
            if(ts.getSubscriber() != null) {
                ts.setSubscriberStreet(streetDAO.get(ts.getSubscriber().getStreetId()));
            }
            BigDecimal taxesBase = ts.getBroughtForwardBalanceDebit().add(ts.getTurnoverBalanceDebit()).add(ts.getBroughtForwardBalanceCredit().negate()).add(ts.getTurnoverBalanceCredit().negate());
            if (taxesBase.compareTo(BigDecimal.ZERO) < 0) {
                ts.setTaxesBase(ts.getBroughtForwardBalanceDebit().add(ts.getTurnoverBalanceDebit()));
            } else {
                ts.setTaxesBase(ts.getBroughtForwardBalanceCredit().add(ts.getTurnoverBalanceCredit()));
            }
        }

//        for (TurnoverSheetTableDTO turnoverSheetTableDTO : turnoverSheetTableDTOs) {
//            BigDecimal carriedForwardBalance =
//                    turnoverSheetTableDTO.getBroughtForwardBalanceDebit().add(turnoverSheetTableDTO.getBroughtForwardBalanceCredit().negate())
//                    .add(turnoverSheetTableDTO.getTurnoverBalanceDebit()).add(turnoverSheetTableDTO.getTurnoverBalanceCredit().negate());
//            if(carriedForwardBalance != turnoverSheetTableDTO.getCarriedForwardBalanceDebit().add(turnoverSheetTableDTO.getCarriedForwardBalanceCredit().negate())) {
//                System.out.println(turnoverSheetTableDTO.getSubscriberAccount());
//            }
//        }
//        System.out.println(turnoverSheetTableDTOs.stream().map(i -> i.getBroughtForwardBalanceDebit()).reduce(BigDecimal.ZERO, BigDecimal::add));
//        System.out.println(turnoverSheetTableDTOs.stream().map(i -> i.getTurnoverBalanceCredit()).reduce(BigDecimal.ZERO, BigDecimal::add));
//        System.out.println(turnoverSheetTableDTOs.stream().map(i -> i.getTurnoverBalanceDebit()).reduce(BigDecimal.ZERO, BigDecimal::add));
//        System.out.println(turnoverSheetTableDTOs.stream().map(i -> i.getCarriedForwardBalanceCredit()).reduce(BigDecimal.ZERO, BigDecimal::add));
//        System.out.println(turnoverSheetTableDTOs.stream().map(i -> i.getCarriedForwardBalanceDebit()).reduce(BigDecimal.ZERO, BigDecimal::add));
        return turnoverSheetTableDTOs;
    }



    public String generateExcelReport(Boolean isAdditional,
                                      Integer serviceId, LocalDate date) {
        TurnoverReportExcel turnoverReportExcel = new TurnoverReportExcel();
        if (serviceId != null) {
            turnoverReportExcel.setService(getService(serviceId));
        }
        if (isAdditional) {
            turnoverReportExcel.setTurnoverSheetTableDTOs(getTurnoverSheetDataByAdditionalServices(date));
        } else {
            turnoverReportExcel.setTurnoverSheetTableDTOs(getTurnoverSheetData(date, serviceId));
        }
        turnoverReportExcel.setDate(date);
        turnoverReportExcel.setAdditionalServices(getAllServices().stream().filter(e -> e.isAdditionalService()).collect(Collectors.toList()));
        turnoverReportExcel.setIsAdditional(isAdditional);
        return turnoverReportExcel.generate();
    }

}
