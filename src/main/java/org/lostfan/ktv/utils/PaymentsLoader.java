package org.lostfan.ktv.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.lostfan.ktv.domain.Payment;
import org.lostfan.ktv.model.PaymentTypes;

public class PaymentsLoader {


    public PaymentsLoader() {
    }

    public List<Payment> load(File file) {
        List<Payment> payments = new ArrayList<>();
        if(file.getName().endsWith(".210")) {
            payments = loadERIP(file);
        }
        if(file.getName().endsWith(".dat")) {
            payments = loadPost(file);
        }

        return payments;
    }

    private List<Payment> loadERIP(File file) {
        List<Payment> payments = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                String str[] = sCurrentLine.split("\\^");
                try {
                    if (!"1".equals(str[1])) {
                        continue;
                    }
                    Payment payment = new Payment();
                    payment.setSubscriberAccount(Integer.parseInt(str[2]));
                    payment.setDate(createDate(str[9]));
                    payment.setBankFileName(file.getName());
//                    payment.setPrice(Integer.parseInt(str[6].split("\\.")[0]));
                    payment.setPrice(new BigDecimal(str[6]));
                    payment.setPaymentTypeId(PaymentTypes.BANK.getId());
                    payments.add(payment);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return payments;
    }

    private List<Payment> loadPost(File file) {
        List<Payment> payments = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                String str[] = sCurrentLine.split(";");
                try {
                    Payment payment = new Payment();
                    payment.setSubscriberAccount(Integer.parseInt(str[0]));
//                    payment.setDate(createDate(str[9]));
                    payment.setBankFileName(file.getName());
                    payment.setPrice(new BigDecimal(str[5]));
                    payment.setPaymentTypeId(PaymentTypes.POST.getId());
                    payments.add(payment);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return payments;
    }

    private LocalDate createDate(String s) {
        return LocalDate.of(Integer.parseInt(s.substring(0, 4)), Integer.parseInt(s.substring(4, 6)), Integer.parseInt(s.substring(6, 8)));
    }

}
