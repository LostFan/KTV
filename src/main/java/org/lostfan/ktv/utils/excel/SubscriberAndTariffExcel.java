package org.lostfan.ktv.utils.excel;

import jxl.Workbook;
import jxl.format.*;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.*;
import org.lostfan.ktv.domain.Subscriber;
import org.lostfan.ktv.domain.Tariff;
import org.lostfan.ktv.model.dto.SubscriberAndTariffDTO;
import org.lostfan.ktv.utils.DateFormatter;
import org.lostfan.ktv.utils.ResourceBundles;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SubscriberAndTariffExcel implements ExcelGenerator {

    private List<SubscriberAndTariffDTO> subscriberAndTariffDTOList;

    private List<Tariff> allTariffs;

    public void setSubscriberAndTariffDTOList(List<SubscriberAndTariffDTO> subscriberAndTariffDTOList) {
        this.subscriberAndTariffDTOList = subscriberAndTariffDTOList.stream().sorted((e1, e2) -> e1.getTariff().getId().compareTo(e2.getTariff().getId())).collect(Collectors.toList());
    }

    private String fileName;
    private LocalDate date;

    public SubscriberAndTariffExcel(String fileName, LocalDate date) {
        this.fileName = fileName;
        this.date = date;
    }

    public List<Tariff> getAllTariffs() {
        return allTariffs;
    }

    public void setAllTariffs(List<Tariff> allTariffs) {
        this.allTariffs = allTariffs.stream().sorted((e1, e2) -> e1.getId().compareTo(e2.getId())).collect(Collectors.toList());;
    }

    @Override
    public String generate() {

        WritableWorkbook workbook;
        String message = null;
        try {
            Integer row = 0;
            Integer SUBSCRIBER_ID_COLUMN = row++;
            Integer SUBSCRIBER_NAME_COLUMN = row++;
            Integer ADDRESS_COLUMN = row++;
            Integer CHANNELS_COLUMN = row++;
            //Creating WorkBook
            String fileName = String.format("%s - %s", ResourceBundles.getEntityBundle().getString(
                    this.fileName), DateFormatter.format(date));
            File file = new File(fileName + ".xls");
            workbook = Workbook.createWorkbook(file);
            //Creating sheet
            WritableSheet sheet = workbook.createSheet("PAGE 1", 0);
            sheet.setColumnView(SUBSCRIBER_ID_COLUMN, 8);
            sheet.setColumnView(SUBSCRIBER_NAME_COLUMN, 20);
            sheet.setColumnView(ADDRESS_COLUMN, 30);
            sheet.setColumnView(CHANNELS_COLUMN, 20);
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

            sheet.addCell(new Label(CHANNELS_COLUMN, row, ResourceBundles.getEntityBundle().getString(
                    "payment.price"), cellFormat));
            row++;

            for (SubscriberAndTariffDTO subscriberDebit : subscriberAndTariffDTOList) {
                sheet.addCell(new Number(SUBSCRIBER_ID_COLUMN, row, subscriberDebit.getSubscriberAccount()));
                sheet.addCell(new Label(SUBSCRIBER_NAME_COLUMN, row, getAbbreviatedName(subscriberDebit.getSubscriber())));
                sheet.addCell(new Label(ADDRESS_COLUMN, row, getFullSubscriberAddress(subscriberDebit)));
                sheet.addCell(new Label(CHANNELS_COLUMN, row, subscriberDebit.getTariff().getName()));
                row++;
            }
            row++;

            WritableCellFormat wcf = new WritableCellFormat();
            wcf.setBorder(Border.TOP, BorderLineStyle.THIN);
            sheet.addCell(new Label(SUBSCRIBER_ID_COLUMN, row, getGuiString("inTotal"), wcf));
            sheet.addCell(new Label(SUBSCRIBER_NAME_COLUMN, row, "", wcf));
            sheet.addCell(new Label(ADDRESS_COLUMN, row, getGuiString("subscribersCount"), wcf));
            sheet.addCell(new Label(CHANNELS_COLUMN, row, "", wcf));
            row++;


            for (Tariff tariff : allTariffs) {
                List<SubscriberAndTariffDTO> result = subscriberAndTariffDTOList.stream()
                        .filter(e -> e.getTariff().getId().equals(tariff.getId())).collect(Collectors.toList());
                if(result.size() > 0) {
                    sheet.addCell(new Number(ADDRESS_COLUMN, row, result.size()));
                    sheet.addCell(new Label(CHANNELS_COLUMN, row, tariff.getName()));
                    row++;
                }
            }

            sheet.addCell(new Number(ADDRESS_COLUMN, row, subscriberAndTariffDTOList.size()));
            sheet.addCell(new Label(CHANNELS_COLUMN, row, getGuiString("inTotal")));
            row++;


            workbook.write();
            workbook.close();
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }
            try {
                desktop.open(file);
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

    private String getAbbreviatedName(Subscriber subscriber) {
        String abbreviatedName = subscriber.getName();
        String[] strings = abbreviatedName.split("\\s+");
        if (strings.length < 2) {
            return subscriber.getName();
        }
        StringBuilder name = new StringBuilder()
                .append(strings[0])
                .append(" ")
                .append(strings[1].charAt(0))
                .append(".");
        if (strings.length > 2) {
            name.append(strings[2].charAt(0))
                    .append(". ");
        }
        return name.toString();
    }

    private String getFullSubscriberAddress(SubscriberAndTariffDTO dto) {
        Subscriber subscriber = dto.getSubscriber();
        if (subscriber == null || dto.getSubscriberStreet() == null) {
            return "";
        }
        StringBuilder address = new StringBuilder(dto.getSubscriberStreet().getName())
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
}
