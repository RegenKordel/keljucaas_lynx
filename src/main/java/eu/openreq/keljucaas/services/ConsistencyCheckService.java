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
	
	@Value("${keljucaas.releaseplanner.reportWithPlaintextMessages}")
	private boolean reportWithPlaintextMessages; 

	public static final String FIELD_SEPARATOR = ","; 
	public static final String TOPIC_SEPARATOR = "@"; 
	
	public static final String SUBMITTED = "analysis";
	public static final String DIAGNOSE_REQUIREMENTS = "reqdiag";
	public static final String DIAGNOSE_RELATIONSHIPS = "reldiag";
	public static final String DIAGNOSE_REQUIREMENTS_AND_RELATIONSHIPS = "reqreldiag";

	public static final List <String> 	UNASSIGNED_RELEASE_TOPICS_OPENREQ = 
			Collections.unmodifiableList(new LinkedList<String>(Arrays.asList(
					ReleasePlanOutputFormatter.TOPIC_RELEASE_ID_STRING,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_REQUIREMENTS_ASSIGNED,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_CAPACITY_USED
					)));

	public static final List<String> NORMAL_RELEASE_TOPICS_OPENREQ =
			Collections.unmodifiableList(new LinkedList<String>(Arrays.asList(
					ReleasePlanOutputFormatter.TOPIC_RELEASE_ID_STRING,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_REQUIREMENTS_ASSIGNED,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_CAPACITY_ALL
					)));


	public static final List<String> DIAGNOSED_RELEASEPLAN_COMMONTOPICS_OPENREQ = 
			Collections.unmodifiableList(new LinkedList<String>(Arrays.asList(
					ReleasePlanOutputFormatter.TOPIC_RELEASE_PLAN_NAME,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_PLAN_CONSISTENT,
					ReleasePlanOutputFormatter.TOPIC_DIAGNOSIS_COMBINED
					)));

	public static final List<String> ORIGINAL_RELEASEPLAN_TOPICS_OPENREQ = 
			Collections.unmodifiableList(new LinkedList<String>(Arrays.asList(
					ReleasePlanOutputFormatter.TOPIC_RELEASE_PLAN_NAME,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_PLAN_CONSISTENT,
					ReleasePlanOutputFormatter.TOPIC_RELATIONSHIPS_BROKEN
					)));
	
	public static final List <String> 	UNASSIGNED_RELEASE_TOPICS_ALL = 
			Collections.unmodifiableList(new LinkedList<String>(Arrays.asList(
					ReleasePlanOutputFormatter.TOPIC_RELEASE_ID_STRING,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_NUMBER,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_REQUIREMENTS_ASSIGNED,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_CAPACITY_USED,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_CAPACITY_AVAILABLE,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_CAPACITY_BALANCE,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_CAPACITY_USED
					)));

	public static final List<String> NORMAL_RELEASE_TOPICS_ALL =
			Collections.unmodifiableList(new LinkedList<String>(Arrays.asList(
					ReleasePlanOutputFormatter.TOPIC_RELEASE_ID_STRING,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_REQUIREMENTS_ASSIGNED,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_CAPACITY_ALL
					)));


	public static final List<String> DIAGNOSED_RELEASEPLAN_ALL= 
			Collections.unmodifiableList(new LinkedList<String>(Arrays.asList(
					ReleasePlanOutputFormatter.TOPIC_RELEASE_PLAN_NAME,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_PLAN_CONSISTENT,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_PLAN_DURATION_MS,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_PLAN_HAS_TIMEOUT,
					ReleasePlanOutputFormatter.TOPIC_DIAGNOSIS_COMBINED,
					ReleasePlanOutputFormatter.TOPIC_RELATIONHIPS_EXLUDED,
					ReleasePlanOutputFormatter.TOPIC_RELATIONSHIPS_IGNORED,
					ReleasePlanOutputFormatter.TOPIC_DIAGNOSIS_RELATIONSHIPS,
					ReleasePlanOutputFormatter.TOPIC_DIAGNOSIS_REQUIREMENTS
					)));

	public static final List<String> ORIGINAL_RELEASEPLAN_TOPICS_ALL = 
			Collections.unmodifiableList(new LinkedList<String>(Arrays.asList(
					ReleasePlanOutputFormatter.TOPIC_RELEASE_PLAN_NAME,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_PLAN_CONSISTENT,
					ReleasePlanOutputFormatter.TOPIC_RELEASE_PLAN_DURATION_MS,
					ReleasePlanOutputFormatter.TOPIC_RELATIONSHIPS_BROKEN,
					ReleasePlanOutputFormatter.TOPIC_RELATIONSHIPS_OK,
					ReleasePlanOutputFormatter.TOPIC_RELATIONSHIPS_IGNORED
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
				releasePlanTopics.add(ReleasePlanOutputFormatter.TOPIC_RELEASE_PLAN_DURATION_MS);
			if (timeOut_ms >0)
				releasePlanTopics.add(ReleasePlanOutputFormatter.TOPIC_RELEASE_PLAN_HAS_TIMEOUT);

			JsonObject releasePlanJson = new JsonObject();
			if (wanted.isOmitCrossProject()) {
				releasePlanTopics.add(ReleasePlanOutputFormatter.TOPIC_RELATIONSHIPS_IGNORED);
			}
			
			if (reportConsistentRelationships) {
				releasePlanTopics.add(ReleasePlanOutputFormatter.TOPIC_RELATIONSHIPS_OK);
			}

			for (String topic : releasePlanTopics) {
				if (reportWithPlaintextMessages)
					relof.buildJsonCombinedOutput(releasePlanInfo, null, topic, ofmt, releasePlanJson);
				else
					relof.buildJsonOutput(releasePlanInfo, null, topic, ofmt, releasePlanJson);	
			}

			List<String> releaseTopics;
			OutputElement releases = ofmt.getFormat(ReleasePlanOutputFormatter.TOPIC_RELEASES_ELEMENT);

			JsonArray releaseArray = new JsonArray();

			for (ReleaseInfo releaseInfo : releasePlanInfo.getReleases()) {
				JsonObject releaseJson = new JsonObject();
				
				if (releaseInfo.getReleaseNr() < 1)
					releaseTopics = getUnassignedReleaseTopics();
				else
					releaseTopics = getNormalReleaseTopics();
				
				for (String releaseTopic : releaseTopics) {
					if (reportWithPlaintextMessages)
						relof.buildJsonCombinedOutput(releasePlanInfo, releaseInfo, releaseTopic, ofmt, releaseJson);
					else
						relof.buildJsonOutput(releasePlanInfo, releaseInfo, releaseTopic, ofmt, releaseJson);	
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
