import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardFrame extends JFrame {

    private List<Student> students = new ArrayList<>();
    private final boolean isAdmin;

    private DefaultTableModel tableModel;
    private JTable table;

    // Buttons (ALL visible)
    private JButton addBtn, viewBtn, searchBtn, deleteBtn, updateBtn;
    private JButton approveBtn, clearBtn, restoreBtn, saveExitBtn;

    public DashboardFrame(boolean isAdmin) {
        this.isAdmin = isAdmin;

        setTitle("Student Grade Management System");
        setSize(1280, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        buildHeader();
        buildLeftButtons();
        buildTable();
        connectListeners();

        refreshTable(); // load from CSV and show

        setVisible(true);
    }

    // =========================
    // Header + Legend
    // =========================
    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JLabel title = new JLabel("Student Grade Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel subtitle = new JLabel("Dashboard  |  Role: " + (isAdmin ? "Admin" : "User"));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        legend.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JLabel yellow = new JLabel("  Borderline  ");
        yellow.setOpaque(true);
        yellow.setBackground(new Color(255, 255, 204));

        JLabel green = new JLabel("  Approved Upgrade  ");
        green.setOpaque(true);
        green.setBackground(new Color(204, 255, 204));

        legend.add(yellow);
        legend.add(green);

        JPanel top = new JPanel(new GridLayout(0, 1));
        top.add(title);
        top.add(subtitle);
        top.add(legend);

        header.add(top, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
    }

    // =========================
    // Left Buttons
    // =========================
    private void buildLeftButtons() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        Font btnFont = new Font("Segoe UI", Font.PLAIN, 14);

        addBtn = new JButton("Add Student");
        viewBtn = new JButton("View Students");
        searchBtn = new JButton("Search Student");
        deleteBtn = new JButton("Delete Student");
        updateBtn = new JButton("Update Marks");

        approveBtn = new JButton("Approve Upgrade");   // admin-only
        clearBtn = new JButton("Clear All");           // admin-only
        restoreBtn = new JButton("Restore Backup");    // admin-only

        saveExitBtn = new JButton("Save & Exit");

        JButton[] btns = {
                addBtn, viewBtn, searchBtn, deleteBtn, updateBtn,
                approveBtn, clearBtn, restoreBtn, saveExitBtn
        };

        for (JButton b : btns) {
            b.setFont(btnFont);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(230, 42));
            nav.add(b);
            nav.add(Box.createVerticalStrut(10));
        }

        // Buttons are NOT removed — only disabled if not admin
        approveBtn.setEnabled(isAdmin);
        clearBtn.setEnabled(isAdmin);
        restoreBtn.setEnabled(isAdmin);

        add(nav, BorderLayout.WEST);
    }

    // =========================
    // Table
    // =========================
    private void buildTable() {
        String[] columns = {
                "Student ID", "Name",
                "Module 1", "Mark 1", "Status 1",
                "Module 2", "Mark 2", "Status 2",
                "Module 3", "Mark 3", "Status 3",
                "Average", "Final Outcome",
                "Borderline", "Approved"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);

        // ✅ Row highlight: Borderline = yellow, Approved = green
        table.setDefaultRenderer(Object.class, new RowHighlightRenderer());

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    // =========================
    // Refresh Table (always loads from CSV)
    // =========================
    private void refreshTable() {
        students = FileHandler.loadStudents();
        tableModel.setRowCount(0);

        for (Student s : students) {
            tableModel.addRow(new Object[]{
                    s.getStudentId(),
                    s.getName(),

                    s.getModule1Name(),
                    s.getSub1(),
                    statusToLabel(s.getSub1Status()),

                    s.getModule2Name(),
                    s.getSub2(),
                    statusToLabel(s.getSub2Status()),

                    s.getModule3Name(),
                    s.getSub3(),
                    statusToLabel(s.getSub3Status()),

                    String.format("%.2f", s.getAverage()),
                    s.getGrade(),

                    s.isBorderline() ? "Yes" : "No",
                    s.isUpgradeApproved() ? "Yes" : "No"
            });
        }
    }

    private String statusToLabel(ExamStatus s) {
        if (s == ExamStatus.FIRST_SITTING) return "First Sitting";
        if (s == ExamStatus.RESIT) return "Resit";
        if (s == ExamStatus.ABSENT) return "Absent";
        return s.toString();
    }

    // =========================
    // Listeners
    // =========================
    private void connectListeners() {

        addBtn.addActionListener(e -> showAddStudentDialog());
        viewBtn.addActionListener(e -> refreshTable());

        searchBtn.addActionListener(e -> showSearchStudentDialog());
        deleteBtn.addActionListener(e -> showDeleteStudentDialog());
        updateBtn.addActionListener(e -> showUpdateStudentDialog());

        approveBtn.addActionListener(e -> approveSelectedStudent());
        clearBtn.addActionListener(e -> showClearAllDialog());
        restoreBtn.addActionListener(e -> showRestoreBackupDialog());

        saveExitBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Saved! Exiting...");
            System.exit(0);
        });
    }

    // =========================
    // Row Highlight Renderer
    // =========================
    private class RowHighlightRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            if (isSelected) return c;

            int modelRow = table.convertRowIndexToModel(row);

            String borderline = tableModel.getValueAt(modelRow, 13).toString(); // Borderline column
            String approved   = tableModel.getValueAt(modelRow, 14).toString(); // Approved column

            if (approved.equalsIgnoreCase("Yes")) {
                c.setBackground(new Color(204, 255, 204)); // light green
            } else if (borderline.equalsIgnoreCase("Yes")) {
                c.setBackground(new Color(255, 255, 204)); // light yellow
            } else {
                c.setBackground(Color.WHITE);
            }

            return c;
        }
    }

    // =========================
    // Dialogs
    // =========================
    private void showAddStudentDialog() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();

        JTextField m1Field = new JTextField("Programming");
        JTextField m2Field = new JTextField("Database Systems");
        JTextField m3Field = new JTextField("Software Engineering");

        JTextField s1Field = new JTextField();
        JTextField s2Field = new JTextField();
        JTextField s3Field = new JTextField();

        JComboBox<ExamStatus> st1Box = new JComboBox<>(ExamStatus.values());
        JComboBox<ExamStatus> st2Box = new JComboBox<>(ExamStatus.values());
        JComboBox<ExamStatus> st3Box = new JComboBox<>(ExamStatus.values());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Student ID (numbers):")); panel.add(idField);
        panel.add(new JLabel("Name:")); panel.add(nameField);

        panel.add(new JLabel("Module 1:")); panel.add(m1Field);
        panel.add(new JLabel("Mark 1 (0-100):")); panel.add(s1Field);
        panel.add(new JLabel("Status 1:")); panel.add(st1Box);

        panel.add(new JLabel("Module 2:")); panel.add(m2Field);
        panel.add(new JLabel("Mark 2 (0-100):")); panel.add(s2Field);
        panel.add(new JLabel("Status 2:")); panel.add(st2Box);

        panel.add(new JLabel("Module 3:")); panel.add(m3Field);
        panel.add(new JLabel("Mark 3 (0-100):")); panel.add(s3Field);
        panel.add(new JLabel("Status 3:")); panel.add(st3Box);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Add Student",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) return;

        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();

            if (id.isEmpty() || name.isEmpty()) throw new IllegalArgumentException("ID and Name required.");
            if (!id.matches("\\d+")) throw new IllegalArgumentException("Student ID must be numbers only.");

            double mark1 = parseMark(s1Field.getText().trim());
            double mark2 = parseMark(s2Field.getText().trim());
            double mark3 = parseMark(s3Field.getText().trim());

            ExamStatus status1 = (ExamStatus) st1Box.getSelectedItem();
            ExamStatus status2 = (ExamStatus) st2Box.getSelectedItem();
            ExamStatus status3 = (ExamStatus) st3Box.getSelectedItem();

            students = FileHandler.loadStudents();

            for (Student s : students) {
                if (s.getStudentId().equals(id)) {
                    JOptionPane.showMessageDialog(this, "This Student ID already exists!", "Duplicate",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            students.add(new Student(
                    id, name,
                    m1Field.getText().trim(), mark1, status1,
                    m2Field.getText().trim(), mark2, status2,
                    m3Field.getText().trim(), mark3, status3
            ));

            FileHandler.saveStudents(students);
            refreshTable();
            JOptionPane.showMessageDialog(this, "✅ Student added and saved!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Add Student",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private double parseMark(String s) {
        double v = Double.parseDouble(s);
        if (v < 0 || v > 100) throw new IllegalArgumentException("Marks must be between 0 and 100.");
        return v;
    }

    private void showSearchStudentDialog() {
        refreshTable();

        String id = JOptionPane.showInputDialog(this, "Enter Student ID to search:");
        if (id == null) return;
        id = id.trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID cannot be empty.");
            return;
        }

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String rowId = tableModel.getValueAt(row, 0).toString();
            if (rowId.equals(id)) {
                table.setRowSelectionInterval(row, row);
                table.scrollRectToVisible(table.getCellRect(row, 0, true));
                JOptionPane.showMessageDialog(this, "✅ Found Student ID: " + id);
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "❌ Student with ID " + id + " not found.");
    }

    private void showDeleteStudentDialog() {
        students = FileHandler.loadStudents();

        String id = JOptionPane.showInputDialog(this, "Enter Student ID to delete:");
        if (id == null) return;
        id = id.trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID cannot be empty.");
            return;
        }

        Student found = null;
        for (Student s : students) {
            if (s.getStudentId().equals(id)) {
                found = s;
                break;
            }
        }

        if (found == null) {
            JOptionPane.showMessageDialog(this, "❌ Student not found.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete student?\n\nID: " + found.getStudentId() + "\nName: " + found.getName(),
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            students.remove(found);
            FileHandler.saveStudents(students);
            refreshTable();
            JOptionPane.showMessageDialog(this, "✅ Deleted.");
        }
    }

    private void showUpdateStudentDialog() {
        students = FileHandler.loadStudents();

        String id = JOptionPane.showInputDialog(this, "Enter Student ID to update:");
        if (id == null) return;
        id = id.trim();

        int idx = -1;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equals(id)) {
                idx = i;
                break;
            }
        }

        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "❌ Student not found.");
            return;
        }

        Student old = students.get(idx);

        JTextField s1Field = new JTextField(String.valueOf(old.getSub1()));
        JTextField s2Field = new JTextField(String.valueOf(old.getSub2()));
        JTextField s3Field = new JTextField(String.valueOf(old.getSub3()));

        JComboBox<ExamStatus> st1Box = new JComboBox<>(ExamStatus.values());
        JComboBox<ExamStatus> st2Box = new JComboBox<>(ExamStatus.values());
        JComboBox<ExamStatus> st3Box = new JComboBox<>(ExamStatus.values());

        st1Box.setSelectedItem(old.getSub1Status());
        st2Box.setSelectedItem(old.getSub2Status());
        st3Box.setSelectedItem(old.getSub3Status());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Student ID:")); panel.add(new JLabel(old.getStudentId()));
        panel.add(new JLabel("Name:")); panel.add(new JLabel(old.getName()));

        panel.add(new JLabel(old.getModule1Name() + " mark:")); panel.add(s1Field);
        panel.add(new JLabel(old.getModule1Name() + " status:")); panel.add(st1Box);

        panel.add(new JLabel(old.getModule2Name() + " mark:")); panel.add(s2Field);
        panel.add(new JLabel(old.getModule2Name() + " status:")); panel.add(st2Box);

        panel.add(new JLabel(old.getModule3Name() + " mark:")); panel.add(s3Field);
        panel.add(new JLabel(old.getModule3Name() + " status:")); panel.add(st3Box);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Marks",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        try {
            double n1 = parseMark(s1Field.getText().trim());
            double n2 = parseMark(s2Field.getText().trim());
            double n3 = parseMark(s3Field.getText().trim());

            ExamStatus ns1 = (ExamStatus) st1Box.getSelectedItem();
            ExamStatus ns2 = (ExamStatus) st2Box.getSelectedItem();
            ExamStatus ns3 = (ExamStatus) st3Box.getSelectedItem();

            Student updated = new Student(
                    old.getStudentId(), old.getName(),
                    old.getModule1Name(), n1, ns1,
                    old.getModule2Name(), n2, ns2,
                    old.getModule3Name(), n3, ns3
            );

            // Keep previous approval flag
            updated.setUpgradeApproved(old.isUpgradeApproved());
            updated.applyApprovedUpgrade();

            students.set(idx, updated);
            FileHandler.saveStudents(students);
            refreshTable();

            JOptionPane.showMessageDialog(this, "✅ Updated and saved.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void approveSelectedStudent() {
        if (!isAdmin) return;

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student row first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        String id = tableModel.getValueAt(modelRow, 0).toString();

        students = FileHandler.loadStudents();

        for (Student s : students) {
            if (s.getStudentId().equals(id)) {

                if (!s.isBorderline()) {
                    JOptionPane.showMessageDialog(this, "This student is NOT borderline.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Approve upgrade for Student ID: " + id + "?",
                        "Confirm Upgrade",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm != JOptionPane.YES_OPTION) return;

                s.setUpgradeApproved(true);
                s.applyApprovedUpgrade();

                FileHandler.saveStudents(students);
                refreshTable();

                JOptionPane.showMessageDialog(this, "✅ Approved & saved!");
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Student not found.");
    }

    private void showClearAllDialog() {
        if (!isAdmin) return;

        students = FileHandler.loadStudents();
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students to clear.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "⚠ Clear ALL students?\nA backup will be created first.",
                "Confirm Clear All",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            FileHandler.backupStudents();
            FileHandler.saveStudents(new ArrayList<>());
            refreshTable();
            JOptionPane.showMessageDialog(this, "✅ Cleared all (backup created).");
        }
    }

    private void showRestoreBackupDialog() {
        if (!isAdmin) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Restore last backup? This will replace current student list.",
                "Restore Backup",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = FileHandler.restoreBackup();
        refreshTable();

        JOptionPane.showMessageDialog(this,
                ok ? "✅ Backup restored." : "❌ No backup found.");
    }
}
