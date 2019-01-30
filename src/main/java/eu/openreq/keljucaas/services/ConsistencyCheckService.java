package eu.openreq.keljucaas.services;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import eu.openreq.keljucaas.domain.release.ReleasePlanInfo;
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
			ReleasePlanOutputFormatter relof = new ReleasePlanOutputFormatter(); 
			

			for (ReleasePlanInfo releasePlanInfo : releasePlanInfosToReport ) {
				ReleasePlanAnalysisDefinition wanted = releasePlanInfo.getWantedAnalysis();
				List <String> releasePlanTopics;
				if (wanted.isDiagnoseDesired())
					releasePlanTopics = getOriginalReleasePlanTopics();
				else
					releasePlanTopics = getDiagnosedReleasePlanCommonTopics();
				JsonObject releasePlanJson = new JsonObject();

				for (String topic: releasePlanTopics) {
					System.out.println(topic);
					relof.buildJsonCombinedOutput(releasePlanInfo, null, topic, ofmt, releasePlanJson);
					
				}
				//OutputDefinition jsonDef = wanted.getStructuredOutputDef();
				releasePlanArrays.add(releasePlanJson);
				StringBuffer sb = new StringBuffer();
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


	List<String> getUnassignedReleaseTopics() {
		String[] topicsToGet = {
		
				ReleasePlanOutputFormatter.topic_capacity_all,
				ReleasePlanOutputFormatter.topic_releases_requirements_not_assigned};
		List<String> topicList = new LinkedList<>();
		for (String s: topicsToGet)
			topicList.add(s);
		return topicList;
	}
	
	List<String> getNormalReleaseTopics() {
		String[] topicsToGet = {
				ReleasePlanOutputFormatter.topic_release_number,
				ReleasePlanOutputFormatter.topic_release_requirements_assigned,
				ReleasePlanOutputFormatter.topic_capacity_all
		};
		List<String> topicList = new LinkedList<>();
		for (String s: topicsToGet)
			topicList.add(s);
		return topicList;
	}


	List<String> getDiagnosedReleasePlanCommonTopics() {
		String[] topicsToGet = {
				ReleasePlanOutputFormatter.topic_release_plan_name,
				ReleasePlanOutputFormatter.topic_release_plan_consistent,
				ReleasePlanOutputFormatter.topic_diagnosis_combined,
		};
		List<String> topicList = new LinkedList<>();
		for (String s: topicsToGet)
			topicList.add(s);
		return topicList;
	}

	List<String> getOriginalReleasePlanTopics() {
		String[] topicsToGet = {
				ReleasePlanOutputFormatter.topic_release_plan_name,
				ReleasePlanOutputFormatter.topic_release_plan_consistent,
				ReleasePlanOutputFormatter.topic_relationships_broken,
				ReleasePlanOutputFormatter.topic_capacity_all
		};
		List<String> topicList = new LinkedList<>();
		for (String s: topicsToGet)
			topicList.add(s);
		return topicList;
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
