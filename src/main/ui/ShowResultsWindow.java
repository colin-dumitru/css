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

        assert studentDAO != null : AddStudentWindow.ASSERTION_FAIL + "updateModel, studentDAO cannot be null";
        assert examDAO != null : AddStudentWindow.ASSERTION_FAIL + "updateModel, examDAO cannot be null";

        List<Student> studentList = studentDAO.getStudents();
        assert studentList != null : AddStudentWindow.ASSERTION_FAIL + "updateModel, studentsList cannot be null";

        String[] columnNames = StudentExportMetadata.columnNames;
        assert columnNames != null && columnNames.length == 6 : AddStudentWindow.ASSERTION_FAIL +
                                                                "updateModel, invalid columnNames array";
        DefaultTableModel tableModel = new DefaultTableModel(columnNames,studentList.size());
        Object[][] data = new Object[studentList.size()][columnNames.length];
        for (int i = 0; i < studentList.size(); i++) {

            Student student = studentList.get(i);
            Exam exam = examDAO.getExamForStudent(student);

            assert student != null : AddStudentWindow.ASSERTION_FAIL + "updateModel, student cannot be null";
            assert exam != null : AddStudentWindow.ASSERTION_FAIL + "updateModel, exam cannot be null";

            data[i] = StudentExportMetadata.getDataVector(student, exam);
        }

        assert data != null && data.length == studentList.size() : AddStudentWindow.ASSERTION_FAIL +
                                                                   "updateModel, invalid data";

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
