package org.lostfan.ktv.view.components;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class DatePickerField extends JDatePickerImpl {

    private static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

        private String datePattern = "dd.MM.yyyy";
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }

            return "";
        }
    }

    public DatePickerField() {
        super(new JDatePanelImpl(new UtilDateModel()), new DateLabelFormatter());
    }

    public DatePickerField(LocalDate initialDate) {
        this();
        setValue(initialDate);
    }

    public void setValue(LocalDate value) {
        if (value == null) {
            getModel().setValue(null);
            getModel().setSelected(false);
        } else {
            getModel().setDate(value.getYear(), value.getMonthValue() - 1, value.getDayOfMonth());
            getModel().setSelected(true);
        }
    }

    public LocalDate getValue() {
        if(getModel().getValue() == null) {
            return null;
        }

        return new java.sql.Date(((Date)getModel().getValue()).getTime()).toLocalDate();
    }
}
