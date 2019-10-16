package eu.openreq.keljucaas.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${keljucaas.releaseplanner.addTimings}")
	private boolean addTimingOutputs; 

	@Value("${keljucaas.releaseplanner.reportConsistentRelationships}")
	private boolean reportConsistentRelationships; 

	public static final String fieldSeparator = ","; 
	public static final String topicSeparator = "@"; 
	
	public static final String submitted = "analysis";
	public static final String diagnoseRequirements = "reqdiag";
	public static final String diagnoseRelationships= "reldiag";
	public static final String diagnoseRequirementsAndRelationships= "reqreldiag";

	public static final List <String> 	UNASSIGNED_RELEASE_TOPICS_OPENREQ = 
			Collections.unmodifiableList(new LinkedList<String>(Arrays.asList(
					ReleasePlanOutputFormatter.topic_release_id_string,
					ReleasePlanOutputFormatter.topic_release_requirements_assigned,
					ReleasePlanOutputFormatter.topic_release_capacity_used
					)));

	public static final List<String> NORMAL_RELEASE_TOPICS_OPENREQ =
			Collections.unmodifiableList(new LinkedList<String>(Arrays.asList(
					ReleasePlanOutputFormatter.topic_release_id_string,
					ReleasePlanOutputFormatter.topic_release_requirements_assigned,
					ReleasePlanOutputFormatter.topic_release_capacity_all
					)));


	public static final List<String> DIAGNOSED_RELEASEPLAN_COMMONTOPICS_OPENREQ = 
			Collections.unmodifiableList(new LinkedList<String>(Arrays.asList(
					ReleasePlanOutputFormatter.topic_release_plan_name,
					ReleasePlanOutputFormatter.topic_release_plan_consistent,
					ReleasePlanOutputFormatter.topic_diagnosis_combined
					)));

	public static final List<String> ORIGINAL_RELEASEPLAN_TOPICS_OPENREQ = 
			Collections.unmodifiableList(new LinkedList<String>(Arrays.asList(
					ReleasePlanOutputFormatter.topic_release_plan_name,
					ReleasePlanOutputFormatter.topic_release_plan_consistent,
					ReleasePlanOutputFormatter.topic_relationships_broken
					)));
	
	public static final List <String> 	UNASSIGNED_RELEASE_TOPICS_ALL = 
			Collections.unmodifiableList(new LinkedList<String>(Arrays.asList(
					ReleasePlanOutputFormatter.topic_release_id_string,
					ReleasePlanOutputFormatter.topic_release_number,
					ReleasePlanOutputFormatter.topic_release_requirements_assigned,
					ReleasePlanOutputFormatter.topic_release_capacity_used,
					ReleasePlanOutputFormatter.topic_release_capacity_available,
					ReleasePlanOutputFormatter.topic_release_capacity_balance,
					ReleasePlanOutputFormatter.topic_release_capacity_used
					)));

	public static final List<String> NORMAL_RELEASE_TOPICS_ALL =
			Collections.unmodifiableList(new LinkedList<String>(Arrays.asList(
					ReleasePlanOutputFormatter.topic_release_id_string,
					ReleasePlanOutputFormatter.topic_release_requirements_assigned,
					ReleasePlanOutputFormatter.topic_release_capacity_all
					)));


	public static final List<String> DIAGNOSED_RELEASEPLAN_ALL= 
			Collections.unmodifiableList(new LinkedList<String>(Arrays.asList(
					ReleasePlanOutputFormatter.topic_release_plan_name,
					ReleasePlanOutputFormatter.topic_release_plan_consistent,
					ReleasePlanOutputFormatter.topic_release_plan_duration_ms,
					ReleasePlanOutputFormatter.topic_diagnosis_combined,
					ReleasePlanOutputFormatter.topic_relationhips_exluded,
					ReleasePlanOutputFormatter.topic_relationships_ignored,
					ReleasePlanOutputFormatter.topic_diagnosis_relationships,
					ReleasePlanOutputFormatter.topic_diagnosis_requirements
					)));

	public static final List<String> ORIGINAL_RELEASEPLAN_TOPICS_ALL = 
			Collections.unmodifiableList(new LinkedList<String>(Arrays.asList(
					ReleasePlanOutputFormatter.topic_release_plan_name,
					ReleasePlanOutputFormatter.topic_release_plan_consistent,
					ReleasePlanOutputFormatter.topic_release_plan_duration_ms,
					ReleasePlanOutputFormatter.topic_relationships_broken,
					ReleasePlanOutputFormatter.topic_relationships_ok
					)));

	private List <String> unassignedReleaseTopics =  UNASSIGNED_RELEASE_TOPICS_OPENREQ;
	private List <String> normalReleaseTopics = NORMAL_RELEASE_TOPICS_OPENREQ;
	private List <String> diagnosedReleasePlanCommonTopics = DIAGNOSED_RELEASEPLAN_COMMONTOPICS_OPENREQ;
	private List <String> originalReleasePlanTopics = ORIGINAL_RELEASEPLAN_TOPICS_OPENREQ;
	
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


	public String generateProjectJsonResponseDetailed(List<ReleasePlanInfo> releasePlanInfosToReport, int timeOut_ms) {
		JsonObject responseObject = new JsonObject();
		JsonArray releasePlanArrays = new JsonArray();
		OutputFormatter ofmt = ReleasePlanOutputFormatter.intitializeOutputFormats();
		ReleasePlanOutputFormatter relof = new ReleasePlanOutputFormatter();

		for (ReleasePlanInfo releasePlanInfo : releasePlanInfosToReport) {
			ReleasePlanAnalysisDefinition wanted = releasePlanInfo.getWantedAnalysis();
			List<String> releasePlanTopics = new LinkedList<>();
			if (wanted.isDiagnoseDesired())
				releasePlanTopics.addAll(getDiagnosedReleasePlanCommonTopics());
			else
				releasePlanTopics.addAll(getOriginalReleasePlanTopics());

			if (addTimingOutputs)
				releasePlanTopics.add(ReleasePlanOutputFormatter.topic_release_plan_duration_ms);
			if (timeOut_ms >0)
				releasePlanTopics.add(ReleasePlanOutputFormatter.topic_release_plan_has_timeout);

			JsonObject releasePlanJson = new JsonObject();
			if (wanted.isOmitCrossProject()) {
				releasePlanTopics.add(ReleasePlanOutputFormatter.topic_relationships_ignored);
			}
			
			if (reportConsistentRelationships) {
				releasePlanTopics.add(ReleasePlanOutputFormatter.topic_relationships_ok);
			}

			for (String topic : releasePlanTopics) {
				relof.buildJsonCombinedOutput(releasePlanInfo, null, topic, ofmt, releasePlanJson);
			}

			List<String> releaseTopics;
			OutputElement releases = ofmt.getFormat(ReleasePlanOutputFormatter.topic_releases_element);

			JsonArray releaseArray = new JsonArray();

			for (ReleaseInfo releaseInfo : releasePlanInfo.getReleases()) {
				JsonObject releaseJson = new JsonObject();
				if (releaseInfo.getReleaseNr() < 1)
					releaseTopics = getUnassignedReleaseTopics();
				else
					releaseTopics = getNormalReleaseTopics();
				for (String releaseTopic : releaseTopics) {
					relof.buildJsonCombinedOutput(releasePlanInfo, releaseInfo, releaseTopic, ofmt, releaseJson);
				}
				releaseArray.add(releaseJson);

			}
			releasePlanJson.add(releases.getDataKey(), releaseArray);

			releasePlanArrays.add(releasePlanJson);
		}

		responseObject.add("response", releasePlanArrays);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyResponseJson = gson.toJson(responseObject);

		return prettyResponseJson;
	}

	List<String> getUnassignedReleaseTopics() {
		return unassignedReleaseTopics;
	}

	List<String> getNormalReleaseTopics() {
		return normalReleaseTopics;
	}


	List<String> getDiagnosedReleasePlanCommonTopics() {
		return diagnosedReleasePlanCommonTopics;
	}

	List<String> getOriginalReleasePlanTopics() {
		return originalReleasePlanTopics;
	}

	public final void setUnassignedReleaseTopics(List<String> unassignedReleaseTopics) {
		this.unassignedReleaseTopics = unassignedReleaseTopics;
	}

	public final void setNormalReleaseTopics(List<String> normalReleaseTopics) {
		this.normalReleaseTopics = normalReleaseTopics;
	}

	public final void setDiagnosedReleasePlanCommonTopics(List<String> diagnosedReleasePlanCommonTopics) {
		this.diagnosedReleasePlanCommonTopics = diagnosedReleasePlanCommonTopics;
	}

	public final void setOriginalReleasePlanTopics(List<String> originalReleasePlanTopics) {
		this.originalReleasePlanTopics = originalReleasePlanTopics;
	}
	
	
}
