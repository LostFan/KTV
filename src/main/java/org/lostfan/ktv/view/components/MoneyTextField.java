package org.lostfan.ktv.view.components;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class MoneyTextField extends TextField {

    private class IntegerDocumentFilter extends DocumentFilter {

        public IntegerDocumentFilter() {
            this.numbersAfterPoint = 2;
        }

        public IntegerDocumentFilter(Integer numbersAfterPoint) {
            this.numbersAfterPoint = numbersAfterPoint;
        }

        private Integer numbersAfterPoint;

        private boolean hasSign;

        private boolean willBeDouble(String insertion, int offset, int lengthToDelete) {
            String text = getText();
            // The string has already a sign
            // Inserting in the beginning
            boolean _hasSign;
            String newText = text.substring(0, offset) + insertion;
            if(text.length() != offset) {
                newText += text.substring(offset + lengthToDelete, text.length());
            }

            if (newText.charAt(0) == '+' || newText.charAt(0) == '-') {
                _hasSign = true;
            } else {
                _hasSign = false;
            }

            if (!newText.matches("^[-\\+]?\\d*(\\d[\\.])?\\d{0," + numbersAfterPoint + "}$")) {
                return false;
            }
            int lengthTextAfterPoint = 0;
            if(newText.contains(".")) {
                int pointPosition = newText.indexOf(".");
                lengthTextAfterPoint = newText.substring(pointPosition).length();
            }
            if (newText.length() - lengthTextAfterPoint > (_hasSign ? 10 : 9)) {
                return false;
            }
            return true;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (willBeDouble(string, offset, 0)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (willBeDouble(text, offset, length)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (offset == 0) {
                hasSign = false;
            }
            super.remove(fb, offset, length);
        }
    }

    public MoneyTextField() {
        super(20);
        ((AbstractDocument) this.getDocument()).setDocumentFilter(new IntegerDocumentFilter());
    }

    public MoneyTextField(Integer initialValue) {
        this();
        setValue(initialValue);

    }

    public MoneyTextField(String name) {
        super(20);
        ((AbstractDocument) this.getDocument()).setDocumentFilter(new IntegerDocumentFilter(getNumbersAfterPoint(name)));
    }

    public static Integer getNumbersAfterPoint(String name) {
        if("material.price".equals(name)) {
            return 4;
        }
        return 2;
    }

    public Integer getValue() {
        if (getText().length() == 0) {
            return null;
        }
        if(getText().equals("-") || getText().equals("+")) {
            return null;
        }
        Double aDouble = Double.parseDouble(getText()) * 100;
        System.out.println(aDouble.intValue());
        return aDouble.intValue();
    }

    public void setValue(Integer value) {
        if (value == null) {
            setText("");
        } else {
            setText(String.valueOf(value));
        }
    }
}
