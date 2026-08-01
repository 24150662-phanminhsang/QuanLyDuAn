package view.components.student;

import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;
import view.components.ContentCard;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class UpcomingAssignmentPanel extends ContentCard {

    private final JPanel assignmentListPanel;

    private List<AssignmentItem> assignments =
            new ArrayList<>();

    private Consumer<Integer> assignmentActionHandler =
            assignmentId -> {
            };

    public UpcomingAssignmentPanel() {
        assignmentListPanel = new JPanel();
        assignmentListPanel.setOpaque(false);

        initializeView();
        refreshAssignments();
    }

    private void initializeView() {
        setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 18 20",
                        "[grow, fill]",
                        "[]14[]"
                )
        );

        setMinimumSize(
                new Dimension(420, 250)
        );

        setPreferredSize(
                new Dimension(900, 320)
        );

        add(
                createHeaderPanel(),
                "growx"
        );

        assignmentListPanel.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 0",
                        "[grow, fill]",
                        "[]10[]10[]"
                )
        );

        add(
                assignmentListPanel,
                "growx"
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
                        FontAwesomeSolid.FILE_ALT,
                        16,
                        UIConstants.DANGER
                )
        );

        JLabel titleLabel = new JLabel(
                "Bài tập sắp đến hạn"
        );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel subtitleLabel = new JLabel(
                "Cần hoàn thành"
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
                "gapleft 7"
        );

        panel.add(
                subtitleLabel,
                "alignx right"
        );

        return panel;
    }

    public void setAssignmentActionHandler(
            Consumer<Integer> handler
    ) {
        assignmentActionHandler =
                handler == null
                        ? assignmentId -> {
                }
                        : handler;
    }

    public void setAssignments(
            List<AssignmentItem> items
    ) {
        assignments =
                items == null
                        ? new ArrayList<>()
                        : new ArrayList<>(items);

        refreshAssignments();
    }

    public void addAssignment(
            AssignmentItem assignment
    ) {
        if (assignment == null) {
            return;
        }

        assignments.add(assignment);
        refreshAssignments();
    }

    public void clearAssignments() {
        assignments.clear();
        refreshAssignments();
    }

    public List<AssignmentItem> getAssignments() {
        return Collections.unmodifiableList(
                assignments
        );
    }

    public void refreshAssignments() {
        assignmentListPanel.removeAll();

        if (assignments.isEmpty()) {
            assignmentListPanel.add(
                    createEmptyState(),
                    "growx"
            );
        } else {
            for (AssignmentItem assignment
                    : assignments) {

                assignmentListPanel.add(
                        createAssignmentRow(
                                assignment
                        ),
                        "growx"
                );
            }
        }

        assignmentListPanel.revalidate();
        assignmentListPanel.repaint();
    }

    private JPanel createAssignmentRow(
            AssignmentItem assignment
    ) {
        JPanel row = new JPanel(
                new MigLayout(
                        "fillx, insets 11 12",
                        "[40!]12[grow, fill]12[84!]",
                        "[]4[]"
                )
        );

        row.setBackground(
                UIConstants.BACKGROUND
        );

        row.putClientProperty(
                "FlatLaf.style",
                "arc: 12; borderWidth: 0"
        );

        JPanel iconPanel =
                createAssignmentIconPanel(
                        assignment.dueDate()
                );

        JLabel assignmentLabel = new JLabel(
                safeText(
                        assignment.assignmentName(),
                        "Bài tập"
                )
        );

        assignmentLabel.setFont(
                UIConstants.FONT_MEDIUM
        );

        assignmentLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        assignmentLabel.setToolTipText(
                assignmentLabel.getText()
        );

        JLabel deadlineLabel = new JLabel(
                safeText(
                        assignment.courseName(),
                        "Khóa học"
                )
                        + "  •  "
                        + formatDeadline(
                        assignment.dueDate()
                )
        );

        deadlineLabel.setFont(
                UIConstants.FONT_SMALL
        );

        deadlineLabel.setForeground(
                getDeadlineColor(
                        assignment.dueDate()
                )
        );

        deadlineLabel.setToolTipText(
                deadlineLabel.getText()
        );

        JButton detailButton =
                createDetailButton(
                        assignment.assignmentId()
                );

        row.add(
                iconPanel,
                "cell 0 0 1 2, alignx center, aligny center"
        );

        row.add(
                assignmentLabel,
                "cell 1 0, growx"
        );

        row.add(
                deadlineLabel,
                "cell 1 1, growx"
        );

        row.add(
                detailButton,
                "cell 2 0 1 2, alignx right, aligny center"
        );

        return row;
    }

    private JPanel createAssignmentIconPanel(
            LocalDate dueDate
    ) {
        boolean urgent =
                getRemainingDays(dueDate) <= 2;

        JPanel panel = new JPanel(
                new MigLayout(
                        "fill, insets 0",
                        "[center]",
                        "[center]"
                )
        );

        panel.setPreferredSize(
                new Dimension(40, 40)
        );

        panel.setMinimumSize(
                new Dimension(40, 40)
        );

        panel.setMaximumSize(
                new Dimension(40, 40)
        );

        panel.setBackground(
                urgent
                        ? UIConstants.DANGER_LIGHT
                        : UIConstants.WARNING_LIGHT
        );

        panel.putClientProperty(
                "FlatLaf.style",
                "arc: 10; borderWidth: 0"
        );

        panel.add(
                new JLabel(
                        FontIcon.of(
                                FontAwesomeSolid.FILE_ALT,
                                15,
                                urgent
                                        ? UIConstants.DANGER
                                        : UIConstants.WARNING
                        )
                )
        );

        return panel;
    }

    private JButton createDetailButton(
            int assignmentId
    ) {
        JButton button = new JButton(
                "Chi tiết"
        );

        button.setFont(
                UIConstants.FONT_SMALL.deriveFont(
                        Font.BOLD
                )
        );

        button.setForeground(
                UIConstants.PRIMARY
        );

        button.setBackground(
                UIConstants.PRIMARY_LIGHT
        );

        button.setFocusable(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setPreferredSize(
                new Dimension(84, 34)
        );

        button.setMinimumSize(
                new Dimension(84, 34)
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        7,
                        10,
                        7,
                        10
                )
        );

        button.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                borderWidth: 0;
                focusWidth: 0;
                """
        );

        button.addActionListener(
                event ->
                        assignmentActionHandler.accept(
                                assignmentId
                        )
        );

        return button;
    }

    private JPanel createEmptyState() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fill, wrap 1, insets 28",
                        "[center]",
                        "[]10[]"
                )
        );

        panel.setOpaque(false);

        panel.add(
                new JLabel(
                        FontIcon.of(
                                FontAwesomeSolid.CHECK_CIRCLE,
                                28,
                                UIConstants.SUCCESS
                        )
                )
        );

        JLabel messageLabel = new JLabel(
                "Không có bài tập sắp đến hạn"
        );

        messageLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        messageLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(messageLabel);

        return panel;
    }

    private String formatDeadline(
            LocalDate dueDate
    ) {
        if (dueDate == null) {
            return "Chưa có hạn nộp";
        }

        long remainingDays =
                getRemainingDays(dueDate);

        if (remainingDays < 0) {
            return "Đã quá hạn";
        }

        if (remainingDays == 0) {
            return "Hạn nộp hôm nay";
        }

        if (remainingDays == 1) {
            return "Còn 1 ngày";
        }

        return "Còn "
                + remainingDays
                + " ngày - "
                + dueDate.format(
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy"
                )
        );
    }

    private long getRemainingDays(
            LocalDate dueDate
    ) {
        if (dueDate == null) {
            return Long.MAX_VALUE;
        }

        return ChronoUnit.DAYS.between(
                LocalDate.now(),
                dueDate
        );
    }

    private Color getDeadlineColor(
            LocalDate dueDate
    ) {
        long remainingDays =
                getRemainingDays(dueDate);

        if (remainingDays < 0) {
            return UIConstants.DANGER;
        }

        if (remainingDays <= 2) {
            return UIConstants.WARNING;
        }

        return UIConstants.TEXT_SECONDARY;
    }

    private String safeText(
            String value,
            String defaultValue
    ) {
        return value == null || value.isBlank()
                ? defaultValue
                : value.trim();
    }

    public record AssignmentItem(
            int assignmentId,
            String assignmentName,
            String courseName,
            LocalDate dueDate
    ) {
    }
}