package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Đọc cấu hình từ file config.properties
 * trong thư mục src/main/resources.
 */
public final class ConfigLoader {

    private static final String CONFIG_FILE =
            "config.properties";

    private static final Properties PROPERTIES =
            new Properties();

    static {
        loadConfiguration();
    }

    private ConfigLoader() {
    }

    private static void loadConfiguration() {
        try (
                InputStream inputStream =
                        ConfigLoader.class
                                .getClassLoader()
                                .getResourceAsStream(CONFIG_FILE)
        ) {
            if (inputStream == null) {
                throw new IllegalStateException(
                        "Không tìm thấy file "
                                + CONFIG_FILE
                                + " trong src/main/resources"
                );
            }

            PROPERTIES.load(inputStream);

        } catch (IOException exception) {
            throw new ExceptionInInitializerError(
                    "Không thể đọc config.properties: "
                            + exception.getMessage()
            );
        }
    }

    public static String get(String key) {
        String value = PROPERTIES.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Thiếu cấu hình: " + key
            );
        }

        return value.trim();
    }
}