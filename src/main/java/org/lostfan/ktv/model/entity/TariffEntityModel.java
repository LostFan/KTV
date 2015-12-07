package org.lostfan.ktv.model.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.lostfan.ktv.dao.DAOFactory;
import org.lostfan.ktv.dao.EntityDAO;
import org.lostfan.ktv.dao.TariffDAO;
import org.lostfan.ktv.domain.Tariff;
import org.lostfan.ktv.domain.TariffPrice;
import org.lostfan.ktv.model.EntityField;
import org.lostfan.ktv.model.EntityFieldTypes;
import org.lostfan.ktv.model.MainModel;
import org.lostfan.ktv.model.dto.TariffWithPrices;
import org.lostfan.ktv.model.transform.TariffWithPricesTransformer;
import org.lostfan.ktv.validation.ValidationResult;

public class TariffEntityModel extends BaseEntityModel<Tariff> {

    private List<EntityField> fields;

    private TariffWithPricesTransformer tariffWithPricesTransformer;

    public TariffEntityModel() {
        fields = new ArrayList<>();
        tariffWithPricesTransformer = new TariffWithPricesTransformer();

        this.fields = new ArrayList<>();
        this.fields.add(new EntityField("tariff.id", EntityFieldTypes.Integer, Tariff::getId, Tariff::setId, false));
        this.fields.add(new EntityField("tariff.name", EntityFieldTypes.String, Tariff::getName, Tariff::setName));
        this.fields.add(new EntityField("tariff.digital", EntityFieldTypes.Boolean, Tariff::isDigital, Tariff::setDigital));
        this.fields.add(new EntityField("tariff.channels", EntityFieldTypes.String, Tariff::getChannels, Tariff::setChannels));
    }

    @Override
    public List<EntityModel> getEntityModels() {
        List<EntityModel> entityModels = new ArrayList<>();
        entityModels.add(MainModel.getServiceEntityModel());
        entityModels.add(MainModel.getSubscriberEntityModel());
        return entityModels;
    }

    public TariffWithPrices getTariffWithPrices(Integer tariffId) {
        TariffWithPrices tarrif = tariffWithPricesTransformer.transformTo(getEntity(tariffId));
        tarrif.setArchivePrices(new ArrayList<>());
        List<TariffPrice> prices = getDao().getTariffPrices(tariffId);
        // Sort by date DESC
        prices.stream()
                .sorted((price1, price2) -> {
                    if (price1.getDate().isAfter(price2.getDate())) {
                        return -1;
                    } else if (price1.getDate().equals(price2.getDate())) {
                        return 1;
                    } else {
                        return 0;
                    }
                })
                .forEach(tariffPrice -> {
                    if (tariffPrice.getDate().isAfter(LocalDate.now())) {
                        tarrif.setNewPrice(tariffPrice);
                    } else if (tarrif.getCurrentPrice() == null) {
                        tarrif.setCurrentPrice(tariffPrice);
                    } else {
                        tarrif.getArchivePrices().add(tariffPrice);
                    }
                });
        return tarrif;
    }

    public ValidationResult save(TariffPrice price) {
        // TODO: validate and save the new price
        return ValidationResult.createEmpty();
    }

    @Override
    public String getEntityName() {
        return "tariff";
    }

    @Override
    public List<EntityField> getFields() {
        return this.fields;
    }

    @Override
    public String getEntityNameKey() {
        return "tariffs";
    }

    @Override
    public Class getEntityClass() {
        return Tariff.class;
    }

    @Override
    protected TariffDAO getDao() {
        return DAOFactory.getDefaultDAOFactory().getTariffDAO();
    }

    @Override
    public Tariff createNewEntity() {
        return new Tariff();
    }
}
