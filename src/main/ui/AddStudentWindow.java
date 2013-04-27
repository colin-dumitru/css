package main.ui;

import edu.css.model.Student;
import edu.css.operations.StudentManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddStudentWindow extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfName;
//    private JTextField tfPrenume;
    private JTextField tfBac;
//    private JTextField tfMedieLiceu;
    private JTextField tfMediaExamen;
    private JTextField admissionAverage;
    private boolean validInput = true;
    private Student student;

    public AddStudentWindow() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        student = new Student();

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
        this.buttonOK.setText("Edit");
        this.tfName.setText(student.getName());
        this.tfBac.setText(student.getAverage().toString());

//        this.tfName.setText(student.getNume());
//        this.tfPrenume.setText(student.getPrenume());
//        this.tfBac.setText(student.getMedieBac().toString());
//        this.tfMedieLiceu.setText(student.getMedieLiceu().toString());
//        this.tfMediaExamen.setText(student.getMedieExamen().toString());
//        this.admissionAverage.setText(student.getMedieAdmitere().toString());
    }

    private void onOK() {
        validateInput();
        if(!validInput){
            return;
        }
        StudentManager.addStudent(student);
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

//        if(!isvalidName(tfPrenume.getText())){
//            validInput = false;
//            tfPrenume.setBackground(Color.RED);
//        }else {
//            tfPrenume.setBackground(Color.WHITE);
//            student.setPrenume(tfPrenume.getText());
//        }

        if(!isValidNumber(tfBac.getText())){
            validInput = false;
            tfBac.setBackground(Color.RED);
        }else {
            tfBac.setBackground(Color.WHITE);
            student.setAverage(Double.valueOf(tfBac.getText()));
        }

//        if(!isValidNumber(tfMediaExamen.getText())){
//            validInput = false;
//            tfMediaExamen.setBackground(Color.RED);
//        }else {
//            tfMediaExamen.setBackground(Color.WHITE);
//            student.setMedieExamen(Double.valueOf(tfMediaExamen.getText()));
//        }

//        if(!isValidNumber(tfMedieLiceu.getText())){
//            validInput = false;
//            tfMedieLiceu.setBackground(Color.RED);
//        }else {
//            tfMedieLiceu.setBackground(Color.WHITE);
//            student.setMedieLiceu(Double.valueOf(tfMedieLiceu.getText()));
//        }

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
}
