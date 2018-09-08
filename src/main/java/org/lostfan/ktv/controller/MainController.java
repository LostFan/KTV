package org.lostfan.ktv.controller;

import org.lostfan.ktv.domain.RenderedService;
import org.lostfan.ktv.model.*;
import org.lostfan.ktv.model.dto.*;
import org.lostfan.ktv.model.entity.EntityModel;
import org.lostfan.ktv.model.entity.RenderedServiceEntityModel;
import org.lostfan.ktv.model.entity.BaseModel;
import org.lostfan.ktv.validation.ValidationResult;
import org.lostfan.ktv.view.*;
import org.lostfan.ktv.view.components.EntityViewFactory;
import org.lostfan.ktv.view.entity.EntityView;
import org.lostfan.ktv.view.entity.PeriodView;
import org.lostfan.ktv.view.entity.TariffTableView;
import org.lostfan.ktv.view.report.*;
import org.lostfan.ktv.view.table.EntityTableView;
import org.lostfan.ktv.view.table.PaymentTableView;
import org.lostfan.ktv.view.table.RenderedServiceTableView;
import org.lostfan.ktv.view.table.ServiceTableView;
import org.lostfan.ktv.view.table.SubscriberTableView;
import org.lostfan.ktv.view.table.SubscriptionFeeTableView;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MainController {

    private MainView view;
    private MainModel model;

    private Map<String, MainInnerController> innerControllers;

    public MainController(MainModel model, MainView view) {
        this.model = model;
        this.view = view;

        innerControllers = new HashMap<>();
        this.model.getEntityItems().forEach(this::createEntityController);
        this.model.getDocumentItems().forEach(this::createEntityController);

        this.view.setMenuEntityActionListener(args -> {
            String code = (String) args;
            model.setCurrentModel(code);
        });

        this.view.setMenuServiceActionListener(args -> {
            String code = (String) args;
            RenderedServiceEntityModel renderedServiceModel = MainModel.getRenderedServiceEntityModel();
            EntityView entityView = EntityViewFactory.createRenderedServiceForm(renderedServiceModel, FixedServices.of(code));
            entityView.setAddActionListener(args_ -> {
                ValidationResult result = ValidationResult.createEmpty();
                if (FixedServices.of(code) == FixedServices.CONNECTION) {
                    ConnectionRenderedService entity = (ConnectionRenderedService) args_;
                    result = renderedServiceModel.save(entity);
                }
                if (FixedServices.of(code) == FixedServices.RECONNECTION) {
                    ReconnectionRenderedService entity = (ReconnectionRenderedService) args_;
                    result = renderedServiceModel.save(entity);
                }
                if (FixedServices.of(code) == FixedServices.DISCONNECTION) {
                    DisconnectionRenderedService entity = (DisconnectionRenderedService) args_;
                    result = renderedServiceModel.save(entity);
                }
                if (FixedServices.of(code) == FixedServices.CHANGE_OF_TARIFF) {
                    ChangeOfTariffRenderedService entity = (ChangeOfTariffRenderedService) args_;
                    result = renderedServiceModel.save(entity);
                }
                if (FixedServices.of(code) == FixedServices.ADDITIONAL_SERVICE) {
                    RenderedService entity = (RenderedService) args_;
                    result = renderedServiceModel.save(entity);
                }
                if (FixedServices.of(code) == FixedServices.MATERIALS) {
                    MaterialsRenderedService entity = (MaterialsRenderedService) args_;
                    result = renderedServiceModel.save(entity);
                }
                if (result.hasErrors()) {
                    entityView.showErrors(result.getErrors());
                    return;
                }
                entityView.hide();
            });
        });
        this.view.setMenuReportActionListener(args -> {
            String code = (String) args;
            switch (Reports.of(code)) {
                case TURNOVER_SHEET:
                    TurnoverReportModel turnoverReportModel = new TurnoverReportModel();
                    TurnoverReportView turnoverReportView = new TurnoverReportView(turnoverReportModel);
                    break;
                case DAILY_REGISTER:
                    new DailyRegisterController();
                    break;
                case CONSOLIDATED_REGISTER_ON_PAYMENT:
                    ConsolidatedRegisterPaymentModel consolidatedRegisterPaymentModel = new ConsolidatedRegisterPaymentModel();
                    ConsolidatedRegisterPaymentView consolidatedRegisterPaymentView = new ConsolidatedRegisterPaymentView(consolidatedRegisterPaymentModel);
                    break;
                case REPORT_TO_BANK:
                    new ReportToBankController();
                    break;
                case DEBTOR_REPORT:
                    DebtorReportModel debtorReportModel = new DebtorReportModel();
                    DebtorReportView debtorReportView = new DebtorReportView(debtorReportModel);
                    break;
                case WRITE_OFF_REPORT:
                    WriteOffReportModel writeOffReportModel = new WriteOffReportModel();
                    WriteOffReportView writeOffReportView = new WriteOffReportView(writeOffReportModel);
                    break;
                case CLAIM_REPORT:
                    ClaimReportModel claimReportModel = new ClaimReportModel();
                    ClaimReportView claimReportView = new ClaimReportView(claimReportModel);
                    break;
                case CHANNELS_REPORT:
                    ChannelsReportModel channelsReportModel = new ChannelsReportModel();
                    ChannelsReportView channelsReportView = new ChannelsReportView(channelsReportModel);
                    break;
                case SERVICE_SHEET:
                    ServiceReportModel serviceReportModel= new ServiceReportModel();
                    new ServiceReportView(serviceReportModel);
                    break;
                case DISCONNECTED_SUBSCRIBERS_SHEET:
                    DisconnectedSubscribersReportModel disconnectedSubscribersReportModel = new DisconnectedSubscribersReportModel();
                    new DisconnectedSubscribersReportView(disconnectedSubscribersReportModel);
            };
        });
        this.view.setMenuFileActionListener(args -> {
            String code = (String) args;
            switch (FileMenu.of(code)) {
                case EDIT_PERIOD:
                    PeriodModel periodModel = new PeriodModel();
                    PeriodView reportView = new PeriodView(periodModel);
                    reportView.setChangeActionListener(args_ -> {
                        LocalDate date = (LocalDate) args_;
                        periodModel.savePeriod(date);
                    });
                    break;
                case CONVERTER_10000_TO_1:
                    new ConverterController(FileMenu.CONVERTER_10000_TO_1);
                    break;
                case CONVERTER_1_TO_10000:
                    new ConverterController(FileMenu.CONVERTER_1_TO_10000);
                    break;
            };
        });

        this.model.addObserver(args -> {
            BaseModel currentModel = (BaseModel) args;
            MainInnerController currentController = this.innerControllers.get(currentModel.getEntityNameKey());
            view.setInnerView(currentController.getView());
        });
    }

    private void createEntityController(String entityCode) {
        MainInnerController entityController;
        if (entityCode.equals(MainModel.getRenderedServiceEntityModel().getEntityNameKey())) {
            entityController = new RenderedServiceController(MainModel.getRenderedServiceEntityModel(),
                    new RenderedServiceTableView(MainModel.getRenderedServiceEntityModel()));
        } else if (entityCode.equals(MainModel.getTariffEntityModel().getEntityNameKey())) {
            entityController = new TariffEntityController(MainModel.getTariffEntityModel(),
                    new TariffTableView(MainModel.getTariffEntityModel()));
        } else if (entityCode.equals(MainModel.getServiceEntityModel().getEntityNameKey())) {
            entityController = new ServiceEntityController(MainModel.getServiceEntityModel(),
                    new ServiceTableView(MainModel.getServiceEntityModel()));
        } else if (entityCode.equals(MainModel.getSubscriptionFeeModel().getEntityNameKey())) {
            entityController = new SubscriptionFeeController(MainModel.getSubscriptionFeeModel(),
                    new SubscriptionFeeTableView(MainModel.getSubscriptionFeeModel()));
        } else if (entityCode.equals(MainModel.getPaymentEntityModel().getEntityNameKey())) {
            entityController = new PaymentController(MainModel.getPaymentEntityModel(),
                    new PaymentTableView(MainModel.getPaymentEntityModel()));
        } else if (entityCode.equals(MainModel.getSubscriberEntityModel().getEntityNameKey())) {
            entityController = new SubscriberController(MainModel.getSubscriberEntityModel(),
                    new SubscriberTableView(MainModel.getSubscriberEntityModel()));
        }
        else {
            EntityModel model = MainModel.getEntityModel(entityCode);
            entityController = new EntityController(model, new EntityTableView(model));
        }
        this.innerControllers.put(entityCode, entityController);
    }
}
