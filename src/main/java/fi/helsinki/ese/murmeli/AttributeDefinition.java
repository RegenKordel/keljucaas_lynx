package fi.helsinki.ese.murmeli;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AttributeDefinition {
	
	enum Optionality {
		FIXED,
		REQUIRED,
		OPTIONAL
	}
	
	@SerializedName("defaultValue")
	@Expose
	private Integer defaultValue;
	
	@SerializedName("valueType")
	@Expose
	private String valueType;
	
	@SerializedName("optionality")
	@Expose
	private Optionality optionality = Optionality.REQUIRED;
	
	public AttributeDefinition(Integer defaultValue, String type, Optionality opt) {
		
		this.defaultValue = defaultValue;
		this.valueType = type;
		this.optionality = opt;
	}
	
	public AttributeDefinition(AttributeValue defaultValue, AttributeValueType type, Optionality opt) {
		
		this(defaultValue.getID(), type.getName(), opt);
	}
	
	public AttributeDefinition(AttributeValue defaultValue, AttributeValueType type) {
		
		this(defaultValue.getID(), type.getName(), Optionality.REQUIRED);
	}
	
	public AttributeDefinition(AttributeValueType type) {
		
		this(null, type);
	}

	public Integer getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(AttributeValue defaultValue) {
		this.defaultValue = defaultValue.getID();
	}
	
	public void setDefaultValue(int defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(AttributeValueType valueType) {
		this.valueType = valueType.getName();
	}
	
	public void setValueType(String typeName) {
		this.valueType = typeName;
	}

	public Optionality getOptionality() {
		return optionality;
	}

	public void setOptionality(Optionality optionality) {
		this.optionality = optionality;
	}
	
	
}
