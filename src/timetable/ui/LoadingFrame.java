
package timetable.ui;

import timetable.model.User;
import javax.swing.*;

import java.awt.Color;
import java.awt.color.*;

public class LoadingFrame extends JFrame {

    public LoadingFrame(User user) {

        super("Login - SASS");

        JProgressBar bar = new JProgressBar(0, 100);

        bar.setStringPainted(true);

        add(bar);

        setSize(500,200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        bar.setForeground(new Color(176,224,230));
        Thread t = new Thread(() -> {

            for (int i = 0; i <= 40; i++) {

                final int progress = i;

                SwingUtilities.invokeLater(() -> {
                    bar.setValue(progress);
                });

                try {
                    Thread.sleep(10);
                    
                }
                catch (InterruptedException e) {
                    return;
                }
            }

            setTitle("Authorising user and password please wait");
            bar.setForeground(new Color(255,255,197));
            try {
                Thread.sleep(300);
            }
            catch (InterruptedException e) {
                return;
            }
            
            setTitle("Authorising user and password - success");
            bar.setForeground(new Color(176,224,230));
            
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
                return;
            }
            
            setTitle("Downloading Resourses....");
            
            for (int i = 41; i <= 80; i++) {

                final int progress = i;

                SwingUtilities.invokeLater(() -> {
                    bar.setValue(progress);
                });

                try {
                    Thread.sleep(25);
                }
                catch (InterruptedException e) {
                    return;
                }
            }
            setTitle("Deleting cache");
            bar.setForeground(new Color(255,255,197));
            
            try {
                Thread.sleep(700);
            }
            catch (InterruptedException e) {
                return;
            }
            
            bar.setForeground(new Color(176,224,230));
            for (int i = 81; i <= 99; i++) {

                final int progress = i;

                SwingUtilities.invokeLater(() -> {
                    bar.setValue(progress);
                });

                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    return;
                }
            }
            
    
            bar.setForeground(new Color(255,255,197));
            // walao who idea is this 5min of typing sia qVq
            try {
                setTitle("Loading TimetableApiAdapter.java ");
                Thread.sleep(100);

                setTitle("Loading TimetableController.java ");
                Thread.sleep(200);

                setTitle("Loading Availability.java ");
                Thread.sleep(100);

                setTitle("Loading ClassSession.java ");
                Thread.sleep(50);

                setTitle("Loading ConflictReport.java ");
                Thread.sleep(100);

                setTitle("Loading Course.java ");
                Thread.sleep(50);

                setTitle("Loading Laboratory.java ");
                Thread.sleep(100);

                setTitle("Loading Lecturer.java ");
                Thread.sleep(300);

                setTitle("Loading ProgrammeLeader.java ");
                Thread.sleep(100);

                setTitle("Loading Resource.java ");
                Thread.sleep(100);

                setTitle("Loading Room.java ");
                Thread.sleep(300);

                setTitle("Loading Student.java ");
                Thread.sleep(100);

                setTitle("Loading StudentGroup.java ");
                Thread.sleep(100);

                setTitle("Loading Timetable.java ");
                Thread.sleep(100);

                setTitle("Loading User.java ");
                Thread.sleep(100);

                setTitle("Loading TimetableExportQueue.java ");
                Thread.sleep(100);

                setTitle("Loading GenericRepository.java ");
                Thread.sleep(100);

                setTitle("Loading AuthService.java ");
                Thread.sleep(20);

                setTitle("Loading ConflictValidator.java ");
                Thread.sleep(20);

                setTitle("Loading SchedulingService.java ");
                Thread.sleep(100);

                setTitle("Loading GreedyStrategy.java ");
                Thread.sleep(20);

                setTitle("Loading PriorityStrategy.java ");
                Thread.sleep(20);

                setTitle("Loading SchedulingStrategy.java ");
                Thread.sleep(100);
                
                setTitle("Loading . . . . . .");

            bar.setForeground(new Color(176,224,230));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            for (int i = 99; i <= 101; i++) {

                final int progress = i;

                SwingUtilities.invokeLater(() -> {
                    bar.setValue(progress);
                });

                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    return;
                }
            }
            
            
            SwingUtilities.invokeLater(() -> {

           //:)
           
                dispose();

                new MainFrame(user).setVisible(true);

            });

        });

        t.start();
    }
}
