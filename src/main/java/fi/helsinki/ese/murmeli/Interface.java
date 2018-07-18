package fi.helsinki.ese.murmeli;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
* Interface
* <p>
* REQUIRES/PROVIDES type of an attribute
*
*/
public class Interface {

	@SerializedName("id")
	@Expose
	final private int id;

	/**
	*
	* (Required)
	*
	*/
	@SerializedName("type")
	@Expose
	private String type;
	
	private static int hid = 0;
	
	public Interface(String type, int id) {
		this.type = type;
		this.id = id;
	}
	
	public Interface(String type) {
		this.type = type;
		this.id = hid;
		hid++;
	}
	
	public int getID() {
		return this.id;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public String getType() {
		return type;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setType(String type) {
		this.type = type;
	}
	
}
