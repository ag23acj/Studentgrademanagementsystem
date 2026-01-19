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
        students = FileHandler.loadStudents();

        setTitle("Student Grade Management System");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Title
        JLabel title = new JLabel("Student Grade Management System (Dashboard)", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttons = new JPanel(new GridLayout(2, 4, 12, 12));
        buttons.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton addBtn = new JButton("Add Student");
        JButton viewBtn = new JButton("View Students");
        JButton searchBtn = new JButton("Search Student");
        JButton deleteBtn = new JButton("Delete Student");
        JButton updateBtn = new JButton("Update Marks");
        JButton clearBtn = new JButton("Clear All");
        JButton saveExitBtn = new JButton("Save & Exit");

        buttons.add(addBtn);
        buttons.add(viewBtn);
        buttons.add(searchBtn);
        buttons.add(deleteBtn);
        buttons.add(updateBtn);
        buttons.add(clearBtn);
        buttons.add(saveExitBtn);
        addBtn.addActionListener(e -> showAddStudentDialog());


        // Table setup
        String[] columns = {"Student ID", "Name", "Sub1", "Sub2", "Sub3", "Average", "Class"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Layout: buttons on the left, table on the center
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(buttons, BorderLayout.NORTH);

        add(leftPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);


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

        students = FileHandler.loadStudents();

        tableModel.setRowCount(0); // clear table

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
            refreshTable();

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
