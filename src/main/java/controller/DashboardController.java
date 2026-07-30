package controller;

import model.Course;
import view.CourseManagementView;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.List;

public class DashboardController {

    private final CourseManagementView courseView;
    private final CourseController courseController;

    public DashboardController(
            CourseManagementView courseView
    ) {
        this.courseView = courseView;
        this.courseController = new CourseController();

        registerEvents();
        loadCourses();
    }

    private void registerEvents() {
        courseView.getBtnAdd().addActionListener(
                event -> addCourse()
        );

        courseView.getBtnUpdate().addActionListener(
                event -> updateCourse()
        );

        courseView.getBtnDelete().addActionListener(
                event -> deleteCourse()
        );

        courseView.getBtnRefresh().addActionListener(
                event -> loadCourses()
        );
    }

    private void loadCourses() {
        try {
            List<Course> courses =
                    courseController.getAllCourses();

            DefaultTableModel model =
                    courseView.getTableModel();

            model.setRowCount(0);

            for (Course course : courses) {
                model.addRow(
                        new Object[]{
                                course.getCourseId(),
                                course.getCourseCode(),
                                course.getCourseName(),
                                course.getDescription(),
                                course.getCredits(),
                                course.getTuitionFee(),
                                course.getStatus()
                        }
                );
            }

            courseView.clearSelection();

        } catch (RuntimeException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void addCourse() {
        Course course = showCourseForm(
                null,
                "Thêm khóa học"
        );

        if (course == null) {
            return;
        }

        try {
            boolean success =
                    courseController.addCourse(course);

            if (success) {
                JOptionPane.showMessageDialog(
                        courseView,
                        "Thêm khóa học thành công.",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadCourses();
            } else {
                showError(
                        "Không thể thêm khóa học."
                );
            }

        } catch (IllegalArgumentException exception) {
            showError(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void updateCourse() {
        int courseId =
                courseView.getSelectedCourseId();

        if (courseId <= 0) {
            showWarning(
                    "Vui lòng chọn khóa học cần cập nhật."
            );
            return;
        }

        try {
            Course currentCourse =
                    courseController.getCourseById(
                            courseId
                    );

            if (currentCourse == null) {
                showError(
                        "Không tìm thấy khóa học."
                );
                return;
            }

            Course updatedCourse =
                    showCourseForm(
                            currentCourse,
                            "Cập nhật khóa học"
                    );

            if (updatedCourse == null) {
                return;
            }

            updatedCourse.setCourseId(courseId);

            boolean success =
                    courseController.updateCourse(
                            updatedCourse
                    );

            if (success) {
                JOptionPane.showMessageDialog(
                        courseView,
                        "Cập nhật khóa học thành công.",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadCourses();
            } else {
                showError(
                        "Không thể cập nhật khóa học."
                );
            }

        } catch (IllegalArgumentException exception) {
            showError(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void deleteCourse() {
        int courseId =
                courseView.getSelectedCourseId();

        if (courseId <= 0) {
            showWarning(
                    "Vui lòng chọn khóa học cần xóa."
            );
            return;
        }

        int confirm =
                JOptionPane.showConfirmDialog(
                        courseView,
                        "Bạn có chắc muốn xóa khóa học này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean success =
                    courseController.deleteCourse(
                            courseId
                    );

            if (success) {
                JOptionPane.showMessageDialog(
                        courseView,
                        "Xóa khóa học thành công.",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadCourses();
            } else {
                showError(
                        "Không thể xóa khóa học."
                );
            }

        } catch (RuntimeException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private Course showCourseForm(
            Course course,
            String title
    ) {
        String currentCode =
                course == null
                        ? ""
                        : course.getCourseCode();

        String currentName =
                course == null
                        ? ""
                        : course.getCourseName();

        String currentDescription =
                course == null
                        ? ""
                        : course.getDescription();

        String currentCredits =
                course == null
                        ? ""
                        : String.valueOf(
                        course.getCredits()
                );

        String currentFee =
                course == null
                        ? ""
                        : course.getTuitionFee()
                        .toPlainString();

        String currentStatus =
                course == null
                        ? "ACTIVE"
                        : course.getStatus();

        String courseCode =
                JOptionPane.showInputDialog(
                        courseView,
                        "Mã khóa học:",
                        currentCode
                );

        if (courseCode == null) {
            return null;
        }

        String courseName =
                JOptionPane.showInputDialog(
                        courseView,
                        "Tên khóa học:",
                        currentName
                );

        if (courseName == null) {
            return null;
        }

        String description =
                JOptionPane.showInputDialog(
                        courseView,
                        "Mô tả:",
                        currentDescription
                );

        if (description == null) {
            return null;
        }

        String creditsText =
                JOptionPane.showInputDialog(
                        courseView,
                        "Số tín chỉ:",
                        currentCredits
                );

        if (creditsText == null) {
            return null;
        }

        String feeText =
                JOptionPane.showInputDialog(
                        courseView,
                        "Học phí:",
                        currentFee
                );

        if (feeText == null) {
            return null;
        }

        String status =
                JOptionPane.showInputDialog(
                        courseView,
                        "Trạng thái ACTIVE hoặc INACTIVE:",
                        currentStatus
                );

        if (status == null) {
            return null;
        }

        try {
            int credits =
                    Integer.parseInt(
                            creditsText.trim()
                    );

            BigDecimal tuitionFee =
                    new BigDecimal(
                            feeText.trim()
                    );

            return new Course(
                    courseCode,
                    courseName,
                    description,
                    credits,
                    tuitionFee,
                    status
            );

        } catch (NumberFormatException exception) {
            showError(
                    "Tín chỉ hoặc học phí không đúng định dạng."
            );

            return null;
        }
    }

    private void showWarning(
            String message
    ) {
        JOptionPane.showMessageDialog(
                courseView,
                message,
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showError(
            String message
    ) {
        JOptionPane.showMessageDialog(
                courseView,
                message,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );
    }
}