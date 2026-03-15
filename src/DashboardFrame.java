import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {



    private final UserRole role;
    private final StudentService studentService;

    private List<Student> students;

    // Table
    private DefaultTableModel tableModel;
    private JTable table;

    // Feedback area
    private JTextArea feedbackArea;

    public DashboardFrame(UserRole role) {
        this.role = role;
        this.studentService = new StudentService();
        this.students = studentService.getAllStudents();

        setTitle("Student Grade Management System");
        setSize(1200, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== Header =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Student Grade Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel subtitle = new JLabel("Dashboard  |  Role: " + role);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // ===== Left Buttons =====
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        Font btnFont = new Font("Segoe UI", Font.PLAIN, 14);

        JButton addBtn = new JButton("Add Student");
        JButton viewBtn = new JButton("View Students");
        JButton searchBtn = new JButton("Search Student");
        JButton deleteBtn = new JButton("Delete Student");
        JButton updateBtn = new JButton("Update Marks");
        JButton approveBtn = new JButton("Approve Upgrade");
        JButton clearBtn = new JButton("Clear All");
        JButton restoreBtn = new JButton("Restore Backup");
        JButton riskBtn = new JButton("View At-Risk Students");

        JButton saveExitBtn = new JButton("Save & Exit");


        JButton[] btns = {addBtn, viewBtn, riskBtn, searchBtn, deleteBtn, updateBtn, approveBtn, clearBtn, restoreBtn, saveExitBtn};


        for (JButton b : btns) {
            b.setFont(btnFont);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(220, 42));
            nav.add(b);
            nav.add(Box.createVerticalStrut(10));
        }

        add(nav, BorderLayout.WEST);

        // Admin-only approve
        approveBtn.setEnabled(role == UserRole.ADMIN);

        // ===== Table =====
        String[] columns = {
                "Student ID", "Name",
                "Module 1", "Mark 1", "Status 1",
                "Module 2", "Mark 2", "Status 2",
                "Module 3", "Mark 3", "Status 3",
                "Average", "Final Outcome",
                "Borderline", "Approved",
                "At-Risk"
        };


        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only table
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);

        JScrollPane tableScroll = new JScrollPane(table);

        // ===== Feedback panel =====
        feedbackArea = new JTextArea(6, 30);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setEditable(false);
        feedbackArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        feedbackArea.setText("Select a student row to view automatic feedback.");

        JScrollPane feedbackScroll = new JScrollPane(feedbackArea);
        feedbackScroll.setBorder(BorderFactory.createTitledBorder("Automatic Feedback"));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPanel.add(tableScroll, BorderLayout.CENTER);
        contentPanel.add(feedbackScroll, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        // When row selected -> show feedback
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateFeedbackForSelectedRow();
        });

        // ===== Listeners =====
        addBtn.addActionListener(e -> showAddStudentDialog());

        viewBtn.addActionListener(e -> {
            students = studentService.getAllStudents();
            refreshTable();
            JOptionPane.showMessageDialog(this, "Loaded " + students.size() + " students.");
        });

        searchBtn.addActionListener(e -> showSearchStudentDialog());

        deleteBtn.addActionListener(e -> showDeleteStudentDialog());

        updateBtn.addActionListener(e -> showUpdateStudentDialog());

        clearBtn.addActionListener(e -> showClearAllDialog());

        restoreBtn.addActionListener(e -> showRestoreBackupDialog());

        approveBtn.addActionListener(e -> approveSelectedStudent());

        riskBtn.addActionListener(e -> showAtRiskStudents());


        saveExitBtn.addActionListener(e -> {
            studentService.saveAllStudents(students);
            JOptionPane.showMessageDialog(this, "Saved! Exiting...");
            System.exit(0);
        });

        // First load
        refreshTable();
        setVisible(true);
    }

    private void showAtRiskStudents() {
        students = studentService.getAllStudents();
        tableModel.setRowCount(0);

        int count = 0;
        for (Student s : students) {
            if (RiskUtils.isAtRisk(s)) {
                count++;

                tableModel.addRow(new Object[]{
                        s.getStudentId(),
                        s.getName(),

                        s.getModule1Name(),
                        s.getSub1(),
                        prettyStatus(s.getSub1Status()),

                        s.getModule2Name(),
                        s.getSub2(),
                        prettyStatus(s.getSub2Status()),

                        s.getModule3Name(),
                        s.getSub3(),
                        prettyStatus(s.getSub3Status()),

                        String.format("%.2f", s.getAverage()),
                        s.getGrade(),

                        s.isBorderline() ? "Yes" : "No",
                        s.isUpgradeApproved() ? "Yes" : "No",
                        "Yes"
                });
            }
        }

        JOptionPane.showMessageDialog(this,
                "At-Risk students: " + count,
                "At-Risk List",
                JOptionPane.INFORMATION_MESSAGE);
    }


    // ======================
    // TABLE + FEEDBACK
    // ======================


    private void refreshTable() {
        students = studentService.getAllStudents(); // always load latest
        tableModel.setRowCount(0);

        for (Student s : students) {
            Object[] row = new Object[]{
                    s.getStudentId(),
                    s.getName(),

                    s.getModule1Name(),
                    s.getSub1(),
                    prettyStatus(s.getSub1Status()),

                    s.getModule2Name(),
                    s.getSub2(),
                    prettyStatus(s.getSub2Status()),

                    s.getModule3Name(),
                    s.getSub3(),
                    prettyStatus(s.getSub3Status()),

                    String.format("%.2f", s.getAverage()),
                    s.getGrade(),

                    s.isBorderline() ? "Yes" : "No",
                    s.isUpgradeApproved() ? "Yes" : "No",
                    RiskUtils.isAtRisk(s) ? "Yes" : "No"

            };
            tableModel.addRow(row);
        }

        feedbackArea.setText("Select a student row to view automatic feedback.");
    }

    private void updateFeedbackForSelectedRow() {

        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            feedbackArea.setText("Select a student to view feedback.");
            return;
        }

        // Convert view row → model row (important for sorted tables)
        int modelRow = table.convertRowIndexToModel(viewRow);

        String studentId = tableModel.getValueAt(modelRow, 0).toString();

        for (Student s : students) {
            if (s.getStudentId().equals(studentId)) {

                StringBuilder text = new StringBuilder();
                text.append(s.getFeedback());

                // ---- At-Risk section ----
                if (RiskUtils.isAtRisk(s)) {
                    text.append("\n\n---\n🚨 At-Risk Indicators:\n");

                    for (String reason : RiskUtils.getRiskReasons(s)) {
                        text.append("• ").append(reason).append("\n");
                    }
                } else {
                    text.append("\n\n---\nNo at-risk indicators detected.");
                }

                feedbackArea.setText(text.toString());
                feedbackArea.setCaretPosition(0);
                return;
            }
        }

        feedbackArea.setText("Feedback not available for the selected student.");
    }


    private String prettyStatus(ExamStatus st) {
        if (st == null) return "First Sitting";
        switch (st) {
            case FIRST_SITTING: return "First Sitting";
            case RESIT: return "Resit";
            case ABSENT: return "Absent";
            default: return st.name();
        }
    }

    // ======================
    // ADD
    // ======================
    private void showAddStudentDialog() {

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();

        JTextField m1Field = new JTextField("Programming");
        JTextField m2Field = new JTextField("Database");
        JTextField m3Field = new JTextField("Software Engineering");

        JTextField s1Field = new JTextField();
        JTextField s2Field = new JTextField();
        JTextField s3Field = new JTextField();

        JComboBox<ExamStatus> st1Box = new JComboBox<>(ExamStatus.values());
        JComboBox<ExamStatus> st2Box = new JComboBox<>(ExamStatus.values());
        JComboBox<ExamStatus> st3Box = new JComboBox<>(ExamStatus.values());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

        panel.add(new JLabel("Student ID (numbers):"));
        panel.add(idField);

        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Module 1 name:"));
        panel.add(m1Field);
        panel.add(new JLabel("Mark 1 (0-100):"));
        panel.add(s1Field);
        panel.add(new JLabel("Status 1:"));
        panel.add(st1Box);

        panel.add(new JLabel("Module 2 name:"));
        panel.add(m2Field);
        panel.add(new JLabel("Mark 2 (0-100):"));
        panel.add(s2Field);
        panel.add(new JLabel("Status 2:"));
        panel.add(st2Box);

        panel.add(new JLabel("Module 3 name:"));
        panel.add(m3Field);
        panel.add(new JLabel("Mark 3 (0-100):"));
        panel.add(s3Field);
        panel.add(new JLabel("Status 3:"));
        panel.add(st3Box);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Student",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) return;

        String id = idField.getText().trim();
        String name = nameField.getText().trim();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID and Name cannot be empty.");
            return;
        }
        if (!id.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Student ID must contain only numbers.");
            return;
        }

        String m1 = m1Field.getText().trim();
        String m2 = m2Field.getText().trim();
        String m3 = m3Field.getText().trim();

        if (m1.isEmpty() || m2.isEmpty() || m3.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Module names cannot be empty.");
            return;
        }

        try {
            double s1 = parseMarkOrThrow(s1Field.getText(), "Mark 1");
            double s2 = parseMarkOrThrow(s2Field.getText(), "Mark 2");
            double s3 = parseMarkOrThrow(s3Field.getText(), "Mark 3");

            ExamStatus st1 = (ExamStatus) st1Box.getSelectedItem();
            ExamStatus st2 = (ExamStatus) st2Box.getSelectedItem();
            ExamStatus st3 = (ExamStatus) st3Box.getSelectedItem();

            // always load latest before add
            Student newStudent = new Student(
                    id, name,
                    m1, s1, st1,
                    m2, s2, st2,
                    m3, s3, st3
            );

            boolean added = studentService.addStudent(newStudent);

            if (!added) {
                JOptionPane.showMessageDialog(this, "This Student ID already exists!");
                return;
            }

            refreshTable();
            JOptionPane.showMessageDialog(this, "✅ Student added successfully!");

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ======================
    // SEARCH
    // ======================
    private void showSearchStudentDialog() {
        String id = JOptionPane.showInputDialog(this, "Enter Student ID to search:");
        if (id == null) return;
        id = id.trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID cannot be empty.");
            return;
        }

        refreshTable();

        for (int row = 0; row < table.getRowCount(); row++) {
            String rowId = table.getValueAt(row, 0).toString();
            if (rowId.equals(id)) {
                table.setRowSelectionInterval(row, row);
                table.scrollRectToVisible(table.getCellRect(row, 0, true));
                updateFeedbackForSelectedRow();
                JOptionPane.showMessageDialog(this, "✅ Student Found!");
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "❌ Student with ID " + id + " not found.");
    }

    // ======================
    // DELETE
    // ======================
    private void showDeleteStudentDialog() {
        String id = JOptionPane.showInputDialog(this, "Enter Student ID to delete:");
        if (id == null) return;
        id = id.trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID cannot be empty.");
            return;
        }

        Student found = studentService.findStudentById(id);

        if (found == null) {
            JOptionPane.showMessageDialog(this, "❌ Student not found.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete student?\n\nID: " + found.getStudentId() +
                        "\nName: " + found.getName() +
                        "\nOutcome: " + found.getGrade(),
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = studentService.deleteStudent(id);

            if (deleted) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "✅ Deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Delete failed.");
            }
        }
    }

    

    // ======================
    // UPDATE MARKS
    // ======================
    private void showUpdateStudentDialog() {
        String id = JOptionPane.showInputDialog(this, "Enter Student ID to update:");
        if (id == null) return;
        id = id.trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID cannot be empty.");
            return;
        }

        students = FileHandler.loadStudents();

        Student found = studentService.findStudentById(id);

        if (found == null) {
            JOptionPane.showMessageDialog(this, "❌ Student not found.");
            return;
        }

        JTextField m1Field = new JTextField(found.getModule1Name());
        JTextField m2Field = new JTextField(found.getModule2Name());
        JTextField m3Field = new JTextField(found.getModule3Name());

        JTextField s1Field = new JTextField(String.valueOf(found.getSub1()));
        JTextField s2Field = new JTextField(String.valueOf(found.getSub2()));
        JTextField s3Field = new JTextField(String.valueOf(found.getSub3()));

        JComboBox<ExamStatus> st1Box = new JComboBox<>(ExamStatus.values());
        JComboBox<ExamStatus> st2Box = new JComboBox<>(ExamStatus.values());
        JComboBox<ExamStatus> st3Box = new JComboBox<>(ExamStatus.values());

        st1Box.setSelectedItem(found.getSub1Status());
        st2Box.setSelectedItem(found.getSub2Status());
        st3Box.setSelectedItem(found.getSub3Status());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

        panel.add(new JLabel("Student ID:"));
        panel.add(new JLabel(found.getStudentId()));

        panel.add(new JLabel("Name:"));
        panel.add(new JLabel(found.getName()));

        panel.add(new JLabel("Module 1 name:"));
        panel.add(m1Field);
        panel.add(new JLabel("Mark 1:"));
        panel.add(s1Field);
        panel.add(new JLabel("Status 1:"));
        panel.add(st1Box);

        panel.add(new JLabel("Module 2 name:"));
        panel.add(m2Field);
        panel.add(new JLabel("Mark 2:"));
        panel.add(s2Field);
        panel.add(new JLabel("Status 2:"));
        panel.add(st2Box);

        panel.add(new JLabel("Module 3 name:"));
        panel.add(m3Field);
        panel.add(new JLabel("Mark 3:"));
        panel.add(s3Field);
        panel.add(new JLabel("Status 3:"));
        panel.add(st3Box);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Update Student",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result != JOptionPane.OK_OPTION) return;

        try {
            double ns1 = parseMarkOrThrow(s1Field.getText(), "Mark 1");
            double ns2 = parseMarkOrThrow(s2Field.getText(), "Mark 2");
            double ns3 = parseMarkOrThrow(s3Field.getText(), "Mark 3");

            ExamStatus nst1 = (ExamStatus) st1Box.getSelectedItem();
            ExamStatus nst2 = (ExamStatus) st2Box.getSelectedItem();
            ExamStatus nst3 = (ExamStatus) st3Box.getSelectedItem();

            Student updated = new Student(found.getStudentId(), found.getName(),
                    m1Field.getText().trim(), ns1, nst1,
                    m2Field.getText().trim(), ns2, nst2,
                    m3Field.getText().trim(), ns3, nst3
            );

            // keep approval flag (optional)
            updated.setUpgradeApproved(found.isUpgradeApproved());
            updated.applyApprovedUpgrade();

            boolean updatedOk = studentService.updateStudent(id, updated);

            if (updatedOk) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "✅ Updated. New outcome: " + updated.getGrade());
            } else {
                JOptionPane.showMessageDialog(this, "❌ Update failed.");
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ======================
    // CLEAR + RESTORE
    // ======================
    private void showClearAllDialog() {
        students = studentService.getAllStudents();
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students to clear.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "⚠ Delete ALL students? A backup will be created.",
                "Confirm Clear All",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            studentService.backupStudents();
            students.clear();
            studentService.saveAllStudents(students);
            refreshTable();
            JOptionPane.showMessageDialog(this, "✅ Cleared all students (backup created).");
        }
    }

    private void showRestoreBackupDialog() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Restore last backup? This will replace current student list.",
                "Restore Backup",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = studentService.restoreBackup();
        if (!ok) {
            JOptionPane.showMessageDialog(this, "❌ No backup found.");
            return;
        }

        refreshTable();
        JOptionPane.showMessageDialog(this, "✅ Backup restored.");
    }

    // ======================
    // APPROVE UPGRADE (ADMIN ONLY)
    // ======================
    private void approveSelectedStudent() {
        if (role != UserRole.ADMIN) {
            JOptionPane.showMessageDialog(this, "Only Admin can approve borderline upgrades.");
            return;
        }

        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a student row first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        String id = tableModel.getValueAt(modelRow, 0).toString();

        Student found = studentService.findStudentById(id);

        if (found == null) {
            JOptionPane.showMessageDialog(this, "Student not found.");
            return;
        }

        if (!found.isBorderline()) {
            JOptionPane.showMessageDialog(this, "This student is NOT borderline. No upgrade needed.");
            return;
        }

        if (found.isUpgradeApproved()) {
            JOptionPane.showMessageDialog(this, "Upgrade already approved.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Approve upgrade for:\n\nID: " + found.getStudentId() +
                        "\nName: " + found.getName() +
                        "\nCurrent Outcome: " + found.getGrade(),
                "Approve Upgrade",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean approved = studentService.approveUpgrade(id);

            if (approved) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "✅ Upgrade approved.");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Upgrade approval failed.");
            }
        }
    }

    // ======================
    // UTIL
    // ======================
    private double parseMarkOrThrow(String text, String fieldName) {
        try {
            double mark = Double.parseDouble(text.trim());
            if (mark < 0 || mark > 100) throw new IllegalArgumentException(fieldName + " must be between 0 and 100.");
            return mark;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a number.");
        }
    }
}
