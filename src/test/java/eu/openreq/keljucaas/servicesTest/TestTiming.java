package eu.openreq.keljucaas.servicesTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.openreq.keljucaas.services.Timing;
import eu.openreq.keljucaas.services.Timing.TimePair;

public class TestTiming {
	
	@Test
	public void testDurationBasic() {
		Timing t1 = new Timing();
		t1.setStart("test", 1000000);
		t1.setEnd("test", 2000000);
		assertEquals(t1.getDuration_ns("test"), new Long(1000000));
		assertEquals(t1.getDuration_ms("test"), new Long(1));
		TimePair test = t1.getTimes("test");
		assertEquals(test.getStart(), new Long(1000000)); 
		assertEquals(test.getEnd(), new Long(2000000));
		test.setEnd(null);
		assertNull(test.getEnd());
		assertNull(test.getDuration_ns());
		assertNull(test.getDuration_ms());
		test.setStart(null);
		assertNull(test.getStart()); 
		assertNull(test.getEnd());
		assertNull(test.getDuration_ns());
		assertNull(test.getDuration_ms());

	}

	@Test
	public void testTimeLeftBasic() {
		TimePair tp = new TimePair();
		Long start = tp.getStart();
		assertTrue (start <= System.nanoTime());
		
		Long left_ms = tp.getTimeLeft_ms(2);
		assertTrue(left_ms.longValue() >= 1);

		Long left_ns = tp.getTimeLeft_ns(2000000);
		assertTrue(left_ns.longValue() >= 1);
	}
	
	@Test
	public void testTimeNull() {
		Timing t1 = new Timing();
		assertNull(t1.getDuration_ms("noexist"));
		assertNull(t1.getDuration_ns("noexist"));
		TimePair tp = new TimePair();
		tp.setStart(null);
		t1.setTimes("nullStart", tp);
		assertNull(t1.getDuration_ms("nullStart"));
		assertNull(t1.getDuration_ns("nullStart"));
		assertNull(tp.getTimeLeft_ms(1));
		assertNull(tp.getTimeLeft_ns(1));
	}

	@Test
	public void testEndDirect() {
		Timing t1 = new Timing();
		t1.setEnd("foo", 1000);
		t1.setStart("foo", 100);
		assertEquals(t1.getDuration_ns("foo"), new Long(900));
	}

}
