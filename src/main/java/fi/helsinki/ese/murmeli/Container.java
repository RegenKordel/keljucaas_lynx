package fi.helsinki.ese.murmeli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
* Container
* <p>
* A container of elements
*
*/
public class Container {

	@SerializedName("id")
	@Expose
	final private int id;

	/**
	*
	* (Required)
	*
	*/
	@SerializedName("nameID")
	@Expose
	private String nameID;
	/**
	* Containers inside this container
	*
	*/
	@SerializedName("children")
	@Expose
	private List<Integer> children = new ArrayList();
	/**
	* Containers directly after this one
	*
	*/
	@SerializedName("next")
	@Expose
	private List<Integer> next = new ArrayList();
	/**
	* Elements in this container
	*
	*/
	@SerializedName("elements")
	@Expose
	private List<String> elements = new ArrayList();
	/**
	* Attributes describing this container
	*
	*/
	@SerializedName("attributes")
	@Expose
	private HashMap<String, Integer> attributes = new HashMap();
	
	private static int hid = 0;
	
	public Container(String nameID, int id) {
		this.nameID = nameID;
		this.id = id;
	}
	
	public Container(String nameID) {
		this.nameID = nameID;
		this.id = hid;
		hid++;
	}
	
	public int getID() {
		return this.id;
	}
	
	public void addChild(Container child) {
		
		this.children.add(child.getID());
	}
	
	public void addNext(Container next) {
		
		this.next.add(next.getID());
	}
	
	public void addElement(Element el) {

		this.elements.add(el.getNameID());
	}
	
	public void addElement(String element) {
		
		this.elements.add(element);
	}
	
	public void addAttribute(String key, Integer value) {

		this.attributes.put(key, value);
	}
	
	public void addAttribute(AttributeValue value) {

		this.attributes.put(value.getName(), value.getID());
	}
	
	/**
	*
	* (Required)
	*
	*/
	public String getNameID() {
		return nameID;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setNameID(String nameID) {
		this.nameID = nameID;
	}
	
	/**
	* Containers inside this container
	*
	*/
	public List<Integer> getChildren() {
		return children;
	}
	
	/**
	* Containers inside this container
	*
	*/
	public void setChildrenAsId(List<Integer> children) {
		this.children = children;
	}
	
	public void setChildren(List<Container> children) {
		
		this.children.clear();
		
		for (Container child : children) {
			this.children.add(child.getID());
		}
	}
	
	/**
	* Containers directly after this one
	*
	*/
	public List<Integer> getNext() {
		return next;
	}
	
	/**
	* Containers directly after this one
	*
	*/
	public void setNextAsId(List<Integer> next) {
		this.next = next;
	}
	
	public void setNext(List<Container> next) {
		
		this.next.clear();
		
		for (Container nextContainer : next) {
			this.next.add(nextContainer.getID());
		}
	}
	
	/**
	* Elements in this container
	*
	*/
	public List<String> getElements() {
		return elements;
	}
	
	/**
	* Elements in this container
	*
	*/
	public void setElementsAsNames(List<String> elements) {
		this.elements = elements;
	}
	
	public void setElements(List<Element> elements) {
		
		this.elements.clear();
		
		for (Element element : elements) {
			this.elements.add(element.getNameID());
		}
	}
	
	/**
	* Attributes describing this container
	*
	*/
	public HashMap<String, Integer> getAttributes() {
		return attributes;
	}
	
	/**
	* Attributes describing this container
	*
	*/
	public void setAttributes(List<AttributeValue> attributes) {
		
		this.attributes.clear();
		
		for (AttributeValue atr : attributes) {
			this.attributes.put(atr.getName(), atr.getID());
		}
	}
	
	public void setAttributesAsIds(HashMap<String, Integer> attributes) {
		
		this.attributes = attributes;
	}
	
	/*public String toString() {
		StringBuilder lel = new StringBuilder();
		
		lel.append(this.nameID + ";");
		for(Container co : this.children) {
			lel.append(co.getNameID() + ",");
		}
		
		lel.append(";");
		
		for (Container co : this.next) {
			lel.append(co.getNameID() + ",");
		}
		
		lel.append(";");
		
		for (Element e : this.elements) {
			lel.append(e.getNameID() + ",");
		}
		
		lel.append(";");
		
		for (AttributeValue a : this.attributes) {
			lel.append(a.getValue().toString() + ",");
		}
		
		return lel.toString();
	}*/
}
