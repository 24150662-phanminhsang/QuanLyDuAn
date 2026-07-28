package view.components;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SidebarButton extends JButton {

    private static final Color PENDING_COLOR =
            new Color(148, 163, 184);

    private boolean menuSelected;
    private boolean pending;

    public SidebarButton(
            String text,
            Ikon icon
    ) {
        super(text);

        setIcon(
                FontIcon.of(
                        icon,
                        15,
                        Color.WHITE
                )
        );

        setFont(UIConstants.FONT_MEDIUM);
        setForeground(Color.WHITE);

        setHorizontalAlignment(
                SwingConstants.LEFT
        );

        setHorizontalTextPosition(
                SwingConstants.RIGHT
        );

        setIconTextGap(10);

        /*
         * Chỉ cố định chiều cao.
         * Chiều rộng tự khớp với sidebar.
         */
        setPreferredSize(
                new Dimension(100, 38)
        );

        setMinimumSize(
                new Dimension(100, 38)
        );

        setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        38
                )
        );

        setFocusable(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setOpaque(true);

        putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                borderWidth: 0;
                margin: 5,9,5,9;
                """
        );

        setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent event
                    ) {
                        if (
                                !pending
                                        && !menuSelected
                        ) {
                            setBackground(
                                    UIConstants.SIDEBAR_HOVER
                            );
                        }
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent event
                    ) {
                        updateAppearance();
                    }
                }
        );

        updateAppearance();
    }

    public void setMenuSelected(
            boolean selected
    ) {
        menuSelected = selected;
        updateAppearance();
    }

    public void setPending(boolean pending) {
        this.pending = pending;

        if (pending) {
            setToolTipText(
                    "Chức năng đang được phát triển"
            );

            setCursor(
                    Cursor.getDefaultCursor()
            );
        } else {
            setToolTipText(null);

            setCursor(
                    Cursor.getPredefinedCursor(
                            Cursor.HAND_CURSOR
                    )
            );
        }

        updateAppearance();
    }

    public boolean isPending() {
        return pending;
    }

    private void updateAppearance() {
        Color iconColor;

        if (pending) {
            setBackground(
                    UIConstants.SIDEBAR
            );

            setForeground(
                    PENDING_COLOR
            );

            iconColor = PENDING_COLOR;

        } else if (menuSelected) {
            setBackground(
                    UIConstants.PRIMARY
            );

            setForeground(Color.WHITE);
            iconColor = Color.WHITE;

        } else {
            setBackground(
                    UIConstants.SIDEBAR
            );

            setForeground(Color.WHITE);
            iconColor = Color.WHITE;
        }

        if (
                getIcon()
                        instanceof FontIcon fontIcon
        ) {
            fontIcon.setIconColor(
                    iconColor
            );
        }
    }
}