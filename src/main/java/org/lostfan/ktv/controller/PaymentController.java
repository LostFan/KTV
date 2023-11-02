package org.lostfan.ktv.controller;

import org.lostfan.ktv.domain.Payment;
import org.lostfan.ktv.model.MainModel;
import org.lostfan.ktv.model.entity.PaymentEntityModel;
import org.lostfan.ktv.validation.ValidationResult;
import org.lostfan.ktv.view.DeleteEntitiesByDateView;
import org.lostfan.ktv.view.FormView;
import org.lostfan.ktv.view.entity.LoadPaymentsView;
import org.lostfan.ktv.view.table.PaymentTableView;
import org.lostfan.ktv.view.entity.PaymentView;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class PaymentController extends EntityController{

    private PaymentEntityModel model;
    private PaymentTableView view;

    public PaymentController(PaymentEntityModel model, PaymentTableView view) {
        super(model, view);
        this.model = model;
        this.view = view;
        view.loadActionListener(this::loadActionPerformed);
        view.deleteByDateActionListener(this::deleteByDateActionPerformed);
        view.newDateActionListener(this::newDateActionPerformed);
    }

    private void loadActionPerformed(Object args) {
        PaymentEntityModel paymentEntityModel = MainModel.getPaymentEntityModel();
        LoadPaymentsView loadPaymentsView = new LoadPaymentsView(paymentEntityModel);
        loadPaymentsView.setAddActionListener(args_ -> {
            List<Payment> payments = (List<Payment>) args_;
            paymentEntityModel.savePayments(payments, loadPaymentsView);
        });

        loadPaymentsView.setLoadPaymentFileActionListener(args_ -> {
            List<Payment> payments = (List<Payment>) args_;
            paymentEntityModel.createPayments(payments);
        });
    }

    private void deleteByDateActionPerformed(Object args) {
        PaymentEntityModel paymentEntityModel = MainModel.getPaymentEntityModel();
        DeleteEntitiesByDateView deleteEntitiesByDateView = new DeleteEntitiesByDateView(paymentEntityModel);
        deleteEntitiesByDateView.setDeleteActionListener(args_ -> {
            LocalDate date = (LocalDate) args_;
            ValidationResult result = paymentEntityModel.deletePaymentsByDate(date);
            if (result.hasErrors()) {
                deleteEntitiesByDateView.showErrors(result.getErrors());
                return;
            }
            deleteEntitiesByDateView.hide();

        });
    }

    private void newDateActionPerformed(Object args) {
        LocalDate date = (LocalDate) args;
        this.model.setDate(date);
    }

    @Override
    public void addActionPerformed(Object args) {
        PaymentView entityView = new PaymentView(model);
        entityView.setAddPaymentsInTableListener(args1 -> {
            Payment payment = (Payment) args1;
            entityView.addPayments(model.loadPayments(payment, null));
        });
        entityView.setAddActionListener(args_ -> {
            List<Payment> payments = (List<Payment>) args_;
            model.savePayments(payments, entityView);
        });
    }

    protected void changeActionPerformed(Object args) {
        int selectedId = (Integer) args;

        Payment entity = model.getEntity(selectedId);
        PaymentView entityView = new PaymentView(model, entity);

        entityView.setAddPaymentsInTableListener(args1 -> {
            Payment payment = (Payment) args1;
            entityView.addPayments(model.loadPayments(payment, entity));
        });
        entityView.setAddActionListener(args_ -> {
            List<Payment> payments = (List<Payment>) args_;
            updatePayments(payments,
                    model.getList(entity.getSubscriberAccount(),entity.getDate(),entity.getBankFileName()),
                    entityView);
        });
    }

    private void updatePayments(List<Payment> payments, List<Payment> oldPayments, FormView view) {
        ValidationResult result = ValidationResult.createEmpty();

        for (Payment payment : oldPayments) {
            result = model.getValidator().validate(payment, result);
            result = model.getPeriodValidator().validate(payment, result);
            if (result.hasErrors()) {
                view.showErrors(result.getErrors());
                return;
            }
        }

        for (Payment payment : payments) {
            result = model.getValidator().validate(payment, result);
            result = model.getPeriodValidator().validate(payment, result);
            if (result.hasErrors()) {
                view.showErrors(result.getErrors());
                return;
            }
        }

        model.update(payments, oldPayments);

        view.hide();
    }

    @Override
    protected void deleteActionPerformed(Object args) {
        List<Integer> selectedIds = (List<Integer>) args;
        ValidationResult result = model.deleteEntityById(selectedIds);
        if (result.hasErrors()) {
            view.errorWindow(result.getErrors());
        }
    }
}
