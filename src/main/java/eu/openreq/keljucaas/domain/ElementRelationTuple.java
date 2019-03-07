package eu.openreq.keljucaas.domain;

// This class is for preserving the information about relations for 
// each element in the graph used for getting the transitive closure.

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((relationship == null) ? 0 : relationship.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		ElementRelationTuple other = (ElementRelationTuple) obj;
		
		/*
		 * for debugging the strange nullpointer:
		 * 
		 * if (other == null) {
			System.out.println("kakku on vale! " + this.element.getNameID());
		} else if (this.relationship == null) {
			System.out.println("piiras on vale! " + this.element.getNameID());
		} else if (other.getRelationship() == null) {
			System.out.println("torttu on vale! " + other.getElement().getNameID());
		}*/
		
		if (this.element.getNameID().equals(other.getElement().getNameID())) {
			if (this.relationship == null && other.relationship == null) {
				return true;
			} else if (this.relationship.equals(other.getRelationship())) {
				return true;
			}
		}
		
		return false;
	}
	
	
}
