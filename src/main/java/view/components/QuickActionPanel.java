package view.components;

import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;

public class QuickActionPanel extends ContentCard {

    private final JPanel actionsPanel;

    private Consumer<String> actionHandler =
            actionKey -> {
            };

    public QuickActionPanel() {
        actionsPanel = new JPanel();
        actionsPanel.setOpaque(false);

        initializeView();
    }

    private void initializeView() {
        setLayout(
                new MigLayout(
                        "fill, wrap 1, insets 18 20",
                        "[grow, fill]",
                        "[]12[grow, fill]"
                )
        );

        setMinimumSize(
                new Dimension(320, 210)
        );

        setPreferredSize(
                new Dimension(460, 230)
        );

        add(
                createHeaderPanel(),
                "growx"
        );

        actionsPanel.setLayout(
                new MigLayout(
                        "fill, wrap 3, insets 0",
                        "[grow, fill][grow, fill][grow, fill]",
                        "[grow, fill][grow, fill]"
                )
        );

        actionsPanel.add(
                createActionButton(
                        "Tài khoản",
                        FontAwesomeSolid.USER_PLUS,
                        UIConstants.PRIMARY,
                        UIConstants.PRIMARY_LIGHT,
                        "USERS"
                ),
                "grow"
        );

        actionsPanel.add(
                createActionButton(
                        "Khóa học",
                        FontAwesomeSolid.BOOK_OPEN,
                        UIConstants.SUCCESS,
                        UIConstants.SUCCESS_LIGHT,
                        "COURSES"
                ),
                "grow"
        );

        actionsPanel.add(
                createActionButton(
                        "Lớp học",
                        FontAwesomeSolid.SCHOOL,
                        UIConstants.PURPLE,
                        UIConstants.PURPLE_LIGHT,
                        "CLASSES"
                ),
                "grow"
        );

        actionsPanel.add(
                createActionButton(
                        "Thanh toán",
                        FontAwesomeSolid.CREDIT_CARD,
                        UIConstants.WARNING,
                        UIConstants.WARNING_LIGHT,
                        "PAYMENTS"
                ),
                "grow"
        );

        actionsPanel.add(
                createActionButton(
                        "Báo cáo",
                        FontAwesomeSolid.CHART_BAR,
                        UIConstants.PRIMARY,
                        UIConstants.PRIMARY_LIGHT,
                        "REPORTS"
                ),
                "grow"
        );

        actionsPanel.add(
                createActionButton(
                        "Thông báo",
                        FontAwesomeSolid.BELL,
                        UIConstants.DANGER,
                        UIConstants.DANGER_LIGHT,
                        "NOTIFICATIONS"
                ),
                "grow"
        );

        add(
                actionsPanel,
                "grow, push"
        );
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[][grow][]",
                        "[center]"
                )
        );

        panel.setOpaque(false);

        JLabel iconLabel = new JLabel(
                FontIcon.of(
                        FontAwesomeSolid.BOLT,
                        15,
                        UIConstants.PRIMARY
                )
        );

        JLabel titleLabel = new JLabel(
                "Thao tác nhanh"
        );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel subtitleLabel = new JLabel(
                "Truy cập nhanh"
        );

        subtitleLabel.setFont(
                UIConstants.FONT_SMALL
        );

        subtitleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(iconLabel);
        panel.add(
                titleLabel,
                "gapleft 6"
        );
        panel.add(
                subtitleLabel,
                "align right"
        );

        return panel;
    }

    public void setActionHandler(
            Consumer<String> actionHandler
    ) {
        this.actionHandler =
                actionHandler == null
                        ? actionKey -> {
                }
                        : actionHandler;
    }

    private JButton createActionButton(
            String title,
            Ikon icon,
            Color iconColor,
            Color backgroundColor,
            String actionKey
    ) {
        JButton button = new JButton(title);

        button.setIcon(
                FontIcon.of(
                        icon,
                        18,
                        iconColor
                )
        );

        button.setFont(
                UIConstants.FONT_SMALL.deriveFont(
                        Font.BOLD
                )
        );

        button.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        button.setBackground(
                backgroundColor
        );

        button.setVerticalTextPosition(
                SwingConstants.BOTTOM
        );

        button.setHorizontalTextPosition(
                SwingConstants.CENTER
        );

        button.setIconTextGap(7);

        button.setMinimumSize(
                new Dimension(92, 70)
        );

        button.setPreferredSize(
                new Dimension(110, 74)
        );

        button.setFocusable(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setBorder(
                BorderFactory.createLineBorder(
                        lighten(iconColor, 0.72f)
                )
        );

        button.putClientProperty(
                "FlatLaf.style",
                """
                arc: 12;
                borderWidth: 1;
                focusWidth: 0;
                margin: 7,7,7,7;
                """
        );

        button.addActionListener(
                event -> actionHandler.accept(
                        actionKey
                )
        );

        return button;
    }

    private Color lighten(
            Color color,
            float ratio
    ) {
        int red = (int) (
                color.getRed()
                        + (255 - color.getRed()) * ratio
        );

        int green = (int) (
                color.getGreen()
                        + (255 - color.getGreen()) * ratio
        );

        int blue = (int) (
                color.getBlue()
                        + (255 - color.getBlue()) * ratio
        );

        return new Color(
                Math.min(red, 255),
                Math.min(green, 255),
                Math.min(blue, 255)
        );
    }
}