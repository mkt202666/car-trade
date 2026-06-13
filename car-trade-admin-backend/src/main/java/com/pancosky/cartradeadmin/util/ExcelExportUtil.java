package com.pancosky.cartradeadmin.util;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExcelExportUtil {

    /**
     * 生成 Excel 并写入 HttpServletResponse
     * @param response HttpServletResponse
     * @param fileName 文件名（不含扩展名）
     * @param headers 表头
     * @param dataRows 数据行
     * @throws IOException
     */
    public static void export(HttpServletResponse response, String fileName, String[] headers, List<String[]> dataRows) throws IOException {
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();

        // 创建Sheet
        Sheet sheet = workbook.createSheet("Sheet1");

        // 创建表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // 创建数据样式
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // 创建表头行
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            // 自动调整列宽
            sheet.setColumnWidth(i, 20 * 256);
        }

        // 填充数据
        for (int i = 0; i < dataRows.size(); i++) {
            Row row = sheet.createRow(i + 1);
            String[] rowData = dataRows.get(i);
            for (int j = 0; j < rowData.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(rowData[j] != null ? rowData[j] : "");
                cell.setCellStyle(dataStyle);
            }
        }

        // 设置响应头
        String encodedFileName = URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8.toString());
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);

        // 写入输出流
        workbook.write(response.getOutputStream());

        // 关闭工作簿
        workbook.close();
    }
}
