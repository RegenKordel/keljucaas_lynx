package eu.openreq.keljucaas.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import fi.helsinki.ese.murmeli.*;
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

	private ElementModel elementModel;
	
	private LinkedHashMap<String, Integer> elementIDToIndex;
	private LinkedHashMap<Integer, String> indexToElementID;

	private final int nContainers;
	private final int nElements;
	Element4Csp[] elementCSPs = null;
	Model model = null;

	public CSPPlanner(ElementModel elementModel) {
		this.elementModel = elementModel;
		nContainers = elementModel.getsubContainers().size();
		nElements = elementModel.getElements().size();
		elementIDToIndex = new LinkedHashMap<>(nElements);
		indexToElementID = new LinkedHashMap<>(nElements);
		initializeelementIndexMaps();
	}

	
	private void initializeelementIndexMaps() {
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
		this.elementCSPs = new Element4Csp[nElements];

		initializeElementCSPs();
		setConstraints();
		addAllRelationships();
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
			elementCSPs[elementIDToIndex.get(element.getNameID())] = element4csp;
		}
	}

	
	/**
	 * Set constraints for ensuring enough effort per release
	 */
	private void setConstraints() {
		for (Container container : elementModel.getsubContainers()) {
			ArrayList<IntVar> containerEffortVars = new ArrayList<>();
			for (Element element : elementModel.getElements().values()) {
				Element4Csp element4Csp = elementCSPs[elementIDToIndex.get(element.getNameID())];

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

	
	/**
	 * Add different dependency types to the model
	 */
	private void addAllRelationships() {
		for (Element element : elementModel.getElements().values()) {
			Element4Csp elementFrom = elementCSPs[elementIDToIndex.get(element.getNameID())];
			addRequiresRelationships(elementFrom, element);
			addExcludesRelationships(elementFrom, element);
		}
	}
	
	
	/**
	 * Add requires-relationships, in this version if A requires B, B must be
	 * included and assigned in the same or earlier sub-container than A
	 * 
	 * @param requiring
	 * @param element
	 */
	private void addRequiresRelationships(Element4Csp requiring, Element element) {
		if (!getRequiresRelationships(element).isEmpty()) {
			addRelationshipsToModel(requiring, getRequiresRelationships(element), model, 1, "<=");
		}
	}
	
	
	private List<Relationship> getRequiresRelationships(Element element) {
		List<Relationship> relationships = new ArrayList<>();
		
		for (Relationship relation : this.elementModel.getRelations()) {
			if (relation.getFromID().equals(element.getNameID())) {
				if (relation.getNameType().equals(NameType.REQUIRES)) {
					relationships.add(relation);
				}
			}
		}
		return relationships;
	}
	
	
	/**
	 * Add excludes-relationships, in this (global) version if A excludes B, B cannot
	 * be in the same root container (in any sub-container) as A
	 * 
	 * @param excluding
	 * @param element
	 */
	private void addExcludesRelationships(Element4Csp excluding, Element element) {
		if (!getExcludesRelationships(element).isEmpty()) {
			addRelationshipsToModel(excluding, getExcludesRelationships(element), model, 0, "!=");
		}
	}
	
	
	private List<Relationship> getExcludesRelationships(Element element) {
		List<Relationship> relationships = new ArrayList<>();
		
		for (Relationship relation : this.elementModel.getRelations()) {
			if (relation.getFromID().equals(element.getNameID())) {
				if (relation.getNameType().equals(NameType.INCOMPATIBLE)) {
					relationships.add(relation);
				}
			}
		}
		return relationships;
	}

	
	//TODO Unclear semanitcs for parameters of this method
	/**
	 * Adds relationships (e.g. requires, excludes) to the model
	 * 
	 * @param elementFrom
	 *            Element4Csp, tells the requiring/excluding element
	 * @param relationships
	 *            List containing the elements requiresrelationships or
	 *            excludesrelationships
	 * @param model
	 *            Choco Model
	 * @param isIncludedValue
	 *            tells whether the relationship is requiring (1) or excluding (0) (if
	 *            0, two elements cannot be in the same project)
	 * @param relation n //TODO this seems to be choco comparison operator, redocument or change. 
	 * 					
	 *            String that tells the model if the two elements can or cannot
	 *            be in the same sub-container (or in a previous etc)
	 */
	private void addRelationshipsToModel(Element4Csp elementFrom, List<Relationship> relationships, Model model,
			int isIncludedValue, String relation) {
		for (Relationship rel : relationships) {
			int elementIndex = elementIDToIndex.get(rel.getToID());
			Element4Csp elementTo = elementCSPs[elementIndex];
			IntVar size = model.intVar("size", 2); 	// added this and the third model.arithm(), breaks consistency if
													// a dependent element is missing from sub-containers (in which case it's 
													// assignedContainer is an array and has domainSize > 1)
													//JT: there is no array in assignedContainer! the domain size can be >1, yes. 
													//JT: and why relationships.size() of 'size' variables? could use a constant in arithm!
			//TODO redo 
			model.ifThen(elementFrom.getIsIncluded(),
					model.and(model.arithm(elementTo.getIsIncluded(), "=", isIncludedValue),
							model.arithm(elementTo.getAssignedContainer(), relation,
									elementFrom.getAssignedContainer()),
							model.arithm(size, "!=", elementTo.assignedContainer.getDomainSize())));
			//If elementFrom.getIsIncluded(), Then model.and(...)
			//"Example: - ifThen(b1, arithm(v1, "=", 2));: b1 is equal to 1 => v1 = 2, so v1 !"
		}
	}

	
	public boolean isReleasePlanConsistent() {
		
		for (int index = 0; index < nElements; index++) {
			elementCSPs[index].require(true);
		}
		model.getSolver().reset();

		Solver solver = model.getSolver();
		boolean solution = solver.solve();
		
		return solution;
	}
	
	
	/**
	 * Get problematic element IDs as a String (elements that have been
	 * diagnosed as breaking the consistency of the model)
	 * 
	 * @return
	 */
	public String getDiagnosis() {
		List<Element4Csp> allElements = new ArrayList<>();
		
		for (int req = 0; req < nElements; req++) {
			allElements.add(elementCSPs[req]);
		}
		List<Element4Csp> diagnosis = fastDiag(allElements, allElements);
		StringBuffer sb = new StringBuffer(); 
		if (diagnosis.isEmpty()) {
			sb.append("(No Diagnosis found.)");
		} 
		else {
			for (int i = 0; i < diagnosis.size(); i++) {
				Element4Csp reqB = diagnosis.get(i);
				String reqId = reqB.getId();
				sb.append(reqId);
				if (diagnosis.size() > 1 && i < diagnosis.size() - 1) {
					sb.append(",");
				}
			}
		}
		return sb.toString();
	}
	

	private void setElementsToList(List<Element4Csp> elementsToSet) {
		for (int i = 0; i < nElements; i++) {
			elementCSPs[i].unRequire();
		}
		for (Element4Csp element : elementsToSet) {
			element.require(true);
		}
	}

	private boolean consistent(List<Element4Csp> constraints) {
		if (constraints.size() == 0) {
			return true;
		}
		setElementsToList(constraints);
		Solver solver = model.getSolver();
		solver.reset();
		boolean result = solver.solve();
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
	private List<Element4Csp> fastDiag(List<Element4Csp> C, List<Element4Csp> AC) {

		if (C.isEmpty()) {
			return Collections.emptyList();
		}
		if (consistent(C)) {
			return Collections.emptyList();
		}

		List<Element4Csp> ACWithoutC = diffListsAsSets(AC, C);
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
	private List<Element4Csp> fd(List<Element4Csp> D, List<Element4Csp> C, List<Element4Csp> AC) {

		boolean isConsistent = consistent(AC);
		int q = C.size();

		if (!D.isEmpty()) {
			if (isConsistent) {
				return Collections.emptyList();
			}
		}

		if (q == 1) {
			return new LinkedList<Element4Csp>(C);
		}

		int k = q / 2;
		List<Element4Csp> C1 = C.subList(0, k);
		List<Element4Csp> C2 = C.subList(k, q);

		List<Element4Csp> ACWithoutC2 = diffListsAsSets(AC, C2);
		List<Element4Csp> D1 = fd(C2, C1, ACWithoutC2);

		List<Element4Csp> ACWithoutD1 = diffListsAsSets(AC, D1);
		List<Element4Csp> D2 = fd(D1, C2, ACWithoutD1);

		return appendListsAsSets(D1, D2);
	}

	
	public static List<Element4Csp> appendListsAsSets(List<Element4Csp> CS1, List<Element4Csp> CS2) {
		List<Element4Csp> union = new ArrayList<>(CS1);
		if (CS2 == null)
			return union;

		for (Element4Csp c : CS2) {
			if (!union.contains(c)) {
				union.add(c);
			}
		}
		return union;
	}

	
	public static List<Element4Csp> diffListsAsSets(List<Element4Csp> ac, List<Element4Csp> c2) {
		List<Element4Csp> diff = new ArrayList<Element4Csp>();
		for (Element4Csp element : ac) {
			if (!c2.contains(element)) {
				diff.add(element);
			}
		}
		return diff;
	}
	
	
	/*
	 * Element is transformed so that Choco solver can understand it.
	 * 
	 */
	public static class Element4Csp {
		private BoolVar isIncluded;
		private IntVar assignedContainer;
		private IntVar[] effortInContainer;
		private boolean denyPosted = false;
		private boolean requirePosted = false;
		private Constraint requireCstr;
		private Constraint denyCstr;
		private Model model;
		private String id;
		private ElementModel elementModel;
		private Element originalElement = null;

		Element4Csp(Element element, CSPPlanner planner, Model model, ElementModel elementModel) {
			this.model = model;
			this.elementModel = elementModel;
			id = element.getNameID();

			isIncluded = model.boolVar(element.getNameID() + "_in");

			requireCstr = model.arithm(isIncluded, "=", 1);
			denyCstr = model.arithm(isIncluded, "=", 0);
			// TODO restore? (no idea what this means)
			model.post(requireCstr);
			requirePosted = true;

			setAssignedContainer(element, planner);
			createEffortVariables(element, planner);
			createConstraints(element, planner);
		}
		
		
		public Element getOriginalElement() {
			return originalElement;
		}

		
		public void setOriginalElement(Element originalElement) {
			this.originalElement = originalElement;
		}

		
		/**
		 * 
		 * @param element
		 * @param planner
		 */
		private void setAssignedContainer(Element element, CSPPlanner planner) {
			if (getElementsContainer(element) == 0) {
				assignedContainer = model.intVar(element.getNameID() + "_assignedTo", 
						-1, planner.getNReleases() - 1);
			} else {
				assignedContainer = model.intVar(element.getNameID() + "_assignedTo",
						getElementsContainer(element) - 1);
			}
		}
		
		
		private int getAssignedRelease(Element element) {
			return this.assignedContainer.getUB();
		}
		
		
		private int getElementsContainer(Element element) {
			for (Container container : this.elementModel.getsubContainers()) {
				if (container.getElements().contains(element.getNameID())) {
					return container.getID();
				}
			}
			return 0;
		}
		
		
		//TODO JT: remove doubles? Or is there some reason
		private int getEffortOfElement(Element element) {
			Double d = (Double) elementModel.getAttributeValues().get(element.getAttributes().get("effort")).getValue();
			return d.intValue();
		}

		
		/**
		 * Create choco variables for representing effort in each container
		 * 
		 * @param element
		 * @param planner
		 */
		private void createEffortVariables(Element element, CSPPlanner planner) {
			effortInContainer = new IntVar[planner.getNReleases()+1];
			int[] effortDomain = new int[2];
			effortDomain[0] = 0;
			effortDomain[1] = getEffortOfElement(element); // TODO: What if there is no effort?

			for (int releaseIndex = 0; releaseIndex < planner.getNReleases(); releaseIndex++) {
				String varName = "req_" + element.getNameID() + "_" + (releaseIndex); //e.g req_REQ1_1 (element 1 in release 1)

				if (getAssignedRelease(element) == -1) { // not assigned
					effortInContainer[releaseIndex] = model.intVar(varName, effortDomain); // effort in each release is 0 or the effort	(bever divided to several releases)																		
				} else { // assigned to release
					if (getAssignedRelease(element) == releaseIndex) {
						effortInContainer[releaseIndex] = model.intVar(varName, effortDomain); //e.g for REQ2_1 (meaning REQ2 in release 1) effortInRelease is req_REQ2_1 = {0,2}
					} else {
						effortInContainer[releaseIndex] = model.intVar(varName, 0); // domain is fixed 0 in other releases (e.g for REQ2_2 (meaning REQ2 in release 2) effortInRelease is req_REQ2_2 = 0
					}
				}
			}
		}

		
		/**
		 * Create constraints that enforce If the effort in assigned release if release
		 * is assigned, connect only the affected release
		 * 
		 * @param element
		 * @param planner
		 */
		private void createConstraints(Element element, CSPPlanner planner) {
			if (getAssignedRelease(element) == 0) {
				for (int releaseIndex = 0; releaseIndex < planner.getNReleases(); releaseIndex++) {
					// effectively forces others to 0 because domain size is 2, and the non-0 gets
					// forbidden //?????????????????????????????
					// Could try if adding explicit constraints would be faster
					model.ifOnlyIf(//TODO JT: if we want to support 0 efforts, should reconsider this! M
							model.and(model.arithm(isIncluded, "=", 1), model.arithm(assignedContainer, "=", releaseIndex)),
							model.arithm(effortInContainer[releaseIndex], "=", getEffortOfElement(element)));
					// "ifOnlyIf(Constraint cstr1, Constraint cstr2)"
					// "Posts an equivalence constraint stating that cstr1 is satisfied <=> cstr2 is satisfied, BEWARE : it is automatically posted (it cannot be reified)"
					// Source: http://www.choco-solver.org/apidocs/org/chocosolver/solver/constraints/IReificationFactory.html
				}
			} else {
				model.ifThenElse(model.arithm(isIncluded, "=", 1),
						model.arithm(effortInContainer[getAssignedRelease(element)], "=", getEffortOfElement(element)),
						model.arithm(effortInContainer[getAssignedRelease(element)], "=", 0));
				// if isIncluded, Then model.arithm(effortInRelease[element.getAssignedRelease() - 1], "=",element.getEffort()),
				// and if Not isIncluded, Then model.arithm(effortInRelease[element.getAssignedRelease() - 1], "=", 0)
				// "IReificationFactory.ifThenElse(BoolVar ifVar, Constraint thenCstr, Constraint elseCstr)"
				// "Posts an implication constraint: ifVar => thenCstr && not(ifVar) => elseCstr."
				// See http://www.choco-solver.org/apidocs/org/chocosolver/solver/variables/class-use/BoolVar.html
				
			}
		}
		

		protected IntVar getEffortOfContainer(int releaseIndex) {
			return effortInContainer[releaseIndex]; 
		}
		

		protected IntVar getAssignedContainer() {
			return assignedContainer;
		}

		
		protected BoolVar getIsIncluded() {
			return isIncluded;
		}
		

		protected void require(boolean include) {
			if (include) {
				if (requirePosted) {
					return;
				}
				requireCstr.post();
				requirePosted = true;
				if (denyPosted) {
					model.unpost(denyCstr);
					denyPosted = false;
				}
			} else { // not include = deny
				if (denyPosted) {
					return;
				}
				denyCstr.post();
				denyPosted = true;
				if (requirePosted) {
					model.unpost(requireCstr);
					requirePosted = false;
				}

			}
		}
		

		protected void unRequire() {
			if (denyPosted) {
				model.unpost(denyCstr);
				denyPosted = false;
			}
			if (requirePosted) {
				model.unpost(requireCstr);
				requirePosted = false;
			}
		}


		public String getId() {
			return id;
		}
		

		public String toString() {
			return id;
		}
	}

}
