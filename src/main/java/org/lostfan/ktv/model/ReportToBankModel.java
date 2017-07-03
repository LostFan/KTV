package org.lostfan.ktv.model;

import org.lostfan.ktv.dao.*;
import org.lostfan.ktv.domain.RenderedService;
import org.lostfan.ktv.domain.Service;
import org.lostfan.ktv.domain.Street;
import org.lostfan.ktv.domain.Subscriber;
import org.lostfan.ktv.model.dto.TurnoverSheetTableDTO;
import org.lostfan.ktv.model.entity.BaseModel;
import org.lostfan.ktv.utils.BaseObservable;
import org.lostfan.ktv.utils.ResourceBundles;
import org.lostfan.ktv.utils.excel.TurnoverReportExcel;

import javax.swing.text.DateFormatter;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportToBankModel extends BaseObservable implements BaseModel {

    private static String SEPARATOR = "^";

    private static String VERSION_OF_BANK_FILE = "1";

    private static String COMPANY_CODE = "32600072";

    private static String AGENT_CODE = "749";

    private static String PAYER_NUMBER = "700223277";

    private static String NUMBER_OF_MESSAGE = "1";

    private static String CURRENCY_CODE = "933";

    private static String BANK_CODE = "BY81PJCB30123000481000000933";

    private SubscriberDAO subscriberDAO = DAOFactory.getDefaultDAOFactory().getSubscriberDAO();
    private StreetDAO streetDAO = DAOFactory.getDefaultDAOFactory().getStreetDAO();
    private RenderedServiceDAO renderedServiceDAO = DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO();
    private PaymentDAO paymentDAO = DAOFactory.getDefaultDAOFactory().getPaymentDAO();

    public ReportToBankModel() {
    }

    @Override
    public String getEntityNameKey() {
        return "reportToBank";
    }

    @Override
    public List<EntityField> getFields() {
        return new ArrayList<>();
    }

    public String generate(LocalDate date) {

        Properties prop = new Properties();
        InputStream input = null;
        try {
            String filename = "config.properties";
            input = getClass().getClassLoader().getResourceAsStream(filename);

            prop.load(input);

            SEPARATOR = prop.getProperty("bank_report.separator");
            VERSION_OF_BANK_FILE = prop.getProperty("bank_report.version_of_bank_file");
            COMPANY_CODE = prop.getProperty("bank_report.company_code");
            AGENT_CODE = prop.getProperty("bank_report.agent_code");
            PAYER_NUMBER = prop.getProperty("bank_report.payer_number");
            NUMBER_OF_MESSAGE = prop.getProperty("bank_report.number_of_message");
            CURRENCY_CODE = prop.getProperty("bank_report.currency_code");
            BANK_CODE = prop.getProperty("bank_report.bank_code");

        } catch (IOException e) {
            e.printStackTrace();
        }


        List<String> rows = new ArrayList<>();
        Map<Integer, BigDecimal> result = new TreeMap<>();
        Map<Integer, BigDecimal> resultRenderedServices = renderedServiceDAO. getAllRenderedServicesPriceBeforeDate(date);
        Map<Integer, BigDecimal> resultPayments = paymentDAO. getAllPaymentsPriceBeforeDate(date);
        Integer i = 0;
        DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("YYYYMMdd");
        DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MM.YYYY");
        BigDecimal sum = BigDecimal.ZERO;
        for (Map.Entry<Integer, BigDecimal> renderedServiceEntry : resultRenderedServices.entrySet()) {
            BigDecimal value = renderedServiceEntry.getValue();
            BigDecimal paymentValue = resultPayments.get(renderedServiceEntry.getKey());
            if(paymentValue != null) {
                value = value.add(paymentValue.negate());
            }
            result.put(renderedServiceEntry.getKey(), value);
        }
        Integer size = 0;
        for (BigDecimal aDouble : result.values()) {
                sum = sum.add(aDouble.setScale(2, BigDecimal.ROUND_HALF_UP));
                size++;
        }

        StringBuilder firstRow = new StringBuilder();

        firstRow.append(VERSION_OF_BANK_FILE)
                .append(SEPARATOR)
                .append(COMPANY_CODE)
                .append(SEPARATOR)
                .append(NUMBER_OF_MESSAGE)
                .append(SEPARATOR)
                .append(yearMonthDayFormatter.format(LocalDate.now()))
                .append(SEPARATOR)
                .append(size)
                .append(SEPARATOR)
                .append(PAYER_NUMBER)
                .append(SEPARATOR)
                .append(AGENT_CODE)
                .append(SEPARATOR)
                .append(BANK_CODE)
                .append(SEPARATOR)
                .append("1")
                .append(SEPARATOR)
                .append(CURRENCY_CODE)
                .append(SEPARATOR)
                .append(sum);

        rows.add(firstRow.toString());

        for (Map.Entry<Integer, BigDecimal> entry : result.entrySet()) {
            BigDecimal value = entry.getValue()
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            Subscriber subscriber = subscriberDAO.get(entry.getKey());
            Street street = streetDAO.get(subscriber.getStreetId());
            String fullAddress = getFullSubscriberAddress(subscriber, street);

            StringBuilder row = new StringBuilder();
            row.append(++i)
                    .append(SEPARATOR)
                    .append(entry.getKey())
                    .append(SEPARATOR)
                    .append(subscriber.getName())
                    .append(SEPARATOR)
                    .append(fullAddress)
                    .append(SEPARATOR)
                    .append(monthYearFormatter.format(date))
                    .append(SEPARATOR)
                    .append(value)
                    .append(SEPARATOR)
                    .append(SEPARATOR)
                    .append(yearMonthDayFormatter.format(LocalDate.now()))
                    .append("130000")
                    .append(SEPARATOR)
                    .append(SEPARATOR);
            rows.add(row.toString());
        }

        return saveToFile(rows, date);
    }

    private String getFullSubscriberAddress(Subscriber subscriber, Street street) {

        if (subscriber == null || street == null) {
            return "";
        }
        StringBuilder address = new StringBuilder(street.getName())
                .append(",")
                .append(subscriber.getHouse())
                .append(subscriber.getIndex());
        if (!subscriber.getBuilding().isEmpty()) {
            address.append(",")
                    .append(getGuiString("buildingAbbreviated"))
                    .append(subscriber.getBuilding());
        }
        address.append(",")
                .append(getGuiString("flatAbbreviated"))
                .append(subscriber.getFlat());
        return address.toString();
    }

    private static String getGuiString(String key) {
        return ResourceBundles.getGuiBundle().getString(key);
    }

    private String saveToFile(List<String> rows, LocalDate date) {
        String message = null;

        DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("YYYYMM");
        BufferedWriter bw = null;
        try {
            File file = new File("202");
            file.mkdir();
            bw = new BufferedWriter(new FileWriter(file.getPath() + "\\" +
                    yearMonthDayFormatter.format(date) + ".202"));
            for (String row : rows) {
                bw.write(row);
                bw.newLine();
            }
        } catch (IOException ex) {
            if (ex instanceof FileNotFoundException) {

                message = "message.fileIsUsed";
            } else {
                message = "message.fail";
            }
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    if (e instanceof FileNotFoundException) {

                        message = "message.fileIsUsed";
                    } else {
                        message = "message.fail";
                    }
                }
            }
        }
        return message;
    }

}
