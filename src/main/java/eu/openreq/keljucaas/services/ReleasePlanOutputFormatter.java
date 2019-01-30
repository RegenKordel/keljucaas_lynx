package eu.openreq.keljucaas.services;

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
import eu.openreq.keljucaas.domain.release.Relationship4Csp;
import eu.openreq.keljucaas.domain.release.ReleaseInfo;
import eu.openreq.keljucaas.domain.release.ReleasePlanInfo;
import eu.openreq.keljucaas.services.OutputFormatter.OutputElement;

public class ReleasePlanOutputFormatter {
	public static final String topic_capacity_all = "capacity.all";
	public static final String topic_capacity_available = "capacity.available";
	public static final String topic_capacity_balance = "capacity.balance";
	public static final String topic_capacity_used = "capacity.used";
	public static final String topic_default = "default";
	public static final String topic_diagnosis_combined = "diagnosis.combined";
	public static final String topic_diagnosis_relationships = "diagnosis.relationships";
	public static final String topic_diagnosis_requirements = "diagnosis.requirements";
	public static final String topic_diagnosis_nodiagnosis = "diagnosis.nodiagnosis";
	public static final String topic_element_separator = "element.separator";
	public static final String topic_list_element_separator = "list.element.separator";
	public static final String topic_relationhips_exluded = "relationhips.exluded";
	public static final String topic_relationships_broken = "relationships.broken";
	public static final String topic_relationships_ok = "relationships.ok";
	public static final String topic_release_number = "release.number";
	public static final String topic_release_plan_consistent = "release.plan.consistent";
	public static final String topic_release_plan_inconsistent = "release.plan.inconsistent";
	public static final String topic_release_plan_name = "release.plan.name";
	public static final String topic_release_requirements_assigned = "release.requirements.assigned";
	public static final String topic_releases_requirements_not_assigned = "releases.requirements.not.assigned";

	public static String availableTopics[] = {
			topic_capacity_all,
			topic_capacity_available,
			topic_capacity_balance,
			topic_capacity_used,
			topic_default,
			topic_diagnosis_combined,
			topic_diagnosis_relationships,
			topic_diagnosis_requirements,
			topic_diagnosis_nodiagnosis,
			topic_element_separator,
			topic_list_element_separator,
			topic_relationhips_exluded,
			topic_relationships_broken,
			topic_relationships_ok,
			topic_release_number,
			topic_release_plan_consistent,
			topic_release_plan_inconsistent,
			topic_release_plan_name,
			topic_release_requirements_assigned,
			topic_releases_requirements_not_assigned};

	void buildFormattedTextOutput (ReleasePlanInfo currentRelPlan, ReleaseInfo currentRelease , String topic ,OutputFormatter ofmt, StringBuffer out) {

		OutputElement listSeparatorFormat = ofmt.getFormat("list.element.separator");
		String listSeparator = listSeparatorFormat.getFormat();
		StringBuffer sb= new StringBuffer();

		switch (topic) {

		case topic_capacity_all: {

		}
		break;
		
		case topic_capacity_available: {
			int capacity = currentRelease.getCapacityAvailable();
			ofmt.appendString(Integer.toString(capacity), topic, out);
		}
		break;

		case topic_capacity_balance: {
			int capacity = currentRelease.getCapacityAvailable() - currentRelease.getCapacityUsed();
			ofmt.appendString(Integer.toString(capacity), topic, out);
		}
		break;

		case topic_capacity_used: {
			int capacity = currentRelease.getCapacityUsed();
			ofmt.appendString(Integer.toString(capacity), topic, out);
		}
		break;

		case topic_diagnosis_combined: {
			List <Diagnosable> diagnosis = currentRelPlan.getAppliedDiagnosis();
			if (diagnosis != null && diagnosis.size() >0) {
				for (Diagnosable diagElem : diagnosis) {
					sb.append(diagElem.getNameId());
					sb.append(listSeparator);
				}
				sb.setLength(sb.length() - listSeparator.length());
				ofmt.appendString(sb.toString(), topic, out);
			}
			else {
				ofmt.appendString(null, "diagnosis.nodiagnosis", out);
			}
		}

		break;

		case topic_diagnosis_relationships: {
			List <Diagnosable> diagnosis = currentRelPlan.getAppliedDiagnosisRelations();
			if (diagnosis != null && diagnosis.size() >0) {
				for (Diagnosable diagElem : diagnosis) {
					sb.append(diagElem.getNameId());
					sb.append(listSeparator);
				}
				sb.setLength(sb.length() - listSeparator.length());
				ofmt.appendString(sb.toString(), topic, out);
			}
			else {
				ofmt.appendString(null, "diagnosis.nodiagnosis", out);
			}
		}
		break;

		case topic_diagnosis_requirements: {
			List <Diagnosable> diagnosis = currentRelPlan.getAppliedDiagnosisElements();
			if (diagnosis != null && diagnosis.size() >0) {
				for (Diagnosable diagElem : diagnosis) {
					sb.append(diagElem.getNameId());
					sb.append(listSeparator);
				}
				sb.setLength(sb.length() - listSeparator.length());
				ofmt.appendString(sb.toString(), topic, out);
			}
			else {
				ofmt.appendString(null, "diagnosis.nodiagnosis", out);
			}

		}
		break;

		case topic_element_separator:
		case topic_list_element_separator:
		case topic_default: {
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
			ofmt.appendString(sb.toString(), topic, out);
		}
		break;

		case topic_release_number: {
			int release = currentRelease.getReleaseNr();
			ofmt.appendString(Integer.toString(release), topic, out);
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
			ofmt.appendString(currentRelPlan.getIdString(), topic, out);
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
			ofmt.appendString(sb.toString(), topic, out);
		}
		break;

		case topic_releases_requirements_not_assigned: {

		}
		break;


		default:
		}
	}

	void buildJsonOutput (ReleasePlanInfo currentRelPlan, ReleaseInfo currentRelease, String topic ,OutputFormatter ofmt, JsonObject jsonObject) {


		switch (topic) {

		case topic_capacity_available: {
			int capacity = currentRelease.getCapacityAvailable();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(capacity));
		}
		break;

		case topic_capacity_balance: {
			int capacity = currentRelease.getCapacityAvailable() - currentRelease.getCapacityUsed();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(capacity));
		}
		break;

		case topic_capacity_used: {
			int capacity = currentRelease.getCapacityUsed();
			jsonObject.add(
					ofmt.getDataKey(topic),
					new JsonPrimitive(capacity));
		}
		break;

		case topic_diagnosis_combined: {
			List <Diagnosable> diagnosis = currentRelPlan.getAppliedDiagnosis();
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

		case topic_diagnosis_relationships: {
			List <Diagnosable> diagnosis = currentRelPlan.getAppliedDiagnosisRelations();
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

		case topic_diagnosis_requirements: {
			List <Diagnosable> diagnosis = currentRelPlan.getAppliedDiagnosisElements();
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
					relArray.add(rel.getNameId());
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
					relArray.add(rel.getNameId());
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
					relArray.add(rel.getNameId());
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

			System.out.println(topicElement);
		}

		return ofmt;
	}

}
