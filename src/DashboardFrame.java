import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {

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

        JButton[] btns = {addBtn, viewBtn, searchBtn, deleteBtn, updateBtn, clearBtn, saveExitBtn};
        for (JButton b : btns) {
            b.setFont(btnFont);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(200, 40));
            nav.add(b);
            nav.add(Box.createVerticalStrut(10));
        }
        // Button listeners
        addBtn.addActionListener(e -> showAddStudentDialog());

        viewBtn.addActionListener(e -> {
            students = FileHandler.loadStudents();
            refreshTable();
            JOptionPane.showMessageDialog(
                    this,
                    "Loaded " + students.size() + " students."
            );
        });


        saveExitBtn.addActionListener(e -> {
            FileHandler.saveStudents(students);
            JOptionPane.showMessageDialog(this, "Saved! Exiting...");
            System.exit(0);
        });
        addBtn.addActionListener(e -> showAddStudentDialog());

        viewBtn.addActionListener(e -> refreshTable());

        searchBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Search feature will be connected next.")
        );

        deleteBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Delete feature will be connected next.")
        );

        updateBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Update feature will be connected next.")
        );

        clearBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Clear All feature will be connected next.")
        );

        saveExitBtn.addActionListener(e -> {
            FileHandler.saveStudents(students);
            JOptionPane.showMessageDialog(this, "Saved! Exiting...");
            System.exit(0);
        });




        // Table setup
        String[] columns = {"Student ID", "Name", "Sub1", "Sub2", "Sub3", "Average", "Class"};
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
            Object[] row = {
                    s.getStudentId(),
                    s.getName(),
                    s.getSub1(),
                    s.getSub2(),
                    s.getSub3(),
                    s.getAverage(),
                    s.getGrade()
            };
            tableModel.addRow(row);
        }
    }


    private void showAddStudentDialog() {

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField sub1Field = new JTextField();
        JTextField sub2Field = new JTextField();
        JTextField sub3Field = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Student ID (numbers):"));
        panel.add(idField);

        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Subject 1 mark (0-100):"));
        panel.add(sub1Field);

        panel.add(new JLabel("Subject 2 mark (0-100):"));
        panel.add(sub2Field);

        panel.add(new JLabel("Subject 3 mark (0-100):"));
        panel.add(sub3Field);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Student",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        // Validation
        String id = idField.getText().trim();
        String name = nameField.getText().trim();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID and Name cannot be empty.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!id.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Student ID must contain only numbers.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double s1 = parseMarkOrThrow(sub1Field.getText(), "Subject 1");
            double s2 = parseMarkOrThrow(sub2Field.getText(), "Subject 2");
            double s3 = parseMarkOrThrow(sub3Field.getText(), "Subject 3");

            // Check duplicate ID
            for (Student s : students) {
                if (s.getStudentId().equals(id)) {
                    JOptionPane.showMessageDialog(this, "This Student ID already exists!", "Duplicate ID",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            students.add(new Student(id, name, s1, s2, s3));
            //refreshTable();

            JOptionPane.showMessageDialog(this, "✅ Student added successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
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
