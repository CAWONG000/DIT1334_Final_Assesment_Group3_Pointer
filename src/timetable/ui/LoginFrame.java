package timetable.ui;

import timetable.model.User;
import timetable.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final AuthService auth = new AuthService();
    private final JTextField username = new JTextField(15);
    private final JPasswordField password = new JPasswordField(15);
    private final JLabel message = new JLabel("	Welcome ");

    public LoginFrame() {
        super("Login - Smart Academic Scheduling System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 35, 20, 5));
        form.add(new JLabel("Username"));
        form.add(username);
        form.add(new JLabel("Password"));
        form.add(password);

        message.setForeground(Color.BLACK);
        message.setHorizontalAlignment(SwingConstants.CENTER);

        JButton login = new JButton("Login");
        //login.setBackground(Color.BLUE);
        login.setBackground(new Color(176,224,230));
        
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
        while (user == null) {

            message.setText("Invalid username or password.");
            message.setForeground(Color.RED);

            Timer timer = new Timer(250, e -> {

                if (message.getForeground().equals(Color.RED))
                {message.setForeground(Color.BLACK);} 
                else {message.setForeground(Color.RED); }
            });
            
            timer.setRepeats(true);
            timer.start();
            return;
        }
        dispose();
        new MainFrame(user).setVisible(true);
    }
}
