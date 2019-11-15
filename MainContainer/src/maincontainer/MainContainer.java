/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maincontainer;

import jade.core.AgentContainer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.util.ExtendedProperties;
import java.util.Properties;

/**
 *
 * @author HP
 */
public class MainContainer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         try {
        //On lance le runtime
        jade.core.Runtime runtime = jade.core.Runtime.instance();
        //On gère l'affiche graphique du main container
        Properties properties = new ExtendedProperties();
        properties.setProperty(Profile.GUI, "true");
        //On crée l'agent main container SMA
        Profile profile = new ProfileImpl((jade.util.leap.Properties) properties);
        AgentContainer mainContainer = (AgentContainer) runtime.createMainContainer(profile);
        
         } catch (Exception e) {
             e.printStackTrace();
        }
    }
    
}
