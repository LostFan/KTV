package org.lostfan.ktv.utils.excel;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.*;
import org.lostfan.ktv.domain.Service;
import org.lostfan.ktv.domain.Subscriber;
import org.lostfan.ktv.model.dto.ServiceReportSheetTableDTO;
import org.lostfan.ktv.utils.ResourceBundles;
import org.lostfan.ktv.utils.SubscriberByAddressComparator;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Boolean;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DisconnectSubscribersReportExcel implements ExcelGenerator {

    private List<ServiceReportSheetTableDTO> serviceReportSheetTableDTOs;

    private LocalDate date;

    public List<ServiceReportSheetTableDTO> getServiceReportSheetTableDTOs() {
        return serviceReportSheetTableDTOs;
    }

    public void setServiceReportSheetTableDTOs(List<ServiceReportSheetTableDTO> serviceReportSheetTableDTOs) {
        this.serviceReportSheetTableDTOs = serviceReportSheetTableDTOs;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String generate() {
        WritableWorkbook workbook;
        String message = null;
        try {
            int i = 0;
            Integer FIRST_COLUMN = i;

            Integer SUBSCRIBER_ID_COLUMN = i++;
            Integer SUBSCRIBER_ADDRESS_COLUMN = i++;
            Integer SUBSCRIBER_NAME_COLUMN = i++;
            Integer DATE = i++;

            //Creating WorkBook
            String fileName =
                String.format("%d.%d.%d.xls",
                    date.getDayOfMonth(), date.getMonthValue(), date.getYear());
            File file = new File(fileName);
            workbook = Workbook.createWorkbook(file);
            //Creating sheet
            WritableSheet sheet = workbook.createSheet("PAGE 1", 0);
            sheet.setColumnView(SUBSCRIBER_ID_COLUMN, 6);
            sheet.setColumnView(SUBSCRIBER_ADDRESS_COLUMN, 25);
            sheet.setColumnView(SUBSCRIBER_NAME_COLUMN, 20);
            sheet.setColumnView(DATE, 20);
            WritableCellFormat cellFormat = new WritableCellFormat();
            cellFormat.setAlignment(Alignment.CENTRE);
            i = 0;
            i++;
            //Addding cells
            sheet.addCell(new Label(SUBSCRIBER_ID_COLUMN, i, ResourceBundles.getEntityBundle().getString(
                    "subscriber"), cellFormat));
            sheet.addCell(new Label(SUBSCRIBER_ADDRESS_COLUMN, i, getGuiString(
                    "address"), cellFormat));
            sheet.addCell(new Label(SUBSCRIBER_NAME_COLUMN, i, ResourceBundles.getEntityBundle().getString(
                    "subscriber.name"), cellFormat));
            sheet.addCell(new Label(DATE, i, getGuiString(
                    "date"), cellFormat));
            i++;

            SubscriberByAddressComparator comparator = new SubscriberByAddressComparator();
            serviceReportSheetTableDTOs = serviceReportSheetTableDTOs.stream()
                    .sorted((o1, o2) -> comparator.compare(o1.getSubscriber(), o2.getSubscriber()))
                    .collect(Collectors.toList());

            for (ServiceReportSheetTableDTO serviceReportSheetTableDTO : serviceReportSheetTableDTOs) {
                sheet.addCell(new Number(SUBSCRIBER_ID_COLUMN, i, serviceReportSheetTableDTO.getSubscriberAccount()));
                sheet.addCell(new Label(SUBSCRIBER_ADDRESS_COLUMN, i, getFullSubscriberAddress(serviceReportSheetTableDTO)));
                sheet.addCell(new Label(SUBSCRIBER_NAME_COLUMN, i, getAbbreviatedName(serviceReportSheetTableDTO)));
                sheet.addCell(new Label(DATE, i,
                        serviceReportSheetTableDTO.getDate() != null ?
                                serviceReportSheetTableDTO.getDate().toString()
                                : ""
                ));
                i++;
            }


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

    private String getFullSubscriberAddress(ServiceReportSheetTableDTO dto) {
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

    private String getAbbreviatedName(ServiceReportSheetTableDTO dto) {
        String abbreviatedName = dto.getSubscriber().getName();
        String[] strings = abbreviatedName.split("\\s+");
        if (strings.length < 2) {
            return dto.getSubscriber().getName();
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

    private static String getGuiString(String key) {
        return ResourceBundles.getGuiBundle().getString(key);
    }
}
