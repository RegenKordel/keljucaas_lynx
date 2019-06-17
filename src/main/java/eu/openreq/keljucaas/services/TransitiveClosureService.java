package eu.openreq.keljucaas.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.springframework.stereotype.Service;

import eu.openreq.keljucaas.domain.ElementRelationTuple;
import eu.openreq.keljucaas.domain.TransitiveClosure;
import fi.helsinki.ese.murmeli.Element;
import fi.helsinki.ese.murmeli.ElementModel;
import fi.helsinki.ese.murmeli.Parts;
import fi.helsinki.ese.murmeli.Relationship;

/**
 * Methods used to handle the graph and transitive closure.
 * 
 */
@Service
public class TransitiveClosureService {

	public Map<String, List<ElementRelationTuple>> generateGraph(Collection<ElementModel> models) {

		Map<String, List<ElementRelationTuple>> graph = new HashMap<>();
		Set<String> mocks = new HashSet<>();

		for (ElementModel model : models) {
			relationsToGraph(graph, model, mocks);
			partsToGraph(graph, model, mocks);
		}

		dealWithDuplicatingMocksInTheGraph(graph, mocks);

		return graph;
	}

	public void addAttributesToTransitiveClosure(Collection<ElementModel> models, ElementModel transitiveClosure) {

		for (Element element : transitiveClosure.getElements().values()) {
			if (element.getAttributes() != null) {
				for (Integer attribute : element.getAttributes().values()) {
					for (ElementModel model : models) {
						if (model.getAttributeValues().containsKey(attribute)) {
							transitiveClosure.addAttributeValue(model.getAttributeValues().get(attribute));
						}
					}
				}
			}
		}

		for (Relationship rel : transitiveClosure.getRelations()) {
			if (rel.getAttributes() != null) {
				for (Integer attribute : rel.getAttributes().values()) {
					for (ElementModel model : models) {
						if (model.getAttributeValues().containsKey(attribute)) {
							transitiveClosure.addAttributeValue(model.getAttributeValues().get(attribute));
						}
					}
				}
			}
		}
	}

	private void dealWithDuplicatingMocksInTheGraph(Map<String, List<ElementRelationTuple>> graph, Set<String> mocks) {

		for (String mock : mocks) {

			String baseName = mock.substring(0, mock.lastIndexOf('-'));

			if (graph.containsKey(baseName)) {
				
				Element real = findRequestedElement(graph, baseName);

				// set mock's relations to the real one
				for (ElementRelationTuple tuple : graph.get(mock)) {

					if (tuple.getRelationship() != null) {

						if (tuple.getRelationship().getFromID().equals(mock)) {

							Relationship rel = new Relationship(tuple.getRelationship().getNameType(), baseName,
									tuple.getRelationship().getToID());
							tuple.setRelationship(rel);
						} else {

							if (tuple.getRelationship().getNameType() == Relationship.NameType.DECOMPOSITION) {
								for (Parts parts : tuple.getElement().getParts()) {
									if (parts.getRole().equals("decomposition")) {
										
										parts.getParts().remove(mock);
										parts.getParts().add(baseName);
									}
								}
							}
							
							Relationship rel = new Relationship(tuple.getRelationship().getNameType(),
									tuple.getRelationship().getFromID(), baseName);
							tuple.setRelationship(rel);
						}
					}

					graph.get(baseName).add(tuple);

					ElementRelationTuple tupleToBeRemoved = null;

					List<ElementRelationTuple> list = graph.get(tuple.getElement().getNameID());

					for (ElementRelationTuple correspondingTuple : list) {

						if (correspondingTuple.getElement().getNameID().equals(mock)) {
							tupleToBeRemoved = correspondingTuple;
							break;
						}
					}

					list.remove(tupleToBeRemoved);

					list.add(new ElementRelationTuple(real, tuple.getRelationship()));
				}

				graph.remove(mock);
			}
		}
	}

	private void partsToGraph(Map<String, List<ElementRelationTuple>> graph, ElementModel model, Set<String> mocks) {

		for (Element element : model.getElements().values()) {
			if (element.getParts() == null || element.getParts().isEmpty()) {
				continue;
			}

			if (!graph.containsKey(element.getNameID())) {
				graph.put(element.getNameID(), new ArrayList<>());

				if (element.getNameID().endsWith("mock")) {
					mocks.add(element.getNameID());
				}
			}

			for (Parts parts : element.getParts()) {
				for (String part : parts.getParts()) {
					if (!graph.containsKey(part)) {
						graph.put(part, new ArrayList<>());

						if (part.endsWith("mock")) {
							mocks.add(part);
						}
					}

					/*//This section was to ensure that findRequestedElement() worked properly in dealWithDuplicatingMoscks when
					 * partDefinition was only passed on as parts. Now they are passed here as decomposition dependencies and
					 * parts both so obsolete for now unless changed in the future. 
					 * 
					 * Relationship rel = new Relationship(Relationship.NameType.DECOMPOSITION, element.getNameID(), part);
					
					ElementRelationTuple tuple = new ElementRelationTuple(model.getElements().get(part), rel);
					graph.get(element.getNameID()).add(tuple);

					tuple = new ElementRelationTuple(element, rel);
					graph.get(part).add(tuple);*/
				}
			}
		}
	}

	private void relationsToGraph(Map<String, List<ElementRelationTuple>> graph, ElementModel model,
			Set<String> mocks) {

		for (Relationship relation : model.getRelations()) {

			if (!graph.containsKey(relation.getFromID())) {
				graph.put(relation.getFromID(), new ArrayList<>());

				if (relation.getFromID().endsWith("mock")) {
					mocks.add(relation.getFromID());
				}
			}

			ElementRelationTuple tuple = new ElementRelationTuple(model.getElements().get(relation.getToID()),
					relation);
			graph.get(relation.getFromID()).add(tuple);

			if (!graph.containsKey(relation.getToID())) {
				graph.put(relation.getToID(), new ArrayList<>());

				if (relation.getToID().endsWith("mock")) {
					mocks.add(relation.getToID());
				}
			}

			tuple = new ElementRelationTuple(model.getElements().get(relation.getFromID()), relation);
			graph.get(relation.getToID()).add(tuple);
		}
	}

	public TransitiveClosure getTransitiveClosure(Map<String, List<ElementRelationTuple>> graph, String id, int depth) {

		ElementModel model = new ElementModel();
		Map<Integer, List<String>> layers = new HashMap<>();

		Element requested = null;

		if (graph.containsKey(id)) {
			requested = this.findRequestedElement(graph, id);
		}

		if (requested == null) {
			TransitiveClosure trans = new TransitiveClosure();
			trans.setModel(model);
			trans.setLayers(layers);
			return trans;
		}

		model.addElement(requested);

		Queue<ElementRelationTuple> queue = new LinkedList<>();

		for (ElementRelationTuple tuple : graph.get(id)) {
			queue.add(tuple);
		}

		this.addElementsAndRelationsToModel(model, queue, new LinkedList<ElementRelationTuple>(), depth, 1, graph,
				layers);

		List<String> layer = new ArrayList<String>();
		layer.add(id);
		layers.put(0, layer);
		
		TransitiveClosure closure = new TransitiveClosure();
		closure.setLayers(layers);
		closure.setModel(model);

		return closure;
	}

	private Element findRequestedElement(Map<String, List<ElementRelationTuple>> graph, String id) {

		Element element = graph.get(id).get(0).getElement();

		if (element != null) {
			for (ElementRelationTuple tuple : graph.get(element.getNameID())) {
				try {
					if (tuple.getElement().getNameID().equals(id)) {
						return tuple.getElement();
					}
				} catch (Exception e) {
					System.out.println(id);
					System.out.println(tuple);
					System.out.println(tuple.getElement());
					System.out.println(tuple.getElement().getNameID());
				}
			}
		}
		return null;
	}

	private void addElementsAndRelationsToModel(ElementModel model, Queue<ElementRelationTuple> currentLayer,
			Queue<ElementRelationTuple> nextLayer, int depth, int currentDepth,
			Map<String, List<ElementRelationTuple>> graph, Map<Integer, List<String>> layers) {

		if (currentDepth > depth) {
			return;
		} else if (currentLayer.isEmpty()) {
			return;
		}

		List<String> layer = new ArrayList<>();

		while (!currentLayer.isEmpty()) {
			ElementRelationTuple tuple = currentLayer.poll();
			if (!model.getElements().containsKey(tuple.getElement().getNameID())) {
				model.addElement(tuple.getElement());
				for (ElementRelationTuple nextTuple : graph.get(tuple.getElement().getNameID())) {
					nextLayer.add(nextTuple);
				}

				layer.add(tuple.getElement().getNameID());
			}
			Relationship relation = tuple.getRelationship();
			if (relation != null) {
				if (!model.getRelations().contains(relation)) {
					model.addRelation(relation);
				}
			}
		}

		if (!layer.isEmpty()) {
			layers.put(currentDepth, layer);
		}

		addElementsAndRelationsToModel(model, nextLayer, new LinkedList<ElementRelationTuple>(), depth,
				currentDepth + 1, graph, layers);
	}

}
