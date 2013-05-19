package main.ui;

import edu.css.model.Exam;
import edu.css.model.Student;
import edu.css.operations.AdmissionHelper;
import edu.css.operations.DAOLoader;
import edu.css.operations.ExamDAO;
import edu.css.operations.StudentDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddStudentWindow extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfName;
    private JTextField tfBac;
    private JTextField tfMediaExamen;
    private JTextField admissionAverage;
    private boolean validInput = true;

    private StudentDAO studentDAO = DAOLoader.getStudentDAO();
    private ExamDAO examDAO = DAOLoader.getExamDAO();

    private Student student;
    private Exam exam;

    public AddStudentWindow() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        student = new Student();
        exam = new Exam();

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

    public AddStudentWindow(Student student){
        this();
        if(student == null){
            MainWindow.showMessageWindow("Invalid Student", "Edit");
            onCancel();
        }
        this.student = student;
        this.exam = examDAO.getExamForStudent(student);
        this.buttonOK.setText("Edit");
        this.tfName.setText(student.getName());
        this.tfBac.setText(student.getAverage().toString());
        this.tfMediaExamen.setText(exam.getMark().toString());
    }

    private void onOK() {
        validateInput();
        if(!validInput){
            return;
        }
        studentDAO.addStudent(student);
        exam.setStudentId(student.getId());
        examDAO.addExam(exam);
        dispose();
    }

    private void validateInput(){
        validInput = true;
        if(!isvalidName(tfName.getText())){
            validInput = false;
            tfName.setBackground(Color.RED);
        }else {
            tfName.setBackground(Color.WHITE);
            student.setName(tfName.getText());
        }

        if(!isValidNumber(tfBac.getText())){
            validInput = false;
            tfBac.setBackground(Color.RED);
        }else {
            tfBac.setBackground(Color.WHITE);
            student.setAverage(Double.valueOf(tfBac.getText()));
        }

        if(tfMediaExamen.getText().trim().length() == 0)
        {
            exam.setMark(0.0);
        }
        else if(!isValidNumber(tfMediaExamen.getText())){
            validInput = false;
            tfMediaExamen.setBackground(Color.RED);
        }else {
            tfMediaExamen.setBackground(Color.WHITE);
            exam.setMark(Double.valueOf(tfMediaExamen.getText()));
        }

//        if(validInput)
//            student.setPassed(AdmissionHelper.passed(student, exam));
    }

    private boolean isvalidName(String name){
        if(name.length() > 2){
            return true;
        }else{
            return false;
        }
    }

    private boolean isValidNumber(String number){
        try {
            double value = Double.parseDouble(number);
            if(value > 10){
                return false;
            }
        }catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    private void onCancel() {
        dispose();
    }

    public Student getStudent() {
        return student;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }
}
