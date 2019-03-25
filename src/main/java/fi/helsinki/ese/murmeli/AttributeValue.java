package fi.helsinki.ese.murmeli;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
* AttributeValue
* <p>
* A value or a set of values
 * @param <T>
*
*/
public class AttributeValue<T> {
	
	@SerializedName("id")
	@Expose
	final private int id;
	
	/**
	*
	* (Required)
	*
	*/
	@SerializedName("name")
	@Expose
	private String name;
	
	/**
	* Whether or not value should be mutable
	* (Required)
	*
	*/
	@SerializedName("mutable")
	@Expose
	private boolean mutable;
	/**
	* Actual value or values
	* (Required)
	*
	*/
	@SerializedName("value")
	@Expose
	private T value = null;
	
	@SerializedName("source")
	@Expose	
	Source source = Source.DEFAULT;
	
	public enum Source {
		FIXED,
		USER,
		DEFAULT,
		INFERRED	
	}
	
	private String type;
	
	private static int hid = 0;
		
	public AttributeValue(String nameID, boolean mutable, T value, int id) {
		
		this.name = nameID;
		this.mutable = mutable;
		this.value = value;
		this.id = id;
	}
	
	public AttributeValue(String nameID, boolean mutable, T value) {
		
		this.name = nameID;
		this.mutable = mutable;
		this.value = value;
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
	public String getName() {
		return name;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSource(Source source) {
		this.source = source;
	}
	
	public Source getSource() {
		return this.source;
	}
	/*
	/**
	* Whether or not value is a set of values or a single value
	* (Required)
	*
	*
	public boolean isSingleValue() {
	return singleValue;
	}
	
	/**
	* Whether or not value is a set of values or a single value
	* (Required)
	*
	*
	public void setSingleValue(boolean singleValue) {
		this.singleValue = singleValue;
	}
	*/
	/**
	* Whether or not value should be mutable
	* (Required)
	*
	*/
	public boolean isMutable() {
		return mutable;
	}
	
	/**
	* Whether or not value should be mutable
	* (Required)
	*
	*/
	public void setMutable(boolean mutable) {
		this.mutable = mutable;
	}
	
	/**
	* Actual value or values
	* (Required)
	*
	*/
	public T getValue() {
		return value;
	}
	
	/**
	* Actual value or values
	* (Required)
	*
	*/
	public void setValue(T value) {
		this.value = value;
	}
	
	public String getType() {
		return type;
	}

	public void setType(AttributeValueType type) {
		this.type = type.getName();
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public boolean isEmpty() {
		return this.value == null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass() != obj.getClass()) {
			return false;
		}
		
		AttributeValue<?> other = (AttributeValue<?>) obj;
		
		if (this.value == null) {
			if (other.getValue() != null) {
				return false;
			}
		} else if (this.value.getClass() != other.getValue().getClass()) {
			return false;
		} else if (!this.value.equals(other.getValue())) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AttributeValue [value=");
		builder.append(this.value);
		builder.append("]");
		return builder.toString();
	}
}
