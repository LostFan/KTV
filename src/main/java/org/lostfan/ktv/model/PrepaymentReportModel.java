package org.lostfan.ktv.model;

import org.lostfan.ktv.dao.*;
import org.lostfan.ktv.domain.RenderedService;
import org.lostfan.ktv.model.dto.SubscriberDebit;
import org.lostfan.ktv.model.entity.BaseModel;
import org.lostfan.ktv.utils.BaseObservable;
import org.lostfan.ktv.utils.excel.SubscriberDebitExcel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PrepaymentReportModel extends BaseObservable implements BaseModel {

    private SubscriberDAO subscriberDAO = DAOFactory.getDefaultDAOFactory().getSubscriberDAO();
    private RenderedServiceDAO renderedServiceDAO = DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO();
    private PaymentDAO paymentDAO = DAOFactory.getDefaultDAOFactory().getPaymentDAO();
    private StreetDAO streetDAO = DAOFactory.getDefaultDAOFactory().getStreetDAO();

    public PrepaymentReportModel() {
    }

    @Override
    public String getEntityNameKey() {
        return "prepaymentReport";
    }

    @Override
    public List<EntityField> getFields() {
        return new ArrayList<>();
    }

    public List<SubscriberDebit> getData(LocalDate date) {

        List<SubscriberDebit> subscriberDebitsResult = new ArrayList<>();
        LocalDate lastMonthDate= date.withDayOfMonth(1).minusDays(1);
        Map<Integer, BigDecimal> endPeriodDebit = renderedServiceDAO.getAllRenderedServicesPriceBeforeDate(lastMonthDate);
        Map<Integer, BigDecimal> endPeriodCredit = paymentDAO.getAllPaymentsPriceBeforeDate(lastMonthDate);
        Map<Integer, BigDecimal> endPeriodBalance = merge(endPeriodDebit, endPeriodCredit);


        Map<Integer, BigDecimal> monthDebit = renderedServiceDAO.getAllRenderedServicesPriceInMonthForSubscriber(date.withDayOfMonth(1));
        Map<Integer, BigDecimal> monthCredit =  paymentDAO.getAllPaymentsPriceInMonthForSubscriber(date.withDayOfMonth(1));

        Map<Integer, BigDecimal> monthBalance = merge(monthDebit, monthCredit);

        Map<Integer, BigDecimal> result = new HashMap<>(monthBalance);



        endPeriodBalance.forEach((key, value) -> {
            if (result.containsKey(key)) {
                result.merge(key, value, (v1, v2) -> {
                    if (BigDecimal.ZERO.compareTo(v2) <= 0) {
                        return v1;
                    } else {
                        return v1.add(v2);
                    }

                });
            }
        });

        for (Map.Entry<Integer, BigDecimal> integerBigDecimalEntry : result.entrySet()) {
            if ((BigDecimal.ZERO.compareTo(integerBigDecimalEntry.getValue()) < 0)) {
                SubscriberDebit subscriberDebit = new SubscriberDebit();
                subscriberDebit.setSubscriberAccount(integerBigDecimalEntry.getKey());
                subscriberDebit.setDebit(integerBigDecimalEntry.getValue());
                subscriberDebit.setSubscriber(subscriberDAO.get(subscriberDebit.getSubscriberAccount()));
                if(subscriberDebit.getSubscriber() != null) {
                    subscriberDebit.setSubscriberStreet(streetDAO.get(subscriberDebit.getSubscriber().getStreetId()));
                }
                subscriberDebitsResult.add(subscriberDebit);
            }
        }
        System.out.println(subscriberDebitsResult.size());
        return subscriberDebitsResult;
    }

    public String generateExcelReport(LocalDate date) {
        SubscriberDebitExcel subscriberDebitExcel = new SubscriberDebitExcel("prepaymentReport");

        subscriberDebitExcel.setSubscriberDebitList(getData(date));

        return subscriberDebitExcel.generate();
    }

    private Map<Integer, BigDecimal> merge(Map<Integer, BigDecimal> debit, Map<Integer, BigDecimal> credit) {
        Map<Integer, BigDecimal> balance = new HashMap<>(credit);

        debit.forEach((key, value) ->  {
                    if (balance.containsKey(key)) {
                        balance.put(key, balance.get(key).add(value.negate()));
                    } else {
                        balance.put(key, value.negate());
                    }
                }
        );
        return balance;
    }

}
