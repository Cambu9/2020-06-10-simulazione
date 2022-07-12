package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	
	private ImdbDAO dao = new ImdbDAO();
	public Graph<Actor, DefaultWeightedEdge> grafo;
	public List<Actor> connessi;	
	private Simulator sim;
	
	public String creaGrafo(String genre) {
		//creo il grafo
		grafo = new SimpleWeightedGraph<Actor, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//aggiungi i vertici
		Graphs.addAllVertices(this.grafo, dao.getVertici(genre));
		
		//aggiungo gli archi
		List<Adiacenza> archi = new ArrayList<Adiacenza>();
		archi = dao.getArchi(genre);
		for(Adiacenza a: archi) {
			Graphs.addEdgeWithVertices(this.grafo, a.getAttore1(), a.getAttore2(), a.getPeso());
		}
		return "Grafo Creato \n#Vertici: " + this.grafo.vertexSet().size() + "\n#ARchi: " + this.grafo.edgeSet().size() + "\n";
	}
	
	public List<String> listAllGenre(){
		List<String> genre = new ArrayList<>();
		genre = dao.listAllGenre();
		return genre;
	}
	
	public void collegati(Actor a){
		List<Actor> vicini = new ArrayList<>();
		connessi = new ArrayList<Actor>();
		connessi.add(a);
		vicini = Graphs.neighborListOf(this.grafo, a);
		for(Actor vicino: vicini) {
			if(!connessi.contains(vicino)) {
				connessi.add(vicino);
				collegati2(vicino);
			}
			
		}
		connessi.remove(a);
		Collections.sort(connessi, new Comparator<Actor>() {

			@Override
			public int compare(Actor o1, Actor o2) {
				// TODO Auto-generated method stub
				return o1.getLastName().compareTo(o2.getLastName());
			}
			
		});
		return;
	}

	private void collegati2(Actor vicino) {
		List<Actor> vicini = new ArrayList<>();
		vicini = Graphs.neighborListOf(this.grafo, vicino);
		for(Actor vicino2: vicini) {
			if(!connessi.contains(vicino2)) {
				connessi.add(vicino2);
				collegati2(vicino2);
			}
			
		}
		
	}

	public void setConnessi(List<Actor> connessi) {
		this.connessi = connessi;
	}
	
	public void simulate(int n) {
		sim = new Simulator(n, grafo);
		sim.init();
		sim.run();
	}
	
	public Collection<Actor> getInterviewedActors(){
		if(sim == null){
			return null;
		}
		return sim.getInterviewedActors();
	}
	
	public Integer getPauses(){
		if(sim == null){
			return null;
		}
		return sim.getPauses();
	}
	
}
