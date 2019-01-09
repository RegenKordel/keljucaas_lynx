package eu.openreq.keljucaas.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReleasePlanInfo {
	private ArrayList<ReleaseInfo> releases = new ArrayList<>();
	private ArrayList <Relationship4Csp> enabledRelationsShips = new ArrayList<>();
	private Map<String, ReleaseInfo> releaseOfElement = new LinkedHashMap<>();

	public void addReleaseInfo(ReleaseInfo releaseInfo) {
		releases.add(releaseInfo);
	}

	public ReleaseInfo getReleaseInfo(int index) {
		return releases.get(index);
	}
	
	public void addEnabledRelationsShip(Relationship4Csp relationship4Csp) {
		enabledRelationsShips.add(relationship4Csp);	
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
}
