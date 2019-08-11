package org.lostfan.ktv.view;

import org.lostfan.ktv.validation.Error;
import org.lostfan.ktv.view.components.*;
import org.lostfan.ktv.view.components.TextField;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Represents a FrameView class that can contain input fields
 * for entering values, retrieving the result object
 * and showing validation errors.
 */
public class FormView extends FrameView implements Iterable<FormView.FormField> {

    public interface FieldValueListener<T> {

        void valueChanged(T newValue);
    }

    public static abstract class FormField<T> {

        protected JLabel label;
        protected JLabel errorLabel;

        protected String fieldKey;

        private Set<FieldValueListener<T>> valueListeners;

        public FormField(String fieldKey) {
            this.label = new JLabel(getEntityString(fieldKey), SwingConstants.LEFT);
            this.fieldKey = fieldKey;

            this.errorLabel = new JLabel();
            this.errorLabel.setVisible(false);
            this.errorLabel.setForeground(Color.RED);
            this.errorLabel.setFont(new Font(this.errorLabel.getName(), Font.BOLD, this.errorLabel.getFont().getSize() - 1));

            valueListeners = new HashSet<>();
        }

        /**
         * Sets the current error of this field.
         * And shows the error message.
         */
        protected void setError(String errorCode) {
            this.errorLabel.setText(getGuiString(errorCode));
            this.errorLabel.setVisible(true);
        }

        /**
         * Removes an error of this field.
         * Hides the error message.
         */
        protected void clearError() {
            this.errorLabel.setText("");
            this.errorLabel.setVisible(false);
        }

        public void setVisible(boolean isVisible) {
            this.label.setVisible(isVisible);
            getInputComponent().setVisible(isVisible);
            this.errorLabel.setVisible(false);
        }

        public boolean isVisible() {
            return this.label.isVisible();
        }

        /**
         * Returns a Component that represents the source for entering a value
         */
        public abstract JComponent getInputComponent();

        /**
         * Returns the current value of the component
         */
        public abstract T getValue();

        /**
         * Sets the value of the input field
         */
        public abstract void setValue(T value);

        /**
         * Returns an EntityField object that is the basis of this field.
         */
        public String getFieldKey() {
            return this.fieldKey;
        }

        public void addValueListener(FieldValueListener<T> valueListener) {
            this.valueListeners.add(valueListener);
        }

        protected void fireValueChanged(T newValue) {
            this.valueListeners.forEach(l -> l.valueChanged(newValue));
        }
    }

    public static class StringFormField extends FormField<String> {

        private TextField textField;

        public StringFormField(String fieldKey) {
            super(fieldKey);
            this.textField = new TextField();

            this.textField.addTextChangeListener(v -> fireValueChanged(getValue()));
        }

        @Override
        protected void setError(String errorCode) {
            super.setError(errorCode);
            this.textField.setBackground(new Color(250, 234, 234));
        }

        @Override
        protected void clearError() {
            super.clearError();
            this.textField.setBackground(UIManager.getColor("TextField.background"));
        }

        @Override
        public JComponent getInputComponent() {
            return this.textField;
        }

        @Override
        public String getValue() {
            return this.textField.getText();
        }

        @Override
        public void setValue(String value) {
            this.textField.setText(value);
        }
    }

    public static class MultilineStringFormField extends FormField<String> {

        private JTextArea textArea;

        public MultilineStringFormField(String fieldKey) {
            super(fieldKey);
            this.textArea = new JTextArea();
            this.textArea.setBorder(LineBorder.createGrayLineBorder());
        }

        @Override
        protected void setError(String errorCode) {
            super.setError(errorCode);
            this.textArea.setBackground(new Color(250, 234, 234));
        }

        @Override
        protected void clearError() {
            super.clearError();
            this.textArea.setBackground(UIManager.getColor("TextField.background"));
        }

        @Override
        public JComponent getInputComponent() {
            return this.textArea;
        }

        @Override
        public String getValue() {
            return this.textArea.getText();
        }

        @Override
        public void setValue(String value) {
            this.textArea.setText(value);
        }
    }


    public static class IntegerFormField extends FormField<Integer> {

        private IntegerTextField textField;

        public IntegerFormField(String fieldKey) {
            super(fieldKey);
            this.textField = new IntegerTextField();

            this.textField.addTextChangeListener(v -> fireValueChanged(getValue()));
        }

        @Override
        protected void setError(String errorCode) {
            super.setError(errorCode);
            this.textField.setBackground(new Color(250, 234, 234));
        }

        @Override
        protected void clearError() {
            super.clearError();
            this.textField.setBackground(UIManager.getColor("TextField.background"));
        }

        @Override
        public JComponent getInputComponent() {
            return this.textField;
        }

        @Override
        public Integer getValue() {
            return this.textField.getValue();
        }

        @Override
        public void setValue(Integer value) {
            this.textField.setValue(value);
        }
    }

    public static class BigDecimalFormField extends FormField<BigDecimal> {

        private TextField textField;

        public BigDecimalFormField(String fieldKey) {
            super(fieldKey);
            // TODO: Implement FormattedTextField or another way to filter double values only
            this.textField = new MoneyTextField(fieldKey);

            this.textField.addTextChangeListener(v -> fireValueChanged(getValue()));
        }

        @Override
        protected void setError(String errorCode) {
            super.setError(errorCode);
            this.textField.setBackground(new Color(250, 234, 234));
        }

        @Override
        protected void clearError() {
            super.clearError();
            this.textField.setBackground(UIManager.getColor("TextField.background"));
        }

        @Override
        public JComponent getInputComponent() {
            return this.textField;
        }

        @Override
        public BigDecimal getValue() {
            String text = textField.getText();
            try {
                return new BigDecimal(text);
            } catch (NumberFormatException | NullPointerException e) {
                return null;
            }
        }

        @Override
        public void setValue(BigDecimal value) {
            this.textField.setText(value.toString());
        }
    }

    public static class DoubleFormField extends FormField<Double> {

        private TextField textField;

        public DoubleFormField(String fieldKey) {
            super(fieldKey);
            // TODO: Implement FormattedTextField or another way to filter double values only
            this.textField = new MoneyTextField();

            this.textField.addTextChangeListener(v -> fireValueChanged(getValue()));
        }

        @Override
        protected void setError(String errorCode) {
            super.setError(errorCode);
            this.textField.setBackground(new Color(250, 234, 234));
        }

        @Override
        protected void clearError() {
            super.clearError();
            this.textField.setBackground(UIManager.getColor("TextField.background"));
        }

        @Override
        public JComponent getInputComponent() {
            return this.textField;
        }

        @Override
        public Double getValue() {
            String text = textField.getText();
            try {
                return new Double(text);
            } catch (NumberFormatException | NullPointerException e) {
                return null;
            }
        }

        @Override
        public void setValue(Double value) {
            this.textField.setText(value.toString());
        }
    }

    public static class BooleanFormField extends FormField<Boolean> {

        private JCheckBox checkBox;

        public BooleanFormField(String fieldKey) {
            super(fieldKey);
            this.checkBox = new JCheckBox();

            this.checkBox.addActionListener(e -> fireValueChanged(getValue()));
        }

        @Override
        public JComponent getInputComponent() {
            return this.checkBox;
        }

        @Override
        public Boolean getValue() {
            return this.checkBox.isSelected();
        }

        @Override
        public void setValue(Boolean value) {
            this.checkBox.setSelected(value);
        }
    }

    public static class DateFormField extends FormField<LocalDate> {

        private DatePickerField datePicker;

        public DateFormField(String fieldKey) {
            super(fieldKey);
            this.datePicker = new DatePickerField();

            datePicker.addActionListener(e -> fireValueChanged(getValue()));
            datePicker.getModel().addChangeListener(e -> fireValueChanged(getValue()));
        }

        @Override
        protected void setError(String errorCode) {
            super.setError(errorCode);
            this.datePicker.getJFormattedTextField().setBackground(new Color(250, 234, 234));
        }

        @Override
        protected void clearError() {
            super.clearError();
            this.datePicker.getJFormattedTextField().setBackground(UIManager.getColor("TextField.background"));
        }

        @Override
        public JComponent getInputComponent() {
            return this.datePicker;
        }

        @Override
        public LocalDate getValue() {
            return this.datePicker.getValue();
        }

        @Override
        public void setValue(LocalDate value) {
            this.datePicker.setValue(value);
        }
    }

    private JPanel fieldPanel;
    private JPanel globalErrorPanel;
    private JLabel formTitleLabel;
    private Map<String, FormField> fields;

    public FormView() {
        this.fieldPanel = new JPanel(new GridBagLayout());
        getContentPanel().add(this.fieldPanel);

        this.formTitleLabel = new JLabel();
        this.formTitleLabel.setVisible(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1d;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        fieldPanel.add(this.formTitleLabel, c);

        this.globalErrorPanel = new JPanel();
        this.globalErrorPanel.setForeground(Color.RED);
        this.globalErrorPanel.setLayout(new BoxLayout(this.globalErrorPanel, BoxLayout.PAGE_AXIS));
        c.insets = new Insets(0, 10, 0, 10);
        c.gridy = 1;
        this.fieldPanel.add(this.globalErrorPanel, c);

        this.fields = new HashMap<>();
    }

    /**
     * Gets a JPanel that stores all added form fields
     * @return
     */
    protected JPanel getFieldPanel() {
        return fieldPanel;
    }

    protected FormField getFormField(String code) {
        return this.fields.get(code);
    }

    /**
     * Sets the form title that will be displayed just above the input fields
     */
    protected void setFormTitle(String title) {
        this.formTitleLabel.setText(title);
        this.formTitleLabel.setVisible(true);
    }

    /**
     * Adds the specified FormField to the fieldList and onto the fieldPanel
     */
    protected void addFormField(FormField field) {
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = this.fields.size() * 2 + 2;
        c.gridx = 0;
        c.insets = new Insets(15, 10, 10, 10);
        this.fieldPanel.add(field.label, c);

        c.gridx = 1;
        c.insets = new Insets(10, 10, 0, 10);
        JComponent inputComponent = field.getInputComponent();
        // HACK: textFields is excessively narrow when it's smaller than the displayed area.
        inputComponent.setMinimumSize(inputComponent.getPreferredSize());
        this.fieldPanel.add(inputComponent, c);

        c.gridy++;
        c.insets = new Insets(0, 10, 5, 10);
        this.fieldPanel.add(field.errorLabel, c);
        this.fields.put(field.fieldKey, field);
    }

    /**
     * Shows the specified errors on the form.
     * If the ValidationResult object has any errors
     * they will be showed under an appropriate field
     */
    public void showErrors(Iterable<Error> errors) {
        for (FormField field : this.fields.values()) {
            field.clearError();
        }

        this.globalErrorPanel.removeAll();

        for (Error error : errors) {
            if (error.getField() == null) {
                String message = getGuiString(error.getMessage());
                if (error.getParams().length != 0) {
                    message = String.format(message, error.getParams());
                }
                JLabel errorLabel = new JLabel(message);
                errorLabel.setForeground(Color.RED);
                this.globalErrorPanel.add(errorLabel);
                this.revalidate();
            } else {
                FormField field = getFormField(error.getField());
                if (field != null) {
                    field.setError(error.getMessage());
                }
            }
        }
    }

    public void clearErrors() {
        for (FormField field : this.fields.values()) {
            field.clearError();
        }
    }

    @Override
    public Iterator<FormField> iterator() {
        return this.fields.values().iterator();
    }
}
