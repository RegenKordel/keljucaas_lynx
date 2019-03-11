package eu.openreq.keljucaas.services;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Locale;

public class OutputFormatter {

	public static class OutputElement {
		private String format; //for formatting text for message
		private String dataKey; //for data in Json
		private String messageKey; //for dataKey of message as Json
		
		public OutputElement(String format, String dataKey) {
			super();
			this.format = format;
			this.dataKey = dataKey;
			this.messageKey = dataKey + "_msg";
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

		public String getMessageKey() {
			return messageKey;
		}

		public void setMessageKey(String messageKey) {
			this.messageKey = messageKey;
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
	
	public String getDataKey(String topic) {
		OutputElement outputElement = getFormat(topic);
		if (outputElement == null)
			return "";
		return outputElement.getDataKey();
	}

	

	@Override
	public String toString() {
		return "OutputFormatter [outputs=" + outputs + "]";
	}
	
	
}
