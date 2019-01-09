package eu.openreq.keljucaas.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import eu.openreq.keljucaas.domain.Diagnosable;
import eu.openreq.keljucaas.domain.Element4Csp;
import eu.openreq.keljucaas.domain.IncompatibleRelationship4Csp;
import eu.openreq.keljucaas.domain.Relationship4Csp;
import eu.openreq.keljucaas.domain.ReleaseInfo;
import eu.openreq.keljucaas.domain.ReleasePlanInfo;
import eu.openreq.keljucaas.domain.RequiresRelationship4Csp;
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
 */
public class CSPPlanner {

	public final static int UNASSIGNED = -1; 

	private ElementModel elementModel;

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

	public CSPPlanner(ElementModel elementModel) {
		this.elementModel = elementModel;
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
		ReleasePlanInfo submittedReleasePlan = createInitialState();
		allocateOriginallyAsssignedElements(submittedReleasePlan);
		releaseStates.put("submitted", submittedReleasePlan);
		ReleasePlanInfo diagnosedReleasePlan = createInitialState();
		releaseStates.put("diagnosed", diagnosedReleasePlan);

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
								new RequiresRelationship4Csp(from, to, model));
						break;
					case INCOMPATIBLE:
						relationship4Csps.add(
								new IncompatibleRelationship4Csp(from, to, model));
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


	protected ReleasePlanInfo createInitialState () {

		ReleasePlanInfo releasePlanInfo = new ReleasePlanInfo();
		ReleaseInfo unAllocatedRelease = new ReleaseInfo(CSPPlanner.UNASSIGNED, "unassigned");
		releasePlanInfo.addReleaseInfo(unAllocatedRelease);

		int index = 0;
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
			int originalRelease= element4Csp.getOriginallyAssignedRelease() +1;
			ReleaseInfo assignedRelease = releasePlanInfo.getReleaseInfo(originalRelease);
			releasePlanInfo.assignElementToRelease(element4Csp, assignedRelease);
			//assignedRelease.addAssignedElement(element4Csp);
		}
	}

	protected void setOriginallyEnabledReleases(ReleasePlanInfo releasePlanInfo) {
		for (Relationship4Csp relationship4Csp: relationship4Csps) {
			releasePlanInfo.addEnabledRelationsShip(relationship4Csp);
		}
	}


	protected void allocateDiagnosed(ReleasePlanInfo releasePlanInfo) {
		for (Element4Csp element4Csp:element4CSPs) {
			int allocatedRelease= element4Csp.getAssignedContainer().getValue()+1;
			ReleaseInfo assignedRelease;
			if (element4Csp.getIsIncluded().getValue() != 0) 
				assignedRelease = releasePlanInfo.getReleaseInfo(allocatedRelease);
			else 
				assignedRelease = releasePlanInfo.getReleaseInfo(0);
			releasePlanInfo.assignElementToRelease(element4Csp, assignedRelease);
			//assignedRelease.addAssignedElement(element4Csp);
		}

		for (Relationship4Csp relationship4Csp: relationship4Csps) {
			if (relationship4Csp.getIsIncluded().getValue() != 0) 
				releasePlanInfo.addEnabledRelationsShip(relationship4Csp);
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

				IntVar effortVar = element4Csp.getEffortOfContainer(container.getID() - 1);

				if (effortVar.getUB() >= 0) {// do not add variable to be summed if the variable cannot be > 0
					containerEffortVars.add(effortVar);
				}
			}
			if (containerEffortVars.size() > 0) {
				IntVar[] effortVarArray = containerEffortVars.toArray(new IntVar[0]);
				//TODO JT: why double?
				Double d = (Double) this.elementModel.getAttributeValues().get(container.getAttributes().get("capacity")).getValue();
				model.sum(effortVarArray, "<=", (Integer) d.intValue()).post(); // TODO: What if no capacity?
			}
		}
	}


	//	/**
	//	 * Add different dependency types to the model
	//	 */
	//	private void addAllRelationships() {
	//		for (Element element : elementModel.getElements().values()) {
	//			Element4Csp elementFrom = element4CSPs[elementIDToIndex.get(element.getNameID())];
	//			addRequiresRelationships(elementFrom, element);
	//			addExcludesRelationships(elementFrom, element);
	//		}
	//	}
	//
	//
	//	/**
	//	 * Add requires-relationships, in this version if A requires B, B must be
	//	 * included and assigned in the same or earlier sub-container than A
	//	 * 
	//	 * @param requiring
	//	 * @param element
	//	 */
	//	private void addRequiresRelationships(Element4Csp requiring, Element element) {
	//		if (!getRequiresRelationships(element).isEmpty()) {
	//			addRelationshipsToModel(requiring, getRequiresRelationships(element), model, 1, "<=");
	//		}
	//	}
	//
	//
	//	private List<Relationship> getRequiresRelationships(Element element) {
	//		List<Relationship> relationships = new ArrayList<>();
	//
	//		for (Relationship relation : this.elementModel.getRelations()) {
	//			if (relation.getFromID().equals(element.getNameID())) {
	//				if (relation.getNameType().equals(NameType.REQUIRES)) {
	//					relationships.add(relation);
	//				}
	//			}
	//		}
	//		return relationships;
	//	}
	//
	//
	//	/**
	//	 * Add excludes-relationships, in this (global) version if A excludes B, B cannot
	//	 * be in the same root container (in any sub-container) as A
	//	 * 
	//	 * @param excluding
	//	 * @param element
	//	 */
	//	private void addExcludesRelationships(Element4Csp excluding, Element element) {
	//		if (!getExcludesRelationships(element).isEmpty()) {
	//			addRelationshipsToModel(excluding, getExcludesRelationships(element), model, 0, "!=");
	//		}
	//	}
	//
	//
	//	private List<Relationship> getExcludesRelationships(Element element) {
	//		List<Relationship> relationships = new ArrayList<>();
	//
	//		for (Relationship relation : this.elementModel.getRelations()) {
	//			if (relation.getFromID().equals(element.getNameID())) {
	//				if (relation.getNameType().equals(NameType.INCOMPATIBLE)) {
	//					relationships.add(relation);
	//				}
	//			}
	//		}
	//		return relationships;
	//	}
	//
	//
	//	//TODO Unclear semanitcs for parameters of this method
	//	/**
	//	 * Adds relationships (e.g. requires, excludes) to the model
	//	 * 
	//	 * @param elementFrom
	//	 *            Element4Csp, tells the requiring/excluding element
	//	 * @param relationships
	//	 *            List containing the elements requiresrelationships or
	//	 *            excludesrelationships
	//	 * @param model
	//	 *            Choco Model
	//	 * @param isIncludedValue
	//	 *            tells whether the relationship is requiring (1) or excluding (0) (if
	//	 *            0, two elements cannot be in the same project)
	//	 * @param relation n //TODO this seems to be choco comparison operator, redocument or change. 
	//	 * 					
	//	 *            String that tells the model if the two elements can or cannot
	//	 *            be in the same sub-container (or in a previous etc)
	//	 */
	//	private void addRelationshipsToModel(Element4Csp elementFrom, List<Relationship> relationships, Model model,
	//			int isIncludedValue, String relation) {
	//		for (Relationship rel : relationships) {
	//			int elementIndex = elementIDToIndex.get(rel.getToID());
	//			Element4Csp elementTo = element4CSPs[elementIndex];
	//			IntVar size = model.intVar("size", 2); 	// added this and the third model.arithm(), breaks consistency if
	//			// a dependent element is missing from sub-containers (in which case it's 
	//			// assignedContainer is an array and has domainSize > 1)
	//			//JT: there is no array in assignedContainer! the domain size can be >1, yes. 
	//			//JT: and why relationships.size() of 'size' variables? could use a constant in arithm!
	//			//TODO redo 
	//			model.ifThen(elementFrom.getIsIncluded(),
	//					model.and(model.arithm(elementTo.getIsIncluded(), "=", isIncludedValue),
	//							model.arithm(elementTo.getAssignedContainer(), relation,
	//									elementFrom.getAssignedContainer()),
	//							model.arithm(size, "!=", elementTo.getAssignedContainer().getDomainSize())));
	//			//If elementFrom.getIsIncluded(), Then model.and(...)
	//			//"Example: - ifThen(b1, arithm(v1, "=", 2));: b1 is equal to 1 => v1 = 2, so v1 !"
	//		}
	//	}


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
	public String getDiagnosis() {

		//List<Element4Csp> diagnosis = fastDiag(allElements, allElements);
		List<Diagnosable> diagnosis = getDiagnosis(true, false);
		StringBuffer sb = new StringBuffer(); 
		if (diagnosis.isEmpty()) {
			sb.append("(No Diagnosis found.)");
		} 
		else {
			for (int i = 0; i < diagnosis.size(); i++) {
				Diagnosable reqB = diagnosis.get(i);
				if (!(reqB instanceof Element4Csp))
					continue;
				Element4Csp elem = (Element4Csp) reqB;
				String reqId = elem.getId();
				sb.append(reqId);
				if (diagnosis.size() > 1 && i < diagnosis.size() - 1) {
					sb.append(","); //TODO modify; all not included anymore
				}
			}
			getDiagnosedSolution(true, false);
		}
		return sb.toString();
	}

	protected void getDiagnosedSolution(boolean diagnoseElements, boolean diagnoseRelationships) {

		List<Diagnosable> all= new ArrayList<>();
		for (int req = 0; req < nElements; req++) {
			all.add(element4CSPs[req]);
		}

		for (Relationship4Csp relationship4Csp: relationship4Csps)
			all.add(relationship4Csp);


		List<Diagnosable> diagnosis = getDiagnosis(diagnoseElements, diagnoseRelationships);

		List<Diagnosable> included = diffListsAsSets(all, diagnosis);


		setRequirementsToList(included);
		boolean OK = consistent(included);
		ReleasePlanInfo diagnosedPlan = releaseStates.get("diagnosed");
		if ((diagnosedPlan != null) && OK ) {
			allocateDiagnosed(diagnosedPlan);
		}

	}

	public String getDiagnosisStr(boolean diagnoseElements, boolean diagnoseRelationships) {

		//List<Element4Csp> diagnosis = fastDiag(allElements, allElements);
		List<Diagnosable> diagnosis = getDiagnosis(diagnoseElements, diagnoseRelationships);
		StringBuffer sb = new StringBuffer(); 
		if (diagnosis.isEmpty()) {
			sb.append("(No Diagnosis found.)");
		} 
		else {
			for (int i = 0; i < diagnosis.size(); i++) {
				Diagnosable reqB = diagnosis.get(i);
				if (reqB instanceof Element4Csp) {
					Element4Csp elem = (Element4Csp) reqB;
					String reqId = elem.getId();
					sb.append(reqId);
				}
				else 
					if (reqB instanceof Relationship4Csp) {
						sb.append(reqB.toString());
					}
				if (diagnosis.size() > 1 && i < diagnosis.size() - 1) {
					sb.append(","); //TODO modify; all not included anymore
				}
			}
			getDiagnosedSolution(diagnoseElements, diagnoseRelationships);
		}
		return sb.toString();
	}

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
		case CONTRIBUTES:
		case DAMAGES:
		case DUPLICATES:
		case REFINES:
		case REPLACES:
		case SIMILAR:
		default:
			return false;

		case INCOMPATIBLE:
		case REQUIRES:
			return true;
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
}
