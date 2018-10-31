package fi.helsinki.ese.murmeli;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
* PartDefinition
* <p>
* Possible children types of the element
*
*/
public class Parts {

	@SerializedName("id")
	@Expose
	final private int id;

	/**
	*
	* (Required)
	*
	*/
	@SerializedName("role")
	@Expose
	private String role;
	
	@SerializedName("parts")
	@Expose
	private List<String> parts = new ArrayList<String>();
	
	@SerializedName("definition")
	@Expose
	private int definition = -1;
	
	private static int hid = 0;
	
	public Parts(String role, int id) {
		this.role = role;
		this.id = id;
	}
	
	public Parts(String role) {
		this.role = role;
		this.id = hid;
		hid++;
	}
	
	public Parts(String role, int id, int definition) {
		this(role, id);
		this.setDefinitionAsId(definition);
	}
	
	public Parts(String role, PartDefinition definition) {
		this(role);
		this.setDefinition(definition);
	}
	
	public Parts(String role, int id, PartDefinition definition) {
		this(role, id);
		this.setDefinition(definition);
	}
	
	public int getDefinition() {
		return definition;
	}

	public void setDefinitionAsId(int definition) {
		this.definition = definition;
	}
	
	public void setDefinition(PartDefinition definition) {
		this.definition = definition.getID();
	}

	public void addPart(Element part) {
		
		this.parts.add(part.getNameID());
	}
	
	public void addPartAsId(String id) {
		
		this.parts.add(id);
	}
	
	public int getID() {
		return this.id;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public String getRole() {
		return role;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setRole(String role) {
		this.role = role;
	}
	
	public List<String> getParts() {
		return parts;
	}
	
	public void setParts(List<Element> parts) {
		
		for (Element element : parts) {
			this.parts.add(element.getNameID());
		}
	}
	
	public void setPartsAsIds(List<String> parts) {
		this.parts = parts;
	}
}