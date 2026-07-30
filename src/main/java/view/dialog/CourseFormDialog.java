package view.dialog;

import model.Course;
import net.miginfocom.swing.MigLayout;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class CourseFormDialog extends JDialog {

    private final boolean createMode;

    private JTextField txtCode;
    private JTextField txtName;
    private JTextArea txtDescription;
    private JSpinner spCredits;
    private JTextField txtFee;
    private JComboBox<String> cboStatus;

    private boolean confirmed;
    private Course course;

    public CourseFormDialog(Window owner, Course course) {
        super(owner);

        this.course = course;
        this.createMode = course == null;

        initialize();
    }

    private void initialize() {

        setTitle(createMode ?
                "Thêm khóa học"
                :
                "Cập nhật khóa học");

        setModal(true);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(
                new MigLayout(
                        "fill,wrap 1,insets 0",
                        "[grow]",
                        "[][][]"
                )
        );

        root.setBackground(Color.WHITE);

        root.add(createHeader(), "growx");
        root.add(createContent(), "grow");
        root.add(createFooter(), "growx");

        setContentPane(root);

        setSize(650,520);
        setLocationRelativeTo(getOwner());
    }

    private JPanel createHeader(){

        JPanel panel=new JPanel(
                new MigLayout(
                        "wrap 1,insets 22 28 18 28",
                        "[grow]"
                )
        );

        panel.setBackground(Color.WHITE);

        panel.setBorder(
                BorderFactory.createMatteBorder(
                        0,0,1,0,
                        UIConstants.BORDER
                )
        );

        JLabel lblTitle=new JLabel(
                createMode ?
                        "Tạo khóa học mới"
                        :
                        "Cập nhật khóa học"
        );

        lblTitle.setFont(
                UIConstants.FONT_HEADING
        );

        lblTitle.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel lblSub=new JLabel(
                "Nhập đầy đủ thông tin khóa học."
        );

        lblSub.setFont(
                UIConstants.FONT_NORMAL
        );

        lblSub.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(lblTitle);
        panel.add(lblSub);

        return panel;
    }

    private JPanel createContent(){

        JPanel panel=new JPanel(
                new MigLayout(
                        "wrap 2,fillx,insets 22 32 18 32",
                        "[right,140!][grow,fill]"
                )
        );

        panel.setBackground(Color.WHITE);

        txtCode=new JTextField();
        txtName=new JTextField();

        txtDescription=new JTextArea(4,20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        JScrollPane scroll=new JScrollPane(
                txtDescription
        );

        spCredits=new JSpinner(
                new SpinnerNumberModel(
                        1,
                        1,
                        20,
                        1
                )
        );

        txtFee=new JTextField();

        cboStatus=new JComboBox<>(
                new String[]{
                        "ACTIVE",
                        "INACTIVE"
                }
        );

        config(txtCode);
        config(txtName);
        config(txtFee);

        configCombo(cboStatus);

        panel.add(label("Mã khóa học"));
        panel.add(txtCode,"h 40!");

        panel.add(label("Tên khóa học"));
        panel.add(txtName,"h 40!");

        panel.add(label("Mô tả"));
        panel.add(scroll,"h 90!");

        panel.add(label("Tín chỉ"));
        panel.add(spCredits,"h 40!");

        panel.add(label("Học phí"));
        panel.add(txtFee,"h 40!");

        panel.add(label("Trạng thái"));
        panel.add(cboStatus,"h 40!");

        if(!createMode){
            loadCourse();
        }

        return panel;
    }
    private void config(JTextField txt){

        txt.setFont(UIConstants.FONT_NORMAL);

        txt.putClientProperty(
                "FlatLaf.style",
                """
                arc:10;
                margin:7,10,7,10;
                borderColor:#CBD5E1;
                focusedBorderColor:#2563EB;
                """
        );
    }

    private void configCombo(JComboBox<?> combo){

        combo.setFont(UIConstants.FONT_NORMAL);

        combo.putClientProperty(
                "FlatLaf.style",
                """
                arc:10;
                borderColor:#CBD5E1;
                focusedBorderColor:#2563EB;
                """
        );
    }

    private JLabel label(String text){

        JLabel lbl=new JLabel(text + ":");

        lbl.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        lbl.setFont(
                UIConstants.FONT_MEDIUM
        );

        lbl.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        return lbl;
    }

    private JPanel createFooter(){

        JPanel panel=new JPanel(
                new MigLayout(
                        "fillx,insets 14 28 18 28",
                        "[grow][]10[]"
                )
        );

        panel.setBackground(Color.WHITE);

        panel.setBorder(
                BorderFactory.createMatteBorder(
                        1,
                        0,
                        0,
                        0,
                        UIConstants.BORDER
                )
        );

        JButton btnCancel=new JButton("Hủy");
        JButton btnSave=new JButton(
                createMode
                        ? "Thêm"
                        : "Lưu"
        );

        btnCancel.setPreferredSize(
                new Dimension(120,40)
        );

        btnSave.setPreferredSize(
                new Dimension(130,40)
        );

        btnCancel.addActionListener(e->dispose());

        btnSave.addActionListener(
                e->saveCourse()
        );

        panel.add(new JPanel(),"growx");
        panel.add(btnCancel);
        panel.add(btnSave);

        return panel;
    }

    private void loadCourse(){

        txtCode.setText(
                course.getCourseCode()
        );

        txtName.setText(
                course.getCourseName()
        );

        txtDescription.setText(
                course.getDescription()==null
                        ? ""
                        : course.getDescription()
        );

        spCredits.setValue(
                course.getCredits()
        );

        txtFee.setText(
                course.getTuitionFee()
                        .toPlainString()
        );

        cboStatus.setSelectedItem(
                course.getStatus()
        );
    }

    private void saveCourse(){

        String code=
                txtCode.getText().trim();

        String name=
                txtName.getText().trim();

        String description=
                txtDescription.getText().trim();

        String feeText=
                txtFee.getText().trim();

        if(code.isBlank()){

            JOptionPane.showMessageDialog(
                    this,
                    "Nhập mã khóa học."
            );

            txtCode.requestFocus();

            return;
        }

        if(name.isBlank()){

            JOptionPane.showMessageDialog(
                    this,
                    "Nhập tên khóa học."
            );

            txtName.requestFocus();

            return;
        }

        BigDecimal fee;

        try{

            fee=new BigDecimal(feeText);

        }catch(Exception ex){

            JOptionPane.showMessageDialog(
                    this,
                    "Học phí không hợp lệ."
            );

            txtFee.requestFocus();

            return;
        }
        if(course==null){
            course=new Course();
        }

        course.setCourseCode(code);
        course.setCourseName(name);

        if(description.isBlank()){
            course.setDescription(null);
        }else{
            course.setDescription(description);
        }

        course.setCredits(
                (Integer) spCredits.getValue()
        );

        course.setTuitionFee(fee);

        course.setStatus(
                cboStatus.getSelectedItem()
                        .toString()
        );

        confirmed=true;

        dispose();
    }

    public boolean isConfirmed(){
        return confirmed;
    }

    public Course getCourse(){
        return confirmed
                ? course
                : null;
    }

    public static Course showCreate(
            Component parent
    ){

        Window owner=
                SwingUtilities
                        .getWindowAncestor(parent);

        CourseFormDialog dialog=
                new CourseFormDialog(
                        owner,
                        null
                );

        dialog.setVisible(true);

        return dialog.getCourse();
    }

    public static Course showEdit(
            Component parent,
            Course course
    ){

        Window owner=
                SwingUtilities
                        .getWindowAncestor(parent);

        CourseFormDialog dialog=
                new CourseFormDialog(
                        owner,
                        course
                );

        dialog.setVisible(true);

        return dialog.getCourse();
    }
}