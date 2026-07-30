package view.components;

import model.Course;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Locale;

public class PublicCourseCard extends JPanel {

    private static final Color PRIMARY =
            new Color(37, 99, 235);

    private static final Color TEXT_DARK =
            new Color(15, 35, 70);

    private static final Color TEXT_GRAY =
            new Color(91, 105, 129);

    private final Course course;

    public PublicCourseCard(
            Course course,
            Runnable detailAction
    ) {
        this.course = course;

        initializeUI(detailAction);
    }

    private void initializeUI(
            Runnable detailAction
    ) {
        setLayout(new BorderLayout());
        setOpaque(false);

        setBorder(
                new EmptyBorder(
                        1,
                        1,
                        7,
                        1
                )
        );

        setPreferredSize(
                new Dimension(230, 260)
        );

        add(
                createImageArea(),
                BorderLayout.NORTH
        );

        add(
                createContent(detailAction),
                BorderLayout.CENTER
        );

        setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent event
                    ) {
                        if (detailAction != null) {
                            detailAction.run();
                        }
                    }

                    @Override
                    public void mouseEntered(
                            MouseEvent event
                    ) {
                        putClientProperty(
                                "hover",
                                true
                        );

                        repaint();
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent event
                    ) {
                        putClientProperty(
                                "hover",
                                false
                        );

                        repaint();
                    }
                }
        );
    }

    private JPanel createImageArea() {
        JPanel imageArea =
                new JPanel(new GridBagLayout());

        imageArea.setOpaque(false);

        imageArea.setPreferredSize(
                new Dimension(230, 105)
        );

        JLabel codeLabel =
                new JLabel(
                        safeText(
                                course.getCourseCode(),
                                "COURSE"
                        )
                );

        codeLabel.setForeground(PRIMARY);

        codeLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        imageArea.add(codeLabel);

        return imageArea;
    }

    private JPanel createContent(
            Runnable detailAction
    ) {
        JPanel content = new JPanel();

        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS
                )
        );

        content.setOpaque(false);

        content.setBorder(
                new EmptyBorder(
                        12,
                        14,
                        12,
                        14
                )
        );

        JLabel nameLabel =
                new JLabel(
                        "<html>"
                                + "<div style='width:195px;'>"
                                + safeText(
                                course.getCourseName(),
                                "Khóa học"
                        )
                                + "</div>"
                                + "</html>"
                );

        nameLabel.setForeground(TEXT_DARK);

        nameLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        nameLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel descriptionLabel =
                new JLabel(
                        "<html>"
                                + "<div style='width:195px;'>"
                                + shortenDescription(
                                course.getDescription()
                        )
                                + "</div>"
                                + "</html>"
                );

        descriptionLabel.setForeground(TEXT_GRAY);

        descriptionLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        descriptionLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JPanel information =
                new JPanel(new BorderLayout());

        information.setOpaque(false);

        information.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        28
                )
        );

        information.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel creditLabel =
                new JLabel(
                        course.getCredits()
                                + " tín chỉ"
                );

        creditLabel.setForeground(TEXT_GRAY);

        creditLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        JLabel priceLabel =
                new JLabel(
                        formatMoney()
                );

        priceLabel.setForeground(PRIMARY);

        priceLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        information.add(
                creditLabel,
                BorderLayout.WEST
        );

        information.add(
                priceLabel,
                BorderLayout.EAST
        );

        JButton detailButton =
                new JButton("Xem chi tiết");

        detailButton.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        detailButton.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        32
                )
        );

        detailButton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        detailButton.setForeground(PRIMARY);

        detailButton.setBackground(Color.WHITE);

        detailButton.setFocusPainted(false);

        detailButton.setBorder(
                BorderFactory.createLineBorder(
                        new Color(191, 219, 254)
                )
        );

        detailButton.addActionListener(event -> {
            if (detailAction != null) {
                detailAction.run();
            }
        });

        content.add(nameLabel);
        content.add(Box.createVerticalStrut(7));
        content.add(descriptionLabel);
        content.add(Box.createVerticalGlue());
        content.add(information);
        content.add(Box.createVerticalStrut(7));
        content.add(detailButton);

        return content;
    }

    private String shortenDescription(
            String description
    ) {
        if (
                description == null
                        || description.isBlank()
        ) {
            return "Thông tin khóa học đang được cập nhật.";
        }

        String normalized =
                description.trim();

        if (normalized.length() <= 95) {
            return normalized;
        }

        return normalized.substring(0, 92)
                + "...";
    }

    private String formatMoney() {
        if (course.getTuitionFee() == null) {
            return "Liên hệ";
        }

        NumberFormat formatter =
                NumberFormat.getNumberInstance(
                        new Locale("vi", "VN")
                );

        return formatter.format(
                course.getTuitionFee()
        ) + "đ";
    }

    private String safeText(
            String value,
            String defaultValue
    ) {
        if (
                value == null
                        || value.isBlank()
        ) {
            return defaultValue;
        }

        return value.trim();
    }

    @Override
    protected void paintComponent(
            Graphics graphics
    ) {
        Graphics2D graphics2D =
                (Graphics2D) graphics.create();

        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        boolean hovered =
                Boolean.TRUE.equals(
                        getClientProperty("hover")
                );

        if (hovered) {
            graphics2D.setColor(
                    new Color(37, 99, 235, 25)
            );
        } else {
            graphics2D.setColor(
                    new Color(15, 23, 42, 18)
            );
        }

        graphics2D.fillRoundRect(
                3,
                4,
                getWidth() - 6,
                getHeight() - 7,
                16,
                16
        );

        graphics2D.setColor(Color.WHITE);

        graphics2D.fillRoundRect(
                1,
                1,
                getWidth() - 4,
                getHeight() - 6,
                16,
                16
        );

        graphics2D.setColor(
                new Color(225, 235, 252)
        );

        graphics2D.fillRoundRect(
                1,
                1,
                getWidth() - 4,
                108,
                16,
                16
        );

        graphics2D.fillRect(
                1,
                80,
                getWidth() - 4,
                28
        );

        if (hovered) {
            graphics2D.setColor(
                    new Color(147, 197, 253)
            );

            graphics2D.drawRoundRect(
                    1,
                    1,
                    getWidth() - 4,
                    getHeight() - 6,
                    16,
                    16
            );
        }

        graphics2D.dispose();

        super.paintComponent(graphics);
    }
}