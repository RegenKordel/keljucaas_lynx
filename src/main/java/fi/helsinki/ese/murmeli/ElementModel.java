package fi.helsinki.ese.murmeli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ElementModel {

	@SerializedName("attributeValueTypes")
	@Expose
	private HashMap<String, AttributeValueType> attributeValueTypes;

	@SerializedName("elementTypes")
	@Expose
	private HashMap<String, ElementType> elementTypes;
	
	@SerializedName("elements")
	@Expose
	private HashMap<String, Element> elements;
	
	@SerializedName("subContainers")
	@Expose
	private List<Container> subContainers;
	
	@SerializedName("rootContainer")
	@Expose
	private Container rootContainer;
	
	@SerializedName("relations")
	@Expose
	private List<Relationship> relations;
	
	@SerializedName("constraints")
	@Expose
	private HashMap<Integer, Constraint> constraints;
	
	@SerializedName("attributeValues")
	@Expose
	private HashMap<Integer, AttributeValue<?>> attributeValues;
	
	@SerializedName("partDefinitions")
	@Expose
	private HashMap<Integer, PartDefinition> partDefinitions;

	
	/*@SerializedName("attributeValues")
	@Expose
	private HashMap<Integer, AttributeValue> parts;*/
	
	public ElementModel() {
		
		this.attributeValueTypes = new HashMap<String, AttributeValueType>();
		this.elementTypes = new HashMap<String, ElementType>();
		this.elements = new HashMap<String, Element>();
		this.subContainers = new ArrayList<Container>();
		this.rootContainer = null;
		this.relations = new ArrayList<Relationship>();
		this.constraints = new HashMap<Integer, Constraint>();
		this.attributeValues = new HashMap<Integer, AttributeValue<?>>();
	}
	
	public HashMap<String, AttributeValueType> getAttributeValueTypes() {
		return attributeValueTypes;
	}

	public void setAttributeValueTypes(HashMap<String, AttributeValueType> attributeValueTypes) {
		this.attributeValueTypes = attributeValueTypes;
	}
	
	public void addValueType(AttributeValueType type) {
		this.attributeValueTypes.put(type.getName(), type);
	}

	public HashMap<Integer, PartDefinition> getPartDefinitions() {
		return partDefinitions;
	}

	public void setPartDefinitions(HashMap<Integer, PartDefinition> partDefinitions) {
		this.partDefinitions = partDefinitions;
	}

	public HashMap<Integer, Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(HashMap<Integer, Constraint> constraints) {
		this.constraints = constraints;
	}
	
	public void addConstraint(Constraint cons) {
		this.constraints.put(cons.getID(), cons);
	}
	
	public HashMap<String, ElementType> getElementTypes() {
		return elementTypes;
	}

	public void setElementTypes(HashMap<String, ElementType> elementTypes) {
		this.elementTypes = elementTypes;
	}
	
	public void addElementType(ElementType type) {
		this.elementTypes.put(type.getNameID(), type);
	}

	public HashMap<String, Element> getElements() {
		return elements;
	}

	public void setElements(HashMap<String, Element> elements) {
		this.elements = elements;
	}
	
	public void addElement(Element element) {
		this.elements.put(element.getNameID(), element);
	}

	public Container getRootContainer() {
		return rootContainer;
	}

	public void setRootContainer(Container rootContainer) {
		this.rootContainer = rootContainer;
	}
	
	public List<Container> getsubContainers() {
		return subContainers;
	}

	public void setSubContainers(List<Container> subContainers) {
		this.subContainers = subContainers;
	}

	public List<Relationship> getRelations() {
		return relations;
	}

	public void setRelations(List<Relationship> relations) {
		this.relations = relations;
	}
	
	public void addRelation(Relationship relation) {
		this.relations.add(relation);
	}
	
	public HashMap<Integer, AttributeValue<?>> getAttributeValues() {
		return attributeValues;
	}

	public void setAttributeValues(HashMap<Integer, AttributeValue<?>> attributeValues) {
		this.attributeValues = attributeValues;
	}

	public void addAttributeValue(AttributeValue<?> value) {
		this.attributeValues.put(value.getID(), value);
	}
}