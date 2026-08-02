package view.teacher;

import model.GradeManagementMode;
import view.GradeManagementView;

import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * Màn hình quản lý điểm dành cho giảng viên.
 *
 * File này chỉ đóng vai trò wrapper, toàn bộ nghiệp vụ
 * vẫn được dùng chung từ GradeManagementView.
 */
public class TeacherGradeView extends JPanel {

    private final int teacherId;
    private final GradeManagementView gradeManagementView;

    public TeacherGradeView(int teacherId) {
        if (teacherId <= 0) {
            throw new IllegalArgumentException(
                    "ID giảng viên không hợp lệ."
            );
        }

        this.teacherId = teacherId;

        setLayout(new BorderLayout());

        gradeManagementView =
                new GradeManagementView(
                        GradeManagementMode.TEACHER,
                        teacherId
                );

        add(
                gradeManagementView,
                BorderLayout.CENTER
        );
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void refreshData() {
        revalidate();
        repaint();
    }
}