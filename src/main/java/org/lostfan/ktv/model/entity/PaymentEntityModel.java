package org.lostfan.ktv.model.entity;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.lostfan.ktv.dao.DAOFactory;
import org.lostfan.ktv.dao.PaymentDAO;
import org.lostfan.ktv.dao.RenderedServiceDAO;
import org.lostfan.ktv.dao.SubscriberDAO;
import org.lostfan.ktv.domain.Payment;
import org.lostfan.ktv.domain.RenderedService;
import org.lostfan.ktv.model.*;
import org.lostfan.ktv.model.searcher.EntitySearcherModel;
import org.lostfan.ktv.utils.PaymentsLoader;
import org.lostfan.ktv.validation.*;
import org.lostfan.ktv.view.FormView;
import org.lostfan.ktv.view.entity.LoadPaymentsView;

public class PaymentEntityModel extends BaseDocumentModel<Payment> {

    private LocalDate date;
    private List<EntityField> fields;
    private FullEntityField loadFullEntityField;
    private Validator<Payment> validator = new PaymentValidator();
    private SubscriberDAO subscriberDAO = DAOFactory.getDefaultDAOFactory().getSubscriberDAO();
    private RenderedServiceDAO renderedServiceDAO = DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO();
    private List<Payment> payments;
    private Integer progress;
    private PeriodValidator periodValidator = new PeriodValidator();
    private NotNullValidator notNullValidator = new NotNullValidator();
    private Map<Integer, List<Payment>> paymentsMap;
    private Map<Integer, List<RenderedService>> renderedServicesMap;

    public PaymentEntityModel() {

        date = LocalDate.now().withDayOfMonth(1);
        payments = new ArrayList<>();

        this.fields = new ArrayList<>();
        this.fields.add(new EntityField("payment.id", EntityFieldTypes.Integer, Payment::getId, Payment::setId, false));
        this.fields.add(new EntityField("payment.payDate", EntityFieldTypes.Date, Payment::getDate, Payment::setDate));
        this.fields.add(new EntityField("subscriber", EntityFieldTypes.Subscriber, Payment::getSubscriberAccount, Payment::setSubscriberAccount));
        this.fields.add(new EntityField("service", EntityFieldTypes.Service, Payment::getServicePaymentId, Payment::setServicePaymentId));
        this.fields.add(new EntityField("renderedService", EntityFieldTypes.RenderedService, Payment::getRenderedServicePaymentId, Payment::setRenderedServicePaymentId));
        this.fields.add(new EntityField("payment.price", EntityFieldTypes.BigDecimal, Payment::getPrice, Payment::setPrice));
        this.fields.add(new EntityField("payment.bankFileName", EntityFieldTypes.String, Payment::getBankFileName, Payment::setBankFileName));
        this.fields.add(new EntityField("paymentType", EntityFieldTypes.PaymentType, Payment::getPaymentTypeId, Payment::setPaymentTypeId));

        loadFullEntityField = new FullEntityField("payment", EntityFieldTypes.Payment, null, null, Payment::new);
        loadFullEntityField.setEntityFields(getFields().stream().filter(e -> !e.getTitleKey().equals("payment.id")).collect(Collectors.toList()));
    }

    @Override
    public List<EntityModel> getEntityModels() {
        List<EntityModel> entityModels = new ArrayList<>();
        entityModels.add(MainModel.getServiceEntityModel());
        entityModels.add(MainModel.getSubscriberEntityModel());
        return entityModels;
    }

    @Override
    public String getEntityName() {
        return "payment";
    }

    @Override
    public List<EntityField> getFields() {
        return this.fields;
    }

    @Override
    public String getEntityNameKey() {
        return "payments";
    }

    @Override
    public Class getEntityClass() {
        return Payment.class;
    }

    @Override
    protected PaymentDAO getDao() {
        return DAOFactory.getDefaultDAOFactory().getPaymentDAO();
    }

    @Override
    public Payment createNewEntity() {
        return new Payment();
    }

    public FullEntityField getLoadFullEntityField() {
        return loadFullEntityField;
    }

    public List<Payment> getPayments() {
        return this.payments;
    }

    public Payment createPayment(Integer subscriberId, LocalDate date, BigDecimal price) {
        Payment payment = new Payment();
        if (subscriberDAO.get(subscriberId) == null) {
            return null;
        }
        HashMap<Integer, BigDecimal> hashMap = subscriberDAO.getServicesBalance(subscriberId);
        payment.setSubscriberAccount(subscriberId);
        payment.setDate(date);
        payment.setPaymentTypeId(null);
        payment.setPrice(price);
        payment.setServicePaymentId(FixedServices.SUBSCRIPTION_FEE.getId());
        return payment;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    /**
     * Creates a new Payment list based on a payment file and the current loaded payments.
     *
     * @param file        A file for loading another payments from.
     * @return a new (merged) list of payments
     */
    public void createPayments(File file) {
        List<Payment> loadedPayments = new PaymentsLoader().load(file);
        Integer count = 0;
        for (Payment loadedPayment : loadedPayments) {
            if (BigDecimal.ZERO.compareTo(loadedPayment.getPrice()) == 0 || subscriberDAO.get(loadedPayment.getSubscriberAccount()) == null) {
                continue;
            }
            progress = 100 * count++ / loadedPayments.size();
            notifyObservers(null);
            this.payments.addAll(addPayments(loadedPayment));

        }
        progress = 100;
        notifyObservers(null);

    }

    public void createPayments(List<Payment> loadedPayments) {
        LocalDate date = LocalDate.MAX;
        if (loadedPayments.size() > 0) {
            date = loadedPayments.get(0).getDate();
        }
        Integer count = 0;
        paymentsMap = getDao().getServiceAndSubscriberPaymentMap();
        renderedServicesMap = renderedServiceDAO.getServiceAndSubscriberRenderedServiceMap(date);
        long begin = System.currentTimeMillis();
        for (Payment loadedPayment : loadedPayments) {
            if (BigDecimal.ZERO.compareTo(loadedPayment.getPrice()) == 0 || subscriberDAO.get(loadedPayment.getSubscriberAccount()) == null) {
                continue;
            }
            progress = 100 * count++ / loadedPayments.size();
            notifyObservers(null);
            this.payments.addAll(addPayments(loadedPayment));

        }
        progress = 100;
        notifyObservers(null);
        long end = System.currentTimeMillis();
        System.out.println(end - begin);

    }

    /**
     * Creates a new Payment list based on a raw in payment file and the current loaded payments.
     *
     * @param newLoadedPayment      Payment without service from the file for loading another payments from.
     * @return list of payments with services
     */
    public List<Payment> addPayments(Payment newLoadedPayment) {

        List<Payment> payments = new ArrayList<>();
        BigDecimal price = newLoadedPayment.getPrice();
        Map<Integer, BigDecimal> paymentMapFromTable = new HashMap<>();
        for (Payment payment : getPayments()) {
            if(paymentMapFromTable.containsKey(payment.getRenderedServicePaymentId())) {
                paymentMapFromTable.put(payment.getRenderedServicePaymentId(),
                        payment.getPrice().add(paymentMapFromTable.get(payment.getRenderedServicePaymentId())));
            } else {
                paymentMapFromTable.put(payment.getRenderedServicePaymentId(), payment.getPrice());
            }
        }

        Map<Integer, BigDecimal> hashMap = getServicesBalance(newLoadedPayment.getSubscriberAccount());

        for (Map.Entry<Integer, BigDecimal> servicePriceEntry : hashMap.entrySet()) {
            if (BigDecimal.ZERO.compareTo(price) == 0) {
                break;
            }
            if (BigDecimal.ZERO.compareTo(servicePriceEntry.getValue()) == 0) {
                continue;
            }
            if (servicePriceEntry.getKey() == FixedServices.SUBSCRIPTION_FEE.getId()) {
                continue;
            }
            if(servicePriceEntry.getValue().compareTo(
                    getPayments().stream().filter(e -> e.getServicePaymentId().equals(servicePriceEntry.getKey())
                        && e.getSubscriberAccount().equals(newLoadedPayment.getSubscriberAccount()))
                        .map(i -> i.getPrice()).reduce(BigDecimal.ZERO, BigDecimal::add)) == 0) {
                continue;
            }

            Map<Integer, Payment> paymentsForNotClosedRenderedServices =
                    getDao().getForNotClosedRenderedServices(newLoadedPayment.getSubscriberAccount(), servicePriceEntry.getKey());
            for (Integer id : paymentsForNotClosedRenderedServices.keySet()) {
                BigDecimal paymentPrice;
                if (paymentMapFromTable.get(id) == null) {
                    paymentPrice = paymentsForNotClosedRenderedServices.get(id).getPrice();
                } else {
                    if (paymentMapFromTable.get(id).equals(paymentsForNotClosedRenderedServices.get(id).getPrice())) {
                        continue;
                    }
                    paymentPrice = paymentsForNotClosedRenderedServices.get(id).getPrice().add(paymentMapFromTable.get(id).negate());
                }
                Payment payment = new Payment();
                payment.setSubscriberAccount(newLoadedPayment.getSubscriberAccount());
                payment.setDate(newLoadedPayment.getDate());
                payment.setPaymentTypeId(newLoadedPayment.getPaymentTypeId());
                payment.setBankFileName(newLoadedPayment.getBankFileName());
                if (price.compareTo(paymentPrice) > 0) {
                    payment.setPrice(paymentPrice);
                    price = price.add(paymentPrice.negate());
                } else {
                    payment.setPrice(price);
                    payment.setServicePaymentId(servicePriceEntry.getKey());
                    payment.setRenderedServicePaymentId(id);
                    payments.add(payment);
                    return payments;
                }
                payment.setServicePaymentId(servicePriceEntry.getKey());
                payment.setRenderedServicePaymentId(id);
                payments.add(payment);
            }
        }
        Payment payment = new Payment();
        payment.setSubscriberAccount(newLoadedPayment.getSubscriberAccount());
        payment.setDate(newLoadedPayment.getDate());
        payment.setBankFileName(newLoadedPayment.getBankFileName());
        payment.setPaymentTypeId(newLoadedPayment.getPaymentTypeId());
        payment.setPrice(price);
        payment.setServicePaymentId(FixedServices.SUBSCRIPTION_FEE.getId());
        payments.add(payment);
        return payments;
    }

    private Map<Integer, BigDecimal> getServicesBalance(Integer subscriberAccount) {
        Map<Integer, BigDecimal> map = new HashMap<>();
        List<RenderedService> renderedServices = renderedServicesMap.get(subscriberAccount);
        List<Payment> payments = paymentsMap.get(subscriberAccount);
        for (RenderedService renderedService : renderedServices) {
            if(payments != null) {
                for (Payment payment : payments) {
                    if (payment.getServicePaymentId().equals(renderedService.getServiceId())) {
                        map.put(renderedService.getServiceId(), renderedService.getPrice().add(payment.getPrice().negate()));
                    }
                }
            }
            if(map.get(renderedService.getServiceId()) == null) {
                map.put(renderedService.getServiceId(), renderedService.getPrice());
            }
        }
        return map;
    }

    public List<Payment> loadPayments(Payment loadPayment, Payment oldPayment) {
        List<Payment> payments = new ArrayList<>();
        BigDecimal price = loadPayment.getPrice();
        if(oldPayment != null
                && oldPayment.getSubscriberAccount().equals(loadPayment.getSubscriberAccount()))
        {
            for (Payment payment1 : getList(oldPayment.getSubscriberAccount(),oldPayment.getDate(), oldPayment.getBankFileName())) {
                BigDecimal paymentPrice = payment1.getPrice();
                Payment payment = new Payment();
                payment.setId(payment1.getId());
                payment.setSubscriberAccount(loadPayment.getSubscriberAccount());
                payment.setDate(loadPayment.getDate());
                payment.setPaymentTypeId(loadPayment.getPaymentTypeId());
                payment.setBankFileName(loadPayment.getBankFileName());
                if (price.compareTo(paymentPrice) > 0) {
                    payment.setPrice(paymentPrice);
                    price = price.add(paymentPrice.negate());
                } else {
                    payment.setPrice(price);
                    payment.setServicePaymentId(payment1.getServicePaymentId());
                    payment.setRenderedServicePaymentId(payment1.getRenderedServicePaymentId());
                    payments.add(payment);
                    return payments;
                }
                payment.setServicePaymentId(payment1.getServicePaymentId());
                payment.setRenderedServicePaymentId(payment1.getRenderedServicePaymentId());
                payments.add(payment);
            }
        }

        HashMap<Integer, BigDecimal> hashMap = subscriberDAO.getServicesBalance(loadPayment.getSubscriberAccount());
        for (Integer serviceId : hashMap.keySet()) {
            if (BigDecimal.ZERO.compareTo(price) == 0) {
                break;
            }
            if (BigDecimal.ZERO.compareTo(hashMap.get(serviceId)) == 0) {
                continue;
            }
            if (serviceId == FixedServices.SUBSCRIPTION_FEE.getId()) {
                continue;
            }

            Map<Integer, Payment> paymentsForNotClosedRenderedServices =
                    getDao().getForNotClosedRenderedServices(loadPayment.getSubscriberAccount(), serviceId);
            for (Integer id : paymentsForNotClosedRenderedServices.keySet()) {
                BigDecimal paymentPrice  = paymentsForNotClosedRenderedServices.get(id).getPrice();
                Payment payment = new Payment();
                payment.setSubscriberAccount(loadPayment.getSubscriberAccount());
                payment.setDate(loadPayment.getDate());
                payment.setPaymentTypeId(loadPayment.getPaymentTypeId());
                payment.setBankFileName(loadPayment.getBankFileName());
                if (price.compareTo(paymentPrice) > 0) {
                    payment.setPrice(paymentPrice);
                    price = price.add(paymentPrice.negate());
                } else {
                    payment.setPrice(price);
                    payment.setServicePaymentId(serviceId);
                    payment.setRenderedServicePaymentId(id);
                    payments.add(payment);
                    return payments;
                }
                payment.setServicePaymentId(serviceId);
                payment.setRenderedServicePaymentId(id);
                payments.add(payment);
            }
        }
        Payment payment = new Payment();
        payment.setSubscriberAccount(loadPayment.getSubscriberAccount());
        payment.setDate(loadPayment.getDate());
        payment.setBankFileName(loadPayment.getBankFileName());
        payment.setPaymentTypeId(loadPayment.getPaymentTypeId());
        payment.setPrice(price);
        payment.setServicePaymentId(FixedServices.SUBSCRIPTION_FEE.getId());
        payments.add(payment);
        return payments;
    }

    @Override
    public Validator<Payment> getValidator() {
        return this.validator;
    }

    public void setDate(LocalDate date) {
        this.date = date;
        updateEntitiesList();
    }

    public List<Payment> getAll() {
        return DAOFactory.getDefaultDAOFactory().getPaymentDAO().getByMonth(this.date);
    }

    public LocalDate getDate() {
        return this.date;
    }

    public List<Payment> getPaymentsByBankFileName(String bankFileName) {
        return getDao().getByBankFileName(bankFileName);
    }

    @Override
    public EntitySearcherModel<Payment> createSearchModel() {
        return null;
    }

    public List<Payment> getList(Integer subscriberId, LocalDate date, String bankFileName) {
        return getDao().getList(subscriberId, date, bankFileName);
    }

    public void update(List<Payment> payments, List<Payment> oldPayments) {
        List<Integer> oldPaymentIds = oldPayments.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<Integer> paymentIds = payments.stream().map(e -> e.getId()).collect(Collectors.toList());
        for (Integer oldPaymentId : oldPaymentIds) {
            if(paymentIds.contains(oldPaymentId)) {
                continue;
            }
            deleteEntityById(oldPaymentId);
        }
        for (Payment payment : payments) {
            save(payment);
        }
    }

    public List<Payment> getPaymentsByDate(LocalDate date) {
        return getDao().getByDate(date);
    }

    public ValidationResult deletePaymentsByDate(LocalDate date) {
        ValidationResult validationResult = ValidationResult.createEmpty();
        notNullValidator.validate(date, "entity.date", validationResult);
        if(validationResult.hasErrors()) {
            return validationResult;
        }
        periodValidator.validate(date, validationResult);
        if(validationResult.hasErrors()) {
            return validationResult;
        }
        getDao().deleteByDate(date);
        updateEntitiesList();
        return validationResult;
    }

    public void savePayments(List<Payment> payments, FormView view) {
        ValidationResult result = ValidationResult.createEmpty();
        for (Payment payment : payments) {
            result = getValidator().validate(payment, result);
            result = getPeriodValidator().validate(payment, result);
            if (result.hasErrors()) {
                view.showErrors(result.getErrors());
                return;
            }
        }
        int size = payments.size();
        int count = 0;
        progress = 0;
        for (Payment payment : payments) {
            progress = 100 * count++ / size;
            notifyObservers(null);
            save(payment);
        }
        progress = 100;
        notifyObservers(null);
        view.hide();
    }
}
