package eu.openreq.keljucaas.domain;

import java.util.ArrayList;


public class ReleaseInfo {

	private final int releaseNdx;
	private final String idString;
	private int capacityUsed= 0;
	private int capacityAvailable;
	private ArrayList <Element4Csp> assignedElements = new ArrayList<>();

	public ReleaseInfo(int releaseNdx, String idString) {
		this.releaseNdx = releaseNdx;
		this.idString = idString;
	}

	public int getCapacityUsed() {
		return capacityUsed;
	}

	public void setCapacityUsed(int capacityUsed) {
		this.capacityUsed = capacityUsed;
	}

	public int getReleaseNdx() {
		return releaseNdx;
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
}