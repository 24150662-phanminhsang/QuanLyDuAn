package util;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIManager;
import java.awt.Color;

public final class UITheme {

    private UITheme() {
    }

    public static void setup() {
        try {
            FlatLightLaf.setup();

            UIManager.put(
                    "defaultFont",
                    UIConstants.FONT_NORMAL
            );

            UIManager.put("Component.arc", 14);
            UIManager.put("Button.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("ProgressBar.arc", 12);

            UIManager.put("Table.rowHeight", 44);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.showHorizontalLines", true);

            UIManager.put(
                    "Table.gridColor",
                    UIConstants.BORDER
            );

            UIManager.put(
                    "Table.selectionBackground",
                    UIConstants.PRIMARY_LIGHT
            );

            UIManager.put(
                    "Table.selectionForeground",
                    UIConstants.TEXT_PRIMARY
            );

            UIManager.put(
                    "TableHeader.background",
                    new Color(248, 250, 252)
            );

            UIManager.put(
                    "TableHeader.foreground",
                    UIConstants.TEXT_PRIMARY
            );

            UIManager.put("ScrollBar.width", 10);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.trackArc", 999);

        } catch (Exception exception) {
            System.err.println(
                    "Không thể khởi tạo giao diện: "
                            + exception.getMessage()
            );
        }
    }
}