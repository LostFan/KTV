package org.lostfan.ktv.model;

import org.lostfan.ktv.dao.*;
import org.lostfan.ktv.domain.Payment;
import org.lostfan.ktv.domain.RenderedService;
import org.lostfan.ktv.model.dto.SubscriberDebit;
import org.lostfan.ktv.model.entity.BaseModel;
import org.lostfan.ktv.utils.BaseObservable;
import org.lostfan.ktv.utils.excel.SubscriberDebitExcel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WriteOffReportModel extends BaseObservable implements BaseModel {

    private SubscriberDAO subscriberDAO = DAOFactory.getDefaultDAOFactory().getSubscriberDAO();
    private RenderedServiceDAO renderedServiceDAO = DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO();
    private PaymentDAO paymentDAO = DAOFactory.getDefaultDAOFactory().getPaymentDAO();
    private StreetDAO streetDAO = DAOFactory.getDefaultDAOFactory().getStreetDAO();

    public WriteOffReportModel() {
    }

    @Override
    public String getEntityNameKey() {
        return "writeOfReport";
    }

    @Override
    public List<EntityField> getFields() {
        return new ArrayList<>();
    }

    public List<SubscriberDebit> getData(LocalDate date) {

        List<SubscriberDebit> subscriberDebitsResult = new ArrayList<>();
        List<Integer> inactiveSubscribersForPeriod = subscriberDAO.getInactiveSubscribersForPeriod(date, LocalDate.now());
        Map<Integer, BigDecimal> endPeriodDebit = renderedServiceDAO.getAllRenderedServicesPriceBeforeDate(LocalDate.now());
        Map<Integer, BigDecimal> endPeriodCredit = paymentDAO.getAllPaymentsPriceBeforeDate(LocalDate.now());
        inactiveSubscribersForPeriod.forEach(e -> {
            BigDecimal debit = endPeriodDebit.get(e) != null ? endPeriodDebit.get(e) : BigDecimal.ZERO;
            BigDecimal credit = endPeriodCredit.get(e) != null ? endPeriodCredit.get(e) : BigDecimal.ZERO;
            BigDecimal result = debit.add(credit.negate());
            if (result.compareTo(BigDecimal.ZERO) != 0) {
                SubscriberDebit subscriberDebit = new SubscriberDebit();
                subscriberDebit.setSubscriberAccount(e);
                subscriberDebit.setDebit(result.setScale(2, RoundingMode.HALF_UP));
                subscriberDebit.setSubscriber(subscriberDAO.get(subscriberDebit.getSubscriberAccount()));
                if(subscriberDebit.getSubscriber() != null) {
                    subscriberDebit.setSubscriberStreet(streetDAO.get(subscriberDebit.getSubscriber().getStreetId()));
                }
                subscriberDebitsResult.add(subscriberDebit);
            }
        });
        System.out.println(subscriberDebitsResult.size());
        return subscriberDebitsResult;
    }

    public String generateExcelReport(LocalDate date) {
        SubscriberDebitExcel subscriberDebitExcel = new SubscriberDebitExcel("writeOfReport");

        subscriberDebitExcel.setSubscriberDebitList(getData(date));

        return subscriberDebitExcel.generate();
    }

}
