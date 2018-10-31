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
public class PartDefinition {

	@SerializedName("id")
	@Expose
	final private int id;

	/**
	*
	* (Required)
	*
	*/
	@SerializedName("cardinality_min")
	@Expose
	private int cardinality_min = 0;
	/**
	*
	* (Required)
	*
	*/
	@SerializedName("cardinality_max")
	@Expose
	private int cardinality_max = 1;
	/**
	*
	* (Required)
	*
	*/
	@SerializedName("role")
	@Expose
	private String role;
	
	@SerializedName("type")
	@Expose
	private List<String> elementTypes = new ArrayList<String>();
	
	private static int hid = 0;
	
	public PartDefinition(int min, int max, String role, int id) {
		this.cardinality_max = max;
		this.cardinality_min = min;
		this.role = role;
		this.id = id;
	}
	
	public PartDefinition(int min, int max, String role) {
		this.cardinality_max = max;
		this.cardinality_min = min;
		this.role = role;
		this.id = hid;
		hid++;
	}
	
	
	public PartDefinition(int max, String role) {
		this(0, max, role);
	}
	
	public PartDefinition(String role) {
		this(1, role);
	}
	
	public void addElementType(ElementType type) {
		this.elementTypes.add(type.getNameID());
	}
	
	public void addElementType(String type) {
		this.elementTypes.add(type);
	}
	
	public int getID() {
		return this.id;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public int getCardinality_min() {
		return cardinality_min;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setCardinality_min(int cardinality_min) {
		this.cardinality_min = cardinality_min;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public int getCardinality_max() {
		return cardinality_max;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setCardinality_max(int cardinality_max) {
		this.cardinality_max = cardinality_max;
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
	
	public List<String> getType() {
		return this.elementTypes;
	}
	
	public void setTypesAsIds(List<String> types) {
		this.elementTypes = types;
	}
	
	public void setTypes(List<ElementType> types) {
		
		for (ElementType type : types) {
			this.elementTypes.add(type.getNameID());
		}
	}
}
