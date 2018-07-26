package eu.openreq.keljucaas.services;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import java.io.StringReader;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Methods used to convert between formats (MulSON, ReqIF, configuration)
 * 
 * @author iivorait
 * @author tlaurinen
 */
@Service
public class FormatTransformerService {
	
	private HashMap<String, Integer> usedNames;

		
	/**
	 * Generate response (consistency and diagnosis) as JSON String
	 * @param isCOnsistent
	 * @param explanationStr
	 * @return
	 */
	public String generateProjectJsonResponse(boolean isCOnsistent, String explanationStr, boolean diagnosisWanted) {
		if (diagnosisWanted) { 
			try {
				 JsonObject responseObject = new JsonObject();
				 
				 JsonObject diagnosis = new JsonObject();
				 diagnosis.addProperty("consistent", isCOnsistent);
				 
				 String[] parts = explanationStr.split(",");
				 
				 if(!isCOnsistent) {
					 JsonArray allArrays = new JsonArray();
					 for (int i = 0; i < parts.length; i++) {
						JsonArray reqArray = new JsonArray();
						JsonObject part = new JsonObject();
						part.addProperty("requirement", parts[i]);
						reqArray.add(part);
						allArrays.add(reqArray);
					}
					 diagnosis.add("diagnosis", allArrays);
				 }
				 
				 responseObject.add("response", diagnosis);
				 
				 Gson gson = new GsonBuilder().setPrettyPrinting().create();
			     String prettyResponseJson = gson.toJson(responseObject);
	
		         return prettyResponseJson;
			 }
			 catch (Exception ex) {
				 return null;
			 }
		} else {
			try {
				 JsonObject responseObject = new JsonObject();
				 
				 JsonObject diagnosis = new JsonObject();
				 diagnosis.addProperty("consistent", isCOnsistent);
				 
				 responseObject.add("response", diagnosis);
				 
				 Gson gson = new GsonBuilder().setPrettyPrinting().create();
			     String prettyResponseJson = gson.toJson(responseObject);
	
		         return prettyResponseJson;
			 }
			 catch (Exception ex) {
				 return null;
			 }
		}
	}
	
}
