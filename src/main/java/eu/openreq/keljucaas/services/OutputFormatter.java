package eu.openreq.keljucaas.services;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Locale;

public class OutputFormatter {

	public static class OutputElement {
		private String format;
		private String dataKey;

		
		public OutputElement(String format, String dataKey) {
			super();
			this.format = format;
			this.dataKey = dataKey;
		}

		public String getFormat() {
			return format;
		}

		public String getDataKey() {
			return dataKey;
		}

		@Override
		public String toString() {
			return "OutputElement [format=" + format + ", dataKey=" + dataKey + "]";
		}
		
		
	}

	private LinkedHashMap<String, OutputElement> outputs = new LinkedHashMap<>();
	private Locale locale;
	
	public OutputElement getFormat(String key) {
		OutputElement elem = outputs.get(key);
		if (elem == null)
			elem = outputs.get("default");
		return elem;
	}
	
	public void setFormat (String key, OutputElement output) {
		outputs.put(key, output);
	}
	
	public OutputFormatter(Locale locale) {
		super();
		this.locale = locale;
	}
	
	public MessageFormat getTextMessageFormatter(String topic) {
		
		OutputElement outputElement = getFormat(topic);
		MessageFormat fmt = new MessageFormat(outputElement.getFormat(), locale);
		return fmt;
	}
	
	public void appendString(String toOutput, String topic, StringBuffer sb) {
		if (toOutput == null) {
			OutputElement outputElement = getFormat(topic);
			sb.append(outputElement.getFormat());
		}
		else {
			MessageFormat fmt = getTextMessageFormatter(topic);
			String[] toFormat = new String[1];
			toFormat[0] = toOutput;
			sb.append (fmt.format(toFormat));
		}
	}
	
	public void appendArgs(Object[] toOutput, String topic, StringBuffer sb) {
		if (toOutput == null) {
			OutputElement outputElement = getFormat(topic);
			sb.append(outputElement.getFormat());
		}
		else {
			MessageFormat fmt = getTextMessageFormatter(topic);
			sb.append (fmt.format(toOutput));
		}
	}

	

	@Override
	public String toString() {
		return "OutputFormatter [outputs=" + outputs + "]";
	}
	
	
}
