package eu.openreq.keljucaas.domain.release;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.openreq.keljucaas.services.CSPPlanner.ReleasePlanAnalysisDefinition;
import eu.openreq.keljucaas.services.ConsistencyCheckService;

public class ReleasePlanInfo {
	private ArrayList<ReleaseInfo> releases = new ArrayList<>();
	private ArrayList <Relationship4Csp> enabledRelationsShips = new ArrayList<>();
	private ArrayList <Relationship4Csp> disabledRelationsShips = new ArrayList<>();
	private Map<Integer, ReleaseInfo> releaseOfElement = new LinkedHashMap<>();
	private List<Diagnosable> appliedDiagnosis; 
	private final String idString;
	private final ReleasePlanAnalysisDefinition wantedAnalysis;
	//Consitency status to report. It is NOT set by the state automatically! 
	private boolean consistent;


	public ReleasePlanInfo(String idString, ReleasePlanAnalysisDefinition wantedAnalysis) {
		super();
		this.idString = idString;
		this.wantedAnalysis = wantedAnalysis;
	}

	public void addReleaseInfo(ReleaseInfo releaseInfo) {
		releases.add(releaseInfo);
	}

	public ReleaseInfo getReleaseInfo(int index) {
		return releases.get(index);
	}

	public void addEnabledRelationsShip(Relationship4Csp relationship4Csp) {
		enabledRelationsShips.add(relationship4Csp);	
	}

	public void addDisabledRelationsShip(Relationship4Csp relationship4Csp) {
		disabledRelationsShips.add(relationship4Csp);	
	}

	public void assignElementToRelease(Element4Csp element, ReleaseInfo releaseInfo)
	{
		releaseInfo.addAssignedElement(element);
		releaseOfElement.put(element.getId(), releaseInfo);
	}

	public ReleaseInfo getReleaseInfo(Element4Csp element) {
		return releaseOfElement.get(element.getId());
	}

	public ArrayList<Relationship4Csp> getEnabledRelationsShips() {
		return enabledRelationsShips;
	}

	public ArrayList<Relationship4Csp> getDisabledRelationsShips() {
		return disabledRelationsShips;
	}

	public String getIdString() {
		return idString;
	}
	
	public ReleasePlanAnalysisDefinition getWantedAnalysis() {
		return wantedAnalysis;
	}

	public boolean isConsistent() {
		return consistent;
	}

	public void setConsistent(boolean consistent) {
		this.consistent = consistent;
	}
	
	//may return null;
	public List<Diagnosable> getAppliedDiagnosis() {
		return appliedDiagnosis;
	}
	
	public List<Diagnosable> getAppliedDiagnosisElements() {
		if (appliedDiagnosis == null)
			return null;
		List<Diagnosable> retVal = new LinkedList<>();
		for (Diagnosable diagnosed:appliedDiagnosis )
			if (diagnosed instanceof Element4Csp)
				retVal.add(diagnosed);
		
		return retVal;
	}
	
	public List<Diagnosable> getAppliedDiagnosisRelations() {
		if (appliedDiagnosis == null)
			return null;
		List<Diagnosable> retVal = new LinkedList<>();
		for (Diagnosable diagnosed:appliedDiagnosis )
			if (diagnosed instanceof Relationship4Csp)
				retVal.add(diagnosed);
		
		return retVal;
	}



	public void setAppliedDiagnosis(List<Diagnosable> appliedDiagnosis) {
		this.appliedDiagnosis = appliedDiagnosis;
	}

	public boolean determineConsistency() {
		consistent = true;
		if (!getUnsatiedRelationsShips().isEmpty()) {
			consistent = false;
			return consistent;
		}
		
		for (ReleaseInfo release: releases) {
			if (!release.isConsistent()) {
				consistent = false;
				return consistent;
			}
		}
		return consistent;
	}

	public ArrayList<Relationship4Csp> getUnsatiedRelationsShips() {
		ArrayList<Relationship4Csp> failedRelationsips = new ArrayList<>();
		for (Relationship4Csp rel: enabledRelationsShips) {
			Element4Csp from = rel.getFrom();
			int fromRelease = getReleaseInfo(from).getReleaseNr();
			Element4Csp to = rel.getTo();
			int toRelease = getReleaseInfo(to).getReleaseNr();
			if (!rel.isSatisfiedWithAssignment(fromRelease, toRelease))
				failedRelationsips.add(rel);
		}
		return failedRelationsips;
	}

	//TODO modify to use getUnsatiedRelationsShips()
	public String getFailedRelationShipsMsg () {
		StringBuilder sb = new StringBuilder();
		boolean added = false;
		for (Relationship4Csp rel: getUnsatiedRelationsShips()) {
			sb.append(rel.getNameId());
			sb.append(ConsistencyCheckService.fieldSeparator);
			added = true;
		}
		if (added)
			sb.setLength(sb.length() - ConsistencyCheckService.fieldSeparator.length());
		return sb.toString();
	}

	public String getDisabledRelationShipsMsg () {
		StringBuilder sb = new StringBuilder();
		boolean added = false;
		for (Relationship4Csp rel: getDisabledRelationsShips()) {
			sb.append(rel.getNameId());
			sb.append(ConsistencyCheckService.fieldSeparator);
			added = true;
		}
		if (added)
			sb.setLength(sb.length() - ConsistencyCheckService.fieldSeparator.length());
		return sb.toString();
	}

	public String getDiagnosis() {
		if (appliedDiagnosis == null)
			return "";
		StringBuffer sb = new StringBuffer(); 
		if (appliedDiagnosis.isEmpty() ) {
			sb.append("(No Diagnosis found.)");
		} 
		else {
			for (int i = 0; i < appliedDiagnosis.size(); i++) {
				Diagnosable reqB = appliedDiagnosis.get(i);
				String reqId = reqB.getNameId();
				sb.append(reqId);
				if (appliedDiagnosis.size() > 1 && i < appliedDiagnosis.size() - 1) {
					sb.append(ConsistencyCheckService.fieldSeparator);
				}
			}
		}
		return sb.toString();
	}



//	public String getDiagnosis() {
//		if (appliedDiagnosis == null)
//			return "";
//		StringBuffer sb = new StringBuffer(); 
//		if (appliedDiagnosis.isEmpty()) {
//			sb.append("(No Diagnosis found.)");
//		} 
//		else {
//			for (int i = 0; i < appliedDiagnosis.size(); i++) {
//				Diagnosable reqB = appliedDiagnosis.get(i);
//				if (!(reqB instanceof Element4Csp))
//					continue;
//				Element4Csp elem = (Element4Csp) reqB;
//				String reqId = elem.getNameId();
//				sb.append(reqId);
//				if (appliedDiagnosis.size() > 1 && i < appliedDiagnosis.size() - 1) {
//					sb.append(ConsistencyCheckService.fieldSeparator);
//				}
//			}
//		}
//		return sb.toString();
//	}

	public void  appendReleasePlanMessage(StringBuilder sb) {

		sb.append("Release plan: '" +getIdString());
		sb.append("' ");
		if (isConsistent())
			sb.append("is correct.");
		else 
			sb.append("contains errors.");
		sb.append(ConsistencyCheckService.topicSeparator);
		for (ReleaseInfo release: releases) {
			sb.append("Release: '");
			sb.append(release.releasePlainText());
			sb.append("' ");
			sb.append(ConsistencyCheckService.topicSeparator);

			sb.append("Assigned requirements: ");
			sb.append(release.getAssignedRequirementsStr());
			sb.append(ConsistencyCheckService.topicSeparator);
			sb.append("Capacity used: ");
			sb.append(release.getCapacityExplanation());
			sb.append(ConsistencyCheckService.topicSeparator);
		}
		sb.append("These relationships are NOT satisfied: ");
		sb.append(getFailedRelationShipsMsg());
		sb.append(ConsistencyCheckService.topicSeparator);
		sb.append("These relationships are Disabled: ");
		sb.append(getDisabledRelationShipsMsg());
		sb.append(ConsistencyCheckService.topicSeparator);
	}


	public String getReleasePlanMessage() {
		StringBuilder sb = new StringBuilder();
		appendReleasePlanMessage (sb);
		return sb.toString();
	}
}
