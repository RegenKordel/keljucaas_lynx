package fi.helsinki.ese.murmeli;

import java.util.HashMap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
* RelationshipType
* <p>
* Relation between two elements
*
*/
public class Relationship {

	public enum NameType {
		
        REFINES,
        REQUIRES,
        INCOMPATIBLE,
        SIMILAR,
        DUPLICATES,
        REPLACES,
        DAMAGES,
        CONTRIBUTES
	}
	
	@SerializedName("id")
	@Expose
	final private int id;

	/**
	*
	* (Required)
	*
	*/
	@SerializedName("nameType")
	@Expose
	private NameType nameType;
	
	/**
	*
	* (Required)
	*
	*/
	@SerializedName("fromID")
	@Expose
	private String fromID;
	
	/**
	*
	* (Required)
	*
	*/
	@SerializedName("toID")
	@Expose
	private String toID;
	
	@SerializedName("power")
	@Expose
	private int power = 0;
	
	@SerializedName("attributes")
	@Expose
	private HashMap<String, Integer> attributes;
	
	private static int hid = 0;
	
	public Relationship(NameType nameType, String from, String to, int id) {
		this.nameType = nameType;
		this.fromID = from;
		this.toID = to;
		this.id = id;
		this.attributes = new HashMap<String, Integer>();
	}
	
	public Relationship(NameType nameType, String from, String to) {
		this.nameType = nameType;
		this.fromID = from;
		this.toID = to;
		this.id = hid;
		this.attributes = new HashMap<String, Integer>();
		hid++;
	}
	
	public Relationship(NameType nameType, Element from, Element to, int id) {
		
		this(nameType, from.getNameID(), to.getNameID(), id);
	}
	
	public Relationship(NameType nameType, Element from, Element to) {
		
		this(nameType, from.getNameID(), to.getNameID());
	}
	
	public HashMap<String, Integer> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, Integer> attributes) {
		this.attributes = attributes;
	}
	
	public int getAttribute(String name) {
		return this.attributes.get(name);
	}
	
	public int getStatus() {
		return this.attributes.get("status");
	}
	
	public void addAttribute(AttributeValue atr) {
		this.attributes.put(atr.getName(), atr.getID());
	}
	
	public void addAttribute(String name, int id) {
		this.attributes.put(name, id);
	}

	public int getID() {
		return this.id;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public NameType getNameType() {
		return nameType;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setNameType(NameType nameType) {
		this.nameType = nameType;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public String getFromID() {
		return fromID;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setFromID(String from) {
		this.fromID = from;
	}
	
	public void setFromID(Element from) {
		this.fromID = from.getNameID();
	}
	
	/**
	*
	* (Required)
	*
	*/
	public String getToID() {
		return toID;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setToID(String to) {
		this.toID = to;
	}
	
	public void setTo(String to) {
		this.toID = to;
	}
}
