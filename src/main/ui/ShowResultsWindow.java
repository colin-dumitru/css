package main.ui;

import edu.css.model.Exam;
import edu.css.model.Student;
import edu.css.model.StudentExportMetadata;
import edu.css.operations.DAOLoader;
import edu.css.operations.ExamDAO;
import edu.css.operations.StudentDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List;

public class ShowResultsWindow extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable studentsTable;
    private JButton reportButton;

    private StudentDAO studentDAO = DAOLoader.getStudentDAO();
    private ExamDAO examDAO = DAOLoader.getExamDAO();

    public ShowResultsWindow() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        updateModel();

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        reportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onReportButtonClick();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
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

    private void updateModel(){
        List<Student> studentList = studentDAO.getStudents();
        String[] columnNames = StudentExportMetadata.columnNames;
        DefaultTableModel tableModel = new DefaultTableModel(columnNames,studentList.size());
        Object[][] data = new Object[studentList.size()][columnNames.length];
        for (int i = 0; i < studentList.size(); i++) {

            Student student = studentList.get(i);
            Exam exam = examDAO.getExamForStudent(student);

            data[i] = StudentExportMetadata.getDataVector(student, exam);
        }
        tableModel.setDataVector(data,columnNames);
        studentsTable.setModel(tableModel);
    }

    private void onReportButtonClick(){
        MainWindow.onReport();
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }
}
