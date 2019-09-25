package eu.openreq.keljucaas.domain.release;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.openreq.keljucaas.services.ConsistencyCheckService;

public class ReleasePlanInfo {
	private ArrayList<ReleaseInfo> releases = new ArrayList<>();
	private ArrayList <Relationship4Csp> enabledRelationsShips = new ArrayList<>();
	private ArrayList <Relationship4Csp> disabledRelationsShips = new ArrayList<>();
	private ArrayList <IgnoredRelationship> ignoredRelationsShips = new ArrayList<>();

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
	
	public ArrayList<ReleaseInfo> getReleases() {
		return releases;
	}

	public void addEnabledRelationsShip(Relationship4Csp relationship4Csp) {
		enabledRelationsShips.add(relationship4Csp);	
	}

	public void addDisabledRelationsShip(Relationship4Csp relationship4Csp) {
		disabledRelationsShips.add(relationship4Csp);	
	}
	
	public void setIgnoredRelationsShips(List <IgnoredRelationship> ignoredRelationships) {
		ignoredRelationsShips.addAll(ignoredRelationships);	
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
	
	public final ArrayList<IgnoredRelationship> getIgnoredRelationsShips() {
		return ignoredRelationsShips;
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
	
	public List<Element4Csp> getAppliedDiagnosisElements() {
		if (appliedDiagnosis == null)
			return null;
		List<Element4Csp> retVal = new LinkedList<>();
		for (Diagnosable diagnosed:appliedDiagnosis )
			if (diagnosed instanceof Element4Csp)
				retVal.add((Element4Csp)diagnosed);
		
		return retVal;
	}
	
	public List<Relationship4Csp> getAppliedDiagnosisRelations() {
		if (appliedDiagnosis == null)
			return null;
		List<Relationship4Csp> retVal = new LinkedList<>();
		for (Diagnosable diagnosed:appliedDiagnosis )
			if (diagnosed instanceof Relationship4Csp)
				retVal.add((Relationship4Csp)diagnosed);
		
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
}
