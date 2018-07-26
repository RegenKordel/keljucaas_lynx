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
import fi.helsinki.ese.murmeli.RelationshipType.NameType;


public class ReleaseCSPPlanner {

	//private ReleasePlan releasePlan;
	private ElementModel elementModel;
	
	private LinkedHashMap<String, Integer> reqIDToIndex;
	private LinkedHashMap<Integer, String> indexToreqID;

	private final int nReleases;
	private final int nRequirements;
	Req4Csp[] reqCSPs = null;
	Model model = null;

	public ReleaseCSPPlanner(ElementModel elementModel) {
		//this.releasePlan = releasePlan;
		this.elementModel = elementModel;
		nReleases = elementModel.getsubContainers().size();
		nRequirements = elementModel.getElements().size();
		reqIDToIndex = new LinkedHashMap<>(nRequirements);
		indexToreqID = new LinkedHashMap<>(nRequirements);
		initializeRequirementIndexMaps();
	}

	private void initializeRequirementIndexMaps() {
		int index1 = 0;
		for (Element element : elementModel.getElements().values()) {
			Integer index2 = Integer.valueOf(index1++);
			reqIDToIndex.put(element.getNameID(), index2);
			indexToreqID.put(index2, element.getNameID());
		}
	}

	public final int getNReleases() {
		return nReleases;
	}

	public final int getNRequirements() {
		return nRequirements;
	}

	/**
	 * Generate Constraint Satisfaction Problem model
	 */
	public void generateCSP() {
		model = new Model("ReleasePlanner"); // NOTE change this?
		this.reqCSPs = new Req4Csp[nRequirements];

		initializeReqCSPs();
		setConstraints();
		addAllDependencies();
	}

	/**
	 * Initialize Req4Csp[] reqCSPs
	 */
	private void initializeReqCSPs() {
		for (Element requirement : elementModel.getElements().values()) {
			Req4Csp req4csp = new Req4Csp(requirement, this, model, this.elementModel);
			reqCSPs[reqIDToIndex.get(requirement.getNameID())] = req4csp;
		}
	}

	/**
	 * Set constraints for ensuring enough effort per release
	 */
	private void setConstraints() {
		for (Container release : elementModel.getsubContainers()) {
			ArrayList<IntVar> releaseEffortVars = new ArrayList<>();
			for (Element requirement : elementModel.getElements().values()) {
				Req4Csp req4Csp = reqCSPs[reqIDToIndex.get(requirement.getNameID())];

				IntVar effortVar = req4Csp.getEffortOfRelease(release.getID() - 1);

				if (effortVar.getUB() >= 0) {// do not add variable to be summed if the variable cannot be > 0
					releaseEffortVars.add(effortVar);
				}
			}
			if (releaseEffortVars.size() > 0) {
				IntVar[] effortVarArray = releaseEffortVars.toArray(new IntVar[0]);
				Double d = (Double) this.elementModel.getAttributeValues().get(release.getAttributes().get("capacity")).getValue();
				model.sum(effortVarArray, "<=", (Integer) d.intValue()).post(); // What if no capacity?
			}
		}
	}

	/**
	 * Add different dependency types to the model
	 */
	private void addAllDependencies() {
		for (Element requirement : elementModel.getElements().values()) {
			Req4Csp requirementFrom = reqCSPs[reqIDToIndex.get(requirement.getNameID())];
			addRequiresDependencies(requirementFrom, requirement);
			addExcludesDependencies(requirementFrom, requirement);
		}
	}
	
	/**
	 * Add requires-dependencies, in this version if A requires B, B must be
	 * included and assigned in the same or earlier release than A
	 * 
	 * @param requiring
	 * @param requirement
	 */
	private void addRequiresDependencies(Req4Csp requiring, Element requirement) {
		if (!getRequiresDependencies(requirement).isEmpty()) {
			addDependenciesToModel(requiring, getRequiresDependencies(requirement), model, 1, "<=");
		}
	}
	
	private List<RelationshipType> getRequiresDependencies(Element element) {
		List<RelationshipType> dependencies = new ArrayList<>();
		
		for (RelationshipType relation : this.elementModel.getRelations()) {
			if (relation.getFromID().equals(element.getNameID())) {
				if (relation.getNameType().equals(NameType.REQUIRES)) {
					dependencies.add(relation);
				}
			}
		}
		
		return dependencies;
	}
	
	/**
	 * Add excludes-dependencies, in this (global) version if A excludes B, B cannot
	 * be in the same project (in any release) as A
	 * 
	 * @param excluding
	 * @param requirement
	 */
	private void addExcludesDependencies(Req4Csp excluding, Element requirement) {
		if (!getExcludesDependencies(requirement).isEmpty()) {
			addDependenciesToModel(excluding, getExcludesDependencies(requirement), model, 0, "!=");
		}
	}
	
	private List<RelationshipType> getExcludesDependencies(Element element) {
		List<RelationshipType> dependencies = new ArrayList<>();
		
		for (RelationshipType relation : this.elementModel.getRelations()) {
			if (relation.getFromID().equals(element.getNameID())) {
				if (relation.getNameType().equals(NameType.INCOMPATIBLE)) {
					dependencies.add(relation);
				}
			}
		}
		
		return dependencies;
	}

	/**
	 * Adds dependencies (e.g. requires, excludes) to the model
	 * 
	 * @param requirementFrom
	 *            Req4Csp, tells the requiring/excluding requirement
	 * @param dependencies
	 *            List containing the requirements requiresDependencies or
	 *            excludesDependencies
	 * @param model
	 *            Choco Model
	 * @param isIncludedValue
	 *            tells whether the dependency is requiring (1) or excluding (0) (if
	 *            0, two requirements cannot be in the same project)
	 * @param relation
	 *            String that tells the model if the two requirements can or cannot
	 *            be in the same release (or in a previous etc)
	 */
	private void addDependenciesToModel(Req4Csp requirementFrom, List<RelationshipType> dependencies, Model model,
			int isIncludedValue, String relation) {
		for (RelationshipType rel : dependencies) {
			int requirementIndex = reqIDToIndex.get(rel.getToID());
			Req4Csp requirementTo = reqCSPs[requirementIndex];
			IntVar size = model.intVar("size", 2); // added this and the third model.arithm(), breaks consistency if
													// a dependent requirement is missing from releases (in which case it's assignedRelease is an array and has domainSize > 1)
			model.ifThen(requirementFrom.getIsIncluded(),
					model.and(model.arithm(requirementTo.getIsIncluded(), "=", isIncludedValue),
							model.arithm(requirementTo.getAssignedRelease(), relation,
									requirementFrom.getAssignedRelease()),
							model.arithm(size, "!=", requirementTo.assignedRelease.getDomainSize())));
			//If requirementFrom.getIsIncluded(), Then model.and(...)
			//"Example: - ifThen(b1, arithm(v1, "=", 2));: b1 is equal to 1 => v1 = 2, so v1 !"
		}
	}

	public boolean isReleasePlanConsistent() {
		
		for (int index = 0; index < nRequirements; index++) {
			reqCSPs[index].require(true);
		}
		model.getSolver().reset();

		Solver solver = model.getSolver();
		boolean solution = solver.solve();
		
		return solution;
	}
	
	/**
	 * Get problematic Requirement IDs as a String (Requirements that have been
	 * diagnosed as breaking the consistency of the model)
	 * 
	 * @return
	 */
	public String getDiagnosis() {
		List<Req4Csp> allReqs = new ArrayList<>();
		
		for (int req = 0; req < nRequirements; req++) {
			allReqs.add(reqCSPs[req]);
		}
		List<Req4Csp> diagnosis = fastDiag(allReqs, allReqs);
		StringBuffer sb = new StringBuffer(); 
		if (diagnosis.isEmpty()) {
			sb.append("(No Diagnosis found.)");
		} 
		else {
			for (int i = 0; i < diagnosis.size(); i++) {
				Req4Csp reqB = diagnosis.get(i);
				String reqId = reqB.getId();
				sb.append(reqId);
				if (diagnosis.size() > 1 && i < diagnosis.size() - 1) {
					sb.append(",");
				}
			}
		
		}
		return sb.toString();
	}

	// Old XML-version
	// public String getDiagnosis() {
	// List<Req4Csp> allReqs = new ArrayList<>();
	// for (int req = 0; req < nRequirements; req++)
	// allReqs.add(reqCSPs[req]);
	//
	// List<Req4Csp> diagnosis =
	// fastDiag(allReqs, allReqs);
	// StringBuffer sb = new StringBuffer();
	// if(diagnosis.isEmpty())
	// sb.append("(No Diagnosis found.)");
	// else {
	// sb.append("Diagnosis: ");
	// for (Req4Csp reqB: diagnosis) {
	//
	// String reqId= reqB.getId();
	// sb.append(reqId);
	// sb.append(" ");
	// }
	// }
	// return sb.toString();
	// }

	public static class Req4Csp {
		private BoolVar isIncluded;
		private IntVar assignedRelease;
		private IntVar[] effortInRelease;
		private boolean denyPosted = false;
		private boolean requirePosted = false;
		private Constraint requireCstr;
		private Constraint denyCstr;
		private Model model;
		private String id;
		private ElementModel elementModel;

		Req4Csp(Element requirement, ReleaseCSPPlanner rcg, Model model, ElementModel elementModel) {
			this.model = model;
			this.elementModel = elementModel;
			id = requirement.getNameID();

			isIncluded = model.boolVar(requirement.getNameID() + "_in");

			requireCstr = model.arithm(isIncluded, "=", 1);
			denyCstr = model.arithm(isIncluded, "=", 0);
			// TODO restore //?????????????????????????????????????????????????
			model.post(requireCstr);
			requirePosted = true;

			setAssignedRelease(requirement, rcg);
			createEffortVariables(requirement, rcg);
			createConstraints(requirement, rcg);

		}

		/**
		 * 
		 * @param requirement
		 * @param rcg
		 */
		private void setAssignedRelease(Element requirement, ReleaseCSPPlanner rcg) {
			if (getRelease(requirement) == 0) {
				assignedRelease = model.intVar(requirement.getNameID() + "_assignedTo", 
						-1, rcg.getNReleases() - 1);
			} else {
				assignedRelease = model.intVar(requirement.getNameID() + "_assignedTo",
						getRelease(requirement) - 1);
			}
		}
		
		private int getAssignedRelease(Element requirement) {
			return this.assignedRelease.getUB();
		}
		
		private int getRelease(Element requirement) {
			for (Container release : this.elementModel.getsubContainers()) {
				if (release.getElements().contains(requirement.getNameID())) {
					return release.getID();
				}
			}
			return 0;
		}
		
		private int getEffort(Element requirement) {
			Double d = (Double) elementModel.getAttributeValues().get(requirement.getAttributes().get("effort")).getValue();
			return d.intValue();
		}

		/**
		 * Create choco variables for representing effort in each release
		 * 
		 * @param requirement
		 * @param rcg
		 */
		private void createEffortVariables(Element requirement, ReleaseCSPPlanner rcg) {
			effortInRelease = new IntVar[rcg.getNReleases()+1];
			int[] effortDomain = new int[2];
			effortDomain[0] = 0;
			effortDomain[1] = getEffort(requirement); // What if there is no effort?

			for (int releaseIndex = 0; releaseIndex < rcg.getNReleases(); releaseIndex++) {
				String varName = "req_" + requirement.getNameID() + "_" + (releaseIndex); //e.g req_REQ1_1 (Requirement 1 in release 1)

				if (getAssignedRelease(requirement) == -1) { // not assigned
					effortInRelease[releaseIndex] = model.intVar(varName, effortDomain); // effort in release is 0 or the effort																				
				} else {// assigned to release
					if (getAssignedRelease(requirement) == releaseIndex) {
						effortInRelease[releaseIndex] = model.intVar(varName, effortDomain); //e.g for REQ2_1 (meaning REQ2 in release 1) effortInRelease is req_REQ2_1 = {0,2}
					} else {
						effortInRelease[releaseIndex] = model.intVar(varName, 0); // domain is fixed 0 in other releases (e.g for REQ2_2 (meaning REQ2 in release 2) effortInRelease is req_REQ2_2 = 0
					}
				}
			}
		}

		/**
		 * Create constraints that enforce If the effort in assigned release if release
		 * is assigned, connect only the affected release
		 * 
		 * @param requirement
		 * @param rcg
		 */
		private void createConstraints(Element requirement, ReleaseCSPPlanner rcg) {
			if (getAssignedRelease(requirement) == 0) {
				for (int releaseIndex = 0; releaseIndex < rcg.getNReleases(); releaseIndex++) {
					// effectively forces others to 0 because domain size is 2, and the non-0 gets
					// forbidden //?????????????????????????????
					// Could try if adding explicit constraints would be faster
					model.ifOnlyIf(
							model.and(model.arithm(isIncluded, "=", 1), model.arithm(assignedRelease, "=", releaseIndex)),
							model.arithm(effortInRelease[releaseIndex], "=", getEffort(requirement)));
					// "ifOnlyIf(Constraint cstr1, Constraint cstr2)"
					// "Posts an equivalence constraint stating that cstr1 is satisfied <=> cstr2 is satisfied, BEWARE : it is automatically posted (it cannot be reified)"
					// Source: http://www.choco-solver.org/apidocs/org/chocosolver/solver/constraints/IReificationFactory.html
				}
			} else {
				model.ifThenElse(model.arithm(isIncluded, "=", 1),
						model.arithm(effortInRelease[getAssignedRelease(requirement)], "=", getEffort(requirement)),
						model.arithm(effortInRelease[getAssignedRelease(requirement)], "=", 0));
				// if isIncluded, Then model.arithm(effortInRelease[requirement.getAssignedRelease() - 1], "=",requirement.getEffort()),
				// and if Not isIncluded, Then model.arithm(effortInRelease[requirement.getAssignedRelease() - 1], "=", 0)
				// "IReificationFactory.ifThenElse(BoolVar ifVar, Constraint thenCstr, Constraint elseCstr)"
				// "Posts an implication constraint: ifVar => thenCstr && not(ifVar) => elseCstr."
				// See http://www.choco-solver.org/apidocs/org/chocosolver/solver/variables/class-use/BoolVar.html
				
			}
		}

		protected IntVar getEffortOfRelease(int releaseIndex) {
			return effortInRelease[releaseIndex]; 
		}

		protected IntVar getAssignedRelease() {
			return assignedRelease;
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

	private void setRequirementsToList(List<Req4Csp> reqsToSet) {
		for (int i = 0; i < nRequirements; i++) {
			reqCSPs[i].unRequire();
		}
		for (Req4Csp req : reqsToSet) {
			req.require(true);
		}
	}

	private boolean consistent(List<Req4Csp> constraints) {

		// System.out.print("consistent? ");

		if (constraints.size() == 0) {
			return true;
		}

		setRequirementsToList(constraints);
		// for (Req4Csp req :constraints)
		// System.out.print(req.getId() +" ");
		Solver solver = model.getSolver();
		solver.reset();
		boolean result = solver.solve();
//		 System.out.println("=" + result);
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
	private List<Req4Csp> fastDiag(List<Req4Csp> C, List<Req4Csp> AC) {

		if (C.isEmpty()) {
			return Collections.emptyList();
		}
		if (consistent(C)) {
			return Collections.emptyList();
		}

		List<Req4Csp> ACWithoutC = diffListsAsSets(AC, C);
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
	private List<Req4Csp> fd(List<Req4Csp> D, List<Req4Csp> C, List<Req4Csp> AC) {

		boolean isConsistent = consistent(AC);
		int q = C.size();

		if (!D.isEmpty()) {
			if (isConsistent) {
				return Collections.emptyList();
			}
		}

		if (q == 1) {
			return new LinkedList<Req4Csp>(C);
		}

		int k = q / 2;
		List<Req4Csp> C1 = C.subList(0, k);
		List<Req4Csp> C2 = C.subList(k, q);

		List<Req4Csp> ACWithoutC2 = diffListsAsSets(AC, C2);
		List<Req4Csp> D1 = fd(C2, C1, ACWithoutC2);

		List<Req4Csp> ACWithoutD1 = diffListsAsSets(AC, D1);
		List<Req4Csp> D2 = fd(D1, C2, ACWithoutD1);

		return appendListsAsSets(D1, D2);
	}

	public static List<Req4Csp> appendListsAsSets(List<Req4Csp> CS1, List<Req4Csp> CS2) {
		// System.out.print("appendListsAsSets: Cs1 =" + CS1 + ", CS2=" + CS2);
		List<Req4Csp> union = new ArrayList<>(CS1);
		if (CS2 == null)
			return union;

		for (Req4Csp c : CS2) {
			if (!union.contains(c)) {
				union.add(c);
			}
		}
		// System.out.println(" : " + union);
		return union;
	}

	public static List<Req4Csp> diffListsAsSets(List<Req4Csp> ac, List<Req4Csp> c2) {
		// System.out.print("diffListsAsSets: ac =" + ac + ", c2=" + c2);
		List<Req4Csp> diff = new ArrayList<Req4Csp>();
		for (Req4Csp requirement : ac) {
			if (!c2.contains(requirement)) {
				diff.add(requirement);
			}

		}
		// System.out.println(" : " + diff);
		return diff;
	}

}
