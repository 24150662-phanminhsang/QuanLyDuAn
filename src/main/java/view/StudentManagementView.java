package view;

import controller.StudentController;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * StudentManagementView
 */
public class StudentManagementView extends JFrame {
    private JTextField txtID, txtName, txtGender, txtPhone, txtEmail, txtAddress;

    private JTable table;

    private DefaultTableModel model;

    private StudentController controller;

    public StudentManagementView() {

        controller = new StudentController();

        setTitle("Student Management");
        setSize(900,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6,2,5,5));

        panel.add(new JLabel("Student ID"));
        txtID = new JTextField();
        panel.add(txtID);

        panel.add(new JLabel("Full Name"));
        txtName = new JTextField();
        panel.add(txtName);

        panel.add(new JLabel("Gender"));
        txtGender = new JTextField();
        panel.add(txtGender);

        panel.add(new JLabel("Phone"));
        txtPhone = new JTextField();
        panel.add(txtPhone);

        panel.add(new JLabel("Email"));
        txtEmail = new JTextField();
        panel.add(txtEmail);

        panel.add(new JLabel("Address"));
        txtAddress = new JTextField();
        panel.add(txtAddress);

        JPanel buttonPanel = new JPanel();

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Gender");
        model.addColumn("Phone");
        model.addColumn("Email");
        model.addColumn("Address");

        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        add(panel,BorderLayout.NORTH);
        add(scrollPane,BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);

        setVisible(true);

        // Thêm sinh viên
        btnAdd.addActionListener(e->{

            Student student = new Student();

            student.setFullName(txtName.getText());
            student.setGender(txtGender.getText());
            student.setPhone(txtPhone.getText());
            student.setEmail(txtEmail.getText());
            student.setAddress(txtAddress.getText());

            if(controller.addStudent(student))
                JOptionPane.showMessageDialog(this,"Thêm thành công");
            else
                JOptionPane.showMessageDialog(this,"Thêm thất bại");

        });

    }


}
