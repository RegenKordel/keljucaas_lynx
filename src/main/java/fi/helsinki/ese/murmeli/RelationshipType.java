package fi.helsinki.ese.murmeli;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
* RelationshipType
* <p>
* Relation between two elements
*
*/
public class RelationshipType {

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
	
	private static int hid = 0;
	
	public RelationshipType(NameType nameType, String from, String to, int id) {
		this.nameType = nameType;
		this.fromID = from;
		this.toID = to;
		this.id = id;
	}
	
	public RelationshipType(NameType nameType, String from, String to) {
		this.nameType = nameType;
		this.fromID = from;
		this.toID = to;
		this.id = hid;
		hid++;
	}
	
	public RelationshipType(NameType nameType, Element from, Element to, int id) {
		
		this(nameType, from.getNameID(), to.getNameID(), id);
	}
	
	public RelationshipType(NameType nameType, Element from, Element to) {
		
		this(nameType, from.getNameID(), to.getNameID());
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
