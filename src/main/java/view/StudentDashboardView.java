package view;

import net.miginfocom.swing.MigLayout;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

public class StudentDashboardView extends JPanel {

    public StudentDashboardView() {
        setLayout(
                new MigLayout(
                        "fill, wrap 1, insets 20",
                        "[grow, fill]",
                        "[]20[]20[grow, fill]"
                )
        );

        setBackground(UIConstants.BACKGROUND);

        JLabel title = new JLabel(
                "Tổng quan học viên"
        );

        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(UIConstants.TEXT_PRIMARY);

        add(title);

        JPanel cards = new JPanel(
                new GridLayout(1, 3, 20, 0)
        );

        cards.setOpaque(false);

        cards.add(
                createCard(
                        "Khóa học",
                        "6",
                        new Color(59, 130, 246)
                )
        );

        cards.add(
                createCard(
                        "Lớp đã đăng ký",
                        "4",
                        new Color(16, 185, 129)
                )
        );

        cards.add(
                createCard(
                        "Tiến độ",
                        "75%",
                        new Color(139, 92, 246)
                )
        );

        add(cards, "growx");

        JTextArea activity = new JTextArea();

        activity.setEditable(false);
        activity.setLineWrap(true);
        activity.setWrapStyleWord(true);

        activity.setFont(UIConstants.FONT_MEDIUM);
        activity.setForeground(UIConstants.TEXT_PRIMARY);
        activity.setBackground(Color.WHITE);

        activity.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        20,
                        18,
                        20
                )
        );

        activity.setText(
                """
                Hoạt động gần đây

                • Đăng ký khóa Java Core

                • Hoàn thành bài kiểm tra 1

                • Đã thanh toán học phí

                • Giảng viên cập nhật tài liệu
                """
        );

        JScrollPane scrollPane =
                new JScrollPane(activity);

        scrollPane.setPreferredSize(
                new Dimension(0, 220)
        );

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        UIConstants.BORDER
                )
        );

        scrollPane.getViewport().setBackground(
                Color.WHITE
        );

        add(scrollPane, "grow, push");
    }

    private JPanel createCard(
            String title,
            String value,
            Color color
    ) {
        JPanel panel = new JPanel(
                new BorderLayout(0, 10)
        );

        panel.setBackground(Color.WHITE);

        panel.putClientProperty(
                "FlatLaf.style",
                "arc: 18"
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                UIConstants.BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                20,
                                20,
                                20,
                                20
                        )
                )
        );

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
                UIConstants.FONT_MEDIUM
        );

        titleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        JLabel valueLabel = new JLabel(value);

        valueLabel.setFont(
                UIConstants.FONT_TITLE
        );

        valueLabel.setForeground(color);

        panel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        panel.add(
                valueLabel,
                BorderLayout.CENTER
        );

        return panel;
    }
}