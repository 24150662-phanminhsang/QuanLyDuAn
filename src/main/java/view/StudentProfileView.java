package view;
import javax.swing.*;
import java.awt.*;

/**
 * StudentProfileView
 */
public class StudentProfileView  extends JFrame {
    public StudentProfileView() {

        setTitle("Student Profile");

        setSize(450,350);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6,2,5,5));

        panel.add(new JLabel("Student ID"));
        panel.add(new JTextField());

        panel.add(new JLabel("Full Name"));
        panel.add(new JTextField());

        panel.add(new JLabel("Gender"));
        panel.add(new JTextField());

        panel.add(new JLabel("Phone"));
        panel.add(new JTextField());

        panel.add(new JLabel("Email"));
        panel.add(new JTextField());

        panel.add(new JLabel("Address"));
        panel.add(new JTextField());

        JButton btnUpdate = new JButton("Update Information");

        add(panel,BorderLayout.CENTER);
        add(btnUpdate,BorderLayout.SOUTH);

        setVisible(true);
    }

}
