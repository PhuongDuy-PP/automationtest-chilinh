package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
//    Properties: data structure lưu dữ liệu dạng key-value
//    Map<String, String>
//    với file .properties thì sẽ dùng Properties
    private static final Properties properties = new Properties();

    public void loadProperties() {
//        try-catch
        try (InputStream input = ConfigReader.class
                .getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("File config.properties not found");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Key " + key + " not found in config.properties");
        }
        return value;
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }
}
