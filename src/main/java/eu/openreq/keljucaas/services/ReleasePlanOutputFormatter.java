package eu.openreq.keljucaas.services;

import static  eu.openreq.keljucaas.services.ConsistencyCheckService.DIAGNOSE_RELATIONSHIPS;
import static  eu.openreq.keljucaas.services.ConsistencyCheckService.DIAGNOSE_REQUIREMENTS;
import static eu.openreq.keljucaas.services.ConsistencyCheckService.DIAGNOSE_REQUIREMENTS_AND_RELATIONSHIPS;
import static eu.openreq.keljucaas.services.ConsistencyCheckService.SUBMITTED;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import eu.openreq.keljucaas.domain.release.Diagnosable;
import eu.openreq.keljucaas.domain.release.Element4Csp;
import eu.openreq.keljucaas.domain.release.IgnoredRelationship;
import eu.openreq.keljucaas.domain.release.Relationship4Csp;
import eu.openreq.keljucaas.domain.release.ReleaseInfo;
import eu.openreq.keljucaas.domain.release.ReleasePlanInfo;
import eu.openreq.keljucaas.services.OutputFormatter.OutputElement;


public class ReleasePlanOutputFormatter {
	public static final String TOPIC_DEFAULT = "default";
	public static final String TOPIC_EMPTY_LIST = "empty.list";

	public static final String TOPIC_RELATIONSHIP_FROM = "relationship.from";
	public static final String TOPIC_RELATIONSHIP_TO = "relationship.to";
	public static final String TOPIC_RELATIONSHIP_TYPE = "relationship.type";
	public static final String TOPIC_RELATIONHIP = "relationhip";



	public static final String TOPIC_DIAGNOSIS_COMBINED = "diagnosis.combined";
	public static final String TOPIC_DIAGNOSIS_RELATIONSHIPS = "diagnosis.relationships";
	public static final String TOPIC_DIAGNOSIS_REQUIREMENTS = "diagnosis.requirements";
	public static final String TOPIC_DIAGNOSIS_NODIAGNOSIS = "diagnosis.nodiagnosis";
	public static final String TOPIC_ELEMENT_SEPARATOR = "element.separator";
	public static final String TOPIC_LIST_ELEMENT_SEPARATOR = "list.element.separator";
	public static final String TOPIC_RELATIONHIPS_EXLUDED = "relationhips.exluded";
	public static final String TOPIC_RELATIONSHIPS_BROKEN = "relationships.broken";
	public static final String TOPIC_RELATIONSHIPS_IGNORED = "relationships.ignored";
	public static final String TOPIC_RELATIONSHIPS_OK = "relationships.ok";
	public static final String TOPIC_RELEASES_ELEMENT = "releases.element";
	public static final String TOPIC_RELEASE_CAPACITY_ALL = "release.capacity.all";
	public static final String TOPIC_RELEASE_CAPACITY_AVAILABLE = "release.capacity.available";
	public static final String TOPIC_RELEASE_CAPACITY_BALANCE = "release.capacity.balance";
	public static final String TOPIC_RELEASE_CAPACITY_USED = "release.capacity.used";
	
	public static final String TOPIC_RELEASE_NUMBER = "release.number";
	public static final String TOPIC_RELEASE_ID_STRING = "release.id_string";
	public static final String TOPIC_RELEASE_SURROUNDCHAR ="release.surroundchar";
	public static final String TOPIC_RELEASE_PLAN_CONSISTENT = "release.plan.consistent";
	public static final String TOPIC_RELEASE_PLAN_HAS_TIMEOUT = "release.plan.has.timeout";
	public static final String TOPIC_RELEASE_PLAN_INCONSISTENT = "release.plan.inconsistent";
	public static final String TOPIC_RELEASE_PLAN_NAME = "release.plan.name";
	public static final String TOPIC_RELEASE_PLAN_DURATION_MS = "release.plan.durarion.ms";
	public static final String TOPIC_RELEASE_REQUIREMENTS_ASSIGNED = "release.requirements.assigned";
	public static final String TOPIC_RELEASES_REQUIREMENTS_NOT_ASSIGNED = "releases.requirements.not.assigned";

	public static final String TOPIC_RELEASE_PLAN_SUBMITTED = "release.plan.submitted";
	public static final String TOPIC_RELEASE_PLAN_DIAGNOSEREQUIREMENTS = "release.plan.diagnoseRequirements";
	public static final String TOPIC_RELEASE_PLAN_DIAGNOSERELATIONSHIPS ="release.plan.diagnoseRelationships";
	public static final String TOPIC_RELEASE_PLAN_DIAGNOSEREQUIREMENTSANDRELATIONSHIPS = "release.plan.diagnoseRequirementsAndRelationships";

	private static final String[] availableTopics = {
			TOPIC_DEFAULT,
			TOPIC_EMPTY_LIST,
			TOPIC_RELATIONSHIP_FROM,
			TOPIC_RELATIONSHIP_TO,
			TOPIC_RELATIONSHIP_TYPE,
			TOPIC_RELATIONHIP,
			TOPIC_DIAGNOSIS_COMBINED,
			TOPIC_DIAGNOSIS_RELATIONSHIPS,
			TOPIC_DIAGNOSIS_REQUIREMENTS,
			TOPIC_DIAGNOSIS_NODIAGNOSIS,
			TOPIC_ELEMENT_SEPARATOR,
			TOPIC_LIST_ELEMENT_SEPARATOR,
			TOPIC_RELATIONHIPS_EXLUDED,
			TOPIC_RELATIONSHIPS_BROKEN,
			TOPIC_RELATIONSHIPS_IGNORED,
			TOPIC_RELATIONSHIPS_OK,
			TOPIC_RELEASES_ELEMENT,
			TOPIC_RELEASE_CAPACITY_ALL,
			TOPIC_RELEASE_CAPACITY_AVAILABLE,
			TOPIC_RELEASE_CAPACITY_BALANCE,
			TOPIC_RELEASE_CAPACITY_USED,
			TOPIC_RELEASE_NUMBER,
			TOPIC_RELEASE_ID_STRING,
			TOPIC_RELEASE_SURROUNDCHAR,
			TOPIC_RELEASE_PLAN_CONSISTENT,
			TOPIC_RELEASE_PLAN_HAS_TIMEOUT,
			TOPIC_RELEASE_PLAN_INCONSISTENT,
			TOPIC_RELEASE_PLAN_NAME,
			TOPIC_RELEASE_PLAN_DURATION_MS,
			TOPIC_RELEASE_REQUIREMENTS_ASSIGNED,
			TOPIC_RELEASES_REQUIREMENTS_NOT_ASSIGNED,
			TOPIC_RELEASE_PLAN_SUBMITTED,
			TOPIC_RELEASE_PLAN_DIAGNOSEREQUIREMENTS,
			TOPIC_RELEASE_PLAN_DIAGNOSERELATIONSHIPS,
			TOPIC_RELEASE_PLAN_DIAGNOSEREQUIREMENTSANDRELATIONSHIPS
	};

	void buildFormattedTextOutput (ReleasePlanInfo currentRelPlan, ReleaseInfo currentRelease, String topic ,OutputFormatter ofmt, StringBuffer out) {

		OutputElement listSeparatorFormat = ofmt.getFormat(TOPIC_LIST_ELEMENT_SEPARATOR);
		String emptylistStr = ofmt.getFormat(TOPIC_EMPTY_LIST).getFormat();
		String listSeparator = listSeparatorFormat.getFormat();
		StringBuffer sb= new StringBuffer();

		switch (topic) {

		case TOPIC_RELEASE_CAPACITY_ALL: {
			int capacityAvail = currentRelease.getCapacityAvailable();
			int capacityUsed = currentRelease.getCapacityUsed();
			int capacityBalance = currentRelease.getCapacityAvailable() - currentRelease.getCapacityUsed();
			Object[] capacities = {
					Integer.valueOf(capacityAvail),
					Integer.valueOf(capacityUsed),
					Integer.valueOf(capacityBalance)
			};
			ofmt.appendArgs(capacities, topic, out);
		}
		break;

		case TOPIC_RELEASE_CAPACITY_AVAILABLE: {
			int capacity = currentRelease.getCapacityAvailable();
			ofmt.appendString(Integer.toString(capacity), topic, out);
		}
		break;

		case TOPIC_RELEASE_CAPACITY_BALANCE: {
			int capacity = currentRelease.getCapacityAvailable() - currentRelease.getCapacityUsed();
			ofmt.appendString(Integer.toString(capacity), topic, out);
		}
		break;

		case TOPIC_RELEASE_CAPACITY_USED: {
			int capacity = currentRelease.getCapacityUsed();
			ofmt.appendString(Integer.toString(capacity), topic, out);
		}
		break;

		case TOPIC_DIAGNOSIS_COMBINED: {
			if (currentRelPlan.getAppliedDiagnosis() != null) {
				List <Element4Csp> req_diagnosis = currentRelPlan.getAppliedDiagnosisElements();
				StringBuffer reqDiags = new StringBuffer();
				if (req_diagnosis != null && req_diagnosis.size() >0) {
					for (Diagnosable diagElem : req_diagnosis) {
						reqDiags.append(diagElem.getNameId());
						reqDiags.append(listSeparator);
					}
					reqDiags.setLength(reqDiags.length() - listSeparator.length());
				}
				else 
					reqDiags.append(emptylistStr);

				List <Relationship4Csp> rel_diagnosis = currentRelPlan.getAppliedDiagnosisRelations();
				StringBuffer relDiags = new StringBuffer();
				if (rel_diagnosis != null && rel_diagnosis.size() >0) {
					for (Diagnosable diagElem : rel_diagnosis) {
						relDiags.append(diagElem.getNameId());
						relDiags.append(listSeparator);
					}
					relDiags.setLength(relDiags.length() - listSeparator.length());
				}
				else
					relDiags.append(emptylistStr);

				Object[] diagnoses = new Object[] {
						reqDiags.toString(),
						relDiags.toString()
				};
				ofmt.appendArgs(diagnoses, topic, out);

			}
			else
				ofmt.appendString(null, TOPIC_DIAGNOSIS_NODIAGNOSIS, out);

		}

		break;

		case TOPIC_DIAGNOSIS_RELATIONSHIPS: {
			List <Relationship4Csp> diagnosis = currentRelPlan.getAppliedDiagnosisRelations();
			if (diagnosis != null && diagnosis.size() >0) {
				for (Diagnosable diagElem : diagnosis) {
					sb.append(diagElem.getNameId());
					sb.append(listSeparator);
				}
				sb.setLength(sb.length() - listSeparator.length());
				ofmt.appendString(sb.toString(), topic, out);
			}
			else {
				ofmt.appendString(null, TOPIC_DIAGNOSIS_NODIAGNOSIS, out);
			}
		}
		break;

		case TOPIC_DIAGNOSIS_REQUIREMENTS: {
			List <Element4Csp> diagnosis = currentRelPlan.getAppliedDiagnosisElements();
			if (diagnosis != null && diagnosis.size() >0) {
				for (Diagnosable diagElem : diagnosis) {
					sb.append(diagElem.getNameId());
					sb.append(listSeparator);
				}
				sb.setLength(sb.length() - listSeparator.length());
				ofmt.appendString(sb.toString(), topic, out);
			}
			else {
				ofmt.appendString(null, TOPIC_DIAGNOSIS_NODIAGNOSIS, out);
			}

		}
		break;

		case TOPIC_ELEMENT_SEPARATOR:
		case TOPIC_LIST_ELEMENT_SEPARATOR:
		case TOPIC_DEFAULT:
		case TOPIC_RELEASE_SURROUNDCHAR: {
			ofmt.appendString(null, topic, out);
		}
		break;

		case TOPIC_RELEASE_PLAN_SUBMITTED:
		case TOPIC_RELEASE_PLAN_DIAGNOSEREQUIREMENTS:
		case TOPIC_RELEASE_PLAN_DIAGNOSERELATIONSHIPS:
		case TOPIC_RELEASE_PLAN_DIAGNOSEREQUIREMENTSANDRELATIONSHIPS: {
			ofmt.appendString(null, topic, out);
		}
		break;

		case TOPIC_RELATIONHIPS_EXLUDED: {
			ArrayList <Relationship4Csp> relationships = currentRelPlan.getDisabledRelationsShips();
			if (relationships != null && relationships.size() >0) {
				for (Relationship4Csp rel :relationships) {
					sb.append(rel.getNameId());
					sb.append(listSeparator);
				}
				sb.setLength(sb.length() - listSeparator.length());
			}
			else
				sb.append(emptylistStr);

			ofmt.appendString(sb.toString(), topic, out);
		}
		break;

		case TOPIC_RELATIONSHIPS_BROKEN: {
			ArrayList <Relationship4Csp> relationships = currentRelPlan.getUnsatisfiedRelationships();
			if (relationships != null && relationships.size() >0) {
				for (Relationship4Csp rel :relationships) {
					sb.append(rel.getNameId());
					sb.append(listSeparator);
				}
				sb.setLength(sb.length() - listSeparator.length());
			}
			else
				sb.append(emptylistStr);

			ofmt.appendString(sb.toString(), topic, out);
		}
		break;

		case TOPIC_RELATIONSHIPS_OK: {
			ArrayList <Relationship4Csp> relationships = currentRelPlan.getSatisfiedRelationships();
			if (relationships != null && relationships.size() >0) {
				for (Relationship4Csp rel :relationships) {
					sb.append(rel.getNameId());
					sb.append(listSeparator);
				}
				sb.setLength(sb.length() - listSeparator.length());
			}
			else
				sb.append(emptylistStr);

			ofmt.appendString(sb.toString(), topic, out);
		}
		break;
		
		case TOPIC_RELATIONSHIPS_IGNORED: {
			List <IgnoredRelationship> relationships = currentRelPlan.getIgnoredRelationsShips();
			if (relationships != null && relationships.size() >0) {
				for (IgnoredRelationship rel :relationships) {
					sb.append(rel.getNameId());
					sb.append(listSeparator);
				}
				sb.setLength(sb.length() - listSeparator.length());
			}
			else
				sb.append(emptylistStr);

			ofmt.appendString(sb.toString(), topic, out);
		}
		break;


		case TOPIC_RELEASE_NUMBER: {
			int release = currentRelease.getReleaseNr();
			String surroundChar = ofmt.getFormat(TOPIC_RELEASE_SURROUNDCHAR).getFormat();

			String[] releaseToFormat = {
					Integer.toString(release),
					surroundChar
			};
			ofmt.appendArgs(releaseToFormat, topic, out);
		}
		break;
		
		case TOPIC_RELEASE_ID_STRING: {
			String idString = currentRelease.getIdString();
			ofmt.appendString(idString, topic, out);
		}
		break;

		case TOPIC_RELEASE_PLAN_DURATION_MS: {
			Long duration = currentRelPlan.getDuration_ms();
			ofmt.appendString(duration.toString(), topic, out);
		}
		break;

		case TOPIC_RELEASE_PLAN_CONSISTENT: {
			boolean isConsistent = currentRelPlan.isConsistent();
			if (isConsistent)
				ofmt.appendString(null, topic, out);
			else
				ofmt.appendString(null, TOPIC_RELEASE_PLAN_INCONSISTENT, out);
		}
		break;
		
		case TOPIC_RELEASE_PLAN_HAS_TIMEOUT: {
			boolean isTimeout = currentRelPlan.isTimeout();
			ofmt.appendString(Boolean.toString(isTimeout), topic, out);
		}
		break;

		case TOPIC_RELEASE_PLAN_NAME: {
			switch (currentRelPlan.getIdString()) {
			case SUBMITTED:
				ofmt.appendString(null, TOPIC_RELEASE_PLAN_SUBMITTED, out);
				break;
			case  DIAGNOSE_REQUIREMENTS:
				ofmt.appendString(null, TOPIC_RELEASE_PLAN_DIAGNOSEREQUIREMENTS, out);
				break;
			case DIAGNOSE_RELATIONSHIPS:
				ofmt.appendString(null, TOPIC_RELEASE_PLAN_DIAGNOSERELATIONSHIPS, out);
				break;
			case DIAGNOSE_REQUIREMENTS_AND_RELATIONSHIPS:
				ofmt.appendString(null, TOPIC_RELEASE_PLAN_DIAGNOSEREQUIREMENTSANDRELATIONSHIPS, out);
				break;
			default:
				ofmt.appendString(currentRelPlan.getIdString(), topic, out);
			}
		}
		break;

		case TOPIC_RELEASE_REQUIREMENTS_ASSIGNED: {
			ArrayList <Element4Csp> requirements = currentRelease.getAssignedElements();
			if (requirements != null && requirements.size() >0) {
				for (Element4Csp req :requirements) {
					sb.append(req.getNameId());
					sb.append(listSeparator);
				}
				sb.setLength(sb.length() - listSeparator.length());
			}
			else
				sb.append(emptylistStr);

			ofmt.appendString(sb.toString(), topic, out);
		}
		break;

		case TOPIC_RELEASES_REQUIREMENTS_NOT_ASSIGNED: {

		}
		break;


		default:
		}
	}

	void buildJsonOutput (ReleasePlanInfo currentRelPlan, ReleaseInfo currentRelease, String topic, OutputFormatter ofmt, JsonObject jsonObject) {


		switch (topic) {

		case TOPIC_RELEASE_CAPACITY_ALL: {
			buildJsonOutput (currentRelPlan, currentRelease, TOPIC_RELEASE_CAPACITY_AVAILABLE, ofmt, jsonObject);
			buildJsonOutput (currentRelPlan, currentRelease, TOPIC_RELEASE_CAPACITY_USED, ofmt, jsonObject);
			buildJsonOutput (currentRelPlan, currentRelease, TOPIC_RELEASE_CAPACITY_BALANCE, ofmt, jsonObject);
		}
		break;

		case TOPIC_RELEASE_CAPACITY_AVAILABLE: {
			int capacity = currentRelease.getCapacityAvailable();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(capacity));
		}
		break;

		case TOPIC_RELEASE_CAPACITY_BALANCE: {
			int capacity = currentRelease.getCapacityAvailable() - currentRelease.getCapacityUsed();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(capacity));
		}
		break;

		case TOPIC_RELEASE_CAPACITY_USED: {
			int capacity = currentRelease.getCapacityUsed();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(capacity));
		}
		break;

		case TOPIC_DIAGNOSIS_COMBINED: {
			JsonObject dianosisJson = new JsonObject();
			if (currentRelPlan.getAppliedDiagnosis() != null) {
				buildJsonOutput (currentRelPlan, currentRelease, TOPIC_DIAGNOSIS_REQUIREMENTS, ofmt, dianosisJson);
				buildJsonOutput (currentRelPlan, currentRelease, TOPIC_DIAGNOSIS_RELATIONSHIPS, ofmt, dianosisJson);
			}

			jsonObject.add(
					ofmt.getDataKey(topic),
					dianosisJson);
		}

		break;

		case TOPIC_DIAGNOSIS_RELATIONSHIPS: {
			List <Relationship4Csp> diagnosis = currentRelPlan.getAppliedDiagnosisRelations();
			JsonArray diagnosisArray = new JsonArray();
			if (diagnosis != null) {
				JsonObject relationshipJson = new JsonObject();
				for (Relationship4Csp relationship :diagnosis) {
					buildRelationShipJson(relationship, ofmt, relationshipJson);
					diagnosisArray.add(relationshipJson);
				}
			}
			jsonObject.add(
					ofmt.getDataKey(topic),
					diagnosisArray);
		}
		break;

		case TOPIC_DIAGNOSIS_REQUIREMENTS: {
			List <Element4Csp> diagnosis = currentRelPlan.getAppliedDiagnosisElements();
			JsonArray diagnosisArray = new JsonArray();
			if (diagnosis != null) {
				for (Diagnosable diagnosed :diagnosis) {
					diagnosisArray.add(diagnosed.getNameId());
				}
			}
			jsonObject.add(
					ofmt.getDataKey(topic),
					diagnosisArray);

		}
		break;

		case TOPIC_RELATIONHIPS_EXLUDED: {
			ArrayList <Relationship4Csp> relationships = currentRelPlan.getDisabledRelationsShips();
			JsonArray relArray = new JsonArray();
			if (relationships != null) {
				for (Relationship4Csp rel :relationships) {
					JsonObject relationshipJson = new JsonObject();
					buildRelationShipJson(rel, ofmt, relationshipJson);
					relArray.add(relationshipJson);
				}
			}
			jsonObject.add(
					ofmt.getDataKey(topic),
					relArray);
		}
		break;

		case TOPIC_RELATIONSHIPS_BROKEN: {
			ArrayList <Relationship4Csp> relationships = currentRelPlan.getUnsatisfiedRelationships();
			JsonArray relArray = new JsonArray();
			if (relationships != null) {
				for (Relationship4Csp rel :relationships) {
					JsonObject relationshipJson = new JsonObject();
					buildRelationShipJson(rel, ofmt, relationshipJson);
					relArray.add(relationshipJson);
				}
			}
			jsonObject.add(
					ofmt.getDataKey(topic),
					relArray);
		}
		break;

		case TOPIC_RELATIONSHIPS_OK: {
			ArrayList <Relationship4Csp> relationships = currentRelPlan.getSatisfiedRelationships();
			JsonArray relArray = new JsonArray();
			if (relationships != null) {
				for (Relationship4Csp rel :relationships) {
					JsonObject relationshipJson = new JsonObject();
					buildRelationShipJson(rel, ofmt, relationshipJson);
					relArray.add(relationshipJson);
				}
			}
			jsonObject.add(
					ofmt.getDataKey(topic),
					relArray);
		}
		break;
		
		case TOPIC_RELATIONSHIPS_IGNORED: {
			List <IgnoredRelationship> relationships = currentRelPlan.getIgnoredRelationsShips();
			JsonArray relArray = new JsonArray();
			if (relationships != null) {
				for (IgnoredRelationship rel :relationships) {
					JsonObject relationshipJson = new JsonObject();
					buildIgnoredRelationShipJson(rel, ofmt, relationshipJson);
					relArray.add(relationshipJson);
				}
			}
			jsonObject.add(
					ofmt.getDataKey(topic),
					relArray);
		}
		break;
		
		case TOPIC_RELEASE_NUMBER: {
			int release = currentRelease.getReleaseNr();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(release));

		}
		break;
		
		case TOPIC_RELEASE_ID_STRING: {
			String idString = currentRelease.getIdString();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(idString));
		}
		break;


		case TOPIC_RELEASE_PLAN_CONSISTENT: {
			Boolean isConsistent = currentRelPlan.isConsistent();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(isConsistent));
		}
		break;
		
		case TOPIC_RELEASE_PLAN_HAS_TIMEOUT: {
			Boolean isTimeout = currentRelPlan.isTimeout();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(isTimeout));
		}
		break;

		case TOPIC_RELEASE_PLAN_NAME: {
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(currentRelPlan.getIdString()));
		}
		break;
		
		case TOPIC_RELEASE_PLAN_DURATION_MS: {
			Long duration = currentRelPlan.getDuration_ms();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(duration));
		}
		break;
		
		case TOPIC_RELEASE_REQUIREMENTS_ASSIGNED: {
			ArrayList <Element4Csp> requirements = currentRelease.getAssignedElements();
			JsonArray relArray = new JsonArray();
			if (requirements != null) {
				for (Element4Csp elem: requirements) {
					relArray.add(elem.getNameId());
				}
			}
			jsonObject.add(
					ofmt.getDataKey(topic),
					relArray);
		}
		break;

		case TOPIC_RELEASES_REQUIREMENTS_NOT_ASSIGNED: {

		}
		break;


		default:
		}
	}

	void buildRelationShipJson(Relationship4Csp relationship, OutputFormatter ofmt, JsonObject jsonObject) {
		String fromKey= ofmt.getFormat(TOPIC_RELATIONSHIP_FROM).getDataKey();
		String  toKey = ofmt.getFormat(TOPIC_RELATIONSHIP_TO).getDataKey();
		String  relKey = ofmt.getFormat(TOPIC_RELATIONSHIP_TYPE).getDataKey();
		jsonObject.addProperty(fromKey, relationship.getFrom().getNameId());
		jsonObject.addProperty(toKey, relationship.getTo().getNameId());
		jsonObject.addProperty(relKey, relationship.getRelationShipName());
	}
	
	void buildIgnoredRelationShipJson(IgnoredRelationship relationship, OutputFormatter ofmt, JsonObject jsonObject) {
		String fromKey= ofmt.getFormat(TOPIC_RELATIONSHIP_FROM).getDataKey();
		String  toKey = ofmt.getFormat(TOPIC_RELATIONSHIP_TO).getDataKey();
		String  relKey = ofmt.getFormat(TOPIC_RELATIONSHIP_TYPE).getDataKey();
		jsonObject.addProperty(fromKey, relationship.getFrom().getNameId());
		jsonObject.addProperty(toKey, relationship.getTo().getNameId());
		jsonObject.addProperty(relKey, relationship.getRelationShipName());
	}


	void buildJsonCombinedOutput (ReleasePlanInfo currentRelPlan, ReleaseInfo currentRelease, String topic,OutputFormatter ofmt, JsonObject jsonObject) {
		buildJsonOutput (currentRelPlan, currentRelease, topic, ofmt, jsonObject);
		StringBuffer sb= new StringBuffer();
		buildFormattedTextOutput(currentRelPlan, currentRelease, topic, ofmt, sb);
		String msgDataName = ofmt.getFormat(topic).getMessageKey();
		jsonObject.addProperty(msgDataName, sb.toString());
	}

	protected static OutputFormatter intitializeOutputFormats () {
		Locale locale = Locale.getDefault();
		OutputFormatter ofmt = new OutputFormatter(locale);
		ResourceBundle bundle = ResourceBundle.getBundle("eu.openreq.keljucaas.services.ReleasePlanDiagnosisOutput", locale);

		for (String topic: availableTopics) {
			String textFormat ="";
			String jsonName = "";
			try {
				textFormat =  (String) bundle.getObject(topic+".text.format");				
			}
			catch (MissingResourceException ex) {

			}
			try {
				jsonName=(String) bundle.getObject(topic+".json.name");
			}
			catch (MissingResourceException ex) {

			}

			OutputElement topicElement = new OutputElement(textFormat, jsonName);
			ofmt.setFormat(topic, topicElement);
		}

		return ofmt;
	}

}
