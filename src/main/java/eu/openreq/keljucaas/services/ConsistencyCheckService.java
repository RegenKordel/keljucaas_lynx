package eu.openreq.keljucaas.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import eu.openreq.keljucaas.domain.release.Diagnosable;
import eu.openreq.keljucaas.domain.release.ReleasePlanInfo;
import eu.openreq.keljucaas.services.CSPPlanner.OutputDefinition;
import eu.openreq.keljucaas.services.CSPPlanner.ReleasePlanAnalysisDefinition;

@Service
public class ConsistencyCheckService {

	public static final String fieldSeparator = ","; 
	public static final String topicSeparator = "@"; 

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

	//The version that returns all diagnosed requirements as their own one requirement arrays
	/**
	 * Generate response (consistency and diagnosis) as JSON String
	 * @param isCOnsistent
	 * @param explanationStr
	 * @return
	 */


	public String generateProjectJsonResponseDetailed(List <ReleasePlanInfo> releasePlanInfosToReport) {
		try {
			JsonObject responseObject = new JsonObject();
			JsonArray releasePlanArrays = new JsonArray();
			OutputFormatter ofmt = ReleasePlanOutputFormatter.intitializeOutputFormats();
			

			for (ReleasePlanInfo releasePlanInfo : releasePlanInfosToReport ) {
				ReleasePlanAnalysisDefinition wanted = releasePlanInfo.getWantedAnalysis();
				OutputDefinition jsonDef = wanted.getStructuredOutputDef();
				JsonObject releasePlanJson = new JsonObject();
				releasePlanJson.addProperty("version", releasePlanInfo.getIdString());
				releasePlanJson.addProperty("consistent", releasePlanInfo.isConsistent());
				if (jsonDef.isDiagnosisWanted()) {
					List<Diagnosable> diagnosis = releasePlanInfo.getAppliedDiagnosis();
					JsonArray diagnosisArray = new JsonArray();
					if (diagnosis != null) {
						for (Diagnosable diagnosed :diagnosis) {
							diagnosisArray.add(diagnosed.getNameId());
						}
					}
					releasePlanJson.add("diagnosis", diagnosisArray);
				}
				
//				if (jsonDef.isIncludedRequirementsWanted()) {
//					releasePlanInfo.get
//				}
				releasePlanArrays.add(releasePlanJson);
				StringBuffer sb = new StringBuffer();
				
				ReleasePlanOutputFormatter relof = new ReleasePlanOutputFormatter(); 
				relof.buildFormattedTextOutput (releasePlanInfo, null, "diagnosis.combined", ofmt, sb);
				System.out.println("************ "+ sb.toString());

			}


			responseObject.add("response", releasePlanArrays);

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String prettyResponseJson = gson.toJson(responseObject);

			return prettyResponseJson;
		}
		catch (Exception ex) {
			return null;
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
