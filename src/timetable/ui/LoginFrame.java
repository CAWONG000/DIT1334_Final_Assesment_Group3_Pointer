package timetable.ui;

import timetable.model.User;
import timetable.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final AuthService auth = new AuthService();
    private final JTextField username = new JTextField(15);
    private final JPasswordField password = new JPasswordField(15);
    private final JLabel message = new JLabel(" ");

    public LoginFrame() {
        super("Login - Smart Academic Scheduling System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        form.add(new JLabel("Username"));
        form.add(username);
        form.add(new JLabel("Password"));
        form.add(password);

        message.setForeground(Color.RED);
        message.setHorizontalAlignment(SwingConstants.CENTER);

        JButton login = new JButton("Login");
        login.addActionListener(e -> doLogin());
        password.addActionListener(e -> doLogin());   // pressing Enter also logs in

        add(form, BorderLayout.NORTH);
        add(message, BorderLayout.CENTER);
        add(login, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    private void doLogin() {
        User user = auth.authenticate(username.getText().trim(),
                new String(password.getPassword()));
        if (user == null) {
            message.setText("Invalid username or password.");
            return;
        }
        dispose();
        new MainFrame(user).setVisible(true);
    }
}
