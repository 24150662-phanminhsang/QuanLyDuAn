package view;

import controller.TeacherController;
import model.Teacher;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class TeacherProfileView extends JPanel {
    private JTextField txtCode, txtName, txtEmail, txtPhone, txtSpecialization;
    private JButton btnUpdate;
    private final TeacherController teacherController = new TeacherController();

    public TeacherProfileView() {
        setLayout(new MigLayout("fill, insets 15", "[320pt][grow]", "[][grow]"));

        JLabel lblTitle = new JLabel("HỒ SƠ CÁ NHÂN GIẢNG VIÊN");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(lblTitle, "span 2, wrap 15");

        JPanel formPanel = new JPanel(new MigLayout("wrap 2", "[right][grow, fill]", "[]10[]"));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết"));

        formPanel.add(new JLabel("Mã Giảng Viên:"));
        txtCode = new JTextField();
        txtCode.setEditable(false);
        formPanel.add(txtCode);

        formPanel.add(new JLabel("Họ và Tên:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Số Điện Thoại:"));
        txtPhone = new JTextField();
        formPanel.add(txtPhone);

        formPanel.add(new JLabel("Chuyên Môn:"));
        txtSpecialization = new JTextField();
        formPanel.add(txtSpecialization);

        btnUpdate = new JButton("Cập Nhật Thông Tin");
        btnUpdate.putClientProperty("JButton.buttonType", "accent");
        formPanel.add(btnUpdate, "span 2, growx, gaptop 15");

        add(formPanel, "top");

        btnUpdate.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Tính năng cập nhật thông tin cá nhân đang được phát triển!");
        });
    }
}