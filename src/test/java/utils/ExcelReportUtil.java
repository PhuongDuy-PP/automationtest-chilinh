package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelReportUtil {
    private static final String EXCEL_PATH = "src/test/resources/data/TestResults.xlsx";
    private static final String HEADER_FEATURE = "Feature";
    private static final String HEADER_TEST_CASE = "Test Case";
    private static final String HEADER_STATUS = "trạng thái";
    private static final String HEADER_NOTE = "Ghi chú";

    private static XSSFWorkbook createNewWorkbook() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test Results");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("STT");
        header.createCell(1).setCellValue(HEADER_FEATURE);
        header.createCell(2).setCellValue(HEADER_TEST_CASE);
        header.createCell(3).setCellValue(HEADER_STATUS);
        header.createCell(4).setCellValue(HEADER_NOTE);

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

//                tìm trong toàn bộ dữ liệu xem có dòng nào cot Test case trùng khớp CHÍNH XÁC với tên scenario đang chạy hay không
                Row targetRow = findRowByTestCase(sheet, testCaseCol, testCaseName);

//                TH1: NẾU KHÔNG KHOP => TAO ROW MOI => THEM THONG TIN, STATUS VAO FILE EXCEL
                if (targetRow == null) {
                    int newRowIndex = sheet.getLastRowNum() + 1;
                    targetRow = sheet.createRow(newRowIndex);
                    setCellValue(targetRow, 0, String.valueOf(newRowIndex));
                    setCellValue(targetRow, testCaseCol, testCaseName);
                    setCellValue(targetRow, featureCol, featureName);
                }


//                TH2: NEU KHOOP => UPDATE VALUE CUA COT TRANG THAI
                setCellValue(targetRow, statusCol, status);

//                test case fail => luu noi dung vao col ghi chu
                boolean isPassed = status != null && status.equalsIgnoreCase("PASSED");
                setCellValue(targetRow, noteCol, isPassed ? "" : note);

//                ghi de lai toan bo workbook xuong file excel
                try (FileOutputStream output = new FileOutputStream(file)) {
                    workbook.write(output);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
