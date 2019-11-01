package eu.openreq.keljucaas.services;

import java.util.HashMap;
import java.util.Map;

public class Timing {
	
	public static class TimePair {
		Long start;
		Long end;

		public final Long getStart() {
			return start;
		}

		public final void setStart(Long start) {
			this.start = start;
		}

		public final Long getEnd() {
			return end;
		}

		public final void setEnd(Long end) {
			this.end = end;
		}

		public TimePair(Long start) {
			super();
			this.start = start;
		}
		
		public TimePair() {
			super();
			this.start = System.nanoTime();
		}

		public Long getDuration_ns() {
			if (start == null)
				return null;
			if (end == null)
				return null;
			return end - start;
		}

		public Long getDuration_ms() {
			if (start == null)
				return null;
			if (end == null)
				return null;
			return (end - start) / 1000000;
		}
		
		public Long getTimeLeft_ms (long duration_ms) {
			if (start == null)
				return null;
			long elapsed = System.nanoTime() - start;
			return  (duration_ms * 1000000 - elapsed) / 1000000;
		}

		public Long getTimeLeft_ns (long duration_ns) {
			if (start == null)
				return null;
			return  duration_ns + start - System.nanoTime();
		}

	}

	private Map<String, TimePair> times = new HashMap<>();
	
	public void setStart(String id, long start) {
		TimePair timepair = times.get(id);
		if (timepair == null) {
			timepair = new TimePair(start);
			times.put(id, timepair);
		} else {
			timepair.setStart(start);
		}
	}
	
	public void setEnd(String id, long end) {
		TimePair timepair = times.get(id);
		if (timepair == null) {
			timepair = new TimePair(null);
			times.put(id, timepair);
		}
		timepair.setEnd(end);
		}
	

	public Long getDuration_ns(String id) {
		TimePair timepair = times.get(id);
		if (timepair == null) {
			return null;
		}
		return timepair.getDuration_ns();
	}

	public Long getDuration_ms(String id) {
		TimePair timepair = times.get(id);
		if (timepair == null) {
			return null;
		}
		return timepair.getDuration_ms();
	}

}
