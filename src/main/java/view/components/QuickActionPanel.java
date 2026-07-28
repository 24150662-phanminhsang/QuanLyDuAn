package view.components;

import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
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
                        "fill, wrap 1, insets 16",
                        "[grow, fill]",
                        "[]10[grow, fill]"
                )
        );

        setMinimumSize(
                new Dimension(320, 205)
        );

        setPreferredSize(
                new Dimension(520, 220)
        );

        JLabel titleLabel =
                new JLabel("Thao tác nhanh");

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
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
                        "Thêm tài khoản",
                        FontAwesomeSolid.USERS,
                        UIConstants.PRIMARY,
                        "USERS"
                ),
                "grow"
        );

        actionsPanel.add(
                createActionButton(
                        "Thêm khóa học",
                        FontAwesomeSolid.BOOK,
                        UIConstants.SUCCESS,
                        "COURSES"
                ),
                "grow"
        );

        actionsPanel.add(
                createActionButton(
                        "Thêm lớp học",
                        FontAwesomeSolid.SCHOOL,
                        UIConstants.PURPLE,
                        "CLASSES"
                ),
                "grow"
        );

        actionsPanel.add(
                createActionButton(
                        "Thanh toán",
                        FontAwesomeSolid.CREDIT_CARD,
                        UIConstants.WARNING,
                        "PAYMENTS"
                ),
                "grow"
        );

        actionsPanel.add(
                createActionButton(
                        "Báo cáo",
                        FontAwesomeSolid.CHART_BAR,
                        UIConstants.PRIMARY,
                        "REPORTS"
                ),
                "grow"
        );

        actionsPanel.add(
                createActionButton(
                        "Tin nhắn",
                        FontAwesomeSolid.COMMENT,
                        UIConstants.DANGER,
                        "MESSAGES"
                ),
                "grow"
        );

        add(
                titleLabel,
                "growx"
        );

        add(
                actionsPanel,
                "grow, push"
        );
    }

    public void setActionHandler(
            Consumer<String> actionHandler
    ) {
        if (actionHandler != null) {
            this.actionHandler =
                    actionHandler;
        }
    }

    private JButton createActionButton(
            String title,
            Ikon icon,
            Color iconColor,
            String actionKey
    ) {
        JButton button =
                new JButton(title);

        button.setIcon(
                FontIcon.of(
                        icon,
                        19,
                        iconColor
                )
        );

        button.setFont(
                UIConstants.FONT_SMALL
                        .deriveFont(
                                java.awt.Font.BOLD
                        )
        );

        button.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        button.setBackground(Color.WHITE);

        button.setVerticalTextPosition(
                SwingConstants.BOTTOM
        );

        button.setHorizontalTextPosition(
                SwingConstants.CENTER
        );

        button.setIconTextGap(5);

        button.setMinimumSize(
                new Dimension(90, 66)
        );

        button.setPreferredSize(
                new Dimension(115, 70)
        );

        button.setFocusable(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.putClientProperty(
                "FlatLaf.style",
                """
                arc: 11;
                borderColor: #E2E8F0;
                borderWidth: 1;
                focusWidth: 1;
                margin: 6,6,6,6;
                """
        );

        button.addActionListener(
                event ->
                        actionHandler.accept(
                                actionKey
                        )
        );

        return button;
    }
}