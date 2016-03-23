package org.lostfan.ktv.view;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.lostfan.ktv.model.DailyRegisterModel;
import org.lostfan.ktv.model.EntityField;
import org.lostfan.ktv.model.EntityFieldTypes;
import org.lostfan.ktv.model.dto.PaymentExt;
import org.lostfan.ktv.model.entity.PaymentEntityModel;
import org.lostfan.ktv.utils.ResourceBundles;
import org.lostfan.ktv.utils.ViewActionListener;
import org.lostfan.ktv.view.components.EntityPanel;
import org.lostfan.ktv.view.components.EntityPanelFactory;

public class DailyRegisterView extends FormView {


    private class ReportTableModel extends AbstractTableModel {

        private List<PaymentExt> paymentExts = new ArrayList<>();

        public ReportTableModel() {
        }

        public ReportTableModel(List<PaymentExt> payments) {
            this.paymentExts = payments;
        }

        public ReportTableModel(PaymentExt payment) {
            this.paymentExts.add(payment);
        }

        @Override
        public int getRowCount() {
            return paymentExts.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return this.paymentExts.get(rowIndex).getSubscriber() != null ? String.format("%s (%d)",
                            this.paymentExts.get(rowIndex).getSubscriber().getName(),
                            this.paymentExts.get(rowIndex).getSubscriber().getAccount()) :
                            this.paymentExts.get(rowIndex).getSubscriberAccount();
                case 1:
                    return this.paymentExts.get(rowIndex).getSubscriber() != null ?
                            this.paymentExts.get(rowIndex).getService().getName() :
                            null;
                case 2:
                    return this.paymentExts.get(rowIndex).getPrice();
            }

            return null;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return ResourceBundles.getEntityBundle().getString(
                            "subscriber");
                case 1:
                    return ResourceBundles.getEntityBundle().getString(
                            "service");
                case 2:
                    return ResourceBundles.getEntityBundle().getString(
                            "servicePrice.price");
            }

            return null;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return String.class;
                case 1:
                    return String.class;
                case 2:
                    return Integer.class;
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }

    private class FooterModel extends AbstractTableModel {

        Integer allPrice;

        public FooterModel() {
        }

        public FooterModel(Integer allPrice) {
            this.allPrice = allPrice;
        }


        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return getGuiString("total");
                case 2:
                    return allPrice;
            }

            return null;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }

    private class ServiceFormField extends FormField<Integer> {

        private EntityPanel panel;

        public ServiceFormField(String fieldKey) {
            super(fieldKey);
            this.panel = EntityPanelFactory.createEntityPanel(EntityFieldTypes.Service);

            this.panel.setParentView(DailyRegisterView.this);
        }

        @Override
        public JComponent getInputComponent() {
            return panel;
        }

        @Override
        public Integer getValue() {
            return this.panel.getSelectedEntity() != null ? this.panel.getSelectedEntity().getId() : null;
        }

        @Override
        public void setValue(Integer value) {

        }
    }

    private JButton addButton;
    private JButton cancelButton;
    private JButton excelButton;
    private JTable reportTable;
    private JTable footerTable;

    private DailyRegisterModel model;

    private ViewActionListener addActionListener;
    private ViewActionListener cancelActionListener;

    private DateFormField dateField;
    private BooleanFormField isAdditionalField;
    private ServiceFormField serviceField;

    public DailyRegisterView(DailyRegisterModel model) {
        this.model = model;
        reportTable = new JTable(new ReportTableModel());
        this.reportTable.setPreferredScrollableViewportSize(new Dimension(500, 70));

        setTitle(getEntityString(model.getEntityNameKey()));

        dateField = new DateFormField("renderedService.date");
        addFormField(dateField);
        isAdditionalField = new BooleanFormField("service.additional");
//        addFormField(isAdditionalField);
        serviceField = new ServiceFormField("service");
//        addFormField(serviceField);

        this.addButton = new JButton(getGuiString("buttons.generateReport"));
        this.addButton.addActionListener(e -> {
            List<PaymentExt> paymentExts = model.getPaymentsExtByDate(dateField.getValue());
            ReportTableModel reportTableModel = new ReportTableModel(
                    paymentExts);
            reportTable.setModel(reportTableModel);
            if (paymentExts.size() > 0) {
                Integer allPrice = paymentExts.stream().mapToInt(o -> o.getPrice()).sum();
                footerTable.setModel(new FooterModel(allPrice));
            }
            reportTable.repaint();
            if (this.addActionListener != null) {

            }
        });
        this.excelButton = new JButton(getGuiString("buttons.generateExcelReport"));
        this.excelButton.addActionListener(e -> {
            if (dateField.getValue() == null) {
                dateField.setError("errors.empty");
                return;
            }
            dateField.clearError();
            String message = model.generateExcelReport(dateField.getValue());
            if (message != null) {
                exceptionWindow(message);
            }

        });


        this.cancelButton = new JButton(getGuiString("buttons.cancel"));
        this.cancelButton.addActionListener(e -> {
            if (this.cancelActionListener != null) {
                this.cancelActionListener.actionPerformed(null);
            }
            hide();
        });

        for (EntityField entityField : model.getFields()) {
            if (!entityField.isEditable()) {
                continue;
            }

        }


        this.isAdditionalField.addValueListener(newValue -> {
            if ((isAdditionalField.getValue())) {
                serviceField.setVisible(false);
            } else {
                serviceField.setVisible(true);
            }
        });

        buildLayout();

        show();
    }

    private void buildLayout() {
        getContentPanel().setLayout(new BorderLayout(10, 10));
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        getContentPanel().add(getFieldPanel(), BorderLayout.PAGE_START);

        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        this.reportTable.getColumnModel().getColumn(0).setCellRenderer(renderer);

        JScrollPane tableScrollPane = new JScrollPane(this.reportTable);
        panel.add(BorderLayout.CENTER, tableScrollPane);
        footerTable = new JTable(new FooterModel());
        footerTable.setRowSelectionAllowed(false);
        footerTable.setColumnSelectionAllowed(false);

        tableScrollPane = new JScrollPane(footerTable);
        footerTable.setTableHeader(null);
        footerTable.setPreferredScrollableViewportSize(new Dimension(500, 17));

        panel.add(BorderLayout.SOUTH, tableScrollPane);

        getContentPanel().add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(excelButton);
        buttonPanel.add(cancelButton);
        getContentPanel().add(buttonPanel, BorderLayout.SOUTH);
    }


    private void exceptionWindow(String message) {
        int optionType = JOptionPane.OK_OPTION;
        int messageType = JOptionPane.WARNING_MESSAGE;
        Object[] selValues = {getGuiString("buttons.ok")};
        String attention = getGuiString("message.attention");
        JOptionPane.showOptionDialog(null,
                getGuiString(message), attention,
                optionType, messageType, null, selValues,
                selValues[0]);
    }
}
