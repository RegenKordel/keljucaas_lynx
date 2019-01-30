package eu.openreq.keljucaas.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import eu.openreq.keljucaas.domain.release.Diagnosable;
import eu.openreq.keljucaas.domain.release.Element4Csp;
import eu.openreq.keljucaas.domain.release.IncompatibleRelationship4Csp;
import eu.openreq.keljucaas.domain.release.Relationship4Csp;
import eu.openreq.keljucaas.domain.release.ReleaseInfo;
import eu.openreq.keljucaas.domain.release.ReleasePlanInfo;
import eu.openreq.keljucaas.domain.release.RequiresRelationship4Csp;
import fi.helsinki.ese.murmeli.Container;
import fi.helsinki.ese.murmeli.Element;
import fi.helsinki.ese.murmeli.ElementModel;
import fi.helsinki.ese.murmeli.Relationship;
import fi.helsinki.ese.murmeli.Relationship.NameType;

/**
 * This class contains a private class Element4Csp
 * This class is used to check consistency and do diagnosis.
 * 
 * Mapping from OpenReq domain:
 * Requirement = Element
 * Release = (Sub-)container
 * Project = (Root)container
 * 
 * Requirements can be assigned to releases. 
 * 
 * Releases are arranged in a linear sequence (ordered by release date)
 * In external view, the first release is number 1, second number 2 etc.
 * 
 * A requirement can be assigned to a release. If assigned, 
 * the requirement specifies the number of the release.
 * If a requirement is not assigned, it specifies release 0.
 * 
 * The implmentation used to use -1 for designating unassigned release, but his became confusing.
 * Unassigned release is always presented as release 0, including the choco model. It now includes 
 * release 0 for unallocated requirements.  
 * Some analysis tasks require that release 1 is set to internal index 0.
 * Some data structures can conveniently include "unassigned release" at 
 * data structure index 0., release 1 at 1 etc.
 * For some data structures, index 0 must represent release 1.
 * This causes some difficulties in understanding the code.
 *  * 
 */
public class CSPPlanner {

	public final static int UNASSIGNED_RELEASE = 0; 

	private ElementModel elementModel;
	private List<ReleasePlanAnalysisDefinition> wantedplans;

	private LinkedHashMap<String, Integer> elementIDToIndex;
	private LinkedHashMap<Integer, String> indexToElementID;

	private final int nContainers;
	private final int nElements;
	private Element4Csp[] element4CSPs = null;
	private ArrayList<Relationship4Csp> relationship4Csps = new ArrayList<>();
	private boolean diagnoseElements; //if false, diagnosis requirements setting does not clear these requirements 
	private boolean diagnoseRelations; //if false, diagnosis requirements setting does not clear these requirements

	private LinkedHashMap <String, ReleasePlanInfo> releaseStates = new LinkedHashMap<>();

	Model model = null;
	//Solution solution;

	public CSPPlanner(ElementModel elementModel, List<ReleasePlanAnalysisDefinition> wantedplans) {
		this.elementModel = elementModel;
		this.wantedplans = wantedplans;
		nContainers = elementModel.getsubContainers().size();
		nElements = elementModel.getElements().size();
		initialize();
	}

	private void initialize() {
		elementIDToIndex = new LinkedHashMap<>(nElements);
		indexToElementID = new LinkedHashMap<>(nElements);
		initializeElementIndexMaps();

		this.element4CSPs = new Element4Csp[nElements];


		generateCSP();

		initializeReleaseStates();
//		ReleasePlanInfo submittedReleasePlan = releaseStates.get("submitted");
		//allocateOriginallyAsssignedElements(submittedReleasePlan);
		//releaseStates.put(submittedReleasePlan.getIdString(), submittedReleasePlan);
//		System.out.println("");
//		System.out.println("Initialize: submmitted "+ submittedReleasePlan.getReleasePlanMessage());
	}

	private Element4Csp getElement4Csp(String elementId) {
		Integer index = elementIDToIndex.get(elementId);
		if (index == null)
			return null;
		return element4CSPs[index.intValue()];

	}

	private void initializeElementIndexMaps() {
		int index1 = 0;
		for (Element element : elementModel.getElements().values()) {
			Integer index2 = Integer.valueOf(index1++);
			elementIDToIndex.put(element.getNameID(), index2);
			indexToElementID.put(index2, element.getNameID());
		}
	}

	public ReleasePlanInfo getReleasePlan(String planName) {
		return releaseStates.get(planName);
	}

	public final int getNReleases() {
		return nContainers;
	}


	public final int getNelements() {
		return nElements;
	}


	/**
	 * Generate Constraint Satisfaction Problem model
	 */
	public void generateCSP() {
		model = new Model("ReleasePlanner"); // NOTE change this?

		initializeElementCSPs();
		setEffortConstraints();
		initializeRelationship4CSPs();
		//solution = new Solution(model);
		//TODO: modify so that it can be controlled if efforts and capacities are used. 
		//If they are not, the mode check boils down to checking dependencies, 
		//which should be more straightforward. Byt maybe it is just easier to make minor modifications

		//TODO Think if support for unassigned requirements should be dropped for now?
	}


	/**
	 * Initialize Element4Csp[] elementCSPs
	 */
	private void initializeElementCSPs() {
		for (Element element : elementModel.getElements().values()) {
			Element4Csp element4csp = new Element4Csp(element, this, model, this.elementModel);
			element4CSPs[elementIDToIndex.get(element.getNameID())] = element4csp;
		}
	}

	/**
	 * Initialize Element4Csp[] elementCSPs
	 */
	private void initializeReleaseStates() {
		for (ReleasePlanAnalysisDefinition wantedPlan : wantedplans) {
			ReleasePlanInfo releasePlan = new ReleasePlanInfo(wantedPlan.getPlanName(), wantedPlan);
			createInitialState (releasePlan);
			//just check, no analysis
			if (!wantedPlan.isDiagnoseRequirements() && !wantedPlan.isDiagnoseRelationships()) {
				allocateOriginallyAsssignedElements(releasePlan);
				setOriginallyEnabledRelationships(releasePlan);
				releasePlan.determineConsistency();
			}
			releaseStates.put(releasePlan.getIdString(), releasePlan);
		}
	}

	private void initializeRelationship4CSPs() {

		for (Relationship relation : this.elementModel.getRelations()) {
			if (isSupported(relation.getNameType())) {
				Element4Csp from = getElement4Csp(relation.getFromID());
				Element4Csp to = getElement4Csp(relation.getToID());
				if ((from!= null) && (to != null)) {
					switch(relation.getNameType()) {
					case REQUIRES:
						relationship4Csps.add(
								new RequiresRelationship4Csp(from, to, model, Integer.valueOf(relation.getID())));
						break;
					case INCOMPATIBLE:
						relationship4Csps.add(
								new IncompatibleRelationship4Csp(from, to, model,Integer.valueOf(relation.getID())));
						break;
					case CONTRIBUTES:
					case DAMAGES:
					case DUPLICATES:
					case REFINES:
					case REPLACES:
					case SIMILAR:
						break;

					}
				}
			}
		}
	}


	protected ReleasePlanInfo createInitialState (ReleasePlanInfo releasePlanInfo) {

		ReleaseInfo unAllocatedRelease = new ReleaseInfo(CSPPlanner.UNASSIGNED_RELEASE, "unassigned");
		releasePlanInfo.addReleaseInfo(unAllocatedRelease);

		int index = CSPPlanner.UNASSIGNED_RELEASE +1;
		for (Container container : elementModel.getsubContainers()) {
			ReleaseInfo releaseInfo = new ReleaseInfo(index++, container.getNameID());
			int capacityOfRelease = determineCapacityAvailable(container);
			releaseInfo.setCapacityAvailable(capacityOfRelease);
			releasePlanInfo.addReleaseInfo(releaseInfo);
		}
		return releasePlanInfo;
	}

	protected void allocateOriginallyAsssignedElements(ReleasePlanInfo releasePlanInfo) {
		for (Element4Csp element4Csp:element4CSPs) {
			int originalRelease= element4Csp.getOriginallyAssignedRelease();
			ReleaseInfo assignedRelease = releasePlanInfo.getReleaseInfo(originalRelease);
			releasePlanInfo.assignElementToRelease(element4Csp, assignedRelease);
		}
	}

	protected void setOriginallyEnabledRelationships(ReleasePlanInfo releasePlanInfo) {
		for (Relationship4Csp relationship4Csp: relationship4Csps) {
			releasePlanInfo.addEnabledRelationsShip(relationship4Csp);
		}
	}


	protected void allocateDiagnosed(ReleasePlanInfo releasePlanInfo) {
		for (Element4Csp element4Csp:element4CSPs) {
			int allocatedRelease=  element4Csp.getAssignedContainer().getValue();
			ReleaseInfo assignedRelease;
			if (element4Csp.getIsIncluded().getValue() != 0) 
				assignedRelease = releasePlanInfo.getReleaseInfo(allocatedRelease);
			else 
				assignedRelease = releasePlanInfo.getReleaseInfo(CSPPlanner.UNASSIGNED_RELEASE);
			releasePlanInfo.assignElementToRelease(element4Csp, assignedRelease);
			//assignedRelease.addAssignedElement(element4Csp);
		}

		for (Relationship4Csp relationship4Csp: relationship4Csps) {
			if (relationship4Csp.getIsIncluded().getValue() != 0) 
				releasePlanInfo.addEnabledRelationsShip(relationship4Csp);
			else
				releasePlanInfo.addDisabledRelationsShip(relationship4Csp);
		}
	}



	/**
	 * Set constraints for ensuring enough effort per release
	 */
	private void setEffortConstraints() {
		for (Container container : elementModel.getsubContainers()) {
			ArrayList<IntVar> containerEffortVars = new ArrayList<>();
			for (Element element : elementModel.getElements().values()) {
				Element4Csp element4Csp = element4CSPs[elementIDToIndex.get(element.getNameID())];

				IntVar effortVar = element4Csp.getEffortOfContainer(container.getID());

				if (effortVar.getUB() >= 0) {// do not add variable to be summed if the variable cannot be > 0
					containerEffortVars.add(effortVar);
				}
			}
			if (containerEffortVars.size() > 0) {
				IntVar[] effortVarArray = containerEffortVars.toArray(new IntVar[0]);
				int capacityOfRelease = determineCapacityAvailable(container);
				model.sum(effortVarArray, "<=", capacityOfRelease).post(); // TODO: What if no capacity?
			}
		}
	}





	public boolean isReleasePlanConsistent() {
		requireAllElements();
		requireAllRelations();

		model.getSolver().reset();

		Solver solver = model.getSolver();
		boolean solvable = solver.solve();
		//		if (solvable)
		//			solution.record();
		return solvable;
	}


	/**
	 * Get problematic element IDs as a String (elements that have been
	 * diagnosed as breaking the consistency of the model)
	 * 
	 * @return
	 */
//	public String getDiagnosis() {
//
//		//List<Element4Csp> diagnosis = fastDiag(allElements, allElements);
//		List<Diagnosable> diagnosis = getDiagnosis(true, false);
//		StringBuffer sb = new StringBuffer(); 
//		if (diagnosis.isEmpty()) {
//			sb.append("(No Diagnosis found.)");
//		} 
//		else {
//			for (int i = 0; i < diagnosis.size(); i++) {
//				Diagnosable reqB = diagnosis.get(i);
//				if (!(reqB instanceof Element4Csp))
//					continue;
//				Element4Csp elem = (Element4Csp) reqB;
//				String reqId = elem.getNameId();
//				sb.append(reqId);
//				if (diagnosis.size() > 1 && i < diagnosis.size() - 1) {
//					sb.append(","); //TODO modify; all not included anymore
//				}
//			}
//			getDiagnosedSolution(true, false);
//		}
//		return sb.toString();
//	}

	public void performDiagnoses() {

		List<Diagnosable> all= new ArrayList<>();
		for (int req = 0; req < nElements; req++) {
			all.add(element4CSPs[req]);
		}

		for (Relationship4Csp relationship4Csp: relationship4Csps)
			all.add(relationship4Csp);

		for (ReleasePlanInfo releasePlanInfo : releaseStates.values()) {
			ReleasePlanAnalysisDefinition wanted = releasePlanInfo.getWantedAnalysis();
			this.diagnoseElements = wanted.isDiagnoseRequirements();
			this.diagnoseRelations= wanted.isDiagnoseRelationships();
			boolean isAnalysisRequired = diagnoseElements || diagnoseRelations;
			ReleasePlanInfo requireFailedForDiagnosis = releaseStates.get(wanted.analyzeOnlyIfIncosistentPlan);
			if ((requireFailedForDiagnosis != null) && requireFailedForDiagnosis.isConsistent())
					isAnalysisRequired = false;
			if (isAnalysisRequired) {
				List<Diagnosable> diagnosis = getDiagnosis(diagnoseElements, diagnoseRelations);
				List<Diagnosable> included = diffListsAsSets(all, diagnosis);
				setRequirementsToList(included);
				boolean OK = consistent(included);
				releasePlanInfo.setConsistent(OK);
				if (OK) {
					//use the current, determined release plan
					allocateDiagnosed(releasePlanInfo);
					releasePlanInfo.setAppliedDiagnosis(diagnosis);
				}
				else {
					//no no diagnosis helped
					//so do not set a diagnosis to utilize
					
					// how to return relevant information to user?
					// If we take out all requirements, relationships do not have any meaning.
					//but if all relationships were diagnosed out, one can could get useful info about resource consumption
					//But the same info is available from the submitted solution
					
					// thus, do not set diagnosis
					// and use the submitted release plan as the release plan.
					//it should not be used for anything
					
					allocateOriginallyAsssignedElements(releasePlanInfo);
					setOriginallyEnabledRelationships(releasePlanInfo);
				}
			}
		}

	}


//	protected void getDiagnosedSolution(boolean diagnoseElements, boolean diagnoseRelationships) {
//
//		List<Diagnosable> all= new ArrayList<>();
//		for (int req = 0; req < nElements; req++) {
//			all.add(element4CSPs[req]);
//		}
//
//		for (Relationship4Csp relationship4Csp: relationship4Csps)
//			all.add(relationship4Csp);
//
//
//		List<Diagnosable> diagnosis = getDiagnosis(diagnoseElements, diagnoseRelationships);
//
//		List<Diagnosable> included = diffListsAsSets(all, diagnosis);
//
//		//TODO: if  a diagnosis cannot be found, manage ouput.
//		//AD to releasePlanInfo information on consistency (determined by check, if diagnosis produced a consistent solution
//		//add parameters for consistency ceck and diagnosis calls if textual description is required
//		//and what elements it shoud contain.
//		//whether to show unallocated release
//		//whether to show cpacitties
//		//Add 
//		//
//		setRequirementsToList(included);
//		boolean OK = consistent(included);
//		ReleasePlanInfo diagnosedPlan = releaseStates.get("diagnosed");
//		if (diagnosedPlan != null)  {
//			allocateDiagnosed(diagnosedPlan);
//			diagnosedPlan.setAppliedDiagnosis(diagnosis);
//			diagnosedPlan.setConsistent(OK);
//		}
//		//System.out.println("getDiagnosedSolution::" + diagnosedPlan.getReleasePlanMessage());
//
//	}


//	public String getDiagnosisStr(boolean diagnoseElements, boolean diagnoseRelationships) {
//
//		//List<Element4Csp> diagnosis = fastDiag(allElements, allElements);
//		List<Diagnosable> diagnosis = getDiagnosis(diagnoseElements, diagnoseRelationships);
//		StringBuilder sb = new StringBuilder (); 
//		if (diagnosis.isEmpty()) {
//			sb.append("(No Diagnosis found.)");
//		} 
//		else {
//			for (int i = 0; i < diagnosis.size(); i++) {
//				Diagnosable reqB = diagnosis.get(i);
//				if (reqB instanceof Element4Csp) {
//					Element4Csp elem = (Element4Csp) reqB;
//					String reqId = elem.getNameId();
//					sb.append(reqId);
//				}
//				else 
//					if (reqB instanceof Relationship4Csp) {
//						sb.append(reqB.getNameId());
//					}
//				if (diagnosis.size() > 1 && i < diagnosis.size() - 1) {
//					sb.append(ConsistencyCheckService.fieldSeparator); //TODO modify; all not included anymore
//				}
//			}
//			getDiagnosedSolution(diagnoseElements, diagnoseRelationships);
//		}
//		ReleasePlanInfo diagnosedPlan = releaseStates.get("diagnosed");
//		if (diagnosedPlan != null) {
//			sb.append(ConsistencyCheckService.topicSeparator);
//			diagnosedPlan.appendReleasePlanMessage(sb);
//		}
//		return sb.toString();
//	}

	//	public String getDiagnosisStr(boolean diagnoseElements, boolean diagnoseRelationships) {
	//
	//		//List<Element4Csp> diagnosis = fastDiag(allElements, allElements);
	//		List<Diagnosable> diagnosis = getDiagnosis(diagnoseElements, diagnoseRelationships);
	//		StringBuilder sb = new StringBuilder (); 
	//		if (diagnosis.isEmpty()) {
	//			sb.append("(No Diagnosis found.)");
	//		} 
	//		else {
	//			for (int i = 0; i < diagnosis.size(); i++) {
	//				Diagnosable reqB = diagnosis.get(i);
	//				if (reqB instanceof Element4Csp) {
	//					Element4Csp elem = (Element4Csp) reqB;
	//					String reqId = elem.getId();
	//					sb.append(reqId);
	//				}
	//				else 
	//					if (reqB instanceof Relationship4Csp) {
	//						sb.append(reqB.toString());
	//					}
	//				if (diagnosis.size() > 1 && i < diagnosis.size() - 1) {
	//					sb.append(","); //TODO modify; all not included anymore
	//				}
	//			}
	//			getDiagnosedSolution(diagnoseElements, diagnoseRelationships);
	//		}
	//		ReleasePlanInfo diagnosedPlan = releaseStates.get("diagnosed");
	//		if (diagnosedPlan != null) {
	//			sb.append("/n");
	//			diagnosedPlan.appendReleasePlanMessage(sb);
	//		}
	//		return sb.toString();
	//	}

	protected List<Diagnosable> getDiagnosis(boolean diagnoseElements, boolean diagnoseRelations) {
		List<Diagnosable> allElements = new ArrayList<>();
		this.diagnoseElements = diagnoseElements;
		this.diagnoseRelations = diagnoseRelations;

		if (diagnoseElements)
			for (int req = 0; req < nElements; req++) 
				allElements.add(element4CSPs[req]);
		else
			requireAllElements();


		if (diagnoseRelations)
			for (Relationship4Csp relationship4Csp: relationship4Csps)
				allElements.add(relationship4Csp);
		else
			requireAllRelations();

		return fastDiag(allElements, allElements);
	}

	private void setRequirementsToList(List<Diagnosable> elementsToSet) {
		if (diagnoseElements)
			for (int i = 0; i < nElements; i++) {
				element4CSPs[i].unRequire();
			}
		if (diagnoseRelations)
			for (Relationship4Csp relationship4Csp: relationship4Csps)
				relationship4Csp.unRequire();

		for (Diagnosable element : elementsToSet) {
			element.require(true);
		}
	}



	private boolean consistent(List<Diagnosable> constraints) {
		if (constraints.size() == 0) {
			return true;
		}
		setRequirementsToList(constraints);
		Solver solver = model.getSolver();
		solver.reset();
		boolean result = solver.solve();
		//		if (result)
		//			solution.record();
		return result;
	}


	/**
	 * Adapted from
	 * /JMiniZinc/at.siemens.ct.jminizinc.diag/src/main/java/at/siemens/ct/jmz/diag/FastDiag.java
	 * 
	 * @param C
	 * @param AC
	 * @return
	 */
	private List<Diagnosable> fastDiag(List<Diagnosable> C, List<Diagnosable> AC) {

		if (C.isEmpty()) {
			return Collections.emptyList();
		}
		if (consistent(C)) {
			return Collections.emptyList();
		}

		List<Diagnosable> ACWithoutC = diffListsAsSets(AC, C);
		Boolean searchForDiagnosis = consistent(ACWithoutC);
		if (!searchForDiagnosis) {
			return Collections.emptyList();
		}
		return fd(Collections.emptyList(), C, AC);
	}


	/**
	 * Function that computes diagnoses in FastDiag Adapted from
	 * /JMiniZinc/at.siemens.ct.jminizinc.diag/src/main/java/at/siemens/ct/jmz/diag/FastDiag.java
	 * 
	 * @param D
	 *            A subset from the user constraints
	 * @param C
	 *            A subset from the user constraints
	 * @param AC
	 *            user constraints
	 * @return a diagnose
	 */
	private List<Diagnosable> fd(List<Diagnosable> D, List<Diagnosable> C, List<Diagnosable> AC) {

		boolean isConsistent = consistent(AC);
		int q = C.size();

		if (!D.isEmpty()) {
			if (isConsistent) {
				return Collections.emptyList();
			}
		}

		if (q == 1) {
			return new LinkedList<Diagnosable>(C);
		}

		int k = q / 2;
		List<Diagnosable> C1 = C.subList(0, k);
		List<Diagnosable> C2 = C.subList(k, q);

		List<Diagnosable> ACWithoutC2 = diffListsAsSets(AC, C2);
		List<Diagnosable> D1 = fd(C2, C1, ACWithoutC2);

		List<Diagnosable> ACWithoutD1 = diffListsAsSets(AC, D1);
		List<Diagnosable> D2 = fd(D1, C2, ACWithoutD1);

		return appendListsAsSets(D1, D2);
	}


	public static List<Diagnosable> appendListsAsSets(List<Diagnosable> CS1, List<Diagnosable> CS2) {
		List<Diagnosable> union = new ArrayList<>(CS1);
		if (CS2 == null)
			return union;

		for (Diagnosable c : CS2) {
			if (!union.contains(c)) {
				union.add(c);
			}
		}
		return union;
	}


	public static List<Diagnosable> diffListsAsSets(List<Diagnosable> ac, List<Diagnosable> c2) {
		List<Diagnosable> diff = new ArrayList<>();
		for (Diagnosable element : ac) {
			if (!c2.contains(element)) {
				diff.add(element);
			}
		}
		return diff;
	}

	public static boolean isSupported(NameType nameType) {
		switch (nameType) {
		case INCOMPATIBLE:
		case REQUIRES:
			return true;

		case CONTRIBUTES:
		case DAMAGES:
		case DUPLICATES:
		case REFINES:
		case REPLACES:
		case SIMILAR:
		default:
			return false;
		}
	}

	public void requireAllElements() {
		for (Element4Csp element4Csp : element4CSPs)
			element4Csp.require(true);
	}

	public void requireAllRelations() {
		for (Relationship4Csp relationship4Csp : relationship4Csps)
			relationship4Csp.require(true);
	}


	//TODO JT: remove doubles? Or is there some reason
	public int determineCapacityAvailable(Container container) {
		Double d = (Double) this.elementModel.getAttributeValues().get(container.getAttributes().get("capacity")).getValue();
		return d.intValue();
	}

	public static class ReleasePlanAnalysisDefinition {
		private final String planName;
		private String analyzeOnlyIfIncosistentPlan;

		private  boolean diagnoseRequirements;
		private boolean diagnoseRelationships;
		


		public ReleasePlanAnalysisDefinition(String planName, boolean diagnoseRequirements,
				boolean diagnoseRelationships) {
			super();
			this.planName = planName;
			this.diagnoseRequirements = diagnoseRequirements;
			this.diagnoseRelationships = diagnoseRelationships;
		}


		public String getAnalyzeOnlyIfIncosistentPlan() {
			return analyzeOnlyIfIncosistentPlan;
		}


		public void setAnalyzeOnlyIfIncosistentPlan(String analyzeOnlyIfIncosistentPlan) {
			this.analyzeOnlyIfIncosistentPlan = analyzeOnlyIfIncosistentPlan;
		}


		public String getPlanName() {
			return planName;
		}

		public boolean isDiagnoseRequirements() {
			return diagnoseRequirements;
		}


		public boolean isDiagnoseRelationships() {
			return diagnoseRelationships;
		}
		
		public boolean isDiagnoseDesired() {
			return diagnoseRequirements || diagnoseRelationships;
		}
		

	}
}
