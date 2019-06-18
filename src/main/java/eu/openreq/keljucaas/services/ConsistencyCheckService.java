package eu.openreq.keljucaas.services;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import eu.openreq.keljucaas.domain.release.ReleaseInfo;
import eu.openreq.keljucaas.domain.release.ReleasePlanAnalysisDefinition;
import eu.openreq.keljucaas.domain.release.ReleasePlanInfo;
import eu.openreq.keljucaas.services.OutputFormatter.OutputElement;

@Service
public class ConsistencyCheckService {

	public static final String fieldSeparator = ","; 
	public static final String topicSeparator = "@"; 
	
	public static final String submitted = "analysis";
	public static final String diagnoseRequirements = "reqdiag";
	public static final String diagnoseRelationships= "reldiag";
	public static final String diagnoseRequirementsAndRelationships= "reqreldiag";
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
					releasePlanTopics = getDiagnosedReleasePlanCommonTopics();
				else
					releasePlanTopics = getOriginalReleasePlanTopics();
				JsonObject releasePlanJson = new JsonObject();

				for (String topic: releasePlanTopics) {
					relof.buildJsonCombinedOutput(releasePlanInfo, null, topic, ofmt, releasePlanJson);
				}

				List<String> releaseTopics;
				OutputElement releases = ofmt.getFormat(ReleasePlanOutputFormatter.topic_releases_element);

				JsonArray releaseArray = new JsonArray();

				for (ReleaseInfo releaseInfo : releasePlanInfo.getReleases()) {
					JsonObject releaseJson = new JsonObject();
					if (releaseInfo.getReleaseNr() <1)
						releaseTopics = getUnassignedReleaseTopics();
					else
						releaseTopics = getNormalReleaseTopics();
					for (String releaseTopic: releaseTopics) {
						relof.buildJsonCombinedOutput(releasePlanInfo, releaseInfo, releaseTopic, ofmt, releaseJson);
					}
					releaseArray.add(releaseJson);

				}
				releasePlanJson.add(
						releases.getDataKey(),
						releaseArray);
			

				releasePlanArrays.add(releasePlanJson);
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
				ReleasePlanOutputFormatter.topic_release_id_string,
				ReleasePlanOutputFormatter.topic_release_requirements_assigned,
				ReleasePlanOutputFormatter.topic_release_capacity_used
				};
		List<String> topicList = new LinkedList<>();
		for (String s: topicsToGet)
			topicList.add(s);
		return topicList;
	}

	List<String> getNormalReleaseTopics() {
		String[] topicsToGet = {
				ReleasePlanOutputFormatter.topic_release_id_string,
				ReleasePlanOutputFormatter.topic_release_requirements_assigned,
				ReleasePlanOutputFormatter.topic_release_capacity_all
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
				ReleasePlanOutputFormatter.topic_relationships_broken
		};
		List<String> topicList = new LinkedList<>();
		for (String s: topicsToGet)
			topicList.add(s);
		return topicList;
	}
}
