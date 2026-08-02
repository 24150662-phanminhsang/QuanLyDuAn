package view;

import controller.ClassController;
import dao.CourseDAO;
import dao.TeacherDAO;
import dao.impl.TeacherDAOImpl;
import model.ClassRoom;
import model.Course;
import model.Teacher;
import net.miginfocom.swing.MigLayout;
import util.UIConstants;
import view.components.ContentCard;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

public class ClassManagementView extends JPanel {
    private static final String LIST_CARD = "LIST";
    private static final String FORM_CARD = "FORM";

    private final ClassController controller = new ClassController();
    private final CourseDAO courseDAO = new CourseDAO();
    private final TeacherDAO teacherDAO = new TeacherDAOImpl();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Mã lớp", "Khóa học", "Giảng viên", "Học kỳ", "Năm học", "Lịch học", "Phòng", "Sĩ số", "Trạng thái"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);
    private final JTextField searchField = new JTextField();
    private final JComboBox<String> filterStatus = new JComboBox<>(new String[]{"ALL", "OPEN", "CLOSED", "COMPLETED", "CANCELLED"});

    private JButton editButton;
    private JButton statusButton;
    private JButton completeButton;
    private JButton cancelClassButton;
    private JButton deleteButton;

    private JTextField codeField;
    private JComboBox<Course> courseCombo;
    private JComboBox<TeacherOption> teacherCombo;
    private JTextField semesterField;
    private JTextField schoolYearField;
    private JTextField scheduleField;
    private JTextField roomField;
    private JSpinner capacitySpinner;
    private JTextField startDateField;
    private JTextField endDateField;
    private JComboBox<String> formStatus;
    private JButton saveButton;
    private ClassRoom editingClass;

    public ClassManagementView() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND);
        cardPanel.setOpaque(false);
        cardPanel.add(createListPanel(), LIST_CARD);
        cardPanel.add(createFormPanel(), FORM_CARD);
        add(cardPanel, BorderLayout.CENTER);
        registerEvents();
        loadData();
    }

    private JPanel createListPanel() {
        JPanel wrapper = new JPanel(new MigLayout("fill, insets 16", "[grow,fill]", "[grow,fill]"));
        wrapper.setOpaque(false);
        ContentCard card = new ContentCard();
        card.setLayout(new MigLayout("fill, wrap 1, insets 18 20", "[grow,fill]", "[]12[]10[grow,fill]"));

        JLabel title = new JLabel("Quản lý lớp học");
        title.setFont(UIConstants.FONT_HEADING);
        JLabel sub = new JLabel("Tạo lớp, phân công giảng viên và quản lý trạng thái lớp");
        sub.setFont(UIConstants.FONT_NORMAL);
        sub.setForeground(UIConstants.TEXT_SECONDARY);
        JPanel heading = new JPanel(new MigLayout("fillx, insets 0", "[grow]", "[][]"));
        heading.setOpaque(false); heading.add(title, "wrap"); heading.add(sub);
        card.add(heading, "growx");
        card.add(createToolbar(), "growx");

        table.setRowHeight(40);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setReorderingAllowed(false);
        int[] widths = {45, 100, 180, 180, 85, 95, 190, 85, 65, 100};
        for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        scroll.getHorizontalScrollBar().setUnitIncrement(20);
        card.add(scroll, "grow,push");
        wrapper.add(card, "grow,push");
        return wrapper;
    }

    private JPanel createToolbar() {
        JPanel outer = new JPanel(new MigLayout("fillx, wrap 1, insets 0, gapy 8", "[grow,fill]", "[][]"));
        outer.setOpaque(false);
        searchField.putClientProperty("JTextField.placeholderText", "Tìm mã lớp, học kỳ, năm học, phòng, lịch học...");
        JButton refresh = button("Làm mới", false);
        JButton add = button("Thêm lớp", true);
        JPanel row1 = new JPanel(new MigLayout("fillx, insets 0", "[grow,fill]10[140!]10[][]", "[]"));
        row1.setOpaque(false); row1.add(searchField, "height 38!"); row1.add(filterStatus, "height 38!"); row1.add(refresh); row1.add(add);

        editButton = button("Cập nhật", false);
        statusButton = button("Đóng đăng ký", false);
        completeButton = button("Hoàn thành", false);
        cancelClassButton = button("Hủy lớp", false);
        deleteButton = button("Xóa", false);
        JPanel row2 = new JPanel(new MigLayout("fillx, insets 0", "[grow][][][][][]", "[]"));
        row2.setOpaque(false); row2.add(new JLabel(), "growx"); row2.add(editButton); row2.add(statusButton); row2.add(completeButton); row2.add(cancelClassButton); row2.add(deleteButton);
        outer.add(row1, "growx"); outer.add(row2, "growx");

        refresh.addActionListener(e -> { searchField.setText(""); filterStatus.setSelectedItem("ALL"); loadData(); });
        add.addActionListener(e -> showCreateForm());
        return outer;
    }

    private JPanel createFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false); wrapper.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        ContentCard card = new ContentCard();
        card.setLayout(new MigLayout("fillx, wrap 2, insets 20 24", "[right,145!]12[grow,fill]", "[]12[]12[]12[]12[]12[]12[]12[]12[]12[]"));

        JLabel title = new JLabel("Thông tin lớp học"); title.setFont(UIConstants.FONT_HEADING);
        JButton back = button("Hủy", false); saveButton = button("Lưu lớp", true);
        JPanel header = new JPanel(new MigLayout("fillx, insets 0", "[grow][][]", "[]"));
        header.setOpaque(false); header.add(title, "growx"); header.add(back); header.add(saveButton);
        card.add(header, "span 2,growx");

        codeField = field("Ví dụ: JAVA01-01");
        courseCombo = new JComboBox<>(); teacherCombo = new JComboBox<>();
        semesterField = field("Ví dụ: HK1"); schoolYearField = field("Ví dụ: 2026-2027");
        scheduleField = field("Ví dụ: Thứ 2 - Tiết 1 đến 3"); roomField = field("Ví dụ: P.A101");
        capacitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 1000, 1));
        startDateField = field("yyyy-MM-dd"); endDateField = field("yyyy-MM-dd");
        formStatus = new JComboBox<>(new String[]{"OPEN", "CLOSED", "COMPLETED", "CANCELLED"});

        addField(card, "Mã lớp *", codeField); addField(card, "Khóa học *", courseCombo); addField(card, "Giảng viên", teacherCombo);
        addField(card, "Học kỳ *", semesterField); addField(card, "Năm học *", schoolYearField); addField(card, "Lịch học", scheduleField);
        addField(card, "Phòng học", roomField); addField(card, "Sĩ số tối đa *", capacitySpinner); addField(card, "Ngày bắt đầu", startDateField);
        addField(card, "Ngày kết thúc", endDateField); addField(card, "Trạng thái", formStatus);

        JScrollPane scroll = new JScrollPane(card); scroll.setBorder(null); scroll.getVerticalScrollBar().setUnitIncrement(16);
        wrapper.add(scroll, BorderLayout.CENTER);
        back.addActionListener(e -> showList()); saveButton.addActionListener(e -> saveForm());
        return wrapper;
    }

    private void registerEvents() {
        DocumentListener listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { search(); }
            public void removeUpdate(DocumentEvent e) { search(); }
            public void changedUpdate(DocumentEvent e) { search(); }
        };
        searchField.getDocument().addDocumentListener(listener);
        filterStatus.addActionListener(e -> search());
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) updateButtons(); });
        table.addMouseListener(new java.awt.event.MouseAdapter() { public void mouseClicked(java.awt.event.MouseEvent e) { if (e.getClickCount()==2) showEditForm(); }});
        editButton.addActionListener(e -> showEditForm());
        statusButton.addActionListener(e -> toggleOpenClosed());
        completeButton.addActionListener(e -> changeStatus("COMPLETED"));
        cancelClassButton.addActionListener(e -> changeStatus("CANCELLED"));
        deleteButton.addActionListener(e -> deleteSelected());
        updateButtons();
    }

    public void loadData() { display(controller.getAllClasses()); loadReferenceData(); }
    private void search() { display(controller.searchClasses(searchField.getText(), String.valueOf(filterStatus.getSelectedItem()))); }

    private void display(List<ClassRoom> classes) {
        tableModel.setRowCount(0);
        if (classes == null) classes = Collections.emptyList();
        for (ClassRoom c : classes) {
            Course course = courseDAO.getCourseById(c.getCourseId());
            Teacher teacher = c.getTeacherId() > 0 ? teacherDAO.getById(c.getTeacherId()) : null;
            int enrolled = controller.countActiveEnrollments(c.getClassId());
            tableModel.addRow(new Object[]{c.getClassId(), c.getClassCode(), course == null ? c.getCourseId() : course.toString(),
                    teacher == null ? "Chưa phân công" : teacher.toString(), c.getSemester(), c.getSchoolYear(), c.getScheduleText(), c.getRoom(),
                    enrolled + "/" + c.getMaximumStudents(), c.getStatus()});
        }
        table.clearSelection(); updateButtons();
    }

    private void loadReferenceData() {
        Course selectedCourse = (Course) courseCombo.getSelectedItem();
        TeacherOption selectedTeacher = (TeacherOption) teacherCombo.getSelectedItem();
        courseCombo.removeAllItems();
        for (Course c : courseDAO.getActiveCourses()) courseCombo.addItem(c);
        teacherCombo.removeAllItems(); teacherCombo.addItem(new TeacherOption(null));
        for (Teacher t : teacherDAO.getAll()) if ("ACTIVE".equalsIgnoreCase(t.getStatus())) teacherCombo.addItem(new TeacherOption(t));
        if (selectedCourse != null) courseCombo.setSelectedItem(selectedCourse);
        if (selectedTeacher != null) teacherCombo.setSelectedItem(selectedTeacher);
    }

    private void showCreateForm() { editingClass = null; clearForm(); saveButton.setText("Lưu lớp"); codeField.setEditable(true); cardLayout.show(cardPanel, FORM_CARD); }
    private void showEditForm() {
        int id = selectedId(); if (id <= 0) { warning("Hãy chọn lớp cần cập nhật."); return; }
        editingClass = controller.getClassById(id); if (editingClass == null) { warning("Không tìm thấy lớp học."); return; }
        loadReferenceData(); codeField.setText(editingClass.getClassCode()); selectCourse(editingClass.getCourseId()); selectTeacher(editingClass.getTeacherId());
        semesterField.setText(value(editingClass.getSemester())); schoolYearField.setText(value(editingClass.getSchoolYear()));
        scheduleField.setText(value(editingClass.getScheduleText())); roomField.setText(value(editingClass.getRoom())); capacitySpinner.setValue(editingClass.getMaximumStudents());
        startDateField.setText(editingClass.getStartDate()==null ? "" : editingClass.getStartDate().toString()); endDateField.setText(editingClass.getEndDate()==null ? "" : editingClass.getEndDate().toString());
        formStatus.setSelectedItem(editingClass.getStatus()); codeField.setEditable(false); saveButton.setText("Lưu thay đổi"); cardLayout.show(cardPanel, FORM_CARD);
    }

    private void saveForm() {
        try {
            Course course = (Course) courseCombo.getSelectedItem(); TeacherOption option = (TeacherOption) teacherCombo.getSelectedItem();
            if (course == null) throw new IllegalArgumentException("Hãy chọn khóa học.");
            ClassRoom c = new ClassRoom(); if (editingClass != null) c.setClassId(editingClass.getClassId());
            c.setClassCode(codeField.getText()); c.setCourseId(course.getCourseId()); c.setTeacherId(option == null || option.teacher == null ? 0 : option.teacher.getTeacherId());
            c.setSemester(semesterField.getText()); c.setSchoolYear(schoolYearField.getText()); c.setScheduleText(scheduleField.getText()); c.setRoom(roomField.getText());
            c.setMaximumStudents(((Number)capacitySpinner.getValue()).intValue()); c.setStartDate(parseDate(startDateField.getText())); c.setEndDate(parseDate(endDateField.getText()));
            c.setStatus(String.valueOf(formStatus.getSelectedItem()));
            boolean ok = editingClass == null ? controller.createClass(c) : controller.updateClass(c);
            if (!ok) { warning("Không thể lưu lớp học."); return; }
            JOptionPane.showMessageDialog(this, editingClass == null ? "Thêm lớp thành công." : "Cập nhật lớp thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData(); showList();
        } catch (RuntimeException ex) { warning(rootMessage(ex)); }
    }

    private void toggleOpenClosed() {
        ClassRoom c = selectedClass(); if (c == null) return;
        try { if ("OPEN".equals(c.getStatus())) controller.closeClass(c.getClassId()); else controller.openClass(c.getClassId()); loadData(); }
        catch (RuntimeException ex) { warning(rootMessage(ex)); }
    }
    private void changeStatus(String status) {
        ClassRoom c = selectedClass(); if (c == null) return;
        int answer = JOptionPane.showConfirmDialog(this, "Xác nhận chuyển lớp sang " + status + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;
        try { if ("COMPLETED".equals(status)) controller.completeClass(c.getClassId()); else controller.cancelClass(c.getClassId()); loadData(); }
        catch (RuntimeException ex) { warning(rootMessage(ex)); }
    }
    private void deleteSelected() {
        int id = selectedId(); if (id <= 0) return;
        int answer = JOptionPane.showConfirmDialog(this, "Chỉ xóa được lớp chưa có học viên. Tiếp tục?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;
        try { controller.deleteClass(id); loadData(); } catch (RuntimeException ex) { warning(rootMessage(ex)); }
    }

    private void updateButtons() {
        ClassRoom c = selectedClass(); boolean selected = c != null;
        editButton.setEnabled(selected); deleteButton.setEnabled(selected); statusButton.setEnabled(selected); completeButton.setEnabled(selected); cancelClassButton.setEnabled(selected);
        if (!selected) { statusButton.setText("Đóng đăng ký"); return; }
        statusButton.setText("OPEN".equals(c.getStatus()) ? "Đóng đăng ký" : "Mở đăng ký");
        boolean terminal = "COMPLETED".equals(c.getStatus()) || "CANCELLED".equals(c.getStatus());
        statusButton.setEnabled(!terminal); completeButton.setEnabled(!terminal); cancelClassButton.setEnabled(!terminal);
    }

    private ClassRoom selectedClass() { int id = selectedId(); return id <= 0 ? null : controller.getClassById(id); }
    private int selectedId() { int row = table.getSelectedRow(); if (row < 0) return -1; Object v = tableModel.getValueAt(table.convertRowIndexToModel(row),0); return v instanceof Number n ? n.intValue() : -1; }
    private void showList() { editingClass = null; clearForm(); cardLayout.show(cardPanel, LIST_CARD); }
    private void clearForm() { if (codeField == null) return; codeField.setText(""); semesterField.setText(""); schoolYearField.setText(""); scheduleField.setText(""); roomField.setText(""); capacitySpinner.setValue(30); startDateField.setText(""); endDateField.setText(""); formStatus.setSelectedItem("OPEN"); if (courseCombo.getItemCount()>0) courseCombo.setSelectedIndex(0); if (teacherCombo.getItemCount()>0) teacherCombo.setSelectedIndex(0); }
    private void selectCourse(int id) { for (int i=0;i<courseCombo.getItemCount();i++) if (courseCombo.getItemAt(i).getCourseId()==id) { courseCombo.setSelectedIndex(i); return; } }
    private void selectTeacher(int id) { for (int i=0;i<teacherCombo.getItemCount();i++) { TeacherOption o=teacherCombo.getItemAt(i); if (o.teacher!=null && o.teacher.getTeacherId()==id) { teacherCombo.setSelectedIndex(i); return; }} teacherCombo.setSelectedIndex(0); }

    private JTextField field(String placeholder) { JTextField f = new JTextField(); f.putClientProperty("JTextField.placeholderText", placeholder); return f; }
    private JButton button(String text, boolean primary) { JButton b = new JButton(text); b.setFont(UIConstants.FONT_MEDIUM); b.setFocusable(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); if (primary) { b.setBackground(UIConstants.PRIMARY); b.setForeground(Color.WHITE); } return b; }
    private void addField(JPanel p, String label, JComponent c) { JLabel l = new JLabel(label); l.setFont(UIConstants.FONT_MEDIUM); p.add(l); p.add(c, "growx,height 38!"); }
    private Date parseDate(String text) { if (text == null || text.isBlank()) return null; try { return Date.valueOf(LocalDate.parse(text.trim())); } catch (DateTimeParseException ex) { throw new IllegalArgumentException("Ngày phải có định dạng yyyy-MM-dd."); } }
    private String value(String s) { return s == null ? "" : s; }
    private void warning(String message) { JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.WARNING_MESSAGE); }
    private String rootMessage(Throwable ex) { Throwable root=ex; while(root.getCause()!=null) root=root.getCause(); return root.getMessage()==null ? "Không xác định" : root.getMessage(); }

    private static final class TeacherOption {
        private final Teacher teacher;
        private TeacherOption(Teacher teacher) { this.teacher = teacher; }
        @Override public String toString() { return teacher == null ? "Chưa phân công" : teacher.toString(); }
        @Override public boolean equals(Object obj) { if (!(obj instanceof TeacherOption other)) return false; if (teacher==null || other.teacher==null) return teacher==other.teacher; return teacher.getTeacherId()==other.teacher.getTeacherId(); }
        @Override public int hashCode() { return teacher == null ? 0 : teacher.getTeacherId(); }
    }
}
