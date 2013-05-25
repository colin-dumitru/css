package main.ui;

import edu.css.model.Exam;
import edu.css.model.Student;
import edu.css.operations.DAOLoader;
import edu.css.operations.ExamDAO;
import edu.css.operations.StudentDAO;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class SelectStudentWindow extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox studentsComboBox;
    private JButton editButton;
    private JButton deleteButton;
    private JButton addButton;

    private StudentDAO studentDAO = DAOLoader.getStudentDAO();
    private ExamDAO examDAO = DAOLoader.getExamDAO();

    public SelectStudentWindow() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onEditButtonClick();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDeleteButtonClick();
            }
        });

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAddButtonClick();
            }
        });

        studentsComboBox.setEditable(false);
        setStudentsComboBox();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void setStudentsComboBox(){
        //call to get Students
        assert studentDAO != null : AddStudentWindow.ASSERTION_FAIL + "setStudentsComboBox, studentDAO cannot be null";
        List<Student> studentList = studentDAO.getStudents();
        assert studentList != null : AddStudentWindow.ASSERTION_FAIL + "setStudentsComboBox, studentsList cannot be null";

        DefaultComboBoxModel model = new DefaultComboBoxModel(studentList.toArray());
        if(model.getSize() == 0){
            model.addElement("No items");
        }else{
            model.setSelectedItem(studentList.get(0));
        }
        studentsComboBox.setModel(model);
        studentsComboBox.updateUI();
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private void onDeleteButtonClick(){
        Student student = getSelectedIndex("Delete");
        assert student != null : AddStudentWindow.ASSERTION_FAIL + "onDeleteButtonClick, student cannot be null";

        if(student != null){
            assert studentDAO != null : AddStudentWindow.ASSERTION_FAIL + "onDeleteButtonClick, studentDAO cannot be null";
            assert examDAO != null : AddStudentWindow.ASSERTION_FAIL + "onDeleteButtonClick, examDAO cannot be null";

            Exam exam = examDAO.getExamForStudent(student);

            int studentCount = studentDAO.getStudents().size();
            studentDAO.deleteStudent(student);
            assert studentDAO.getStudents().size() == studentCount-1 : AddStudentWindow.ASSERTION_FAIL + "" +
                    "                                                       onDeleteOperation, invalid delete student operation";

            examDAO.deleteExam(exam);
            assert examDAO.getExamForStudent(student) == null: AddStudentWindow.ASSERTION_FAIL +
                                                                   "onDeleteOperation, invalid delete exam operation";

            setStudentsComboBox();
        }
    }

    private Student getSelectedIndex(String action){
        assert action.equalsIgnoreCase("delete") ||
               action.equalsIgnoreCase("edit") : AddStudentWindow.ASSERTION_FAIL +
                                                    "getSelectedIndex, invalid action [delete / edit ] allowed, no " + action +" allowed";
        try{
            Student selectedStudent = (Student) studentsComboBox.getSelectedItem();
            assert selectedStudent != null : AddStudentWindow.ASSERTION_FAIL +
                                                "getSelectedIndex, invalid selected object, student expected, got null";
            if(0 == JOptionPane.showConfirmDialog(null,"Are you sure you want to " + action.toLowerCase() + " " + selectedStudent.toString(),action.toUpperCase(), JOptionPane.YES_NO_OPTION )){
                return selectedStudent;
            }
        }catch (ClassCastException e){
            MainWindow.showMessageWindow("No registered Students",action.toUpperCase());
        }
        return null;
    }

    private void onEditButtonClick(){
        Student student = getSelectedIndex("Edit");
        if(student == null){
            return;
        }
        assert student != null : AddStudentWindow.ASSERTION_FAIL + "onEditButton, student cannot be null";
        AddStudentWindow addStudentWindow = new AddStudentWindow(student);
        MainWindow.runWindow(addStudentWindow);
        setStudentsComboBox();
    }

    private void onAddButtonClick(){
        AddStudentWindow addStudentWindow = new AddStudentWindow();
        MainWindow.runWindow(addStudentWindow);
        setStudentsComboBox();
    }
}
