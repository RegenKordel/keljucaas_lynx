package fi.helsinki.ese.murmeli;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


	/**
	* Constraint
	* <p>
	* Constraint regarding an Element
	*
	*/
	public class Constraint {

	@SerializedName("id")
	@Expose
	final private int id;

	/**
	* The name of the constraint
	*
	*/
	@SerializedName("name")
	@Expose
	private String name;
	/**
	* The constraint’s expression as a string
	* (Required)
	*
	*/
	@SerializedName("expression")
	@Expose
	private String expression;
	
	private static int hid = 0;
	
	public Constraint(String expression, String name, int id) {
		this.expression = expression;
		this.name = name;
		this.id = id;
	}
	
	public Constraint(String expression, String name) {
		this.expression = expression;
		this.name = name;
		this.id = hid;
		hid++;
	}
	
	public Constraint(String expression) {
		this(expression, "");
	}
	
	public int getID() {
		return this.id;
	}
	
	/**
	* The name of the constraint
	*
	*/
	public String getName() {
		return name;
	}
	
	/**
	* The name of the constraint
	*
	*/
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	* The constraint’s expression as a string
	* (Required)
	*
	*/
	public String getExpression() {
		return expression;
	}
	
	/**
	* The constraint’s expression as a string
	* (Required)
	*
	*/
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
}
