package main.ui;

import edu.css.java.ReportGenerator;
import edu.css.operations.DAOLoader;
import edu.css.operations.ExamDAO;
import edu.css.operations.StudentDAO;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class MainWindow extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JButton selectStudentButton;
    private JButton calculateButton;
    private JButton reportButton;
    private JButton viewStudentsButton;
    private static StudentDAO studentDAO = DAOLoader.getStudentDAO();
    private static ExamDAO examDAO = DAOLoader.getExamDAO();

    public MainWindow() {
        setContentPane(contentPane);
        setLocationRelativeTo(null);
        setModal(true);
        getRootPane().setDefaultButton(buttonCancel);

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        selectStudentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSelectStudent();
            }
        });

        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCalculate();
            }
        });

        reportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onReport();
            }
        });

        viewStudentsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onViewStudent();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void onSelectStudent(){
        SelectStudentWindow selectStudentWindow = new SelectStudentWindow();
        assert selectStudentWindow != null : AddStudentWindow.ASSERTION_FAIL + "onSelectStudent invalid object initialization";
        runWindow(selectStudentWindow);
    }

    private void onCalculate(){
        //todo: call calculate media to all students
        onViewStudent();
    }

    public static void onReport(){
        JFileChooser fileChooser = new JFileChooser(new File(".").getAbsoluteFile());
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "PDF files", "pdf");
        fileChooser.setFileFilter(filter);
        if(JFileChooser.APPROVE_OPTION != fileChooser.showSaveDialog(null)){
            return;
        }
        String filename = fileChooser.getSelectedFile().getAbsolutePath();
        assert filename != null : AddStudentWindow.ASSERTION_FAIL + "onReport, invalid filename path";

        assert studentDAO != null : AddStudentWindow.ASSERTION_FAIL + "onReport, invalid studentDAO object";
        assert examDAO != null : AddStudentWindow.ASSERTION_FAIL + "onReport, invalid examDAO object";

        ReportGenerator reportGenerator = new ReportGenerator(studentDAO.getStudents(), examDAO, filename);
        reportGenerator.generate();

        filename = reportGenerator.getOutputFileName();
        assert filename != null && filename.endsWith(".pdf") : AddStudentWindow.ASSERTION_FAIL + "onReport, invalid file format";
        runFile(filename);
    }

    public static void runFile(String filename){
        if (Desktop.isDesktopSupported()) {
            try {
                assert filename != null : AddStudentWindow.ASSERTION_FAIL + "runFile, invalid filename parameter";
                File myFile = new File(filename);
                if(!myFile.exists()){
                    showMessageWindow("File doesn't exist:\n\t " + filename,"Report");
                    return;
                }
                Desktop.getDesktop().open(myFile);
            } catch (IOException ex) {
                showMessageWindow("Could not open generated file:\n\t " + filename,"Report");
            }
        }
    }

    private void onViewStudent(){
        ShowResultsWindow showResultsWindow = new ShowResultsWindow();
        assert showResultsWindow != null : AddStudentWindow.ASSERTION_FAIL + "onViewStudent invalid object initialization";
        runWindow(showResultsWindow);
    }
    public static void runWindow(JDialog window){
        assert window != null : AddStudentWindow.ASSERTION_FAIL + "runWindow , invalid Parameter";
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public static void showMessageWindow(String message, String title){
        JOptionPane.showMessageDialog(null,message,title,JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println(e);
        }
        //assert false;
        MainWindow dialog = new MainWindow();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
