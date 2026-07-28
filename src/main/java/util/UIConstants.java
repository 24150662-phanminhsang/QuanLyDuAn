package util;

import java.awt.Color;
import java.awt.Font;

public final class UIConstants {

    private UIConstants() {
    }

    /*
     * Màu chính.
     */
    public static final Color PRIMARY =
            new Color(37, 99, 235);

    public static final Color PRIMARY_HOVER =
            new Color(29, 78, 216);

    public static final Color PRIMARY_LIGHT =
            new Color(219, 234, 254);

    /*
     * Sidebar.
     */
    public static final Color SIDEBAR =
            new Color(8, 35, 70);

    public static final Color SIDEBAR_SECONDARY =
            new Color(17, 55, 96);

    public static final Color SIDEBAR_HOVER =
            new Color(24, 67, 117);

    /*
     * Nội dung.
     */
    public static final Color BACKGROUND =
            new Color(244, 247, 251);

    public static final Color CARD_BACKGROUND =
            Color.WHITE;

    public static final Color BORDER =
            new Color(226, 232, 240);

    /*
     * Văn bản.
     */
    public static final Color TEXT_PRIMARY =
            new Color(15, 35, 64);

    public static final Color TEXT_SECONDARY =
            new Color(100, 116, 139);

    /*
     * Trạng thái.
     */
    public static final Color SUCCESS =
            new Color(22, 163, 74);

    public static final Color SUCCESS_LIGHT =
            new Color(220, 252, 231);

    public static final Color WARNING =
            new Color(234, 88, 12);

    public static final Color WARNING_LIGHT =
            new Color(255, 237, 213);

    public static final Color DANGER =
            new Color(225, 29, 72);

    public static final Color DANGER_LIGHT =
            new Color(255, 228, 230);

    public static final Color PURPLE =
            new Color(124, 58, 237);

    public static final Color PURPLE_LIGHT =
            new Color(237, 233, 254);

    /*
     * Font được thu nhỏ để phù hợp nhiều màn hình.
     */
    public static final Font FONT_SMALL =
            new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    11
            );

    public static final Font FONT_NORMAL =
            new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    13
            );

    public static final Font FONT_MEDIUM =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    13
            );

    public static final Font FONT_HEADING =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    18
            );

    public static final Font FONT_TITLE =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    24
            );

    public static final Font FONT_STATISTIC =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    27
            );
}