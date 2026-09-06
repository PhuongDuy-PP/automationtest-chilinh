package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//Quy ước:
//convert file csv
// => json dynamic theo số lượng column và data tương ứng
// flow:
// B1: đọc line đầu tiên (header) -> đặt làm key (username, password, expected)
// B2: các dòng tiếp theo -> Admin,admin123,success -> 1 Map<Tên cột, giá trị> -> gộp 1 List
//VD: [
// {
//      username=Admin,
//      password=admin123,
//      expected=success
// }
// ]
public class DataReader {
    public static List<Map<String, String>> readCsv(String filePath) {
//        tạo biến lưu trữ
        List<Map<String, String>> rows = new ArrayList<>();
//        B1: mở file
        try (InputStream input = DataReader.class.getClassLoader().getResourceAsStream(filePath)) {
            if (input == null) {
                throw new RuntimeException("File " + filePath + " not found");
            }

//            B2: đọc từng dòng dữ liệu trong file csv
//            bọc InputStream vào BufferedReader và chỉ định charset UTF-8
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
                String headerLine = reader.readLine();
                if(headerLine == null) {
                    return rows;
                }
//                convert Admin,admin123,success -> ['Admin', 'admin123', 'success']
                String[] headers = headerLine.split(",");

//                đọc từng dòng dữ liệu
                String line;
                while((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }
                    String[] values = line.split(",");
                    Map<String, String> row = new LinkedHashMap<>();
                    for (int i = 0; i < headers.length && i < values.length; i++) {
                        row.put(headers[i], values[i]);
                    }
                    rows.add(row);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rows;
    }
}
