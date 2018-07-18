package fi.helsinki.ese.murmeli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
* ElementType
* <p>
* Defines an element
*
*/
public class Element {

	@SerializedName("id")
	@Expose
	final private int id;

	/**
	* The name of the element
	* (Required)
	*
	*/
	@SerializedName("nameID")
	@Expose
	private String nameID;
	
	/**
	* Attributes related to element
	*
	*/
	@SerializedName("attributes")
	@Expose
	private HashMap<String, Integer> attributes;
	
	@SerializedName("parts")
	@Expose
	private List<Parts> parts;
	
	/**
	* Required interfaces
	*
	*/
	@SerializedName("requiredInterfaces")
	@Expose
	private List<Integer> requiredInterfaces;
	
	/**
	* Provided interfaces
	*
	*/
	@SerializedName("providedInterfaces")
	@Expose
	private List<Integer> providedInterfaces;
	
	/**
	* ElementType related to this individual Element
	*
	*/
	
	@SerializedName("type")
	@Expose
	private String type;
	
	private static int hid = 0;
	
	public Element(String nameID, int id) {
		this.nameID = nameID;
		this.attributes = new HashMap();
		this.requiredInterfaces = new ArrayList();
		this.providedInterfaces = new ArrayList();
		this.parts = new ArrayList();
		this.id = id;
	}
	
	public Element(String nameID) {
		this.nameID = nameID;
		this.attributes = new HashMap();
		this.requiredInterfaces = new ArrayList();
		this.providedInterfaces = new ArrayList();
		this.parts = new ArrayList();
		this.id = hid;
		hid++;
	}
	
	public String getType() {
		return type;
	}

	public void setType(ElementType type) {
		this.type = type.getNameID();
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public int getID() {
		return this.id;
	}
	
	public void addAttribute(AttributeValue attribute) {
		
		this.attributes.put(attribute.getName(), attribute.getID());
	}
	
	public void addPart(Parts part) {

		this.parts.add(part);
	}
	
	public void addRequiredInterface(Interface required) {

		this.requiredInterfaces.add(required.getID());
	}
	
	public void addProvidedInterface(Interface provided) {
		
		this.providedInterfaces.add(provided.getID());
	}
	
	/**
	* The name of the elementType
	* (Required)
	*
	*/
	public String getNameID() {
		return nameID;
	}
	
	/**
	* The name of the elementType
	* (Required)
	*
	*/
	public void setNameID(String nameID) {
		this.nameID = nameID;
	}
	
	/**
	* Attributes related to element
	*
	*/
	public HashMap<String, Integer> getAttributes() {
		return attributes;
	}
	
	/**
	* Attributes related to element
	*
	*/
	public void setAttributes(HashMap<String, Integer> attributes) {
		this.attributes = attributes;
	}
	
	public void setAttributes(List<AttributeValue> attributes) {
		
		this.attributes.clear();
		
		for (AttributeValue atr : attributes) {
			this.attributes.put(atr.getName(), atr.getID());
		}
	}
	
	public List<Parts> getParts() {
		return parts;
	}
	
	public void setParts(List<Parts> parts) {
		this.parts = parts;
	}
	
	/**
	* Required interfaces
	*
	*/
	public List<Integer> getRequiredInterfaces() {
		return requiredInterfaces;
	}
	
	/**
	* Required interfaces
	*
	*/
	public void setRequiredInterfaces(List<Interface> requiredInterfaces) {
		
		for (Interface interf : requiredInterfaces) {
			this.requiredInterfaces.add(interf.getID());
		}
	}
	
	public void setRequiredInterfacesAsIds(List<Integer> requiredInterfaces) {
		this.requiredInterfaces = requiredInterfaces;
	}
	
	/**
	* Provided interfaces
	*
	*/
	public List<Integer> getProvidedInterfaces() {
		return providedInterfaces;
	}
	
	/**
	* Provided interfaces
	*
	*/
	public void setProvidedInterfaces(List<Interface> providedInterfaces) {
		
		for (Interface interf : providedInterfaces) {
			this.requiredInterfaces.add(interf.getID());
		}
	}
	
	public void setProvidedInterfacesAsIds(List<Integer> providedInterfaces) {
		this.providedInterfaces = providedInterfaces;
	}
}
