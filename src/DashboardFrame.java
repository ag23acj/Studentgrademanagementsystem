import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;



public class DashboardFrame extends JFrame {

    private final String subject1Name = "Programming";
    private final String subject2Name = "Database";
    private final String subject3Name = "Software Engineering";


    private List<Student> students;

    // Table components
    private DefaultTableModel tableModel;
    private JTable table;

    public DashboardFrame() {
        students = new java.util.ArrayList<>();


        setTitle("Student Grade Management System");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Student Grade Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel subtitle = new JLabel("Dashboard");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);


        // Buttons panel
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        Font btnFont = new Font("Segoe UI", Font.PLAIN, 14);

        JButton addBtn = new JButton("Add Student");
        JButton viewBtn = new JButton("View Students");
        JButton searchBtn = new JButton("Search Student");
        JButton deleteBtn = new JButton("Delete Student");
        JButton updateBtn = new JButton("Update Marks");
        JButton clearBtn = new JButton("Clear All");
        JButton saveExitBtn = new JButton("Save & Exit");
        JButton restoreBtn = new JButton("Restore Backup");


        JButton[] btns = {addBtn, viewBtn, searchBtn, deleteBtn, updateBtn, clearBtn,restoreBtn, saveExitBtn};
        for (JButton b : btns) {
            b.setFont(btnFont);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(200, 40));
            nav.add(b);
            nav.add(Box.createVerticalStrut(10));
        }

        // Load students at startup


// Button listeners (ONLY ONCE)
        addBtn.addActionListener(e -> showAddStudentDialog());

        viewBtn.addActionListener(e -> {
            students = FileHandler.loadStudents();
            refreshTable();
            JOptionPane.showMessageDialog(this, "Loaded " + students.size() + " students.");
        });

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





        // Table setup
        String[] columns = {"Student ID", "Name",
                subject1Name, subject1Name + " Status",
                subject2Name, subject2Name + " Status",
                subject3Name, subject3Name + " Status",
                "Avg", "Class"};


        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);

// Content panelstudents = FileHandler.loadStudents();
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        students = FileHandler.loadStudents();
        refreshTable();
        setVisible(true);


        add(nav, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);



        viewBtn.addActionListener(e -> refreshTable());

        saveExitBtn.addActionListener(e -> {
            FileHandler.saveStudents(students);
            JOptionPane.showMessageDialog(this, "Saved! Exiting...");
            System.exit(0);
        });

        refreshTable();

        setVisible(true);
    }



    private void refreshTable() {
        tableModel.setRowCount(0);

        for (Student s : students) {
            Object[] row = new Object[]{
                    s.getStudentId(),
                    s.getName(),

                    s.getSub1(),
                    s.getSub1Status(),

                    s.getSub2(),
                    s.getSub2Status(),

                    s.getSub3(),
                    s.getSub3Status(),

                    s.getAverage(),
                    s.getGrade()
            };

            tableModel.addRow(row);
        }
    }



    private void showSearchStudentDialog() {

        students = FileHandler.loadStudents();
        refreshTable();


        String id = JOptionPane.showInputDialog(this, "Enter Student ID to search:");

        if (id == null) return; // user pressed cancel
        id = id.trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID cannot be empty.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        refreshTable();

        for (int row = 0; row < table.getRowCount(); row++) {

            String rowId = table.getValueAt(row, 0).toString(); // column 0 = Student ID

            if (rowId.equals(id)) {
                table.setRowSelectionInterval(row, row);
                table.scrollRectToVisible(table.getCellRect(row, 0, true));

                String name = table.getValueAt(row, 1).toString();
                String avg = table.getValueAt(row, 8).toString();
                String grade = table.getValueAt(row, 9).toString();


                JOptionPane.showMessageDialog(this,
                        "✅ Student Found!\n\nID: " + id + "\nName: " + name + "\nAverage: " + avg + "\nClass: " + grade,
                        "Search Result",
                        JOptionPane.INFORMATION_MESSAGE);

                return;
            }
        }

        JOptionPane.showMessageDialog(this,
                "❌ Student with ID " + id + " not found.",
                "Search Result",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showDeleteStudentDialog() {

        // Always load latest data before deleting
        students = FileHandler.loadStudents();
        refreshTable();

        String id = JOptionPane.showInputDialog(this, "Enter Student ID to delete:");

        if (id == null) return; // Cancel
        id = id.trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID cannot be empty.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "❌ Student with ID " + id + " not found.",
                    "Delete Result", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this student?\n\nID: " + found.getStudentId() +
                        "\nName: " + found.getName() +
                        "\nClass: " + found.getGrade(),
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            students.remove(found);
            FileHandler.saveStudents(students);
            refreshTable();

            JOptionPane.showMessageDialog(this, "✅ Student deleted successfully!",
                    "Delete Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }



    private void showUpdateStudentDialog() {

        // Load latest data
        students = FileHandler.loadStudents();
        refreshTable();

        String id = JOptionPane.showInputDialog(this, "Enter Student ID to update:");

        if (id == null) return;
        id = id.trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID cannot be empty.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student found = null;
        int foundIndex = -1;

        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equals(id)) {
                found = students.get(i);
                foundIndex = i;
                break;
            }
        }

        if (found == null) {
            JOptionPane.showMessageDialog(this, "❌ Student with ID " + id + " not found.",
                    "Update Result", JOptionPane.ERROR_MESSAGE);
            return;
        }


        JTextField sub1Field = new JTextField(String.valueOf(found.getSub1()));
        JTextField sub2Field = new JTextField(String.valueOf(found.getSub2()));
        JTextField sub3Field = new JTextField(String.valueOf(found.getSub3()));

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Student ID:"));
        panel.add(new JLabel(found.getStudentId()));

        panel.add(new JLabel("Name:"));
        panel.add(new JLabel(found.getName()));

        panel.add(new JLabel("New Subject 1 mark (0-100):"));
        panel.add(sub1Field);

        panel.add(new JLabel("New Subject 2 mark (0-100):"));
        panel.add(sub2Field);

        panel.add(new JLabel("New Subject 3 mark (0-100):"));
        panel.add(sub3Field);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Update Student Marks",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            double newS1 = parseMarkOrThrow(sub1Field.getText(), "Subject 1");
            double newS2 = parseMarkOrThrow(sub2Field.getText(), "Subject 2");
            double newS3 = parseMarkOrThrow(sub3Field.getText(), "Subject 3");


            Student updated = new Student(found.getStudentId(), found.getName(), newS1, newS2, newS3);
            students.set(foundIndex, updated);

            FileHandler.saveStudents(students);
            refreshTable();

            JOptionPane.showMessageDialog(this,
                    "✅ Marks updated successfully!\nNew Class: " + updated.getGrade(),
                    "Update Result",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showClearAllDialog() {

        students = FileHandler.loadStudents();
        refreshTable();

        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students to clear.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "⚠ Are you sure you want to delete ALL students?\nThis cannot be undone.",
                "Confirm Clear All",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            FileHandler.backupStudents(); // backup before deleting everything

            students.clear();
            FileHandler.saveStudents(students);
            refreshTable();

            JOptionPane.showMessageDialog(this, "✅ All students cleared successfully.");
        }
    }

    private void showRestoreBackupDialog() {

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Restore last backup? This will replace current student list.",
                "Restore Backup",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = FileHandler.restoreBackup();

        if (!ok) {
            JOptionPane.showMessageDialog(this,
                    "❌ No backup found. Clear All must be used at least once (with backup enabled).",
                    "Restore Backup",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        students = FileHandler.loadStudents();
        refreshTable();

        JOptionPane.showMessageDialog(this, "✅ Backup restored successfully!");
    }







    private void showAddStudentDialog() {

        // Input fields
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField sub1Field = new JTextField();
        JTextField sub2Field = new JTextField();
        JTextField sub3Field = new JTextField();

        // Status dropdowns
        JComboBox<ExamStatus> s1StatusBox = new JComboBox<>(ExamStatus.values());
        JComboBox<ExamStatus> s2StatusBox = new JComboBox<>(ExamStatus.values());
        JComboBox<ExamStatus> s3StatusBox = new JComboBox<>(ExamStatus.values());

        // Panel layout
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

        panel.add(new JLabel("Student ID (numbers):"));
        panel.add(idField);

        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel(subject1Name + " mark (0-100):"));
        panel.add(sub1Field);
        panel.add(new JLabel("Subject 1 status:"));
        panel.add(s1StatusBox);

        panel.add(new JLabel(subject2Name + " mark (0-100):"));
        panel.add(sub2Field);
        panel.add(new JLabel("Subject 2 status:"));
        panel.add(s2StatusBox);

        panel.add(new JLabel(subject3Name + " mark (0-100):"));
        panel.add(sub3Field);
        panel.add(new JLabel("Subject 3 status:"));
        panel.add(s3StatusBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Student",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        // Read + validate
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
            double s1 = parseMarkOrThrow(sub1Field.getText(), "Subject 1");
            double s2 = parseMarkOrThrow(sub2Field.getText(), "Subject 2");
            double s3 = parseMarkOrThrow(sub3Field.getText(), "Subject 3");

            ExamStatus st1 = (ExamStatus) s1StatusBox.getSelectedItem();
            ExamStatus st2 = (ExamStatus) s2StatusBox.getSelectedItem();
            ExamStatus st3 = (ExamStatus) s3StatusBox.getSelectedItem();

            // load latest list to check duplicates properly
            students = FileHandler.loadStudents();

            for (Student s : students) {
                if (s.getStudentId().equals(id)) {
                    JOptionPane.showMessageDialog(this, "This Student ID already exists!",
                            "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            students.add(new Student(id, name, s1, st1, s2, st2, s3, st3));
            FileHandler.saveStudents(students);
            refreshTable();

            JOptionPane.showMessageDialog(this, "✅ Student added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double parseMarkOrThrow(String text, String fieldName) {
        try {
            double mark = Double.parseDouble(text.trim());
            if (mark < 0 || mark > 100) {
                throw new IllegalArgumentException(fieldName + " must be between 0 and 100.");
            }
            return mark;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }


}
