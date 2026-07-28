package view.components;

import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;

public class StatCard extends ContentCard {

    private final JLabel valueLabel;

    public StatCard(
            String title,
            String description,
            Ikon icon,
            Color iconColor,
            Color iconBackground
    ) {
        setLayout(
                new MigLayout(
                        "fill, insets 15",
                        "46![grow]",
                        "[][grow][]"
                )
        );

        /*
         * Chỉ đặt kích thước tối thiểu.
         * Không ép card quá rộng.
         */
        setMinimumSize(
                new Dimension(145, 130)
        );

        JPanel iconPanel = new JPanel(
                new MigLayout(
                        "fill, insets 0",
                        "[center]",
                        "[center]"
                )
        );

        iconPanel.setBackground(iconBackground);

        iconPanel.putClientProperty(
                "FlatLaf.style",
                "arc: 999"
        );

        JLabel iconLabel = new JLabel(
                FontIcon.of(
                        icon,
                        21,
                        iconColor
                )
        );

        iconPanel.add(
                iconLabel,
                "width 44!, height 44!"
        );

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        valueLabel = new JLabel("0");

        valueLabel.setFont(
                UIConstants.FONT_STATISTIC
        );

        valueLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(description);

        descriptionLabel.setFont(
                UIConstants.FONT_SMALL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        add(
                iconPanel,
                "cell 0 0 1 2, width 44!, height 44!"
        );

        add(
                titleLabel,
                "cell 1 0, growx"
        );

        add(
                valueLabel,
                "cell 1 1, growx"
        );

        add(
                descriptionLabel,
                "cell 0 2 2 1, growx"
        );
    }

    public void setValue(int value) {
        valueLabel.setText(
                String.valueOf(value)
        );
    }
}