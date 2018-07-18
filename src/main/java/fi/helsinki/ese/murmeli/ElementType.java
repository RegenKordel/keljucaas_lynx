package fi.helsinki.ese.murmeli;

import java.util.List;
import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ElementType {

	@SerializedName("nameID")
	@Expose
	private String nameID;
	
	@SerializedName("attributeDefinitions")
	@Expose
	private List<AttributeDefinition> attributeDefinitions;
	
	@SerializedName("potentialParts")
	@Expose
	private List<PartDefinition> potentialParts = new ArrayList<PartDefinition>();
	
	public ElementType(String nameID, List<AttributeDefinition> attributeDefinitions) {
		
		this.nameID = nameID;
		this.attributeDefinitions = attributeDefinitions;
	}
	
	public ElementType(String nameID) {
		
		this(nameID, new ArrayList<AttributeDefinition>());
	}
	
	public ElementType(List<AttributeDefinition> attributeDefinitions) {
		
		this("", attributeDefinitions);
	}
	
	public String getNameID() {
		return nameID;
	}

	public void setNameID(String nameID) {
		this.nameID = nameID;
	}

	public List<AttributeDefinition> getAttributeDefinitions() {
		return attributeDefinitions;
	}

	public void setAttributeDefinitions(List<AttributeDefinition> attributeDefinitions) {
		this.attributeDefinitions = attributeDefinitions;
	}

	public void addAttributeDefinition(AttributeDefinition def) {
		
		this.attributeDefinitions.add(def);
	}
	
	public void setPotentialParts(List<PartDefinition> parts) {
	
		for (PartDefinition part : parts) {
			this.potentialParts.add(part);
		}
	}
	
	public void setPotentialPartsAsIds(List<PartDefinition> parts) {
		this.potentialParts = parts;
	}
	
	public List<PartDefinition> getPotentialParts() {
		
		return this.potentialParts;
	}
	
	public void addPotentialPart(PartDefinition part) {
		
		this.potentialParts.add(part);
	}
	
//	public void addPotentialPart(int partID) {
//		
//		this.potentialParts.add(partID);
//	}
}
