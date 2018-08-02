package eu.openreq.keljucaas.domain;

import fi.helsinki.ese.murmeli.Element;
import fi.helsinki.ese.murmeli.Relationship;

public class ElementRelationTuple {
	private Element element;
	private Relationship relationship;
	
	public ElementRelationTuple (Element element, Relationship relation) {
		this.element = element;
		this.relationship = relation;
	}
	
	public ElementRelationTuple (Element element) {
		this.element = element;
		this.relationship = null;
	}
	
	public Element getElement() {
		return element;
	}
	public void setElement(Element element) {
		this.element = element;
	}
	public Relationship getRelationship() {
		return relationship;
	}
	public void setRelationship(Relationship relationship) {
		this.relationship = relationship;
	}
}
