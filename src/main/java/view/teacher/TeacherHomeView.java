package view.teacher;

import controller.ClassController;
import model.ClassRoom;
import net.miginfocom.swing.MigLayout;
import service.GradeService;
import util.UIConstants;
import view.components.ContentCard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class TeacherHomeView extends JPanel {

    private final int teacherId;

    private final ClassController classController;
    private final GradeService gradeService;

    private final JLabel totalClassLabel;
    private final JLabel totalStudentLabel;
    private final JLabel pendingGradeLabel;
    private final JLabel nextScheduleLabel;

    private final DefaultTableModel tableModel;
    private final JTable classTable;

    public TeacherHomeView(
            int teacherId
    ) {
        if (teacherId <= 0) {
            throw new IllegalArgumentException(
                    "ID giảng viên không hợp lệ."
            );
        }

        this.teacherId = teacherId;

        classController =
                new ClassController();

        gradeService =
                new GradeService();

        totalClassLabel =
                createValueLabel("0");

        totalStudentLabel =
                createValueLabel("0");

        pendingGradeLabel =
                createValueLabel("0");

        nextScheduleLabel =
                createValueLabel("--");

        tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "ID lớp",
                                "Tên lớp",
                                "Lịch học",
                                "Phòng",
                                "Sĩ số tối đa"
                        },
                        0
                ) {
                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        classTable =
                new JTable(tableModel);

        initializeView();
        loadData();
    }

    private void initializeView() {
        setLayout(
                new BorderLayout()
        );

        setBackground(
                UIConstants.BACKGROUND
        );

        JPanel wrapper =
                new JPanel(
                        new MigLayout(
                                "fill, wrap 1, insets 18",
                                "[grow, fill]",
                                "[]16[]16[grow, fill]"
                        )
                );

        wrapper.setOpaque(false);

        wrapper.add(
                createHeaderPanel(),
                "growx"
        );

        wrapper.add(
                createStatisticPanel(),
                "growx"
        );

        wrapper.add(
                createClassTableCard(),
                "grow, push"
        );

        add(
                wrapper,
                BorderLayout.CENTER
        );
    }

    private JPanel createHeaderPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow]",
                                "[][]"
                        )
                );

        panel.setOpaque(false);

        JLabel titleLabel =
                new JLabel(
                        "Tổng quan giảng dạy"
                );

        titleLabel.setFont(
                UIConstants.FONT_TITLE
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Theo dõi lớp học, lịch dạy và tình trạng nhập điểm."
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(
                titleLabel,
                "wrap"
        );

        panel.add(
                descriptionLabel
        );

        return panel;
    }

    private JPanel createStatisticPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow, fill]12[grow, fill]12[grow, fill]12[grow, fill]",
                                "[]"
                        )
                );

        panel.setOpaque(false);

        panel.add(
                createStatisticCard(
                        "Lớp đang phụ trách",
                        totalClassLabel,
                        "Tổng số lớp được phân công"
                ),
                "grow"
        );

        panel.add(
                createStatisticCard(
                        "Tổng học viên",
                        totalStudentLabel,
                        "Tổng học viên trong các lớp"
                ),
                "grow"
        );

        panel.add(
                createStatisticCard(
                        "Chưa đủ điểm",
                        pendingGradeLabel,
                        "Lớp còn thiếu dữ liệu điểm"
                ),
                "grow"
        );

        panel.add(
                createStatisticCard(
                        "Lịch dạy gần nhất",
                        nextScheduleLabel,
                        "Buổi dạy sắp tới"
                ),
                "grow"
        );

        return panel;
    }

    private ContentCard createStatisticCard(
            String title,
            JLabel valueLabel,
            String description
    ) {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 16",
                        "[grow]",
                        "[]8[]6[]"
                )
        );

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                UIConstants.FONT_MEDIUM
        );

        titleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        JLabel descriptionLabel =
                new JLabel(description);

        descriptionLabel.setFont(
                UIConstants.FONT_SMALL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        card.add(titleLabel);
        card.add(valueLabel);
        card.add(descriptionLabel);

        return card;
    }

    private JLabel createValueLabel(
            String text
    ) {
        JLabel label =
                new JLabel(text);

        label.setFont(
                UIConstants.FONT_TITLE
                        .deriveFont(
                                Font.BOLD,
                                24f
                        )
        );

        label.setForeground(
                UIConstants.PRIMARY
        );

        return label;
    }

    private ContentCard createClassTableCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fill, wrap 1, insets 18",
                        "[grow, fill]",
                        "[]8[grow, fill]"
                )
        );

        JLabel titleLabel =
                new JLabel(
                        "Lớp học đang phụ trách"
                );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Danh sách lớp được phân công cho giảng viên."
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        JPanel titlePanel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 1, insets 0",
                                "[grow]",
                                "[][]"
                        )
                );

        titlePanel.setOpaque(false);

        titlePanel.add(titleLabel);
        titlePanel.add(descriptionLabel);

        card.add(
                titlePanel,
                "growx"
        );

        configureTable();

        JScrollPane scrollPane =
                new JScrollPane(
                        classTable
                );

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        UIConstants.BORDER
                )
        );

        scrollPane.getViewport()
                .setBackground(Color.WHITE);

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);

        card.add(
                scrollPane,
                "grow, push"
        );

        return card;
    }

    private void configureTable() {
        classTable.setRowHeight(40);

        classTable.setFillsViewportHeight(true);

        classTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        classTable.setShowVerticalLines(false);
        classTable.setShowHorizontalLines(true);

        classTable.setGridColor(
                UIConstants.BORDER
        );

        classTable.setSelectionBackground(
                new Color(
                        239,
                        246,
                        255
                )
        );

        classTable.setSelectionForeground(
                UIConstants.TEXT_PRIMARY
        );

        classTable.getTableHeader()
                .setReorderingAllowed(false);

        classTable.getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                40
                        )
                );

        classTable.getTableHeader()
                .setFont(
                        UIConstants.FONT_MEDIUM
                                .deriveFont(
                                        Font.BOLD
                                )
                );
    }

    public void loadData() {
        List<ClassRoom> classes;

        try {
            classes =
                    classController
                            .getClassesByTeacherId(
                                    teacherId
                            );
        }
        catch (RuntimeException exception) {
            exception.printStackTrace();

            classes = Collections.emptyList();

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể tải dữ liệu tổng quan.\n\n"
                            + "Lỗi gốc: "
                            + getRootErrorMessage(exception),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
//        } catch (RuntimeException exception) {
//            classes =
//                    Collections.emptyList();
//
//            JOptionPane.showMessageDialog(
//                    this,
//                    "Không thể tải dữ liệu tổng quan.\n"
//                            + exception.getMessage(),
//                    "Lỗi",
//                    JOptionPane.ERROR_MESSAGE
//            );
//        }


        displayClasses(classes);
        updateStatistics(classes);
    }

    private void displayClasses(
            List<ClassRoom> classes
    ) {
        tableModel.setRowCount(0);

        for (ClassRoom classRoom : classes) {
            tableModel.addRow(
                    new Object[]{
                            classRoom.getClassId(),
                            classRoom.getClassName(),
                            safeText(
                                    classRoom.getSchedule()
                            ),
                            safeText(
                                    classRoom.getRoom()
                            ),
                            classRoom.getMaxStudents()
                    }
            );
        }
    }

    private void updateStatistics(
            List<ClassRoom> classes
    ) {
        totalClassLabel.setText(
                String.valueOf(
                        classes.size()
                )
        );

        /*
         * Hai chỉ số này tạm thời để 0.
         * Sau khi nối EnrollmentDAO và GradeDAO,
         * chúng ta sẽ tính bằng dữ liệu thật.
         */
        totalStudentLabel.setText("0");
        pendingGradeLabel.setText("0");

        String nearestSchedule =
                classes.stream()
                        .map(ClassRoom::getSchedule)
                        .filter(
                                schedule ->
                                        schedule != null
                                                && !schedule.isBlank()
                        )
                        .findFirst()
                        .orElse("--");

        nextScheduleLabel.setText(
                nearestSchedule
        );
    }

    private String safeText(
            String value
    ) {
        return value == null
                || value.isBlank()
                ? "--"
                : value.trim();
    }

    private String getRootErrorMessage(
            Throwable throwable
    ) {
        if (throwable == null) {
            return "Không xác định";
        }

        Throwable current = throwable;

        while (current.getCause() != null) {
            current = current.getCause();
        }

        if (current.getMessage() == null
                || current.getMessage().isBlank()) {
            return current.getClass().getSimpleName();
        }

        return current.getMessage();
    }
}