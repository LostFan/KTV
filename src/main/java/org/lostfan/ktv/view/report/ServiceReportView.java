package org.lostfan.ktv.view.report;


import org.lostfan.ktv.domain.Entity;
import org.lostfan.ktv.model.EntityField;
import org.lostfan.ktv.model.EntityFieldTypes;
import org.lostfan.ktv.model.ServiceReportModel;
import org.lostfan.ktv.model.TurnoverReportModel;
import org.lostfan.ktv.model.dto.ServiceReportSheetTableDTO;
import org.lostfan.ktv.utils.ResourceBundles;
import org.lostfan.ktv.utils.ViewActionListener;
import org.lostfan.ktv.validation.NotNullValidator;
import org.lostfan.ktv.validation.ValidationResult;
import org.lostfan.ktv.view.FormView;
import org.lostfan.ktv.view.components.EntityPanel;
import org.lostfan.ktv.view.components.EntityPanelFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReportView extends FormView {

    private class ModelObserver implements org.lostfan.ktv.utils.Observer {
        @Override
        public void update(Object args) {
            ServiceReportView.this.progressBar.setValue((Integer) args);
        }
    }

    private class ReportTableModel extends AbstractTableModel {

        private List<ServiceReportSheetTableDTO> serviceReportSheetTableDTOs = new ArrayList<>();

        public ReportTableModel() {
        }

        public ReportTableModel(List<ServiceReportSheetTableDTO> serviceReportSheetTableDTOs) {
            this.serviceReportSheetTableDTOs = serviceReportSheetTableDTOs;
        }

        @Override
        public int getRowCount() {
            return serviceReportSheetTableDTOs.size();
        }

        @Override
        public int getColumnCount() {
            return 7;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0: return this.serviceReportSheetTableDTOs.get(rowIndex).getSubscriberAccount();
            }

            return null;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0: return ResourceBundles.getEntityBundle().getString(
                        "subscriber");
            }

            return null;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Integer.class;
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

            this.panel.setParentView(ServiceReportView.this);
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

    private NotNullValidator validator = new NotNullValidator();
    private JButton addButton;
    private JButton cancelButton;
    private JButton excelButton;
    private JTable reportTable;
    private JProgressBar progressBar;

    private ServiceReportModel model;

    private ModelObserver modelObserver;

    private ViewActionListener addActionListener;
    private ViewActionListener cancelActionListener;

    private DateFormField startDateField;
    private DateFormField endDateField;
    private BooleanFormField isAdditionalField;
    private ServiceFormField serviceField;

    public ServiceReportView(ServiceReportModel model) {
        this(model, null);
    }

    public ServiceReportView(ServiceReportModel model, Entity entity) {
        this.model = model;
        reportTable = new JTable(new ReportTableModel());
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        this.reportTable.setPreferredScrollableViewportSize(new Dimension(500, 70));

        setTitle(getEntityString(model.getEntityNameKey()));

        startDateField = new DateFormField("renderedService.startDate");
        addFormField(startDateField);
        endDateField = new DateFormField("renderedService.endDate");
        addFormField(endDateField);
        isAdditionalField = new BooleanFormField("service.additional");
        addFormField(isAdditionalField);
        serviceField = new ServiceFormField("service");
        addFormField(serviceField);

        this.addButton = new JButton(getGuiString("buttons.generateReport"));
        this.addButton.addActionListener(e -> {
            ReportTableModel reportTableModel;
            if(isAdditionalField.getValue()) {
                reportTableModel = new ReportTableModel(
                        model.getTurnoverSheetDataByAdditionalServices(startDateField.getValue(), endDateField.getValue()));
            } else {
                reportTableModel = new ReportTableModel(
                        model.getTurnoverSheetData(startDateField.getValue(), endDateField.getValue(), serviceField.getValue()));
            }
            reportTable.setModel(reportTableModel);
            reportTable.repaint();
            if (this.addActionListener != null) {

            }
        });
        this.excelButton = new JButton(getGuiString("buttons.generateExcelReport"));
        this.excelButton.addActionListener(e -> {
            ValidationResult validationResult = ValidationResult.createEmpty();
            validator.validate(startDateField.getValue(), startDateField.getFieldKey(),
                    validationResult);
            validator.validate(endDateField.getValue(), endDateField.getFieldKey(),
                    validationResult);
            if (!isAdditionalField.getValue()) {
                validator.validate(serviceField.getValue(), serviceField.getFieldKey(),
                        validationResult);
            }

            if (validationResult.hasErrors()) {
                this.showErrors(validationResult.getErrors());
                return;
            }
            this.clearErrors();
            new Thread(() -> {
                String message = model.generateExcelReport(isAdditionalField.getValue(),
                        serviceField.getValue(), startDateField.getValue(), endDateField.getValue());
                if (message != null) {
                    exceptionWindow(message);
                }
            }).start();

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

        this.modelObserver = new ModelObserver();

        model.addObserver(this.modelObserver);

        buildLayout();

        show();
    }

    private void buildLayout() {
        getContentPanel().setLayout(new BorderLayout(10, 10));
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        getContentPanel().add(getFieldPanel(), BorderLayout.PAGE_START);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        this.reportTable.getColumnModel().getColumn(0).setCellRenderer(renderer);
//        addStringActionTableCellEditorToColumns();
        JScrollPane tableScrollPane = new JScrollPane(this.reportTable);

        getContentPanel().add(tableScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(excelButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(progressBar);
        getContentPanel().add(buttonPanel, BorderLayout.SOUTH);
    }


    private void exceptionWindow(String message) {
        int optionType = JOptionPane.OK_OPTION;
        int messageType = JOptionPane.WARNING_MESSAGE;
        Object[] selValues = { getGuiString("buttons.ok")};
        String attention = getGuiString("message.attention");
        JOptionPane.showOptionDialog(null,
                getGuiString(message), attention,
                optionType, messageType, null, selValues,
                selValues[0]);
    }
}
