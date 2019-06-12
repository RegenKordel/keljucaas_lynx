package eu.openreq.keljucaas.domain.release;

import java.util.ArrayList;

import eu.openreq.keljucaas.services.CSPPlanner;
import eu.openreq.keljucaas.services.ConsistencyCheckService;


public class ReleaseInfo {

	private final int releaseNr;
	private final String idString;
	private int capacityUsed= 0;
	private int capacityAvailable = 0;
	private ArrayList <Element4Csp> assignedElements = new ArrayList<>();

	public ReleaseInfo(int releaseNdx, String idString) {
		this.releaseNr = releaseNdx;
		this.idString = idString;
	}

	public int getCapacityUsed() {
		return capacityUsed;
	}

	public void setCapacityUsed(int capacityUsed) {
		this.capacityUsed = capacityUsed;
	}

	public int getReleaseNr() {
		return releaseNr;
	}

	public int getCapacityAvailable() {
		return capacityAvailable;
	}

	public void setCapacityAvailable(int capacityAvailable) {
		this.capacityAvailable = capacityAvailable;
	}

	public ArrayList<Element4Csp> getAssignedElements() {
		return assignedElements;
	}

	public void addAssignedElement(Element4Csp element4Csp) {
		assignedElements.add(element4Csp);	
		capacityUsed += element4Csp.getEffortOfElement();
	}

	public void removeAssignedElemment(Element4Csp element4Csp) {
		boolean removed = assignedElements.remove(element4Csp);
		if (removed)
			capacityUsed -= element4Csp.getEffortOfElement();
	}

	public boolean isCapacitySatisfied() {
		if (releaseNr == CSPPlanner.UNASSIGNED_RELEASE)
			return true;
		else 
			return capacityUsed <= capacityAvailable;
	}
	
	public boolean isConsistent() {
		if (releaseNr == CSPPlanner.UNASSIGNED_RELEASE)
			return true;
		// there might be other checks in the future, so own function
		return  isCapacitySatisfied();
	}
	
	//TODO localize/externalize strings
	public String getCapacityExplanation() {
		boolean isUnlimitedCapacity = (releaseNr == CSPPlanner.UNASSIGNED_RELEASE) || capacityAvailable == 0;
		int spared = capacityAvailable - capacityUsed;
		if (isUnlimitedCapacity)
			return "used: " + capacityUsed + " of (no limit)";
		else
			return "used: " + capacityUsed + " of ("+ capacityAvailable + ") " +
			(spared >=0 ? ("free: " + spared) : ("missing: " + (-spared))) +".";  
	}
	
	public String releasePlainText() {
		return idString + ((releaseNr == CSPPlanner.UNASSIGNED_RELEASE) ? "" : (" " + releaseNr));
		
	}
	
	public void buildAssignedRequirements(StringBuilder sb) {
		boolean added = false;
		for (Element4Csp req : getAssignedElements()) {
			sb.append(req.getNameId());
			sb.append(ConsistencyCheckService.fieldSeparator);
			added = true;
		}
		if (added)
			sb.setLength(sb.length() - ConsistencyCheckService.fieldSeparator.length());
	}
	
	public String getAssignedRequirementsStr() {
		StringBuilder sb = new StringBuilder();
		buildAssignedRequirements(sb);
		return sb.toString();
	}

	public final String getIdString() {
		return idString;
	}
	
	
}