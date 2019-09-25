package eu.openreq.keljucaas.domain.release;

import fi.helsinki.ese.murmeli.Relationship;

public class IgnoredRelationship {
	private Element4Csp from;
	private Element4Csp to;
	private Relationship relationship;

	
	public IgnoredRelationship(Element4Csp from, Element4Csp to, Relationship relationship) {
		super();
		this.from = from;
		this.to = to;
		this.relationship = relationship;
	}


	public String getNameId() {
		return ("rel_" + this.from.getNameId()+"_" + Relationship4Csp.getRelationshipName(relationship) + "_" + this.to.getNameId());
	}


	public final Element4Csp getFrom() {
		return from;
	}


	public final Element4Csp getTo() {
		return to;
	}
	
	public final String getRelationShipName() {
		return Relationship4Csp.getRelationshipName(relationship);
	}

	
}
