package org.lostfan.ktv.model.entity;

import org.lostfan.ktv.dao.*;
import org.lostfan.ktv.domain.*;
import org.lostfan.ktv.model.*;
import org.lostfan.ktv.model.dto.*;
import org.lostfan.ktv.model.searcher.EntitySearcherModel;
import org.lostfan.ktv.model.searcher.RenderedServiceSearcherModel;
import org.lostfan.ktv.validation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RenderedServiceEntityModel extends BaseDocumentModel<RenderedService> {

    private LocalDate date;
    private List<EntityField> fields;

    private EntityField tariffField;
    private EntityField serviceEntityField;
    private EntityField disconnectionReasonField;

    private List<FullEntityField> fullFields;

    private Validator<RenderedService> validator = new RenderedServiceValidator();
    private Validator<SubscriberTariff> validatorSubscriberTariff = new SubscriberTariffValidator();
    private Validator<RenderedService> connectionAdditionValidator = new ConnectionAdditionValidator();
    private Validator<RenderedService> reconnectionAdditionValidator = new ReconnectionAdditionValidator();
    private ConnectionEditValidator connectionEditValidator = new ConnectionEditValidator();
    private DisconnectionAdditionValidator disconnectionAdditionValidator = new DisconnectionAdditionValidator();
    private DisconnectionEditValidator disconnectionEditValidator = new DisconnectionEditValidator();
    private ChangeTariffAdditionValidator changeTariffAdditionValidator = new ChangeTariffAdditionValidator();
    private ChangeTariffEditValidator changeTariffEditValidator = new ChangeTariffEditValidator();
    private ConnectionDeleteValidator connectionDeleteValidator = new ConnectionDeleteValidator();
    private DisconnectionDeleteValidator disconnectionDeleteValidator = new DisconnectionDeleteValidator();
    private ChangeTariffDeleteValidator changeTariffDeleteValidator = new ChangeTariffDeleteValidator();
    private PeriodValidator periodValidator = new PeriodValidator();

    private MaterialConsumptionDAO materialConsumptionDAO = DAOFactory.getDefaultDAOFactory().getMaterialConsumptionDAO();
    private MaterialDAO materialDAO = DAOFactory.getDefaultDAOFactory().getMaterialDAO();
    private SubscriberDAO subscriberDAO = DAOFactory.getDefaultDAOFactory().getSubscriberDAO();
    private ServiceDAO serviceDAO = DAOFactory.getDefaultDAOFactory().getServiceDAO();

    public RenderedServiceEntityModel() {
        date = LocalDate.now().withDayOfMonth(1);

        this.fields = new ArrayList<>();
        this.fields.add(new EntityField("renderedService.id", EntityFieldTypes.Integer, RenderedService::getId, RenderedService::setId, false));
        this.fields.add(new EntityField("renderedService", EntityFieldTypes.Service, RenderedService::getServiceId, RenderedService::setServiceId, false));
        this.fields.add(new EntityField("renderedService.date", EntityFieldTypes.Date, RenderedService::getDate, RenderedService::setDate));
        this.fields.add(new EntityField("subscriber", EntityFieldTypes.Subscriber, RenderedService::getSubscriberAccount, RenderedService::setSubscriberAccount));
        this.fields.add(new EntityField("renderedService.price", EntityFieldTypes.BigDecimal, RenderedService::getPrice, RenderedService::setPrice));

        this.serviceEntityField = new EntityField("service", EntityFieldTypes.Service, RenderedService::getServiceId, RenderedService::setServiceId, false);
        this.tariffField = new EntityField("tariff", EntityFieldTypes.Tariff, TariffField::getTariffId, TariffField::setTariffId);
        this.disconnectionReasonField = new EntityField("disconnectionReason", EntityFieldTypes.DisconnectionReason, DisconnectionRenderedService::getDisconnectionReasonId, DisconnectionRenderedService::setDisconnectionReasonId);

        this.fullFields = new ArrayList<>();

        FullEntityField materialConsumptionField = new FullEntityField("materialConsumption", EntityFieldTypes.MaterialConsumption, MaterialsDTO::getMaterialConsumption, MaterialsDTO::setMaterialConsumption, MaterialConsumption::new);
        List<EntityField> entityFields = MainModel.getMaterialConsumptionEntityModel().getFields().stream().filter(e -> !e.getTitleKey().equals("renderedService")).filter(e -> !e.getTitleKey().equals("materialConsumption.id")).collect(Collectors.toList());
        entityFields.add(new EntityField("materialConsumption.price", EntityFieldTypes.BigDecimal, new Function<MaterialConsumption, BigDecimal>() {
            @Override
            public BigDecimal apply(MaterialConsumption materialConsumption) {
                if (materialConsumption.getMaterialId() == null) {
                    return null;
                }
                return materialDAO.get(materialConsumption.getMaterialId()).getPrice();
            }
        }, (e1, e2) -> {
        }));
        entityFields.add(new EntityField("materialConsumption.allPrice", EntityFieldTypes.BigDecimal, new Function<MaterialConsumption, BigDecimal>() {
            @Override
            public BigDecimal apply(MaterialConsumption materialConsumption) {
                if (materialConsumption.getMaterialId() == null
                        || materialConsumption.getAmount() == null) {
                    return null;
                }
                return ((materialDAO.get(materialConsumption.getMaterialId()).getPrice().multiply(new BigDecimal(materialConsumption.getAmount())))).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        }, (e1, e2) -> {
        }));
        materialConsumptionField.setEntityFields(entityFields);

        this.fullFields.add(materialConsumptionField);
    }

    @Override
    public List<EntityModel> getEntityModels() {
        List<EntityModel> entityModels = new ArrayList<>();
        entityModels.add(MainModel.getServiceEntityModel());
        entityModels.add(MainModel.getSubscriberEntityModel());
        return entityModels;
    }

    public ConnectionRenderedService getConnectionRenderedService(RenderedService renderedService) {
        SubscriberTariff subscriberTariff = subscriberDAO.getSubscriberTariffAtDate(renderedService.getSubscriberAccount(), renderedService.getDate());
        return ConnectionRenderedService.build(renderedService, subscriberTariff);
    }

    public ReconnectionRenderedService getReconnectionRenderedService(RenderedService renderedService) {
        SubscriberTariff subscriberTariff = subscriberDAO.getSubscriberTariffAtDate(renderedService.getSubscriberAccount(), renderedService.getDate());
        return ReconnectionRenderedService.build(renderedService, subscriberTariff);
    }

    public DisconnectionRenderedService getDisconnectionRenderedService(RenderedService renderedService) {
        SubscriberSession subscriberSession = subscriberDAO.getSubscriberSessionByDisconnectionDate(renderedService.getSubscriberAccount(), renderedService.getDate());
        return DisconnectionRenderedService.build(renderedService , subscriberSession);
    }

    public ChangeOfTariffRenderedService getChangeOfTariffRenderedService(RenderedService renderedService) {
        SubscriberTariff subscriberTariff = subscriberDAO.getSubscriberTariffByConnectionDate(renderedService.getSubscriberAccount(), renderedService.getDate());
        return ChangeOfTariffRenderedService.build(renderedService, subscriberTariff);
    }

    public MaterialsRenderedService getMaterialsRenderedService(RenderedService renderedService) {
        List<MaterialConsumption> materialConsumptions = materialConsumptionDAO.getByRenderedServiceId(renderedService.getId());
        return MaterialsRenderedService.build(renderedService, materialConsumptions);
    }

    @Override
    public String getEntityName() {
        return "renderedService";
    }

    @Override
    public List<EntityField> getFields() {
        return this.fields;
    }

    // TODO: a bad idea to have these methods
    public EntityField getTariffField() {
        return tariffField;
    }

    public EntityField getServiceField() {
        return this.serviceEntityField;
    }

    public EntityField getDisconnectionReasonField() {
        return this.disconnectionReasonField;
    }

    @Override
    public List<FullEntityField> getFullFields() {
        return this.fullFields;
    }

    @Override
    public String getEntityNameKey() {
        return "renderedServices";
    }

    @Override
    public Class getEntityClass() {
        return RenderedService.class;
    }


    public ValidationResult save(ConnectionRenderedService entity) {
        ValidationResult result = this.getValidator().validate(entity);

        SubscriberTariff subscriberTariff = new SubscriberTariff();
        subscriberTariff.setTariffId(entity.getTariffId());
        subscriberTariff.setSubscriberAccount(entity.getSubscriberAccount());
        subscriberTariff.setConnectTariff(entity.getDate());
        result = validatorSubscriberTariff.validate(subscriberTariff, result);

        if (result.hasErrors()) {
            return result;
        }

        result = periodValidator.validate(entity, result);
        if (result.hasErrors()) {
            return result;
        }

        SubscriberSession subscriberSession = new SubscriberSession();
        subscriberSession.setConnectionDate(entity.getDate());
        subscriberSession.setSubscriberAccount(entity.getSubscriberAccount());
        if (entity.getId() == null) {
            result = this.connectionAdditionValidator.validate(entity, result);
            if (result.hasErrors()) {
                return result;
            }
            getDao().transactionBegin();
            try {
                getDao().save(entity);
                subscriberDAO.saveSubscriberTariff(subscriberTariff);
                subscriberDAO.saveSubscriberSession(subscriberSession);
                getDao().commit();
            } catch (DAOException e) {
                getDao().rollback();
            }

            updateEntitiesList();
            return result;
        }

        RenderedService prevRenderedService = getDao().get(entity.getId());

        result = periodValidator.validate(prevRenderedService, result);
        if (result.hasErrors()) {
            return result;
        }

        if (!prevRenderedService.getSubscriberAccount().equals(entity.getSubscriberAccount())) {
            result = this.connectionAdditionValidator.validate(entity, result);
        }
        result = this.connectionEditValidator.validate(entity, prevRenderedService, result);

        if (result.hasErrors()) {
            return result;
        }
        getDao().transactionBegin();
        try {
            subscriberDAO.deleteSubscriberSession(prevRenderedService.getSubscriberAccount(), prevRenderedService.getDate());
            subscriberDAO.deleteSubscriberTariff(prevRenderedService.getSubscriberAccount(), prevRenderedService.getDate());

            getDao().update(entity);
            subscriberDAO.saveSubscriberSession(subscriberSession);
            subscriberDAO.saveSubscriberTariff(subscriberTariff);
            getDao().commit();
        } catch (DAOException e) {
            getDao().rollback();
        }

        updateEntitiesList();
        return result;
    }

    public ValidationResult save(ReconnectionRenderedService entity) {
        ValidationResult result = this.getValidator().validate(entity);

        if (result.hasErrors()) {
            return result;
        }

        result = periodValidator.validate(entity, result);
        if (result.hasErrors()) {
            return result;
        }

        SubscriberSession subscriberSession = new SubscriberSession();
        subscriberSession.setConnectionDate(entity.getDate());
        subscriberSession.setSubscriberAccount(entity.getSubscriberAccount());
        if (entity.getId() == null) {
            result = this.reconnectionAdditionValidator.validate(entity, result);
            if (result.hasErrors()) {
                return result;
            }
            getDao().transactionBegin();
            try {
                getDao().save(entity);
                subscriberDAO.saveSubscriberSession(subscriberSession);
                getDao().commit();
            } catch (DAOException e) {
                getDao().rollback();
            }
            updateEntitiesList();
            return result;
        }

        RenderedService prevRenderedService = getDao().get(entity.getId());

        result = periodValidator.validate(prevRenderedService, result);
        if (result.hasErrors()) {
            return result;
        }

        if (!prevRenderedService.getSubscriberAccount().equals(entity.getSubscriberAccount())) {
            result = this.reconnectionAdditionValidator.validate(entity, result);
        }
        result = this.connectionEditValidator.validate(entity, prevRenderedService, result);

        if (result.hasErrors()) {
            return result;
        }
        getDao().transactionBegin();
        try {
            subscriberDAO.deleteSubscriberSession(prevRenderedService.getSubscriberAccount(), prevRenderedService.getDate());
            getDao().update(entity);
            subscriberDAO.saveSubscriberSession(subscriberSession);
            getDao().commit();
        } catch (DAOException e) {
            getDao().rollback();
        }

        updateEntitiesList();
        return result;
    }

    public ValidationResult save(DisconnectionRenderedService entity) {
        ValidationResult result = this.getValidator().validate(entity);
        if (result.hasErrors()) {
            return result;
        }

        result = periodValidator.validate(entity, result);
        if (result.hasErrors()) {
            return result;
        }

        if (entity.getId() == null) {
            result = this.disconnectionAdditionValidator.validate(entity, result);
            if (result.hasErrors()) {
                return result;
            }
            SubscriberSession subscriberSession = subscriberDAO.getNotClosedSubscriberSession(entity.getSubscriberAccount(), entity.getDate());
            subscriberSession.setDisconnectionDate(entity.getDate());
            subscriberSession.setDisconnectionReasonId(entity.getDisconnectionReasonId());

            getDao().transactionBegin();
            try {
                getDao().save(entity);
                subscriberDAO.updateSubscriberSession(subscriberSession);
                getDao().commit();
            } catch (DAOException e) {
                getDao().rollback();
            }
            updateEntitiesList();
            return result;
        }

        RenderedService prevRenderedService = getDao().get(entity.getId());

        result = periodValidator.validate(prevRenderedService, result);
        if (result.hasErrors()) {
            return result;
        }

        if (!prevRenderedService.getSubscriberAccount().equals(entity.getSubscriberAccount())) {
            result = this.disconnectionAdditionValidator.validate(entity, result);
        }
        result = this.disconnectionEditValidator.validate(entity, prevRenderedService, result) ;

        if (result.hasErrors()) {
            return result;
        }

        SubscriberSession currentSession = subscriberDAO.getSubscriberSessionByDisconnectionDate(prevRenderedService.getSubscriberAccount(), prevRenderedService.getDate());
        currentSession.setDisconnectionDate(null);
        currentSession.setDisconnectionReasonId(null);
        getDao().transactionBegin();
        try {
            subscriberDAO.updateSubscriberSession(currentSession);
            SubscriberSession newSession = subscriberDAO.getNotClosedSubscriberSession(entity.getSubscriberAccount(), entity.getDate());
            newSession.setDisconnectionDate(entity.getDate());
            newSession.setDisconnectionReasonId(entity.getDisconnectionReasonId());
            getDao().update(entity);
            subscriberDAO.updateSubscriberSession(newSession);
            getDao().commit();
        } catch (DAOException | NullPointerException e) {
            getDao().rollback();
        }

        updateEntitiesList();
        return result;
    }

    public ValidationResult save(ChangeOfTariffRenderedService entity) {
        ValidationResult result = this.getValidator().validate(entity);
        if (result.hasErrors()) {
            return result;
        }

        result = periodValidator.validate(entity, result);
        if (result.hasErrors()) {
            return result;
        }

        SubscriberTariff newSubscriberTariff = new SubscriberTariff();
        newSubscriberTariff.setTariffId(entity.getTariffId());
        newSubscriberTariff.setSubscriberAccount(entity.getSubscriberAccount());
        newSubscriberTariff.setConnectTariff(entity.getDate());

        if (entity.getId() == null) {

            result = this.changeTariffAdditionValidator.validate(entity, result);
            if (result.hasErrors()) {
                return result;
            }

            SubscriberTariff subscriberTariff = subscriberDAO.getNotClosedSubscriberTariff(entity.getSubscriberAccount(), entity.getDate());
            subscriberTariff.setDisconnectTariff(entity.getDate());
            getDao().transactionBegin();
            try {
                getDao().save(entity);
                subscriberDAO.updateSubscriberTariff(subscriberTariff);
                subscriberDAO.saveSubscriberTariff(newSubscriberTariff);
                getDao().commit();
            } catch (DAOException e) {
                getDao().rollback();
            }
            updateEntitiesList();
            return result;
        }

        RenderedService prevRenderedService = getDao().get(entity.getId());

        result = periodValidator.validate(prevRenderedService, result);
        if (result.hasErrors()) {
            return result;
        }

        if (!(prevRenderedService.getSubscriberAccount().equals(entity.getSubscriberAccount()))) {
            result = this.changeTariffAdditionValidator.validate(entity, result);
        }
        result = this.changeTariffEditValidator.validate(entity, prevRenderedService, result);

        if (result.hasErrors()) {
            return result;
        }

        getDao().transactionBegin();
        try {
            subscriberDAO.deleteSubscriberTariff(prevRenderedService.getSubscriberAccount(), prevRenderedService.getDate());
            SubscriberTariff oldSubscriberTariff = subscriberDAO.getSubscriberTariffByDisconnectionDate(prevRenderedService.getSubscriberAccount(), prevRenderedService.getDate());
            oldSubscriberTariff.setDisconnectTariff(null);
            subscriberDAO.updateSubscriberTariff(oldSubscriberTariff);
            getDao().update(entity);
            SubscriberTariff notClosedSubscriberTariffNewSubscriber = subscriberDAO.getNotClosedSubscriberTariff(entity.getSubscriberAccount(), entity.getDate());
            notClosedSubscriberTariffNewSubscriber.setDisconnectTariff(newSubscriberTariff.getConnectTariff());
            subscriberDAO.updateSubscriberTariff(notClosedSubscriberTariffNewSubscriber);
            subscriberDAO.saveSubscriberTariff(newSubscriberTariff);
            getDao().commit();
        } catch (DAOException | NullPointerException e) {
            getDao().rollback();
        }
        updateEntitiesList();
        return result;
    }

    public ValidationResult save(RenderedService entity) {
        ValidationResult result = this.getValidator().validate(entity);

        if (result.hasErrors()) {
            return result;
        }

        result = periodValidator.validate(entity, result);
        if (result.hasErrors()) {
            return result;
        }

        if (entity.getId() == null) {
            getDao().save(entity);
            return result;
        }

        RenderedService prevRenderedService = getDao().get(entity.getId());

        result = periodValidator.validate(prevRenderedService, result);
        if (result.hasErrors()) {
            return result;
        }

        getDao().update(entity);
        updateEntitiesList();
        return result;
    }

    public ValidationResult save(MaterialsRenderedService entity) {
        ValidationResult result = this.getValidator().validate(entity);
        for (MaterialConsumption materialConsumption : entity.getMaterialConsumption()) {
            result = MainModel.getMaterialConsumptionEntityModel().getValidator().validate(materialConsumption, result);
        }
        if (result.hasErrors()) {
            return result;
        }

        result = periodValidator.validate(entity, result);
        if (result.hasErrors()) {
            return result;
        }

        if (entity.getId() == null) {
            getDao().transactionBegin();
            try {
                getDao().save(entity);
                for (MaterialConsumption materialConsumption : entity.getMaterialConsumption()) {
                    materialConsumption.setRenderedServiceId(entity.getId());
                    materialConsumptionDAO.save(materialConsumption);
                }
                getDao().commit();
            } catch (DAOException e) {
                getDao().rollback();
            }
            return result;
        }

        RenderedService prevRenderedService = getDao().get(entity.getId());

        result = periodValidator.validate(prevRenderedService, result);
        if (result.hasErrors()) {
            return result;
        }
        getDao().transactionBegin();
        try {
            getDao().update(entity);
            updateMaterials(entity);
            getDao().commit();
        } catch (DAOException e) {
            getDao().rollback();
        }
        updateEntitiesList();
        return result;
    }


    public ValidationResult deleteRenderedServicesById(List<Integer> ids) {
        ValidationResult result = ValidationResult.createEmpty();
        if (ids.size() == 0) {
            return result;
        }

        for (Integer id : ids) {
            RenderedService entity = getDao().get(id);
            periodValidator.validate(entity, result);
            if(entity.getServiceId().equals(FixedServices.CONNECTION.getId())) {
                isDeleteConnection(entity, result);
            }
            if(entity.getServiceId().equals(FixedServices.DISCONNECTION.getId())) {
                isDeleteDisconnection(entity, result);
            }
            if(entity.getServiceId().equals(FixedServices.CHANGE_OF_TARIFF.getId())) {
                isDeleteChangeTariff(entity, result);
            }
            if(entity.getServiceId().equals(FixedServices.RECONNECTION.getId())) {
                isDeleteReconnection(entity, result);
            }
        }
        if (result.hasErrors()) {
            return result;
        }
        for (Integer id : ids) {
            RenderedService entity = getDao().get(id);
            getDao().transactionBegin();
            try {
                if (entity.getServiceId().equals(FixedServices.CONNECTION.getId())) {
                    deleteConnection(entity);
                } else if (entity.getServiceId().equals(FixedServices.DISCONNECTION.getId())) {
                    deleteDisconnection(entity);
                } else if (entity.getServiceId().equals(FixedServices.CHANGE_OF_TARIFF.getId())) {
                    deleteChangeTariff(entity);
                } else if (entity.getServiceId().equals(FixedServices.RECONNECTION.getId())) {
                    deleteReconnection(entity);
                } else if (entity.getServiceId().equals(FixedServices.MATERIALS.getId())) {
                    deleteMaterials(entity);
                } else {
                    getDao().delete(entity.getId());
                }
                getDao().commit();
            } catch (DAOException e) {
                getDao().rollback();
            }

        }
        updateEntitiesList();
        return result;
    }

    private ValidationResult isDeleteConnection(RenderedService entity, ValidationResult result) {
        connectionDeleteValidator.validate(entity, result);
        return result;
    }

    private void deleteConnection(RenderedService entity) {
        subscriberDAO.deleteSubscriberSession(entity.getSubscriberAccount(), entity.getDate());
        SubscriberTariff oldSubscriberTariff = subscriberDAO.getSubscriberTariffAtDate(entity.getSubscriberAccount(), entity.getDate());
        if (entity.getDate().equals(oldSubscriberTariff.getConnectTariff())) {
            subscriberDAO.deleteSubscriberTariff(entity.getSubscriberAccount(), entity.getDate());
        }
        getDao().delete(entity.getId());
    }

    private ValidationResult isDeleteReconnection(RenderedService entity, ValidationResult result) {
        connectionDeleteValidator.validate(entity, result);
        return result;
    }

    private void deleteReconnection(RenderedService entity) {
        subscriberDAO.deleteSubscriberSession(entity.getSubscriberAccount(), entity.getDate());
        getDao().delete(entity.getId());
    }

    private ValidationResult isDeleteDisconnection(RenderedService entity, ValidationResult result) {
        disconnectionDeleteValidator.validate(entity, result);
        return result;
    }

    private void deleteDisconnection(RenderedService entity) {
        SubscriberSession currentSession = subscriberDAO.getSubscriberSessionByDisconnectionDate(entity.getSubscriberAccount(), entity.getDate());
        currentSession.setDisconnectionDate(null);
        currentSession.setDisconnectionReasonId(null);
        subscriberDAO.updateSubscriberSession(currentSession);
        getDao().delete(entity.getId());
    }

    private ValidationResult isDeleteChangeTariff(RenderedService entity, ValidationResult result) {
        changeTariffDeleteValidator.validate(entity, result);
        return result;
    }

    private void deleteChangeTariff(RenderedService entity) {
        subscriberDAO.deleteSubscriberTariff(entity.getSubscriberAccount(), entity.getDate());
        SubscriberTariff oldSubscriberTariff = subscriberDAO.getSubscriberTariffByDisconnectionDate(entity.getSubscriberAccount(), entity.getDate());
        oldSubscriberTariff.setDisconnectTariff(null);
        subscriberDAO.updateSubscriberTariff(oldSubscriberTariff);
        getDao().delete(entity.getId());
    }

    private void deleteMaterials(RenderedService entity) {
        List<MaterialConsumption> materialConsumptionList = materialConsumptionDAO.getByRenderedServiceId(entity.getId());
        for (MaterialConsumption materialConsumption : materialConsumptionList) {
            materialConsumptionDAO.delete(materialConsumption.getId());
        }
        getDao().delete(entity.getId());
    }

    @Override
    protected RenderedServiceDAO getDao() {
        return DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO();
    }

    @Override
    public RenderedService createNewEntity() {
        return new RenderedService();
    }

    @Override
    public Validator<RenderedService> getValidator() {
        return this.validator;
    }

    private void updateMaterials(MaterialsDTO entity) {
        List<MaterialConsumption> materialConsumptionList = materialConsumptionDAO.getByRenderedServiceId(entity.getId());

        for (MaterialConsumption materialConsumption : materialConsumptionList) {
            if (!entity.getMaterialConsumption().contains(materialConsumption)) {
                materialConsumptionDAO.delete(materialConsumption.getId());
            }
        }

        for (MaterialConsumption materialConsumption : entity.getMaterialConsumption()) {
            if (materialConsumption.getId() != null) {
                materialConsumptionDAO.update(materialConsumption);
            } else {
                materialConsumption.setRenderedServiceId(entity.getId());
                materialConsumptionDAO.save(materialConsumption);
            }
        }
    }

    public void setServiceFieldEditable(boolean editable) {
        for (EntityField field : fields) {
            if (field.getTitleKey().equals("service")) {
                field.setEditable(editable);
            }
        }
    }

    public BigDecimal getRenderedServicePriceByDate(Integer serviceId, LocalDate date) {
        if (serviceId != null && date != null) {
            return serviceDAO.getPriceByDate(serviceId, date);
        }
        return BigDecimal.ZERO;
    }

    public Integer getSubscriberTariff(Integer subscriberAccount, LocalDate date) {
        if (subscriberAccount != null && date != null) {
            SubscriberTariff subscriberTariff = subscriberDAO.getSubscriberTariffAtDate(subscriberAccount, date);
            return subscriberTariff != null ? subscriberTariff.getTariffId() : null;
        }
        return null;
    }

    public void setDate(LocalDate date) {
        this.date = date;
        updateEntitiesList();
    }

    public List<RenderedService> getAll() {
        return DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO().getByMonth(this.date)
                .stream().filter(e -> e.getServiceId() != FixedServices.SUBSCRIPTION_FEE.getId()).collect(Collectors.toList());
    }

    public LocalDate getDate() {
        return this.date;
    }

    @Override
    public EntitySearcherModel<RenderedService> createSearchModel() {
        return new RenderedServiceSearcherModel();
    }
}
