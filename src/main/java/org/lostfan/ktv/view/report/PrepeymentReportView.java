package org.lostfan.ktv.view.report;


import org.lostfan.ktv.model.EntityFieldTypes;
import org.lostfan.ktv.model.PrepaymentReportModel;
import org.lostfan.ktv.model.WriteOffReportModel;
import org.lostfan.ktv.model.dto.SubscriberDebit;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PrepeymentReportView extends FormView {


    private class ReportTableModel extends AbstractTableModel {

        private List<SubscriberDebit> subscriberDebits = new ArrayList<>();

        public ReportTableModel() {
        }

        public ReportTableModel(List<SubscriberDebit> subscriberDebits) {
            this.subscriberDebits = subscriberDebits;
        }

        @Override
        public int getRowCount() {
            return subscriberDebits.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0: return this.subscriberDebits.get(rowIndex).getSubscriberAccount();
                case 1: return this.subscriberDebits.get(rowIndex).getDebit();
            }

            return null;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0: return ResourceBundles.getEntityBundle().getString(
                        "subscriber");
                case 1: return ResourceBundles.getGuiBundle().getString(
                        "debit");
            }

            return null;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Integer.class : BigDecimal.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }

    private class TariffFormField extends FormField<Integer> {

        private EntityPanel panel;

        public TariffFormField(String fieldKey) {
            super(fieldKey);
            this.panel = EntityPanelFactory.createEntityPanel(EntityFieldTypes.Tariff);

            this.panel.setParentView(PrepeymentReportView.this);
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

    private PrepaymentReportModel model;

    private ViewActionListener addActionListener;
    private ViewActionListener cancelActionListener;

    private DateFormField dateField;

    public PrepeymentReportView(PrepaymentReportModel model) {

        this.model = model;
        reportTable = new JTable(new ReportTableModel());
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        this.reportTable.setPreferredScrollableViewportSize(new Dimension(500, 70));

        setTitle(getEntityString(model.getEntityNameKey()));

        dateField = new DateFormField("renderedService.date");
        addFormField(dateField);

        this.addButton = new JButton(getGuiString("buttons.generateReport"));
        this.addButton.addActionListener(e -> {
            ValidationResult validationResult = ValidationResult.createEmpty();
            validator.validate(dateField.getValue(), dateField.getFieldKey(),
                    validationResult);
            if (validationResult.hasErrors()) {
                this.showErrors(validationResult.getErrors());
                return;
            }
            this.clearErrors();
            ReportTableModel reportTableModel;
            reportTableModel = new ReportTableModel(
                    model.getData(dateField.getValue()));

            reportTable.setModel(reportTableModel);
            reportTable.repaint();

        });

        this.excelButton = new JButton(getGuiString("buttons.generateExcelReport"));
        this.excelButton.addActionListener(e -> {
            ValidationResult validationResult = ValidationResult.createEmpty();
            validator.validate(dateField.getValue(), dateField.getFieldKey(),
                    validationResult);
            if (validationResult.hasErrors()) {
                this.showErrors(validationResult.getErrors());
                return;
            }
            this.clearErrors();
            new Thread(() -> {
                String message = model.generateExcelReport(dateField.getValue());
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
        JScrollPane tableScrollPane = new JScrollPane(this.reportTable);

        getContentPanel().add(tableScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(excelButton);
        buttonPanel.add(cancelButton);
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
