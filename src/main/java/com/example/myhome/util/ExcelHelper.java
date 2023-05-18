package com.example.myhome.util;

import com.example.myhome.model.Invoice;
import com.example.myhome.model.InvoiceComponents;
import com.example.myhome.model.InvoiceTemplate;
import com.example.myhome.model.PaymentDetails;
import com.example.myhome.repository.PaymentDetailsRepository;
import lombok.extern.java.Log;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@Log
public class ExcelHelper {

    /*

    Класс для создания квитанций в форме Excel файла при помощи заданных шаблонов

    метод createMapFromInvoice создаёт пары ключ-значение для шаблона (ключ: %параметр для замены%)
    сделан только на русском языке

    метод turnInvoiceIntoExcel пробегается по шаблону и подставляет нужные параметры
    единственное исключение - это услуги , которых может быть неопределенное количество
    так что в этом случае он создает нужные ячейки и заполняет их

     */

    private static final String FILE_PATH = "C:\\Users\\OneSmiLe\\IdeaProjects\\MyHome\\src\\main\\resources\\static\\files\\";

    private static final List<String> templateCommandsList = List.of("%payCompany%",
            "%accountNumber%", "%invoiceNumber%", "%invoiceDate%", "%invoiceAddress%",
            "%total%", "%accountBalance%", "%totalDebt%", "%invoiceMonth%",
            "%serviceName%", "%servicePrice%", "%serviceUnit%", "%serviceAmount%", "%serviceTotal%", "%service%");

    @Autowired private PaymentDetailsRepository paymentDetailsRepository;

    public String turnInvoiceIntoExcel(Invoice invoice, InvoiceTemplate template) throws IOException {

        Map<String, String> commandsMap = createMapFromInvoice(invoice);
        List<InvoiceComponents> components = invoice.getComponents();

        log.info(invoice.toString());
        LocalDateTime now = LocalDateTime.now();
        String finalFileName = "invoice-"+ now.getYear() + now.getMonth().getValue() + now.getDayOfMonth()
                + now.getHour() + now.getMinute() +"."+template.getFile().split("\\.")[1];

        File originalFile = new File(FILE_PATH+template.getFile());
        File clonedFile = new File(FILE_PATH+finalFileName);
        Files.copy(originalFile.toPath(), clonedFile.toPath());

        try(InputStream in = new FileInputStream(clonedFile)){

            XSSFWorkbook workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheetAt(0);

            for(Row row : sheet) {

                for(Cell cell : row) {

                    if (cell.getCellType() == CellType.STRING) {
                        if (commandsMap.containsKey(cell.getStringCellValue()))
                            cell.setCellValue(commandsMap.get(cell.getStringCellValue()));
                        else if (cell.getStringCellValue().equalsIgnoreCase("%serviceName%")) {
                            for (int i = 0; i < components.size(); i++) {
                                sheet.getRow(row.getRowNum()+i)
                                        .getCell(cell.getColumnIndex(), Row.MissingCellPolicy.RETURN_NULL_AND_BLANK)
                                        .setCellValue(components.get(i).getService().getName());
                            }
                        }
                        else if (cell.getStringCellValue().equalsIgnoreCase("%servicePrice%")) {
                            for (int i = 0; i < components.size(); i++) {
                                sheet.getRow(row.getRowNum()+i)
                                        .getCell(cell.getColumnIndex(), Row.MissingCellPolicy.RETURN_NULL_AND_BLANK)
                                        .setCellValue(components.get(i).getUnit_price());
                            }
                        }
                        else if (cell.getStringCellValue().equalsIgnoreCase("%serviceUnit%")) {
                            for (int i = 0; i < components.size(); i++) {
                                sheet.getRow(row.getRowNum()+i)
                                        .getCell(cell.getColumnIndex(), Row.MissingCellPolicy.RETURN_NULL_AND_BLANK)
                                        .setCellValue(components.get(i).getService().getUnit().getName());
                            }
                        }
                        else if (cell.getStringCellValue().equalsIgnoreCase("%serviceAmount%")) {
                            for (int i = 0; i < components.size(); i++) {
                                sheet.getRow(row.getRowNum()+i)
                                        .getCell(cell.getColumnIndex(), Row.MissingCellPolicy.RETURN_NULL_AND_BLANK)
                                        .setCellValue(components.get(i).getUnit_amount());
                            }
                            Cell finalCell = sheet.getRow(row.getRowNum()+components.size())
                                    .getCell(cell.getColumnIndex(), Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);

                            finalCell.setCellValue("Разом: ");

                            CellStyle cellStyle = workbook.createCellStyle();
                            Font font = workbook.createFont();
                            font.setFontName("Arial");
                            font.setBold(true);
                            cellStyle.setFont(font);

                            cellStyle.setAlignment(HorizontalAlignment.CENTER);

                            finalCell.setCellStyle(cellStyle);
                        }
                        else if (cell.getStringCellValue().equalsIgnoreCase("%serviceTotal%")) {
                            for (int i = 0; i < components.size(); i++) {
                                sheet.getRow(row.getRowNum()+i)
                                        .getCell(cell.getColumnIndex(), Row.MissingCellPolicy.RETURN_NULL_AND_BLANK)
                                        .setCellValue(components.get(i).getTotalPrice());
                            }
                            Cell totalCell = sheet.getRow(row.getRowNum()+components.size())
                                    .getCell(cell.getColumnIndex(), Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);

                            totalCell.setCellValue(invoice.getTotal_price());

                            CellStyle cellStyle = workbook.createCellStyle();
                            Font font = workbook.createFont();
                            font.setFontName("Arial");
                            font.setBold(true);
                            cellStyle.setFont(font);

                            cellStyle.setAlignment(HorizontalAlignment.CENTER);

                            totalCell.setCellStyle(cellStyle);
                        }
                    }
                }

            }

            try (OutputStream fileOut = new FileOutputStream(clonedFile)){
                workbook.write(fileOut);
            }
            log.info("Workbook closed");
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("try block over");
        return finalFileName;
    }

    private Map<String, String> createMapFromInvoice(Invoice invoice) {

        PaymentDetails pd = paymentDetailsRepository.findById(1L).orElse(null);
        String payCompany = (pd != null) ? pd.getName() + ", " + pd.getDescription() : "N/A";
        String invoiceAddress = (invoice.getApartment() != null && invoice.getApartment().getOwner() != null) ?
                invoice.getApartment().getOwner().getFullName() : "N/A";
        String total = String.valueOf(invoice.getTotal_price());
        String accountNumber = (invoice.getAccount() != null) ? String.format("%010d", invoice.getAccount().getId()) : "N/A";
        String accountBalance = (invoice.getAccount() != null) ? String.valueOf(invoice.getAccount().getBalance()) : "N/A";
        String invoiceNumber = String.valueOf(invoice.getId());
        String invoiceDate = invoice.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String invoiceMonth = invoice.getDate().format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        return Map.of("%payCompany%", payCompany, "%invoiceAddress%", invoiceAddress, "%total%", total,
                "%accountNumber%",accountNumber, "%accountBalance%", accountBalance, "%invoiceNumber%",
                invoiceNumber, "%invoiceDate%", invoiceDate,"%invoiceMonth%", invoiceMonth, "%totalDebt%", total);
    }

}
