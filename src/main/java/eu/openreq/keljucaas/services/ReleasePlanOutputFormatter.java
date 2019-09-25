package eu.openreq.keljucaas.services;

import static  eu.openreq.keljucaas.services.ConsistencyCheckService.diagnoseRelationships;
import static  eu.openreq.keljucaas.services.ConsistencyCheckService.diagnoseRequirements;
import static eu.openreq.keljucaas.services.ConsistencyCheckService.diagnoseRequirementsAndRelationships;
import static eu.openreq.keljucaas.services.ConsistencyCheckService.submitted;

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
	public static final String topic_default = "default";
	public static final String topic_empty_list = "empty.list";

	public static final String topic_relationship_from = "relationship.from";
	public static final String topic_relationship_to = "relationship.to";
	public static final String topic_relationship_type = "relationship.type";
	public static final String topic_relationhip = "relationhip";



	public static final String topic_diagnosis_combined = "diagnosis.combined";
	public static final String topic_diagnosis_relationships = "diagnosis.relationships";
	public static final String topic_diagnosis_requirements = "diagnosis.requirements";
	public static final String topic_diagnosis_nodiagnosis = "diagnosis.nodiagnosis";
	public static final String topic_element_separator = "element.separator";
	public static final String topic_list_element_separator = "list.element.separator";
	public static final String topic_relationhips_exluded = "relationhips.exluded";
	public static final String topic_relationships_broken = "relationships.broken";
	public static final String topic_relationships_ignored = "relationships.ignored";
	public static final String topic_relationships_ok = "relationships.ok";
	public static final String topic_releases_element = "releases.element";
	public static final String topic_release_capacity_all = "release.capacity.all";
	public static final String topic_release_capacity_available = "release.capacity.available";
	public static final String topic_release_capacity_balance = "release.capacity.balance";
	public static final String topic_release_capacity_used = "release.capacity.used";

	public static final String topic_release_number = "release.number";
	public static final String topic_release_id_string = "release.id_string";
	public static final String topic_release_surroundchar ="release.surroundchar";
	public static final String topic_release_plan_consistent = "release.plan.consistent";
	public static final String topic_release_plan_inconsistent = "release.plan.inconsistent";
	public static final String topic_release_plan_name = "release.plan.name";
	public static final String topic_release_requirements_assigned = "release.requirements.assigned";
	public static final String topic_releases_requirements_not_assigned = "releases.requirements.not.assigned";

	public static final String topic_release_plan_submitted = "release.plan.submitted";
	public static final String topic_release_plan_diagnoseRequirements = "release.plan.diagnoseRequirements";
	public static final String topic_release_plan_diagnoseRelationships ="release.plan.diagnoseRelationships";
	public static final String topic_release_plan_diagnoseRequirementsAndRelationships = "release.plan.diagnoseRequirementsAndRelationships";

	private static final String[] availableTopics = {
			topic_default,
			topic_empty_list,
			topic_relationship_from,
			topic_relationship_to,
			topic_relationship_type,
			topic_relationhip,
			topic_diagnosis_combined,
			topic_diagnosis_relationships,
			topic_diagnosis_requirements,
			topic_diagnosis_nodiagnosis,
			topic_element_separator,
			topic_list_element_separator,
			topic_relationhips_exluded,
			topic_relationships_broken,
			topic_relationships_ignored,
			topic_relationships_ok,
			topic_releases_element,
			topic_release_capacity_all,
			topic_release_capacity_available,
			topic_release_capacity_balance,
			topic_release_capacity_used,
			topic_release_number,
			topic_release_id_string,
			topic_release_surroundchar,
			topic_release_plan_consistent,
			topic_release_plan_inconsistent,
			topic_release_plan_name,
			topic_release_requirements_assigned,
			topic_releases_requirements_not_assigned,
			topic_release_plan_submitted,
			topic_release_plan_diagnoseRequirements,
			topic_release_plan_diagnoseRelationships,
			topic_release_plan_diagnoseRequirementsAndRelationships
	};

	void buildFormattedTextOutput (ReleasePlanInfo currentRelPlan, ReleaseInfo currentRelease, String topic ,OutputFormatter ofmt, StringBuffer out) {

		OutputElement listSeparatorFormat = ofmt.getFormat(topic_list_element_separator);
		String emptylistStr = ofmt.getFormat(topic_empty_list).getFormat();
		String listSeparator = listSeparatorFormat.getFormat();
		StringBuffer sb= new StringBuffer();

		switch (topic) {

		case topic_release_capacity_all: {
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

		case topic_release_capacity_available: {
			int capacity = currentRelease.getCapacityAvailable();
			ofmt.appendString(Integer.toString(capacity), topic, out);
		}
		break;

		case topic_release_capacity_balance: {
			int capacity = currentRelease.getCapacityAvailable() - currentRelease.getCapacityUsed();
			ofmt.appendString(Integer.toString(capacity), topic, out);
		}
		break;

		case topic_release_capacity_used: {
			int capacity = currentRelease.getCapacityUsed();
			ofmt.appendString(Integer.toString(capacity), topic, out);
		}
		break;

		case topic_diagnosis_combined: {
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
				ofmt.appendString(null, topic_diagnosis_nodiagnosis, out);

		}

		break;

		case topic_diagnosis_relationships: {
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
				ofmt.appendString(null, topic_diagnosis_nodiagnosis, out);
			}
		}
		break;

		case topic_diagnosis_requirements: {
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
				ofmt.appendString(null, topic_diagnosis_nodiagnosis, out);
			}

		}
		break;

		case topic_element_separator:
		case topic_list_element_separator:
		case topic_default:
		case topic_release_surroundchar: {
			ofmt.appendString(null, topic, out);
		}

		case topic_release_plan_submitted:
		case topic_release_plan_diagnoseRequirements:
		case topic_release_plan_diagnoseRelationships:
		case topic_release_plan_diagnoseRequirementsAndRelationships: {
			ofmt.appendString(null, topic, out);
		}

		case topic_relationhips_exluded: {
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

		case topic_relationships_broken: {
			ArrayList <Relationship4Csp> relationships = currentRelPlan.getUnsatiedRelationsShips();
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

		case topic_relationships_ok: {
			ArrayList <Relationship4Csp> relationships = currentRelPlan.getEnabledRelationsShips();
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
		
		case topic_relationships_ignored: {
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


		case topic_release_number: {
			int release = currentRelease.getReleaseNr();
			String surroundChar = ofmt.getFormat(topic_release_surroundchar).getFormat();

			String[] releaseToFormat = {
					Integer.toString(release),
					surroundChar
			};
			ofmt.appendArgs(releaseToFormat, topic, out);
		}
		break;
		
		case topic_release_id_string: {
			String idString = currentRelease.getIdString();
			ofmt.appendString(idString, topic, out);
		}
		break;



		case topic_release_plan_consistent: {
			boolean isConsistent = currentRelPlan.isConsistent();
			if (isConsistent)
				ofmt.appendString(null, topic, out);
			else
				ofmt.appendString(null, topic_release_plan_inconsistent, out);
		}
		break;

		case topic_release_plan_name: {
			switch (currentRelPlan.getIdString()) {
			case submitted:
				ofmt.appendString(null, topic_release_plan_submitted, out);
				break;
			case  diagnoseRequirements:
				ofmt.appendString(null, topic_release_plan_diagnoseRequirements, out);
				break;
			case diagnoseRelationships:
				ofmt.appendString(null, topic_release_plan_diagnoseRelationships, out);
				break;
			case diagnoseRequirementsAndRelationships:
				ofmt.appendString(null, topic_release_plan_diagnoseRequirementsAndRelationships, out);
				break;
			default:
				ofmt.appendString(currentRelPlan.getIdString(), topic, out);
			}
		}
		break;

		case topic_release_requirements_assigned: {
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

		case topic_releases_requirements_not_assigned: {

		}
		break;


		default:
		}
	}

	void buildJsonOutput (ReleasePlanInfo currentRelPlan, ReleaseInfo currentRelease, String topic, OutputFormatter ofmt, JsonObject jsonObject) {


		switch (topic) {

		case topic_release_capacity_all: {
			buildJsonOutput (currentRelPlan, currentRelease, topic_release_capacity_available, ofmt, jsonObject);
			buildJsonOutput (currentRelPlan, currentRelease, topic_release_capacity_used, ofmt, jsonObject);
			buildJsonOutput (currentRelPlan, currentRelease, topic_release_capacity_balance, ofmt, jsonObject);
		}
		break;

		case topic_release_capacity_available: {
			int capacity = currentRelease.getCapacityAvailable();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(capacity));
		}
		break;

		case topic_release_capacity_balance: {
			int capacity = currentRelease.getCapacityAvailable() - currentRelease.getCapacityUsed();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(capacity));
		}
		break;

		case topic_release_capacity_used: {
			int capacity = currentRelease.getCapacityUsed();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(capacity));
		}
		break;

		case topic_diagnosis_combined: {
			JsonObject dianosisJson = new JsonObject();
			if (currentRelPlan.getAppliedDiagnosis() != null) {
				buildJsonOutput (currentRelPlan, currentRelease, topic_diagnosis_requirements, ofmt, dianosisJson);
				buildJsonOutput (currentRelPlan, currentRelease, topic_diagnosis_relationships, ofmt, dianosisJson);
			}

			jsonObject.add(
					ofmt.getDataKey(topic),
					dianosisJson);
		}

		break;

		case topic_diagnosis_relationships: {
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

		case topic_diagnosis_requirements: {
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

		case topic_relationhips_exluded: {
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

		case topic_relationships_broken: {
			ArrayList <Relationship4Csp> relationships = currentRelPlan.getUnsatiedRelationsShips();
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

		case topic_relationships_ok: {
			ArrayList <Relationship4Csp> relationships = currentRelPlan.getEnabledRelationsShips();
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
		
		case topic_relationships_ignored: {
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
		
		case topic_release_number: {
			int release = currentRelease.getReleaseNr();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(release));

		}
		break;
		
		case topic_release_id_string: {
			String idString = currentRelease.getIdString();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(idString));
		}
		break;


		case topic_release_plan_consistent: {
			Boolean isConsistent = currentRelPlan.isConsistent();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(isConsistent));
		}
		break;

		case topic_release_plan_name: {
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(currentRelPlan.getIdString()));
		}
		break;

		case topic_release_requirements_assigned: {
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

		case topic_releases_requirements_not_assigned: {

		}
		break;


		default:
		}
	}

	void buildRelationShipJson(Relationship4Csp relationship, OutputFormatter ofmt, JsonObject jsonObject) {
		String fromKey= ofmt.getFormat(topic_relationship_from).getDataKey();
		String  toKey = ofmt.getFormat(topic_relationship_to).getDataKey();
		String  relKey = ofmt.getFormat(topic_relationship_type).getDataKey();
		jsonObject.addProperty(fromKey, relationship.getFrom().getNameId());
		jsonObject.addProperty(toKey, relationship.getTo().getNameId());
		jsonObject.addProperty(relKey, relationship.getRelationShipName());
	}
	
	void buildIgnoredRelationShipJson(IgnoredRelationship relationship, OutputFormatter ofmt, JsonObject jsonObject) {
		String fromKey= ofmt.getFormat(topic_relationship_from).getDataKey();
		String  toKey = ofmt.getFormat(topic_relationship_to).getDataKey();
		String  relKey = ofmt.getFormat(topic_relationship_type).getDataKey();
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
