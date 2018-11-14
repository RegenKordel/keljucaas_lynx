package eu.openreq.keljucaas.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.springframework.stereotype.Service;

@Service
public class ConsistencyCheckService {
		
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
					// JsonArray allArrays = new JsonArray();
					 JsonArray reqArray = new JsonArray();
					 for (int i = 0; i < parts.length; i++) {
						
//						JsonObject part = new JsonObject();
//						
//						part.addProperty("requirement", parts[i]);
//						reqArray.add(part);
						reqArray.add(parts[i]);
					//	allArrays.add(reqArray);
					}
					 diagnosis.add("diagnosis", reqArray);
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

// The version that returns all diagnosed requirements as their own one requirement arrays
//	/**
//	 * Generate response (consistency and diagnosis) as JSON String
//	 * @param isCOnsistent
//	 * @param explanationStr
//	 * @return
//	 */
//	public String generateProjectJsonResponse(boolean isCOnsistent, String explanationStr, boolean diagnosisWanted) {
//		if (diagnosisWanted) { 
//			try {
//				 JsonObject responseObject = new JsonObject();
//				 
//				 JsonObject diagnosis = new JsonObject();
//				 diagnosis.addProperty("consistent", isCOnsistent);
//				 
//				 String[] parts = explanationStr.split(",");
//				 
//				 if(!isCOnsistent) {
//					 JsonArray allArrays = new JsonArray();
//					 for (int i = 0; i < parts.length; i++) {
//						JsonArray reqArray = new JsonArray();
//						JsonObject part = new JsonObject();
//						part.addProperty("requirement", parts[i]);
//						reqArray.add(part);
//						allArrays.add(reqArray);
//					}
//					 diagnosis.add("diagnosis", allArrays);
//				 }
//				 
//				 responseObject.add("response", diagnosis);
//				 
//				 Gson gson = new GsonBuilder().setPrettyPrinting().create();
//			     String prettyResponseJson = gson.toJson(responseObject);
//	
//		         return prettyResponseJson;
//			 }
//			 catch (Exception ex) {
//				 return null;
//			 }
//		} else {
//			try {
//				 JsonObject responseObject = new JsonObject();
//				 
//				 JsonObject diagnosis = new JsonObject();
//				 diagnosis.addProperty("consistent", isCOnsistent);
//				 
//				 responseObject.add("response", diagnosis);
//				 
//				 Gson gson = new GsonBuilder().setPrettyPrinting().create();
//			     String prettyResponseJson = gson.toJson(responseObject);
//	
//		         return prettyResponseJson;
//			 }
//			 catch (Exception ex) {
//				 return null;
//			 }
//		}
//	}
	
	
}
