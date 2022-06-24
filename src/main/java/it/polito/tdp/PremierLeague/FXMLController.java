/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Model;
import it.polito.tdp.PremierLeague.model.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnTopPlayer"
    private Button btnTopPlayer; // Value injected by FXMLLoader

    @FXML // fx:id="btnDreamTeam"
    private Button btnDreamTeam; // Value injected by FXMLLoader

    @FXML // fx:id="txtK"
    private TextField txtK; // Value injected by FXMLLoader

    @FXML // fx:id="txtGoals"
    private TextField txtGoals; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	this.txtResult.clear();
    	//prendi l'input
    	Double x;
    	try {
    		x = Double.parseDouble(this.txtGoals.getText());
    		if( x < 0 || x >= 1) {
    			this.txtResult.setText("Devi inserire un numero decimale compreso fra 0 e 1 (escluso)");
    			return;
    		}
    	}catch(NumberFormatException e) {
    		this.txtResult.setText("Devi inserire un valore numerico decimale");
    		return;
    	}
    	//se sono qui posso proseguire con la creazione del grafo
    	this.model.creaGrafo(x);
    	this.txtResult.setText("Grafo creato!\n");
    	this.txtResult.appendText("#VERTICI: "+this.model.nVertices()+"\n");
    	this.txtResult.appendText("#ARCHI: "+this.model.nArchi()+"\n");

    }

    @FXML
    void doDreamTeam(ActionEvent event) {
    	this.txtResult.clear();
    	//controlla che il grafo sia creato
    	if(!this.model.isGraphCreated()) {
    		this.txtResult.setText("Devi prima creare il grafico");
    		return;
    	}
    	int k;
    	try {
    		k = Integer.parseInt(this.txtK.getText());
    		if( k >= 5) {
    			this.txtResult.setText("Devi inserire un numero (positivo) maggiore di 5");
    			return;
    		}
    	}catch(NumberFormatException e) {
    		this.txtResult.setText("Devi inserire un valore numerico intero");
    		return;
    	}
    	//se sono qui posso avviare la ricorsione
    	List<Player> dreamTeam = new ArrayList<>(this.model.createDreamTeam(k));
    	for(Player p : dreamTeam) {
    		this.txtResult.appendText(p +"\n");
    	}
    	this.txtResult.appendText("Grado di titolarità del dream team: "+this.model.bestTitolarita()+"\n"); 
    }

    @FXML
    void doTopPlayer(ActionEvent event) {
    	this.txtResult.clear();
    	//controlla che il grafo sia creato
    	if(!this.model.isGraphCreated()) {
    		this.txtResult.setText("Devi prima creare il grafico");
    		return;
    	}
    	//se sono qui posso proseguire
    	Player top = this.model.getTopPlayer();
    	this.txtResult.setText("TOP PLAYER: "+top+"\n\n");
    	this.txtResult.appendText("AVVERSARI BATTUTI:\n");
    	List<Adiacenza> opponents = new ArrayList<>(this.model.getOpponents(top));
    	for(Adiacenza a : opponents) {
    		this.txtResult.appendText(a.getP2()+" | "+a.getPeso()+"\n");
    	}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnTopPlayer != null : "fx:id=\"btnTopPlayer\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnDreamTeam != null : "fx:id=\"btnDreamTeam\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtK != null : "fx:id=\"txtK\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtGoals != null : "fx:id=\"txtGoals\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    }
}
