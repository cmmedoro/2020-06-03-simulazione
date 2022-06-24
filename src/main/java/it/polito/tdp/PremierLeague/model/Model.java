package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private Graph<Player, DefaultWeightedEdge> grafo;
	private List<Player> giocatori;
	private Map<Integer, Player> idMap;
	//strutture dati per la ricorsione
	private List<Player> dreamTeam;
	private int titolaritaDreamTeam;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
	}
	
	public void creaGrafo(double x) {
		//grafo semplice, pesato, ORIENTATO
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		//devo inserire i vertici
		this.idMap = new HashMap<>();
		this.giocatori = new ArrayList<>(this.dao.getVertices(x, idMap));
		Graphs.addAllVertices(this.grafo, this.giocatori);
		//aggiungi gli archi
		for(Adiacenza a : this.dao.getAdiacenza(idMap)) {
			if(a.getPeso() > 0) {
				Graphs.addEdgeWithVertices(this.grafo, a.getP1(), a.getP2(), a.getPeso());
			}else if( a.getPeso() < 0) {
				Graphs.addEdgeWithVertices(this.grafo, a.getP2(), a.getP1(), -1*a.getPeso());
			}
		}
	}
	
	public boolean isGraphCreated() {
		if(this.grafo == null) {
			return false;
		}
		return true;
	}
	public int nVertices() {
		return this.grafo.vertexSet().size();
	}
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public Player getTopPlayer(){
		//battuto il maggior numero di avversari in termini di minuti giocati
		Player top = null;
		List<Player> battuti = new ArrayList<>();
		int maxUscenti = 0;
		for(Player p : this.grafo.vertexSet()) {
			if(this.grafo.outDegreeOf(p) > maxUscenti) {
				maxUscenti = this.grafo.outDegreeOf(p);
				top = p;
			}
		}
		return top;
	}
	
	public List<Adiacenza> getOpponents(Player topPlayer){
		List<Player> opponents = new ArrayList<>();
		List<Adiacenza> opp = new ArrayList<>();
		for(DefaultWeightedEdge d : this.grafo.outgoingEdgesOf(topPlayer)) {
			opponents.add(Graphs.getOppositeVertex(this.grafo, d, topPlayer));
			opp.add(new Adiacenza(topPlayer, Graphs.getOppositeVertex(this.grafo, d, topPlayer), (int)this.grafo.getEdgeWeight(d)));
		}
		Collections.sort(opp);
		return opp;
	}
	
	public List<Player> getListOpponents(Player p){
		List<Player> opponents = new ArrayList<>();
		for(DefaultWeightedEdge d : this.grafo.outgoingEdgesOf(p)) {
			opponents.add(Graphs.getOppositeVertex(this.grafo, d, p));
		}
		return opponents;
	}
	
	public List<Player> createDreamTeam(int k){
		List<Player> parziale = new ArrayList<>();
		this.dreamTeam = new ArrayList<>();
		List<Player> validi = new ArrayList<>(this.grafo.vertexSet());
		creaDreamTeam(parziale, 1, validi, k);
		this.titolaritaDreamTeam = this.gradoTitolaritaTeam(dreamTeam);
		return this.dreamTeam;
	}

	private void creaDreamTeam(List<Player> parziale, int livello, List<Player> validi, int k) {
		//caso terminale:
		if(parziale.size() == k) {
			//se ho raggiunto il massimo numero di giocatori del dream team controllo se questa è la soluzione migliore
			if(this.gradoTitolaritaTeam(parziale) > this.gradoTitolaritaTeam(this.dreamTeam)) {
				this.dreamTeam = new ArrayList<>(parziale);
			}
			return;
		}
		//caso normale
		for(Player p : validi) {
			if(!parziale.contains(p)) {
				parziale.add(p);
				List<Player> esclusi = this.getListOpponents(p);
				List<Player> rimanenti = new ArrayList<>(validi);
				for(Player pp : esclusi) {
					rimanenti.remove(pp);
				}
				creaDreamTeam(parziale, livello+1, rimanenti, k);
				parziale.remove(p);
			}
		}
	}
	
	private int gradoTitolaritaSingolo(Player p) {
		int grado = 0;
		int sommaUscenti = 0;
		int sommaEntranti = 0;
		for(DefaultWeightedEdge d : this.grafo.outgoingEdgesOf(p)) {
			sommaUscenti += this.grafo.getEdgeWeight(d);
		}
		for(DefaultWeightedEdge d : this.grafo.incomingEdgesOf(p)) {
			sommaEntranti += this.grafo.getEdgeWeight(d);
		}
		grado = sommaUscenti - sommaEntranti;
		return grado;
	}
	
	private int gradoTitolaritaTeam(List<Player> team) {
		int grado = 0;
		for(Player p : team) {
			grado += this.gradoTitolaritaSingolo(p);
		}
		return grado;
	}
	
	public int bestTitolarita() {
		return this.titolaritaDreamTeam;
	}

}
