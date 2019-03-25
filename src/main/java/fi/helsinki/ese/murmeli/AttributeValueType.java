package fi.helsinki.ese.murmeli;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
* AttributeValueType
* <p>
* Reference to a certain value or set of values
 * @param <T>
*
*/
public class AttributeValueType {

	public enum BaseType {

        BOOLEAN,
        INT,
        STRING,
	}

	public enum Cardinality {

        SINGLE,
        SET, //what is the set of relevant multi-cardinality types?
        ARRAY
	}
	
	public enum Bound {
		ENUM,
		UNBOUND,
		RANGE
	}

	@SerializedName("id")
	@Expose
	final private int id;
	
	@SerializedName("nameID")
	@Expose
	private String nameID;
	
	@SerializedName("values")
	@Expose
	private List<Integer> values = new ArrayList<Integer>();
	
	@SerializedName("baseType")
	@Expose
	private final BaseType baseType;
	
	@SerializedName("cardinality")
	@Expose
	private final Cardinality cardinality;
	
	@SerializedName("bound")
	@Expose
	private Bound bound;
	
	@SerializedName("range")
	@Expose
	private int[] range;
	
	private static int hid = 0;

	//abstract List<Value> getPossibleValues();

	public AttributeValueType(BaseType baseType, Cardinality cardinality, String nameID, int id) {

		this.nameID = nameID;
        this.baseType = baseType;
        this.cardinality = cardinality;
        this.id = id;
	}
	
	public AttributeValueType(BaseType baseType, Cardinality cardinality, String nameID) {

		this.nameID = nameID;
        this.baseType = baseType;
        this.cardinality = cardinality;
        this.id = hid;
		hid++;
	}

	public AttributeValueType(BaseType baseType, Cardinality cardinality) {
		
		this(baseType, cardinality, "");
	}
	
	public AttributeValueType(Cardinality cardinality, String nameID, int id, int min, int max) {
		
		this.cardinality = cardinality;
		this.baseType = BaseType.INT;
		this.nameID = nameID;
		this.id = id;
		this.range = new int[2];
		range[0] = min;
		range[1] = max;
		this.bound = Bound.RANGE;
	}
	
	public AttributeValueType(Cardinality cardinality, String nameID, int min, int max) {
		
		this(cardinality, nameID, hid, min, max);
		hid++;
	}
	
	public AttributeValueType(Cardinality cardinality, int min, int max) {
		
		this(cardinality, "", min, max);
	}
	
	public AttributeValueType(int min, int max) {
		
		this(Cardinality.SINGLE, min, max);
	}
	
	public int[] getRange() {
		return range;
	}

	public void setRange(int[] range) {
		this.range = range;
	}

	public Bound getBound() {
		return bound;
	}

	public void setBound(Bound bound) {
		this.bound = bound;
	}

	public int getID() {
		return this.id;
	}
	
	public String getName() {
		
		return this.nameID;
	}

	public BaseType getBaseType() {

        return baseType;
	}

	public Cardinality getCardinality() {

        return cardinality;
	}
	
	public List<Integer> getValues() {
		
		return this.values;
	}
	
	public void setValuesAsIDs(List<Integer> values) {
		
		this.values = values;
	}
	
	public void setValues(List<AttributeValue<?>> values) {
		
		this.values.clear();
		
		for (AttributeValue<?> value : values) {
			
			this.values.add(value.getID());
		}
	}
	
	public void addValue(AttributeValue<?> value) {
		
		this.values.add(value.getID());
	}
	
	public boolean equals(AttributeValueType other) {
		
		return (this.baseType.equals(other.getBaseType()) && this.cardinality.equals(other.getCardinality()) && this.nameID.equals(other.getName()));
	}
}