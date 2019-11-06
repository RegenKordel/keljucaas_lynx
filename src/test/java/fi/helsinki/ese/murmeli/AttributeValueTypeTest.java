package fi.helsinki.ese.murmeli;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import fi.helsinki.ese.murmeli.AttributeValueType.BaseType;
import fi.helsinki.ese.murmeli.AttributeValueType.Bound;
import fi.helsinki.ese.murmeli.AttributeValueType.Cardinality;


public class AttributeValueTypeTest {

	@Test
	public void valueTypeBoolSingle() {
		AttributeValueType avt = new  AttributeValueType(BaseType.BOOLEAN, Cardinality.SINGLE, "boolsingle", 1);
		assertEquals(avt.getCardinality(),  Cardinality.SINGLE);
		assertEquals(avt.getBaseType(), BaseType.BOOLEAN);
		assertEquals(avt.getID(), 1);
		assertEquals(avt.getName(), "boolsingle");
	}
	
	@Test
	public void valueTypeIntegerSingle1() {
		AttributeValueType avt = new  AttributeValueType(BaseType.INT, Cardinality.SINGLE, "intsingle");
		assertEquals(avt.getCardinality(),  Cardinality.SINGLE);
		assertEquals(avt.getBaseType(), BaseType.INT);
		assertEquals(avt.getName(), "intsingle");
	}

	@Test
	public void valueTypeIntegerSingle2() {
		AttributeValueType avt = new  AttributeValueType(Cardinality.SINGLE, 1, 100);
		assertEquals(avt.getCardinality(),  Cardinality.SINGLE);
		assertEquals(avt.getBaseType(), BaseType.INT);
		assertEquals (avt.getBound(), Bound.RANGE);
		int[] range = avt.getRange();
		assertEquals(range[0], 1);
		assertEquals(range[1], 100);
		range[0] = 2;
		avt.setRange(range);
		assertEquals(avt.getRange()[0], 2);
	}
	
	@Test
	public void valueTypeIntegerSingle3() {
		AttributeValueType avt = new  AttributeValueType(1, 100);
		assertEquals(avt.getCardinality(),  Cardinality.SINGLE);
		assertEquals(avt.getBaseType(), BaseType.INT);
		assertEquals (avt.getBound(), Bound.RANGE);
		int[] range = avt.getRange();
		assertEquals(range[0], 1);
		assertEquals(range[1], 100);
		range[0] = 2;
		avt.setRange(range);
		assertEquals(avt.getRange()[0], 2);
	}

	
	@Test
	public void valueTypeRange() {
		AttributeValueType avt = new  AttributeValueType(Cardinality.SINGLE, "range", 1, 100);
		assertEquals(avt.getCardinality(),  Cardinality.SINGLE);
		assertEquals(avt.getBaseType(), BaseType.INT);
		assertEquals(avt.getName(), "range");
		assertEquals (avt.getBound(), Bound.RANGE);
		int[] range = avt.getRange();
		assertEquals(range[0], 1);
		assertEquals(range[1], 100);
		range[0] = 2;
		avt.setRange(range);
		assertEquals(avt.getRange()[0], 2);
		
	}
	
	@Test
	public void valueTypeEnum() {
		List<AttributeValue<?>> values = new LinkedList<>();
		values.add(new AttributeValue<Integer> ("v1", false, Integer.valueOf(1)));
		values.add(new AttributeValue<Integer> ("v3", false, Integer.valueOf(3)));
		values.add(new AttributeValue<Integer> ("v5", false, Integer.valueOf(5)));
		values.add(new AttributeValue<Integer> ("v7", false, Integer.valueOf(7)));
		values.add(new AttributeValue<Integer> ("v9", false, Integer.valueOf(9)));
		
		AttributeValueType avt = new AttributeValueType(BaseType.INT, Cardinality.SINGLE);
		avt.setValues(values);
		avt.setBound(Bound.ENUM);
		assertEquals (avt.getBound(), Bound.ENUM);
		assertEquals(avt.getValues().size(), 5);
	}
	
}
		
