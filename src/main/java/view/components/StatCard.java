package view.components;

import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.Locale;

public class StatCard extends ContentCard {

    private final JLabel valueLabel;
    private final NumberFormat numberFormat;

    public StatCard(
            String title,
            String description,
            Ikon icon,
            Color iconColor,
            Color iconBackground
    ) {
        numberFormat = NumberFormat.getIntegerInstance(
                new Locale("vi", "VN")
        );

        valueLabel = new JLabel("0");

        initializeView(
                title,
                description,
                icon,
                iconColor,
                iconBackground
        );
    }

    private void initializeView(
            String title,
            String description,
            Ikon icon,
            Color iconColor,
            Color iconBackground
    ) {
        setLayout(
                new MigLayout(
                        "fill, wrap 1, insets 14 15",
                        "[grow, fill]",
                        "[]10[]4[]"
                )
        );

        setMinimumSize(
                new Dimension(145, 128)
        );

        setPreferredSize(
                new Dimension(190, 135)
        );

        add(
                createHeaderPanel(
                        title,
                        icon,
                        iconColor,
                        iconBackground
                ),
                "growx"
        );

        configureValueLabel();

        add(
                valueLabel,
                "growx"
        );

        add(
                createDescriptionLabel(description),
                "growx"
        );
    }

    private JPanel createHeaderPanel(
            String title,
            Ikon icon,
            Color iconColor,
            Color iconBackground
    ) {
        JPanel headerPanel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow, fill]8[40!]",
                        "[center]"
                )
        );

        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(
                safeText(title, "Thống kê")
        );

        titleLabel.setFont(
                UIConstants.FONT_NORMAL.deriveFont(
                        Font.BOLD
                )
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        titleLabel.setToolTipText(
                safeText(title, "Thống kê")
        );

        JPanel iconPanel = createIconPanel(
                icon,
                iconColor,
                iconBackground
        );

        headerPanel.add(
                titleLabel,
                "growx"
        );

        headerPanel.add(
                iconPanel,
                "width 40!, height 40!, align right"
        );

        return headerPanel;
    }

    private JPanel createIconPanel(
            Ikon icon,
            Color iconColor,
            Color iconBackground
    ) {
        JPanel iconPanel = new JPanel(
                new MigLayout(
                        "fill, insets 0",
                        "[center]",
                        "[center]"
                )
        );

        iconPanel.setBackground(
                iconBackground == null
                        ? UIConstants.PRIMARY_LIGHT
                        : iconBackground
        );

        iconPanel.setMinimumSize(
                new Dimension(40, 40)
        );

        iconPanel.setPreferredSize(
                new Dimension(40, 40)
        );

        iconPanel.putClientProperty(
                "FlatLaf.style",
                """
                arc: 999;
                borderWidth: 0;
                """
        );

        if (icon != null) {
            JLabel iconLabel = new JLabel(
                    FontIcon.of(
                            icon,
                            19,
                            iconColor == null
                                    ? UIConstants.PRIMARY
                                    : iconColor
                    )
            );

            iconPanel.add(
                    iconLabel,
                    "align center"
            );
        }

        return iconPanel;
    }

    private void configureValueLabel() {
        valueLabel.setFont(
                UIConstants.FONT_STATISTIC
        );

        valueLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        valueLabel.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        valueLabel.setToolTipText("0");
    }

    private JLabel createDescriptionLabel(
            String description
    ) {
        JLabel descriptionLabel = new JLabel(
                safeText(
                        description,
                        "Thông tin hệ thống"
                )
        );

        descriptionLabel.setFont(
                UIConstants.FONT_SMALL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        descriptionLabel.setToolTipText(
                safeText(
                        description,
                        "Thông tin hệ thống"
                )
        );

        return descriptionLabel;
    }

    public void setValue(int value) {
        setValue((long) value);
    }

    public void setValue(long value) {
        long safeValue = Math.max(0, value);

        String formattedValue =
                numberFormat.format(safeValue);

        valueLabel.setText(formattedValue);
        valueLabel.setToolTipText(formattedValue);
    }

    public void setValue(String value) {
        String displayValue =
                value == null || value.isBlank()
                        ? "0"
                        : value.trim();

        valueLabel.setText(displayValue);
        valueLabel.setToolTipText(displayValue);
    }

    private String safeText(
            String value,
            String defaultValue
    ) {
        return value == null || value.isBlank()
                ? defaultValue
                : value.trim();
    }
}