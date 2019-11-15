/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.gui.GuiEvent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sma.agent.StudentAgent;

/**
 * FXML Controller class
 *
 * @author HP
 */
public class StudentPlateformController implements Initializable {

    @FXML
    private TextArea TxtBox_messageProf;
    @FXML
    private TextField TxtBox_Question;
    @FXML
    private Button Bt_questionner;
    @FXML
    private TextField TxtBox_numeroEtudiant;
    @FXML
    private Button Bt_commencez;
    @FXML
    private TextArea TxtBox_mesmessage;
    
    //On lui donne l'agent
    private StudentAgent monAgent;
    private  ContainerController agentContainer ;

    public StudentAgent getMonAgent() {
        return monAgent;
    }

    public void setMonAgent(StudentAgent monAgent) {
        this.monAgent = monAgent;
    }
    
    
    public void startConteneur(){
        try {
            jade.core.Runtime runtime = jade.core.Runtime.instance();
            Profile profile = new ProfileImpl(false);
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            agentContainer =  runtime.createAgentContainer(profile);
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        startConteneur();
        
    }    

    @FXML
    private void Action_Bt_questionner(ActionEvent event) {
        if(TxtBox_Question.getText().equals("")){
            new Alert(Alert.AlertType.INFORMATION, "Saisissez votre question svp !!!").showAndWait();
        }else{
            String question = TxtBox_Question.getText();
            TxtBox_mesmessage.setText(TxtBox_mesmessage.getText() + " \n " + TxtBox_Question.getText());
            TxtBox_Question.setText("");
            GuiEvent guiEvent = new GuiEvent(this, 1) ;//1:représente le nom de l'evènement , c'est ce qu'on recupérer dans l'agent pour effectuer le traitement
            guiEvent.addParameter(question);
            monAgent.onGuiEvent(guiEvent);
        }
        
        
    }

    @FXML
    private void Action_Bt_commencez(ActionEvent event) {
        if(TxtBox_numeroEtudiant.getText().equals("")){
            new Alert(Alert.AlertType.INFORMATION, "Entrez votre numéro étudiant svp !!!").showAndWait();
        }else{
            String EtudiantNom = TxtBox_numeroEtudiant.getText();
            //On déploie notre agent
            AgentController agentController;
            try {
                agentController = agentContainer.createNewAgent(EtudiantNom,"sma.agent.StudentAgent", new Object[]{this}); //Le tableau représente l'interface graphique
                agentController.start();
                TxtBox_numeroEtudiant.setEditable(false);
                Bt_commencez.setVisible(false);
                        
            } catch (StaleProxyException ex) {
                ex.printStackTrace();
            }
        }
    }
    //Notre système de reception de message venant de l'agent à l'endroit de l'interface graphique
    public void viewMessageTeacher(GuiEvent ge){
        String message = ge.getParameter(0).toString();
        TxtBox_messageProf.setText(TxtBox_messageProf.getText() + " \n " + message);
        
    }
    public String viewInfo(){
        return TxtBox_numeroEtudiant.getText();
    }
    
}
