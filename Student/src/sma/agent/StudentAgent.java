/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sma.agent;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.MessageTemplate;
import java.util.logging.Level;
import java.util.logging.Logger;
import student.StudentPlateformController;

/**
 *
 * @author HP
 */
public class StudentAgent extends GuiAgent {

    private static final long serialVersionUID = 1L;

    //On lie l'agent à son interface graphique
    private StudentPlateformController gui;
    private AID[] listeEtudiant,listeProf;
    
    @Override
    protected void setup() { //Fonction d'initialisation de l'agent
        //Au démarage , on recupère la fenetre graphique
       gui = (StudentPlateformController) getArguments()[0];
       //On fait l'inverse
       gui.setMonAgent(this);
       System.out.println("Initialisation de l'agent "+ this.getAID().getName());
       //Publier des services
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
           @Override
           public void action() { //Publication de services
               DFAgentDescription dFAgentDescription = new DFAgentDescription();
               dFAgentDescription.setName(getAID());
               ServiceDescription serviceDescription = new ServiceDescription();
               serviceDescription.setType("EtudiantCours");
               serviceDescription.setName("EtudiantCours-online");
               dFAgentDescription.addServices(serviceDescription);
               try {
                   DFService.register(myAgent, dFAgentDescription);
              } catch (FIPAException ex) {
                   Logger.getLogger(StudentAgent.class.getName()).log(Level.SEVERE, null, ex);
              }
           }
       });

         parallelBehaviour.addSubBehaviour(new TickerBehaviour(this, 3000){
           private static final long serialVersionUID = 1L;
            protected void onTick(){
                DFAgentDescription description = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("EtudiantCours");
                description.addServices(sd);
                try {
                    DFAgentDescription[] agentDescription = DFService.search(myAgent, description);
                    listeEtudiant = new AID[agentDescription.length];
                    for (int i = 0; i < agentDescription.length; i++) {
                        //if(this.getAgent().getAID() != agentDescription[i].getName()){
                            listeEtudiant[i] = agentDescription[i].getName();
                        //}
                    }
                } catch (FIPAException ex) {
                    Logger.getLogger(StudentAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } 
        });
         
         //On gère la réception de message
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
           private static final long serialVersionUID = 1L;
           @Override
           public void action() {
               //Message template
               //Message template
               MessageTemplate messageTemplate = MessageTemplate.or(
                       MessageTemplate.MatchPerformative(jade.lang.acl.ACLMessage.REQUEST),
                       MessageTemplate.MatchPerformative(jade.lang.acl.ACLMessage.CFP)
               );

               jade.lang.acl.ACLMessage messagesRecus = receive(messageTemplate);
               if(messagesRecus != null){
                   System.out.println("réception d'un message : " + messagesRecus.getContent());
                   
                   GuiEvent guiEvent = new GuiEvent(this, 1);
                   String messageEtu = messagesRecus.getContent();
                   guiEvent.addParameter(messageEtu); 
                   gui.viewMessageTeacher(guiEvent);
               }
           }
       });
       
    }
    
    @Override
    public void takeDown() { //Fonction appelée avant la destruction de l'agent
        System.out.println("Destruction de l'agent");
        
    }

    @Override
    protected void beforeMove() {//Avant que l'agent se déplace d'un container à un autre
        System.out.println("Avant migration .... du container " + this.getContainerController().getName());
    }

    @Override
    protected void afterMove() { //Après que l'agent s'être déplacer
        System.out.println("Après migration .... vers le container " + this.getContainerController().getName());
    }
    
    @Override
    public void onGuiEvent(GuiEvent ge) {
        if(ge.getType() == 1){
            //On envoi le message
            jade.lang.acl.ACLMessage aCLMessage = new jade.lang.acl.ACLMessage(jade.lang.acl.ACLMessage.REQUEST);
            String messageEnv = ge.getParameter(0).toString();
            aCLMessage.setContent(this.getAID().getLocalName()+" -> "+ messageEnv);
            
            for(AID aid:listeEtudiant){   
                
                   aCLMessage.addReceiver(aid);      
            }
            
            DFAgentDescription descriptionProf = new DFAgentDescription();
            ServiceDescription sdProf = new ServiceDescription();
            sdProf.setType("EnseignantCours");
            descriptionProf.addServices(sdProf);
            
            try {
                DFAgentDescription[] agentDescriptionProf = DFService.search(this, descriptionProf);
                listeProf = new AID[agentDescriptionProf.length];
                for (int i = 0; i < agentDescriptionProf.length; i++) {
                    listeProf[i] = agentDescriptionProf[i].getName();
                }
            } catch (FIPAException ex) {
                Logger.getLogger(StudentAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
            for(AID aid:listeProf){
                  aCLMessage.addReceiver(aid);
            }
            
            //On envoi le message
            send(aCLMessage);
        }
    }
    
}
