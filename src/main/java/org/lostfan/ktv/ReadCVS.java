package org.lostfan.ktv;


import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.*;
import jxl.write.Label;
import jxl.write.Number;
import org.lostfan.ktv.dao.DAOFactory;
import org.lostfan.ktv.dao.impl.postgre.PostGreDaoFactory;
import org.lostfan.ktv.domain.Payment;
import org.lostfan.ktv.domain.RenderedService;
import org.lostfan.ktv.domain.Service;
import org.lostfan.ktv.domain.Street;
import org.lostfan.ktv.domain.Subscriber;
import org.lostfan.ktv.domain.SubscriberSession;
import org.lostfan.ktv.domain.SubscriberTariff;
import org.lostfan.ktv.domain.Tariff;
import org.lostfan.ktv.domain.TariffPrice;
import org.lostfan.ktv.model.FixedServices;
import org.lostfan.ktv.model.PaymentTypes;
import org.lostfan.ktv.utils.ConnectionManager;
import org.lostfan.ktv.utils.DateFormatter;
import org.lostfan.ktv.utils.PostgreConnectionManager;
import org.lostfan.ktv.utils.ResourceBundles;
//
public class ReadCVS {
    public static void main(String[] args) {
        ReadCVS obj = new ReadCVS();
        ConnectionManager.setManager(new PostgreConnectionManager());
        DAOFactory.setDefaultDAOFactory(new PostGreDaoFactory());


        obj.generate();
        ConnectionManager.getManager().close();
//        try {
//            Thread.sleep(15000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
    }


    private List<Payment> loadERIP() {
        String file = "20200620233.202";
        String line = "";
        String cvsSplitBy = ";";
        List<Payment> payments = new ArrayList<>();
        try {
            FileInputStream fis =  new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "Cp1251"));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] str = sCurrentLine.split("\\^");
                try {
//                    if (!"1".equals(str[1])) {
//                        continue;
//                    }
                    System.out.println(str[1]);
//                    Payment payment = new Payment();
//                    payment.setSubscriberAccount(Integer.parseInt(str[2]));
//                    payment.setDate(createDate(str[9]));
////                    payment.setBankFileName(file.getName());
////                    payment.setPrice(Integer.parseInt(str[6].split("\\.")[0]));
//                    payment.setPrice(new BigDecimal(str[6]));
//                    payment.setPaymentTypeId(PaymentTypes.BANK.getId());
//                    payments.add(payment);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return payments;
    }

    public String generate() {

        WritableWorkbook workbook;
        String message = null;
        try {
            Integer row = 0;
            Integer SUBSCRIBER_ID_COLUMN = row++;
            Integer SUBSCRIBER_NAME_COLUMN = row++;
            Integer ADDRESS_COLUMN = row++;
            Integer STATUS_COLUMN = row++;
            Integer CONNECTION_DATE_COLUMN = row++;
            Integer DISCONNECTION_DATE_COLUMN = row++;
            Integer TARIFF_COLUMN = row++;
            Integer TARIFF_DATE_COLUMN = row++;
            Integer PAYMENT_PRICE_DEBIT = row++;
            Integer PAYMENT_PRICE_CREDIT = row++;
            //Creating WorkBook
            String fileName = String.format("%s - %s",
                    "result", DateFormatter.format(LocalDate.now()));
            File resultfile = new File(fileName + ".xls");
            workbook = Workbook.createWorkbook(resultfile);
            //Creating sheet
            WritableSheet sheet = workbook.createSheet("PAGE 1", 0);
            sheet.setColumnView(SUBSCRIBER_ID_COLUMN, 8);
            sheet.setColumnView(SUBSCRIBER_NAME_COLUMN, 20);
            sheet.setColumnView(ADDRESS_COLUMN, 30);
            sheet.setColumnView(STATUS_COLUMN, 20);
            sheet.setColumnView(CONNECTION_DATE_COLUMN, 20);
            sheet.setColumnView(DISCONNECTION_DATE_COLUMN, 20);
            sheet.setColumnView(TARIFF_COLUMN, 20);
            sheet.setColumnView(TARIFF_DATE_COLUMN, 20);
            sheet.setColumnView(PAYMENT_PRICE_DEBIT, 20);
            sheet.setColumnView(PAYMENT_PRICE_CREDIT, 20);
            WritableCellFormat cellFormat = new WritableCellFormat();
            cellFormat.setAlignment(Alignment.CENTRE);
            row = 0;

            //Addding cells
            sheet.addCell(new Label(SUBSCRIBER_ID_COLUMN, row, ResourceBundles.getEntityBundle().getString(
                    "subscriber"), cellFormat));
            sheet.addCell(new Label(SUBSCRIBER_NAME_COLUMN, row, ResourceBundles.getEntityBundle().getString(
                    "subscriber.name"), cellFormat));
            sheet.addCell(new Label(ADDRESS_COLUMN, row, ResourceBundles.getGuiBundle().getString(
                    "address"), cellFormat));
            sheet.addCell(new Label(STATUS_COLUMN, row, "Статус", cellFormat));
            sheet.addCell(new Label(CONNECTION_DATE_COLUMN, row, "Дата подключения", cellFormat));
            sheet.addCell(new Label(DISCONNECTION_DATE_COLUMN, row, "Дата отключения", cellFormat));
            sheet.addCell(new Label(TARIFF_COLUMN, row, "Пакет", cellFormat));
            sheet.addCell(new Label(TARIFF_DATE_COLUMN, row, "Дата установки пакета", cellFormat));

            sheet.addCell(new Label(PAYMENT_PRICE_DEBIT, row, ResourceBundles.getGuiBundle().getString(
                    "debit"), cellFormat));
            sheet.addCell(new Label(PAYMENT_PRICE_CREDIT, row, ResourceBundles.getGuiBundle().getString(
                    "credit"), cellFormat));
            row++;

            String file = "202008.202";
            String line = "";
            String cvsSplitBy = ";";
            List<Payment> payments = new ArrayList<>();
            try {
                FileInputStream fis =  new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
                String sCurrentLine;
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                while ((sCurrentLine = br.readLine()) != null) {
                    String[] str = sCurrentLine.split("\\^");
                    try {
//                    if (!"1".equals(str[1])) {
//                        continue;
//                    }
                        sheet.addCell(new jxl.write.Number(SUBSCRIBER_ID_COLUMN, row, Integer.parseInt(str[1])));
                        SubscriberSession notClosedSubscriberSession = DAOFactory.getDefaultDAOFactory().getSubscriberDAO().getNotClosedSubscriberSession(Integer.parseInt(str[1]), LocalDate.now());
                        SubscriberTariff tariffByDate = DAOFactory.getDefaultDAOFactory().getSubscriberDAO().getSubscriberTariffAtDate(Integer.parseInt(str[1]), LocalDate.now());
                        if (notClosedSubscriberSession != null) {
                            sheet.addCell(new Label(STATUS_COLUMN, row, "Подключен"));
                            sheet.addCell(new Label(CONNECTION_DATE_COLUMN, row,  dateTimeFormatter.format(notClosedSubscriberSession.getConnectionDate())));

                        } else {
                            sheet.addCell(new Label(STATUS_COLUMN, row, "Отключен"));
                            SubscriberSession closedSubscriberSession = DAOFactory.getDefaultDAOFactory().getSubscriberDAO().getClosedSubscriberSession(Integer.parseInt(str[1]), LocalDate.now());
                            sheet.addCell(new Label(CONNECTION_DATE_COLUMN, row,  dateTimeFormatter.format(closedSubscriberSession.getConnectionDate())));
                            sheet.addCell(new Label(DISCONNECTION_DATE_COLUMN, row,  dateTimeFormatter.format(closedSubscriberSession.getDisconnectionDate())));
                        }
                        if (tariffByDate != null) {
                            sheet.addCell(new Label(TARIFF_COLUMN, row, DAOFactory.getDefaultDAOFactory().getTariffDAO().get(tariffByDate.getTariffId()).getName()));
                            sheet.addCell(new Label(TARIFF_DATE_COLUMN, row,  dateTimeFormatter.format(tariffByDate.getConnectTariff())));
                        }
                        sheet.addCell(new Label(SUBSCRIBER_NAME_COLUMN, row, str[2]));
                        sheet.addCell(new Label(ADDRESS_COLUMN, row, str[3]));
                        double v = Double.parseDouble(str[5]);
                        if (v>0) {
                            sheet.addCell(new Number(PAYMENT_PRICE_DEBIT, row, v));
                            sheet.addCell(new Number(PAYMENT_PRICE_CREDIT, row, 0));
                        } else if (v<0) {
                            sheet.addCell(new Number(PAYMENT_PRICE_DEBIT, row, 0));
                            sheet.addCell(new Number(PAYMENT_PRICE_CREDIT, row, v*-1));
                        } else {
                            sheet.addCell(new Number(PAYMENT_PRICE_DEBIT, row, 0));
                            sheet.addCell(new Number(PAYMENT_PRICE_CREDIT, row, 0));
                        }
                        row++;
                        System.out.println(str[1]);
//                    Payment payment = new Payment();
//                    payment.setSubscriberAccount(Integer.parseInt(str[2]));
//                    payment.setDate(createDate(str[9]));
////                    payment.setBankFileName(file.getName());
////                    payment.setPrice(Integer.parseInt(str[6].split("\\.")[0]));
//                    payment.setPrice(new BigDecimal(str[6]));
//                    payment.setPaymentTypeId(PaymentTypes.BANK.getId());
//                    payments.add(payment);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }


            workbook.write();
            workbook.close();
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }
            try {
                desktop.open(resultfile);
            } catch (IOException | NullPointerException ioe) {
                message = "message.fail";
            }
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {

                message = "message.fileIsUsed";
            } else {
                message = "message.fail";
            }
        } catch (WriteException e) {
            message = "message.fail";
        }
        return message;
    }

    private LocalDate createDate(String s) {
        return LocalDate.of(Integer.parseInt(s.substring(0, 4)), Integer.parseInt(s.substring(4, 6)), Integer.parseInt(s.substring(6, 8)));
    }
//
//    // steps
//    //create services
//    //load subscribers
//    //load subs fees
//    //load connections - rend services
//    //load addit services
//    //load payments
//    //create connections
//    //create sessions  and subs tariffs
//
//
    public void run() {
        //        String csvFile = "BASES/" + fileName;
//        BufferedReader br = null;
//        String line = "";
//        String cvsSplitBy = ";";
//
//        Map<Integer, Integer> map = new HashMap<>();
//        try {
//            FileInputStream fis =  new FileInputStream(csvFile);
//            br = new BufferedReader(new InputStreamReader(fis, "Cp1251"));
//            int size = -1;
//            while ((line = br.readLine()) != null) {
//                String[] row = line.split(cvsSplitBy);
//                map.put(parseInt(row[1]), parseInt(row[4]));
//            }
    }
//    }
////        getPayments();
////        getSubscribersIdWithoutConnection();
//
//        loadTariffs();
//        createServices();
//        loadStreets();
//        loadSubscribers();
//        loadSubscriptionFees();
//        loadConnections();
//        loadAdditionalServices();
//        loadPayments();
//        updateTariffsInSubscriberTariffs();
//
//
//
////        createFirstSessionAndSubscriberTariff();
//
//
////        for (Payment payment :  DAOFactory.getDefaultDAOFactory().getPaymentDAO().getByDate(LocalDate.of(2003,03,30))) {
////            DAOFactory.getDefaultDAOFactory().getPaymentDAO().delete(payment.getId());
////
////        }
//
//
////        compareSubFees();
////        compareCon();
//
//
//
//
//
//
////        loadTariffPrices();
////        loadServices();
////        loadServicePrices();
////        loadDisconnectionReasons();
////        loadSubscribers();
//    }
//
//    private void compareSubFees() {
//        Map<Integer, Integer> mapFile = getABFile();
//        Map<Integer, Integer> mapBase = getBase("ABON.txt");
//        Map<Integer, Integer> mapBase2 = getBase("2.txt");
//        int size = 0;
//        int sum = 0;
//        System.out.println(mapBase.size());
//
//        for (Integer integer : mapBase.keySet()) {
//            if( mapFile.get(integer).intValue() !=  mapBase.get(integer).intValue()) {
//                System.out.println(integer + "\t" + mapFile.get(integer) + "\t" + mapBase.get(integer) + "\t" + (mapBase.get(integer) - mapFile.get(integer)));
//                size++;
//                sum += mapBase.get(integer).intValue() - mapFile.get(integer).intValue();
//            }
//        }
//        for (Integer integer : mapFile.keySet()) {
//            if(mapBase.get(integer) == null && mapFile.get(integer)!=0) {
//                System.out.println(integer + "\t" + mapFile.get(integer) + "\t" + 0);
//                sum+= -mapFile.get(integer);
//                size++;
//            }
//
//        }
//        System.out.println(sum);
//        System.out.println(mapFile.size());
//
//        for (Integer integer : mapBase2.keySet()) {
//            if(mapBase.get(integer) == null) {
//                System.out.println(integer + "\t" + mapBase2.get(integer) + "\t");
//            }
//        }
//    }
//
//    private void compareCon() {
//        Map<Integer, Integer> mapFile = getUSFile();
//        Map<Integer, Integer> mapBase = getBase("conn.txt");
//        for (Integer integer : mapBase.keySet()) {
//            if(mapBase.get(integer).intValue()< 0) {
//
//                if(integer <=5632) {
//                    Payment payment = new Payment();
//                    payment.setSubscriberAccount(integer);
//                    payment.setPrice(mapBase.get(integer).doubleValue() * -1);
//                    RenderedService renderedService = DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO()
//                            .getBySubscriber(integer)
//                            .stream().filter(renderedServiceIn -> renderedServiceIn.getServiceId() == 2)
//                            .findFirst().get();
//                    payment.setDate(renderedService.getDate());
//                    payment.setServicePaymentId(renderedService.getServiceId());
//                    payment.setRenderedServicePaymentId(renderedService.getId());
//                }
//                System.out.println(integer + "\t" + mapBase.get(integer));
//            }
//
//        }
////        int size = 0;
////        int sum = 0;
////        System.out.println(mapFile.size());
////
////        for (Integer integer : mapFile.keySet()) {
////            if(!mapBase.containsKey(integer)) {
////                System.out.println(integer);
////                continue;
////            }
////            if( mapFile.get(integer).intValue() !=  mapBase.get(integer).intValue()) {
////                System.out.println(integer + "\t" + mapFile.get(integer) + "\t" + mapBase.get(integer) + "\t" + (mapBase.get(integer) - mapFile.get(integer)));
////                size++;
////                sum += mapBase.get(integer).intValue() - mapFile.get(integer).intValue();
////            }
////        }
////        for (Integer integer : mapFile.keySet()) {
////            if(mapBase.get(integer) == null && mapFile.get(integer)!=0) {
////                System.out.println(integer + "\t" + mapFile.get(integer) + "\t" + 0);
////                sum+= -mapFile.get(integer);
////                size++;
////            }
////
////        }
////        System.out.println(sum);
////        System.out.println(mapFile.size());
//
//    }
//
//    public Map<Integer, Integer> getABFile() {
//        String csvFile = "BASES/OB_AB_11.TXT";
//        BufferedReader br = null;
//        String line = "";
//        String cvsSplitBy = "\t";
//
//        Map<Integer, Integer> map = new HashMap<>();
//        try {
//            FileInputStream fis =  new FileInputStream(csvFile);
//            br = new BufferedReader(new InputStreamReader(fis, "Cp1251"));
//            int size = -1;
//            int sum1 = 0;
//            int sum2 = 0;
//            while ((line = br.readLine()) != null) {
//                String[] row = line.split(cvsSplitBy);
//                if(row[0].length() >0 && row[0].substring(0,1).equals("0")) {
////                    System.out.println(parseInt(line.substring(0, 7).trim()));
////                    System.out.println(parseInt(line.substring(68, 75).trim()));
////                    System.out.println(parseInt(line.substring(75, 82).trim()));
//                    sum1+=parseInt(line.substring(75, 82).trim());
//                    sum2+=parseInt(line.substring(68, 75).trim());
//                    map.put(parseInt(line.substring(0, 7).trim()),parseInt(line.substring(75, 82).trim()) - parseInt(line.substring(68, 75).trim()));
//                }
//            }
//            System.out.println(sum2 + "   " + sum1 + "   " + (sum2 - sum1));
////            System.out.println(size + " streets have been loaded");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("No subscribers loaded.");
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return map;
//    }
//
//    public Map<Integer, Integer> getUSFile() {
//        String csvFile = "BASES/OB_US_11.TXT";
//        BufferedReader br = null;
//        String line = "";
//        String cvsSplitBy = "\t";
//
//        Map<Integer, Integer> map = new HashMap<>();
//        try {
//            FileInputStream fis =  new FileInputStream(csvFile);
//            br = new BufferedReader(new InputStreamReader(fis, "Cp1251"));
//            int size = -1;
//            int sum1 = 0;
//            int sum2 = 0;
//            while ((line = br.readLine()) != null) {
//                String[] row = line.split(cvsSplitBy);
//                if(row[0].length() >0 && row[0].substring(0,1).equals("0")) {
////                    System.out.println(parseInt(line.substring(0, 7).trim()));
////                    System.out.println(parseInt(line.substring(68, 75).trim()));
////                    System.out.println(parseInt(line.substring(75, 82).trim()));
//                    sum1+=parseInt(line.substring(75, 82).trim());
//                    sum2+=parseInt(line.substring(68, 75).trim());
//                    map.put(parseInt(line.substring(0, 7).trim()),parseInt(line.substring(75, 82).trim()) - parseInt(line.substring(68, 75).trim()));
//                }
//            }
//            System.out.println(sum2 + "   " + sum1 + "   " + (sum2 - sum1));
////            System.out.println(size + " streets have been loaded");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("No subscribers loaded.");
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return map;
//    }
//
//    public Map<Integer, Integer> getBase(String fileName) {
//        String csvFile = "BASES/" + fileName;
//        BufferedReader br = null;
//        String line = "";
//        String cvsSplitBy = ";";
//
//        Map<Integer, Integer> map = new HashMap<>();
//        try {
//            FileInputStream fis =  new FileInputStream(csvFile);
//            br = new BufferedReader(new InputStreamReader(fis, "Cp1251"));
//            int size = -1;
//            while ((line = br.readLine()) != null) {
//                String[] row = line.split(cvsSplitBy);
//                map.put(parseInt(row[1]), parseInt(row[4]));
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("No subscribers loaded.");
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return map;
//    }
//
//    public List<Integer> getSubscribersIdWithoutConnection() {
//            List<Integer> ids = new ArrayList<>();
//            try {
//                PreparedStatement preparedStatement = ConnectionManager.getManager().getConnection().prepareStatement("SELECT \"subscriber\".\"account\" FROM \"subscriber\"\n" +
//                        "        LEFT JOIN (SELECT * from \"rendered_service\" WHERE \"service_id\" = 2) ON \"subscriber_account\" = \"subscriber\".\"account\"\n" +
//                        "        where \"subscriber_account\" IS NULL");
//                ResultSet rs = preparedStatement.executeQuery();
//                while (rs.next()) {
//                    ids.add(rs.getInt("account"));
//                }
//
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        for (Integer id : ids) {
//            System.out.println(id);
//        }
//            return ids;
//
//    }
//    public void getPayments() {
//        List<RenderedService> renderedServiceList = DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO().getByService(2);
////        renderedServiceList.stream()
////        SELECT * FROM "subscriber"
////        inner join "service" on ("account"="id" +10)
//
////        SELECT * FROM "rendered_service"
////        right join "subscriber" on ("subscriber_account"="account")
////        where "service_id" = 2
//
////        SELECT "account" FROM "rendered_service"
////        right OUTER  join "subscriber" on ("subscriber_account"="account")
////        where "service_id" = 2   GROUP BY "account" HAVING COUNT("account")>1
//
////        SELECT "subscriber"."account" FROM "subscriber"
////        LEFT JOIN (SELECT * from "rendered_service" WHERE "service_id" = 2) ON "subscriber_account" = "subscriber"."account"
////        where "subscriber_account" IS NULL
//
////        SELECT "service_id", "subscriber_account",SUM("price") as "price" FROM "rendered_service"  group by "service_id", "subscriber_account"
//
////       SELECT "payment"."service_id", "payment"."subscriber_account", SUM("payment"."price") as "payment_price"
////        FROM "payment"  group by  "payment"."service_id", "payment". "subscriber_account" LEFT JOIN (
////                SELECT "rendered_service". "service_id", "rendered_service"."subscriber_account",SUM("rendered_service"."price") as "price"
////        FROM "rendered_service"  group by "rendered_service"."service_id", "rendered_service". "subscriber_account")
////        ON "payment"."subscriber_account" = "rendered_service"."subscriber_account" AND "payment"."service_id"="rendered_service"."service_id"
//
////        SELECT "payment"."service_id",  "payment"."subscriber_account"  , "payment"."payment_price", "rendered_service"."rendered_service_price" ,
////                "payment"."payment_price" -  "rendered_service"."rendered_service_price"  as "Balance"
////        from(SELECT "payment"."service_id", "payment"."subscriber_account", SUM("payment"."price") as "payment_price"
////                FROM "payment"         group by  "payment"."service_id", "payment". "subscriber_account") as "payment" INNER JOIN (
////                SELECT "rendered_service". "service_id", "rendered_service"."subscriber_account",SUM("rendered_service"."price") as "rendered_service_price"
////        FROM "rendered_service"  group by "rendered_service"."service_id", "rendered_service". "subscriber_account") as  "rendered_service"
////        ON "payment"."subscriber_account" = "rendered_service"."subscriber_account" AND "payment"."service_id"="rendered_service"."service_id"
//////        order by  "payment"."service_id"
//
////        SELECT "payment"."service_id", "rendered_service"."subscriber_account",  "payment"."subscriber_account"  ,
////                (CASE WHEN "payment"."payment_price" is NULL THEN 0 ELSE "payment"."payment_price" END) AS "payment_price",
////                "rendered_service"."rendered_service_price" ,
////                (CASE WHEN "payment"."payment_price" is NULL THEN - "rendered_service"."rendered_service_price" ELSE "payment"."payment_price" - "rendered_service"."rendered_service_price" END) AS "Balance"
////        from(SELECT "payment"."service_id", "payment"."subscriber_account", SUM("payment"."price") as "payment_price"
////                FROM "payment" where  "payment"."date"<'2015-06-01'  AND "payment"."service_id" = 1      group by  "payment"."service_id", "payment". "subscriber_account") as "payment" RIGHT JOIN (
////                SELECT "rendered_service". "service_id", "rendered_service"."subscriber_account",SUM("rendered_service"."price") as "rendered_service_price"
////        FROM "rendered_service" where  "rendered_service"."date"<'2015-06-01'  group by "rendered_service"."service_id", "rendered_service". "subscriber_account") as  "rendered_service"
////        ON ("payment"."subscriber_account" = "rendered_service"."subscriber_account") where "rendered_service"."service_id" = 1
//
////
////        SELECT * FROM(SELECT "payment"."service_id",  "payment"."subscriber_account"  , "payment"."payment_price", "rendered_service"."rendered_service_price" ,
////                "payment"."payment_price" -  "rendered_service"."rendered_service_price"  as "Balance"
////                from(SELECT "payment"."service_id", "payment"."subscriber_account", SUM("payment"."price") as "payment_price"
////                        FROM "payment" where  "payment"."date"<'2015-05-01'        group by  "payment"."service_id", "payment". "subscriber_account") as "payment" INNER JOIN (
////                        SELECT "rendered_service". "service_id", "rendered_service"."subscriber_account",SUM("rendered_service"."price") as "rendered_service_price"
////                        FROM "rendered_service" where  "rendered_service"."date"<'2015-05-01'  group by "rendered_service"."service_id", "rendered_service". "subscriber_account") as  "rendered_service"
////                ON "payment"."subscriber_account" = "rendered_service"."subscriber_account" AND "payment"."service_id"="rendered_service"."service_id" where "payment"."service_id" = 1) as "table" INNER JOIN (
////                SELECT * from "subscriber") as "subscriber" ON "subscriber"."account"= "table"."subscriber_account" order by "subscriber"."street_id","subscriber"."house"
//
////        SELECT "payment"."service_id", "rendered_service"."subscriber_account",  "payment"."subscriber_account"  ,
////                (CASE WHEN "payment"."payment_price" is NULL THEN 0 ELSE "payment"."payment_price" END) AS "payment_price",
////                (CASE WHEN "rendered_service"."rendered_service_price" is NULL THEN 0 ELSE "rendered_service"."rendered_service_price" END) AS "rendered_service_price",
////                (CASE WHEN "payment"."payment_price" is NULL THEN 0 ELSE "payment"."payment_price" END) -
////                (CASE WHEN "rendered_service"."rendered_service_price" is NULL THEN 0 ELSE "rendered_service"."rendered_service_price" END)AS "Balance"
////        from(SELECT "payment"."service_id", "payment"."subscriber_account", SUM("payment"."price") as "payment_price"
////                FROM "payment" where  "payment"."date"<'2015-11-01'  AND "payment"."service_id" = 1      group by  "payment"."service_id", "payment". "subscriber_account") as "payment" FULL JOIN (
////                SELECT "rendered_service". "service_id", "rendered_service"."subscriber_account",SUM("rendered_service"."price") as "rendered_service_price"
////        FROM "rendered_service" where  "rendered_service"."date"<'2015-11-01' AND "rendered_service"."service_id" = 1   group by "rendered_service"."service_id", "rendered_service". "subscriber_account") as  "rendered_service"
////        ON ("payment"."subscriber_account" = "rendered_service"."subscriber_account")
//
//
//
//    }
//    public void createServices() {
//        Service service = new Service();
//        service.setName(getString("subscriptionFee"));
////        service.setId(FixedServices.SUBSCRIPTION_FEE.getId());
//        service.setAdditionalService(false);
//        DAOFactory.getDefaultDAOFactory().getServiceDAO().save(service);
//        service = new Service();
//        service.setName(getString("connection"));
////        service.setId(FixedServices.CONNECTION.getId());
//        service.setAdditionalService(false);
//        DAOFactory.getDefaultDAOFactory().getServiceDAO().save(service);
//
//        service = new Service();
//        service.setName(getString("reconnection"));
//        //service.setId(3);
//        service.setAdditionalService(true);
//        DAOFactory.getDefaultDAOFactory().getServiceDAO().save(service);
//        service = new Service();
//        service.setName(getString("disconnection"));
//        //service.setId(4);
//        service.setAdditionalService(true);
//        DAOFactory.getDefaultDAOFactory().getServiceDAO().save(service);
//        service = new Service();
//        service.setName(getString("changeOfTariff"));
//        //service.setId(5);
//        service.setAdditionalService(true);
//        DAOFactory.getDefaultDAOFactory().getServiceDAO().save(service);
//        service = new Service();
//        service.setName(getString("settingUpTV"));
//        service.setAdditionalService(true);
//        //service.setId(6);
//        DAOFactory.getDefaultDAOFactory().getServiceDAO().save(service);
//        service = new Service();
//        service.setName(getString("materialsService"));
//        service.setAdditionalService(true);
//        //service.setId(7);
//        DAOFactory.getDefaultDAOFactory().getServiceDAO().save(service);
//        service = new Service();
//        service.setName(getString("workOnTheReplacementOfMaterials"));
//        service.setAdditionalService(true);
//        //service.setId(8);
//        DAOFactory.getDefaultDAOFactory().getServiceDAO().save(service);
//        service = new Service();
//        service.setName(getString("connectingExtTV"));
//        service.setAdditionalService(true);
//        //service.setId(9);
//        DAOFactory.getDefaultDAOFactory().getServiceDAO().save(service);
//        service = new Service();
//        service.setName(getString("workOnConnections"));
//        //service.setId(10);
//        service.setAdditionalService(true);
//        DAOFactory.getDefaultDAOFactory().getServiceDAO().save(service);
//    }
//    public void loadStreets() {
//        String csvFile = "BASES/SP_OB.csv";
//        BufferedReader br = null;
//        String line = "";
//        String cvsSplitBy = ",";
//
//
//        try {
//            FileInputStream fis =  new FileInputStream(csvFile);
//            br = new BufferedReader(new InputStreamReader(fis, "Cp1251"));
//            int size = -1;
//            while ((line = br.readLine()) != null) {
//                if (size == -1) {
//                    size++;
//                    continue;
//                }
//                Street street = new Street();
//                String[] row = line.split(cvsSplitBy);
//                if(parseInt(row[0]) != 5) {
//                    continue;
//                }
//                street.setId(parseInt(row[1]));
//                street.setName(row[2]);
//                DAOFactory.getDefaultDAOFactory().getStreetDAO().save(street);
//                size++;
//            }
//            System.out.println(size + " streets have been loaded");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("No subscribers loaded.");
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public void loadTariffs() {
//        String csvFile = "BASES/SP_OB.csv";
//        BufferedReader br = null;
//        String line = "";
//        String cvsSplitBy = ",";
//        Tariff tariffZero = new Tariff();
//        tariffZero.setId(0);
//        tariffZero.setName("!!!");
//        tariffZero.setChannels("0");
//        DAOFactory.getDefaultDAOFactory().getTariffDAO().save(tariffZero);
//        TariffPrice tariffPriceZero = new TariffPrice();
//        tariffPriceZero.setTariffId(0);
//        tariffPriceZero.setDate(LocalDate.of(2016,1,1));
//        tariffPriceZero.setPrice(0D);
//        DAOFactory.getDefaultDAOFactory().getTariffDAO().saveTariffPrice(tariffPriceZero);
//
//        try {
//            FileInputStream fis =  new FileInputStream(csvFile);
//            br = new BufferedReader(new InputStreamReader(fis, "Cp1251"));
//            int size = -1;
//            while ((line = br.readLine()) != null) {
//                if (size == -1) {
//                    size++;
//                    continue;
//                }
//
//                String[] row = line.split(cvsSplitBy);
//                if(parseInt(row[0]) != 1) {
//                    continue;
//                }
//                Tariff tariff = new Tariff();
//                tariff.setId(parseInt(row[1]));
//                tariff.setName(row[2]);
//                tariff.setChannels(row[1]);
//                DAOFactory.getDefaultDAOFactory().getTariffDAO().save(tariff);
//                TariffPrice tariffPrice = new TariffPrice();
//                tariffPrice.setTariffId(parseInt(row[1]));
//                tariffPrice.setDate(LocalDate.of(2016, 1, 1));
//                tariffPrice.setPrice(parseDouble(row[3]));
//                DAOFactory.getDefaultDAOFactory().getTariffDAO().saveTariffPrice(tariffPrice);
//                size++;
//            }
//            System.out.println(size + " tariffs have been loaded");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("No tariffs loaded.");
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//
//
//    public void loadSubscribers() {
//        String csvFile = "BASES/ABONENT.CSV";
//        BufferedReader br = null;
//        String line = "";
//        String cvsSplitBy = ",";
//
//
//        try {
//            FileInputStream fis =  new FileInputStream(csvFile);
//            br = new BufferedReader(new InputStreamReader(fis, "Cp1251"));
//            int size = -1;
//            while ((line = br.readLine()) != null) {
//                if (size == -1) {
//                    size++;
//                    continue;
//                }
//                Subscriber subscriber = new Subscriber();
//                String[] row = line.split(cvsSplitBy);
//
//                subscriber.setId(parseInt(row[1]));
//                subscriber.setName(row[2] + " " + row[3] + " "  + row[4]);
//                subscriber.setStreetId(parseInt(row[5]));
//                subscriber.setBalance(0);
//                subscriber.setHouse(parseInt(row[6]));
//                subscriber.setIndex(row[7]);
//                subscriber.setBuilding(row[8]);
//                subscriber.setFlat(row[9]);
//                subscriber.setPhone(row[10]);
//                try {
//                    String[] contractDate = row[11].split("-");
//                    subscriber.setContractDate(LocalDate.of(Integer.parseInt(contractDate[2]) + 2000, Integer.parseInt(contractDate[1]), Integer.parseInt(contractDate[0])));
//                } catch (Exception e) {
//                    System.out.println("Exception:");
//                    System.out.println(row[1]);
//                    System.out.println(row[11]);
//                }
//                DAOFactory.getDefaultDAOFactory().getSubscriberDAO().save(subscriber);
//                size++;
//            }
//            System.out.println(size + " subscribers have been loaded");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("No subscribers loaded.");
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//
//
//    public void loadSubscriptionFees() {
//        String csvFile = "BASES/history.CSV";
//        BufferedReader br = null;
//        String line = "";
//        String cvsSplitBy = ",";
//
////        Set<Integer> set = new HashSet<>();
//        try {
//            FileInputStream fis =  new FileInputStream(csvFile);
//            br = new BufferedReader(new InputStreamReader(fis, "Cp1251"));
//            int size = -1;
//            while ((line = br.readLine()) != null) {
//                if (size == -1) {
//                    size++;
//                    continue;
//                }
//                String[] row = line.split(cvsSplitBy);
//                if(row[0].equals("0") || row[2].equals("0") || row[5].equals("0"))
//                    continue;
//
//                RenderedService renderedService = new RenderedService();
//                renderedService.setServiceId(FixedServices.SUBSCRIPTION_FEE.getId());
//                renderedService.setDate(LocalDate.of(parseInt(row[2]), parseInt(row[3]), 1));
//                renderedService.setSubscriberAccount(parseInt(row[1]));
//
//
//                renderedService.setPrice(parseDouble(row[5]));
//                if(parseInt(row[2]) == 2004 && parseInt(row[3]) ==  3 && !row[4].equals("") && !row[4].equals(" ")) {
//                    renderedService.setPrice(parseDouble(row[5]) + parseDouble(row[4]));
//                }
////                if(!set.contains(parseInt(row[1]))) {
////                    renderedService.setPrice(parseInt(row[5]) + parseInt(row[4]));
////                }
////                set.add(parseInt(row[1]));
//                DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO().save(renderedService);
//                size++;
//            }
//            System.out.println(size + " subscriptionFees have been loaded");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("No subscriptionFees loaded.");
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public void loadConnections() {
//        String csvFile = "BASES/ABONENT.CSV";
//        BufferedReader br = null;
//        String line = "";
//        String cvsSplitBy = ",";
//
//
//        try {
//            FileInputStream fis =  new FileInputStream(csvFile);
//            br = new BufferedReader(new InputStreamReader(fis, "Cp1251"));
//            int size = -1;
//            int disconnectionSize = 0;
//            List<Integer> integers = new ArrayList<>();
//            while ((line = br.readLine()) != null) {
//                if (size == -1) {
//                    size++;
//                    continue;
//                }
//                String[] row = line.split(cvsSplitBy);
//
//                RenderedService renderedService = new RenderedService();
//                renderedService.setServiceId(FixedServices.CONNECTION.getId());
//                if(row[13].equals("")) {
//                    renderedService.setDate(parseDateHyphen(row[11]));
//                } else {
//                    renderedService.setDate(parseDateHyphen(row[13]));
//                }
//                renderedService.setSubscriberAccount(parseInt(row[1]));
//                renderedService.setPrice(parseDouble(row[12]));
//
////                if(!integers.contains(renderedService.getSubscriberAccount())) {
//                    DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO().save(renderedService);
////                }
////                integers.add(renderedService.getSubscriberAccount());
//
//                SubscriberSession subscriberSession = new SubscriberSession();
//                subscriberSession.setSubscriberAccount(renderedService.getSubscriberAccount());
//                subscriberSession.setConnectionDate(renderedService.getDate());
//                if(parseInt(row[18]) == 0) {
//                    if(row.length == 20) {
//                        subscriberSession.setDisconnectionDate(parseDateHyphen(row[19]));
//                    } else {
//                        subscriberSession.setDisconnectionDate(parseDateHyphen(row[17]).plusDays(1));
//                    }
//                }
//                DAOFactory.getDefaultDAOFactory().getSubscriberDAO().saveSubscriberSession(subscriberSession);
//
//                SubscriberTariff subscriberTariff = new SubscriberTariff();
//                subscriberTariff.setSubscriberAccount(renderedService.getSubscriberAccount());
//                subscriberTariff.setConnectTariff(renderedService.getDate());
//                subscriberTariff.setTariffId(parseInt(row[18]));
////                if(parseInt(row[18]) == 0) {
////                    if(row.length == 20) {
////                        subscriberTariff.setDisconnectTariff(parseDateHyphen(row[19]));
////                    } else {
////                        subscriberTariff.setDisconnectTariff(parseDateHyphen(row[17]).plusDays(1));
////                    }
////                }
//
//                DAOFactory.getDefaultDAOFactory().getSubscriberDAO().saveSubscriberTariff(subscriberTariff);
//
//                if(parseInt(row[18]) == 0) {
//                    RenderedService disconnection = new RenderedService();
//                    disconnection.setServiceId(FixedServices.DISCONNECTION.getId());
//                    if(row.length == 20) {
//                        disconnection.setDate(parseDateHyphen(row[19]));
//                    } else {
//                        disconnection.setDate(parseDateHyphen(row[17]));
//                    }
//                    disconnection.setSubscriberAccount(parseInt(row[1]));
//                    disconnection.setPrice(0D);
//                    DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO().save(disconnection);
//                    disconnectionSize++;
//                }
//                size++;
//            }
//            System.out.println(size + " connections have been loaded");
//            System.out.println(disconnectionSize + " disconnections have been loaded");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("No connections loaded.");
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public void loadAdditionalServices() {
//        String csvFile = "BASES/his_d.CSV";
//        BufferedReader br = null;
//        String line = "";
//        String cvsSplitBy = ",";
//
//
//        try {
//            FileInputStream fis =  new FileInputStream(csvFile);
//            br = new BufferedReader(new InputStreamReader(fis, "Cp1251"));
//            int size = -1;
//            while ((line = br.readLine()) != null) {
//                if (size == -1) {
//                    size++;
//                    continue;
//                }
//                String[] row = line.split(cvsSplitBy);
//                if(row.length < 5 || row[4].equals(""))
//                    continue;
//                RenderedService renderedService = new RenderedService();
//
//                switch (row[1]) {
//                    case "001" :  renderedService.setServiceId(3); break;
//                    case "002" :  renderedService.setServiceId(9); break;
//                    case "003" :  renderedService.setServiceId(6); break;
//                    case "004" :  renderedService.setServiceId(5); break;
//                    case "005" :  renderedService.setServiceId(7); break;
//                    case "006" :  renderedService.setServiceId(8); break;
//                    case "007" :  renderedService.setServiceId(10); break;
//                }
//                renderedService.setId(parseInt(row[2]) + 1000000);
//                renderedService.setDate(parseDateHyphen(row[4]));
//                renderedService.setSubscriberAccount(parseInt(row[0]));
//                renderedService.setPrice(parseDouble(row[5]));
//
//                DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO().save(renderedService);
//                size++;
//            }
//            System.out.println(size + " additional services have been loaded");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("No additional services loaded.");
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public void loadPayments() {
//        String csvFile = "BASES/kvit.CSV";
//        BufferedReader br = null;
//        String line = "";
//        String cvsSplitBy = ",";
//
//
//        try {
//            FileInputStream fis =  new FileInputStream(csvFile);
//            br = new BufferedReader(new InputStreamReader(fis, "Cp1251"));
//            int size = -1;
//            while ((line = br.readLine()) != null) {
//                if (size == -1) {
//                    size++;
//                    continue;
//                }
//                String[] row = line.split(cvsSplitBy);
//                Payment payment = new Payment();
//                if(row[4] == "" || row[4] == "0" || row[4] == " " ) {
//                    continue;
//                }
//                payment.setSubscriberAccount(parseInt(row[0]));
//                payment.setPrice(parseDouble(row[4]));
//                payment.setDate(parseDateHyphen(row[1]));
//                if(parseInt(row[5]) == 0) {
//                    payment.setServicePaymentId(2);
//                    RenderedService renderedService = DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO().getFirstRenderedServiceLessDate(2, parseInt(row[0]), LocalDate.now());
//                    if (renderedService != null) {
//                        payment.setRenderedServicePaymentId(renderedService.getId());
//                    }
//                }
//                if(parseInt(row[5]) == 2) {
//                    switch (row[6]) {
//                        case "001" :  payment.setServicePaymentId(3); break;
//                        case "002" :  payment.setServicePaymentId(9); break;
//                        case "003" :  payment.setServicePaymentId(6); break;
//                        case "004" :  payment.setServicePaymentId(5); break;
//                        case "005" :  payment.setServicePaymentId(7); break;
//                        case "006" :  payment.setServicePaymentId(8); break;
//                        case "007" :  payment.setServicePaymentId(10); break;
//                    }
//                    if(DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO().get(parseInt(row[7]) + 1000000) != null) {
//                        payment.setRenderedServicePaymentId(parseInt(row[7]) + 1000000);
//                    }
//                }
//
//                if(parseInt(row[5]) == 1) {
//                    payment.setServicePaymentId(1);
//                }
//                DAOFactory.getDefaultDAOFactory().getPaymentDAO().save(payment);
//                size++;
//            }
//            System.out.println(size + " payments have been loaded");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("No payments loaded.");
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        for (Subscriber subscriber :  DAOFactory.getDefaultDAOFactory().getSubscriberDAO().getAll()) {
//            if(subscriber.getId() > 5632) {
//                continue;
//            }
//            if(!DAOFactory.getDefaultDAOFactory().getPaymentDAO()
//                    .getBySubscriber(subscriber.getId()).stream()
//                    .filter(payment1 -> payment1.getServicePaymentId() == 2)
//                    .findFirst().isPresent()) {
//
//                Payment payment = new Payment();
//                payment.setSubscriberAccount(subscriber.getId());
//                payment.setPrice(41050D);
//                RenderedService renderedService = DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO()
//                        .getBySubscriber(subscriber.getId())
//                        .stream().filter(renderedServiceIn -> renderedServiceIn.getServiceId() == 2)
//                        .findFirst().get();
//                payment.setDate(renderedService.getDate());
//                payment.setServicePaymentId(renderedService.getServiceId());
//                payment.setRenderedServicePaymentId(renderedService.getId());
//                DAOFactory.getDefaultDAOFactory().getPaymentDAO().save(payment);
//            }
//        }
//    }
//
//    public void updateTariffsInSubscriberTariffs() {
//        String csvFile = "BASES/history.CSV";
//        BufferedReader br = null;
//        String line = "";
//        String cvsSplitBy = ",";
//
////        Set<Integer> set = new HashSet<>();
//        List<RenderedService> renderedServices = DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO()
//                .getByService(FixedServices.DISCONNECTION.getId());
//
//        try {
//            FileInputStream fis = new FileInputStream(csvFile);
//            br = new BufferedReader(new InputStreamReader(fis, "Cp1251"));
//            int size = -1;
//            HashSet<Integer> hashSet = new HashSet();
//            while ((line = br.readLine()) != null) {
//                if (size == -1) {
//                    size++;
//                    continue;
//                }
//                String[] row = line.split(cvsSplitBy);
//
//                Integer subscriber_account = parseInt(row[1]);
//
//                if (row[0].equals("0") || subscriber_account == null || !row[10].equals("99")
//                        || row[8].equals("0") || row[8] == null)
//                    continue;
//
//                if (renderedServices.stream().filter(e -> e.getSubscriberAccount().equals(subscriber_account)).count() == 0) {
//                    continue;
//                }
////                hashSet.add(subscriber_account);
//                System.out.println(subscriber_account + "=" + row[8]);
//                SubscriberTariff subscriberTariff = DAOFactory.getDefaultDAOFactory().getSubscriberDAO()
//                        .getNotClosedSubscriberTariff(subscriber_account, LocalDate.of(2017, 1, 1));
//                subscriberTariff.setTariffId(parseInt(row[8]));
//                DAOFactory.getDefaultDAOFactory().getSubscriberDAO().updateSubscriberTariff(subscriberTariff);
////                RenderedService renderedService = new RenderedService();
////                renderedService.setServiceId(FixedServices.SUBSCRIPTION_FEE.getId());
////                renderedService.setDate(LocalDate.of(parseInt(row[2]), parseInt(row[3]), 1));
////                renderedService.setSubscriberAccount(parseInt(row[1]));
////
////
////                renderedService.setPrice(parseInt(row[5]));
////                if(parseInt(row[2]) == 2004 && parseInt(row[3]) ==  3 && !row[4].equals("") && !row[4].equals(" ")) {
////                    renderedService.setPrice(parseInt(row[5]) + parseInt(row[4]));
////                }
//////                if(!set.contains(parseInt(row[1]))) {
//////                    renderedService.setPrice(parseInt(row[5]) + parseInt(row[4]));
//////                }
//////                set.add(parseInt(row[1]));
////                DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO().save(renderedService);
//                size++;
//            }
//            System.out.println(size + " subscriptionFees have been loaded");
//            System.out.println(renderedServices.size() + " renderedServices size");
//            System.out.println(hashSet.size() + " hashSet size");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("No subscriptionFees loaded.");
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private void createFirstSessionAndSubscriberTariff() {
//        List<RenderedService> connections =  DAOFactory.getDefaultDAOFactory().getRenderedServiceDAO().getByService(2);
//        for (RenderedService connection : connections) {
//            SubscriberSession subscriberSession = new SubscriberSession();
//            subscriberSession.setSubscriberAccount(connection.getSubscriberAccount());
//            subscriberSession.setConnectionDate(connection.getDate());
//
//        }
//    }
//
//    private Boolean parseBoolean(String element) {
//        return element.equals("Да");
//    }
//
//
//    private LocalDate parseDate(String element) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
//        LocalDate date = LocalDate.parse(element, formatter);
//        return date;
//    }
//
//    private LocalDate parseDateHyphen(String element) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
//        LocalDate date = LocalDate.parse(element, formatter);
//        return date;
//    }
//
//    private Integer parseInt(String str) {
//        // char code 160 is a space char
//        str = str.replaceAll("\u00A0", "");
//        if (str.length() == 0) {
//            return null;
//        }
//
//        Integer number;
//        try {
//            number = Integer.parseInt(str);
//        } catch (NumberFormatException ex) {
//            number = null;
//        }
//        return number;
//    }
//
//    private Double parseDouble(String str) {
//        // char code 160 is a space char
//        str = str.replaceAll("\u00A0", "");
//        if (str.length() == 0) {
//            return null;
//        }
//
//        Double number;
//        try {
//            number = Double.parseDouble(str);
//        } catch (NumberFormatException ex) {
//            number = null;
//        }
//        return number;
//    }
//
//    private String getString(String str) {
//        return ResourceBundles.getEntityBundle().getString(str);
//    }
//
}
//
