import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {

    private List<Student> students;
    private final boolean isAdmin;

    private DefaultTableModel tableModel;
    private JTable table;

    private JButton addBtn, viewBtn, searchBtn, deleteBtn, updateBtn, clearBtn, restoreBtn, saveExitBtn;

    public DashboardFrame(boolean isAdmin) {
        this.isAdmin = isAdmin;

        setTitle("Student Grade Management System");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Load initial data
        students = FileHandler.loadStudents();

        // ===== Header =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Student Grade Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel subtitle = new JLabel("Dashboard  |  Role: " + (isAdmin ? "Admin" : "User"));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // ===== Left Buttons =====
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        Font btnFont = new Font("Segoe UI", Font.PLAIN, 14);

        addBtn = new JButton("Add Student");
        viewBtn = new JButton("View Students");
        searchBtn = new JButton("Search Student");
        deleteBtn = new JButton("Delete Student");
        updateBtn = new JButton("Update Marks");
        clearBtn = new JButton("Clear All");
        restoreBtn = new JButton("Restore Backup");
        saveExitBtn = new JButton("Save & Exit");

        JButton[] btns = {addBtn, viewBtn, searchBtn, deleteBtn, updateBtn, clearBtn, restoreBtn, saveExitBtn};
        for (JButton b : btns) {
            b.setFont(btnFont);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(200, 40));
            nav.add(b);
            nav.add(Box.createVerticalStrut(10));
        }

        // Admin-only controls
        deleteBtn.setEnabled(isAdmin);
        clearBtn.setEnabled(isAdmin);
        restoreBtn.setEnabled(isAdmin);

        add(nav, BorderLayout.WEST);

        // ===== Table =====
        String[] columns = {
                "Student ID", "Name",
                "Module 1", "Mark 1", "Status 1",
                "Module 2", "Mark 2", "Status 2",
                "Module 3", "Mark 3", "Status 3",
                "Average", "Final Outcome"
        };

        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // ===== Listeners =====
        addBtn.addActionListener(e -> showAddStudentDialog());
        viewBtn.addActionListener(e -> refreshTable());
        searchBtn.addActionListener(e -> showSearchStudentDialog());
        deleteBtn.addActionListener(e -> showDeleteStudentDialog());
        updateBtn.addActionListener(e -> showUpdateStudentDialog());
        clearBtn.addActionListener(e -> showClearAllDialog());
        restoreBtn.addActionListener(e -> showRestoreBackupDialog());

        saveExitBtn.addActionListener(e -> {
            FileHandler.saveStudents(students);
            JOptionPane.showMessageDialog(this, "Saved! Exiting...");
            System.exit(0);
        });

        // Initial view
        refreshTable();
        setVisible(true);
    }

    // =========================
    // Refresh Table
    // =========================
    private void refreshTable() {
        students = FileHandler.loadStudents();

        tableModel.setRowCount(0);
        for (Student s : students) {
            Object[] row = new Object[]{
                    s.getStudentId(),
                    s.getName(),

                    s.getModule1Name(), s.getSub1(), s.getSub1Status(),
                    s.getModule2Name(), s.getSub2(), s.getSub2Status(),
                    s.getModule3Name(), s.getSub3(), s.getSub3Status(),

                    String.format("%.2f", s.getAverage()),
                    s.getGrade()
            };
            tableModel.addRow(row);
        }
    }

    // =========================
    // Add Student
    // =========================
    private void showAddStudentDialog() {

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();

        JTextField m1Field = new JTextField("Programming");
        JTextField m2Field = new JTextField("Database");
        JTextField m3Field = new JTextField("Software Engineering");

        JTextField sub1Field = new JTextField();
        JTextField sub2Field = new JTextField();
        JTextField sub3Field = new JTextField();

        JComboBox<ExamStatus> s1StatusBox = new JComboBox<>(ExamStatus.values());
        JComboBox<ExamStatus> s2StatusBox = new JComboBox<>(ExamStatus.values());
        JComboBox<ExamStatus> s3StatusBox = new JComboBox<>(ExamStatus.values());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

        panel.add(new JLabel("Student ID (numbers):"));
        panel.add(idField);

        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Module 1 Name:"));
        panel.add(m1Field);
        panel.add(new JLabel("Module 1 Mark (0-100):"));
        panel.add(sub1Field);
        panel.add(new JLabel("Module 1 Status:"));
        panel.add(s1StatusBox);

        panel.add(new JLabel("Module 2 Name:"));
        panel.add(m2Field);
        panel.add(new JLabel("Module 2 Mark (0-100):"));
        panel.add(sub2Field);
        panel.add(new JLabel("Module 2 Status:"));
        panel.add(s2StatusBox);

        panel.add(new JLabel("Module 3 Name:"));
        panel.add(m3Field);
        panel.add(new JLabel("Module 3 Mark (0-100):"));
        panel.add(sub3Field);
        panel.add(new JLabel("Module 3 Status:"));
        panel.add(s3StatusBox);

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
            JOptionPane.showMessageDialog(this, "Student ID and Name cannot be empty.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!id.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Student ID must contain only numbers.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double s1 = parseMarkOrThrow(sub1Field.getText(), "Module 1");
            double s2 = parseMarkOrThrow(sub2Field.getText(), "Module 2");
            double s3 = parseMarkOrThrow(sub3Field.getText(), "Module 3");

            ExamStatus st1 = (ExamStatus) s1StatusBox.getSelectedItem();
            ExamStatus st2 = (ExamStatus) s2StatusBox.getSelectedItem();
            ExamStatus st3 = (ExamStatus) s3StatusBox.getSelectedItem();

            students = FileHandler.loadStudents();

            for (Student existing : students) {
                if (existing.getStudentId().equals(id)) {
                    JOptionPane.showMessageDialog(this, "This Student ID already exists!",
                            "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Student newStudent = new Student(
                    id, name,
                    m1Field.getText().trim(), s1, st1,
                    m2Field.getText().trim(), s2, st2,
                    m3Field.getText().trim(), s3, st3
            );

            students.add(newStudent);
            FileHandler.saveStudents(students);

            refreshTable();

            JOptionPane.showMessageDialog(this, "✅ Student added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================
    // Search
    // =========================
    private void showSearchStudentDialog() {
        refreshTable();

        String id = JOptionPane.showInputDialog(this, "Enter Student ID to search:");
        if (id == null) return;
        id = id.trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID cannot be empty.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int row = 0; row < table.getRowCount(); row++) {
            String rowId = table.getValueAt(row, 0).toString();
            if (rowId.equals(id)) {
                table.setRowSelectionInterval(row, row);
                table.scrollRectToVisible(table.getCellRect(row, 0, true));
                JOptionPane.showMessageDialog(this, "✅ Student Found: " + id,
                        "Search Result", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "❌ Student with ID " + id + " not found.",
                "Search Result", JOptionPane.ERROR_MESSAGE);
    }

    // =========================
    // Delete (Admin)
    // =========================
    private void showDeleteStudentDialog() {
        if (!isAdmin) return;

        refreshTable();

        String id = JOptionPane.showInputDialog(this, "Enter Student ID to delete:");
        if (id == null) return;
        id = id.trim();

        Student found = null;
        for (Student s : students) {
            if (s.getStudentId().equals(id)) {
                found = s;
                break;
            }
        }

        if (found == null) {
            JOptionPane.showMessageDialog(this, "❌ Student not found.",
                    "Delete", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete " + found.getName() + " (" + found.getStudentId() + ")?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            students.remove(found);
            FileHandler.saveStudents(students);
            refreshTable();
            JOptionPane.showMessageDialog(this, "✅ Student deleted successfully!");
        }
    }

    // =========================
    // Update Marks
    // =========================
    private void showUpdateStudentDialog() {
        refreshTable();

        String id = JOptionPane.showInputDialog(this, "Enter Student ID to update marks:");
        if (id == null) return;
        id = id.trim();

        Student found = null;
        int index = -1;

        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equals(id)) {
                found = students.get(i);
                index = i;
                break;
            }
        }

        if (found == null) {
            JOptionPane.showMessageDialog(this, "❌ Student not found.",
                    "Update", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField s1Field = new JTextField(String.valueOf(found.getSub1()));
        JTextField s2Field = new JTextField(String.valueOf(found.getSub2()));
        JTextField s3Field = new JTextField(String.valueOf(found.getSub3()));

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel(found.getModule1Name() + " Mark:"));
        panel.add(s1Field);
        panel.add(new JLabel(found.getModule2Name() + " Mark:"));
        panel.add(s2Field);
        panel.add(new JLabel(found.getModule3Name() + " Mark:"));
        panel.add(s3Field);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Update Marks", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return;

        try {
            double ns1 = parseMarkOrThrow(s1Field.getText(), "Module 1");
            double ns2 = parseMarkOrThrow(s2Field.getText(), "Module 2");
            double ns3 = parseMarkOrThrow(s3Field.getText(), "Module 3");

            Student updated = new Student(
                    found.getStudentId(), found.getName(),
                    found.getModule1Name(), ns1, found.getSub1Status(),
                    found.getModule2Name(), ns2, found.getSub2Status(),
                    found.getModule3Name(), ns3, found.getSub3Status()
            );

            students.set(index, updated);
            FileHandler.saveStudents(students);
            refreshTable();

            JOptionPane.showMessageDialog(this, "✅ Marks updated successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================
    // Clear All (Admin)
    // =========================
    private void showClearAllDialog() {
        if (!isAdmin) return;

        refreshTable();

        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students to clear.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "⚠ Delete ALL students? Backup will be created.",
                "Confirm Clear All",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            FileHandler.backupStudents();
            students.clear();
            FileHandler.saveStudents(students);
            refreshTable();
            JOptionPane.showMessageDialog(this, "✅ All students cleared!");
        }
    }

    // =========================
    // Restore Backup (Admin)
    // =========================
    private void showRestoreBackupDialog() {
        if (!isAdmin) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Restore backup? Current list will be replaced.",
                "Restore Backup",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = FileHandler.restoreBackup();

        if (!ok) {
            JOptionPane.showMessageDialog(this, "❌ No backup found.",
                    "Restore Backup", JOptionPane.ERROR_MESSAGE);
            return;
        }

        refreshTable();
        JOptionPane.showMessageDialog(this, "✅ Backup restored!");
    }

    // =========================
    // Validation
    // =========================
    private double parseMarkOrThrow(String text, String fieldName) {
        try {
            double mark = Double.parseDouble(text.trim());
            if (mark < 0 || mark > 100) throw new IllegalArgumentException(fieldName + " must be 0-100.");
            return mark;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }
}
