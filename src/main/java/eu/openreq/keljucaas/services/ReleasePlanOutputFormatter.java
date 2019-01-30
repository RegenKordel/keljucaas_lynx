package eu.openreq.keljucaas.services;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import eu.openreq.keljucaas.domain.release.Diagnosable;
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
	public static final String topic_element_separator = "element.separator";
	public static final String topic_list_element_separator = "list.element.separator";
	public static final String topic_relationhips_exluded = "relationhips.exluded";
	public static final String topic_relationships_broken = "relationships.broken";
	public static final String topic_relationships_ok = "relationships.ok";
	public static final String topic_release_number = "release.number";
	public static final String topic_release_plan_consistent = "release.plan.consistent";
	public static final String topic_release_plan_name = "release.plan.name";
	public static final String topic_release_requirements_assigned = "release.requirements.assigned";
	public static final String topic_releases_requirements_not_assigned = "releases.requirements.not.assigned";
	public static final String topic_diagnosis_nodiagnosis = "diagnosis.nodiagnosis";

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
			topic_release_plan_name,
			topic_release_requirements_assigned,
			topic_releases_requirements_not_assigned	};

	void buildFormattedTextOutput (ReleasePlanInfo currentRelPlan, ReleaseInfo currentRelease , String topic ,OutputFormatter ofmt, StringBuffer out) {

		OutputElement listSeparatorFormat = ofmt.getFormat("list.element.separator");
		String listSeparator = listSeparatorFormat.getFormat();
		StringBuilder sb= new StringBuilder();

		switch (topic) {

		case topic_capacity_all: {

		}
		case topic_capacity_available: {

		}
		break;

		case topic_capacity_balance: {

		}
		break;

		case topic_capacity_used: {

		}
		break;

		case topic_default: {

		}
		break;

		case topic_diagnosis_combined: {
			List <Diagnosable> diagnosis = currentRelPlan.getAppliedDiagnosis();
			//MessageFormat fmt = ofmt.getTextMessageFormatter(topic);
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

		}
		break;

		case topic_diagnosis_requirements: {

		}
		break;

		case topic_element_separator: {

		}
		break;

		case topic_list_element_separator: {

		}
		break;

		case topic_relationhips_exluded: {

		}
		break;

		case topic_relationships_broken: {

		}
		break;

		case topic_relationships_ok: {

		}
		break;

		case topic_release_number: {

		}
		break;

		case topic_release_plan_consistent: {

		}
		break;

		case topic_release_plan_name: {

		}
		break;

		case topic_release_requirements_assigned: {

		}
		break;

		case topic_releases_requirements_not_assigned: {

		}
		break;

		case topic_diagnosis_nodiagnosis: {

		}
		break;



		default:
		}
	}
	
	void buildJsonOutput (ReleasePlanInfo currentRelPlan, ReleaseInfo currentRelease , String topic ,OutputFormatter ofmt, StringBuffer out) {

		OutputElement listSeparatorFormat = ofmt.getFormat("list.element.separator");
		String listSeparator = listSeparatorFormat.getFormat();
		StringBuilder sb= new StringBuilder();

		switch (topic) {

		case topic_capacity_all: {

		}
		case topic_capacity_available: {

		}
		break;

		case topic_capacity_balance: {

		}
		break;

		case topic_capacity_used: {

		}
		break;

		case topic_default: {

		}
		break;

		case topic_diagnosis_combined: {
			List <Diagnosable> diagnosis = currentRelPlan.getAppliedDiagnosis();
			//MessageFormat fmt = ofmt.getTextMessageFormatter(topic);
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

		}
		break;

		case topic_diagnosis_requirements: {

		}
		break;

		case topic_element_separator: {

		}
		break;

		case topic_list_element_separator: {

		}
		break;

		case topic_relationhips_exluded: {

		}
		break;

		case topic_relationships_broken: {

		}
		break;

		case topic_relationships_ok: {

		}
		break;

		case topic_release_number: {

		}
		break;

		case topic_release_plan_consistent: {

		}
		break;

		case topic_release_plan_name: {

		}
		break;

		case topic_release_requirements_assigned: {

		}
		break;

		case topic_releases_requirements_not_assigned: {

		}
		break;

		case topic_diagnosis_nodiagnosis: {

		}
		break;



		default:
		}
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
