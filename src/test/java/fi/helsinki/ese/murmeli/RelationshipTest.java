package fi.helsinki.ese.murmeli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import fi.helsinki.ese.murmeli.AttributeValueType.BaseType;
import fi.helsinki.ese.murmeli.AttributeValueType.Bound;
import fi.helsinki.ese.murmeli.AttributeValueType.Cardinality;
import fi.helsinki.ese.murmeli.Relationship.NameType;


public class RelationshipTest {

	@Test
	public void TestRelationship1() {
		Relationship rel = new Relationship(NameType.CONTRIBUTES, "a", "b", 1);
		rel.addAttribute(new AttributeValue<Integer> ("v1", false, Integer.valueOf(1)));
		rel.getAttribute("v1");
	}
	
	@Test
	public void TestRelationship2() {
		Relationship rel1 = new Relationship(NameType.CONTRIBUTES, "a", "b", 1);
		rel1.addAttribute(new AttributeValue<Integer> ("v1", false, Integer.valueOf(1)));
		rel1.getAttribute("v1");
		
		Relationship rel2 = new Relationship(NameType.CONTRIBUTES, "a", "b", 1);
		rel2.addAttribute(new AttributeValue<Integer> ("v1", false, Integer.valueOf(1)));
		assertEquals(rel1, rel2);
	}

	@Test
	@Ignore ("Failing equals")
	public void TestRelationship3() {
		Element el1 = new Element("el1", 1);
		Element el2 = new Element("el2", 2);
		
		
		Relationship rel1 = new Relationship(NameType.EXCLUDES, el1, el2);
		Relationship rel2 = new Relationship(NameType.DAMAGES, el1, el2);
		assertNotEquals(rel1, rel2);
	}
	

	@Test
	public void TestRelationship4() {
		
		Relationship rel1 = new Relationship(NameType.EXCLUDES, "e1", null);
		Relationship rel2 = new Relationship(NameType.DAMAGES, null, "e2");
		assertNotEquals(rel1, rel2);
	}

	@Test
	public void TestRelationship5() {
		Element el1 = new Element("el1", 1);
		Element el2 = new Element("el2", 2);
		
		
		Relationship rel1 = new Relationship(NameType.EXCLUDES, el1, el2);
		rel1.addAttribute(new AttributeValue<Integer> ("v1", false, Integer.valueOf(1)));

		Relationship rel2 = new Relationship(NameType.DAMAGES, el1, el2);
		rel2.addAttribute(new AttributeValue<Integer> ("v1", false, Integer.valueOf(1)));
	}

	
}
		
