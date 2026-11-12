package timetable.ui;

import timetable.controller.TimetableController;
import timetable.model.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/** The window only draws the screen and passes user actions to TimetableController. */
public class MainFrame extends JFrame {
    private final TimetableController controller = TimetableController.getInstance();
    private final User currentUser;
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Day", "Start", "End", "Course", "Lecturer", "Group", "Room", "Type"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel status = new JLabel("Ready");
    private final JTextField search = new JTextField(16);
    private final JComboBox<String> courseFilter = new JComboBox<>();

    public MainFrame(User user) {
        super("Smart Academic Scheduling System");
        this.currentUser = user;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1400, 650);
        setLocationRelativeTo(null);
        buildUi();
    }

    private void buildUi() {
        JPanel top = new JPanel();
        top.setPreferredSize(new Dimension(1400, 90));
        JButton addLecture = new JButton("Add Lecture");
        JButton addExtra = new JButton("Add Extra Class");
        JButton generate = new JButton("Generate Timetable");
        JButton validate = new JButton("Validate");
        JButton export = new JButton("Export CSV");
        JButton exportSummary = new JButton("Export Summary");
        JButton publish = new JButton("Queue Cloud Records");
        JButton reset = new JButton("Reset Timetable");
        JButton register = new JButton("Register Resource");
        JButton submitAvail = new JButton("Submit Availability");
        JButton logout = new JButton("Logout");

        courseFilter.addItem("All courses");
        for (Course c : controller.getCourses()) {
            courseFilter.addItem(c.getCourseCode());
        }

        top.add(addLecture); top.add(addExtra); top.add(generate); top.add(validate);
        top.add(new JLabel("Search:")); top.add(search);
        top.add(new JLabel("Course:")); top.add(courseFilter);
        top.add(export); top.add(exportSummary); top.add(publish);
        top.add(reset); top.add(register); top.add(submitAvail); top.add(logout);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        addLecture.addActionListener(e -> showAddSession(false));
        addExtra.addActionListener(e -> showAddSession(true));
        generate.addActionListener(e -> runGenerate());
        validate.addActionListener(e -> runValidate());
        search.addActionListener(e -> filterTable());
        courseFilter.addActionListener(e -> refreshTable());
        export.addActionListener(e -> exportCsv());
        exportSummary.addActionListener(e -> exportSummaryFile());
        publish.addActionListener(e -> publishRecords());
        register.addActionListener(e -> showRegisterResource());
        submitAvail.addActionListener(e -> showSubmitAvailability());
        reset.addActionListener(e -> {
            controller.reset();
            courseFilter.setSelectedIndex(0);
            refreshTable();
            status.setText("Timetable and resources reset to default state.");
            JOptionPane.showMessageDialog(this, "Demo data has been reset!", "Reset Successful",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        logout.addActionListener(e -> {
            currentUser.logout();
            dispose();
            new LoginFrame().setVisible(true);
        });

        // Role rules: only the Programme Leader can change data. Lecturers and students view and search.
        if (!(currentUser instanceof ProgrammeLeader)) {
            addLecture.setEnabled(false);
            addExtra.setEnabled(false);
            generate.setEnabled(false);
            validate.setEnabled(false);
            publish.setEnabled(false);
            reset.setEnabled(false);
            register.setEnabled(false);
        }
        submitAvail.setEnabled(currentUser instanceof Lecturer);

        refreshTable();
        status.setText(currentUser.viewTimetable());
    }

    // ---------- table ----------

    private void refreshTable() {
        String selected = (String) courseFilter.getSelectedItem();
        List<ClassSession> sessions = (selected == null || selected.equals("All courses"))
                ? controller.getSessionsFor(currentUser)
                : controller.getSessionsByCourse(currentUser, selected);
        model.setRowCount(0);
        for (ClassSession s : sessions) {
            model.addRow(new Object[]{s.getDay(), s.getStartTime(), s.getEndTime(),
                    s.getCourse().getCourseCode(), s.getLecturer().getLecturerName(),
                    s.getGroup().getGroupId(), s.getResource().getName(),
                    s.isExtraClass() ? "Extra Class" : "Lecture"});
        }
        filterTable();
    }

    private void filterTable() {
        String q = search.getText().trim().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        sorter.setRowFilter(q.isEmpty() ? null
                : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(q)));
    }

    // ---------- dialogs ----------

    private void showAddSession(boolean extra) {
        JComboBox<Course> courseBox = new JComboBox<>(controller.getCourses().toArray(new Course[0]));
        JComboBox<Lecturer> lecturerBox = new JComboBox<>(controller.getLecturers().toArray(new Lecturer[0]));
        JComboBox<StudentGroup> groupBox = new JComboBox<>(controller.getGroups().toArray(new StudentGroup[0]));
        JComboBox<Resource> resourceBox = new JComboBox<>(controller.getResources().toArray(new Resource[0]));
        JComboBox<DayOfWeek> dayBox = new JComboBox<>(DayOfWeek.values());
        JTextField start = new JTextField("09:00");
        JTextField end = new JTextField("11:00");
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Course")); p.add(courseBox);
        p.add(new JLabel("Lecturer")); p.add(lecturerBox);
        p.add(new JLabel("Student group")); p.add(groupBox);
        p.add(new JLabel("Room/Laboratory")); p.add(resourceBox);
        p.add(new JLabel("Day")); p.add(dayBox);
        p.add(new JLabel("Start (HH:mm)")); p.add(start);
        p.add(new JLabel("End (HH:mm)")); p.add(end);
        int answer = JOptionPane.showConfirmDialog(this, p, extra ? "Add Extra Class" : "Add Lecture",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (answer != JOptionPane.OK_OPTION) return;
        try {
            List<String> errors = controller.addSession((Course) courseBox.getSelectedItem(),
                    (Lecturer) lecturerBox.getSelectedItem(), (StudentGroup) groupBox.getSelectedItem(),
                    (Resource) resourceBox.getSelectedItem(), (DayOfWeek) dayBox.getSelectedItem(),
                    start.getText(), end.getText(), extra);
            if (!errors.isEmpty()) {
                JOptionPane.showMessageDialog(this, String.join("\n", errors), "Conflict",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                status.setText(extra ? "Extra class added" : "Lecture added");
                refreshTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please check the entered details: " + ex.getMessage(),
                    "Input error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegisterResource() {
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Room", "Laboratory"});
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField capacityField = new JTextField("30");
        JTextField equipmentField = new JTextField("Computers");
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Type")); p.add(typeBox);
        p.add(new JLabel("ID")); p.add(idField);
        p.add(new JLabel("Name")); p.add(nameField);
        p.add(new JLabel("Capacity")); p.add(capacityField);
        p.add(new JLabel("Equipment (labs only)")); p.add(equipmentField);
        int answer = JOptionPane.showConfirmDialog(this, p, "Register Resource",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (answer != JOptionPane.OK_OPTION) return;
        try {
            Resource created = controller.registerResource((String) typeBox.getSelectedItem(),
                    idField.getText(), nameField.getText(), capacityField.getText(), equipmentField.getText());
            status.setText("Registered " + created);
            JOptionPane.showMessageDialog(this, "Registered: " + created, "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Capacity must be a whole number.", "Input error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSubmitAvailability() {
        JComboBox<DayOfWeek> dayBox = new JComboBox<>(DayOfWeek.values());
        JTextField start = new JTextField("08:00");
        JTextField end = new JTextField("18:00");
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Day")); p.add(dayBox);
        p.add(new JLabel("Available from (HH:mm)")); p.add(start);
        p.add(new JLabel("Available until (HH:mm)")); p.add(end);
        int answer = JOptionPane.showConfirmDialog(this, p, "Submit Availability",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (answer != JOptionPane.OK_OPTION) return;
        try {
            DayOfWeek day = (DayOfWeek) dayBox.getSelectedItem();
            controller.submitAvailability((Lecturer) currentUser, day, start.getText(), end.getText());
            status.setText("Availability submitted for " + day);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please check the entered details: " + ex.getMessage(),
                    "Input error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- background tasks ----------

    private void runGenerate() {
        JComboBox<Course> courseBox = new JComboBox<>(controller.getCourses().toArray(new Course[0]));
        JComboBox<Lecturer> lecturerBox = new JComboBox<>(controller.getLecturers().toArray(new Lecturer[0]));
        JComboBox<StudentGroup> groupBox = new JComboBox<>(controller.getGroups().toArray(new StudentGroup[0]));
        JComboBox<Resource> resourceBox = new JComboBox<>(controller.getResources().toArray(new Resource[0]));
        JSpinner hoursSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 4, 1));
        JComboBox<String> strategyBox = new JComboBox<>(
                new String[]{"Greedy (earliest slot)", "Priority-Based"});
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Course")); p.add(courseBox);
        p.add(new JLabel("Lecturer")); p.add(lecturerBox);
        p.add(new JLabel("Student group")); p.add(groupBox);
        p.add(new JLabel("Room/Laboratory")); p.add(resourceBox);
        p.add(new JLabel("Hours")); p.add(hoursSpinner);
        p.add(new JLabel("Strategy")); p.add(strategyBox);
        int answer = JOptionPane.showConfirmDialog(this, p, "Generate Timetable Slot",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (answer != JOptionPane.OK_OPTION) return;

        String strategyName = strategyBox.getSelectedIndex() == 0 ? "Greedy" : "Priority-Based";
        Course course = (Course) courseBox.getSelectedItem();
        Lecturer lecturer = (Lecturer) lecturerBox.getSelectedItem();
        StudentGroup group = (StudentGroup) groupBox.getSelectedItem();
        Resource resource = (Resource) resourceBox.getSelectedItem();
        int hours = (Integer) hoursSpinner.getValue();

        SwingWorker<ClassSession, Void> worker = new SwingWorker<>() {
            @Override protected ClassSession doInBackground() {
                return controller.generateSession(strategyName, course, lecturer, group, resource, hours);
            }
            @Override protected void done() {
                try {
                    ClassSession s = get();
                    if (s == null) {
                        status.setText("No free slot found.");
                        JOptionPane.showMessageDialog(MainFrame.this, "No conflict-free slot was found.",
                                "Generate", JOptionPane.WARNING_MESSAGE);
                    } else {
                        refreshTable();
                        status.setText("Generated " + s.getDay() + " " + s.getStartTime()
                                + "-" + s.getEndTime() + " with " + strategyName);
                    }
                } catch (Exception ex) {
                    status.setText("Generation failed: " + ex.getMessage());
                }
            }
        };
        status.setText("Generating timetable...");
        worker.execute();
    }

    private void runValidate() {
        SwingWorker<ConflictReport, Void> worker = new SwingWorker<>() {
            @Override protected ConflictReport doInBackground() {
                return controller.validateAll();
            }
            @Override protected void done() {
                try {
                    ConflictReport report = get();
                    JOptionPane.showMessageDialog(MainFrame.this, report.getDetails(), "Validation Results",
                            report.hasConflicts() ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
                    status.setText(report.hasConflicts() ? "Conflicts detected" : "Validation passed");
                } catch (Exception ex) {
                    status.setText("Validation failed");
                }
            }
        };
        status.setText("Validating in background...");
        worker.execute();
    }

    private void publishRecords() {
        controller.queueRecords();
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override protected Void doInBackground() {
                Map<String, Object> record;
                while ((record = controller.pollRecord()) != null) {
                    // Placeholder: insert HTTP API call here; this is queue processing, not an actual cloud upload.
                    publish("Prepared cloud record: " + record.get("sessionId"));
                }
                return null;
            }
            @Override protected void process(List<String> messages) {
                if (!messages.isEmpty()) status.setText(messages.get(messages.size() - 1));
            }
            @Override protected void done() { status.setText("Queue processed (cloud API not configured)"); }
        };
        worker.execute();
    }

    // ---------- export ----------

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("timetable.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            controller.exportCsv(chooser.getSelectedFile(), controller.getSessionsFor(currentUser));
            status.setText("CSV exported");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
        }
    }

    private void exportSummaryFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("timetable_summary.txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        boolean ok = controller.exportSummary(chooser.getSelectedFile().getPath());
        status.setText(ok ? "Summary exported" : "Summary export failed");
    }
}