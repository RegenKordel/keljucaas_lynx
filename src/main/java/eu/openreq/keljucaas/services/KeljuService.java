package eu.openreq.keljucaas.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import eu.openreq.keljucaas.domain.ElementRelationTuple;
import fi.helsinki.ese.murmeli.Element;
import fi.helsinki.ese.murmeli.ElementModel;
import fi.helsinki.ese.murmeli.Parts;
import fi.helsinki.ese.murmeli.Relationship;

public class KeljuService {
	
	public Map<String, List<ElementRelationTuple>> generateGraph(ElementModel model) {
		Map<String, List<ElementRelationTuple>> graph = new HashMap();
		
		for (Relationship relation : model.getRelations()) {
			if (!graph.containsKey(relation.getFromID())) {
				graph.put(relation.getFromID(), new ArrayList());
				System.out.println("relation.getFromID(): " + relation.getFromID());
 			}
			ElementRelationTuple tuple = new ElementRelationTuple(model.getElements().get(relation.getToID()), relation);
			graph.get(relation.getFromID()).add(tuple);
			System.out.println("relation.getFromID(): " + graph.get(relation.getFromID()));
			if (!graph.containsKey(relation.getToID())) {
				graph.put(relation.getToID(), new ArrayList());
 			}
			tuple = new ElementRelationTuple(model.getElements().get(relation.getFromID()), relation);
			graph.get(relation.getToID()).add(tuple);
		}
		
		for (Element element : model.getElements().values()) {
			if (element.getParts() == null || element.getParts().isEmpty()) {
				continue;
			}
			if (!graph.containsKey(element.getNameID())) {
				graph.put(element.getNameID(), new ArrayList());
 			}
			for (Parts parts : element.getParts()) {
				for (String part : parts.getParts()) {
					if (!graph.containsKey(part)) {
						graph.put(part, new ArrayList());
		 			}
					ElementRelationTuple tuple = new ElementRelationTuple(model.getElements().get(part));
					graph.get(element.getNameID()).add(tuple);
					
					tuple = new ElementRelationTuple(element);
					graph.get(part).add(tuple);
				}
			}
		}
		return graph;
	}
	
	public ElementModel getTransitiveClosure(Map<String, List<ElementRelationTuple>> graph, String id, int depth) {
		ElementModel model = new ElementModel();
		Element requested = this.findRequestedElement(graph, id);
		System.out.println("Requst: " + requested.getNameID());
		if (requested == null) {
			return model;
		}
		model.addElement(requested);
		
		Queue<ElementRelationTuple> queue = new LinkedList();
		
		for (ElementRelationTuple tuple : graph.get(id)) {
			queue.add(tuple);
		}
		this.addElementsAndRelationsToModel(model, queue, new LinkedList<ElementRelationTuple>(), depth, 1, graph);
		return model;
	}
	
	private Element findRequestedElement(Map<String, List<ElementRelationTuple>> graph, String id) {
		System.out.println(graph.get(id).get(0).getElement());
		Element element = graph.get(id).get(0).getElement();
		System.out.println(element.getNameID());
		if (element != null) {
			for (ElementRelationTuple tuple : graph.get(element.getNameID())) {
				if (tuple.getElement().getNameID().equals(id)) {
					return tuple.getElement();
				}
			}
		}
		return null;
	}
	
	private void addElementsAndRelationsToModel(ElementModel model, Queue<ElementRelationTuple> currentLayer, 
			Queue<ElementRelationTuple> nextLayer, int depth, int currentDepth, Map<String, List<ElementRelationTuple>> graph) {
		if (currentDepth > depth) {
			return;
		} else if (currentLayer.isEmpty()) {
			return;
		}
		while (!currentLayer.isEmpty()) {
			ElementRelationTuple tuple = currentLayer.poll();
			System.out.println("TUPPELI: " + tuple.getElement().getNameID());
			if (!model.getElements().containsKey(tuple.getElement().getNameID())) {
				model.addElement(tuple.getElement());
				for (ElementRelationTuple nextTuple : graph.get(tuple.getElement().getNameID())) {
					nextLayer.add(nextTuple);
				}
			}
			Relationship relation = tuple.getRelationship();
			if (relation != null) {
				if (!model.getRelations().contains(relation)) {
					model.addRelation(relation);
				}
			}
		}
		addElementsAndRelationsToModel(model, nextLayer, new LinkedList<ElementRelationTuple>(), depth, currentDepth+1, graph);
	}
 	
}
