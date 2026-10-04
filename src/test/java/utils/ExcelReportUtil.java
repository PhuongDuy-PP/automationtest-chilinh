package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExcelReportUtil {
    private static final String EXCEL_PATH = "src/test/resources/data/TestResults_"
            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".xlsx";
    private static final String HEADER_FEATURE = "Feature";
    private static final String HEADER_TEST_CASE = "Test Case";
    private static final String HEADER_STATUS = "trạng thái";
    private static final String HEADER_NOTE = "Ghi chú";
    private static final String HEADER_BROWSER = "Browser";

    private static XSSFWorkbook createNewWorkbook() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test Results");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("STT");
        header.createCell(1).setCellValue(HEADER_FEATURE);
        header.createCell(2).setCellValue(HEADER_TEST_CASE);
        header.createCell(3).setCellValue(HEADER_STATUS);
        header.createCell(4).setCellValue(HEADER_NOTE);
        header.createCell(5).setCellValue(HEADER_BROWSER);

        return workbook;
    }

    private static int findColumnIndex(Row headerRow, String headerName) {
        if(headerRow == null) {
            return -1;
        }

        for(Cell cell: headerRow) {
            if(cell.getStringCellValue().trim().equalsIgnoreCase(headerName)) {
                return cell.getColumnIndex();
            }
        }
        return -1;
    }

//    getCellValue => doc gia tri cua 1 cell
    private static String getCellValue(Row row, int columnIndex) {
        if (row == null || columnIndex == -1) {
            return "";
        }
        Cell cell = row.getCell(columnIndex);
        return cell == null ? "" : cell.getStringCellValue().trim();
    }

    private static void setCellValue(Row row, int columnIndex, String value) {
        if (columnIndex == -1) {
//            file excel ko cos cot nay => bo qua, khong lam crash toan bo qua trinh update
            return;
        }

        Cell existingCell = row.getCell(columnIndex);
        if (existingCell != null) {
            row.removeCell(existingCell);
        }
        row.createCell(columnIndex).setCellValue(value == null ? "" : value);
    }

    private static Row findRowByTestCase(XSSFSheet sheet, int testCaseCol, String testCaseName) {
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if(row != null && getCellValue(row, testCaseCol).equals(testCaseName.trim())){
                return row;
            }
        }
        return null;
    }

    private static String getBrowserName() {
        String browser = System.getProperty("browser");
        if (browser == null || browser.isBlank()) {
            return "chrome";
        }
        return browser.trim().toLowerCase();
    }

//    hàm update status test case trong file excel
//    dùng cơ chế synchornized: đưa các kết qua test case vào hàng đợi
//    cái nào xong trước thì đọc file để cập nhật status trước
    public static synchronized void updateStatus(String featureName, String testCaseName, String status, String note) {
//        tạo Object đại diện cho path lưu thông tin file
        File file = new File(EXCEL_PATH);

        try {
            XSSFWorkbook workbook; // biến lưu trữ toàn bộ nội dung file excel trong RAM để thao tác
            if (file.exists()) {
                try (FileInputStream input = new FileInputStream(file)) {
                    workbook = new XSSFWorkbook(input);
                }
            } else {
//                không tồn tại => tạo file excel mới
                workbook = createNewWorkbook();
            }

            try (workbook){
//                lay sheet đầu tiên
                XSSFSheet sheet = workbook.getSheetAt(0);
//                dong 0 luon la header
                Row headerRow = sheet.getRow(0);

//                dò xem cột Test case và trạng thái nằm ở vị trí nào trong file excel
//                không tìm thấy => -1
                int testCaseCol = findColumnIndex(headerRow, HEADER_TEST_CASE);
                int statusCol = findColumnIndex(headerRow, HEADER_STATUS);
                int noteCol = findColumnIndex(headerRow, HEADER_NOTE);
                int featureCol = findColumnIndex(headerRow, HEADER_FEATURE);

                if(testCaseCol == -1 || statusCol == -1) {
                    System.out.println("File excel thiếu cột " + HEADER_TEST_CASE + " HOẶC " + HEADER_STATUS);
                    return;
                }

//                update: them column browser
                int browserCol = findColumnIndex(headerRow, HEADER_BROWSER);

                int newRowIndex = sheet.getLastRowNum() + 1;
                Row targetRow = sheet.createRow(newRowIndex);
                setCellValue(targetRow, 0, String.valueOf(newRowIndex));
                setCellValue(targetRow, testCaseCol, testCaseName);
                setCellValue(targetRow, featureCol, featureName);
                setCellValue(targetRow, browserCol, getBrowserName());
                setCellValue(targetRow, statusCol, status);

//                test case fail => luu noi dung vao cot ghi chu
                boolean isPassed = status != null && status.equalsIgnoreCase("PASSED");
                setCellValue(targetRow, noteCol, isPassed ? "" : note);

                try (FileOutputStream output = new FileOutputStream(file)) {
                    workbook.write(output);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
