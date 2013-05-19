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
        List<Student> studentList = studentDAO.getStudents();
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
        if(student != null){
            Exam exam = examDAO.getExamForStudent(student);
            studentDAO.deleteStudent(student);
            examDAO.deleteExam(exam);
            setStudentsComboBox();
        }
    }

    private Student getSelectedIndex(String action){
        try{
            Student selectedStudent = (Student) studentsComboBox.getSelectedItem();
            if(0 == JOptionPane.showConfirmDialog(null,"Are you sure you want to " + action.toLowerCase() + selectedStudent.toString(),action.toUpperCase(), JOptionPane.YES_NO_OPTION )){
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
